package simulator.stages;

import simulator.core.ACASim;
import simulator.core.CPUMemory;
import simulator.instructions.Instruction;
import simulator.instructions.InstructionBundle;
import simulator.instructions.Opcode;
import simulator.instructions.UnknownInstruction;

public class DecodeStage implements IPipelineStage {

	private InstructionBundle old = null;
	private InstructionBundle curr = null;
	private InstructionBundle next = null;
	private InstructionBundle held = null;

	// [--OP--][--O1--][--O2--][--O3--] 4 x 8 bits operands
	// [------------------------------] 32 bit instruction

	@Override
	public void tick() {
		ACASim.dbgLog("DECODE");

		//if(!canAcceptInstruction()) {
		//	System.out.println("stalled");
		//	return;
		//}

		if(!ACASim.getInstance().branchPredictor.allowDecodeTransactions()) {
			ACASim.dbgLog("skip decode blocked");
			return;
		}

		if(next == null && held == null) { 
			ACASim.dbgLog("skip nothing to do " + (next == null) + " " + (held == null));
			return;
		} else {
			if(held != null && held.hasInstructions()) {
				ACASim.dbgLog("holding bundle");
				// need to finish curr first
				curr = held;
			} else {
				ACASim.dbgLog("next->curr");
				curr = next;
				next = null;
			}
		}

		// need to accept next from fetch and put it in curr
		// move next to curr only if not holdingBundle
		// if tick() is called and holdingBundle and allowDecodeTransactions then add to rb and release result

		//curr = next;
		//next = null;

		// curr contains a bundle of UnknownInstruction from fetch
		// unless this is an incomplete decode

		InstructionBundle res = new InstructionBundle();

		int branchCount = 0;

		while(curr.hasInstructions()) {
			Instruction enc = curr.fetchInstruction();

			ACASim.dbgLog("new instruction " + String.format("0x%08X", enc.getRawOpcode()));

			int opcRaw = (enc.getRawOpcode() & Opcode.MSK_OPC) >> 24;
			int op1Raw = (enc.getRawOpcode() & Opcode.MSK_OP1) >> 16;
			int op2Raw = (enc.getRawOpcode() & Opcode.MSK_OP2) >> 8;
			int op3Raw =  enc.getRawOpcode() & Opcode.MSK_OP3;

			Opcode opc = Opcode.fromHex(opcRaw);

			Instruction decoded = opc.instantiate();

			decoded.setOpcode(opc);
			decoded.setRawOpcode(enc.getRawOpcode());
			decoded.setAddress(enc.getAddress());
			decoded.setOperands(op1Raw, op2Raw, op3Raw);

			decoded.decode();

			// moved down
			//decoded.allocRegister();

			ACASim.dbgLog("Decoded: " + opc.toString() + " " + op1Raw + " " + op2Raw + " " + op3Raw + " " + decoded + " at addr " + decoded.getAddress());

			boolean bpAllowProcess = ACASim.getInstance().branchPredictor.canProcessBundle(decoded);
			if(decoded.getEU() == ExecutionUnit.BRANCH) branchCount++;

			/*
			int tagsAvailable = ACASim.getInstance().mem().tagsAvailable();
			if(tagsAvailable == 0) {
				ACASim.getInstance().mem().gcTags();
				tagsAvailable = ACASim.getInstance().mem().tagsAvailable();
			}
			|| tagsAvailable == 0
			 */

			if(!bpAllowProcess || branchCount > 1) {
				// the branch predictor prevents us from committing this bundle
				// this will set allowDecodeTransactions to false
				//commitToRB = false;

				// relying on held.hasInstructions()
				//holdingBundle = true;

				curr.returnInstruction(enc);

				// still commit whatever we can
				// need to tell FETCH not to fetch anything when it gets ticked immediately after
				break;
			}

			// moved to second loop
			//int shouldFlushBundle = ACASim.getInstance().branchPredictor.onInstructionDecoded(decoded);

			// onInstructionDecoded will save the scoreboard if marking as speculative
			//decoded.scoreboardDestReg();

			/*
			if(shouldFlushBundle == 1) {
				curr = res;
				old = curr;
				ACASim.getInstance().mem().PC--;
				// leave the branch in the queue so decode keeps ticking it
				ACASim.dbgLog("Abandoning bundle");
				return;
			}
			 */

			if(!ACASim.getInstance().branchPredictor.allowDecodeTransactions()) {
				// branch predictor has blocked further execution
				// return this instruction back to the bundle?
				ACASim.dbgLog("BP blocked decode"); 
			}

			res.pushInstruction(decoded);

			// moved to second loop
			//ACASim.getInstance().reorderBuffer.push(decoded);

			/*
				if(shouldFlushBundle == 2) {
					curr = res;
					old = curr;
					ACASim.dbgLog("Flushing bundle");
					return;
				}
			 */
		}

		if(curr.hasInstructions()) {
			// curr has leftover unknowninstructions
			// save it to held
			held = curr;

			for(Instruction instr : held.getQueue()) {
				ACASim.dbgLog("In held: " + instr.getAddress());
			}

		} else {
			// we've processed everything
			held = null;
		}

		// curr is what we're passing to the next stage
		// should never have unknowninstructions in it
		curr = res;

		InstructionBundle tmp = new InstructionBundle();
		for(Instruction instr : curr.getQueue()) {
			if(instr instanceof UnknownInstruction) {
				ACASim.dbgLog("err");
				throw new IllegalStateException();
			}

			ACASim.dbgLog("Commit to rb " + instr + " " + instr.getAddress());

			int shouldFlushBundle = ACASim.getInstance().branchPredictor.onInstructionDecoded(instr);

			boolean flush = false;
			if(shouldFlushBundle == 1) {
				// if we hit this then there was another branch in the bundle that we missed
				// this shouldn't happen
				throw new IllegalStateException();
			} else if(shouldFlushBundle == 2) {
				// need to drop everything else in curr
				ACASim.dbgLog("Flushing bundle");
				flush = true;
			}

			instr.allocRegister();

			tmp.pushInstruction(instr);
			ACASim.getInstance().reorderBuffer.push(instr);

			if(flush) {
				held = null;
				next = null;
				break;
			}
		}

		curr = tmp;

		//if(holdingBundle && next != null) {
		//	curr = next;
		//	next = null;
		//}

		//holdingBundle = false;

	}

	@Override
	public void acceptNextInstruction(Instruction instr) {
		throw new IllegalStateException("Attempted to pass single instruction to decode stage");
	}

	@Override
	public boolean canAcceptInstruction() {
		return curr == null && next == null && ACASim.getInstance().reorderBuffer.size() + CPUMemory.FETCH_WIDTH < CPUMemory.RB_SIZE;
	}

	@Override
	public boolean isResultAvailable() {
		return curr != null;
	}

	@Override
	public IStageTransaction getCurrentTransaction() {
		return old;
	}

	@Override
	public IStageTransaction getResult() {
		InstructionBundle res = curr;
		curr = null;
		return res;
	}

	@Override
	public void clearOldInstruction() {
		if(curr != null) return;

		old = null;
	}

	@Override
	public void acceptTransaction(IStageTransaction tr) {
		if(!(tr instanceof InstructionBundle)) {
			throw new IllegalStateException("Attempted to pass non-bundle to decode stage");
		}

		ACASim.dbgLog("set next");
		next = (InstructionBundle) tr;
	}

	public void flushBuffer() {
		curr = null;
		held = null;
		next = null;

		ACASim.dbgLog("Flush bundle");
	}

}
