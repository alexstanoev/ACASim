package simulator.instructions.branch;

import simulator.instructions.Instruction;
import simulator.instructions.Opcode;
import simulator.stages.ExecutionUnit;

public class BGZInstruction extends Instruction {

	public BGZInstruction() {
		super(Opcode.BGZ.hex(), 1, ExecutionUnit.BRANCH);
	}

	// BGEZ R1 R2 -> PC = (R1 >= 0) ? OP2 : PC
	@Override
	public void execute() {
		if(super.cyclesPassed()) {
			if(super.regval1 > 0) {
				super.cpu.mem().PC = super.op2;
			}
		}
		
		super.result = 0; // TODO better way
	}

	@Override
	public void decode() {
		super.srcreg1 = super.op1;
	}

	@Override
	public void writeBack() {
		//super._writeBack();
	}

}
