//An Owen Khoury and Zachary Skemp Masterpiece
//This project made me so sad many times. But overall it was good
//9-22-16
//okk6nb & zrs2sb

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
//import java.util.Map;
import java.util.Scanner;
import java.util.Stack;

import javax.xml.stream.events.Characters;

public class Expert_System {
	
	
	static int logicNum = 0;
	static boolean hasRecursed = false;
	
	static Map<String, String> rootVars = new HashMap<String, String>();
	static Map<String, String> learnedVars = new HashMap<String, String>();
	
	// These arraylist just keep the order in which variable are learned
	static ArrayList<String> rootVarsOrder = new ArrayList<String>();
	static ArrayList<String> learnedVarsOrder = new ArrayList<String>();
	
	static ArrayList<String> rules = new ArrayList<String>();
	static ArrayList<String> facts = new ArrayList<String>();
	
	
	// Associativity constants for operators
    private static final int LEFT_ASSOC  = 0;
    private static final int RIGHT_ASSOC = 1;
  
    // Operators
    private static final Map<String, int[]> OPERATORS = new HashMap<String, int[]>();
    static
    {
        // Map<"token", []{precendence, associativity}>
        OPERATORS.put("&", new int[] { 5, LEFT_ASSOC });
        OPERATORS.put("|", new int[] { 0, LEFT_ASSOC });       
    }

	public static void main(String[] args) {
		Scanner scanner = new Scanner(System.in);
		String command = scanner.nextLine();
		
		while (!command.equals("-1")) {

			// Parse the 3 different teach commands
			if (command.contains("Teach")) {
				if (command.contains("-L") || command.contains("-R")) { // 1st teach command													
					String[] partsOfCom = command.split(" ");
					String arg = partsOfCom[1];
					String var = partsOfCom[2];
					String varDef = command.substring(command.indexOf("=") + 3, command.length()-1);
					teach1(arg, var, varDef);
					
				} else if (command.contains("->")) {					// 3rd teach command
					String[] partsOfCom = command.split(" ");
					String exp = partsOfCom[1];
					String var = partsOfCom[3];
					teach3(exp, var);
				} else {												// 2nd teach command
					String[] partsOfCom = command.split(" ");
					String var = partsOfCom[1];
					boolean tf = (command.contains("true")) ? true : false;
					teach2(var, tf);
				}
			} else if (command.contains("List")) {
				list();
			} else if (command.contains("Learn")) {
				learn();
			} else if (command.contains("Query")) {
				String query = command.substring(6, command.length()-1);
				query(query);
			} else if (command.contains("Why")) {
				String y = command.substring(4, command.length()-1);
				//why(y);
				how(y);
			}
			command = scanner.nextLine();
		}
	}
	
	
	public static void teach1 (String arg, String var, String varDef) {
		String putTogether = var + " = " + "\"" + varDef + "\"";
		if (arg.equals("-R")) {
			rootVarsOrder.add(putTogether);
			rootVars.put(var, varDef);
		} else {
			learnedVarsOrder.add(putTogether);
			learnedVars.put(var, varDef);
		}
		
		facts.add("!" + var);
		
	}
	
	public static void teach2(String rootVar, boolean tf) {

		if (rootVars.containsKey(rootVar) && facts.contains(rootVar) && tf == false) {

			facts.remove(rootVar);
			facts.add("!" + rootVar);
			
			setLearnedVarsFalse();

		} else if (rootVars.containsKey(rootVar) && facts.contains("!" + rootVar) && tf == true) {
			String tempfix = "!" + rootVar;
			facts.remove(tempfix);
			facts.add(rootVar);
			
			setLearnedVarsFalse();
			
		} else if ((rootVars.containsKey(rootVar) && facts.contains("!" + rootVar) && tf == false) || 
				(rootVars.containsKey(rootVar) && facts.contains(rootVar) && tf == true)) {
			// Do nothing
		} else {
			if (rootVars.containsKey(rootVar) && tf) {
				facts.add(rootVar);
			} else if (rootVars.containsKey(rootVar) && !tf) {
				facts.add("!" + rootVar);
			}
		}
	}
	
