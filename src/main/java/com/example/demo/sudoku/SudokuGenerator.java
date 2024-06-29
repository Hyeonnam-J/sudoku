package com.example.demo.sudoku;

import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Random;

public class SudokuGenerator {
    private static final int SIZE = 9;
    private int[][] originBoard = new int[SIZE][SIZE];
    private int[][] board = new int[SIZE][SIZE];
    private int[][] row = new int[SIZE+1][SIZE+1];
    private int[][] col = new int[SIZE+1][SIZE+1];
    private int[][] diag = new int[SIZE+1][SIZE+1];
    private Random random = new Random();

    public HashMap<String, int[][]> generate(int numToKeep) {
        SudokuGenerator sudokuGenerator = new SudokuGenerator();

        sudokuGenerator.boardInit();
        sudokuGenerator.makeSudoku(0);
        int[][] answer = copyBoard(sudokuGenerator.board);
        System.out.println("answer ========================");
        sudokuGenerator.printBoard();

        sudokuGenerator.removeNumbersFromBoard(numToKeep);
        int[][] question = copyBoard(sudokuGenerator.board);
        System.out.println("question ======================");
        sudokuGenerator.printBoard();

        /**
         * ForkJoinPool 병렬 처리 도입 외에 성능을 더 향상할 방법은 딱히...
         */

        HashMap<String, int[][]> map = new HashMap<>();
        map.put("question", question);
        map.put("answer", answer);
        
        return map;
    }

    // 깊은 복사.
    private int[][] copyBoard(int[][] original) {
        int[][] copy = new int[original.length][original[0].length];
        for (int i = 0; i < original.length; i++) {
            for (int j = 0; j < original[i].length; j++) {
                copy[i][j] = original[i][j];
            }
        }
        return copy;
    }

    // 백 트래킹 부하 감소를 위해 규칙에 영향을 주지 않는 대각선 서브그리드 먼저 초기화
    public void boardInit() {
        int[] seqDiag = {0, 4, 8};

        for (int offset = 0; offset < SIZE; offset += 3) {
            int[] seq = new int[SIZE];
            for (int i = 0; i < SIZE; i++) {
                seq[i] = i + 1;
            }
            shuffleArray(seq);

            // 0 1 2
            // 3 4 5
            // 6 7 8
            // row, col을 좌표로 표시.
            // 위의 경우 row 1, 5 col 2, 5 이렇게 표시. 두 번째 행, 세 번째 컬럼의 값이 5. 값이 있으면 1.
            for (int idx = 0; idx < SIZE; idx++) {
                int i = idx / 3;
                int j = idx % 3;
                row[offset + i][seq[idx]] = 1;  // 규칙 행 검사.
                col[offset + j][seq[idx]] = 1;  // 규칙 열 검사.
                int k = seqDiag[offset / 3];
                diag[k][seq[idx]] = 1;          // 규칙 서브그리드 검사.
                originBoard[offset + i][offset + j] = seq[idx];
            }
        }
    }   // boardInit

    //  배열을 섞는 Fisher-Yates 알고리즘
    private void shuffleArray(int[] arr) {
        for (int i = arr.length - 1; i > 0; i--) {
            int index = random.nextInt(i + 1);  // nexInt -> 0부터 bound-1 사이의 정수를 리턴.
            int temp = arr[index];
            arr[index] = arr[i];
            arr[i] = temp;
        }
    }   // shuffleArray

    public boolean makeSudoku(int k) {
        if (k > 80) {
            for (int i = 0; i < SIZE; i++) {
                board[i] = Arrays.copyOf(originBoard[i], SIZE);
            }
            return true; // 스도쿠가 완성되면 true 반환
        }

        // k를 통해 1~9 사이의 정수를 얻어 그 위치를 정하는 로직.
        int i = k / SIZE;
        int j = k % SIZE;
        int startNum = random.nextInt(SIZE) + 1;    // 1~9

        // 이미 숫자가 할당된 칸인 경우 다음 칸으로 이동
        if (originBoard[i][j] != 0) {
            return makeSudoku(k + 1);
        }

        for (int m = 1; m <= SIZE; m++) {
            int num = 1 + (m + startNum) % SIZE;    // 수정된 숫자 할당 로직
            int d = (i / 3) * 3 + (j / 3);          // 서브그리드 인덱스

            // num의 행, 렬, 서브그리드 사용유무 체크. <빠른 검증>
            if (row[i][num] == 0 && col[j][num] == 0 && diag[d][num] == 0) {
                row[i][num] = col[j][num] = diag[d][num] = 1;
                originBoard[i][j] = num;

                // 백 트래킹 핵심.
                // makeSudoku(k + 1)가 참을 반환하면 현재 위치의 num은 옳은 값이므로 현재 함수도 참을 반환하여 이전 단계로 전달한다.
                // 그러나 거짓이 반환되면 이것을 되돌려야 한다.
                if (makeSudoku(k + 1)) {
                    return true; // 다음 칸에서 스도쿠가 완성되면 true 반환
                }
                row[i][num] = col[j][num] = diag[d][num] = 0;
                originBoard[i][j] = 0;
            }
        }
        return false; // 해당 칸에서 유효한 숫자를 찾지 못한 경우 false 반환
    }   // makeSudoku

