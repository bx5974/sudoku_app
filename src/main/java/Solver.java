import java.util.*;

/**
 * Created by ryan on 2/29/16.
 */
public class Solver {

    public static void solve(Sudoku board){

        simpleSolve(board);

        if (isSolved(board)){
            return;
        }


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
}
