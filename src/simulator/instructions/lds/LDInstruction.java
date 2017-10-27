package simulator.instructions.lds;

import simulator.instructions.Instruction;
import simulator.instructions.Opcode;

public class LDInstruction extends Instruction {

	public LDInstruction() {
		super(Opcode.LD.hex(), 3);
	}
	
	// LD R1 R2-> DMEM[R2] = R1
	@Override
	public void execute() {
		if(super.cyclesPassed()) {
			int rdest = super.cpu.mem().REG[dest];
			super.cpu.mem().DMEM[rdest] = super.cpu.mem().REG[super.op1];
			
			System.out.println("Set DMEM[" + rdest + "] to " + super.cpu.mem().REG[super.op1]);
			
			super.result = 0;
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
