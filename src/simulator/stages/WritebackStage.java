package simulator.stages;

import simulator.core.ACASim;
import simulator.instructions.Instruction;

public class WritebackStage implements IPipelineStage {

	private Instruction old = null;
	private Instruction curr = null;
	private Instruction next = null;

	@Override
	public void tick() {
		ACASim.dbgLog("WRITEBACK");

		for(ExecutionUnit eu : ACASim.getInstance().executionUnits.keySet()) {
			for(ExecutionUnitStage eus : ACASim.getInstance().executionUnits.get(eu)) {
				if(eus.isResultAvailable()) {
					Instruction result = (Instruction) eus.getResult();
					result.writeBack();
				}
			}
		}
		
			/*
		//if(!canAcceptInstruction()) {
		//	System.out.println("stalled");
		//	return;
		//}
		
		if(next == null) {
			ACASim.dbgLog("skip");
			return;
		}
		
		curr = next;
		next = null;

		curr.writeBack();
		
		old = curr;
		
		curr = null;
		*/
	}

	@Override
	public void acceptTransaction(IStageTransaction tr) {
		/*
		if(tr instanceof Instruction) {
			acceptNextInstruction((Instruction) tr);
		} else {
			throw new IllegalStateException("Attempted to pass invalid transaction to WriteBack");
		}
		*/
	}
	
	@Override
	public void acceptNextInstruction(Instruction instr) {
		//next = instr;
	}

	@Override
	public boolean canAcceptInstruction() {
		return true;
		//return curr == null && next == null;
	}

	@Override
	public boolean isResultAvailable() {
		return true;
		//return curr != null;
	}
	
	@Override
	public IStageTransaction getResult() {
		return null;
		//Instruction res = curr;
		//curr = null;
		//return res;
	}
	
	@Override
	public IStageTransaction getCurrentTransaction() {
		return old;
	}

	@Override
	public void clearOldInstruction() {
		old = null;
	}
	
}
