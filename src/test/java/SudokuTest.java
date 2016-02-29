import org.junit.Assert;
import org.junit.Test;

import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import static java.util.Arrays.*;
import static org.hamcrest.core.Is.is;

/**
 * Created by ryan on 2/22/16.
 */
public class SudokuTest {



    @Test
    public void IJtoIndex() {

        Assert.assertThat("0,0 -> 0", Sudoku.IJtoIndex(0,0), is(0));
        Assert.assertThat("1,1 -> 4", Sudoku.IJtoIndex(1,1), is(4));
        Assert.assertThat("3,0 -> 0", Sudoku.IJtoIndex(3,0), is(0));
        Assert.assertThat("7,2 -> 5", Sudoku.IJtoIndex(7,2), is(5));

        Assert.assertThat("3,2 -> 2", Sudoku.IJtoIndex(3,2), is(2));
        Assert.assertThat("8,8 -> 8", Sudoku.IJtoIndex(8,8), is(8));
        Assert.assertThat("8,0 -> 6", Sudoku.IJtoIndex(8,0), is(6));
        Assert.assertThat("4,4 -> 4", Sudoku.IJtoIndex(4,4), is(4));
        Assert.assertThat("0,7 -> 1", Sudoku.IJtoIndex(0,7), is(1));

    }

    @Test(expected = IllegalArgumentException.class)
    public void playOnOccupiedSquare() {
        int[][] board = {
                {7, 0, 6, 4, 8, 0, 0, 0, 0},
                {0, 0, 2, 0, 0, 3, 6, 1, 0},
                {0, 0, 0, 0, 0, 0, 0, 0, 0},
                {0, 1, 0, 0, 0, 0, 7, 2, 9},
                {0, 7, 0, 0, 0, 0, 0, 8, 0},
                {6, 8, 5, 0, 0, 0, 0, 4, 0},
                {0, 0, 0, 0, 0, 0, 0, 0, 0},
                {0, 5, 7, 2, 0, 0, 4, 0, 0},
                {0, 0, 0, 0, 4, 6, 8, 0, 5}};

        Sudoku sudoku = new Sudoku(board);

        sudoku.play(0,0,8);
    }

    @Test(expected = IllegalArgumentException.class)
    public void playOutOfBounds() {
        int[][] board = {
                {7, 0, 6, 4, 8, 0, 0, 0, 0},
                {0, 0, 2, 0, 0, 3, 6, 1, 0},
                {0, 0, 0, 0, 0, 0, 0, 0, 0},
                {0, 1, 0, 0, 0, 0, 7, 2, 9},
                {0, 7, 0, 0, 0, 0, 0, 8, 0},
                {6, 8, 5, 0, 0, 0, 0, 4, 0},
                {0, 0, 0, 0, 0, 0, 0, 0, 0},
                {0, 5, 7, 2, 0, 0, 4, 0, 0},
                {0, 0, 0, 0, 4, 6, 8, 0, 5}};

        Sudoku sudoku = new Sudoku(board);

        sudoku.play(0,9,8);
    }

    @Test(expected = IllegalArgumentException.class)
    public void playSameOnRow() {
        int[][] board = {
                {7, 0, 6, 4, 8, 0, 0, 0, 0},
                {0, 0, 2, 0, 0, 3, 6, 1, 0},
                {0, 0, 0, 0, 0, 0, 0, 0, 0},
                {0, 1, 0, 0, 0, 0, 7, 2, 9},
                {0, 7, 0, 0, 0, 0, 0, 8, 0},
                {6, 8, 5, 0, 0, 0, 0, 4, 0},
                {0, 0, 0, 0, 0, 0, 0, 0, 0},
                {0, 5, 7, 2, 0, 0, 4, 0, 0},
                {0, 0, 0, 0, 4, 6, 8, 0, 5}};

        Sudoku sudoku = new Sudoku(board);

        sudoku.play(0,1,4);
    }

    @Test(expected = IllegalArgumentException.class)
    public void playSameOnColumn() {
        int[][] board = {
                {7, 0, 6, 4, 8, 0, 0, 0, 0},
                {0, 0, 2, 0, 0, 3, 6, 1, 0},
                {0, 0, 0, 0, 0, 0, 0, 0, 0},
                {0, 1, 0, 0, 0, 0, 7, 2, 9},
                {0, 7, 0, 0, 0, 0, 0, 8, 0},
                {6, 8, 5, 0, 0, 0, 0, 4, 0},
                {0, 0, 0, 0, 0, 0, 0, 0, 0},
                {0, 5, 7, 2, 0, 0, 4, 0, 0},
                {0, 0, 0, 0, 4, 6, 8, 0, 5}};

        Sudoku sudoku = new Sudoku(board);

        sudoku.play(0,6,4);
    }

