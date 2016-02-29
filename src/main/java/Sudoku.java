import java.util.*;

/**
 * Created by ryan on 2/22/16.
 */
public class Sudoku {

    public static class Square {

        public Integer value;
        public Set<Integer> legalValues;
        public Integer id;
        public Integer row;
        public Integer column;
        public Integer sector;

        public Square(int id) {
            this.legalValues = new HashSet<>();
            this.id = id;
            value = null;
            for (int i=1; i<=9; i++){
                legalValues.add(i);
            }
        }
        
        public void set(int value) {
            this.value = value;
            this.legalValues.clear();
        }

        public String toString() {
            return "{id: " + id + ", value: " + value +", row: " + row + ", column: " + column + ", sector: " + sector + ", legalValues: " + legalValues + "}";
        }

        public boolean equals(Square that){
            return this.id == that.id;
        }
    }

    private List<List<Square>> rows;
    private List<List<Square>> columns;
    private List<List<Square>> sectors;
    private List<Square> squares;
    private int moveCount = 0;

    public Sudoku(int[][] board) {
        rows = new ArrayList<>();
        columns = new ArrayList<>();
        sectors = new ArrayList<>();
        squares = new ArrayList<>();

        for (int i=0; i<81; i++) {
            Square square = new Square(i);
            squares.add(square);
        }

        int id = 0;
        for (int i=0; i<9; i++){
            List<Square> row = new ArrayList<>(9);
            List<Square> column  = new ArrayList<>(9);
            for (int j=0; j<9; j++){
                Square square = squares.get(id++);
                square.row = i;
                square.column = j;
                row.add(square);
                column.add(new Square(-1));
            }
            rows.add(row);
            columns.add(column);
        }
        for (Square square : squares) {
            columns.get(square.column).set(square.row, square);
        }


        for (int i=0; i<9; i++){
            sectors.add(new ArrayList<>(9));
        }

        for (int squareId=0; squareId<81; squareId++) {
            Square square = squares.get(squareId);
            int row = square.row;
            int column = square.column;
            if(row < 3 && column < 3) {
                square.sector = 0;
                sectors.get(0).add(square);
            }
            else if (row < 3 && column < 6) {
                square.sector = 1;
                sectors.get(1).add(square);
            }
            else if (row < 3) {
                square.sector = 2;
                sectors.get(2).add(square);
            }
            else if (row < 6 && column < 3) {
                square.sector = 3;
                sectors.get(3).add(square);
            }
            else if (row < 6 && column < 6) {
                square.sector = 4;
                sectors.get(4).add(square);
            }
            else if (row < 6) {
                square.sector = 5;
                sectors.get(5).add(square);
            }
            else if (column < 3) {
                square.sector = 6;
                sectors.get(6).add(square);
            }
            else if (column < 6) {
                square.sector = 7;
                sectors.get(7).add(square);
            }
            else {
                square.sector = 8;
                sectors.get(8).add(square);
            }

        }

        for (int i=0; i<9; i++) {
            for (int j=0; j<9; j++){
                if (board[i][j] != 0)
                    play(i, j, board[i][j]);
            }
        }
    }
    
    public void play(int row, int column, int value) {

        if (row < 0 || row > 8)
            throw new IllegalArgumentException("row: " + row + " is out of bounds");
        if (column < 0 || column > 8)
            throw new IllegalArgumentException("column: " + column + " is out of bounds");
        if (value < 1 || value > 9)
            throw new IllegalArgumentException("value: " + " is not valid");

        if (rows.get(row).get(column).value != null)
            throw new IllegalArgumentException("square: " + row + ", " + column + " is not blank");

        if (rows.get(row).contains(value))
            throw new IllegalArgumentException("row: " + row + " already contains a " + value);

        if (columns.get(column).contains(value))
            throw new IllegalArgumentException("column: " + column + " already contains a " + value);

        int sector = getSector(row, column);
        int index = IJtoIndex(row, column);

        if (sectors.get(sector).contains(value))
            throw new IllegalArgumentException("sector " + sector + " already contains a " + value);

        if (!rows.get(row).get(column).legalValues.contains(value))
            throw new IllegalArgumentException(value + " is not a legal value for row: " + row + " column: " + column);
        if (!columns.get(column).get(row).legalValues.contains(value))
            throw new IllegalArgumentException(value + " is not a legal value for row: " + row + " column: " + column);
        if (!sectors.get(sector).get(index).legalValues.contains(value))
            throw new IllegalArgumentException(value + " is not a legal value for row: " + row + " column: " + column);



        Square beingPlayed = rows.get(row).get(column);
        if (!ok(this)){
            System.out.print("");
        }
        for (Square square : rows.get(row)) {
            if (square.id == 1)
                System.out.print("");
            if (square.value == null && !square.equals(beingPlayed)) {
                square.legalValues.remove(value);
                if (square.legalValues.size() == 0) {
                    throw new IllegalArgumentException("empty legalMoves for square: " + square);
                }
            }
        }
        for (Square square : columns.get(column)) {
            if (square.value == null && !square.equals(beingPlayed)) {
                square.legalValues.remove(value);
                if (square.legalValues.size() == 0) {
                    throw new IllegalArgumentException("empty legalMoves for square: " + square);
                }
            }
        }
        for (Square square : sectors.get(sector)) {
            if (square.value == null && !square.equals(beingPlayed)) {
                square.legalValues.remove(value);
                if (square.legalValues.size() == 0) {
                    throw new IllegalArgumentException("empty legalMoves for square: " + square);
                }
            }
        }
        if (!ok(this)){
            System.out.println("");
        }
        moveCount++;
        beingPlayed.set(value);
    }

