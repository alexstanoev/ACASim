package simulator.stages;

import simulator.instructions.Instruction;

public interface IPipelineStage {

	public void tick();
	public Instruction getResult();
	public void acceptNextInstruction(Instruction instr);
	public boolean canAcceptInstruction();
	
}
