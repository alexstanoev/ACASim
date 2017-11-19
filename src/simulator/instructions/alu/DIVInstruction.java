package simulator.instructions.alu;

import simulator.instructions.Instruction;
import simulator.instructions.Opcode;
import simulator.stages.ExecutionUnit;

public class DIVInstruction extends Instruction {

	public DIVInstruction() {
		super(Opcode.DIV.hex(), 5, ExecutionUnit.ALU);
	}

	// DIV R1 R2 R3 -> R3 = R1 / R2
	@Override
	public void execute() {
		if(super.cyclesPassed()) {
			super.result = super.cpu.mem().REG[super.op1] / super.cpu.mem().REG[super.op2];
		}
	}

	@Override
	public void decode() {
		super.destreg = super.op3;
	}

	@Override
	public void writeBack() {
		super._writeBack();
	}

}
