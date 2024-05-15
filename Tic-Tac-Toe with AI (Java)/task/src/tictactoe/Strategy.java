package tictactoe;

/**
 * An algorithm to generate a player's move.
 */
public abstract class Strategy {

    /**
     * Name of the Strategy.
     */
    private final String name;
    /**
     * Current state of the game.
     */
    protected final Grid grid;

    /**
     * Set the Strategy's name and grid to apply the move to.
     */
    public Strategy(String name, Grid grid) {
        this.name = name;
        this.grid = grid;
    }

    /**
     * Output the Strategy's name.
     */
    protected void printPlayerType() {
        System.out.println("Making move level \""+name+"\"");
    }

    /**
     * Find the location of the best move for a piece.
     * @param toPlace piece to place on the board
     * @return co-ordinate of move to make
     */
    abstract int[] execute(Piece toPlace);
}
