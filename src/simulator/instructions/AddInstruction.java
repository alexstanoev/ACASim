package simulator.instructions;

public class AddInstruction extends Instruction {

	public AddInstruction() {
		super(Opcode.ADD.hex(), 1);
	}

	// ADD R1 R2 R3 -> R3 = R1 + R2
	@Override
	public void execute() {
		System.out.println("ADD execute");
		
		if(super.cyclesPassed()) {
			super.result = super.cpu.mem().REG[super.op1] + super.cpu.mem().REG[super.op2];
			
			System.out.println("releasing result");
		} else {
			System.out.println("exec pass");
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
