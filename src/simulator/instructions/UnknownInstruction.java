package simulator.instructions;

public class UnknownInstruction extends Instruction {

	public UnknownInstruction(int rawForm) {
		super(rawForm);
	}
	
	@Override
	public void execute() {
		return;
	}

	@Override
	public void decode() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void writeBack() {
		return;
	}

}
