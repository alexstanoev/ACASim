package simulator.core;

import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Scanner;

public class IOUtils {

	public static void readProgram(String filename, ArrayList<Integer> imem) throws Exception {
		Scanner s = new Scanner(new File(filename));
		while (s.hasNext()) {
			int num = (int) s.nextLong(16);
			//System.out.println(num);
			imem.add(num);
		}
		s.close();
	}

	public static void readData(String filename, int[] dmem) throws Exception {
		int loc = 0;
		Scanner s = new Scanner(new File(filename));
		while (s.hasNext()) {
			int num = (int) s.nextLong(16);
			dmem[loc++] = num;
		}
		s.close();
	}
	
	public static void readFile(String filename, ArrayList<String> dest) throws Exception {
		Scanner s = new Scanner(new File(filename));
		while (s.hasNext()) {
			dest.add(s.nextLine());
		}
		s.close();
	}

	public static void writeToFile(String filename, ArrayList<String> lines) throws Exception {
		FileWriter writer = new FileWriter(filename); 
		for(String str : lines) {
			writer.write(str + "\n");
		}
		writer.close();
	}

}
