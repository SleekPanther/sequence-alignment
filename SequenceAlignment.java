import java.util.*;

public class SequenceAlignment {
	static final int GAP_PENALTY = 2;
	static final int VOWEL_VOWEL_PENALTY = 1;
	static final int CONSONANT_CONSONANT_PENALTY = 1;
	static final int VOWEL_CONSONANT_PENALTY = 3;

	static final String GAP_CHAR = "_";		//For printing the final alignment

	Character[] vowels = new Character[] {'a', 'e', 'i', 'o', 'u'};
	Character[] consonants = new Character[] {'b', 'c', 'd', 'f', 'g', 'h', 'j', 'k', 'l', 'm', 'n', 'p', 'q', 'r', 's', 't', 'v', 'w', 'x', 'y', 'z'};

	int[][] memoTable;
	int[][][] predecessorIndexes;	//stored index where the value @ memoTable[i][j] came from (diagonal, above or left)

	//Creates the memo table for the optimal alignment
	public void calcOptimalAlignment(String seq1, String seq2){
		seq1 = seq1.toLowerCase();
		seq2 = seq2.toLowerCase();

		seq1 = seq1.trim();		//trim any whitespace
		seq2 = seq2.trim();

		seq1 = seq1.replaceAll(" ", "");	//Remove spaces
		seq2 = seq2.replaceAll(" ", "");

		seq1 = " " + seq1;		//prepend a space @ the start. Allows for easier calls to mismatchPenalty() & array boundaryies for size of memoTalbe to be "<" instead of "<="
		seq2 = " " + seq2;

		//Initialize 2D arrays for memoization
		memoTable = new int[seq1.length()][seq2.length()];
		predecessorIndexes = new int[seq1.length()][seq2.length()][2];

		//Array bounds are < seq1.length() (not <= ) since both sequences have a blank space @ the start
		//Fill 0th column
		for(int i=0; i<seq1.length(); i++){
			memoTable[i][0] = i * GAP_PENALTY;		// base case: j = 0
			predecessorIndexes[i][0][0] = i-1;
			predecessorIndexes[i][0][1] = 0;
		}
		//Fill 0th row
		for(int j=0; j<seq2.length(); j++){
			memoTable[0][j] = j * GAP_PENALTY;		// base case: i = 0
			predecessorIndexes[0][j][0] = 0;
			predecessorIndexes[0][j][1] = j-1;
		}
		//Set upper left with negative predecessor since it has no predecessor
		predecessorIndexes[0][0][0] = -1;
		predecessorIndexes[0][0][1] = -1;


		//Fill rest of memo table
		for(int j=1; j<seq2.length(); j++){
			for(int i=1; i<seq1.length(); i++){
				int bothAligned = mismatchPenalty(seq1.charAt(i), seq2.charAt(j)) + memoTable[i-1][j-1];	//case1: seq1[i] & seq2[j] aligned with each other
				int seq1WithGap = GAP_PENALTY+memoTable[i-1][j];		//case2: seq1 with gap
				int seq2WithGap = GAP_PENALTY+memoTable[i][j-1];		//case3: seq2 with gap
				//Calculate the min of 3 values & store predecessors
				if(bothAligned<=seq1WithGap && bothAligned<=seq2WithGap){		//case1 smallest
					memoTable[i][j] = bothAligned;
					predecessorIndexes[i][j][0] = i-1;
					predecessorIndexes[i][j][1] = j-1;
				}
				else if(seq1WithGap<=bothAligned && seq1WithGap<=seq2WithGap){	//case2 smallest
					memoTable[i][j] = seq1WithGap;
					predecessorIndexes[i][j][0] = i-1;
					predecessorIndexes[i][j][1] = j;
				}
				else{									//case3 smallest
					memoTable[i][j] = seq2WithGap;
					predecessorIndexes[i][j][0] = i;
					predecessorIndexes[i][j][1] = j-1;
				}
			}
		}

		System.out.println("Memoization table");
		printTable(memoTable);
		System.out.println("\nPredecessor table (where the values came from)");
		printTable3D(predecessorIndexes);

		System.out.println("\n" + memoTable[seq1.length()-1][seq2.length()-1] + "\t is the Minimum penalty for aligning \""+seq1.substring(1, seq1.length()) +"\" & \""+seq2.substring(1, seq2.length()) +"\"");
		findAlignment(seq1, seq2);
	}

