package simulator.core;

import java.util.ArrayList;
import java.util.Arrays;

import simulator.instructions.Instruction;

public class CPUMemory {

	public static final int FETCH_WIDTH = 4;
	public static final int RS_WIDTH = 16;
	public static final int RB_SIZE = 32;
	public static final int WB_PER_CYCLE = 8;

	public static final int MEMSIZE = 512;
	public static final int NUMARCHREGS = 16;
	public static final int NUMPHYSREGS = 64;

	public int[] DMEM = new int[MEMSIZE];

	// instruction memory - contiguous, variable-length
	private ArrayList<Integer> imem = new ArrayList<Integer>();

	public int PC;

	//private int[] REG = new int[NUMARCHREGS];

	private int[] HWREG = new int[NUMPHYSREGS];
	private boolean[] HWREG_ALLOC = new boolean[NUMPHYSREGS];

	// remap file
	private int[] REG_MAPPING = new int[NUMARCHREGS];
	private int[] REG_MAPPING_SAVED = new int[NUMARCHREGS];

	// remap ready tag
	private boolean[] SCOREBOARD = new boolean[NUMPHYSREGS];

	public CPUMemory() {
		Arrays.fill(SCOREBOARD, true);
		Arrays.fill(REG_MAPPING, -1);
	}

	public int[] getDMem() {
		return DMEM;
	}

	public ArrayList<Integer> getIMemList() {
		return imem;
	}

	public int[] getReg() {
		return HWREG;
	}

	/*
		In the renaming stage, every architectural register referenced (for read or write) is looked up in an architecturally-indexed remap file. 
		This file returns a tag and a ready bit. The tag is non-ready if there is a queued instruction which will write to it that has not yet executed. 
		For read operands, this tag takes the place of the architectural register in the instruction. For every register write, a new tag is pulled 
		from a free tag FIFO, and a new mapping is written into the remap file, so that future instructions reading the architectural register will 
		refer to this new tag. The tag is marked as unready, because the instruction has not yet executed. The previous physical register allocated 
		for that architectural register is saved with the instruction in the reorder buffer, which is a FIFO that holds the instructions in program 
		order between the decode and graduation stages.

		The instructions are then placed in various issue queues. As instructions are executed, the tags for their results are broadcast, and the issue
		queues match these tags against the tags of their non-ready source operands. A match means that the operand is ready. The remap file also matches
		these tags, so that it can mark the corresponding physical registers as ready. When all the operands of an instruction in an issue queue are ready,
		that instruction is ready to issue. The issue queues pick ready instructions to send to the various functional units each cycle. 
		Non-ready instructions stay in the issue queues.

		An exception or branch misprediction causes the remap file to back up to the remap state at last valid instruction. 
	 */

	public void saveRegMap() {
		System.arraycopy(REG_MAPPING, 0, REG_MAPPING_SAVED, 0, NUMARCHREGS);
	}

	public void restoreRegMap() {
		System.arraycopy(REG_MAPPING_SAVED, 0, REG_MAPPING, 0, NUMARCHREGS);
	}

	public int getArchReg(int archReg) {
		if(REG_MAPPING[archReg] == -1) {
			// not mapped, zero
			return -2;
		}

		return HWREG[REG_MAPPING[archReg]];
	}

	public int getTagArchMap(int tag) {
		for(int i = 0; i < NUMARCHREGS; i++) {
			if(REG_MAPPING[i] == tag) {
				return i;
			}
		}
		return -1;
	}

	public int getTag(int archReg) {
		if(archReg >= NUMARCHREGS || REG_MAPPING[archReg] == -1) {
			// not mapped, zero
			return -2;
		}
		return REG_MAPPING[archReg];
	}

	// resolve renamed mapping
	public int readReg(int tag) {
		if(tag < 0) {
			return 0;
		}
		//if(REG_MAPPING[tag] == -1) {
		//	ACASim.dbgLog("no mapping");
		//	return 0;
		//}
		return HWREG[tag];
	}

	// write to renamed register
	public void writeReg(int tag, int val) {
		if(tag == -1) {
			ACASim.dbgLog("no mapping");
			return;
		}

		HWREG[tag] = val;
		SCOREBOARD[tag] = true;

		//ACASim.dbgLog("Out of registers");
	}

	public boolean isSBAvail(int tag) {
		if(tag < 0) {
			return true;
		}

		//System.out.println(tag + " -> " + SCOREBOARD[tag]);

		return SCOREBOARD[tag];
	}

	public void setSB(int tag, boolean val) {
		if(tag < 0) {
			ACASim.dbgLog("sb no map");
			return;
		}
		SCOREBOARD[tag] = val;
	}

	public int tagsAvailable() {
		int avail = 0;
		for(int i = 0; i < NUMPHYSREGS; i++) {
			if(!HWREG_ALLOC[i]) {
				avail++;
			}
		}
		
		return avail;
	}
	
	public void gcTags() {
		// need to leave the last copy of a register in case it is used in the future

		for(int i = 0; i < NUMPHYSREGS; i++) {
			if(HWREG_ALLOC[i]) {
				if(getTagArchMap(i) != -1) {
					continue;
				}

				boolean used = false;
				for(Instruction rb : ACASim.getInstance().reorderBuffer) {
					if(rb.usesTag(i)) {
						used = true;
						break;
					}
				}

				if(!used) {
					HWREG_ALLOC[i] = false;
				}
			}
		}
	}

	public int allocTag(int archReg) {
		for(int i = 0; i < NUMPHYSREGS; i++) {
			if(!HWREG_ALLOC[i]) {
				HWREG_ALLOC[i] = true;
				REG_MAPPING[archReg] = i;
				SCOREBOARD[i] = false;
				ACASim.dbgLog("alloc " + i + " to " + archReg);
				return i;
			}
		}

		// out of registers, hope a gc will help
		gcTags();
		
		return allocTag(archReg);
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

	public void dumpArchRegisters() {
		for(int i = 0; i < NUMARCHREGS; i++) {
			int hw = REG_MAPPING[i];
			if(hw >= 0 && HWREG_ALLOC[hw]) {
				System.out.println("R" + i + ": " + String.format("0x%08X", HWREG[hw]));
			} else {
				System.out.println("R" + i + ": unused");
			}
		}
	}

}
