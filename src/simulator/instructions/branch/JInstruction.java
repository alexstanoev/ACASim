package simulator.instructions.branch;

import simulator.instructions.Instruction;
import simulator.instructions.Opcode;

public class JInstruction extends Instruction {

	public JInstruction() {
		super(Opcode.J.hex(), 1);
	}

	// ADD R1 R2 R3 -> R3 = R1 + R2
	// TODO extend opcode length
	@Override
	public void execute() {
		if(super.cyclesPassed()) {
			System.out.println("PC " + super.cpu.mem().PC + " -> " + super.dest);
			super.cpu.mem().PC = super.dest;
			
			// release dummy result (TODO better way)
			super.result = 0;
		}
	}

	@Override
	public void decode() {
		super.dest = super.op1;
	}

	@Override
	public void writeBack() {
		//super._writeBack();
	}

}
