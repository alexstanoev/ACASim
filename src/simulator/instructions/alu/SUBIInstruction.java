package simulator.instructions.alu;

import simulator.instructions.Instruction;
import simulator.instructions.Opcode;
import simulator.stages.ExecutionUnit;

public class SUBIInstruction extends Instruction {

	public SUBIInstruction() {
		super(Opcode.SUBI.hex(), 1, ExecutionUnit.ALU);
	}

	// ADDI R1 I1 R3 -> R3 = R1 + I1
	@Override
	public void execute() {
		if(super.cyclesPassed()) {
			super.result = super.regval1 - super.op2;
		}
	}

	@Override
	public void decode() {
		super.srcreg1 = super.op1;
		super.destreg = super.op3;
	}

	@Override
	public void writeBack() {
		super._writeBack();
	}

}
