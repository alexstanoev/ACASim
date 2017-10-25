package simulator.instructions.lds;

import simulator.instructions.Instruction;
import simulator.instructions.Opcode;

public class LDInstruction extends Instruction {

	public LDInstruction() {
		super(Opcode.LD.hex(), 1);
	}
	
	// LDI I1 R1-> R1 = I1
	@Override
	public void execute() {
		if(super.cyclesPassed()) {
			super.cpu.mem().DMEM[super.dest] = super.cpu.mem().REG[super.op2];
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
