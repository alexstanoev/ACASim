package simulator.instructions;

public class NOPInstruction extends Instruction {

	public NOPInstruction() {
		super(Opcode.NOP.hex(), 1);
	}

	// do nothing
	@Override
	public void execute() {
		return;
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
