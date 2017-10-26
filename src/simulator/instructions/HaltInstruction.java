package simulator.instructions;

public class HaltInstruction extends Instruction {

	public HaltInstruction() {
		super(Opcode.HALT.hex(), 1);
	}

	// (special case) halt the CPU
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
