package simulator.stages;

import simulator.instructions.Instruction;

public class ExecuteStage implements IPipelineStage {

	private Instruction curr = null;
	private Instruction next = null;

	@Override
	public void tick() {
		System.out.println("EXECUTE");

		if(next == null) {
			System.out.println("skip");
			return;
		}
		
		curr = next;
		next = null;

		curr.execute();
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
	public Instruction getResult() {
		Instruction res = curr;
		curr = null;
		return res;
	}

}
