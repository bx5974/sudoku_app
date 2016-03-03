import java.util.*;

/**
 * Created by ryan on 2/29/16.
 */
public class Solver {

    public static void main(String[] args) {
        int[][] board3 = {
                {0,9,8,0,0,0,0,0,2},
                {0,0,0,0,4,0,0,0,0},
                {0,1,5,0,0,9,0,8,0},
                {0,3,0,0,0,1,0,0,0},
                {5,0,0,6,2,3,0,0,4},
                {0,0,0,9,0,0,0,1,0},
                {0,8,0,5,0,0,2,3,0},
                {0,0,0,0,7,0,0,0,0},
                {6,0,0,0,0,0,7,5,0}};

        Sudoku board = new Sudoku(board3);
        solve(board);
        System.out.println(board);
    }

    public static Sudoku solve(Sudoku board){

//        simpleSolve(board);

        if (isSolved(board)){
//            System.out.println(board);
            return board;
        }

        while (!isSolved(board)) {
//            System.out.println(board);
//            System.out.println(board.getMoveCount());
            if (!ok(board)) {
                System.out.println("bad board");
                return board;
            }
            Sudoku copy = copyBoard(board);
            Sudoku.Move nextTry = tryMove(copy);
            if (isSolved(board)) {
//                System.out.println(board.toString());
                return board;
            }
            if (nextTry != null) {
                board.getSquares().get(nextTry.id).legalValues.remove(nextTry.value);
                if (!ok(board))
                    board.getSquares().get(nextTry.id).legalValues.add(nextTry.value);
//                System.out.println("removing " + nextTry.toString());
            } else {
                return solve(copy);
            }
//            System.out.println(board.getMoveCount());
        }
        return board;

    }

    private static boolean ok(Sudoku board){
        try {
            simpleSolve(board);
        } catch (Exception e){
            return false;
        }
        return true;

    }

    private static Sudoku.Move tryMove(Sudoku board) {

        Sudoku.Move move = null;

        try {
            simpleSolve(board);
            if (isSolved(board))
                return null;
//            move = getSimplestMove(board);
            move = getRandomGuess(board);
            Sudoku.Square square = board.getSquares().get(move.id);
            board.play(square.row, square.column, move.value);
            simpleSolve(board);
        } catch (IllegalArgumentException | NoSuchElementException e) {
            return move;
        }
        try {
            simpleSolve(board);
        } catch (IllegalArgumentException | NoSuchElementException e) {
            return move;
        }
        return null;
    }

    private static Sudoku.Move getSimplestMove(Sudoku board) {

        int smallestLegalMoves = 9;
        int id = 0;
        for (Sudoku.Square square : board.getSquares()) {
            if (square.legalValues.size() < smallestLegalMoves && square.legalValues.size() > 0) {
                smallestLegalMoves = square.legalValues.size();
                id = square.id;
            }
        }
        int value = board.getSquares().get(id).legalValues.iterator().next();

        Sudoku.Move result = new Sudoku.Move(id, value);
        return result;
    }

    private static Sudoku.Move getRandomGuess(Sudoku board) {

        Set<Sudoku.Square> with2LegalMoves = new HashSet<>();
        int size = 2;
        while (with2LegalMoves.size() == 0) {
            for (Sudoku.Square square : board.getSquares()) {
                if (square.legalValues.size() == size) {
                    with2LegalMoves.add(square);
                }
            }
            size++;
        }

        Sudoku.Move result = null;

        int randomIndex = (int) Math.floor(Math.random() * with2LegalMoves.size());
        int index = 0;
        for (Sudoku.Square square : with2LegalMoves) {
            if (index++ == randomIndex) {
                int legalMovesRandomIndex = (int) Math.floor(Math.random() * square.legalValues.size());
                int legalMovesIndex = 0;
                for (Integer legalMove : square.legalValues){
                    if (legalMovesIndex++ == legalMovesRandomIndex) {
                        result = new Sudoku.Move(square.id, legalMove);
                        return result;
                    }
                }
            }
        }

        return result;
    }



