package simulator.instructions;

import simulator.stages.ExecutionUnit;

public class HALTInstruction extends Instruction {

	public HALTInstruction() {
		super(Opcode.HALT.hex(), 1, ExecutionUnit.ALU);
	}

	// (special case) halt the CPU
	@Override
	public void execute() {
		super.cyclesPassed();
		super.result = 1;
		//super.cpu.halt();
	}

	@Override
	public void decode() {
		// dummy target to allow writeback
		//super.destreg = 255;
	}

	@Override
	public void writeBack() {
		super.cpu.halt();
	}

}
