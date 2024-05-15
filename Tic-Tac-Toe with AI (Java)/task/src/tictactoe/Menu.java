package tictactoe;

import java.util.Scanner;

/**
 * Takes commands to run a new game of tic-tac-toe or exit the program.
 */
public class Menu {

    /**
     * Message for invalid input.
     */
    private static final String ERROR = "Bad parameters!";
    private static final Scanner scan = new Scanner(System.in);

    /**
     * Includes all implemented controller types for players.
     */
    public enum Player {
        USER, EASY, MEDIUM, HARD;
    }

    /**
     * Get user input, terminating with escape.
     */
    private static String getCommand() {
        System.out.print("Input command: ");
        if (scan.hasNextLine()) {
            return scan.nextLine().trim().toUpperCase();
        }
        return "";
    }

    /**
     * Allows user to run game(s) or exit the program.
     */
    public void loop() {
        while (true) {
            String command = getCommand();
            if ("EXIT".equals(command)) break;
            String[] parameters = command.split("\\s");
            boolean isError = parameters.length != 3 && parameters[0].equals("START");
            if (!isError) {
                try {
                    Player p1 = Player.valueOf(parameters[1]);
                    Player p2 = Player.valueOf(parameters[2]);
                    Game game = new Game();
                    game.play(p1, p2);
                } catch (IllegalArgumentException e) {
                    isError = true;
                }
            }
            if (isError) System.out.println(ERROR);
        }
    }
}
