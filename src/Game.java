import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.lang.Math;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;


public class Game implements Cloneable {

    enum State {
        W,
        B,
        BLANK
    }

    public HashMap<String, Integer> moveDirections = new HashMap<String, Integer>();

    int[] tileChangeArray = new int[8];

    //initialize game board state with blank values
    public ArrayList<State> gameState = new ArrayList<State>(Collections.nCopies(64, State.BLANK));

    public int black_score, white_score;

    public Game() {
        //initialize game board state with centre values
        gameState.set(27, State.B);
        gameState.set(28, State.W);
        gameState.set(35, State.W);
        gameState.set(36, State.B);
        setMoveDirections(moveDirections);

    }

    public Game(Game game) {
        setMoveDirections(moveDirections);
        this.gameState = (ArrayList<State>) game.gameState.clone();
        this.black_score = game.black_score;
        this.white_score = game.white_score;

    }

    public void setMoveDirections(HashMap<String, Integer> moveDirections) {
        moveDirections.put("top", -8);
        moveDirections.put("top right", -7);
        moveDirections.put("right", 1);
        moveDirections.put("down right", 9);
        moveDirections.put("down", 8);
        moveDirections.put("down left", 7);
        moveDirections.put("left", -1);
        moveDirections.put("top left", -9);
    }

    public void DisplayGame(boolean isPlayerTurn) {
        final String ANSI_BLACK_BACKGROUND = "\u001B[40m";
        final String ANSI_WHITE_BACKGROUND = "\u001B[47m";

        final String ANSI_WHITE = "\u001B[37m";
        final String ANSI_BLACK = "\u001B[30m";
        final String ANSI_GREEN = "\u001B[32m";

        final String ANSI_RESET = "\u001B[0m";

        CalculateScores();
        System.out.println(ANSI_BLACK_BACKGROUND + ANSI_WHITE + "Black: " + black_score + ANSI_RESET);
        System.out.println(ANSI_WHITE_BACKGROUND + ANSI_BLACK + "White: " + white_score + ANSI_RESET);
        System.out.println("-------------------------------------------");
        ArrayList<Integer> validMoves = GetValidMoves(gameState, isPlayerTurn);
        for (int i = 0; i < gameState.size(); i++) {
            String elem = String.valueOf(gameState.get(i));
            if (elem.equals(String.valueOf(State.BLANK))) {
                if (validMoves.contains(i)) {
                    elem = ANSI_GREEN + String.valueOf(i+1)  + ANSI_RESET;
                } else {
                    elem = "   ";
                }
            } else {
                if (elem.equals(String.valueOf(State.B)))
                    elem = ANSI_BLACK_BACKGROUND + ANSI_WHITE + " " + elem + " " + ANSI_RESET;
                else if (elem.equals(String.valueOf(State.W)))
                    elem = ANSI_WHITE_BACKGROUND + ANSI_BLACK + " " + elem + " " + ANSI_RESET;
            }

            if ((i+1) % 8 != 0) {
                System.out.print(elem + " | ");
            } else {
                System.out.println(elem);
                System.out.println("-------------------------------------------");
            }
        }

    }

    public int[] GetBounds(int position, int direction) {
        int lowerBound = 0;
        int upperBound = 63;

        if (direction == 1 || direction == -1 ) { //horizontal moves
            lowerBound = (int) Math.floor(position/8) * 8;
            upperBound = lowerBound + 7;
        } else if ( direction == 7 || direction == -9 ) { //left diagonal moves
            int leftBound = (int) Math.floor(position/8) * 8;
//                        int rightBound = leftBound + 7;

            int bound = (position - leftBound) * Math.abs(direction);
            lowerBound = position - bound;
            upperBound = position + bound;
        } else if (direction == -7 || direction == 9) { //right diagonal moves
            int leftBound = (int) Math.floor(position/8) * 8;
            int rightBound = leftBound + 7;

            int bound = Math.abs(position - rightBound) * Math.abs(direction);
            lowerBound = position - bound;
            upperBound = position + bound;
        }

        return new int[] {lowerBound, upperBound};

    }

