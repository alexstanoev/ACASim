package simulator.instructions.branch;

import simulator.instructions.Instruction;
import simulator.instructions.Opcode;
import simulator.stages.ExecutionUnit;

public class BGEZInstruction extends Instruction {

	// TODO rename to BGZ
	public BGEZInstruction() {
		super(Opcode.BGEZ.hex(), 1, ExecutionUnit.BRANCH);
	}

	// BGEZ R1 R2 -> PC = (R1 >= 0) ? R2 : PC
	@Override
	public void execute() {
		if(super.cyclesPassed()) {
			if(super.cpu.mem().REG[super.srcreg1] > 0) {
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
