package simulator.instructions;

import java.util.ArrayDeque;

import simulator.stages.IStageTransaction;

public class InstructionBundle implements IStageTransaction {

	private ArrayDeque<Instruction> instructions = new ArrayDeque<Instruction>();

	public void pushInstruction(Instruction instr) {
		instructions.add(instr);
	}

	public Instruction fetchInstruction() {
		return instructions.poll();
	}
	
	public boolean hasInstructions() {
		return instructions.peek() != null;
	}
}
