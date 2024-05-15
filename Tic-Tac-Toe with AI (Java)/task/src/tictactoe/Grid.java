package tictactoe;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;


/**
 * Represents a square tic-tac-toe grid.
 */
public class Grid {

    /**
     * Dimensions of the NxN square table.
     */
    public static final int LENGTH = 3;
    /**
     * Total number of cells in the {@link #table}.
     */
    public static final int TOTAL_SYMBOLS = LENGTH * LENGTH;
    /**
     * ASCII border for the top and bottom of the grid.
     */
    private static final String BORDER = "-".repeat(TOTAL_SYMBOLS);
    /**
     * Reference of the 'X' piece.
     */
    public static final Piece X_PIECE = new Piece('X');
    /**
     * Reference of the 'O' piece.
     */
    public static final Piece O_PIECE = new Piece('O');
    /**
     * The number of {@value LENGTH}-in-a-row rows.
     */
    private static final int TOTAL_ROWS = (2* LENGTH) + 1;
    /**
     * The associate cell positions of all {@value LENGTH}-in-a-row rows.
     */
    private static final List<List<int[]>> rows;

    static { // initialise the rows
        int DIAG_DOWN_INDEX = TOTAL_ROWS - 1;
        int DIAG_UP_INDEX = DIAG_DOWN_INDEX - 1;
        rows = new ArrayList<>(TOTAL_ROWS);
        for (int i = 0; i < TOTAL_ROWS; i++) {
            // make all row lists
            rows.add(new ArrayList<>(LENGTH));
        }
        for (int i = 0; i < LENGTH; i++) {
            rows.get(DIAG_DOWN_INDEX).add(new int[]{i, i});
            rows.get(DIAG_UP_INDEX).add(new int[]{(LENGTH-1)-i, i});
            for (int j = 0; j < LENGTH; j++) {
                rows.get(i).add(new int[]{i, j}); // horizontal rows
                rows.get(i+LENGTH).add(new int[]{j, i}); // vertical rows
            }
        }
    }

    /**
     * Array of each cell on the game board.
     */
    private final Cell[][] table;
    /**
     * Current {@link State} of the game.
     */
    private State state;
    /**
     * Positions that result in either 'X' or 'O' winning.
     */
    private final Map<Character, List<int[]>> winMoves;

    /**
     * Check a table index is in the {@link #table} bounds.
     * @param i value to check
     */
    private void checkIndex(int i) {
        if (i < 0 || i >= LENGTH) {
            throw new ArrayIndexOutOfBoundsException("Index is out of bounds for the "+LENGTH+"x"+LENGTH+" grid");
        }
    }

    /**
     * Check a 2D co-ordinate is a valid array.
     * @param position array to check
     */
    private void checkPosition(int[] position) {
        if (position == null) {
            throw new NullPointerException("position cannot be null");
        }
        if (position.length != 2) {
            throw new IndexOutOfBoundsException("position array must be of length 2");
        }
    }

    /**
     * Retrieve the cell from the table at a given index.
     * @param x row
     * @param y column
     * @return symbol value
     */
    public Cell getCell(int y, int x) {
        checkIndex(x);
        checkIndex(y);
        return table[y][x];
    }

    /**
     * Retrieve the cell from the table at a given index.
     * @param position {y, x} co-ordinate of cell
     * @return symbol value
     */
    public Cell getCell(int[] position) {
        checkPosition(position);
        return getCell(position[0], position[1]);
    }

    /**
     * Place either 'X' or 'O' at a given index.
     * @param x row
     * @param y column
     * @param piece new value to set
     */
    public void setCell(int y, int x, Piece piece) {
        if (piece == null) {
            throw new NullPointerException("Cannot set symbol to null");
        }
        if (piece.isEmpty()) {
            throw new NoSuchElementException("Cannot place an empty symbol");
        }
        checkIndex(x);
        checkIndex(y);
        table[y][x].setTo(piece);
        evaluateState(); // process the new grid state
    }

    /**
     * Place either 'X' or 'O' at a given index.
     * @param position {y, x} co-ordinate of cell
     * @param piece new value to set
     */
    public void setCell(int[] position, Piece piece) {
        checkPosition(position);
        setCell(position[0], position[1], piece);
    }

    /**
     * Get the current state of the grid.
     * @return game's status (i.e. win/lose/draw/unfinished)
     */
    public State getState() {
        return state;
    }

    /**
     * Get all locations resulting in a win if a piece was placed there.
     * @return map of any winning positions for 'X' and 'O'
     */
    public Map<Character, List<int[]>> getWinMoves() {
        return winMoves;
    }

    /**
     * Create a {@link Cell} for each input character and place it in the new Grid's {@link #table}.
     * @param symbols list of characters denoting the current grid state, left to right, top to bottom
     */
    public Grid(String symbols) {
        this.table = new Cell[LENGTH][LENGTH];
        if (symbols == null) {
            throw new NullPointerException("Input symbols cannot be null");
        } else if (symbols.length() != TOTAL_SYMBOLS) {
            throw new IndexOutOfBoundsException("Input symbols must have a length of "+TOTAL_SYMBOLS);
        }
        for (int i = 0; i < TOTAL_SYMBOLS; i++) {
            char c = symbols.charAt(i);
            Cell cell = new Cell(c);
            table[i/LENGTH][i%LENGTH] = cell;
        }
        this.winMoves = new HashMap<>(){
            {
                put('X', new ArrayList<>());
                put('O', new ArrayList<>());
            }
        };
        evaluateState(); // find and set the current game state
    }

