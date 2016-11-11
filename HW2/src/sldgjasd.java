import java.util.Scanner;

public class sldgjasd {
	
	public static void main(String[] args) {
		Scanner scan = new Scanner(System.in);
		String str = "";
		while (!str.equals("end")) {
			 str = scan.nextLine();
	            char indicator = str.charAt(0);
	            if (indicator == 'E') {
	                maskEmail(str);
	            } 
		}
	}
	
	public static void maskEmail(String email) {
		StringBuilder sb = new StringBuilder();
		for (int i=0; i < email.length(); i++) {
			char c = email.charAt(i);
			boolean isDigit = (c >='0' && c <= '9');
			if (isDigit || c == '+')
				sb.append(c);
		}
		
		System.out.println("sb: " + sb);
		
		String lastFourDigits = sb.substring(sb.length()-4);
		sb.delete(sb.length()-4, sb.length());
//		System.out.println(lastFourDigits);
//		
//		System.out.println("sb: " + sb);
//		
//		String last3 = sb.substring(sb.length()-3);
//		sb.delete(sb.length()-3, sb.length());
//		System.out.println(last3);
//		
//		String next3 = sb.substring(sb.length()-3);
//		sb.delete(sb.length()-3, sb.length());
//		System.out.println(next3);
		
		sb.delete(sb.length()-6, sb.length());
		
		System.out.println();
		
		
		
		if (sb.length() == 0) {
			System.out.println("masked: " + "***-***-" + lastFourDigits);
		} else {
			for (int i = 1; i < sb.length(); i++) {
				sb.setCharAt(i, '*');
			}
			System.out.println("masked: " + sb.toString() + "-***-***-" + lastFourDigits);
		}
		
		
		
	}

}
