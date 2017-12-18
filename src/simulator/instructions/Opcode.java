package simulator.instructions;

import simulator.core.ACASim;

public enum Opcode {

	NOP (0x00, "simulator.instructions.NOPInstruction"),
	HALT(0x0f, "simulator.instructions.HALTInstruction"),

	// ALU
	ADD (0x01, "simulator.instructions.alu.ADDInstruction"),
	ADDI(0x02, "simulator.instructions.alu.ADDIInstruction"),
	SUB (0x03, "simulator.instructions.alu.SUBInstruction"),
	MUL (0x04, "simulator.instructions.alu.MULInstruction"),
	DIV (0x05, "simulator.instructions.alu.DIVInstruction"),
	XOR (0x06, "simulator.instructions.alu.XORInstruction"),

	SHL(0x07, "simulator.instructions.alu.SHLInstruction"),
	SHR(0x08, "simulator.instructions.alu.SHRInstruction"),

	CMP(0x09, "simulator.instructions.alu.CMPInstruction"),
	SUBI(0x0A, "simulator.instructions.alu.SUBIInstruction"),

	MOVI(0x0B, "simulator.instructions.alu.MOVIInstruction"),
	MOD(0x0C, "simulator.instructions.alu.MODInstruction"),
	
	// magic syscall
	SYS(0x0D, "simulator.instructions.alu.SYSInstruction"),

	// LOAD/STORE
	// load REG[OP1] into DMEM[R2]
	LD(0x10, "simulator.instructions.lds.LDInstruction"),
	// load I1 into DMEM[R2]
	LDI(0x11, "simulator.instructions.lds.LDIInstruction"),
	// store DMEM[R1] into REG[OP2]
	ST(0x12, "simulator.instructions.lds.STInstruction"),

	// BRANCH
	// jump to address in register
	J(0x20, "simulator.instructions.branch.JInstruction"),
	// jump to immediate address
	JI(0x21, "simulator.instructions.branch.JIInstruction"),
	// jump relative
	JR(0x22, "simulator.instructions.branch.JRInstruction"),

	// branch to address in R2 if R1 >= 0
	BGEZ(0x23,"simulator.instructions.branch.BGEZInstruction"),
	// branch to address in R2 if R1 < 0
	BLTZ(0x24, "simulator.instructions.branch.BLTZInstruction"),
	// branch to address in R2 if R1 != 0
	BZ(0x25, "simulator.instructions.branch.BZInstruction"),
	// branch to address in R2 if R1 > 0
	BGZ(0x26,"simulator.instructions.branch.BGZInstruction"),

	// FPU
	FADD (0x31, "simulator.instructions.fpu.FADDInstruction"),
	FSUB (0x32, "simulator.instructions.fpu.FSUBInstruction"),
	FMUL (0x33, "simulator.instructions.fpu.FMULInstruction"),
	FDIV (0x34, "simulator.instructions.fpu.FDIVInstruction");

	private int _hex;
	private String _class;

	Opcode(int binVal, String classPath) {
		this._hex = binVal;
		this._class = classPath;
	}

	public int hex() {
		return _hex;
	}

	public Instruction instantiate() {
		try {
			return (Instruction) Class.forName(_class).getConstructor().newInstance();
		} catch (Exception e) {
			System.out.println("Failed to create instance of " + _class + " for opcode " + this);
			e.printStackTrace();
			System.exit(0);
		}

		return null;
	}

	public static Opcode fromHex(int opc) {
		for (Opcode l : Opcode.values()) {
			if (l.hex() == opc) return l;
		}

		ACASim.getInstance().halt();
		System.err.println("Illegal opcode: " + opc);
		return null;
	}

	public static final int MSK_OPC = 0xff000000;
	public static final int MSK_OP1 = 0x00ff0000;
	public static final int MSK_OP2 = 0x0000ff00;
	public static final int MSK_OP3 = 0x000000ff;

	public static final int POS_OPC = 24;
	public static final int POS_OP1 = 16;
	public static final int POS_OP2 = 8;
	public static final int POS_OP3 = 0;
}
