package tictactoe;

import java.util.NoSuchElementException;

/**
 * Used to generate and process a full game tree, finding the best possible move.
 */
public class Minimax {
    /**
     * Starting value of beta, greater than maximum possible score.
     */
    private static final int POSITIVE_INFINITY = 10;
    /**
     * Starting value of alpha, less than minimum possible score.
     */
    private static final int NEGATIVE_INFINITY = POSITIVE_INFINITY * -1;
    /**
     * The root node of the game tree.
     */
    private final Node root;

    /**
     * Initialise the root node with the starting values of alpha and beta.
     * @param grid the current game board
     */
    public Minimax(Grid grid) {
        this.root = new Node(grid, NEGATIVE_INFINITY, POSITIVE_INFINITY);
    }

    /**
     * Find the co-ordinates of the best possible move for the current player.
     * @return {y, x} array of the move's position on the grid
     */
    public int[] getBestMove() {
        root.compute();
        return root.bestMove;
    }

    /**
     * Dictates the heuristic value of each possible {@link Grid.State}.
     */
    public enum Score {
        MAX_SCORE(Math.floorDiv(POSITIVE_INFINITY, 10)),
        MIN_SCORE(MAX_SCORE.value * -1),
        NEUTRAL(0);

        private final int value;

        Score(int value) {
            this.value = value;
        }

        /**
         * Get the value of a given grid state.
         * @return integer representing how beneficial the board is for player 'X'
         */
        public static int getValue(Grid.State state) {
            return switch (state) {
                case X_WINS -> MAX_SCORE.value;
                case O_WINS -> MIN_SCORE.value;
                default -> NEUTRAL.value;
            };
        }

    }

    /**
     * Represents a node of the game tree.
     */
    private class Node {
        /**
         * Current game board.
         */
        private final Grid grid;
        /**
         * Value of the current best choice for the maximising player.
         */
        private int alpha;
        /**
         * Value of the current best choice for the minimising player.
         */
        private int beta;
        /**
         * Move associated with either alpha or beta.
         */
        private int[] bestMove;

        /**
         * Create a new node of the game tree.
         */
        private Node(Grid grid, int alpha, int beta) {
            this.grid = grid;
            this.alpha = alpha;
            this.beta = beta;
        }

        /**
         * Fully explore the game tree, updating each node with the best possible move from their children.
         * @return the best score found at the current depth
         */
        private int compute() {
            if (grid.getState() != Grid.State.UNFINISHED) {
                return Score.getValue(grid.getState());
            }
            Piece piece = grid.nextPiece().orElseThrow(NoSuchElementException::new);
            boolean isMax = piece.equals(Grid.X_PIECE);
            for (int[] move : grid.getEmptyCells()) { // for every possible move
                // create and process child node
                Grid childGrid = new Grid(grid.getSymbols());
                childGrid.setCell(move, piece);
                Node child = new Node(childGrid, alpha, beta);
                int value = child.compute();
                // update either alpha (max node) or beta (min node)
                if (isMax && value > alpha) {
                    bestMove = move;
                    alpha = value;
                } else if (!isMax && value < beta) {
                    bestMove = move;
                    beta = value;
                }
                /* alpha can only increase, beta can only decrease
                 * if alpha exceeds beta, this value cannot be propagated up the tree
                 */
                if (beta <= alpha) break;
            }
            return isMax ? alpha : beta; // associated best value for this player
        }
    }
}