    private static boolean ok(Sudoku board){
        for (Square s : board.getSquares()) {
            if(s.value == null && s.legalValues.size() == 0)
                return false;
        }
        return true;
    }





    public static class Move {
        int id;
        int value;

        public Move(int id, int value){
            this.id = id;
            this.value = value;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            Move move = (Move) o;

            if (id != move.id) return false;
            return value == move.value;

        }

        @Override
        public int hashCode() {
            int result = id;
            result = 31 * result + value;
            return result;
        }
    }

    public Sudoku(Sudoku source) {

        this.columns = new ArrayList<>(9);
        this.rows = new ArrayList<>(9);
        this.sectors = new ArrayList<>(9);
        this.squares = new ArrayList<>(81);

        for (int i=0; i<9; i++){
            columns.add(new ArrayList<>(9));
            rows.add(new ArrayList<>(9));
            sectors.add(new ArrayList<>(9));
            for (int j=0; j<9; j++){
                columns.get(i).add(new Square(-1));
                rows.get(i).add(new Square(-1));
                sectors.get(i).add(new Square(-1));
            }
        }


        for (Square square : source.squares) {
            Square newSquare = new Square(square.id);
            newSquare.legalValues = square.legalValues;
            newSquare.row = square.row;
            newSquare.column = square.column;
            newSquare.sector = square.sector;
            newSquare.value = square.value;
            this.squares.add(newSquare);

            columns.get(square.column).set(square.row, newSquare);
            rows.get(square.row).set(square.column, newSquare);

            int sector = getSector(square.row, square.column);
            int index = IJtoIndex(square.row, square.column);
            sectors.get(sector).set(index, newSquare);
        }

    }






    public String toString() {
        String s = "";
        for (int i=0; i<9; i++) {
            for (int j=0; j<3; j++) {
                Integer value  = rows.get(i).get(j).value;
                s += (value == null ? "- " : value + " ");
            }
            s += "| ";
            for (int j=3; j<6; j++) {
                Integer value  = rows.get(i).get(j).value;
                s += value == null ? "- " : value + " ";
            }
            s += "| ";
            for (int j=6; j<9; j++) {
                Integer value  = rows.get(i).get(j).value;
                s += value == null ? "- " : value + " ";
            }
            s += "\n";

            if ((i+1)%3 == 0 && i != 8)
                s += "---------------------\n";
        }
        return s;
    }


    public static int IJtoIndex(int i, int j) {
        int sector = getSector(i, j);
        Pair offset = sectorToOffset(sector);

        i = i - offset.i;
        j = j - offset.j;

        int index = 0;
        while (j > 0) {
            index++;
            j--;
        }
        while (i > 0) {
            index += 3;
            i--;
        }

        return index;
    }

    private static int getSector(int i, int j) {
        if (i < 3 && j < 3)
            return 0;
        if (i < 3 && j < 6)
            return 1;
        if (i < 3)
            return 2;
        if (i < 6 && j < 3)
            return 3;
        if (i < 6 && j < 6)
            return 4;
        if (i < 6)
            return 5;
        if (j < 3)
            return 6;
        if (j < 6)
            return 7;
        return 8;
    }

