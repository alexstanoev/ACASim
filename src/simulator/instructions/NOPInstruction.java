package simulator.instructions;

public class NOPInstruction extends Instruction {

	public NOPInstruction() {
		super(Opcode.NOP.hex(), 1);
	}

	// do nothing
	@Override
	public void execute() {
		super.cyclesPassed();
		super.result = 0; // TODO better way
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
