package simulator.instructions;

import simulator.core.ACASim;

public abstract class Instruction {

	protected ACASim cpu;
	
	private int rawOpcode; // encoded instruction
	private Opcode opcode; // decoded opcode
	private int address;   // address in Imem
	
	protected int op1;  // operand 1
	protected int op2;  // operand 2
	protected int op3;  // operand 3
	protected int dest; // destination register
	
	protected Integer result = null; // temporary result (from Execute stage), written to dest in WriteBack stage
	protected int clockCycles; // clock cycles before the result is released
	protected int currCycles = 0;
	
	public Instruction(int _rawOpcode, int _clockCycles) {
		this.rawOpcode = _rawOpcode;
		this.clockCycles = _clockCycles;
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
	
	public void _writeBack() {
		System.out.println("Write to R" + dest + " - " + result);
		cpu.mem().REG[dest] = result;
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
	
	public abstract void decode();
	public abstract void execute();
	public abstract void writeBack();

}
