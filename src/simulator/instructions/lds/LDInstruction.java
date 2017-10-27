package simulator.instructions.lds;

import simulator.instructions.Instruction;
import simulator.instructions.Opcode;

public class LDInstruction extends Instruction {

	public LDInstruction() {
		super(Opcode.LD.hex(), 3);
	}
	
	// LD R1 R2-> R2 = DMEM[R1]
	@Override
	public void execute() {
		if(super.cyclesPassed()) {
			super.result = super.cpu.mem().DMEM[super.op2];
		}
	}

	@Override
	public void decode() {
		super.dest = super.op1;
	}

	@Override
	public void writeBack() {
		super._writeBack();
	}

}