    public void removeNumbersFromBoard(int numToKeep) {
        if (numToKeep < 17) {
            numToKeep = 17;
        }

        // 제거할 숫자 개수
        int numToRemove = SIZE * SIZE - numToKeep;

        List<Position> allPositions = new ArrayList<>();
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                allPositions.add(new Position(i, j));
            }
        }

        Collections.shuffle(allPositions);
        backtrackingRemove(numToRemove, allPositions);
    }   // removeNumbersFromBoard

    private static class Position {
        int x, y;
        Position(int x, int y) {
            this.x = x;
            this.y = y;
        }
    }

    private boolean backtrackingRemove(int numToRemove, List<Position> allPositions) {
        // System.out.println(numToRemove);

        if (numToRemove == 0) return true;
        if (allPositions.isEmpty()) return false;

        for (int idx = 0; idx < allPositions.size(); idx++) {

            int i = allPositions.get(idx).x;
            int j = allPositions.get(idx).y;

            int backup = board[i][j];
            removeNumber(i, j, backup);

            if (hasUniqueSolution()) {
                List<Position> newPositions = new ArrayList<>(allPositions);
                newPositions.remove(idx);
                if (backtrackingRemove(numToRemove - 1, newPositions)) {
                    return true;
                }
            }
            restoreNumber(i, j, backup);
        }
        return false;
    }

    public boolean hasUniqueSolution() {

        int solutionCount = 0;

        // 유일한 해를 검증하는 백트래킹 메서드
        return solveForUnique(solutionCount) == 1;
    }

    private int solveForUnique(int count) {

        int[] nextEmpty = findNextEmptyCell(board);
        if (nextEmpty == null) {
            return count + 1;
        }

        int i = nextEmpty[0];
        int j = nextEmpty[1];

        for (int num = 1; num <= SIZE; num++) {
            if (isValid(i, j, num)) {
                board[i][j] = num;
                row[i][num] = 1;
                col[j][num] = 1;
                diag[(i / 3) * 3 + (j / 3)][num] = 1;

                count = solveForUnique(count);

                board[i][j] = 0;
                row[i][num] = 0;
                col[j][num] = 0;
                diag[(i / 3) * 3 + (j / 3)][num] = 0;

                if (count > 1) return count;
            }
        }
        return count;
    }

    // 최소 남은 값 휴리스틱
    private int[] findNextEmptyCell(int[][] grid) {
        int minOptions = SIZE + 1;
        int[] res = null;

        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                if (grid[i][j] == 0) {
                    int options = 0;
                    for (int num = 1; num <= SIZE; num++) {
                        if (isValid(i, j, num)) {
                            options++;
                        }
                    }
                    if (options < minOptions) {
                        minOptions = options;
                        res = new int[]{i, j};
                    }
                }
            }
        }
        return res;
    }

    private boolean isValid(int x, int y, int num) {

        int d = (x / 3) * 3 + (y / 3);
        if (row[x][num] == 1) return false;
        if (col[y][num] == 1) return false;
        if (diag[d][num] == 1) return false;

        return true;
    }

    private void removeNumber(int x, int y, int num) {
        board[x][y] = 0;
        row[x][num] = 0;
        col[y][num] = 0;
        diag[(x / 3) * 3 + (y / 3)][num] = 0;
    }

    private void restoreNumber(int x, int y, int num) {
        board[x][y] = num;
        row[x][num] = 1;
        col[y][num] = 1;
        diag[(x / 3) * 3 + (y / 3)][num] = 1;
    }

    public void printBoard() {
        for (int i = 0; i < SIZE; i++) {
            System.out.println(Arrays.toString(board[i]));

            // String row = Arrays.toString(board[i]);
            // row = row.replace('[', '{').replace(']', '}');
            // System.out.println(row);

            // String row = Arrays.toString(board[i]);
            // row = row.replace('[', '{').replace(']', '}');
            // if (i < SIZE - 1) {
            //     System.out.println(row + ",");
            // } else {
            //     System.out.println(row);
            // }
        }
    }   // printBoard
}
