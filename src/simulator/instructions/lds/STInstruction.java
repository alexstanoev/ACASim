package simulator.instructions.lds;

import simulator.instructions.Instruction;
import simulator.instructions.Opcode;

public class STInstruction extends Instruction {

	public STInstruction() {
		super(Opcode.ST.hex(), 3);
	}
	
	// ST R1 R2-> R2 = DMEM[R1]
	@Override
	public void execute() {
		if(super.cyclesPassed()) {
			int rdest = super.cpu.mem().REG[super.op1];
			super.result = super.cpu.mem().DMEM[rdest];
		}
	}

	@Override
	public void decode() {
		super.dest = super.op2;
	}

	@Override
	public void writeBack() {
		super._writeBack();
	}

}
