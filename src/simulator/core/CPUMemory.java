package simulator.core;

import java.util.ArrayList;
import java.util.Arrays;

public class CPUMemory {

	public static final int FETCH_WIDTH = 2;
	public static final int RS_WIDTH = 4;
	public static final int RB_SIZE = 16;

	public static final int MEMSIZE = 128;
	public static final int NUMREGS = 10;
	public int[] DMEM = new int[MEMSIZE];

	// instruction memory - contiguous, variable-length
	private ArrayList<Integer> imem = new ArrayList<Integer>();

	public int PC;

	// register file, 10 general-purpose registers
	// special use:
	// R9  - LR
	// R10 - SP
	public int[] REG = new int[NUMREGS];
	public boolean[] SCOREBOARD = new boolean[NUMREGS];

	public CPUMemory() {
		Arrays.fill(SCOREBOARD, true);
	}

	public ArrayList<Integer> getIMemList() {
		return imem;
	}

	public int fetchInstrOpcode(int iaddr) {
		try {
			return imem.get(iaddr);
		} catch(IndexOutOfBoundsException e) {
			System.err.println("Out of bounds instruction memory access at " + iaddr);
			ACASim.getInstance().halt();
			return 0;
		}
	}

	public int memget(int addr) {
		try {
			return DMEM[addr];
		} catch(IndexOutOfBoundsException e) {
			System.err.println("Out of bounds memory access at " + addr);
			ACASim.getInstance().halt();
			return 0;
		}
	}

}
