package simulator.instructions.lds;

import simulator.instructions.Instruction;
import simulator.instructions.Opcode;
import simulator.stages.ExecutionUnit;

public class LDInstruction extends Instruction {

	public LDInstruction() {
		super(Opcode.LD.hex(), 3, ExecutionUnit.LDS);
	}
	
	// ST R1 R2-> R2 = DMEM[R1]
	@Override
	public void execute() {
		if(super.cyclesPassed()) {
			//super.result = super.cpu.mem().DMEM[super.regval1 + super.op3];
			super.result = 0;
		}
	}

	@Override
	public void decode() {
		super.srcreg1 = super.op1;
		super.srcreg2 = super.op2;
		super.destreg = super.op3;
	}

	@Override
	public void writeBack() {
		super.result = super.cpu.mem().DMEM[super.regval1 + super.regval2];
		super._writeBack();
	}

}
