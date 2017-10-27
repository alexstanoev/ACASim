package simulator.core;

import java.io.File;
import java.util.ArrayList;
import java.util.Scanner;

public class IOUtils {

	public static void readProgram(String filename, ArrayList<Integer> imem) throws Exception {
		Scanner s = new Scanner(new File(filename));
		while (s.hasNext()) {
		    int num = s.nextInt(16);
		    //System.out.println(num);
		    imem.add(num);
		}
		s.close();
	}
	
}