	private void printTable(int[][] table){
		for(int[] row : table){
			for(int value : row){
				System.out.print(value + "\t");
			}
			System.out.println();
		}
	}

	private void printTable3D(int[][][] table){
		for(int[][] row : table){
			for(int[] xyPair : row){
				System.out.print(Arrays.toString(xyPair) + "\t");
			}
			System.out.println();		//Ends up printing a trailing newline
		}
	}

	//Retrace the memoTable to find the actual alignment, not just the minimum cost
	private void findAlignment(String seq1, String seq2){
		String seq1Aligned = "";		//Holds the actual sequence with gaps added
		String seq2Aligned = "";

		int i = seq1.length()-1;	//-1 since seq1 & seq2 have leading space
		int j = seq2.length()-1;

		//Retrace the memoTable calculations. Stops when reaches the start of 1 sequence (so additional gaps may still need to be added to the other)
		while(i>0 && j>0){
			if(memoTable[i][j] - mismatchPenalty(seq1.charAt(i), seq2.charAt(j)) == memoTable[i-1][j-1]){		//case1: both aligned
				seq1Aligned = seq1.charAt(i) + seq1Aligned;
				seq2Aligned = seq2.charAt(j) + seq2Aligned;
				i=i-1;
				j=j-1;
			}
			else if(memoTable[i][j] - GAP_PENALTY == memoTable[i-1][j]){		//case2: seq1 with gap
				seq1Aligned = seq1.charAt(i) + seq1Aligned;
				seq2Aligned = GAP_CHAR + seq2Aligned;
				i=i-1;
			}
			else if(memoTable[i][j] - GAP_PENALTY == memoTable[i][j-1]){		//case3: seq2 with gap
				seq2Aligned = seq2.charAt(j) + seq2Aligned;
				seq1Aligned = GAP_CHAR + seq1Aligned;
				j=j-1;
			}
		}
		//Now i==0 or j==0 or both. Finish by adding any additional leading gaps to the start of the sequence whoes pointer ISN'T == 0
		while(i>0){		//Seq1 reached the beginning, print rest of seq2 & add gaps to seq2
			seq1Aligned = seq1.charAt(i) + seq1Aligned;
			seq2Aligned = GAP_CHAR + seq2Aligned;
			i=i-1;
		}
		while(j>0){		//Seq2 reached the beginning, print rest of seq1 & add gaps to seq2
			seq2Aligned = seq2.charAt(j) + seq2Aligned;
			seq1Aligned = GAP_CHAR + seq1Aligned;
			j=j-1;
		}

		System.out.println("\nOptimal Alignment:\n" +seq1Aligned+"\n"+seq2Aligned + "\n\n");
	}

	public int mismatchPenalty(char char1, char char2){
		if(char1==char2){		//no mismatch
			return 0;
		}
		else if(Arrays.asList(consonants).contains(char1) && Arrays.asList(consonants).contains(char2)){
			return CONSONANT_CONSONANT_PENALTY;
		}
		else if(Arrays.asList(vowels).contains(char1) && Arrays.asList(vowels).contains(char2)){
			return VOWEL_VOWEL_PENALTY;
		}
		return VOWEL_CONSONANT_PENALTY;
	}


	public static void main(String[] args) {
		SequenceAlignment sequenceAligner = new SequenceAlignment();

		String[][] testSequences = {{"MEAN", "name"},
									{"abc", "ab"},
									{"asdc", "gcasa"},
									{"abc", "bc"}
									};
		for(int i=0; i< testSequences.length; i++){
			sequenceAligner.calcOptimalAlignment(testSequences[i][0], testSequences[i][1]);
		}
	}

}