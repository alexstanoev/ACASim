package simulator.instructions.alu;

import simulator.instructions.Instruction;
import simulator.instructions.Opcode;

public class SHLInstruction extends Instruction {

	public SHLInstruction() {
		super(Opcode.SHL.hex(), 1);
	}

	// SHL R1 R2 R3 -> R3 = R1 << R2
	@Override
	public void execute() {
		if(super.cyclesPassed()) {
			super.result = super.cpu.mem().REG[super.op1] << super.cpu.mem().REG[super.op2];
		}
	}

	@Override
	public void decode() {
		super.dest = super.op3;
	}

	@Override
	public void writeBack() {
		super._writeBack();
	}

}