    private static Pair sectorAndIndexToIJ(int sector, int index) {

        Pair offset = sectorToOffset(sector);
        Pair ij = indexToIJ(index);
        return new Pair(offset.i + ij.i, offset.j + ij.j);
    }

    private static Pair indexToIJ(int index) {
        Pair result;
        switch (index) {
            case 0:
                result = new Pair(0, 0);
                break;
            case 1:
                result = new Pair(0, 1);
                break;
            case 2:
                result = new Pair(0, 2);
                break;
            case 3:
                result = new Pair(1, 0);
                break;
            case 4:
                result = new Pair(1, 1);
                break;
            case 5:
                result = new Pair(1, 2);
                break;
            case 6:
                result = new Pair(2, 0);
                break;
            case 7:
                result = new Pair(2, 1);
                break;
            default:
                result = new Pair(2, 2);
        }
        return result;
    }

    private static Pair sectorToOffset(int sector) {
        Pair result;
        switch (sector) {
            case 0:
                result = new Pair(0, 0);
                break;
            case 1:
                result = new Pair(0, 3);
                break;
            case 2:
                result = new Pair(0, 6);
                break;
            case 3:
                result = new Pair(3, 0);
                break;
            case 4:
                result = new Pair(3, 3);
                break;
            case 5:
                result = new Pair(3, 6);
                break;
            case 6:
                result = new Pair(6, 0);
                break;
            case 7:
                result = new Pair(6, 3);
                break;
            default:
                result = new Pair(6, 6);
                break;
        }
        return result;
    }

    private static class Pair {
        public int i;
        public int j;

        public Pair(int i, int j) {
            this.i = i;
            this.j = j;
        }

        public boolean equals(Pair that) {
            return this.i == that.i && this.j == that.j;
        }
    }

    public static void main(String[] args) {
        int[][] board1 = {
                {7, 0, 6, 4, 8, 0, 0, 0, 0},
                {0, 0, 2, 0, 0, 3, 6, 1, 0},
                {0, 0, 0, 0, 0, 0, 0, 0, 0},
                {0, 1, 0, 0, 0, 0, 7, 2, 9},
                {0, 7, 0, 0, 0, 0, 0, 8, 0},
                {6, 8, 5, 0, 0, 0, 0, 4, 0},
                {0, 0, 0, 0, 0, 0, 0, 0, 0},
                {0, 5, 7, 2, 0, 0, 4, 0, 0},
                {0, 0, 0, 0, 4, 6, 8, 0, 5}};

        int[][] board2 = {
                {0, 9, 6, 0, 8, 0, 3, 7, 0},
                {8, 0, 0, 3, 7, 4, 0, 0, 0},
                {0, 0, 3, 9, 6, 0, 0, 0, 8},
                {0, 5, 0, 7, 3, 0, 0, 8, 0},
                {0, 0, 1, 0, 0, 0, 7, 0, 0},
                {0, 7, 0, 0, 2, 1, 0, 3, 0},
                {5, 0, 0, 0, 9, 6, 8, 0, 0},
                {0, 0, 0, 8, 5, 3, 0, 0, 6},
                {0, 8, 9, 0, 1, 0, 5, 2, 0}};

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


//        System.out.println(s.toString());

//        System.out.println();

        long now = System.currentTimeMillis();
        Sudoku s = new Sudoku(board3);
        Solver.solve(s);
        System.out.println(System.currentTimeMillis() - now);
        System.out.println(s);
//        System.out.printf("solved: %s\n", s.isSolved());
//        System.out.println(s);

    }


    public List<List<Square>> getRows() {
        return rows;
    }

    public void setRows(List<List<Square>> rows) {
        this.rows = rows;
    }

    public List<List<Square>> getColumns() {
        return columns;
    }

    public void setColumns(List<List<Square>> columns) {
        this.columns = columns;
    }

    public List<List<Square>> getSectors() {
        return sectors;
    }

    public void setSectors(List<List<Square>> sectors) {
        this.sectors = sectors;
    }

    public List<Square> getSquares() {
        return squares;
    }

    public void setSquares(List<Square> squares) {
        this.squares = squares;
    }

    public int getMoveCount() {
        return moveCount;
    }

    public void setMoveCount(int moveCount) {
        this.moveCount = moveCount;
    }

    public static int[][] makeGrid(Sudoku board){

        int[][] result = new int[9][9];

        for (Square s : board.getSquares()){
            Integer value = s.value == null ? 0 : s.value;
            result[s.row][s.column] = value;
        }

        return result;
    }
}