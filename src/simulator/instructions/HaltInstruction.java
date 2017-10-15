package simulator.instructions;

public class HaltInstruction extends Instruction {

	public HaltInstruction() {
		super(Opcode.HALT.hex());
	}

	// (special case) halt the CPU
	@Override
	public void execute() {
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