	public static void setLearnedVarsFalse() {
		/*
		 * Go through every fact. check if it is a learned var. make all learned
		 * vars false
		 */
		
		for (int i = 0; i < facts.size(); i++) {
			if (learnedVars.containsKey(facts.get(i))) {
				String temp = facts.get(i);
				if (!temp.contains("!"))
					facts.set(i, "!" + temp);
			}
		}
	}
	
	public static void teach3 (String expr, String var) {
		// var must be a learned variable
		// expr can be a combination of learned and root vars
		rules.add(expr + " -> " + var);
	}
	
	public static void learn() {
		// Go through all of the rules. parse the rules. determine new facts
		// based on existing facts

		setLearnedVarsFalse();

		for (String rule : rules) {
			String[] partOfRule = rule.split(" ");
			String ifPart = partOfRule[0];
			String thenPart = partOfRule[2];

			String[] result = infixToRPN(addSpacesToString(ifPart).split(" "));
			int ans = Integer.parseInt(logicVal(result));

			if (ans == 1 && facts.contains("!" + thenPart)) {
				facts.remove("!" + thenPart);
				facts.add(thenPart);
			} else if (ans == 1 && !facts.contains(thenPart)) {
				facts.add(thenPart);
				
			}
		}
	}
	
	/*
	 *  Adds spaces into the passed in string, so that it can be passed into the postfix parser
	 */
	public static String addSpacesToString(String ifPart) {
		String newIfPart = "";
		
		//Add spaces around parenthesis and any predicate logic symbols
		for (int i = 0; i < ifPart.length(); i++) {
			if (ifPart.charAt(i) == ')' || ifPart.charAt(i) == '(' || ifPart.charAt(i) == '&'
					|| ifPart.charAt(i) == '|') {
				newIfPart += " " + ifPart.charAt(i) + " ";
			} else {
				newIfPart += ifPart.charAt(i);
			}
		}

		StringBuilder y = new StringBuilder();
		y.append(newIfPart.replaceAll("\\s+", " "));
		if (y.charAt(0) == ' ') {
			y.deleteCharAt(0);
		}

		if (y.charAt(y.length() - 1) == ' ') {
			y.deleteCharAt(y.length() - 1);
		}

		return y.toString();
	}
	
	/*
	 * Determine the truth value of the expression passed in. Use backwards chaining if necessary
	 */
	public static boolean query (String expr) {
		//System.out.println(expr);
		String[] result = infixToRPN(addSpacesToString(expr).split(" "));
		//System.out.println(Arrays.toString(result));
		//System.out.println(logicVal2(result));
		if (logicVal2(result).equals("1")) {
			System.out.println("true");
			return true;
		} else {
			System.out.println("false");
			return false;
		}	
	}
	
	/*
	 * Recursively go backwards through the rules until you determine the truth value of var
	 */
	public static String backwardChaining (String var) {
		if (facts.contains(var)) {
			return "1";
		} else {
			for (int i = 0; i < rules.size(); i++) {
				String[] partOfRule = rules.get(i).split(" ");
				String ifPart = partOfRule[0];
				String thenPart = partOfRule[2];
				
				if (var.equals(thenPart)) {
					String[] result = infixToRPN(addSpacesToString(ifPart).split(" "));
					//System.out.println(Arrays.toString(result));
					return logicVal2(result);
				}
			}
		}
		
		return "0";
	}
	
	public static void how (String expr) {
		String[] result = infixToRPN(addSpacesToString(expr).split(" "));
		ArrayList<String> vars = new ArrayList<String>();
		vars.add(expr); //This will be the final statement: Thus expr
		boolean daTruth = query(expr);
		parseHow(howLogic2(result, vars), daTruth);
		
		
	}
	
