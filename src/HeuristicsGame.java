import java.util.ArrayList;
import java.util.Collections;
import java.util.Arrays;

public class HeuristicsGame extends RandomGamePlayout {
    Game game;

    boolean isPlayerTurn;

    public ArrayList<Double> win_scores = new ArrayList(Collections.nCopies(64,0.0));

    public HeuristicsGame(Game game) {
        super(game);
        this.game = game;
        this.isPlayerTurn = false;
    }

    public HeuristicsGame(Game game, boolean isPlayerTurn) {
        super(game);
        this.game = game;
        this.isPlayerTurn = isPlayerTurn;
    }

    public int ChooseMove(Game game) {
        Game currentGame = new Game(game);

//        Integer[] oppMoves = new Integer[64];

        int numPlayout = 1;
        long time = System.currentTimeMillis();
        long end = time + 5000; //limit time allowed for random playouts to be 5 seconds
        while (System.currentTimeMillis() < end) {

            ArrayList<Integer> validMoves = currentGame.GetValidMoves(currentGame.gameState, isPlayerTurn);
            if (validMoves.size() == 1) {
                win_scores.set(validMoves.get(0), 1000.0);
                break;
            }
            for (Integer move : validMoves) {

//                System.out.println("AI's initial for random playout: " + move.toString());//<---- for debugging
                currentGame.PlaceMove(move, isPlayerTurn);

//                game.DisplayGame(!isPlayerTurn);//<---- for debugging
                int winner = RandomPlayout(currentGame,!isPlayerTurn);

                if (winner < 0) {
                    currentGame.CalculateScores();
                    if (currentGame.black_score >= currentGame.white_score) {
                        winner = 1;
                    } else {
                        winner = 2;
                    }
                }

                double weight = 1.00;
                //increment score if it won or tied
                if (winner == 1 || winner == 0) {
                    win_scores.set(move, (win_scores.get(move)+1));

                }
//                else if (winner == 0) {
//                    win_scores.set(move, (win_scores.get(move)+1));
//                }
                else {
                    win_scores.set(move, win_scores.get(move)+0.001);
                }

                if ((move % 8) == 0 || ((move+1) % 8) == 0 || move < 8 || move > 55) { //edge moves
                    weight *= 1.25;
                    if (move == 0 || move == 7 || move == 56 || move == 63) // corner moves
                        weight *= 1.75;
                    if (move == 1 || move == 8 || move == 9 || move == 6 || move == 14 || move == 15 || move == 48 || move == 49 || move == 57 || move == 54 || move == 55 || move == 62) //moves adjacent to corners
                        weight = 0.5;
                    win_scores.set(move, (win_scores.get(move)*weight));
                }
//
                currentGame = new Game(game);
            }

            numPlayout++;
        }

        System.out.println("Number of playouts done: " + numPlayout);
//        System.out.println("----- Win scores -----");//<---- for debugging
//        for (double score : win_scores) {
//            System.out.println(win_scores.indexOf(score)+ ": " + score);
//
//        }

        return win_scores.indexOf(Collections.max(win_scores));
    }
}

