# Synqrinus Interview Programming challenge
The interview questions and solutions for a Synqrinus interview.

Now as a standard SBT project.

For brevity, here are the outputs for the Coins exercise, for the given values:

| Input | Output |
| --- | --- |
| `653` | `3 pennies, 2 quarters, 3 toonies, 8 total coins` |
| `63` | `3 pennies, 1 dime, 2 quarters, 6 total coins` |
| `132` | `2 pennies, 1 nickel, 1 quarter, 1 loonie, 5 total coins` |

And for the bonus:

| Input | Output |
| --- | --- |
| `653` | `1 bonus coin, 3 toonies, 4 total coins` |
| `63` | `1 dime, 1 bonus coin, 2 total coins` |
| `132` | `1 penny, 1 quarter, 2 bonus coins, 4 total coins` |

</br></br>

And since the answers were already given for the inputs for the Tree exercise,
here's an example for a large, generated tree:

```lisp
Input:
[["A" nil]
 ["B" "A"]
 ["C" "A"]
 ["D" "B"]
 ["E" "A"]
 ["F" "B"]
 ["G" "E"]
 ["H" "D"]
 ["I" "F"]
 ["J" "A"]
 ["K" "E"]
 ["L" "G"]
 ["M" "I"]
 ["N" "K"]
 ["O" "G"]
 ["P" "C"]
 ["Q" "A"]
 ["R" "D"]
 ["S" "M"]
 ["T" "K"]]

Output:
A
├B
│├D
││├H
││└R
│└F
│ └I
│  └M
│   └S
├C
│└P
├E
│├G
││├L
││└O
│└K
│ ├N
│ └T
├J
└Q
```
