package simulator.core;

import java.util.LinkedList;

import simulator.stages.DecodeStage;
import simulator.stages.ExecuteStage;
import simulator.stages.FetchStage;
import simulator.stages.IPipelineStage;
import simulator.stages.WritebackStage;

public class ACASim {

	private static ACASim inst;
	
	//private Clock clock;
	private CPUMemory state;
	private int clockTicks = 0;
	private volatile boolean run = false;
	
	private LinkedList<IPipelineStage> pipeline = new LinkedList<IPipelineStage>();
	
	public static void main(String[] args) {
		inst = new ACASim();
		inst.setup();

		try {
			inst.loadProgram("/home/alex/dev/cwk/aca/test.hex");
		} catch(Exception e) {
			System.err.println("Failed loading program: " + e.getMessage());
			e.printStackTrace();
			return;
		}
		
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
	}
	
	private void instantiatePipeline() {
		pipeline.add(new FetchStage());
		pipeline.add(new DecodeStage());
		pipeline.add(new ExecuteStage());
		pipeline.add(new WritebackStage());
	}
	
	public void step() {
		IPipelineStage prev = null;
		for(IPipelineStage elem : pipeline) {
			if(!run) break;
			
			System.out.println("Tick: " + elem.toString());
			if(prev != null) {
				if(elem.canAcceptInstruction()) {
					elem.acceptNextInstruction(prev.getResult());
					System.out.println("pass instr");
				}
			}
			
			elem.tick();
			
			prev = elem;
			clockTicks++;
		}
	}
	
	public void loadProgram(String filename) throws Exception {
		IOUtils.readProgram(filename, mem().getIMemList());
	}
	
	public void run() {
		run = true;
		
		while(run) {
			step();
			
			System.out.println("Ticks: " + clockTicks);
			//System.in.read();
		}
		
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
