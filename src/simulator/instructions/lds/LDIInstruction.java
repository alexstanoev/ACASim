package simulator.instructions.lds;

import simulator.instructions.Instruction;
import simulator.instructions.Opcode;
import simulator.stages.ExecutionUnit;

public class LDIInstruction extends Instruction {

	public LDIInstruction() {
		super(Opcode.LDI.hex(), 3, ExecutionUnit.LDS);
	}
	
	// LDI R1 R2 -> DMEM[R2] = R1
	@Override
	public void execute() {
		if(super.cyclesPassed()) {
			super.cpu.mem().DMEM[super.op2] = super.op1;
			
			super.result = 0;
		}
	}

	@Override
	public void decode() {
		//super.destreg = super.op2;
	}

	@Override
	public void writeBack() {
		//super._writeBack();
	}

}
