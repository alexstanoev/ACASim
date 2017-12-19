package simulator.instructions.lds;

import simulator.core.ACASim;
import simulator.instructions.Instruction;
import simulator.instructions.Opcode;
import simulator.stages.ExecutionUnit;

public class STOInstruction extends Instruction {

	public STOInstruction() {
		super(Opcode.STO.hex(), 3, ExecutionUnit.LDS);
	}

	// STO R1 R2 I1 -> DMEM[R2 + I1] = R1
	@Override
	public void execute() {
		if(super.cyclesPassed() /*&& !super.isSpeculative()*/) {
			ACASim.dbgLog("STO " + (super.regval2 + super.op3) + "  " + super.regval1 + " " + super.srcreg1 + " " + super.srcreg2);
			
			//super.cpu.mem().DMEM[super.regval2 + super.op3] = super.regval1;

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
		super.cpu.mem().DMEM[super.regval2 + super.op3] = super.regval1;
		
		//super._writeBack();
	}

}
