package simulator.instructions.fpu;

import simulator.instructions.Instruction;
import simulator.instructions.Opcode;
import simulator.stages.ExecutionUnit;

public class FMULInstruction extends Instruction {

	public FMULInstruction() {
		super(Opcode.FMUL.hex(), 1, ExecutionUnit.FPU);
	}

	// ADD R1 R2 R3 -> R3 = R1 + R2
	@Override
	public void execute() {
		if(super.cyclesPassed()) {
			float rv1 = Float.intBitsToFloat(super.regval1);
			float rv2 = Float.intBitsToFloat(super.regval2);
			float res = rv1 * rv2;
			
			super.result = Float.floatToIntBits(res);
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
