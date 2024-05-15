package tictactoe;

/**
 * Represents a cell on the table, allows its symbol replaced when a move is made.
 */
public class Cell extends Piece {

    /**
     * Set this Cell's symbol to that of another piece.
     */
    void setTo(Piece piece) {
        if (piece == null) {
            throw new NullPointerException("Cannot set "+this+" to a null Piece");
        }
        this.symbol = piece.getSymbol();
    }

    /**
     * Set this Cell's symbol to that of another cell.
     */
    void setTo(Cell other) {
        if (other == null) {
            throw new NullPointerException("Cannot set "+this+" to a null Cell");
        }
        this.symbol = other.symbol;
    }

    /**
     * Set this Cell's symbol to an empty tile.
     */
    void setEmpty() { this.symbol = ' '; }

    /**
     * Create a cell from 'X', 'O' or '_' (empty).
     */
    Cell(char symbol) { super(symbol); }

    /**
     * Cells are equal if they hold the same {@link #symbol}.
     * Cells are not equal to Pieces.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true; // check identity
        if (o instanceof Cell) {
            return symbol == ((Cell) o).symbol;
        }
        return false;
    }

    @Override
    public String toString() {
        return "Cell("+symbol+")";
    }
}