	public static String printingHow (String var) {
		if(var.contains("|") || var.contains("&")){
			StringBuilder pDiddy = new StringBuilder();
			for (char c : var.toCharArray()) {
			if(c == '|'){
				pDiddy.append(" OR ");
			}
			else if(c == '&') {
				pDiddy.append(" AND ");
			}
			else {
				pDiddy.append(printingHow(Character.toString(c)));
			}
			}			
			return ("(" + pDiddy.toString() + ")");
		}
		if(rootVars.containsKey(var)){
			return (rootVars.get(var));
		}
		else{
			return (learnedVars.get(var));
		}
	}

	public static void tryToAdd(ArrayList<String> finalOutput, String addedString) {
		if(finalOutput.contains(addedString)) {
			return;
		}
		else{
			finalOutput.add(addedString);
			return;
		}
	}
	
	public static void parseHow(ArrayList<String> vars, boolean daTruth) {
		ArrayList<String> finalOutput = new ArrayList<String>();
		for (int i = vars.size() - 1; i >= 0; i--) {
			//System.out.println(finalOutput);

			
			if (i == 0) {
				String addedString;
				if(daTruth == false){
					addedString = ("THUS I CANNOT PROVE " +  printingHow(vars.get(i)));
				}
				else if (vars.get(i).contains("!")) {
					addedString = ("THUS I KNOW THAT NOT " + printingHow(vars.get(i)));
				} else {
					addedString = ("THUS I KNOW THAT " + printingHow(vars.get(i)));
				}
				tryToAdd(finalOutput, addedString);
			} else if (vars.get(i).contains("*")) {
				if (vars.get(i).contains("!")) {
					String addedString = ("I KNOW IT IS NOT TRUE THAT "
							+ printingHow(vars.get(i).substring(1, vars.get(i).length() - 1)));
					tryToAdd(finalOutput, addedString);
				} else {
					String addedString = ("I KNOW THAT "
							+ printingHow(vars.get(i).substring(0, vars.get(i).length() - 1)));
					tryToAdd(finalOutput, addedString);
				}
			} else {
				String addedString;
				if (vars.get(i).contains("!") && vars.get(i - 1).contains("!")) {
					addedString = ("BECAUSE I KNOW IT IS NOT TRUE THAT " + printingHow(vars.get(i).substring(1))
							+ ", I KNOW IT IS NOT TRUE THAT " + printingHow(vars.get(i - 1).substring(1)));
				} else if (vars.get(i).contains("!")) {
					addedString = ("BECAUSE I KNOW IT IS NOT TRUE THAT " + printingHow(vars.get(i).substring(1))
							+ ", I KNOW " + printingHow(vars.get(i - 1)));
				} else if (vars.get(i).contains("!")) {
					addedString = ("BECAUSE I KNOW THAT " + printingHow(vars.get(i)) + ", I KNOW IT IS NOT TRUE THAT"
							+ printingHow(vars.get(i - 1).substring(1)));
				} else {
					addedString = ("BECAUSE I KNOW THAT " + printingHow(vars.get(i)) + ", I KNOW "
							+ printingHow(vars.get(i - 1)));
				}
				tryToAdd(finalOutput, addedString);
				i--;
			}
		}
		//System.out.println(finalOutput);

		for (int j = 0; j < finalOutput.size(); j++) {
			System.out.println(finalOutput.get(j));
		}
	}
	
	

	
	public static void list() {
		System.out.println("Root Variables:");
		for (String rvar : rootVarsOrder) {
			System.out.println("\t" + rvar);
		}
		System.out.println();

		System.out.println("Learned Variables:");
		for (String lvar : learnedVarsOrder) {
			System.out.println("\t" + lvar);
		}
		System.out.println();

		System.out.println("Facts:");
		for (String fact : facts) {
			System.out.println("\t" + fact);
		}
		System.out.println();

		System.out.println("Rules:");
		for (String rulez : rules) {
			System.out.println("\t" + rulez);
		}
		System.out.println();
	}

