package simulator.stages;

import java.util.ArrayList;

import simulator.core.ACASim;
import simulator.core.CPUMemory;
import simulator.instructions.Instruction;
import simulator.instructions.InstructionBundle;

public class ReservationStage implements IPipelineStage {

	private ArrayList<Instruction> instructionQueue = new ArrayList<Instruction>(CPUMemory.RS_WIDTH);

	@Override
	public void tick() {
		pumpInstructions();
	}

	private void pumpInstructions() {
		// try to keep EUs fed with instructions
		for(ExecutionUnit eu : ACASim.getInstance().executionUnits.keySet()) {
			for(ExecutionUnitStage eus : ACASim.getInstance().executionUnits.get(eu)) {
				if(eus.canAcceptInstruction()) {
					ACASim.dbgLog("Free eu: " + eu + " " + eus);

					for(int i = 0; i < instructionQueue.size(); i++) {
						Instruction next = instructionQueue.get(i);

						ACASim.dbgLog("Instr: " + next.getOpcode() + " " + next.getEU() + " " + next);

						if(next.isPurged()) {
							ACASim.dbgLog("Purging from queue");
							instructionQueue.remove(i);
							break;
						}

						if(eus.getType() == next.getEU() && next.operandsAvailable()) {
							next.fetchOperands();

							eus.acceptTransaction(next);
							instructionQueue.remove(i);

							ACASim.dbgLog("Pass " + next.getOpcode() + " to " + eus);

							break;
						}
					}
				}
			}
		}
	}

	@Override
	public void acceptNextInstruction(Instruction instr) {
		throw new IllegalStateException("Attempted to pass single instruction to reservation stage");
	}

	@Override
	public boolean canAcceptInstruction() {
		return instructionQueue.size() + CPUMemory.FETCH_WIDTH <= CPUMemory.RS_WIDTH;
	}

	@Override
	public boolean isResultAvailable() {
		return true;
	}

	@Override
	public IStageTransaction getCurrentTransaction() {
		// TODO
		return null;
	}

	@Override
	public IStageTransaction getResult() {
		//InstructionBundle res = curr;
		//curr = null;
		//return res;
		// TODO
		return null;
	}

	@Override
	public void clearOldInstruction() {
		//if(curr != null) return;

		//old = null;
	}

	@Override
	public void acceptTransaction(IStageTransaction tr) {
		if(!(tr instanceof InstructionBundle)) {
			throw new IllegalStateException("Attempted to pass non-bundle to reservation stage");
		}

		InstructionBundle curr = (InstructionBundle) tr;

		while(curr.hasInstructions()) {
			Instruction enc = curr.fetchInstruction();
			instructionQueue.add(enc);

			ACASim.dbgLog("Accept: " + enc.getOpcode());

			if(instructionQueue.size() > CPUMemory.RS_WIDTH) {
				throw new IllegalStateException("Overfull instruction queue");
			}
		}
	}

}
