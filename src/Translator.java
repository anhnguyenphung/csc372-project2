import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Scanner;

public class Translator {
	public static void main(String args[]) throws IOException {
		
		// read in the program as a txt file in our language and store to stringFile
		File file = new File(args[0]);
		Scanner sc = new Scanner(file);
		String stringFile = "";
		while (sc.hasNextLine()) {
			stringFile += sc.nextLine();
		}
		String[] statements = stringFile.split(";");
		
		// create a new Java file to store the program in our language in txt file
		String outFileName = args[0].replace(".txt", ".java");
		File outFile = new File(outFileName);
		outFile.createNewFile();
		PrintWriter pw = new PrintWriter(outFileName);
		pw.close();
		
		FileWriter fw = new FileWriter(outFileName, true);
	    BufferedWriter bw = new BufferedWriter(fw);
	    String outContent = "public class ";
	    outContent += args[0].replace(".txt", "");
	    outContent += "{public static void main(String args[]) {";
	    
	    outContent += "}}";
	    bw.write(outContent);
	    bw.close();
	    
		
	}
}
