package simulator.stages;

import simulator.core.ACASim;
import simulator.instructions.Instruction;

public class ExecutionUnitStage implements IPipelineStage {

	private Instruction old = null;
	private Instruction curr = null;
	private Instruction next = null;

	private ExecutionUnit type;
	
	public ExecutionUnitStage(ExecutionUnit _type) {
		this.type = _type;
	}
	
	@Override
	public void tick() {
		ACASim.dbgLog("EXECUTE " + type);

		if(next != null) {
			curr = next;
			next = null;
			old = curr;
			
			ACASim.dbgLog("new instruction " + String.format("0x%08X", curr.getRawOpcode()));
		}

		if(curr == null) {
			ACASim.dbgLog("exec nop");
			return;
		}
		
		curr.execute();
		
		if(curr.isResultAvailable()) {
			ACASim.dbgLog("tossing out " + curr);
			curr = null;
		}
	}

	@Override
	public void acceptTransaction(IStageTransaction tr) {
		if(tr instanceof Instruction) {
			acceptNextInstruction((Instruction) tr);
		} else {
			throw new IllegalStateException("Attempted to pass invalid transaction to Execute");
		}
	}
	
	@Override
	public void acceptNextInstruction(Instruction instr) {
		if(!canAcceptInstruction()) {
			throw new IllegalStateException("Attempted to pass instruction when canAccept is false");
		}
		
		next = instr;
	}

	@Override
	public boolean canAcceptInstruction() {
		return curr == null && next == null;
	}

	@Override
	public boolean isResultAvailable() {
		if(curr != null) {
			ACASim.dbgLog("avail: " + curr.isResultAvailable());
			return curr.isResultAvailable();
		}
		
		return false;
	}
	
	@Override
	public IStageTransaction getResult() {
		Instruction res = curr;
		curr = null;
		return res;
	}
	
	@Override
	public IStageTransaction getCurrentTransaction() {
		return old;
	}

	@Override
	public void clearOldInstruction() {
		if(curr != null && !curr.isResultAvailable()) return;
		
		old = null;
	}
	
	public ExecutionUnit getType() {
		return type;
	}
	
}
