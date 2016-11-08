package cs445.a3;

import java.util.List;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;

public class Sudoku {
	//create 2 integer variables, previousRow and previousColumn
	private static int previousRow, previousColumn;
	
    static boolean isFullSolution(int[][] board) {
        for (int i = 0; i < board.length; i++) {
        	//declare 2 ArrayLists to hold values in each row and column
        	ArrayList<Integer> columnValues = new ArrayList<Integer>();
        	ArrayList<Integer> rowValues = new ArrayList<Integer>();

        	for (int j = 0; j < board.length; j++) {
            	//check column values
        		if (!columnValues.contains(board[i][j]) && board[i][j] > 0)
        			columnValues.add(board[i][j]);
        		else
        			return false;
        		
        		//check row values
        		if (!rowValues.contains(board[j][i]) && board[j][i] > 0)
        			rowValues.add(board[j][i]);
        		else
        			return false;
        	}
        }
        
    	for (int i = 1; i < 8; i+=3) {
    		for (int j = 1; j < 8; j+=3) {
            	ArrayList<Integer> regionValues = new ArrayList<Integer>();
    			for (int k = i-1; k < i+2; k++) {
    				for (int l = j-1; l < j+2; l++) {
    					if (!regionValues.contains(board[k][l]) && board[k][l] > 0)
    						regionValues.add(board[k][l]);
    					else
    						return false;
    				}
    			}
    			
    		}
    	}
        
        //return true if all rows and columns contain values 1-9
        return true;
    }

    static boolean reject(int[][] board, int row, int column) {
    	//if this is the first iteration, return false
    	if (row == -1)
    		return false;
    	
    	//test whether the previously added choice's row/column already contain that value and return true if they do
    	for (int i = 0; i < board.length; i++) {
    		if (board[row][i] == board[row][column] && i != column)
    			return true;

    		if (board[i][column] == board[row][column] && i != row)
    			return true;
    	}
    	
    	//obtain the row and column indeces of the center square of the region
		int regionCenterRow = -1;
		int regionCenterColumn = -1;
		
		if (row < 3)	
			regionCenterRow = 1;
		else if (row < 6)
			regionCenterRow = 4;
		else
			regionCenterRow = 7;
		
		if (column < 3)
			regionCenterColumn = 1;
		else if (column < 6)
			regionCenterColumn = 4;
		else
			regionCenterColumn = 7;
		
		//return true if a duplicate value is found in the region
		for (int i = regionCenterRow-1; i < regionCenterRow+2; i++) {
			for (int j = regionCenterColumn-1; j < regionCenterColumn+2; j++) {
				if (board[row][column] == board[i][j] && i != row && j != column) {
					return true;
				}
			}
    	}
    
    	//otherwise return false
        return false;
    }

    static int[][] extend(int[][] board) {
    	//make a copy of board
    	int[][] newBoard = makeCopy(board);
    	
    	//create a boolean variable
    	boolean added = false;
    	
    	//find the next empty Sudoku square
    	int[] location = findEmpty(board);
    	
    	//if no empty square was found, return null; otherwise set the empty square to 1 and return newBoard
    	if (location == null)
    		return null;
    	else {
    		previousRow = location[0];
    		previousColumn = location[1];
    		newBoard[previousRow][previousColumn] = 1;
    		return newBoard;
    	}
    }

    static int[][] next(int[][] board, int row, int column) {
    	//make a copy of the board
    	int[][] newBoard = makeCopy(board);
    	
    	if (newBoard[row][column] == 9)
    		return null;
    	else {
    		newBoard[row][column]++;
    		return newBoard;
    	}
    }

