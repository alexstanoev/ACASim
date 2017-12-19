package simulator.instructions.branch;

import simulator.instructions.Instruction;
import simulator.instructions.Opcode;
import simulator.stages.ExecutionUnit;

public class JALInstruction extends Instruction {

	public JALInstruction() {
		super(Opcode.JAL.hex(), 1, ExecutionUnit.BRANCH);
	}

	// JAL OP1 -> PC = OP1
	// RA = PC
	@Override
	public void execute() {
		if(super.cyclesPassed()) {
			System.out.println("PC " + super.cpu.mem().PC + " -> " + (super.op2));
			super.cpu.mem().PC = super.op2;
			
			// next instruction
			super.result = getAddress() + 1;
		}
	}

	@Override
	public void decode() {
		//super.srcreg1 = super.op1;
		super.destreg = super.op1; // R14 - RA
	}

	@Override
	public void writeBack() {
		super._writeBack();
	}

}
