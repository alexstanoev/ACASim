package simulator.instructions.alu;

import simulator.instructions.Instruction;
import simulator.instructions.Opcode;
import simulator.stages.ExecutionUnit;

public class SPCInstruction extends Instruction {

	public SPCInstruction() {
		super(Opcode.SPC.hex(), 1, ExecutionUnit.ALU);
	}

	// SUB R1 R2 R3 -> R3 = R1 - R2
	@Override
	public void execute() {
		if(super.cyclesPassed()) {
			super.result = getAddress() + super.op1;
		}
	}

	@Override
	public void decode() {
		super.destreg = super.op2;
	}

	@Override
	public void writeBack() {
		super._writeBack();
	}

}
