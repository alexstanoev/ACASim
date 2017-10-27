package simulator.core;

import java.util.ArrayList;
import java.util.HashMap;

import simulator.instructions.Opcode;

public class ACAAssembler {

	public static void main(String[] args) {
		if(args.length != 2) {
			System.err.println("Usage: assemble [src] [dest]");
		}

		String fileIn = args[0];
		String fileOut = args[1];
		ArrayList<String> in = new ArrayList<String>();

		try {
			IOUtils.readFile(fileIn, in);
		} catch(Exception e) {
			e.printStackTrace();
		}

		// first pass: labels
		HashMap<String, Integer> labels = new HashMap<String, Integer>();

		int addr = 0;
		for(String line : in) {
			if(line.startsWith(";")) continue; // comment

			if(line.endsWith(":")) {
				labels.put(line.substring(0, line.length() - 1), addr);
			}

			addr++;
		}

		// second pass: opcodes
		ArrayList<String> program = new ArrayList<String>();

		addr = -1;
		for(String line : in) {
			if(line.startsWith(";")) continue; // comment

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
					if(parts[1].startsWith("%")) {
						if(labels.containsKey(parts[1].substring(1))) {
							op1 = labels.get(parts[1].substring(1));
						}
					} else if(parts[1].startsWith("R")) {
						parts[1] = parts[1].substring(1);
						op1 = Integer.parseInt(parts[1]);
					} else {
						op1 = Integer.parseInt(parts[1], 16);
					}					
				}

				if(parts.length > 2) {
					if(parts[2].startsWith("R")) {
						parts[2] = parts[2].substring(1);
						op2 = Integer.parseInt(parts[2]);
					} else {
						op2 = Integer.parseInt(parts[2], 16);
					}
				}

				if(parts.length > 3) {
					if(parts[3].startsWith("R")) {
						parts[3] = parts[3].substring(1);
						op3 = Integer.parseInt(parts[3]);
					} else {
						op3 = Integer.parseInt(parts[3], 16);
					}
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

}
