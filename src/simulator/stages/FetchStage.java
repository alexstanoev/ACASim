package simulator.stages;

import simulator.core.ACASim;
import simulator.core.CPUMemory;
import simulator.instructions.Instruction;
import simulator.instructions.UnknownInstruction;

public class FetchStage implements IPipelineStage {

	private Instruction old = null;
	private Instruction curr = null;
	//private Instruction next = null;

	@Override
	public void tick() {
		ACASim.dbgLog("FETCH");

		if(!canAcceptInstruction()) {
			ACASim.dbgLog("stalled");
			return;
		}
		
		CPUMemory state = ACASim.getInstance().mem();
		int currOpcode = state.fetchInstrOpcode(state.PC);

		ACASim.dbgLog("new instruction " + String.format("0x%08X", currOpcode));
		
		curr = new UnknownInstruction(currOpcode);
		curr.setAddress(state.PC);

		state.PC++;
		
		old = curr;
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
		if(curr != null) return;
		
		old = null;
	}

}
