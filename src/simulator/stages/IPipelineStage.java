package simulator.stages;

import simulator.instructions.Instruction;

public interface IPipelineStage {

	public void tick();
	public boolean isResultAvailable();
	public Instruction getResult();
	public Instruction getCurrentInstruction();
	public void acceptNextInstruction(Instruction instr);
	public boolean canAcceptInstruction();
	public void clearOldInstruction();
	
}