	/*
	 * The logic if (of the if -> then statement) is passed in to function in posfix notation
	 * Go through and evaluate the truth value of the entire expression. If any variable is unknown,
	 * recursively apply backward chaining to determine its truth value
	 */
	public static String logicVal2(String[] tokens) {
		Stack<String> stack = new Stack<String>();
		
		if (tokens.length == 1) {
			if (facts.contains(tokens[0])) {
				//System.out.println(tokens[0]);
				return "1";
			} else {
				//System.out.println("bc: " + tokens[0]);
				return backwardChaining(tokens[0]);
			}
		}

		// For each token
		for (String token : tokens) {
			// If the token is a value push it onto the stack
			if (!isOperator(token)) {
				stack.push(token);
//				if (facts.contains(token)) {
//					System.out.println(token);
//					return "1";
//				} else {
//					System.out.println(token);
//					return backwardChaining(token);
//				}
			} else {
				// Token is an operator: pop top two entries
				String d2 = stack.pop();
				String d1 = stack.pop();
				
//				System.out.println();
//				System.out.println(d2);
//				System.out.println(d1);
//				System.out.println(token);
//				System.out.println();
				
				String val1;
				String val2;
				
				if (facts.contains(d2) || d2.equals("1")) {
					val2 = "1";
				} else if (d2.equals("0")) {
					val2 = "0";
				} else {
					val2 = backwardChaining(d2);
				}
				
				if (facts.contains(d1) || d1.equals("1")) {
					val1 = "1";
				} else if (d1.equals("0")) {
					val1 = "0";
				} else {
					val1 = backwardChaining(d1);
				}
				
//				System.out.println(val2);
//				System.out.println(val1);
				
				
				// Get the result
				int result = token.compareTo("&") == 0 ? Integer.parseInt(val1) & Integer.parseInt(val2)
						: token.compareTo("|") == 0 ? Integer.parseInt(val1) | Integer.parseInt(val2) : 0;
				
//				System.out.println(result);
//				System.out.println();

				// Push result onto stack
				stack.push(Integer.toString(result));
//				System.out.println(stack);
//				System.out.println();
//				System.out.println();
				
			}
		}
		return stack.pop();
	}

	
	// This is a test function
	public static ArrayList<String> howLogic2(String[] tokens, ArrayList<String> vars) {
		// Stack<String> stack = new Stack<String>();

		// For each token
		for (String token : tokens) {
			if (!isOperator(token)) {
				if (facts.contains(token)) {
					vars.add(token + "*"); // Add this for facts
				} 
				else if(rootVars.containsKey(token) && facts.contains("!" + token)) {
					vars.add("!" + token + "*");
				}
				else {
					for (int i = 0; i < rules.size(); i++) {
						String[] partOfRule = rules.get(i).split(" ");
						String ifPart = partOfRule[0];
						String thenPart = partOfRule[2];

						if (thenPart.equals(token)) {
							String[] result = infixToRPN(addSpacesToString(ifPart).split(" "));
							vars.add(token);
							vars.add(ifPart);
							howLogic2(result, vars);
						}
					}
				}
			}
		}
		//System.out.println("Final status of vars: " + vars);
		return vars;
	}
	
	
	/*
	 * The first variable stored in vars is what we are finally trying to prove (the thus variable). The last variable
	 */
	
	
	 
//	public static void parseVarsList(ArrayList<String> vars) {
//		// ArrayList of operators
//		ArrayList<Character> operators = new ArrayList<Character>();
//		operators.add('&');
//		operators.add('|');
//
//		ArrayList<String> temp = new ArrayList<String>();
//		temp = vars;
//
//		// I believe that the variable in the last position will always be a
//		// fact
//		if (rootVars.containsKey(vars.get(vars.size() - 1))) {
//			System.out.println("I KNOW " + rootVars.get(vars.get(vars.size() - 1)));
//		} else if (learnedVars.containsKey(vars.get(vars.size() - 1))) {
//			System.out.println("I KNOW " + learnedVars.get(vars.get(vars.size() - 1)));
//		}
//
//		// Removes first instance of the variable in the last postition. idk if
//		// this works? seems to
//		vars.remove(vars.get(vars.size() - 1));
//
//		System.out.println(temp);
//
//		while (temp.size() > 1) {
//			/*
//			 * I think this following if condition will be able to identify when
//			 * there was a lone fact and an if -> then
//			 */
//			System.out.println(temp.size());
//			// if (temp.size() >= 3) { // The current var we're looking at is a
//			// lone fact. ignore on first pass,
//			// String currentVar = vars.get(vars.size() - 1);
//			// String oneBehindCur = vars.get(vars.size() - 2);
//			// String twoBehindCur = vars.get(vars.size() - 3);
//			// if (!currentVar.equals(oneBehindCur) ||
//			// !currentVar.equals(twoBehindCur)) {
//			// if (rootVars.containsKey(currentVar)) {
//			// System.out.println("I KNOW " + rootVars.get(currentVar));
//			// } else {
//			// System.out.println("I KNOW " + learnedVars.get(currentVar));
//			// }
//			//
//			// vars.remove(vars.size()-1);
//			// }
//			// } else {
//
//			String bcVar = temp.get(temp.size() - 1);
//			System.out.println("bc var= " + bcVar);
//			String iKnowVar = temp.get(temp.size() - 2);
//			System.out.println("iKnowVar= " + iKnowVar);
//
//			if (bcVar.length() == 1 && iKnowVar.length() == 1) {
//				if (learnedVars.containsKey(bcVar) && learnedVars.containsKey(iKnowVar)) {
//					System.out.println(
//							"BECAUSE I KNOW " + learnedVars.get(bcVar) + ", I KNOW " + learnedVars.get(iKnowVar));
//				} else if (rootVars.containsKey(bcVar) && rootVars.containsKey(iKnowVar)) {
//					System.out.println("BECAUSE I KNOW " + rootVars.get(bcVar) + ", I KNOW " + rootVars.get(iKnowVar));
//				} else if (rootVars.containsKey(bcVar) && learnedVars.containsKey(iKnowVar)) {
//					System.out
//							.println("BECAUSE I KNOW " + rootVars.get(bcVar) + ", I KNOW " + learnedVars.get(iKnowVar));
//				} else if (learnedVars.containsKey(bcVar) && rootVars.containsKey(iKnowVar)) {
//					System.out
//							.println("BECAUSE I KNOW " + learnedVars.get(bcVar) + ", I KNOW " + rootVars.get(iKnowVar));
//				}
//			} else if (bcVar.length() > 1 && iKnowVar.length() == 1) {
//				System.out.print("BECAUSE I KNOW ");
//				for (char c : bcVar.toCharArray()) {
//					String cStr = Character.toString(c);
//					boolean isRootVar = false;
//					if (rootVars.containsKey(cStr))
//						isRootVar = true;
//
//					if (!operators.contains(c)) {
//						if (isRootVar) {
//							// if (query(cStr)) {
//							System.out.print(rootVars.get(cStr));
//							// }
//						} else {
//							// if (query(cStr)) {
//							System.out.print(learnedVars.get(cStr));
//							// }
//						}
//					} else {
//						if (c == '&') {
//							System.out.print(" AND ");
//						} else if (c == '|') {
//							System.out.print(" OR ");
//						}
//					}
//				}
//
//				String thusVar = iKnowVar;
//				if (learnedVars.containsKey(thusVar)) {
//					System.out.println(" I THUS KNOW " + learnedVars.get(thusVar));
//				} else if (rootVars.containsKey(thusVar)) {
//					System.out.println(" I THUS KNOW " + rootVars.get(thusVar));
//				}
//			}
//
//			temp.remove(temp.size() - 1);
//			temp.remove(temp.size() - 1);
//
//			System.out.println(temp);
//		}
//	}
//	
//	
	
	
	