    public ArrayList GetValidMoves(ArrayList gameState, boolean isPlayerTurn) throws IndexOutOfBoundsException {
       ArrayList<Integer> validMoves = new ArrayList<Integer>();

       State opponent, player;
       if (isPlayerTurn) {
            opponent = State.W;
            player = State.B;
       } else {
            opponent = State.B;
            player = State.W;
       }

        int firstPosition = gameState.indexOf(player);

        for (int i = firstPosition; i < gameState.size(); i++) {
            State elem = (State) gameState.get(i);
            if (elem.equals(player)) {

                for (Integer d : moveDirections.values()) {
//                    System.out.println(i);
                    int checkPosition = i;

                    int[] bounds = GetBounds(checkPosition, d);
                    int lowerBound = bounds[0];
                    int upperBound = bounds[1];

                    //remove directions for edge spaces
                    if (checkPosition % 8 == 0) { //left edge spaces
                        if (d == 7 || d == -1 || d == -9) {
//                            System.out.println(checkPosition + ": Skipped directions for left edge");
                            continue;
                        }
                    } else if ((checkPosition+1) % 8 == 0) { //right edge spaces
                        if (d == -7 || d == 1 || d == 9) {
//                            System.out.println(checkPosition + ": Skipped directions for right edge");
                            continue;
                        }
                    }

                    try {
                        int nextPosition = checkPosition + d;
                        while (gameState.get(nextPosition).equals(opponent) && ((nextPosition) >= lowerBound) && ((nextPosition) <= upperBound)) {
//                            System.out.println("Direction " + d);
//                            System.out.println("Lower bound: "+ lowerBound);
//                            System.out.println("Upper bound: "+ upperBound);
                            nextPosition += d;

                            if ((gameState.get(nextPosition).equals(State.BLANK)) && ((nextPosition) >= lowerBound) && ((nextPosition) <= upperBound)) {

                                if (!validMoves.contains(nextPosition)) {
//                                    System.out.println("checkPosition: " + checkPosition);
//                                    System.out.println("valid move: " + (checkPosition + d));
                                    validMoves.add(nextPosition);
                                }

                                break;
                            }
                        }
                    } catch (Exception e) {
                        continue;
                    }


                }
            }
        }

        return validMoves;
    }

    public int UserInput() throws java.io.IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

        ArrayList<Integer> validMoves = GetValidMoves(gameState, true);

        int positionInput;

