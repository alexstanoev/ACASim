package simulator.instructions.alu;

import simulator.instructions.Instruction;
import simulator.instructions.Opcode;
import simulator.stages.ExecutionUnit;

public class SYSInstruction extends Instruction {

	public SYSInstruction() {
		super(Opcode.SYS.hex(), 1, ExecutionUnit.ALU);
	}

	// CMP R1 R2 R3 -> R3 = R1 == R2
	// Rdest is -1 if x < y, 0 if x == y, 1 if x > y
	@Override
	public void execute() {
		if(super.cyclesPassed()) {
			super.result = 0;
			
		}
	}

	@Override
	public void decode() {
		super.srcreg1 = super.op1;
		//super.srcreg2 = super.op2;
		//super.destreg = super.op3;
	}

	@Override
	public void writeBack() {
		//super._writeBack();
		System.out.println("SYS OUT " + getAddress() + ": " + super.regval1);
	}

}
