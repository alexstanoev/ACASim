package simulator.stages;

import simulator.instructions.Instruction;

public interface IPipelineStage {

	public void tick();
	public boolean isResultAvailable();
	public IStageTransaction getResult();
	public IStageTransaction getCurrentTransaction();
	public void acceptNextInstruction(Instruction instr);
	public void acceptTransaction(IStageTransaction tr);
	public boolean canAcceptInstruction();
	public void clearOldInstruction();
	
}
