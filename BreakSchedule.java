package optimalBreaks;

import java.util.ArrayList;
import java.util.*;

public class BreakSchedule {
	
	
	// Use this class to implement programs for Tasks 2 & 3. Your file must not change the package name,
	// nor the class name. You must not change the names nor the signatures of the two program stubs
	// in this file. You may augment this file with any other method or variable/data declarations that you
	// might need to implement the dynamic programming strategy requested for Tasks 2&3.
	// Make sure however that all your declarations are public.
	
	public Integer[][][][] table;
	public ArrayList<Integer> ans;
	
	// Precondition: word is a string and breakList is an array of integers in strictly increasing order
	//               the last element of breakList is no more than the number of characters in word.
	// Postcondition: Return the minimum total cost of breaking the string word into |breakList|+1 pieces, where 
	//                the position of each each break is specified by the integers in breakList. 
	//                Refer to the assignment specification for how a single break contributes to the cost.

	int totalCost (String word, ArrayList<Integer> breakList) {
		
		// Check for any obvious 0 answers
		if (breakList == null || breakList.size() == 0 || word.length() == 0){
			return 0;
		}

		// Prepare variables for recursive function
		int[] sub = {0, word.length() - 1}; // populate 'sub' with 'word' start and end character indices
		ArrayList<Integer> cleanList = new ArrayList<Integer>();
		for (int i = 0; i < breakList.size(); i++){
			if(breakList.get(i) >= sub[0] && breakList.get(i) < sub[1]){ // Populate 'cleanList' with valid breaks from 'breakList'
				cleanList.add(breakList.get(i));
			}
		}

		// Check 'cleanList' is not empty to avoid unnecessary 'myCost' call
		if(cleanList.size() == 0){
			return 0;
		}

		// Initialize 'table' to allow for all possible inputs based on 'word' length
		table = new Integer[word.length()-1][word.length()][word.length()-1][word.length()-1];
		
		return myCost(sub, cleanList);
	}

	public int myCost (int[] word, ArrayList<Integer> breakList) {
		if (breakList.size() == 0){
			return 0;
		}
		
		// Check table for previous result
		if(table[word[0]][word[1]][breakList.get(0)][breakList.get(breakList.size()-1)] != null){
			return table[word[0]][word[1]][breakList.get(0)][breakList.get(breakList.size()-1)];
		}

		// Cost of break on given substring
		int strLgth = word[1] - word[0] + 1;

		// 'breakList's with only one break do not require recursive call
		if (breakList.size() == 1){
			table[word[0]][word[1]][breakList.get(0)][breakList.get(breakList.size()-1)] = strLgth;
			return strLgth;
		}

		int opt = 0;

		// Find cost of optimal cost for break 'b' including costs for all subsequent breaks
		for (int b = 0; b < breakList.size(); b++) {

			// Create 'breakList's for substrings from break 'b'
			ArrayList<Integer> greaterB = new ArrayList<Integer>();
			ArrayList<Integer> lesserB = new ArrayList<Integer>();
			for (int i = 0; i < b; i++){
				lesserB.add(breakList.get(i));
			}
			for (int i = b + 1; i < breakList.size(); i++){
				greaterB.add(breakList.get(i));
			}

			// Create indices for substrings from break 'b'
			int[] small = new int[2];
			small[0] = 0;
			small[1] = breakList.get(b);
			int[] big = new int[2];
			big[0] = breakList.get(b) + 1;
			big[1] = word[1];

			// Calculate cost of break 'b' + optimal costs of subsequent breaks
			int next = strLgth + myCost(small, lesserB) + myCost(big, greaterB);

			if (opt == 0 || (next < opt && next != 0)) {
				opt = next;
			}
		}

		// Store optimal break cost and return
		table[word[0]][word[1]][breakList.get(0)][breakList.get(breakList.size()-1)] = opt;
		return opt;
	}
	 
	// Precondition: word is a string and breakList is an array of integers in strictly increasing order
	//               the last element of breakList is no more than the number of characters in word.
	// Postcondition: Return the schedule of breaks so that word is broken according to the list of
	// 					breaks specified in breakList which yields the minimum total cost.

	ArrayList<Integer> breakSchedule (String word, ArrayList<Integer> breakList) {
		
		// Check for any obvious null answers
		if (breakList == null || breakList.size() == 0 || word.length() == 0){
			return null;
		}

		// Prepare variables for recursive function
		int[] sub = {0, word.length() - 1};
		ArrayList<Integer> cleanList = new ArrayList<Integer>();
		for (int i = 0; i < breakList.size(); i++){
			if(breakList.get(i) >= sub[0] && breakList.get(i) < sub[1]){
				cleanList.add(breakList.get(i));
			}
		}

		// Check 'cleanList' is not empty to avoid unnecessary 'mySchedule' call
		if (cleanList.isEmpty()){
			return null;
		}

		// Populate 'table' and get optimal cost
		int opt = totalCost(word, breakList);

		ans = new ArrayList<Integer>();
		mySchedule(sub, cleanList, opt);
		return ans;
	}

	public void mySchedule (int[] word, ArrayList<Integer> breakList, int opt) {
		
		for (int b = 0; b < breakList.size(); b++) {

			// Create 'breakList's for substrings from break 'b'
			ArrayList<Integer> greaterB = new ArrayList<Integer>();
			ArrayList<Integer> lesserB = new ArrayList<Integer>();
			for (int i = 0; i < b; i++){
				lesserB.add(breakList.get(i));
			}
			for (int i = b + 1; i < breakList.size(); i++){
				greaterB.add(breakList.get(i));
			}

			// Create indices for substrings from break 'b'
			int[] small = new int[2];
			small[0] = 0;
			small[1] = breakList.get(b);
			int[] big = new int[2];
			big[0] = breakList.get(b) + 1;
			big[1] = word[1];

			// Find optimal break based on optimal cost and cost of subsequent breaks
			if(myCost(small, lesserB) + myCost(big, greaterB) + word[1] - word[0] + 1 == opt){
				ans.add(breakList.get(b)); // Add this break to the answer

				// Find subsequent optimal breaks
				mySchedule(small, lesserB, myCost(small, lesserB));
				mySchedule(big, greaterB, myCost(big, greaterB));
				break; // Avoid adding unnecessary breaks in case of other optimal breakSchedules existing
			}
		}
	}

	public static void main(String[] args) {
		BreakSchedule x= new BreakSchedule();
		String w= "Supercalifragilisticexpialidocious";
		ArrayList<Integer> b= new ArrayList<Integer>();
		b.add(1);
		b.add(6);
		b.add(7);
		b.add(14);
		b.add(24);
		b.add(31);
		b.add(32);
		b.add(33);
		System.out.println("The word is: " + w);
		System.out.println("The break list is: " + b);
		System.out.println("The smallest cost is: " + x.totalCost(w,b));
		System.out.println("The order of breaks is: " + x.breakSchedule(w,b));
	}
}
