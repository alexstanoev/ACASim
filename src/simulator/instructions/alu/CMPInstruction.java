package simulator.instructions.alu;

import simulator.core.ACASim;
import simulator.instructions.Instruction;
import simulator.instructions.Opcode;
import simulator.stages.ExecutionUnit;

public class CMPInstruction extends Instruction {

	public CMPInstruction() {
		super(Opcode.CMP.hex(), 1, ExecutionUnit.ALU);
	}

	// CMP R1 R2 R3 -> R3 = R1 == R2
	// Rdest is -1 if x < y, 0 if x == y, 1 if x > y
	@Override
	public void execute() {
		if(super.cyclesPassed()) {
			super.result = Integer.compare(super.regval1, super.regval2);
			
			ACASim.dbgLog(super.regval1 + " " + super.regval2 + " " + super.result);
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
