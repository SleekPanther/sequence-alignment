import java.util.*;

public class SequenceAlignment {
	static final int GAP_PENALTY = 2;
	static final int VOWEL_VOWEL_PENALTY = 1;
	static final int CONSONANT_CONSONANT_PENALTY = 1;
	static final int VOWEL_CONSONANT_PENALTY = 3;

	//lowercase uppercase vowels

	String[] vowelsLowerCase = {"a", "e", "i", "o", "u"};
	String[] conconantsLowerCase = {"b", "c", "d", "f", "g", "h", "j", "k", "l", "m", "n", "p", "q", "r", "s", "t", "v", "w", "x", "y", "z"};

	public SequenceAlignment(){

	}

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
	}

}