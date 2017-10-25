package simulator.core;

import java.util.LinkedList;

import javax.swing.SwingUtilities;

import simulator.gui.CPUView;
import simulator.stages.*;

public class ACASim {

	private static ACASim inst;

	private boolean useGUI = true;
	private CPUView guiInst = null;

	//private Clock clock;
	private Thread simThread = null;
	private CPUMemory state;
	public int clockTicks = 0;

	private volatile boolean run = false;
	private volatile boolean doStep = false;
	private volatile int clockSleepMs = 0;

	public LinkedList<IPipelineStage> pipeline = new LinkedList<IPipelineStage>();
	public Stage pipelineStage = Stage.FETCH;

	public static void main(String[] args) {
		inst = new ACASim();
		inst.setup();

		inst.guiSetup();

		inst.run();
	}

	public static ACASim getInstance() {
		return inst;
	}

	public CPUMemory mem() {
		return state;
	}

	public void setup() {
		state = new CPUMemory();

		instantiatePipeline();
		//clock = new Clock();

		if(useGUI && guiInst == null) {
			guiInst = new CPUView();
		}

		try {
			inst.loadProgram("/home/alex/dev/cwk/aca/test.hex");
		} catch(Exception e) {
			System.err.println("Failed loading program: " + e.getMessage());
			e.printStackTrace();
			return;
		}
	}

	public void guiSetup() {
		if(useGUI) {
			guiInst.setup();
			guiInst.setVisible(true);
		}
	}

	private void instantiatePipeline() {
		pipeline.clear();
		pipeline.add(new FetchStage());
		pipeline.add(new DecodeStage());
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
		setup();
		guiInst.update();
	}

	public void setSleepMs(int ms) {
		clockSleepMs = ms;
		//System.out.print("ms: " + ms);
	}
	// end

	private void step() {
		// TODO put pipeline back in place
		//IPipelineStage prev = null;
		//for(IPipelineStage elem : pipeline) {
		//if(!run) break;

		IPipelineStage elem = pipeline.get(pipelineStage.val());

		System.out.println("Tick: " + elem.toString() + " Stage: " + pipelineStage);

		if(pipelineStage != Stage.FETCH) {
			IPipelineStage prev = pipeline.get(pipelineStage.val() - 1);

			if(elem.canAcceptInstruction()) {
				if(prev.isResultAvailable()) {
					elem.acceptNextInstruction(prev.getResult());
					System.out.println("passing instruction");
				} else {
					//elem.acceptNextInstruction(new NOPInstruction());
					System.out.println("stalling");
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
						step();
					}

					System.out.println("Ticks: " + clockTicks);

					try {
						if(doStep) {
							run = false;
							synchronized (simThread) {
								simThread.wait();
							}

						} else {
							//synchronized (simThread) {
							//simThread.wait(clockSleepMs);
							//}
							if(run) {
								Thread.sleep(clockSleepMs);

								System.out.println("run wait");
							} else {
								synchronized (simThread) {
									simThread.wait();
								}
							}
						}
					} catch(InterruptedException e) {

					}
				}
			}
		});

		simThread.start();
	}

	public void halt() {
		run = false;
		System.err.println("Halting");
		printRegisters();
	}

	public void printRegisters() {
		for(int r : mem().REG) {
			System.out.println(String.format("0x%08X", r));
		}
	}

}
