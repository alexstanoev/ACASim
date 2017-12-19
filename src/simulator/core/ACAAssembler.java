package simulator.core;

import java.util.ArrayList;
import java.util.HashMap;

import simulator.instructions.Opcode;

public class ACAAssembler {

	private static HashMap<String, String> registerAliases = new HashMap<String, String>();

	static {
		// R0 - R7, no alt names
		
		registerAliases.put("RV0", "R8"); // result value 1
		registerAliases.put("RV1", "R9"); // result value 2
		registerAliases.put("RF0", "R10"); // fn arg 1
		registerAliases.put("RF1", "R11"); // fn arg 2
		registerAliases.put("RS0", "R12"); // saved value 1
		registerAliases.put("RS1", "R13"); // saved value 2
		
		registerAliases.put("RA", "R14"); // return address
		registerAliases.put("RSP", "R15"); // stack pointer
		
		registerAliases.put("RZ", "R99"); // always zero
	}

	public static void main(String[] args) {
		if(args.length != 2) {
			System.err.println("Usage: assemble [src] [dest]");
			return;
		}

		String fileIn = args[0];
		String fileOut = args[1];
		ArrayList<String> in = new ArrayList<String>();

		try {
			IOUtils.readFile(fileIn, in);
		} catch(Exception e) {
			e.printStackTrace();
			return;
		}

		// first pass: labels
		HashMap<String, Integer> labels = new HashMap<String, Integer>();

		int addr = 0;
		for(String line : in) {
			line = line.trim();
			if(line.length() == 0 || line.startsWith(";")) continue;

			if(line.endsWith(":")) {
				labels.put(line.substring(0, line.length() - 1), addr);
				continue;
			}

			addr++;
		}

		// second pass: opcodes
		ArrayList<String> program = new ArrayList<String>();

		addr = -1;
		for(String line : in) {
			line = line.trim();
			if(line.length() == 0 || line.startsWith(";")) continue; // comment

			addr++;

			if(line.endsWith(":")) {
				// label, don't care
				continue;
			}

			Opcode opc = null;
			int op1 = 0, op2 = 0, op3 = 0;

			String[] parts = line.split(" ");
			if(parts.length == 0) {
				// no arguments
				try {
					opc = Opcode.valueOf(line);
				} catch(Exception e) {
					System.err.println("Can't evaluate zero arg opcode at " + line);
					e.printStackTrace();
					return;
				}
			} else {
				try {
					opc = Opcode.valueOf(parts[0]);
				} catch(Exception e) {
					System.err.println("Can't evaluate opcode at " + line);
					e.printStackTrace();
					return;
				}

				if(parts.length > 1) {
					op1 = parseOperand(parts[1], labels);
				}

				if(parts.length > 2) {
					op2 = parseOperand(parts[2], labels);
				}

				if(parts.length > 3) {
					op3 = parseOperand(parts[3], labels);
				}

				String assembled = String.format("%02X", opc.hex()) + String.format("%02X", op1) + String.format("%02X", op2) + String.format("%02X", op3);
				program.add(assembled);

				System.out.println(assembled);
			}
		}
		try {
			IOUtils.writeToFile(fileOut, program);
		} catch(Exception e) {
			e.printStackTrace();
		}

		System.out.println("Program assembled; " + program.size() + " instructions generated");

	}

	private static int parseOperand(String src, HashMap<String, Integer> labels) {
		int result;
		if(src.startsWith("%")) {
			if(labels.containsKey(src.substring(1))) {
				result = labels.get(src.substring(1));
			} else {
				System.err.println("Nonexistent label: " + src.substring(1));
				throw new IllegalArgumentException();
			}
		} else if(src.startsWith("R")) {
			if(registerAliases.containsKey(src)) {
				src = registerAliases.get(src);
			}

			src = src.substring(1);
			result = Integer.parseInt(src);
		} else if(src.startsWith("0x")) {
			src = src.substring(2);
			result = Integer.parseInt(src, 16);
		} else if(src.endsWith("f")) {
			src = src.substring(0, src.length() - 1);
			result = Float.floatToIntBits(Float.parseFloat(src));
			// TODO generate MOV, SHL, MOV
		} else {
			result = Integer.parseInt(src);
		}

		return result;
	}

}
