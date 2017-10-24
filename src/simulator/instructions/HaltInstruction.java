package simulator.instructions;

public class HaltInstruction extends Instruction {

	public HaltInstruction() {
		super(Opcode.HALT.hex(), 1);
	}

	// (special case) halt the CPU
	@Override
	public void execute() {
		if(super.cyclesPassed()) {
			super.cpu.halt();
		}
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
