package simulator.instructions.lds;

import simulator.core.ACASim;
import simulator.instructions.Instruction;
import simulator.instructions.Opcode;
import simulator.stages.ExecutionUnit;

public class LDInstruction extends Instruction {

	public LDInstruction() {
		super(Opcode.LD.hex(), 3, ExecutionUnit.LDS);
	}
	
	// LD R1 R2-> DMEM[R2] = R1
	@Override
	public void execute() {
		if(super.cyclesPassed()) {
			int rdest = super.cpu.mem().REG[srcreg2];
			super.cpu.mem().DMEM[rdest] = super.cpu.mem().REG[super.srcreg1];
			
			ACASim.dbgLog("Set DMEM[" + rdest + "] to " + super.cpu.mem().REG[super.srcreg1]);
			
			super.result = 0;
		}
	}

	@Override
	public void decode() {
		super.srcreg1 = super.op1;
		super.srcreg2 = super.op2;
	}

	@Override
	public void writeBack() {
		//super._writeBack();
	}

}
