package simulator.core;

import simulator.instructions.Instruction;
import simulator.instructions.Opcode;
import simulator.stages.DecodeStage;
import simulator.stages.ExecutionUnit;
import simulator.stages.FetchStage;
import simulator.stages.Stage;

public class BranchPredictor {

	public static final int BP_LRU_SIZE = 1024;
	public static final int BP_BTAC_SIZE = 16;
	public static final boolean BP_DYNAMIC = true;

	public int branchesExecuted = 0;
	public int correctGuesses = 0;
	
	private boolean predictedContext = false;
	private boolean stallDecode = false;

	private int oldPC;
	private int predictedPC;

	private LRUCache<Integer, BPSaturatingState> dynamicLRU = new LRUCache<Integer, BPSaturatingState>(BP_LRU_SIZE);
	private LRUCache<Integer, Integer> BTAC = new LRUCache<Integer, Integer>(BP_BTAC_SIZE);

	public void beforeBranchExecuted(Instruction instr) {
		ACASim.dbgLog("BEFORE BRANCH EXECUTED PC=" + ACASim.getInstance().mem().PC);
		oldPC = ACASim.getInstance().mem().PC;
	}

	public void onBranchExecuted(Instruction instr) {
		ACASim.dbgLog("BRANCH EXECUTED");

		branchesExecuted++;
		predictedContext = false;
		stallDecode = false;
		//predictedPC = -1;

		int jumpTargetPC = (ACASim.getInstance().mem().PC == oldPC) ? instr.getAddress() + 1 : ACASim.getInstance().mem().PC;
		
		if(instr.getOpcode() == Opcode.J && ACASim.getInstance().mem().PC != oldPC) {
			BTAC.put(instr.getAddress(), ACASim.getInstance().mem().PC);
			ACASim.dbgLog("BTAC PUT " + instr.getAddress() + " to " + ACASim.getInstance().mem().PC);
		}
		
		if(ACASim.getInstance().mem().PC != oldPC) {
			// taken
			if(BP_DYNAMIC) {
				if(dynamicLRU.containsKey(instr.getAddress())) {
					dynamicLRU.put(instr.getAddress(), dynamicLRU.get(instr.getAddress()).inc());
				} else {
					dynamicLRU.put(instr.getAddress(), BPSaturatingState.WEAK_YES);
				}
			}
		} else {
			// not taken
			if(BP_DYNAMIC) {
				if(dynamicLRU.containsKey(instr.getAddress())) {
					dynamicLRU.put(instr.getAddress(), dynamicLRU.get(instr.getAddress()).dec());
				} else {
					dynamicLRU.put(instr.getAddress(), BPSaturatingState.WEAK_NO);
				}
			}
		}

		if(jumpTargetPC == predictedPC) {
			correctGuesses++;

			ACASim.dbgLog("Reverting from PC " + ACASim.getInstance().mem().PC + " to " + oldPC);

			// branch was correct so we did the jump back in decode
			ACASim.getInstance().mem().PC = oldPC;

			ACASim.dbgLog("GUESS CORRECT");
			// guess was correct, mark all speculative instructions in RB as not speculative
			for(Instruction rbi : ACASim.getInstance().reorderBuffer) {
				if(rbi.isSpeculative()) {
					rbi.setSpeculative(false);
				}
			}

		} else {
			ACASim.dbgLog("GUESS INCORRECT PC:" + ACASim.getInstance().mem().PC + " PRED:" + predictedPC + " OLD:" + oldPC + " NEXTI:" + (instr.getAddress() + 1));

			if(ACASim.getInstance().mem().PC == oldPC && instr.getOpcode() != Opcode.J) {
				// restore PC to the line after the branch if the branch didn't change PC and we were wrong
				// don't do this for instructions that use the BTAC
				ACASim.getInstance().mem().PC = instr.getAddress() + 1;
			}

			ACASim.getInstance().mem().restoreRegMap();

			//for(Instruction ins : ACASim.getInstance().reorderBuffer) {
			//	if(ins.isSpeculative()) {
			//		ACASim.dbgLog("spec: " + ins);
			//	}
			//}

			// guess was incorrect, drop all speculative instructions
			while(ACASim.getInstance().reorderBuffer.size() > 0 && ACASim.getInstance().reorderBuffer.peekFirst().isSpeculative()) {
				Instruction rm = ACASim.getInstance().reorderBuffer.removeFirst();

				// tell any execution units and reservation stations to throw it away too
				rm.purge();

				ACASim.dbgLog("INCORRECT " + rm);
			}

			((FetchStage) (ACASim.getInstance().pipeline.get(Stage.FETCH.val()))).flushBuffer();
			((DecodeStage) (ACASim.getInstance().pipeline.get(Stage.DECODE.val()))).flushBuffer();

		}
		// if the guess was correct then remove the speculative and no execute bit from every instr in the reorder buffer after targetaddr
		// otherwise remove all instrs from rb after targetaddr

		predictedPC = -1;
	}

