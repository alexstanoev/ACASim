package simulator.instructions;

public class AddIInstruction extends Instruction {

	public AddIInstruction() {
		super(Opcode.ADDI.hex(), 1);
	}

	// ADD R1 R2 R3 -> R3 = R1 + R2
	@Override
	public void execute() {
		System.out.println("ADDI execute");
		
		if(super.cyclesPassed()) {
			super.result = super.cpu.mem().REG[super.op1] + super.op2;
		}
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
