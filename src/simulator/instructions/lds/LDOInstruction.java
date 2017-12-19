package simulator.instructions.lds;

import simulator.instructions.Instruction;
import simulator.instructions.Opcode;
import simulator.stages.ExecutionUnit;

public class LDOInstruction extends Instruction {

	public LDOInstruction() {
		super(Opcode.LDO.hex(), 3, ExecutionUnit.LDS);
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
		super.srcreg1 = super.op2;
		super.destreg = super.op1;
	}

	@Override
	public void writeBack() {
		super.result = super.cpu.mem().DMEM[super.regval1 + super.op3];
		super._writeBack();
	}

}
