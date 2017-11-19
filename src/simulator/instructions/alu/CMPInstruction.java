package simulator.instructions.alu;

import simulator.instructions.Instruction;
import simulator.instructions.Opcode;
import simulator.stages.ExecutionUnit;

public class CMPInstruction extends Instruction {

	public CMPInstruction() {
		super(Opcode.CMP.hex(), 1, ExecutionUnit.ALU);
	}

	// CMP R1 R2 R3 -> R3 = R1 == R2
	@Override
	public void execute() {
		if(super.cyclesPassed()) {
			super.result = (super.cpu.mem().REG[super.op1] == super.cpu.mem().REG[super.op2]) ? 1 : 0;
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
