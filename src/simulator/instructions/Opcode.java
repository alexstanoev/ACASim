package simulator.instructions;

import java.lang.reflect.InvocationTargetException;

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

	SHL(0x07, "simulator.instructions.alu.XORInstruction"),
	SHR(0x08, "simulator.instructions.alu.XORInstruction"),

	CMP(0x07, "simulator.instructions.alu.XORInstruction"),

	// LOAD/STORE
	LD(0x10, "simulator.instructions.lds.LDInstruction"),
	ST(0x11, "simulator.instructions.lds.STInstruction"),

	// BRANCH

	// J - address
	// JR - jump address reg
	// BGEZ R1 >= 0
	// BLTZ R1 <= 0

	// BNE/BEQ?

	J(0x20, "simulator.instructions.branch.JInstruction"),
	BGEZ(0x21,"simulator.instructions.branch.BGEZInstruction"),
	BLTZ(0x22,"simulator.instructions.branch.BLTZInstruction");

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
		} catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException
				| NoSuchMethodException | SecurityException | ClassNotFoundException e) {
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
}
