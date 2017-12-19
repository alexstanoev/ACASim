package simulator.instructions.alu;

import simulator.core.ACASim;
import simulator.instructions.Instruction;
import simulator.instructions.Opcode;
import simulator.stages.ExecutionUnit;

public class MODInstruction extends Instruction {

	public MODInstruction() {
		super(Opcode.MOD.hex(), 3, ExecutionUnit.ALU);
	}

	// MOD R1 R2 R3 -> R3 = R1 % R2
	@Override
	public void execute() {
		if(super.cyclesPassed()) {
			if(super.regval2 == 0) {
				super.result = 0;
			} else {
				super.result = super.regval1 % super.regval2;
				ACASim.dbgLog(super.regval1 + " % " + super.regval2 + " = " + super.result);
			}
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
		super._writeBack();
	}

}