    /**
     * Create a blank grid.
     */
    public Grid() {
        this("_".repeat(Grid.TOTAL_SYMBOLS));
    }

    /**
     * Get the current board as a string of symbols from left-to-right, top-to-bottom.
     * @return all {@value TOTAL_SYMBOLS} on the board
     */
    public String getSymbols() {
        StringBuilder sb = new StringBuilder(TOTAL_SYMBOLS);
        for (int i = 0; i < TOTAL_SYMBOLS; i++) {
            sb.append(table[i/LENGTH][i%LENGTH].getSymbol());
        }
        return sb.toString();
    }

    /**
     * Create a string representation of the game board.
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(BORDER);
        sb.append("\n");
        for (int i = 0; i < TOTAL_SYMBOLS; i++) {
            int x = i % LENGTH;
            int y = i / LENGTH;
            if (x == 0) sb.append("| ");
            sb.append(getCell(y, x).getSymbol()).append(" ");
            if (x == LENGTH - 1) {
                sb.append("|\n");
            }
        }
        sb.append(BORDER);
        return sb.toString();
    }

    /**
     * Find the next piece to place on the table, X goes first.
     * @return current turn's piece, empty if grid is invalid or game is over
     */
    public Optional<Piece> nextPiece() {
        int xCount = 0;
        int oCount = 0;
        // count number of Xs and Os, if difference is < 1 game is invalid
        for (int i = 0; i < TOTAL_SYMBOLS; i++) {
            int x = i % LENGTH;
            int y = i / LENGTH;
            switch (getCell(y, x).getSymbol()) {
                case 'X' -> xCount++;
                case 'O' -> oCount++;
            }
        }
        if (xCount + oCount == TOTAL_SYMBOLS) return Optional.empty(); // game is finished - no next piece
        Piece next;
        switch (xCount - oCount) {
            case 0 -> next = X_PIECE;
            case 1 -> next = O_PIECE;
            default -> {
                System.out.println("Error - Impossible grid");
                return Optional.empty();
            }
        }
        return Optional.of(next);
    }

    /**
     * Is it the X player's turn?
     */
    public boolean isXTurn() {
        Optional<Piece> optional = nextPiece();
        return optional.map(piece -> piece.equals(X_PIECE)).orElse(false);
    }

    /**
     * All possible game states e.g. win/lose.
     */
    public enum State {
        UNFINISHED("Game not finished"),
        DRAW("Draw"),
        X_WINS("X wins"),
        O_WINS("O wins"),
        UNPROCESSED("Error - state not set"); // default game state -> hasn't been calculated yet

        private final String msg;

        State(String msg) {
            this.msg = msg;
        }

        @Override
        public String toString() {
            return msg;
        }
    }

    /**
     * Return a list of the positions of all empty cells.
     */
    public List<int[]> getEmptyCells() {
        List<int[]> result = new ArrayList<>();
        for (int i = 0; i < TOTAL_SYMBOLS; i++) {
            int x = i % LENGTH;
            int y = i / LENGTH;
            if (getCell(y, x).isEmpty()) {
                result.add(new int[]{y, x});
            }
        }
        return result;
    }

    /**
     * Count the number of the same symbols in a given row.
     * @param currentRow indexes of current row
     * @param lastSymbol track the symbol with the highest frequency
     * @param missing index of the missing symbol in a 2-in-row
     * @return how many of the same symbols in the row
     */
    private int countRow(List<int[]> currentRow, Cell lastSymbol, AtomicInteger missing) {
        int count = 0;
        for (int i = 0; i < LENGTH; i++) {
            // count how many of the same symbol are in a row
            Cell current = getCell(currentRow.get(i));
            if (current.isEmpty()) {
                missing.set(i);
            } else {
                if (current.equals(lastSymbol)) {
                    count++;
                } else {
                    count = 1;
                    lastSymbol.setTo(current);
                }
            }
        }
        return count;
    }

    /**
     * Find and set the grid's current state according to the pieces on the game board.
     */
    public void evaluateState() {
        winMoves.values()
                .stream()
                .filter(list -> !list.isEmpty())
                .forEach(List::clear); // empty all lists
        state = State.UNPROCESSED;
        Cell lastSymbol = new Cell('_');
        boolean isDraw = true;
        for (int r = 0; r < TOTAL_ROWS; r++) {
            AtomicInteger missing = new AtomicInteger(-1);
            int count = countRow(rows.get(r), lastSymbol, missing); // how many in a row
            int missingIndex = missing.get(); // index of missing piece
            if (count == LENGTH) { // game is won
                state = lastSymbol.getWinState().orElseThrow(NoSuchElementException::new);
                break;
            } else if (count == LENGTH-1 && missingIndex != -1) {
                // has one empty slot, both other pieces in the row are the same
                int[] move = rows.get(r).get(missingIndex);
                winMoves.get((lastSymbol.getSymbol())).add(move);
            }
            lastSymbol.setEmpty(); // reset last symbol for next row
            if (isDraw) {
                isDraw = missingIndex == -1; // there were no empty pieces in the row
            }
        }
        if (state == State.UNPROCESSED) state = isDraw ? State.DRAW : State.UNFINISHED;
    }
}
