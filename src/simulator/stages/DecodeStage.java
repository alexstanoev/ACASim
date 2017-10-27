package simulator.stages;

import simulator.core.ACASim;
import simulator.instructions.Instruction;
import simulator.instructions.Opcode;

public class DecodeStage implements IPipelineStage {

	private Instruction old = null;
	private Instruction curr = null;
	private Instruction next = null;

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
		}

		curr = next;
		next = null;

		ACASim.dbgLog("new instruction " + String.format("0x%08X", curr.getRawOpcode()));

		int opcRaw = (curr.getRawOpcode() & MSK_OPC) >> 24;
		int op1Raw = (curr.getRawOpcode() & MSK_OP1) >> 16;
		int op2Raw = (curr.getRawOpcode() & MSK_OP2) >> 8;
		int op3Raw = curr.getRawOpcode() & MSK_OP3;

		Opcode opc = Opcode.fromHex(opcRaw);

		Instruction decoded = opc.instantiate();

		decoded.setOpcode(opc);
		decoded.setRawOpcode(curr.getRawOpcode());
		decoded.setAddress(curr.getAddress());
		decoded.setOperands(op1Raw, op2Raw, op3Raw);

		decoded.decode();

		ACASim.dbgLog("Decoded: " + opc.toString() + " " + op1Raw + " " + op2Raw + " " + op3Raw);

		curr = decoded;
		old = curr;
	}

	@Override
	public void acceptNextInstruction(Instruction instr) {
		next = instr;
	}

	@Override
	public boolean canAcceptInstruction() {
		return curr == null && next == null;
	}

	@Override
	public boolean isResultAvailable() {
		return curr != null;
	}

	@Override
	public Instruction getCurrentInstruction() {
		return old;
	}

	@Override
	public Instruction getResult() {
		Instruction res = curr;
		curr = null;
		return res;
	}

	@Override
	public void clearOldInstruction() {
		if(curr != null) return;

		old = null;
	}

}
