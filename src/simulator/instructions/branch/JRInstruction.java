package simulator.instructions.branch;

import simulator.instructions.Instruction;
import simulator.instructions.Opcode;
import simulator.stages.ExecutionUnit;

public class JRInstruction extends Instruction {

	public JRInstruction() {
		super(Opcode.JR.hex(), 1, ExecutionUnit.BRANCH);
	}

	// JR OP1 -> PC += REG[OP1]
	// branch delay slot: the instruction after jumps is always executed
	@Override
	public void execute() {
		if(super.cyclesPassed()) {
			System.out.println("PC " + super.cpu.mem().PC + " -> " + (super.cpu.mem().PC + super.regval1));
			super.cpu.mem().PC += super.regval1;
			
			// release dummy result (TODO better way)
			super.result = 0;
		}
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
