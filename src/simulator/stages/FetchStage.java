package simulator.stages;

import simulator.core.ACASim;
import simulator.core.CPUMemory;
import simulator.instructions.Instruction;
import simulator.instructions.UnknownInstruction;

public class FetchStage implements IPipelineStage {

	private Instruction curr = null;
	//private Instruction next = null;

	@Override
	public void tick() {
		System.out.println("FETCH");

		CPUMemory state = ACASim.getInstance().mem();
		int currOpcode = state.fetchInstrOpcode(state.PC);

		curr = new UnknownInstruction(currOpcode);

		state.PC++;
	}

	@Override
	public void acceptNextInstruction(Instruction instr) {
		return;
	}

	@Override
	public boolean canAcceptInstruction() {
		return curr == null;
	}

	@Override
	public Instruction getResult() {
		Instruction res = curr;
		curr = null;
		return res;
	}

}
