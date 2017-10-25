package simulator.instructions.branch;

import simulator.instructions.Instruction;
import simulator.instructions.Opcode;

public class BLTZInstruction extends Instruction {

	public BLTZInstruction() {
		super(Opcode.BLTZ.hex(), 1);
	}

	// ADD R1 R2 R3 -> R3 = R1 + R2
	@Override
	public void execute() {
		if(super.cyclesPassed()) {
			if(super.cpu.mem().REG[super.op1] <= 0) {
				super.cpu.mem().PC = super.dest;
			}
		}
	}

	@Override
	public void decode() {
		super.dest = super.op2;
	}

	@Override
	public void writeBack() {
		//super._writeBack();
	}

}