	public boolean canProcessBundle(Instruction instr) {
		if(instr.getEU() == ExecutionUnit.BRANCH) {
			if(predictedContext) {
				// already predicted a branch, stall until it has been executed TODO
				ACASim.dbgLog("Hit branch inside speculative context!");
				stallDecode = true;
				return false;
			}
		}

		return true;
	}

	public int onInstructionDecoded(Instruction instr) {
		if(instr.getEU() == ExecutionUnit.BRANCH) {
			if(predictedContext) {
				// already predicted a branch, stall until it has been executed TODO
				ACASim.dbgLog("Hit branch inside speculative context!");
				stallDecode = true;
				return 1;
			}

			predictedContext = true;

			// TODO fix
			//System.arraycopy(ACASim.getInstance().mem().SCOREBOARD, 0, SCOREBOARD_SV, 0, CPUMemory.NUMREGS);
			ACASim.getInstance().mem().saveRegMap();

			// TODO decode target PC from instruction
			// predict taken or not taken
			int decodedPC = decodePC(instr);
			boolean prediction = decodedPC == -1 ? false : predictBranch(instr, decodedPC);

			if(prediction) {
				predictedPC = decodedPC;

				ACASim.dbgLog("Predict taken PC " + ACASim.getInstance().mem().PC + " -> " + predictedPC);
				ACASim.getInstance().mem().PC = predictedPC;
				//stateSaved = true;
			} else {
				//predictedPC = ACASim.getInstance().mem().PC + 1;
				predictedPC = instr.getAddress() + 1;
				ACASim.dbgLog("Predict not taken PC " + ACASim.getInstance().mem().PC + " -> " + (instr.getAddress() + 1));
			}

			// ideally this shouldn't be done when predicting not taken
			//ACASim.getInstance().mem().PC = predictedPC;

			//oldPC = ACASim.getInstance().mem().PC;

			// decode should ignore the rest of the bundle if we predicted taken
			return decodedPC != -1 && prediction ? 2 : 0;

			// temporary hack: always drop the bundle to work around the early halt bug
			//return 2;
		} else {
			if(predictedContext) {
				instr.setSpeculative(true);
				ACASim.dbgLog("Marking instr as speculative");
			}
		}

		return 0;
	}

	private int decodePC(Instruction instr) {
		int predictedPC = -1;

		// J - value in op1 reg [btac]
		// JR - PC + op1 reg [btac]

		// JI - op1
		// Bx - value in op2

		switch(instr.getOpcode()) {
		case JI:
		case JAL:
			predictedPC = (instr.getRawOpcode() & Opcode.MSK_OP1) >> 16;
			break;
		case BGEZ:
		case BLTZ:
		case BZ:
			predictedPC = (instr.getRawOpcode() & Opcode.MSK_OP2) >> 8;
			break;
		case J:
			if(BTAC.containsKey(instr.getAddress())) {
				predictedPC = BTAC.get(instr.getAddress());
				ACASim.dbgLog("BTAC pred " + instr.getAddress() + " to " + predictedPC);
			}
			break;
		case JR:
		default:
			ACASim.dbgLog("Can't predict branch");
			break;
		}

		return predictedPC;
	}

	private boolean predictBranch(Instruction instr, int decodedPC) {
		if(!BP_DYNAMIC) {
			return predictBranchStatic(instr, decodedPC);
		} else {
			return predictBranchDynamic(instr, decodedPC);
		}
	}

	private boolean predictBranchDynamic(Instruction instr, int decodedPC) {
		if(!dynamicLRU.containsKey(instr.getAddress())) {
			boolean staticPred = predictBranchStatic(instr, decodedPC);

			BPSaturatingState cacheVal = staticPred ? BPSaturatingState.WEAK_YES : BPSaturatingState.WEAK_NO;
			dynamicLRU.put(instr.getAddress(), cacheVal);

			return staticPred;
		} else {
			BPSaturatingState cacheVal = dynamicLRU.get(instr.getAddress());

			return (cacheVal == BPSaturatingState.WEAK_YES || cacheVal == BPSaturatingState.STRONG_YES);
		}
	}


	private boolean predictBranchStatic(Instruction instr, int decodedPC) {
		if(instr.getOpcode() == Opcode.JI || instr.getOpcode() == Opcode.J) {
			// unconditional
			return true;
		}

		// can't predict Opcode.JR yet
		// should be doable by computing target

		if(decodedPC < instr.getAddress()) {
			// backwards jump, predict taken
			return true;
		} else {
			// forwards jump, predict not taken
			return false;
		}

		//return true;

		//return new Random().nextBoolean();
	}

	public boolean allowDecodeTransactions() {
		return !stallDecode;
	}

	enum BPSaturatingState {
		STRONG_NO(0), WEAK_NO(1), WEAK_YES(2), STRONG_YES(3);

		private int _num;
		BPSaturatingState(int num) {
			_num = num;
		}

		public BPSaturatingState inc() {
			if(_num < 3) {
				return values()[_num + 1];
			}

			return values()[_num];
		}

		public BPSaturatingState dec() {
			if(_num > 0) {
				return values()[_num - 1];
			}

			return values()[_num];
		}
	}
}
