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
		if(super.cyclesPassed() && !super.isSpeculative()) {
			super.cpu.mem().DMEM[super.regval2] = super.regval1;
			
			ACASim.dbgLog("Set DMEM[" + super.regval2 + "] to " + super.regval1);
			
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
