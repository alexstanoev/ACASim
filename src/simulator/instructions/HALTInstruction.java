package simulator.instructions;

import simulator.stages.ExecutionUnit;

public class HALTInstruction extends Instruction {

	public HALTInstruction() {
		super(Opcode.HALT.hex(), 1, ExecutionUnit.ALU);
	}

	// (special case) halt the CPU
	// need to stuff the pipeline with NOPs manually (TODO fix)
	@Override
	public void execute() {
		super.cyclesPassed();
		super.result = 1;
		super.cpu.halt();
	}

	@Override
	public void decode() {
		return;
	}

	@Override
	public void writeBack() {
		return;
	}

}