        while (true) {
            System.out.print("Your move (type location number): ");
            positionInput = Integer.parseInt(reader.readLine()) - 1;

            if (gameState.get(positionInput).equals(State.BLANK)) {
                if (validMoves.contains(positionInput)) {
//                    gameState.set(positionInput, State.B);
                    break;
                } else {
                    System.out.println("Index number " + (positionInput + 1) + " is not a valid move! Please make a different move:");
                }

            } else {
                System.out.println("Index number " + (positionInput + 1) + " is already occupied! Please make a different move:");
            }
        }
//        DisplayGame(false);
        return positionInput;
    }

    public void PlaceMove(int movePosition, boolean playerTurn) {
        State opponent, player;
        if (playerTurn) {
            opponent = State.W;
            player = State.B;
        } else {
            opponent = State.B;
            player = State.W;
        }

//        if (!playerTurn) {
        gameState.set(movePosition, player);
//        }


//        int checkPosition = movePosition;
        tileChangeArray = new int[8];

        //check top
        if (CheckPosition(movePosition,-8, opponent, 0)) {
            ChangeTiles(movePosition, tileChangeArray[0], -8, player);
        }

        //check top right diag
        if (CheckPosition(movePosition, -7, opponent, 1)) {
            ChangeTiles(movePosition, tileChangeArray[1], -7, player);
        }

        //check right
        if (CheckPosition(movePosition, 1, opponent, 2)) {
            ChangeTiles(movePosition, tileChangeArray[2], 1, player);
        }

        //check down right diag
        if (CheckPosition(movePosition, 9, opponent, 3)) {
            ChangeTiles(movePosition, tileChangeArray[3], 9, player);
        }

        //check down
        if (CheckPosition(movePosition, 8, opponent, 4)) {
            ChangeTiles(movePosition, tileChangeArray[4], 8, player);
        }

        //check down left diag
        if (CheckPosition(movePosition, 7, opponent, 5)) {
            ChangeTiles(movePosition, tileChangeArray[5], 7, player);
        }

        //check left
        if (CheckPosition(movePosition, -1, opponent, 6)) {
            ChangeTiles(movePosition, tileChangeArray[6], -1, player);
        }

        //check top left diag
        if (CheckPosition(movePosition, -9, opponent, 7)) {
            ChangeTiles(movePosition, tileChangeArray[7], -9, player);
        }

//        System.out.println("**tileChangeList starts here**");
//        for (Integer i : tileChangeArray) {
//            System.out.println(i.toString());
//        }
//        System.out.println("**tileChangeList ends here**");

//        DisplayGame(!playerTurn).;
    }

    public boolean CheckPosition(int checkPosition, int direction, State opponent, int index) throws IndexOutOfBoundsException{
//        System.out.println("**CheckPosition starts here**");
        int[] bounds = GetBounds(checkPosition, direction);
        int lowerBound = bounds[0];
        int upperBound = bounds[1];

        try {
            int nextPosition = checkPosition + direction;
            while ((nextPosition <= upperBound) && (nextPosition >= lowerBound) && gameState.get(nextPosition).equals(opponent)) {
                checkPosition += direction;
                nextPosition = checkPosition + direction;
//            System.out.println("checkPosition: " + checkPosition);
//            System.out.println("piece: " + gameState.get(checkPosition));
                if (!gameState.get(nextPosition).equals(opponent) && !gameState.get(nextPosition).equals(State.BLANK) && (nextPosition <= upperBound) && (nextPosition >= lowerBound)) {
//                System.out.println("checkPosition: " + checkPosition);
                    tileChangeArray[index] = checkPosition;
//                break;
                    return true;
                }
            }
        } catch (Exception e) {
            return false;
        }

//        System.out.println("**CheckPosition ends here**");
        return false;
    }

    public void ChangeTiles(int startPosition, int endPosition, int direction, State player) {
//        System.out.println("startPosition: " + startPosition);
//        System.out.println("next Position: " + (startPosition+direction));
//        System.out.println("endPosition: " + endPosition);
        startPosition += direction;

        if (direction > 0) {
            for (int p = startPosition; p <= endPosition; p += direction) {
//                System.out.println(String.valueOf(p));
//                System.out.println("gameState: " + gameState.get(p));
//           System.out.println(player.toString());
                gameState.set(p, player);
            }
        } else {
            for (int p = startPosition; p >= endPosition; p += direction) {
//                System.out.println(String.valueOf(p));
//                System.out.println("gameState: " + gameState.get(p));
//           System.out.println(player.toString());
                gameState.set(p, player);
            }
        }
    }

    public void CalculateScores() {
        black_score = Collections.frequency(gameState, State.B);
        white_score = Collections.frequency(gameState, State.W);
    }

    public int CheckForWin() {
        /*
        black wins = 1
        white wins = 2
        tie = 0
        no one wins = -1
        */
        int blank = Collections.frequency(gameState, State.BLANK);

        CalculateScores();

        if (blank == 0) {
            if (black_score > white_score) {
                return 1;
            } else if (white_score > black_score) {
                return 2;
            } else  {
                return 0;
            }
        } else if ((black_score > white_score) && white_score == 0) {
            return 1;
        } else if ((white_score > black_score) && black_score == 0) {
            return 2;
        } else {
            return -1;
        }
    }
}