	public static String logicVal(String[] tokens) {
		Stack<String> stack = new Stack<String>();

		// For each token
		for (String token : tokens) {
			// If the token is a value push it onto the stack
			if (!isOperator(token)) {
				stack.push(token);
			} else {
				// Token is an operator: pop top two entries
				String d2 = stack.pop();
				String d1 = stack.pop();
				
//				System.out.println(d2);
//				System.out.println(d1);
//				System.out.println(token);
//				System.out.println();
				
				int val1;
				int val2;
				
				if (facts.contains(d2) || d2.equals("1")) {
					val2 = 1;
				} else {
					val2 = 0;
				}
				
				if (facts.contains(d1) || d1.equals("1")) {
					val1 = 1;
				} else {
					val1 = 0;
				}
				
//				System.out.println(val2);
//				System.out.println(val1);
				
				// Get the result
				int result = token.compareTo("&") == 0 ? val1 & val2
						: token.compareTo("|") == 0 ? val1 | val2 : 0;
				
//				System.out.println(result);
//				System.out.println();

				// Push result onto stack
				stack.push(Integer.toString(result));
//				System.out.println(stack);
//				System.out.println();
//				System.out.println();
				
			}
		}
		return stack.pop();
	}
	
	
	// Test if token is an operator
    private static boolean isOperator(String token) 
    {
        return OPERATORS.containsKey(token);
    }
  
