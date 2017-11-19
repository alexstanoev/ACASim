package simulator.instructions;

import simulator.core.ACASim;
import simulator.stages.ExecutionUnit;
import simulator.stages.IStageTransaction;

public abstract class Instruction implements IStageTransaction {

	protected ACASim cpu;

	private int rawOpcode; // encoded instruction
	private Opcode opcode; // decoded opcode
	private int address;   // address in Imem

	protected int op1;  // operand 1
	protected int op2;  // operand 2
	protected int op3;  // operand 3

	protected int srcreg1 = -1;
	protected int srcreg2 = -1;
	protected int destreg = -1; // destination register

	protected Integer result = null; // temporary result (from Execute stage), written to dest in WriteBack stage
	protected int clockCycles; // clock cycles before the result is released
	protected int currCycles = 0;

	protected ExecutionUnit eu;

	public Instruction(int _rawOpcode, int _clockCycles, ExecutionUnit _eu) {
		this.rawOpcode = _rawOpcode;
		this.clockCycles = _clockCycles;
		this.eu = _eu;
		this.cpu = ACASim.getInstance();
	}

	public void setRawOpcode(int _rawOpcode) {
		this.rawOpcode = _rawOpcode;
	}

	public int getRawOpcode() {
		return rawOpcode;
	}

	public void setOpcode(Opcode decoded) {
		this.opcode = decoded;
	}

	public void setOperands(int _op1, int _op2, int _op3) {
		this.op1 = _op1;
		this.op2 = _op2;
		this.op3 = _op3;
	}

	public Opcode getOpcode() {
		return opcode;
	}

	public void scoreboardDestReg() {
		if(destreg != -1) {
			cpu.mem().SCOREBOARD[destreg] = false;
		}
	}

	public boolean operandsAvailable() {
		boolean avail = true;

		if(srcreg1 != -1) {
			avail = avail && cpu.mem().SCOREBOARD[srcreg1];
		}

		if(srcreg2 != -1) {
			avail = avail && cpu.mem().SCOREBOARD[srcreg2];
		}
		
		ACASim.dbgLog(opcode + " avail operands: " + avail);

		return avail;
	}

	public void _writeBack() {
		if(destreg == -1) {
			throw new IllegalStateException("Attempted to write back instruction that does not set destreg");
		}

		ACASim.dbgLog("Write to R" + destreg + " - " + result);
		cpu.mem().REG[destreg] = result;
		cpu.mem().SCOREBOARD[destreg] = true;
	}

	public boolean isResultAvailable() {
		return result != null && currCycles == clockCycles;
	}

	protected boolean cyclesPassed() {
		currCycles++;

		if(currCycles > clockCycles) {
			throw new IllegalStateException("Instruction " + opcode + " took more cycles than requested. No result assigned?");
		}

		return currCycles == clockCycles;
	}

	public void setAddress(int pc) {
		this.address = pc;
	}

	public int getAddress() {
		return this.address;
	}

	public int getCyclesRemaining() {
		return clockCycles - currCycles;
	}

	public ExecutionUnit getEU() {
		return eu;
	}

	public abstract void decode();
	public abstract void execute();
	public abstract void writeBack();

}
