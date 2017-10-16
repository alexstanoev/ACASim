package simulator.core;

import java.util.ArrayList;

public class CPUMemory {

	// 1k of RAM
	public static final int MEMSIZE = 128;
	public static final int NUMREGS = 10;
	public char[] DMEM = new char[MEMSIZE];
	
	// instruction memory - contiguous, variable-length
	private ArrayList<Integer> imem = new ArrayList<Integer>();
	
	//private ArrayList<Integer> dmem = new ArrayList<Integer>();
	
	public int PC;
	
	// register file, 10 general-purpose registers
	public int[] REG = new int[NUMREGS];

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
