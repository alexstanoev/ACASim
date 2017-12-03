package simulator.core;

import simulator.instructions.Instruction;
import simulator.stages.ExecutionUnit;

public class BranchPredictor {

	private boolean predictedContext = false;
	private boolean stallDecode = false;

	private int oldPC;
	private int predictedPC;

	public void onBranchExecuted(Instruction instr) {
		ACASim.dbgLog("BRANCH EXECUTED");
		
		predictedContext = false;
		
		if(ACASim.getInstance().mem().PC == predictedPC) {
			ACASim.dbgLog("GUESS CORRECT");
			// guess was correct, mark all speculative instructions in RB as not speculative
			for(Instruction rbi : ACASim.getInstance().reorderBuffer) {
				if(rbi.isSpeculative()) {
					rbi.setSpeculative(false);
				}
				//else {
				//	// iterating from the tail of the queue - the first non speculative instruction marks the end
				//	break;
				//}
			}
		} else {
			ACASim.dbgLog("GUESS INCORRECT");
			
			// guess was incorrect, drop all speculative instructions
			while(ACASim.getInstance().reorderBuffer.size() > 0 && ACASim.getInstance().reorderBuffer.peekFirst().isSpeculative()) {
				Instruction rm = ACASim.getInstance().reorderBuffer.removeFirst();
				
				// tell any execution units and reservation stations to throw it away too
				rm.purge();

				ACASim.dbgLog("INCORRECT " + rm);
			}
		}
		// if the guess was correct then remove the speculative and no execute bit from every instr in the reorder buffer after targetaddr
		// otherwise remove all instrs from rb after targetaddr
	}

	public void onInstructionDecoded(Instruction instr) {
		if(instr.getEU() == ExecutionUnit.BRANCH) {
			if(predictedContext) {
				// already predicted a branch, stall until it has been executed TODO
				ACASim.dbgLog("Hit branch inside speculative context!");
				return;
			}

			predictedContext = true;

			// TODO decode target PC from instruction
			// predict taken or not taken
			// keep branch delay slots in mind

			// predict not taken
			predictedPC = ACASim.getInstance().mem().PC;

			oldPC = ACASim.getInstance().mem().PC;
			ACASim.getInstance().mem().PC = predictedPC;
		} else {
			if(predictedContext) {
				instr.setSpeculative(true);
				ACASim.dbgLog("Marking instr as speculative");
			}
		}
	}

	// TODO
	public boolean allowDecodeTransactions() {
		return !stallDecode;
	}
}
