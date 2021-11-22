/* 
 * AUTHOR: Anh Nguyen Phung, Trong Nguyen
 * FILE: Translator.java
 * ASSIGNMENT: Project 2
 * COURSE: CSC 372, Fall 2021
 * PURPOSE: Parse our language and produce a compilable/runnable program in Java
 * USAGE:
 * 
 * javac Translator.java
 * java Translator Program.txt
 * javac Program.java
 * java Program arg0 arg1 arg2 ... 
 * 
 * where arg0, arg1, arg2, ... are command line arguments
 */

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Scanner;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class Translator {
	private static Set<String> setIntVar = new HashSet<>(); // keep track of integer variables that are declared
	private static Set<String> setBoolVar = new HashSet<>(); // keep track of boolean variables that are declared
	
	public static void main(String args[]) throws IOException {
		
		// read in the program as a txt file in our language and store to stringFile
		File file = new File(args[0]);
		Scanner sc = new Scanner(file);
		List<String> statements = new ArrayList<>();
		while (sc.hasNextLine()) {
			statements.add(sc.nextLine());
		}
		sc.close();
		
	    String outContent = "public class ";
	    outContent += args[0].replace(".txt", "");
	    outContent += "{\npublic static void main(String args[]) {\n";
	    int lineNum = 1;
	    for (String statement: statements) {
	    	
	    	// handle comment
	    	Pattern comment = Pattern.compile("(.*)#(.*)");
	    	Matcher m = comment.matcher(statement);
	    	Pattern quote = Pattern.compile("\"");
	    	Matcher mm = quote.matcher(statement);
	    	while (m.find() && !mm.find()) {
	    		statement = m.group(1);
	    		m = comment.matcher(statement);
	    		mm = quote.matcher(statement);
	    	}
	    	
	    	statement = statement.strip();
	    	
	    	// handle invalid symbol if there is no strings in the line of code
	    	Pattern str = Pattern.compile("(.*)\"(.*)\"(.*)");
	    	Matcher str_m = str.matcher(statement);
	    	if (!str_m.find()) {
	    		handleInvalidSymbol(statement, lineNum);
	    	}
	    	
	    	// use regex to match keyword in the language
	    	Pattern command_line = Pattern.compile("use (\\d+) cmd args");
	    	Pattern int_asgmt = Pattern.compile("integer (.+) assign (.+)");
	    	Pattern bool_asgmt = Pattern.compile("boolean (.+) assign (.+)");
	    	Pattern print_out = Pattern.compile("printout (.+)");
	    	Pattern if_stmt = Pattern.compile("if (.+)");
	    	Pattern loop = Pattern.compile("during (.+)");
	    	Pattern asgmt = Pattern.compile("(.+) assign (.+)");
	    	Pattern print = Pattern.compile("print (.+)");
	    	
	    	
	    	Matcher m0 = command_line.matcher(statement);
	    	Matcher m1 = int_asgmt.matcher(statement);
	    	Matcher m2 = bool_asgmt.matcher(statement);
	    	Matcher m3 = print_out.matcher(statement);
	    	Matcher m4 = if_stmt.matcher(statement);
	    	Matcher m5 = loop.matcher(statement);
	    	Matcher m6 = asgmt.matcher(statement);
	    	Matcher m7 = print.matcher(statement);
	    	
	    	// process the line of code
	    	if (m0.find() && statement.length() == m0.group(1).length() + 13) {
	    		int numArgs = Integer.valueOf(m0.group(1));
	    		for (int i = 0; i < numArgs; i++) {
	    			outContent += "int arg" + String.valueOf(i) + " = Integer.valueOf(args[" + 
	    						String.valueOf(i) + "]);\n";
	    			setIntVar.add("arg" + String.valueOf(i));
	    		}
	    	} else if (m1.find() && statement.length() == m1.group(1).length() + m1.group(2).length() + 16) {
	    		outContent += "int " + process_var_asgmt(m1, true, false, false, lineNum);
	    	} else if (m2.find() && statement.length() == m2.group(1).length() + m2.group(2).length() + 16) {
	    		outContent += "boolean " + process_var_asgmt(m2, false, true, false, lineNum);
	    	} else if (m3.find() && statement.length() == m3.group(1).length() + 9) {
	    		outContent += "System.out.println(" + 
	    					process_expr(m3.group(1) , true, false, lineNum) + ");\n";
	    	} else if (m4.find() && statement.length() == m4.group(1).length() + 3) {
	    		outContent += "if (" + process_expr(m4.group(1), false, true, lineNum) + ") {\n";
	    	} else if (m5.find() && statement.length() == m5.group(1).length() + 7) {
	    		outContent += "while (" + process_expr(m5.group(1), false, true, lineNum) + ") {\n";
	    	} else if (m6.find() && statement.length() == m6.group(1).length() + m6.group(2).length() + 8) {
	    		outContent += process_var_asgmt(m6, false, false, true, lineNum);
	    	} else if (m7.find() && statement.length() == m7.group(1).length() + 6) {
	    		outContent += "System.out.print(" + 
    					process_expr(m7.group(1) , true, false, lineNum) + ");\n"; 
	    	} else if (statement.equals("else")) {
	    		outContent += "} else {\n";
	    	} else if (statement.equals("end")) {
	    		outContent += "}\n";
	    	} else if (statement.equals("break")) {
	    		outContent += "break;\n";
	    	} else if (statement.equals("")) {
	    	} else {
	    		System.out.print("Error on line " + String.valueOf(lineNum) + " : ");
				System.out.println("Unrecognized line. This line is not valid in the language.");
				System.exit(1);
	    	}
	    	lineNum ++;
	    }
	    outContent += "\n}\n}";
	    // create a new Java file to store the program in our language in txt file
	 	String outFileName = args[0].replace(".txt", ".java");
	 	File outFile = new File(outFileName);
	 	outFile.createNewFile();
	 	PrintWriter pw = new PrintWriter(outFileName);
	 	pw.close();
	 	
	    FileWriter fw = new FileWriter(outFileName, true);
	    BufferedWriter bw = new BufferedWriter(fw);
	    bw.write(outContent);
	    bw.close();
	
	}


	/**
     * Purpose: Handle invalid symbol if there is no strings in the line of code.
     * 
     * @param statement, is the string to process
     * @param lineNum, is an int representing the current line of code
     * 
     * @return void
     * 
     */
	private static void handleInvalidSymbol(String statement, int lineNum) {
		Pattern symbol = Pattern.compile("(.*)([\\W&&[^# \"\t\n]])(.*)");
		Matcher m = symbol.matcher(statement);
		if (m.find()) {
			System.out.print("Error on line " + String.valueOf(lineNum) + " : ");
			System.out.println("Unrecognized symbol \"" + m.group(2) + "\" in a non-string. ");
			System.out.println("Allowed symbols in non-strings and non-comments are '\"' (for string), "
					+ "'#' (for comment), space, tab, and new line (for usual code writing).");
			System.exit(1);
		}
	}

	/**
     * Purpose: Process an assignment statement in our language and return an updated
     * assignment statement in Java with parsing error checking.
     * 
     * @param m, is a Matcher object for pattern matching
     * @param intAsgmt, is a boolean representing if the statement is an integer assignment
     * @param boolAsgmt, is a boolean representing if the statement is a boolean assignment
     * @param reassign, is a boolean representing if the statement is a reassignment
     * @param lineNum, is an int representing the current line of code
     * 
     * @return A string representing the updated assignment statement in Java 
     * 
     */
	private static String process_var_asgmt(Matcher m, boolean intAsgmt, boolean boolAsgmt, 
			boolean reassign, int lineNum) {
		String var = m.group(1).strip();
		String statement = m.group(2).strip();
		String result = "";
		Pattern nonAlpha = Pattern.compile("[^a-zA-Z]+");
		Matcher m0 = nonAlpha.matcher(var);
		// parsing error checking
		if (m0.find()) {
			System.out.print("Error on line " + String.valueOf(lineNum) + " : ");
			System.out.println("Variable \"" + var + "\" (non-command line argument variable) "
					+ "contains non-alpha character.");
			System.exit(1);
		}
		if (!reassign && (setIntVar.contains(var) || setBoolVar.contains(var))) {
			System.out.print("Error on line " + String.valueOf(lineNum) + " : ");
			System.out.println("Variable \"" + var + "\" is already declared.");
			System.exit(1);
		}
		if (reassign && (!setIntVar.contains(var) && !setBoolVar.contains(var))) {
			System.out.print("Error on line " + String.valueOf(lineNum) + " : ");
			System.out.println("Variable \"" + var + "\" need to be declared first.");
			System.exit(1);
		}
		// based on the flag, process the string and check parsing error
		if (intAsgmt) {
			if (!check_int_expr(statement)) {
				System.out.print("Error on line " + String.valueOf(lineNum) + " : ");
				System.out.println("Invalid int expression for the assignment of variable \"" + var + "\".");
				System.exit(1);
			}
			setIntVar.add(var);
			result += var + " = " + process_int_expr(statement) + ";";
		} else if (boolAsgmt) {
			if (!check_bool_expr(statement)) {
				System.out.print("Error on line " + String.valueOf(lineNum) + " : ");
				System.out.println("Invalid boolean expression for the assignment of variable \"" + var + "\".");
				System.exit(1);
			}
			setBoolVar.add(var);
			result += var + " = " + process_bool_expr(process_int_expr(statement)) + ";";
		} else if (reassign) {
			if (setIntVar.contains(var)) {
				if (!check_int_expr(statement)) {
					System.out.print("Error on line " + String.valueOf(lineNum) + " : ");
					System.out.println("Reassign variable \"" + var + "\" need to an int expression.");
					System.exit(1);
				}
				result += var + " = " + process_int_expr(statement) + ";";
			} else if (setBoolVar.contains(var)) {
				if (!check_bool_expr(statement)) {
					System.out.print("Error on line " + String.valueOf(lineNum) + " : ");
					System.out.println("Reassign variable \"" + var + "\" need to be a boolean expression.");
					System.exit(1);
				} 
				result += var + " = " + process_bool_expr(process_int_expr(statement)) + ";\n";
			}
		}
		return result;
	}

	/**
     * Purpose: Process an expression in our language and return an updated expression in Java
     * with parsing error checking.
     * 
     * @param statement, is the string to process
     * @param isPrint, is a boolean representing if the expression is for printing out
     * @param isMustBeBool, is a boolean representing if the statement must be a boolean expression
     * @param lineNum, is an int representing the current line of code
     * 
     * @return A string representing the updated expression in Java 
     * 
     */
	private static String process_expr(String statement, boolean isPrint, boolean isMustBeBool, 
			int lineNum) {
		statement = statement.strip();
		// based on the flag, process the string and check parsing error
		if (isPrint) {
			Pattern str = Pattern.compile("\"(.*)\"");
			Matcher m = str.matcher(statement);
			if (m.find()) {
				if (m.group(1).length() + 2 == statement.length()) {
					return statement;
				} else {
					System.out.print("Error on line " + String.valueOf(lineNum) + " : ");
					System.out.println("Unrecognized string/expression for printing out.");
					System.exit(1);
				}
			}
			if (check_int_expr(statement)) {
				return process_int_expr(statement);
			} else {
				if (check_bool_expr(statement)) {
					return process_bool_expr(process_int_expr(statement));
				} else {
					System.out.print("Error on line " + String.valueOf(lineNum) + " : ");
					System.out.println("Unrecognized string/expression for printing out.");
					System.exit(1);
				}
			}
		}
		if (isMustBeBool) {
			if (check_bool_expr(statement)) {
				return process_bool_expr(process_int_expr(statement));
			} else {
				System.out.print("Error on line " + String.valueOf(lineNum) + " : ");
				System.out.println("The condition should a valid boolean expression.");
				System.exit(1);
			}
		}
		return null;
	}

	/**
     * Purpose: Return true if the string is a valid integer expression based on our language,
     * false otherwise.
     * 
     * @param statement, is the string to process
     * 
     * @return True if the string is a valid integer expression based on our language, false
     * otherwise
     * 
     */
	private static boolean check_int_expr(String statement) {
		statement = statement.strip();
		Pattern sign = Pattern.compile("(.+) add (.+)");
		Matcher m = sign.matcher(statement);
		if (m.find()) {
			return check_int_expr(m.group(1)) && check_int_mult_expr(m.group(2));
		}
		sign = Pattern.compile("(.+) sub (.+)");
		m = sign.matcher(statement);
		if (m.find()) {
			return check_int_expr(m.group(1)) && check_int_mult_expr(m.group(2));
		}
		return check_int_mult_expr(statement);
		
	}
	
	/**
     * Purpose: Return true if the string is a valid multiplication/division/modulo integer
     * expression based on our language, false otherwise.
     * 
     * @param statement, is the string to process
     * 
     * @return True if the string is a valid multiplication/division/modulo integer expression
     * based on our language, false otherwise
     * 
     */
	private static boolean check_int_mult_expr(String statement) {
		statement = statement.strip();
		Pattern sign = Pattern.compile("(.+) mult (.+)");
		Matcher m = sign.matcher(statement);
		if (m.find()) {
			return check_int_mult_expr(m.group(1)) && check_int_not_expr(m.group(2));
		}
		sign = Pattern.compile("(.+) div (.+)");
		m = sign.matcher(statement);
		if (m.find()) {
			return check_int_mult_expr(m.group(1)) && check_int_not_expr(m.group(2));
		}
		sign = Pattern.compile("(.+) mod (.+)");
		m = sign.matcher(statement);
		if (m.find()) {
			return check_int_mult_expr(m.group(1)) && check_int_not_expr(m.group(2));
		}
		return check_int_not_expr(statement);
	}


	/**
     * Purpose: Return true if the string is a valid negative integer expression based on our
     * language false otherwise.
     * 
     * @param statement, is the string to process
     * 
     * @return True if the string is a valid negative integer expression based on our language,
     * false otherwise
     * 
     */
	private static boolean check_int_not_expr(String statement) {
		statement = statement.strip();
		Pattern sign = Pattern.compile("negate (.+)");
		Matcher m = sign.matcher(statement);
		if (m.find() && m.group(1).length() + 7 == statement.length()) {
			return check_int_root_expr(m.group(1));
		}
		return check_int_root_expr(statement);
	}


	/**
     * Purpose: Return true if the string is a valid root integer expression based on our
     * language, false otherwise.
     * 
     * @param statement, is the string to process
     * 
     * @return True if the string is a valid root integer expression based on our language,
     * false otherwise
     * 
     */
	private static boolean check_int_root_expr(String statement){
		statement = statement.strip();
		Pattern var = Pattern.compile("[^a-zA-Z]+");
		Matcher m = var.matcher(statement);
		if (!m.find() && setIntVar.contains(statement)) {
			return true;
		}
		Pattern number = Pattern.compile("[^0-9]+");
		m = number.matcher(statement);
		if (!m.find()) {
			return true;
		}
		if (statement.length() > 3) { // check for command line argument variable 
			if (statement.substring(0, 3).equals("arg") && setIntVar.contains(statement)){
				return true;
			}
		}
		return false;
	}

	/**
     * Purpose: Return true if the string is a valid boolean expression based on our language,
     * false otherwise.
     * 
     * @param statement, is the string to process
     * 
     * @return True if the string is a valid boolean expression based on our language, false
     * otherwise
     * 
     */
	private static boolean check_bool_expr(String statement) {
		statement = statement.strip();
		Pattern sign = Pattern.compile("(.+) or (.+)");
		Matcher m = sign.matcher(statement);
		if (m.find()) {
			return check_bool_expr(m.group(1)) && check_bool_and_expr(m.group(2));
		}
		return check_bool_and_expr(statement);
	}

	/**
     * Purpose: Return true if the string is a valid and boolean expression based on our language,
     * false otherwise.
     * 
     * @param statement, is the string to process
     * 
     * @return True if the string is a valid and boolean expression based on our language, false
     * otherwise
     * 
     */
	private static boolean check_bool_and_expr(String statement) {
		statement = statement.strip();
		Pattern sign = Pattern.compile("(.+) and (.+)");
		Matcher m = sign.matcher(statement);
		if (m.find()) {
			return check_bool_and_expr(m.group(1)) && check_bool_not_expr(m.group(2));
		}
		return check_bool_not_expr(statement);
	}


	/**
     * Purpose: Return true if the string is a valid not boolean expression based on our language,
     * false otherwise.
     * 
     * @param statement, is the string to process
     * 
     * @return True if the string is a valid not boolean expression based on our language, false
     * otherwise
     * 
     */
	private static boolean check_bool_not_expr(String statement) {
		statement = statement.strip();
		Pattern sign = Pattern.compile("not (.+)");
		Matcher m = sign.matcher(statement);
		if (m.find() && m.group(1).length() + 4 == statement.length()) {
			return check_bool_root_expr(m.group(1));
		}
		return check_bool_root_expr(statement);
	}


	/**
     * Purpose: Return true if the string is a valid root boolean expression based on our language,
     * false otherwise.
     * 
     * @param statement, is the string to process
     * 
     * @return True if the string is a valid root boolean expression based on our language, false
     * otherwise
     * 
     */
	private static boolean check_bool_root_expr(String statement) {
		// check for the validity of comparison
		Pattern sign = Pattern.compile("(.+) gt (.+)");
		Matcher m = sign.matcher(statement);
		if (m.find()) {
			return check_int_expr(m.group(1)) && check_int_expr(m.group(2));
		}
		sign = Pattern.compile("(.+) gte (.+)");
		m = sign.matcher(statement);
		if (m.find()) {
			return check_int_expr(m.group(1)) && check_int_expr(m.group(2));
		}
		sign = Pattern.compile("(.+) lt (.+)");
		m = sign.matcher(statement);
		if (m.find()) {
			return check_int_expr(m.group(1)) && check_int_expr(m.group(2));
		}
		sign = Pattern.compile("(.+) lte (.+)");
		m = sign.matcher(statement);
		if (m.find()) {
			return check_int_expr(m.group(1)) && check_int_expr(m.group(2));
		}
		sign = Pattern.compile("(.+) diff (.+)");
		m = sign.matcher(statement);
		if (m.find()) {
			return check_int_expr(m.group(1)) && check_int_expr(m.group(2));
		}
		sign = Pattern.compile("(.+) equal (.+)");
		m = sign.matcher(statement);
		if (m.find()) {
			return check_int_expr(m.group(1)) && check_int_expr(m.group(2));
		}
		statement = statement.strip();
		if (statement.equals("true")) {
			return true;
		}
		if (statement.equals("false")) {
			return true;
		}
		Pattern var = Pattern.compile("[^a-zA-Z]+");
		m = var.matcher(statement);
		if (!m.find() && setBoolVar.contains(statement)) {
			return true;
		}
		return false;
	}
	
	/**
     * Purpose: Process a boolean expression our language and return an updated boolean expression
     * in Java.
     * 
     * @param statement, is the string to process
     * 
     * @return A string representing the updated boolean expression in Java
     * 
     */
	private static String process_bool_expr(String statement) {
		statement = statement.strip();
		Pattern sign = Pattern.compile("(.+) lt (.+)");
		Matcher m = sign.matcher(statement);
		while (m.find()) {
			statement = m.group(1) + " < " + m.group(2);
			m = sign.matcher(statement);
		}
		
		sign = Pattern.compile("(.+) gt (.+)");
		m = sign.matcher(statement);
		while (m.find()) {
			statement = m.group(1) + " > " + m.group(2);
			m = sign.matcher(statement);
		}
		
		sign = Pattern.compile("(.+) lte (.+)");
		m = sign.matcher(statement);
		while (m.find()) {
			statement = m.group(1) + " <= " + m.group(2);
			m = sign.matcher(statement);
		}
		
		sign = Pattern.compile("(.+) gte (.+)");
		m = sign.matcher(statement);
		while (m.find()) {
			statement = m.group(1) + " >= " + m.group(2);
			m = sign.matcher(statement);
		}
		
		sign = Pattern.compile("(.+) equal (.+)");
		m = sign.matcher(statement);
		while (m.find()) {
			statement = m.group(1) + " == " + m.group(2);
			m = sign.matcher(statement);
		}
		
		sign = Pattern.compile("(.+) diff (.+)");
		m = sign.matcher(statement);
		while (m.find()) {
			statement = m.group(1) + " != " + m.group(2);
			m = sign.matcher(statement);
		}
		
		sign = Pattern.compile("(.+) and (.+)");
		m = sign.matcher(statement);
		while (m.find()) {
			statement = m.group(1) + " && " + m.group(2);
			m = sign.matcher(statement);
		}
		
		sign = Pattern.compile("(.+) or (.+)");
		m = sign.matcher(statement);
		while (m.find()) {
			statement = m.group(1) + " || " + m.group(2);
			m = sign.matcher(statement);
		}

		sign = Pattern.compile("not (.+)");
		m = sign.matcher(statement);
		while (m.find()) {
			statement = statement.replaceAll("not ", "!");
			m = sign.matcher(statement);
		}
		
		return statement;
	}

	/**
     * Purpose: Process a integer expression our language and return an updated integer expression
     * in Java.
     * 
     * @param statement, is the string to process
     * 
     * @return A string representing the updated integer expression in Java
     * 
     */
	private static String process_int_expr(String statement) {
		statement = statement.strip();
		Pattern sign = Pattern.compile("(.+) add (.+)");
		Matcher m = sign.matcher(statement);
		while (m.find()) {
			statement = m.group(1) + " + " + m.group(2);
			m = sign.matcher(statement);
		}
		
		sign = Pattern.compile("(.+) sub (.+)");
		m = sign.matcher(statement);
		while (m.find()) {
			statement = m.group(1) + " - " + m.group(2);
			m = sign.matcher(statement);
		}
		
		sign = Pattern.compile("(.+) mult (.+)");
		m = sign.matcher(statement);
		while (m.find()) {
			statement = m.group(1) + " * " + m.group(2);
			m = sign.matcher(statement);
		}
		
		sign = Pattern.compile("(.+) div (.+)");
		m = sign.matcher(statement);
		while (m.find()) {
			statement = m.group(1) + " / " + m.group(2);
			m = sign.matcher(statement);
		}
		
		sign = Pattern.compile("(.+) mod (.+)");
		m = sign.matcher(statement);
		while (m.find()) {
			statement = m.group(1) + " % " + m.group(2);
			m = sign.matcher(statement);
		}
		
		sign = Pattern.compile("negate (.+)");
		m = sign.matcher(statement);
		while (m.find()) {
			statement = statement.replaceAll("negate ", "-");
			m = sign.matcher(statement);
		}

		return statement;
	}
	
}