    // Test associativity of operator token
    private static boolean isAssociative(String token, int type) 
    {
        if (!isOperator(token)) 
        {
            throw new IllegalArgumentException("Invalid token: " + token);
        }
         
        if (OPERATORS.get(token)[1] == type) {
            return true;
        }
        return false;
    }
  
    // Compare precedence of operators.    
    private static final int cmpPrecedence(String token1, String token2) 
    {
        if (!isOperator(token1) || !isOperator(token2)) 
        {
            throw new IllegalArgumentException("Invalid tokens: " + token1
                    + " " + token2);
        }
        return OPERATORS.get(token1)[0] - OPERATORS.get(token2)[0];
    }
  
    // Convert infix expression format into reverse Polish notation
    public static String[] infixToRPN(String[] inputTokens) 
    {
        ArrayList<String> out = new ArrayList<String>();
        Stack<String> stack = new Stack<String>();
         
        // For each token
        for (String token : inputTokens) 
        {
            // If token is an operator
            if (isOperator(token)) 
            {  
                // While stack not empty AND stack top element 
                // is an operator
                while (!stack.empty() && isOperator(stack.peek())) 
                {                    
                    if ((isAssociative(token, LEFT_ASSOC)         && 
                         cmpPrecedence(token, stack.peek()) <= 0) || 
                        (isAssociative(token, RIGHT_ASSOC)        && 
                         cmpPrecedence(token, stack.peek()) < 0)) 
                    {
                        out.add(stack.pop());   
                        continue;
                    }
                    break;
                }
                // Push the new operator on the stack
                stack.push(token);
            } 
            // If token is a left bracket '('
            else if (token.equals("(")) 
            {
                stack.push(token);  // 
            } 
            // If token is a right bracket ')'
            else if (token.equals(")")) 
            {                
                while (!stack.empty() && !stack.peek().equals("(")) 
                {
                    out.add(stack.pop()); 
                }
                stack.pop(); 
            } 
            // If token is a number
            else
            {
                out.add(token); 
            }
        }
        while (!stack.empty())
        {
            out.add(stack.pop()); 
        }
        String[] output = new String[out.size()];
        return out.toArray(output);
    }

}
