import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

public class RandomGamePlayout {

    public ArrayList<Double> win_scores = new ArrayList(Collections.nCopies(64,0.0));

    Game game;

//    int numPlayouts = 400;

    public RandomGamePlayout(Game game) {
        this.game = game;
    }

    public int ChooseMove(Game game) {

        boolean isPlayerTurn = false;
        Game currentGame = new Game(game);

        int numPlayout = 1;
        long time = System.currentTimeMillis();
        long end = time + 5000; //limit time allowed for random playouts to be 5 seconds
        while (System.currentTimeMillis() < end) {

//            System.out.println("===== Playout " + (i+1) + " =====");//<---- for debugging

            ArrayList<Integer> validMoves = currentGame.GetValidMoves(currentGame.gameState, false);
            if (validMoves.size() == 1) {
                win_scores.set(validMoves.get(0), 1000.0);
                break;
            }
            for (Integer move : validMoves) { //do a random playout for each valid move as the initial move

//                System.out.println("AI's initial for random playout: " + move.toString());//<---- for debugging
                currentGame.PlaceMove(move, false);

//                game.DisplayGame(!isPlayerTurn);//<---- for debugging
                int winner = RandomPlayout(currentGame,true);

                if (winner < 0) {
                    currentGame.CalculateScores();
                    if (currentGame.white_score >= currentGame.black_score) {
                        winner = 2;
                    } else {
                        winner = 1;
                    }
                }

                //increment score if it won or tied
                if (winner == 2 || winner == 0) {
                    win_scores.set(move, win_scores.get(move)+1);
                } else { //increment score if lost by 0.001 in case if no other move won
                    win_scores.set(move, win_scores.get(move)+0.001);
                }
                currentGame = new Game(game);
            }
            numPlayout++;
        }

        System.out.println("Number of playouts done: " + numPlayout);
//        System.out.println("----- Win scores -----");<---- for debugging
//        for (int score : win_scores) {//
//            System.out.println(win_scores.indexOf(score)+ ": " + score);
//
//        }

        return win_scores.indexOf(Collections.max(win_scores));

    }

    public int RandomPlayout(Game game, boolean isPlayerTurn) {

        int winner = game.CheckForWin();

        while (winner < 0) {    //loop until the game is done or there are no more moves
//            if (isPlayerTurn) {//<---- for debugging
//                System.out.println("Black's turn");
//            } else {
//                System.out.println("White's turn");
//            }

//            game.DisplayGame(isPlayerTurn);//<---- for debugging

            ArrayList<Integer> validMoves = game.GetValidMoves(game.gameState, isPlayerTurn);
//            for (Integer i : validMoves) {//<---- for debugging
//                System.out.println((i+1));
//            }
//            game.DisplayGame(isPlayerTurn);//<---- for debugging

            if (validMoves.size() == 0) {   //if no available moves
//                System.out.println("No valid moves for player");//<---- for debugging
                isPlayerTurn = !isPlayerTurn;

                if (game.GetValidMoves(game.gameState, isPlayerTurn).size() == 0 ) //if also no moves available for other player
                    break;

                continue;
            }

            Integer nextMove = validMoves.get(new Random().nextInt(validMoves.size()));

//            System.out.println("nextMove: " + (nextMove+1)); //<---- for debugging

            game.PlaceMove(nextMove, isPlayerTurn);

//            game.DisplayGame(!isPlayerTurn);//<---- for debugging

            winner = game.CheckForWin();    //check who won

//            System.out.println(winner);//<---- for debugging

            isPlayerTurn = !isPlayerTurn;
        }

//        System.out.println("Playout done");

        return winner;


    }

}
