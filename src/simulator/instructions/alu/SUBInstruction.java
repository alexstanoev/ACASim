package simulator.instructions.alu;

import simulator.instructions.Instruction;
import simulator.instructions.Opcode;
import simulator.stages.ExecutionUnit;

public class SUBInstruction extends Instruction {

	public SUBInstruction() {
		super(Opcode.SUB.hex(), 1, ExecutionUnit.ALU);
	}

	// SUB R1 R2 R3 -> R3 = R1 - R2
	@Override
	public void execute() {
		if(super.cyclesPassed()) {
			super.result = super.regval1 - super.regval2;
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
