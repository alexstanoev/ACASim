package simulator.stages;

import simulator.instructions.*;

public class DecodeStage implements IPipelineStage {

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
		System.out.println("DECODE");

		if(next == null) {
			System.out.println("skip");
			return;
		}
		
		curr = next;
		next = null;

		int opcRaw = (curr.getRawOpcode() & MSK_OPC) >> 24;
		int op1Raw = (curr.getRawOpcode() & MSK_OP1) >> 16;
		int op2Raw = (curr.getRawOpcode() & MSK_OP2) >> 8;
		int op3Raw = curr.getRawOpcode() & MSK_OP3;

		Opcode opc = Opcode.fromHex(opcRaw);

		Instruction decoded = null;

		switch(opc) {

		case NOP:
			decoded = new NOPInstruction();
			break;
		case ADD:
			decoded = new AddInstruction();
			break;
		case ADDI:
			decoded = new AddIInstruction();
			break;
		case CMP:

		case LD:
		case LDI:
		case STR:

		case B:
		case J:
		case BL:

		case HALT:
			decoded = new HaltInstruction();
			break;
		}

		decoded.setOperands(op1Raw, op2Raw, op3Raw);
		
		decoded.decode();

		System.out.println("Decoded: " + opc.toString() + " " + op1Raw + " " + op2Raw + " " + op3Raw);

		curr = decoded;
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
	public Instruction getResult() {
		Instruction res = curr;
		curr = null;
		return res;
	}

}
