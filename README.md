# Sequence Alignment
Implementation of the classic **Dynamic Progogramming** problem. Applications include Unix `diff`, DNA sequence matching, "did you mean" search suggestions

## Problemm Statement 
Given 2 sequences, find the minimum cost of aligning the 2 sequences  
Gaps can be inserted to 1 sequence or the other, but not at the same time  
`2` = **Gap Penalty (δ)**  
If 2 characters are aligned with each other, there may be a **mismatch penalty (α<sub>i j</sub>)**
 - `0` = aligning identical letters
 - `1` = aligning a vowel with a vowel, or a consonant with a consonant
 - `3` = aligning a vowel with a consonant

**Minimum cost =** sum of mismatch & gap penalties (the optimal alignment)

## Optimal Substructure


## Pseudocode


## Usage
### Requirements & Caveats
- **Letters only** (no spaces, numbers or special characters)
- `_` (*Underscore character*) is reserved to represent
- View results in a fixed-width font for the 2 sequences to be lined up
- Leading & trailing whitespace is trimmed
- **Uppercase / Lowercase is ignored** (all converted to lowercase)

### Setup
- Provide 2 strings (edit `testSequences` 2D array)
- Optionally change the `GAP_PENALTY` and the mismatch penalties  
Currently `VOWEL_VOWEL_PENALTY` and `CONSONANT_CONSONANT_PENALTY` are the same, but are arbitrary

## Code Notes

## References
- [String Alignment using Dynamic Programming - Gina M. Cannarozzi](http://www.biorecipes.com/DynProgBasic/code.html) to retrace the memo table & find the alignment
