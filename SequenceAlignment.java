import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class SequenceAlignment {
	static final String GAP_CHAR = "_"; 	//Only For printing the final alignment

	private static final Set<Character> vowels = new HashSet<>(Arrays.asList(new Character[] { 'a', 'e', 'i', 'o', 'u' }));
	private static final Set<Character> consonants = new HashSet<>(Arrays.asList(new Character[] { 'b', 'c', 'd', 'f', 'g', 'h', 'j', 'k', 'l', 'm', 'n', 'p', 'q', 'r', 's', 't', 'v', 'w', 'x', 'y', 'z' }));
	private static final Set<Character> numbers = new HashSet<>(Arrays.asList(new Character[] { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9' }));

	private int[][] memoTable;
	private int[][][] predecessorIndexes;	//stores the index where the value at memoTable[i][j] came from (diagonal, above or left)

	private int gapPenalty;
	private int vowelVowelMismatchPenalty;
	private int consonantConsonantMismatchPenalty;
	private int vowelConsonantMismatchPenalty;
	private int numberNumberMismatchPenalty;


	public SequenceAlignment(int gapPenalty, int vowelVowelMismatchPenalty, int consonantConsonantMismatchPenalty, int vowelConsonantMismatchPenalty, int numberNumberMismatchPenalty) {
		this.gapPenalty = gapPenalty;
		this.vowelVowelMismatchPenalty = vowelVowelMismatchPenalty;
		this.consonantConsonantMismatchPenalty = consonantConsonantMismatchPenalty;
		this.vowelConsonantMismatchPenalty = vowelConsonantMismatchPenalty;
		this.numberNumberMismatchPenalty = numberNumberMismatchPenalty;
	}

	public SequenceAlignment() {
		this(2, 1, 1, 3, 1);
	}

	public void calculateAndPrintOptimalAlignment(String seq1, String seq2){
		calcOptimalAlignment(seq1, seq2, true);
	}

	public void calcOptimalAlignment(String sequence1Original, String sequence2Original, boolean printResults) {
		String seq1 = sanitizeSequence(sequence1Original);
		String seq2 = sanitizeSequence(sequence2Original);

		//Initialize 2D arrays for memoization
		memoTable = new int[seq1.length()][seq2.length()];
		predecessorIndexes = new int[seq1.length()][seq2.length()][2];

		//Array bounds are < seq1.length() (not <= ) since both sequences have a blank space @ the start
		//Fill 0th column
		for (int i = 0; i < seq1.length(); i++) {	// base case: j = 0
			memoTable[i][0] = i * this.gapPenalty;
			predecessorIndexes[i][0][0] = i - 1;
			predecessorIndexes[i][0][1] = 0;
		}
		//Fill 0th row
		for (int j = 0; j < seq2.length(); j++) {	// base case: i = 0
			memoTable[0][j] = j * this.gapPenalty;
			predecessorIndexes[0][j][0] = 0;
			predecessorIndexes[0][j][1] = j - 1;
		}
		//Set upper left with negative predecessor since it has no predecessor
		predecessorIndexes[0][0][0] = -1;
		predecessorIndexes[0][0][1] = -1;


		//Fill rest of memo table
		for (int j = 1; j < seq2.length(); j++) {
			for (int i = 1; i < seq1.length(); i++) {
				int alignedCharWithCharPenalty = mismatchPenalty(seq1.charAt(i), seq2.charAt(j)) + memoTable[i - 1][j - 1];	//case1: seq1[i] & seq2[j] aligned with each other
				int seq1CharWithGap = this.gapPenalty + memoTable[i - 1][j];		//case2: seq1 with gap
				int seq2CharWithGap = this.gapPenalty + memoTable[i][j - 1];		//case3: seq2 with gap
				//Calculate the min of 3 values & store predecessors
				if (alignedCharWithCharPenalty <= seq1CharWithGap && alignedCharWithCharPenalty <= seq2CharWithGap) {			//case1 is the min
					memoTable[i][j] = alignedCharWithCharPenalty;
					predecessorIndexes[i][j][0] = i - 1;
					predecessorIndexes[i][j][1] = j - 1;
				}
				else if (seq1CharWithGap <= alignedCharWithCharPenalty && seq1CharWithGap <= seq2CharWithGap) {	//case2 is the min
					memoTable[i][j] = seq1CharWithGap;
					predecessorIndexes[i][j][0] = i - 1;
					predecessorIndexes[i][j][1] = j;
				}
				else {	//case3 is the min
					memoTable[i][j] = seq2CharWithGap;
					predecessorIndexes[i][j][0] = i;
					predecessorIndexes[i][j][1] = j - 1;
				}
			}
		}

		if(printResults){
			System.out.println("Aligning \""+sequence1Original+"\" with \""+sequence2Original+"\"");
			System.out.println("Memoization table");
			printTable(memoTable);
			System.out.println("\nPredecessor table (where the values came from)");	
			printTable3D(predecessorIndexes);

			int minimumPenalty = memoTable[sequence1Original.length()][sequence2Original.length()];
	 		System.out.println("\n" + minimumPenalty + "\t is the Minimum penalty for aligning \""+sequence1Original+"\" with \""+sequence2Original+"\"");
			findAlignment(seq1, seq2, memoTable);
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

	private void printTable3D(int[][][] table3D) {
		for (int[][] row : table3D) {
			for (int[] xyPair : row) {
				System.out.print(Arrays.toString(xyPair) + "\t");
			}
			System.out.println();
		}
	}

	//Retrace the memoTable to find the actual alignment, not just the minimum cost
	private void findAlignment(String seq1, String seq2, int[][] memoTable) {
		String seq1Aligned = ""; 	//Holds the actual sequence with gaps added
		String seq2Aligned = "";

		int i = seq1.length() - 1; //-1 since seq1 & seq2 have leading space
		int j = seq2.length() - 1;

		//Retrace the memoTable calculations. Stops when reaches the start of 1 sequence (so additional gaps may still need to be added to the other)
		while (i > 0 && j > 0) {
			if (memoTable[i][j] - mismatchPenalty(seq1.charAt(i), seq2.charAt(j)) == memoTable[i - 1][j - 1]) { //case1: both aligned
				seq1Aligned = seq1.charAt(i) + seq1Aligned;
				seq2Aligned = seq2.charAt(j) + seq2Aligned;
				i--;
				j--;
			}
			else if (memoTable[i][j] - this.gapPenalty == memoTable[i - 1][j]) { //case2: seq1 with gap
				seq1Aligned = seq1.charAt(i) + seq1Aligned;
				seq2Aligned = GAP_CHAR + seq2Aligned;
				i--;
			}
			else if (memoTable[i][j] - this.gapPenalty == memoTable[i][j - 1]) { //case3: seq2 with gap
				seq2Aligned = seq2.charAt(j) + seq2Aligned;
				seq1Aligned = GAP_CHAR + seq1Aligned;
				j--;
			}
		}
		//Now i==0 or j==0 or both. Finish by adding any additional leading gaps to the start of the sequence whose pointer ISN'T == 0
		while (i > 0) {		//Seq1 reached the beginning, print rest of seq2 & add gaps to seq2
			seq1Aligned = seq1.charAt(i) + seq1Aligned;
			seq2Aligned = GAP_CHAR + seq2Aligned;
			i--;
		}
		while (j > 0) {		//Seq2 reached the beginning, print rest of seq1 & add gaps to seq2
			seq2Aligned = seq2.charAt(j) + seq2Aligned;
			seq1Aligned = GAP_CHAR + seq1Aligned;
			j--;
		}

		System.out.println("\nOptimal Alignment:\n" + seq1Aligned + "\n" + seq2Aligned + "\n\n");
	}

	private int mismatchPenalty(char char1, char char2) {
		if (char1 == char2) {
			return 0;
		}
		else if (consonants.contains(char1) && consonants.contains(char2)) {
			return this.consonantConsonantMismatchPenalty;
		}
		else if (vowels.contains(char1) && vowels.contains(char2)) {
			return this.vowelVowelMismatchPenalty;
		}
		else if (numbers.contains(char1) && numbers.contains(char2)){
			return this.numberNumberMismatchPenalty;
		}
		return this.vowelConsonantMismatchPenalty;
	}

	private String sanitizeSequence(String sequence){
		return " " + sequence		//prepend a space @ the start. Allows for easier calls to mismatchPenalty() & array boundaries for size of memoTalbe to be "<" instead of "<="
				.toLowerCase()
				.replaceAll("[^a-z0-9]", "");	//remove all characters other than letters & digits (trims surrounding whitespace & removes spaces too). a-z matches the lowercase alphabet since previous line guarantees lowercase
	}


	public static void main(String[] args) {
		String[][] testSequences = {
			{ "MEAN", "name" },		//case insensitivity test
			{ "abc", "ab" },
			{ "asdc", "gcasa" },
			{ "abc", "bc" },
			{ "ab", "zabz" },
			{ "ab", "1ab" },
			{ "2ab", "1ab1" },
		};

		SequenceAlignment sequenceAligner = new SequenceAlignment();
		for (int i = 0; i < testSequences.length; i++) {
			sequenceAligner.calculateAndPrintOptimalAlignment(testSequences[i][0], testSequences[i][1]);
		}
	}

}