    @Test(expected = IllegalArgumentException.class)
    public void playSameInSector() {
        int[][] board = {
                {7, 0, 6, 4, 8, 0, 0, 0, 0},
                {0, 0, 2, 0, 0, 3, 6, 1, 0},
                {0, 0, 0, 0, 0, 0, 0, 0, 0},
                {0, 1, 0, 0, 0, 0, 7, 2, 9},
                {0, 7, 0, 0, 0, 0, 0, 8, 0},
                {6, 8, 5, 0, 0, 0, 0, 4, 0},
                {0, 0, 0, 0, 0, 0, 0, 0, 0},
                {0, 5, 7, 2, 0, 0, 4, 0, 0},
                {0, 0, 0, 0, 4, 6, 8, 0, 5}};

        Sudoku sudoku = new Sudoku(board);

        sudoku.play(7, 6, 4);
    }

    @Test
    public void play() {
        int[][] board = {
                {0, 9, 6, 0, 8, 0, 3, 7, 0},
                {8, 0, 0, 3, 7, 4, 0, 0, 0},
                {0, 0, 3, 9, 6, 0, 0, 0, 8},
                {0, 5, 0, 7, 3, 0, 0, 8, 0},
                {0, 0, 1, 0, 0, 0, 7, 0, 0},
                {0, 7, 0, 0, 2, 1, 0, 3, 0},
                {5, 0, 0, 0, 9, 6, 8, 0, 0},
                {0, 0, 0, 8, 5, 3, 0, 0, 6},
                {0, 8, 9, 0, 1, 0, 5, 2, 0}};

        Sudoku sudoku = new Sudoku(board);

        sudoku.play(4, 5, 8);

        Assert.assertThat("8 is in row 4, place 5", sudoku.getRows().get(4).get(5).value, is(8));
        Assert.assertThat("8 is in column 5, place 4", sudoku.getColumns().get(5).get(4).value, is(8));
        Assert.assertThat("8 is in sector 4, place 5", sudoku.getSectors().get(4).get(5).value, is(8));
    }

    @Test
    public void solveFromFile() throws Exception {

        File file = new File("/home/ryan/proj/sudoku_app/src/main/resources/easy_short");
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                int[][] board = new int[9][9];

                for (int i=0; i<81; i++){
                    int column = i % 9;
                    int row = i / 9;
                    board[row][column] = Integer.parseInt(line.substring(i,i+1));
                }
                Sudoku s = new Sudoku(board);
                System.out.println("\n" + s);
                Solver.solve(s);
                System.out.println(s);
                Assert.assertThat("board is solved: " , Solver.isSolved(s), is(Boolean.TRUE));
            }
        }
    }

    @Test
    public void findUniqueValues() throws NoSuchMethodException, IllegalAccessException, InstantiationException, InvocationTargetException {

        Method method = Solver.class.getDeclaredMethod("isolateUniqueInSection", List.class);
        method.setAccessible(true);
        Object i = Solver.class.newInstance();

        List<Sudoku.Square> squareList = new ArrayList<>();
        Sudoku.Square square = new Sudoku.Square(0);
        square.legalValues = new HashSet<>(Arrays.<Integer>asList(1,2,3));
        Sudoku.Square square1 = new Sudoku.Square(1);
        square1.legalValues = new HashSet<>(Arrays.<Integer>asList(2,3,4));
        Sudoku.Square square2 = new Sudoku.Square(2);
        square2.legalValues = new HashSet<>(Arrays.<Integer>asList(2,3,5));
        Sudoku.Square square3 = new Sudoku.Square(3);
        square3.legalValues = new HashSet<>(Arrays.<Integer>asList(9,2,3));
        squareList.add(square);
        squareList.add(square1);
        squareList.add(square2);
        squareList.add(square3);

        method.invoke(i, squareList);

        Assert.assertThat("unique values isolated" , square.legalValues.size(), is(1));
        Assert.assertThat("unique values isolated" , square1.legalValues.size(), is(1));
        Assert.assertThat("unique values correct value" , square.legalValues.iterator().next(), is(1));
        Assert.assertThat("unique values correct value" , square1.legalValues.iterator().next(), is(4));
        Assert.assertThat("unique values correct value" , square2.legalValues.iterator().next(), is(5));
        Assert.assertThat("unique values correct value" , square3.legalValues.iterator().next(), is(9));


        square.legalValues = new HashSet<>(Arrays.<Integer>asList(1,2,3,9));
        square1.legalValues = new HashSet<>(Arrays.<Integer>asList(2,3,4));
        square2.legalValues = new HashSet<>(Arrays.<Integer>asList(2,3,5));
        square3.legalValues = new HashSet<>(Arrays.<Integer>asList(2,3,9));

        method.invoke(i, squareList);

        Assert.assertThat("unique values isolated" , square.legalValues.size(), is(1));
        Assert.assertThat("unique values isolated" , square1.legalValues.size(), is(1));
        Assert.assertThat("unique values isolated" , square2.legalValues.size(), is(1));
        Assert.assertThat("unique values isolated" , square3.legalValues.size(), is(3));
    }
}
