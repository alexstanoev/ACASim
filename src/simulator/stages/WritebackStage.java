package simulator.stages;

import simulator.instructions.Instruction;

public class WritebackStage implements IPipelineStage {

	private Instruction old = null;
	private Instruction curr = null;
	private Instruction next = null;

	@Override
	public void tick() {
		System.out.println("WRITEBACK");

		//if(!canAcceptInstruction()) {
		//	System.out.println("stalled");
		//	return;
		//}
		
		if(next == null) {
			System.out.println("skip");
			return;
		}
		
		curr = next;
		next = null;

		curr.writeBack();
		
		old = curr;
		
		curr = null;
	}

	@Override
	public void acceptNextInstruction(Instruction instr) {
		next = instr;
	}

	@Override
	public boolean canAcceptInstruction() {
		return curr == null && next == null;
	}

	@Override
	public boolean isResultAvailable() {
		return curr != null;
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
		old = null;
	}
	
}
