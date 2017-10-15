package simulator.instructions;

import simulator.core.ACASim;

public enum Opcode {

	NOP(0x00),
	HALT(0x0f),

	ADD(0x01),
	ADDI(0x02),
	CMP(0x03),

	LD(0x10),
	LDI(0x11),
	STR(0x12),

	B(0x20),
	J(0x21),
	BL(0x22);


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
