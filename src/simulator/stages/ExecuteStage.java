package simulator.stages;

import simulator.core.ACASim;
import simulator.instructions.Instruction;

public class ExecuteStage implements IPipelineStage {

	@Override
	public void tick() {
		ACASim.dbgLog("EXECUTE ALL");

		for(ExecutionUnit eu : ACASim.getInstance().executionUnits.keySet()) {
			for(ExecutionUnitStage eus : ACASim.getInstance().executionUnits.get(eu)) {
				eus.tick();
			}
		}
	}

	@Override
	public void acceptTransaction(IStageTransaction tr) {
		// don't care
	}

	@Override
	public void acceptNextInstruction(Instruction instr) {
		// don't care
	}

	@Override
	public boolean canAcceptInstruction() {
		return true;
	}

	@Override
	public boolean isResultAvailable() {
		return true;
	}

	@Override
	public IStageTransaction getResult() {
		return null;
	}

	@Override
	public IStageTransaction getCurrentTransaction() {
		return null;
	}

	@Override
	public void clearOldInstruction() {
		// don't care
	}

}
