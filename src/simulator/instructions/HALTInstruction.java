package simulator.instructions;

public class HALTInstruction extends Instruction {

	public HALTInstruction() {
		super(Opcode.HALT.hex(), 1);
	}

	// (special case) halt the CPU
	// need to stuff the pipeline with NOPs manually (TODO fix)
	@Override
	public void execute() {
		super.cyclesPassed();
	}

	@Override
	public void decode() {
		return;
	}

	@Override
	public void writeBack() {
		super.cpu.halt();
	}

}
