package simulator.core;

import java.util.Random;

import simulator.instructions.Instruction;
import simulator.instructions.Opcode;
import simulator.stages.ExecutionUnit;

public class BranchPredictor {

	private boolean predictedContext = false;
	private boolean stallDecode = false;
	private boolean stateSaved = false;

	private boolean[] SCOREBOARD_SV = new boolean[CPUMemory.NUMREGS];


	//private int oldPC;
	private int predictedPC;

	public void onBranchExecuted(Instruction instr) {
		ACASim.dbgLog("BRANCH EXECUTED");

		predictedContext = false;
		predictedPC = -1;

		if(ACASim.getInstance().mem().PC == predictedPC) {
			ACASim.dbgLog("GUESS CORRECT");
			// guess was correct, mark all speculative instructions in RB as not speculative
			for(Instruction rbi : ACASim.getInstance().reorderBuffer) {
				if(rbi.isSpeculative()) {
					rbi.setSpeculative(false);
				}
			}
		} else {
			ACASim.dbgLog("GUESS INCORRECT " + ACASim.getInstance().mem().PC + " " + predictedPC);

			//if(stateSaved) {
			// restore scoreboard

			int i = 0;
			for(boolean s : ACASim.getInstance().mem().SCOREBOARD) {
				if(SCOREBOARD_SV[i++] != s) {
					ACASim.dbgLog("Diff at " + i + " main:" + s);
				}
			}

			System.arraycopy(SCOREBOARD_SV, 0, ACASim.getInstance().mem().SCOREBOARD, 0, CPUMemory.NUMREGS);
			stateSaved = false;
			ACASim.dbgLog("Restoring scoreboard");
			//}

			// guess was incorrect, drop all speculative instructions
			while(ACASim.getInstance().reorderBuffer.size() > 0 && ACASim.getInstance().reorderBuffer.peekFirst().isSpeculative()) {
				Instruction rm = ACASim.getInstance().reorderBuffer.removeFirst();

				// tell any execution units and reservation stations to throw it away too
				rm.purge();

				ACASim.dbgLog("INCORRECT " + rm);
			}
		}
		// if the guess was correct then remove the speculative and no execute bit from every instr in the reorder buffer after targetaddr
		// otherwise remove all instrs from rb after targetaddr
	}

	public void onInstructionDecoded(Instruction instr) {
		if(instr.getEU() == ExecutionUnit.BRANCH) {
			if(predictedContext) {
				// already predicted a branch, stall until it has been executed TODO
				ACASim.dbgLog("Hit branch inside speculative context!");
				return;
			}

			predictedContext = true;

			System.arraycopy(ACASim.getInstance().mem().SCOREBOARD, 0, SCOREBOARD_SV, 0, CPUMemory.NUMREGS);

			// TODO decode target PC from instruction
			// predict taken or not taken
			// keep branch delay slots in mind
			int decodedPC = decodePC(instr);

			if(decodedPC != -1 && predictBranch(instr)) {
				predictedPC = decodedPC;
				ACASim.getInstance().mem().PC = predictedPC;
				//stateSaved = true;
			} else {
				//predictedPC = ACASim.getInstance().mem().PC;
			}

			//oldPC = ACASim.getInstance().mem().PC;

		} else {
			if(predictedContext) {
				instr.setSpeculative(true);
				ACASim.dbgLog("Marking instr as speculative");
			}
		}
	}

	private int decodePC(Instruction instr) {
		int predictedPC = -1;

		// J - value in op1 reg [btac]
		// JR - PC + op1 reg [btac]

		// JI - op1
		// Bx - value in op2

		switch(instr.getOpcode()) {
		case JI:
			predictedPC = (instr.getRawOpcode() & Opcode.MSK_OP1) >> 16;
			break;
		case BGEZ:
		case BLTZ:
		case BZ:
			predictedPC = (instr.getRawOpcode() & Opcode.MSK_OP2) >> 8;
			break;
		case JR:
		case J:
		default:
			ACASim.dbgLog("Unexpected branch");
			break;
		}

		return predictedPC;
	}

	private boolean predictBranch(Instruction instr) {
		if(instr.getOpcode() == Opcode.JI || instr.getOpcode() == Opcode.JR || instr.getOpcode() == Opcode.J) {
			// unconditional
			return true;
		}

		return false;
		//return new Random().nextBoolean();
	}

	// TODO
	public boolean allowDecodeTransactions() {
		return !stallDecode;
	}
}
