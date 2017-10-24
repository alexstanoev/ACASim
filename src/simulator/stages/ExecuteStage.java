package simulator.stages;

import simulator.instructions.Instruction;

public class ExecuteStage implements IPipelineStage {

	private Instruction old = null;
	private Instruction curr = null;
	private Instruction next = null;

	@Override
	public void tick() {
		System.out.println("EXECUTE");

		if(next != null) {
			curr = next;
			next = null;
			old = curr;
			
			System.out.println("new instruction " + curr.getRawOpcode());
		}

		if(curr == null) {
			System.out.println("exec nop");
			return;
		}
		
		curr.execute();
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
			System.out.println("avail: " + curr.isResultAvailable());
			return curr.isResultAvailable();
		}
		
		return false;
	}
	
	@Override
	public Instruction getResult() {
		Instruction res = curr;
		curr = null;
		return res;
	}
	
	@Override
	public Instruction getCurrentInstruction() {
		return old;
	}

	@Override
	public void clearOldInstruction() {
		if(curr != null && !curr.isResultAvailable()) return;
		
		old = null;
	}
	
}
