# Noughts & Crosses with AI
A CLI game, complete with a computer AI.


## Prerequisites
This program is written in Java 17.


## Features
- player customisation, choose player type (either a user or computer player) for both "X" and "O"
- win state analysis, game loops until a player wins or no more moves can be made
- command line output, the current state of the board is represented in an ASCII table

## Getting Started
Run Main.java:

```bash
java Main
```
Then input the desired mode for both players:
```
 usage: start player_X_mode player_O_mode
       player_mode: either a user or an AI level (user|easy|medium|hard)
```      

### Example
```
Input command: > start easy medium
---------
|       |
|       |
|       |
---------
Making move level "easy"
---------
|     X |
|       |
|       |
---------
...
Making move level "medium"
---------
| X   X |
| O O O |
| X     |
---------
O wins
Input command: > exit
```

## License
[MIT](https://opensource.org/license/mit/)
