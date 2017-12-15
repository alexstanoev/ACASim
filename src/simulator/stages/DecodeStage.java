package simulator.stages;

import simulator.core.ACASim;
import simulator.core.CPUMemory;
import simulator.instructions.Instruction;
import simulator.instructions.InstructionBundle;
import simulator.instructions.Opcode;

public class DecodeStage implements IPipelineStage {

	private InstructionBundle old = null;
	private InstructionBundle curr = null;
	private InstructionBundle next = null;

	// [--OP--][--O1--][--O2--][--O3--] 4 x 8 bits operands
	// [------------------------------] 32 bit instruction

	private final int MSK_OPC = 0xff000000;
	private final int MSK_OP1 = 0x00ff0000;
	private final int MSK_OP2 = 0x0000ff00;
	private final int MSK_OP3 = 0x000000ff;

	@Override
	public void tick() {
		ACASim.dbgLog("DECODE");

		//if(!canAcceptInstruction()) {
		//	System.out.println("stalled");
		//	return;
		//}

		if(next == null) {
			ACASim.dbgLog("skip");
			return;
		} else {
			if(curr != null && curr.hasInstructions()) {
				// need to finish curr first
			} else {
				curr = next;
				next = null;
			}
		}

		//curr = next;
		//next = null;

		InstructionBundle res = new InstructionBundle();

		while(curr.hasInstructions()) {
			Instruction enc = curr.fetchInstruction();

			ACASim.dbgLog("new instruction " + String.format("0x%08X", enc.getRawOpcode()));

			int opcRaw = (enc.getRawOpcode() & MSK_OPC) >> 24;
			int op1Raw = (enc.getRawOpcode() & MSK_OP1) >> 16;
			int op2Raw = (enc.getRawOpcode() & MSK_OP2) >> 8;
			int op3Raw = enc.getRawOpcode() & MSK_OP3;

			Opcode opc = Opcode.fromHex(opcRaw);

			Instruction decoded = opc.instantiate();

			decoded.setOpcode(opc);
			decoded.setRawOpcode(enc.getRawOpcode());
			decoded.setAddress(enc.getAddress());
			decoded.setOperands(op1Raw, op2Raw, op3Raw);

			decoded.decode();
			decoded.allocRegister();

			ACASim.dbgLog("Decoded: " + opc.toString() + " " + op1Raw + " " + op2Raw + " " + op3Raw + " " + decoded);

			int shouldFlushBundle = ACASim.getInstance().branchPredictor.onInstructionDecoded(decoded);

			// onInstructionDecoded will save the scoreboard if marking as speculative
			//decoded.scoreboardDestReg();

			if(shouldFlushBundle == 1) {
				curr = res;
				old = curr;
				ACASim.getInstance().mem().PC--;
				// leave the branch in the queue so decode keeps ticking it
				ACASim.dbgLog("Abandoning bundle");
				return;
			}

			if(!ACASim.getInstance().branchPredictor.allowDecodeTransactions()) {
				// branch predictor has blocked further execution
				// return this instruction back to the bundle?
				ACASim.dbgLog("BP blocked decode"); 
			}

			res.pushInstruction(decoded);
			ACASim.getInstance().reorderBuffer.push(decoded);

			if(shouldFlushBundle == 2) {
				curr = res;
				old = curr;
				ACASim.dbgLog("Flushing bundle");
				return;
			}
		}

		curr = res;
		old = curr;

	}

	@Override
	public void acceptNextInstruction(Instruction instr) {
		throw new IllegalStateException("Attempted to pass single instruction to decode stage");
	}

	@Override
	public boolean canAcceptInstruction() {
		return curr == null && next == null && ACASim.getInstance().reorderBuffer.size() + CPUMemory.FETCH_WIDTH < CPUMemory.RB_SIZE;
	}

	@Override
	public boolean isResultAvailable() {
		return curr != null && ACASim.getInstance().branchPredictor.allowDecodeTransactions(); // && ACASim.getInstance().branchPredictor.allowDecodeTransactions()
	}

	@Override
	public IStageTransaction getCurrentTransaction() {
		return old;
	}

	@Override
	public IStageTransaction getResult() {
		InstructionBundle res = curr;
		curr = null;
		return res;
	}

	@Override
	public void clearOldInstruction() {
		if(curr != null) return;

		old = null;
	}

	@Override
	public void acceptTransaction(IStageTransaction tr) {
		if(!(tr instanceof InstructionBundle)) {
			throw new IllegalStateException("Attempted to pass non-bundle to decode stage");
		}

		next = (InstructionBundle) tr;
	}

	public void flushBuffer() {
		curr = null;
	}
	
}
