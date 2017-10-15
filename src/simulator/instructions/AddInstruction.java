package simulator.instructions;

public class AddInstruction extends Instruction {

	public AddInstruction() {
		super(Opcode.ADD.hex());
	}

	// ADD R1 R2 R3 -> R3 = R1 + R2
	@Override
	public void execute() {
		super.result = super.cpu.mem().REG[super.op1] + super.cpu.mem().REG[super.op2];
	}

	@Override
	public void decode() {
		super.dest = super.op3;
	}

	@Override
	public void writeBack() {
		super._writeBack();
	}

}
