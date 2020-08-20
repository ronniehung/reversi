import java.io.IOException;

public class Main {

   public static int PlayGame() throws java.io.IOException {
        Game game = new Game();

        while (game.CheckForWin() == -1) {
            boolean noWhiteMoves = false, noBlackMoves = false;

            //Your turn
//            if (game.GetValidMoves(game.gameState, true).size() > 0) {
//                game.DisplayGame(true);
//
//                int input = game.UserInput();
//                game.PlaceMove(input, true);
//            } else {
//                noBlackMoves = true;
//                System.out.println("No moves for Black");
//            }

            //Heuristics AI's turn
            game.DisplayGame(true);
            if (game.GetValidMoves(game.gameState, true).size() > 0) {
//                RandomGamePlayout playout = new RandomGamePlayout(game);
                HeuristicsGame playout = new HeuristicsGame(game, true);

//                long startTime = System.nanoTime();
                int AImove = playout.ChooseMove(game);
//                long endTime = System.nanoTime();
//                long elapsedTime = endTime - startTime;

//                System.out.println("Time duration: " + TimeUnit.SECONDS.convert(elapsedTime, TimeUnit.NANOSECONDS));

                System.out.println("Heuristics AI's move: " + (AImove+1));
                game.PlaceMove(AImove, true);
            } else {
                noBlackMoves = true;
                System.out.println("No moves for Black");
            }

            game.DisplayGame(false);

            //AI's turn
            if (game.GetValidMoves(game.gameState, false).size() > 0) {
                RandomGamePlayout playout = new RandomGamePlayout(game);
//                HeuristicsGame playout = new HeuristicsGame(game);

//                long startTime = System.nanoTime();
                int AImove = playout.ChooseMove(game);
//                long endTime = System.nanoTime();
//                long elapsedTime = endTime - startTime;

//                System.out.println("Time duration: " + TimeUnit.SECONDS.convert(elapsedTime, TimeUnit.NANOSECONDS));

                System.out.println("AI's move: " + (AImove+1));
                game.PlaceMove(AImove, false);
            } else {
                noWhiteMoves = true;
                System.out.println("No moves for White");
            }

            if (noBlackMoves && noWhiteMoves)
                break;

        }

       game.DisplayGame(false);

        if (game.black_score > game.white_score) {
            System.out.println("Black wins");
            return 1;
        } else if (game.white_score > game.black_score) {
            System.out.println("White wins");
            return  2;
        } else {
            System.out.println("Tie");
        }

        return 0;

    }

    public static void main(String[] args) throws IOException {
//       int numGames = 10;
//
//       int[] stats = new int[3];
//
//       for (int i = 0; i < numGames; i++) {
//           System.out.println("===== Game " + (i+1) + " =====");
//           int winner = PlayGame();
//
//           if (winner == 0)
//               stats[0]++;
//           else if (winner == 1)
//               stats[1]++;
//           else
//               stats[2]++;
//       }
//
//       System.out.println("Black wins: " + stats[1]);
//       System.out.println("White wins: " + stats[2]);
//       System.out.println("Ties: " + stats[0]);

       //uncomment the code above and comment below to see multiple games
         PlayGame();

    }
}
