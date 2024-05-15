package tictactoe;

import java.util.NoSuchElementException;
import java.util.Optional;

/**
 * Represents a piece that can exist on the table.
 */
public class Piece {


    /**
     * Character representing the piece.
     */
    protected char symbol;

    /**
     * Get the ASCII representation of the piece.
     */
    public char getSymbol() {
        return symbol;
    }

    /**
     * Check input is a valid character.
     * @param symbol must be `X`, `O` or `_` (representing an empty tile)
     */
    protected static char validSymbol(char symbol) {
        return switch (symbol) {
            case 'X', 'O' -> symbol;
            case '_', ' ' -> ' ';
            default -> throw new NoSuchElementException("\""+symbol+"\" is not a valid Tile");
        };

    }

    /**
     * Does this contain a game piece?
     */
    public boolean isEmpty() {
        return symbol == ' ';
    }

    /**
     * Create a Piece object from 'X', 'O' or '_' (empty).
     */
    Piece(char symbol) {
        this.symbol = validSymbol(symbol);
    }

    /**
     * If the piece is not empty, get the symbol's associated win state.
     */
    protected Optional<Grid.State> getWinState() {
        return switch (symbol) {
            case 'X' -> Optional.of(Grid.State.X_WINS);
            case 'O' -> Optional.of(Grid.State.O_WINS);
            default -> Optional.empty();
        };
    }

    /**
     * If the piece is not empty, get the opposing player.
     */
    public Piece getOpposite() {
        return switch (symbol) {
            case 'X' -> new Piece('O');
            case 'O' -> new Piece('X');
            default -> throw new NoSuchElementException("Empty has no opposite player");
        };
    }

    /**
     * Pieces are equal if they hold the same {@link #symbol}.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true; // check identity
        if (o instanceof Piece) {
            return this.symbol == ((Piece) o).symbol;
        }
        return false;
    }

    @Override
    public String toString() {
        return "Piece("+symbol+")";
    }


}