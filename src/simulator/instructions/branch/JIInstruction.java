package simulator.instructions.branch;

import simulator.core.ACASim;
import simulator.instructions.Instruction;
import simulator.instructions.Opcode;
import simulator.stages.ExecutionUnit;

public class JIInstruction extends Instruction {

	public JIInstruction() {
		super(Opcode.JI.hex(), 1, ExecutionUnit.BRANCH);
	}

	// JI I1 -> PC = I1
	// branch delay slot: the instruction after jumps is always executed
	@Override
	public void execute() {
		if(super.cyclesPassed()) {
			ACASim.dbgLog("PC " + super.cpu.mem().PC + " -> " + super.op1);
			super.cpu.mem().PC = super.op1;
			
			// release dummy result (TODO better way)
			super.result = 0;
		}
	}

	@Override
	public void decode() {
	}

	@Override
	public void writeBack() {
		//super._writeBack();
	}

}
