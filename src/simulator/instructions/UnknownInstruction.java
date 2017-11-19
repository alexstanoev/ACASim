package simulator.instructions;

import simulator.stages.ExecutionUnit;

public class UnknownInstruction extends Instruction {

	public UnknownInstruction(int rawForm) {
		super(rawForm, 1, ExecutionUnit.UNKNOWN);
	}
	
	@Override
	public void execute() {
		throw new IllegalStateException("Attempted to execute UnknownInstruction");
	}

	@Override
	public void decode() {
		throw new IllegalStateException("Attempted to decode UnknownInstruction");
	}

	@Override
	public void writeBack() {
		throw new IllegalStateException("Attempted to writeBack UnknownInstruction");
	}

}
