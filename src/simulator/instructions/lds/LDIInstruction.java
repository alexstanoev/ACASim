package simulator.instructions.lds;

import simulator.instructions.Instruction;
import simulator.instructions.Opcode;

public class LDIInstruction extends Instruction {

	public LDIInstruction() {
		super(Opcode.LDI.hex(), 1);
	}
	
	// LDI I1 R1-> R1 = I1
	@Override
	public void execute() {
		if(super.cyclesPassed()) {
			super.cpu.mem().DMEM[super.dest] = super.op2;
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