    static void testIsFullSolution() {
    	//create a 2D array of integers to be used throughout the method
    	int[][] testBoard;
    	
    	//case where an unsolved sudoku board is passed in
    	System.out.println("An unsolved Sudoku board:\n");
    	testBoard = readBoard("isFullSoln_unsolved.su");
    	printBoard(testBoard);
    	boolean solved = isFullSolution(testBoard);
    	System.out.println("\nExpected: false from isFullSolution().");
    	System.out.println("Returned: " + solved + ". The method isFullSolution() worked.\n");
    	System.out.println("---|---|---|---\n");
    	
    	//case where a solved sudoku board is passed in
    	System.out.println("A completely solved Sudoku board:\n");
    	testBoard = readBoard("isFullSoln_solved.su");
    	printBoard(testBoard);
    	solved = isFullSolution(testBoard);
    	System.out.println("\nExpected: true from isFullSolution().");
    	System.out.println("Returned: " + solved + ". The method isFullSolution() worked.\n");
    	System.out.println("---|---|---|---\n");
    	
      	//case where the last region has a duplicate value
    	System.out.println("A Sudoku board where the bottom right corner's region contains two 4's:\n");
    	testBoard = readBoard("isFullSoln_lastRegionWrong.su");
    	printBoard(testBoard);
    	solved = isFullSolution(testBoard);
    	System.out.println("\nExpected: false from isFullSolution().");
    	System.out.println("Returned: " + solved + ". The method isFullSolution() worked.\n");
    	System.out.println("---|---|---|---\n");
    }

    static void testReject() {
    	//create a 2D array of integers to be used throughout the method
    	int[][] testBoard;
    	
    	//case where the last number added to a Sudoku board is already in the region
    	System.out.println("A Sudoku board where the last number added is a duplicate in the region:\n");
    	testBoard = readBoard("reject_region.su");
    	printBoard(testBoard);
    	boolean rejected = reject(testBoard, 1, 1);
    	System.out.println("\nExpected: true from reject().");
    	System.out.println("Returned: " + rejected + ". The method reject() worked.\n");
    	System.out.println("---|---|---|---\n");
    	
    	//case where the last number added to a Sudoku board is already in the current row
    	System.out.println("A Sudoku board where the last number added is a duplicate in its row:\n");
    	testBoard = readBoard("reject_row.su");
    	printBoard(testBoard);
    	rejected = reject(testBoard, 1, 1);
    	System.out.println("\nExpected: true from reject().");
    	System.out.println("Returned: " + rejected + ". The method reject() worked.\n");
    	System.out.println("---|---|---|---\n");
    	
    	//case where the last number added to a Sudoku board is already in the current column
    	System.out.println("A Sudoku board where the last number added is a duplicate in its column:\n");
    	testBoard = readBoard("reject_column.su");
    	printBoard(testBoard);
    	rejected = reject(testBoard, 1, 1);
    	System.out.println("\nExpected: true from reject().");
    	System.out.println("Returned: " + rejected + ". The method reject() worked.\n");
    	System.out.println("---|---|---|---\n");
    	
    	//case where the last number added to a Sudoku board works
    	System.out.println("A Sudoku board where the last number added works:\n");
    	testBoard = readBoard("reject_noProblems.su");
    	printBoard(testBoard);
    	rejected = reject(testBoard, 1, 1);
    	System.out.println("\nExpected: false from reject().");
    	System.out.println("Returned: " + rejected + ". The method reject() worked.\n");
    	System.out.println("---|---|---|---\n");
    }

    static void testExtend() {
    	//create a 2D array of integers to be used throughout the method
    	int[][] testBoard;
    	
    	//case where an empty sudoku board is passed in
    	System.out.println("An empty Sudoku board:\n");
    	testBoard = readBoard("extend_empty.su");
    	printBoard(testBoard);
    	System.out.println("\nExpecting: a 1 in the top left corner of the board.\n");  
    	printBoard(extend(testBoard));
    	System.out.println("\nThe extend() method worked.\n");  
    	System.out.println("---|---|---|---\n");
    	
    	//case where a partially solved sudoku board is passed in
    	System.out.println("A partially solved Sudoku board:\n");
    	testBoard = readBoard("extend_partiallySolved.su");
    	printBoard(testBoard);
    	System.out.println("\nExpecting: a 1 at Row 3, Column 5.\n");  
    	printBoard(extend(testBoard));
    	System.out.println("\nThe extend() method worked.\n");  
    	System.out.println("---|---|---|---\n");
    	
    	//case where a completed board is passed in
    	System.out.println("A fully solved Sudoku board:\n");
    	testBoard = readBoard("isFullSoln_solved.su");
    	printBoard(testBoard);
    	System.out.println("\nExpecting: null because extend() returns null if there are no more empty blocks.\n");  
    	testBoard = extend(testBoard);
    	System.out.println(testBoard);
    	System.out.println("\nThe extend() method worked.\n");  
    	System.out.println("---|---|---|---\n");
    }

