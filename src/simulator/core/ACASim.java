package simulator.core;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;

import javax.swing.SwingUtilities;

import simulator.gui.CPUView;
import simulator.instructions.Instruction;
import simulator.stages.*;

public class ACASim {

	private static ACASim inst;

	private static boolean debug = false;
	private static boolean useGUI = true;

	private static String filename = "prog/test.hex";
	private CPUView guiInst = null;

	private boolean pipelined = true;

	private Thread simThread = null;
	private CPUMemory state;
	public int clockTicks = 0;
	public int instructionsRetired = 0;

	private volatile boolean run = false;
	private volatile boolean doStep = false;
	private volatile int clockSleepMs = 0;

	public LinkedList<IPipelineStage> pipeline = new LinkedList<IPipelineStage>();
	public Stage pipelineStage = Stage.FETCH;

	public HashMap<ExecutionUnit, ArrayList<ExecutionUnitStage>> executionUnits = new HashMap<ExecutionUnit, ArrayList<ExecutionUnitStage>>();
	public ArrayDeque<Instruction> reorderBuffer = new ArrayDeque<Instruction>();
	
	public BranchPredictor branchPredictor;

	public static void main(String[] args) {
		if(args.length == 0) {
			System.out.println("Usage: sim [filename] [gui|nogui]");
			return;
		}

		if(args.length > 1) {
			filename = args[0];

			if(args[1].equals("gui")) {
				useGUI = true;
			} else {
				useGUI = false;
			}
		}

		inst = new ACASim();
		inst.setup();

		inst.guiSetup();

		inst.run();

		if(!useGUI) {
			inst.runContinuously();
		}
	}

	public static ACASim getInstance() {
		return inst;
	}

	public CPUMemory mem() {
		return state;
	}

	public void setup() {
		state = new CPUMemory();
		branchPredictor = new BranchPredictor();
		
		instantiatePipeline();
		instantiateExecutionUnits();

		if(useGUI && guiInst == null) {
			guiInst = new CPUView();
		}

		try {
			inst.loadProgram(filename);
			System.out.println("Loaded " + filename);
		} catch(Exception e) {
			System.err.println("Failed loading program: " + e.getMessage());
			System.exit(0);
		}
	}

	public void guiSetup() {
		if(useGUI) {
			guiInst.setup();
			guiInst.setVisible(true);
		}
	}

	private void instantiateExecutionUnits() {
		reorderBuffer.clear();
		executionUnits.clear();
		for(ExecutionUnit type : ExecutionUnit.values()) {
			if(type.num() > 0) {
				executionUnits.put(type, new ArrayList<ExecutionUnitStage>());
				for(int i = 0; i < type.num(); i++) {
					executionUnits.get(type).add(new ExecutionUnitStage(type));
				}
			}
		}
	}

	private void instantiatePipeline() {
		pipeline.clear();
		pipeline.add(new FetchStage());
		pipeline.add(new DecodeStage());
		pipeline.add(new ReservationStage());
		pipeline.add(new ExecuteStage());
		pipeline.add(new WritebackStage());
	}

	// GUI API start	
	public void runContinuously() {
		doStep = false;
		run = true;
		synchronized (simThread) {
			simThread.notify();
		}
	}

	public void pause() {
		doStep = false;
		run = false;
	}

	public void singleStep() {
		doStep = true;
		run = true;
		synchronized (simThread) {
			simThread.notify();
		}
	}

	public void reset() {
		clockTicks = 0;
		instructionsRetired = 0;
		setup();
		guiInst.update();
	}

	public void setSleepMs(int ms) {
		clockSleepMs = ms;
	}
	// end

	private void stepPipeline() {

		for(IPipelineStage elem : pipeline) {
			// the old instruction is only used for the GUI
			// this makes the GUI pipeline look like it's advancing
			elem.clearOldInstruction();
		}

		IPipelineStage next = null;
		for(int i = pipeline.size() - 1; i >= 0; i--) {
			IPipelineStage elem = pipeline.get(i);

			if(!run) break;

			ACASim.dbgLog("Tick: " + elem.toString());

			elem.tick();

			if(next != null) {

				if(next.canAcceptInstruction()) {
					if(elem.isResultAvailable()) {
						next.acceptTransaction(elem.getResult());
						//System.out.println("passing instruction");
					} else {
						//elem.acceptNextInstruction(new NOPInstruction());
						//System.out.println("stalling");
					}
				}


			}

			next = elem;

		}

		clockTicks++;

		if(useGUI) {
			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					guiInst.update();
				}
			});
		}


	}

	private void stepStage() {
		//IPipelineStage prev = null;
		//for(IPipelineStage elem : pipeline) {
		//if(!run) break;

		IPipelineStage elem = pipeline.get(pipelineStage.val());

		ACASim.dbgLog("Tick: " + elem.toString() + " Stage: " + pipelineStage);

		if(pipelineStage != Stage.FETCH) {
			IPipelineStage prev = pipeline.get(pipelineStage.val() - 1);

			if(elem.canAcceptInstruction()) {
				if(prev.isResultAvailable()) {
					elem.acceptTransaction(prev.getResult());
					//System.out.println("passing instruction");
				} else {
					//elem.acceptNextInstruction(new NOPInstruction());
					//System.out.println("stalling");
				}
			}
		}

		elem.tick();

		clockTicks++;

		if(useGUI) {
			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					guiInst.update();
				}
			});
		}

		// the old instruction is only used for the GUI
		// this makes the GUI pipeline look like it's advancing
		if(pipelineStage != Stage.FETCH) {
			pipeline.get(pipelineStage.val() - 1).clearOldInstruction();
		} else {
			pipeline.get(Stage.WRITEBACK.val()).clearOldInstruction();
		}

		if(pipelineStage == Stage.WRITEBACK) {
			pipelineStage = Stage.FETCH;
		} else {
			pipelineStage = Stage.fromVal(pipelineStage.val() + 1);
		}

	}

	public void loadProgram(String filename) throws Exception {
		IOUtils.readProgram(filename, mem().getIMemList());
	}

	public void run() {
		simThread = new Thread(new Runnable() {

			@Override
			public void run() {
				while(true) {
					if(run) {
						if(pipelined) {
							stepPipeline();
						} else {
							stepStage();
						}
					}

					ACASim.dbgLog("Clock cycles: " + clockTicks);

					try {
						if(doStep) {
							run = false;
							synchronized (simThread) {
								simThread.wait();
							}
						} else {
							if(run) {
								if(clockSleepMs == 0) {
									if(useGUI) {
										Thread.sleep(5); // yield for GUI
									}
								} else {
									Thread.sleep(clockSleepMs);
								}
								//System.out.println("run wait");
							} else {
								synchronized (simThread) {
									simThread.wait();
								}
							}
						}
					} catch(InterruptedException e) {}
				}
			}
		});

		simThread.start();
	}

	public void halt() {
		run = false;
		System.out.println("Halting at " + clockTicks + " clock cycles.");

		printRegisters();
        // TODO print IPC (retired instructions), instr. executed, clock cycles

		if(!useGUI) {
			System.exit(0);
		}
	}

	public void printRegisters() {
		System.out.println("Register dump:");
		for(int r : mem().REG) {
			System.out.println(String.format("0x%08X", r));
		}
	}

	public static void dbgLog(String str) {
		if(debug) {
			System.out.println(new Exception().getStackTrace()[1].getClassName() + ": " + str);
		}
	}

}
