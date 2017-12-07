package simulator.stages;

import simulator.core.ACASim;
import simulator.core.CPUMemory;
import simulator.instructions.Instruction;
import simulator.instructions.InstructionBundle;
import simulator.instructions.UnknownInstruction;

public class FetchStage implements IPipelineStage {

	private InstructionBundle old = null;
	private InstructionBundle curr = null;
	//private Instruction next = null;

	//private boolean stopFetch = false;
	
	@Override
	public void tick() {
		ACASim.dbgLog("FETCH");

		if(!canAcceptInstruction()) {
			ACASim.dbgLog("stalled");
			return;
		}

		CPUMemory state = ACASim.getInstance().mem();

		curr = new InstructionBundle();

		for(int i = 0; i < CPUMemory.FETCH_WIDTH; i++) {
			if(state.PC >= state.getIMemList().size()) {
				ACASim.dbgLog("out-of-bounds fetch");
				//stopFetch = true;
				return;
			}
			
			int currOpcode = state.fetchInstrOpcode(state.PC);

			ACASim.dbgLog("new instruction " + String.format("0x%08X", currOpcode));

			UnknownInstruction tmp = new UnknownInstruction(currOpcode);
			tmp.setAddress(state.PC);
			
			curr.pushInstruction(tmp);

			state.PC++;
		}

		old = curr;
	}

	@Override
	public void acceptTransaction(IStageTransaction tr) {
		throw new IllegalStateException("Attempted to pass transaction to fetch stage");
	}

	@Override
	public void acceptNextInstruction(Instruction instr) {
		throw new IllegalStateException("Attempted to pass instruction to fetch stage");
	}

	@Override
	public boolean canAcceptInstruction() {
		return curr == null && ACASim.getInstance().mem().PC < ACASim.getInstance().mem().getIMemList().size();
	}

	@Override
	public boolean isResultAvailable() {
		return curr != null;
	}

	@Override
	public IStageTransaction getResult() {
		InstructionBundle res = curr;
		curr = null;
		return res;
	}

	@Override
	public IStageTransaction getCurrentTransaction() {
		return old;
	}

	@Override
	public void clearOldInstruction() {
		if(curr != null) return;

		old = null;
	}

}