    static void testNext() {
    	//create a 2D array of integers to be used throughout the method
    	int[][] testBoard;
    	
    	//case where a number 1-8 must be incremented because the previous partial solution was rejected
    	System.out.println("A Sudoku board that is rejected by reject() after inserting a 1 at Row 2 Column 2:\n");
    	testBoard = readBoard("next_standardCase.su");
    	printBoard(testBoard);
    	System.out.println("\nExpecting: a 2 in Row 2, Column 2.\n");  
    	printBoard(next(testBoard, 1, 1));
    	System.out.println("\nThe next() method worked.\n");  
    	System.out.println("---|---|---|---\n");
    	
    	//case where the reject() method rejected a 9, and therefore next() needs to return null
    	System.out.println("A Sudoku board that is rejected by reject() after inserting a 9 at Row 2 Column 2:\n");
    	testBoard = readBoard("next_nine.su");
    	printBoard(testBoard);
    	System.out.println("\nExpecting: null because next() returns null when there are no more decisions available.\n");  
    	testBoard = next(testBoard, 1, 1);
    	System.out.println(testBoard);
    	System.out.println("\nThe next() method worked.\n");  
    	System.out.println("---|---|---|---\n");
    }

    static void printBoard(int[][] board) {
        if (board == null) {
            System.out.println("This Sudoku puzzle does not have a solution.");
            return;
        }
        for (int i = 0; i < 9; i++) {
            if (i == 3 || i == 6) {
                System.out.println("----+-----+----");
            }
            for (int j = 0; j < 9; j++) {
                if (j == 2 || j == 5) {
                    System.out.print(board[i][j] + " | ");
                } else {
                    System.out.print(board[i][j]);
                }
            }
            System.out.print("\n");
        }
    }

    static int[][] readBoard(String filename) {
        List<String> lines = null;
        try {
            lines = Files.readAllLines(Paths.get(filename), Charset.defaultCharset());
        } catch (IOException e) {
        	System.out.println("An invalid board was provided and could not successfully be read in.");
            return null;
        }
        int[][] board = new int[9][9];
        int val = 0;
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                try {
                    val = Integer.parseInt(Character.toString(lines.get(i).charAt(j)));
                } catch (Exception e) {
                    val = 0;
                }
                board[i][j] = val;
            }
        }
        return board;
    }

    static int[][] solve(int[][] board, int row, int column) {
        if (reject(board, row, column)) {
        	return null;
        }
        if (isFullSolution(board)) return board;
        int[][] attempt = extend(board);
        row = previousRow; //update row for this activation record
        column = previousColumn; //update column for this activation record
        while (attempt != null) {
            int[][] solution = solve(attempt, row, column);
            if (solution != null) return solution;
            attempt = next(attempt, row, column);
        }
        return null;
    }
    
    private static int[][] makeCopy(int[][] board) {
    	//declare a new 2D array of integers to hold the new board
    	int[][] newBoard = new int[board.length][board.length];
    	
    	//copy the board
    	for (int i = 0; i < board.length; i++) {
    		for (int j = 0; j < board.length; j++)
    			newBoard[i][j] = board[i][j];
    	}
    	
    	return newBoard;
    }

    private static int[] findEmpty(int[][] board) {
    	//create an integer array of size 2
    	int[] location = new int[2];
    	
    	//create a boolean variable
    	boolean found = false;
    	
    	//locate the next empty Sudoku square
    	for (int i = 0; i < board.length; i++) {
    		for (int j = 0; j < board.length; j++) {
    			if (board[i][j] == 0) {
    				found = true;
    				location[0] = i;
    				location[1] = j;
    				break;
    			}
    		}
    		if (found)
    			break;
    	}
    	//if an empty Sudoku square was found, return the location; otherwise return null
    	if (found)
    		return location;
    	else
    		return null;
    }
    
    public static void main(String[] args) {
        if (args[0].equals("-t")) {
            testIsFullSolution();
            testReject();
            testExtend();
            testNext();
        } else {
            int[][] board = readBoard(args[0]);
            if (board == null)
            	System.exit(0);
            printBoard(board);
            System.out.println();
            printBoard(solve(board, -1, -1));
        }
    }
}