    public static boolean simpleSolve(Sudoku board){

        int moveCount;
        do {
            moveCount = board.getMoveCount();
            for (List<Sudoku.Square> row : board.getRows())
                isolateUniqueInSection(row);
            for (List<Sudoku.Square> column : board.getColumns())
                isolateUniqueInSection(column);
            for (List<Sudoku.Square> sector : board.getSectors())
                isolateUniqueInSection(sector);
            if (board.getMoveCount() == 81) {
                return true;
            }
            for (Sudoku.Square square : board.getSquares()) {
                if (square.legalValues.size() == 1) {
                    board.play(square.row, square.column, square.legalValues.iterator().next());
                }
            }
            if (board.getMoveCount() == 81) {
                return true;
            }
//            System.out.println(moveCount + " : " + board.getMoveCount());
        } while (moveCount != board.getMoveCount());

        return false;
    }


    private static void isolateUniqueInSection(List<Sudoku.Square> squares) {

        List<Set<Integer>> list = new ArrayList<>();
        for (Sudoku.Square square : squares){
            list.add(square.legalValues);
        }
//        System.out.println(list);
        list = findUniqueValues(list);
        int index = 0;
        for (Sudoku.Square square : squares){
            square.legalValues = list.get(index++);
        }

//        System.out.println(list);
//        System.out.println();
    }


    private static List<Set<Integer>> findUniqueValues(List<Set<Integer>> list) {

        List<Set<Integer>> newList = new ArrayList<>();

        //loop every set in list
        int setIndex = 0;
        for (Set<Integer> set : list) {

            Set<Integer> unique = new HashSet<>();

            //loop every value in set
            for (Integer value : set) {

                boolean found = false;

                //loop every other set
                int otherSetIndex = 0;
                for (Set<Integer> otherSet : list) {
                    if (setIndex != otherSetIndex) {

                        //loop every member of other set
                        for (Integer otherValue : otherSet) {
                            if(value != null && value.equals(otherValue)) {
                                found = true;
                            }
                        }
                    }
                    otherSetIndex++;
                }

                if (!found)
                    unique.add(value);
            }

            if (!unique.isEmpty())
                newList.add(unique);
            else
                newList.add(set);

            setIndex++;
        }
        return newList;
    }

    public static Sudoku tryGuess(Sudoku.Move move, Sudoku board) {

        try {
            Sudoku.Square square = board.getSquares().get(move.id);
            board.play(square.row, square.column, move.value);
            simpleSolve(board);
            return board;
        } catch (IllegalArgumentException e) {
            System.out.println(e.getMessage());
            return null;
        }
    }

    public static Sudoku.Move findEasiestGuess(Sudoku board) {
        Sudoku.Square lowestSquare;
        int lowest = 9;
        Sudoku.Move potentialMove = null;
        for (Sudoku.Square square : board.getSquares()) {
            if (square.value == null && square.legalValues.size() < lowest){
                lowestSquare = square;
                lowest = square.legalValues.size();

                Set<Integer> legalValues = lowestSquare.legalValues;
                Iterator<Integer> iterator = legalValues.iterator();

                potentialMove = new Sudoku.Move(lowestSquare.id, iterator.next());
            }
        }

        return potentialMove;
    }

    public static boolean isSolved(Sudoku board){

        for(int i=0; i<9; i++){
            if (!isSectionSolved(board.getRows().get(i))
             || !isSectionSolved(board.getColumns().get(i))
             || !isSectionSolved(board.getSectors().get(i))) {
                return false;
            }
        }
        return true;
    }

    private static boolean isSectionSolved(List<Sudoku.Square> squares){

        Set<Integer> checker = new HashSet<>();
        for (int i=1; i<=9; i++){
            checker.add(i);
        }

        for (Sudoku.Square square : squares) {
            checker.remove(square.value);
        }
        return checker.size() == 0;
    }

    private static Sudoku copyBoard(Sudoku source) {
        Sudoku copy = new Sudoku(Sudoku.makeGrid(source));
        for (Sudoku.Square square : source.getSquares()) {
            Set<Integer> legalValues = square.legalValues;
            copy.getSquares().get(square.id).legalValues.clear();
            for (Integer value : legalValues)
                copy.getSquares().get(square.id).legalValues.add(value);
        }
        copy.setMoveCount(source.getMoveCount());

        return copy;
    }
}
