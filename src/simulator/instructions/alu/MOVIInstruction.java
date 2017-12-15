package simulator.instructions.alu;

import simulator.instructions.Instruction;
import simulator.instructions.Opcode;
import simulator.stages.ExecutionUnit;

public class MOVIInstruction extends Instruction {

	public MOVIInstruction() {
		super(Opcode.MOVI.hex(), 1, ExecutionUnit.ALU);
	}

	// MOVI H L R1 -> R1 = HL
	@Override
	public void execute() {
		if(super.cyclesPassed()) {
			super.result = (super.op1 << 8) + super.op2;
		}
	}

	@Override
	public void decode() {
		//super.srcreg1 = super.op1;
		super.destreg = super.op3;
	}

	@Override
	public void writeBack() {
		super._writeBack();
	}

}
