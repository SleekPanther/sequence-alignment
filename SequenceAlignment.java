import java.util.*;

public class SequenceAlignment {
	static final int GAP_PENALTY = 2;
	static final int VOWEL_VOWEL_PENALTY = 1;
	static final int CONSONANT_CONSONANT_PENALTY = 1;
	static final int VOWEL_CONSONANT_PENALTY = 3;

	//lowercase uppercase vowels

	String[] vowelsLowerCase = {"a", "e", "i", "o", "u"};
	String[] conconantsLowerCase = {"b", "c", "d", "f", "g", "h", "j", "k", "l", "m", "n", "p", "q", "r", "s", "t", "v", "w", "x", "y", "z"};

	int[][] memoTable;
	int[][][] predecessorIndexes;


	public SequenceAlignment(){
	}

	public void calcOptimalAlignment(String seq1, String seq2){
		seq1 = seq1.trim();		//trim any whitespace
		seq2 = seq2.trim();

		seq1 = " " + seq1;		//prepend a space @ the start. Allows for easier calls to mismatchPenalty() & array boundaryies for size of memoTalbe to be "<" instead of "<="
		seq2 = " " + seq2;

		memoTable = new int[seq1.length()][seq2.length()];
		predecessorIndexes = new int[seq1.length()][seq2.length()][2];

		//Array bounds are < seq1.length() (not <= ) since both sequences have a blank space @ the start
		//Fill 0th column
		for(int i=0; i<seq1.length(); i++){
			memoTable[i][0] = i * GAP_PENALTY;		// base case: j = 0
		}
		//Fill 0th row
		for(int j=0; j<seq2.length(); j++){
			memoTable[0][j] = j * GAP_PENALTY;		// base case: i = 0
		}

		//Fill rest of memo table
		for(int j=1; j<seq2.length(); j++){
			for(int i=1; i<seq1.length(); i++){
				int case1 = mismatchPenalty(seq1.charAt(i)+"", seq2.charAt(j)+"") + memoTable[i-1][j-1];	//case1: seq1[i] & seq2[j] aligned with each other
				int case2 = GAP_PENALTY+memoTable[i-1][j];		//case2: seq1 with gap
				int case3 = GAP_PENALTY+memoTable[i][j-1];		//case3: seq2 with gap
				//Calculate the min of 3 values & store predecessors
				if(case1<=case2 && case1<=case3){		//case1 smallest
					memoTable[i][j] = case1;
					predecessorIndexes[i][j][0] = i-1;
					predecessorIndexes[i][j][1] = j-1;
					System.out.println("case1: "+case1);
				}
				else if(case2<=case1 && case2<=case3){	//case2 smallest
					memoTable[i][j] = case2;
					predecessorIndexes[i][j][0] = i-1;
					predecessorIndexes[i][j][1] = j;
					System.out.println("case2: "+case2);
				}
				else{									//case3 smallest
					memoTable[i][j] = case3;
					predecessorIndexes[i][j][0] = i;
					predecessorIndexes[i][j][1] = j-1;
					System.out.println("case3: "+case3);
				}
			}
		}

		System.out.println("Memoization table");
		printTable(memoTable);
		System.out.println("\nPredecessor table (where the values came from)");
		printTable3D(predecessorIndexes);

	}

	private void printTable(int[][] table){
		for(int[] row : table){
			System.out.print(row[0]);
			for(int j=1; j<row.length; j++){
				System.out.print("\t"+row[j]);
			}
			System.out.println();
		}
	}

	private void printTable3D(int[][][] table){
		for(int[][] row : table){
			for(int[] xyPair : row){
				System.out.print("["+xyPair[0] + ", "+xyPair[1]+"]\t");
			}
			System.out.println();		//Ends up printing a trailing newline
		}
	}
	private void findAlignment(){

	}

	//consider changing to char method
	public int mismatchPenalty(String char1, String char2){
		char1 = char1.trim().toLowerCase();
		char2 = char2.trim().toLowerCase();
		
		if(char1.equals(char2)){	//no mismatch
			return 0;
		}
		else if(Arrays.asList(conconantsLowerCase).contains(char1) && Arrays.asList(conconantsLowerCase).contains(char2)){
			return CONSONANT_CONSONANT_PENALTY;
		}
		else if(Arrays.asList(vowelsLowerCase).contains(char1) && Arrays.asList(vowelsLowerCase).contains(char2)){
			return VOWEL_VOWEL_PENALTY;
		}
		return VOWEL_CONSONANT_PENALTY;
	}


	public static void main(String[] args) {
		SequenceAlignment sequenceAligner = new SequenceAlignment();

		System.out.println(sequenceAligner.mismatchPenalty(" a  ", " A            "));
		System.out.println(sequenceAligner.mismatchPenalty(" a  ", " I            "));
		System.out.println(sequenceAligner.mismatchPenalty("b", "x"));
		System.out.println(sequenceAligner.mismatchPenalty("a", "z"));

		String testString1 = "mean";
		String testString2 = "name";
		sequenceAligner.calcOptimalAlignment(testString1, testString2);
	}

}