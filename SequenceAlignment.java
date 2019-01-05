package com.tomatedigital.utils.general;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class SequenceAlignment {
	static final String GAP_CHAR = "_"; //For printing the final alignment

	private static final Set<Character> vowels = new HashSet<>(Arrays.asList(new Character[] { 'a', 'e', 'i', 'o', 'u' }));
	private static final Set<Character> consonants = new HashSet<>(Arrays.asList(new Character[] { 'b', 'c', 'd', 'f', 'g', 'h', 'j', 'k', 'l', 'm', 'n', 'p', 'q', 'r', 's', 't', 'v', 'w', 'x', 'y', 'z' }));
	private static final Set<Character> numbers = new HashSet<>(Arrays.asList(new Character[] { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9' }));


	private int[][] memoTable;
	private int[][][] predecessorIndexes; //stored index where the value @ memoTable[i][j] came from (diagonal, above or left)
	private int gap;
	private int vowelV;
	private int consonantC;
	private int vowelC;
	private int numberN;


	public SequenceAlignment(int gap, int vv, int cc, int vc, int nn) {
		this.gap = gap;
		this.vowelV = vv;
		this.consonantC = cc;
		this.vowelC = vc;
		this.numberN = nn;
	}

	public SequenceAlignment() {
		this(2, 1, 1, 3, 1);
	}

	//Creates the memo table for the optimal alignment
	public void calcOptimalAlignment(String seq1, String seq2) {
		seq1 = seq1.toLowerCase();
		seq2 = seq2.toLowerCase();

		seq1 = seq1.trim(); //trim any whitespace
		seq2 = seq2.trim();

		seq1 = seq1.replaceAll(" ", ""); //Remove spaces
		seq2 = seq2.replaceAll(" ", "");

		seq1 = " " + seq1; //prepend a space @ the start. Allows for easier calls to mismatchPenalty() & array boundaryies for size of memoTalbe to be "<" instead of "<="
		seq2 = " " + seq2;

		//Initialize 2D arrays for memoization
		memoTable = new int[seq1.length()][seq2.length()];
		predecessorIndexes = new int[seq1.length()][seq2.length()][2];

		//Array bounds are < seq1.length() (not <= ) since both sequences have a blank space @ the start
		//Fill 0th column
		for (int i = 0; i < seq1.length(); i++) {
			memoTable[i][0] = i * this.gap; // base case: j = 0
			predecessorIndexes[i][0][0] = i - 1;
			predecessorIndexes[i][0][1] = 0;
		}
		//Fill 0th row
		for (int j = 0; j < seq2.length(); j++) {
			memoTable[0][j] = j * this.gap; // base case: i = 0
			predecessorIndexes[0][j][0] = 0;
			predecessorIndexes[0][j][1] = j - 1;
		}
		//Set upper left with negative predecessor since it has no predecessor
		predecessorIndexes[0][0][0] = -1;
		predecessorIndexes[0][0][1] = -1;


		//Fill rest of memo table
		for (int j = 1; j < seq2.length(); j++) {
			for (int i = 1; i < seq1.length(); i++) {
				int bothAligned = mismatchPenalty(seq1.charAt(i), seq2.charAt(j)) + memoTable[i - 1][j - 1]; //case1: seq1[i] & seq2[j] aligned with each other
				int seq1WithGap = this.gap + memoTable[i - 1][j]; //case2: seq1 with gap
				int seq2WithGap = this.gap + memoTable[i][j - 1]; //case3: seq2 with gap
				//Calculate the min of 3 values & store predecessors
				if (bothAligned <= seq1WithGap && bothAligned <= seq2WithGap) { //case1 smallest
					memoTable[i][j] = bothAligned;
					predecessorIndexes[i][j][0] = i - 1;
					predecessorIndexes[i][j][1] = j - 1;
				}
				else if (seq1WithGap <= bothAligned && seq1WithGap <= seq2WithGap) { //case2 smallest
					memoTable[i][j] = seq1WithGap;
					predecessorIndexes[i][j][0] = i - 1;
					predecessorIndexes[i][j][1] = j;
				}
				else { //case3 smallest
					memoTable[i][j] = seq2WithGap;
					predecessorIndexes[i][j][0] = i;
					predecessorIndexes[i][j][1] = j - 1;
				}
			}
		}

	}

	private void printTable(int[][] table) {
		for (int[] row : table) {
			for (int value : row) {
				System.out.print(value + "\t");
			}
			System.out.println();
		}
	}

	private void printTable3D(int[][][] table) {
		for (int[][] row : table) {
			for (int[] xyPair : row) {
				System.out.print(Arrays.toString(xyPair) + "\t");
			}
			System.out.println(); //Ends up printing a trailing newline
		}
	}

	//Retrace the memoTable to find the actual alignment, not just the minimum cost
	private void findAlignment(String seq1, String seq2) {
		String seq1Aligned = ""; //Holds the actual sequence with gaps added
		String seq2Aligned = "";

		int i = seq1.length() - 1; //-1 since seq1 & seq2 have leading space
		int j = seq2.length() - 1;

		//Retrace the memoTable calculations. Stops when reaches the start of 1 sequence (so additional gaps may still need to be added to the other)
		while (i > 0 && j > 0) {
			if (memoTable[i][j] - mismatchPenalty(seq1.charAt(i), seq2.charAt(j)) == memoTable[i - 1][j - 1]) { //case1: both aligned
				seq1Aligned = seq1.charAt(i) + seq1Aligned;
				seq2Aligned = seq2.charAt(j) + seq2Aligned;
				i = i - 1;
				j = j - 1;
			}
			else if (memoTable[i][j] - this.gap == memoTable[i - 1][j]) { //case2: seq1 with gap
				seq1Aligned = seq1.charAt(i) + seq1Aligned;
				seq2Aligned = GAP_CHAR + seq2Aligned;
				i = i - 1;
			}
			else if (memoTable[i][j] - this.gap == memoTable[i][j - 1]) { //case3: seq2 with gap
				seq2Aligned = seq2.charAt(j) + seq2Aligned;
				seq1Aligned = GAP_CHAR + seq1Aligned;
				j = j - 1;
			}
		}
		//Now i==0 or j==0 or both. Finish by adding any additional leading gaps to the start of the sequence whoes pointer ISN'T == 0
		while (i > 0) { //Seq1 reached the beginning, print rest of seq2 & add gaps to seq2
			seq1Aligned = seq1.charAt(i) + seq1Aligned;
			seq2Aligned = GAP_CHAR + seq2Aligned;
			i = i - 1;
		}
		while (j > 0) { //Seq2 reached the beginning, print rest of seq1 & add gaps to seq2
			seq2Aligned = seq2.charAt(j) + seq2Aligned;
			seq1Aligned = GAP_CHAR + seq1Aligned;
			j = j - 1;
		}

		System.out.println("\nOptimal Alignment:\n" + seq1Aligned + "\n" + seq2Aligned + "\n\n");
	}

	private int mismatchPenalty(char char1, char char2) {
		if (char1 == char2) { //no mismatch
			return 0;
		}
		else if (consonants.contains(char1) && consonants.contains(char2)) {
			return this.consonantC;
		}
		else if (vowels.contains(char1) && vowels.contains(char2)) {
			return this.vowelV;
		}
		else if (numbers.contains(char1) && numbers.contains(char2))
			return this.numberN;
		
		else
			return this.vowelC;
	}


	public static void main(String[] args) {
		SequenceAlignment sequenceAligner = new SequenceAlignment();

		String[][] testSequences = { { "MEAN", "name" }, { "abc", "ab" }, { "asdc", "gcasa" }, { "abc", "bc" } };
		for (int i = 0; i < testSequences.length; i++) {
			sequenceAligner.calcOptimalAlignment(testSequences[i][0], testSequences[i][1]);
		}
	}

}
