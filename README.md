# Sequence Alignment
Implementation of the classic **Dynamic Programming** problem using the [Needleman–Wunsch algorithm](https://en.wikipedia.org/wiki/Needleman%E2%80%93Wunsch_algorithm) which requires quadratic space & time complexity.

## Problem Statement 
Given 2 sequences, find the minimum cost of aligning the 2 sequences (**case insensitive**).  
Gaps can be inserted to 1 sequence or the other, but incur a penalty.

`2` = **Gap Penalty (δ)**  
If 2 characters are aligned with each other, there may be a **mismatch penalty (α<sub>i j</sub>)**
 - `0` = aligning identical letters
 - `1` = aligning a vowel with a vowel, or a consonant with a consonant
 - `3` = aligning a vowel with a consonant

**Minimum cost =** sum of mismatch & gap penalties (the optimal alignment)

## Optimal Substructure
**There may be multiple optimal paths, but this only finds `1` of them**  
![](images/optimal-substructure.png)

## Runtime
**O(M*N)**  
Storage: also O(M*N)

## Pseudocode
![](images/pseudocode.png)

## Usage
### Requirements & Caveats
- **Alphanumeric characters only** (all others including spaces are sanitized out)
- **Case insensitive**
- `_` (*Underscore character*) represents gaps but only when displaying results (it will be removed & ignored if it's present in a sequence)
- View results in a fixed-width font for the 2 sequences to be lined up
- `predecessorIndexes` is calculated when creating `memoTable` but not used to find the actual alignment, only to show where the values in `memoTable` came from
  - For example: if `predecessorIndexes[4][4]` contains the array `[4, 3]`, it means the value of `memoTable[4][4]` (which is `6`) came from `memoTable[4][3]` (i.e. `case3`, a character in `seq2` was aligned with a gap so it came from the left)
  - `predecessorIndexes[0][0]` contains the array `[-1, -1]` because the upper left corner has no predecessor

### Setup
- Provide 2 strings (edit `testSequences` 2D array) & run `calculateAndPrintOptimalAlignment()`
- Optionally change the penalties by passing in arguments to the non-default constructor  
Currently `vowelVowelMismatchPenalty`, `consonantConsonantMismatchPenalty` & `numberNumberMismatchPenalty` are the same, but all are arbitrary

### Example: aligning "mean" with "name"
![](images/example-mean-name.png)

## Code Notes
- `seq1` & `seq2` have a leading space added (after sanitizing illegal & whitespace characters)  
This is so that `seq1.charAt(i)` & `seq2.charAt(j)` work in the loops & the string indexes match up  
It causes some adjustments for the array sizes. 
 - `memoTable = new int[seq1.length()][seq2.length()]` uses the exact value of `length()` which includes the space
 - Loops like `for(int i=0; i < seq1.length(); i++)` use `i < seq1.length()` to not go out of bounds of the string indexes
 - In `findAlignment()`, `int i = seq1.length() - 1;` & `int j = seq2.length() - 1;` since the 2 sequences have leading spaces & arrays indexes start from `0`
- `findAlignment()` retraces each calculation in the `memoTable` to see where it came from  

## References
- [String Alignment using Dynamic Programming - Gina M. Cannarozzi](http://www.biorecipes.com/DynProgBasic/code.html) to retrace the memo table & find the alignment
