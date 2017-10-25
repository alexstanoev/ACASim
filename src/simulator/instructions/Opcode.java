package simulator.instructions;

import simulator.core.ACASim;

public enum Opcode {

	NOP(0x00),
	HALT(0x0f),

	// ALU
	ADD(0x01),
	ADDI(0x02),
	SUB(0x03),
	MUL(0x04),
	DIV(0x05),
	XOR(0x06),
	
	SHL(0x07),
	SHR(0x08),
	
	CMP(0x07),
	
	// LOAD/STORE
	LD(0x10),
	LDI(0x11),
	STR(0x12),

	// BRANCH
	
	// J - address
	// JR - jump address reg
	// BGEZ R1 >= 0
	// BLTZ R1 <= 0
	
	// BNE/BEQ?
	
	J(0x20),
	BGEZ(0x21),
	BLTZ(0x22);

	private int _hex;

	Opcode(int binVal) {
		this._hex = binVal;
	}

	public int hex() {
		return _hex;
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
