package simulator.instructions.branch;

import simulator.core.ACASim;
import simulator.instructions.Instruction;
import simulator.instructions.Opcode;
import simulator.stages.ExecutionUnit;

public class JInstruction extends Instruction {

	public JInstruction() {
		super(Opcode.J.hex(), 1, ExecutionUnit.BRANCH);
	}

	// J OP1 -> PC = REG[OP1]
	// branch delay slot: the instruction after jumps is always executed
	@Override
	public void execute() {
		if(super.cyclesPassed()) {
			ACASim.dbgLog("PC " + super.cpu.mem().PC + " -> " + super.cpu.mem().REG[super.dest]);
			super.cpu.mem().PC = super.cpu.mem().REG[super.dest];
			
			// release dummy result (TODO better way)
			super.result = 0;
		}
	}

	@Override
	public void decode() {
		super.dest = super.op1;
	}

	@Override
	public void writeBack() {
		//super._writeBack();
	}

}
