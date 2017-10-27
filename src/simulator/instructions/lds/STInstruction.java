package simulator.instructions.lds;

import simulator.instructions.Instruction;
import simulator.instructions.Opcode;

public class STInstruction extends Instruction {

	public STInstruction() {
		super(Opcode.ST.hex(), 3);
	}
	
	// ST R1 R2-> DMEM[R2] = R1
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