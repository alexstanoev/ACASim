package simulator.instructions.branch;

import simulator.instructions.Instruction;
import simulator.instructions.Opcode;

public class JIInstruction extends Instruction {

	public JIInstruction() {
		super(Opcode.JI.hex(), 1);
	}

	// JI I1 -> PC = I1
	// branch delay slot: the instruction after jumps is always executed
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
