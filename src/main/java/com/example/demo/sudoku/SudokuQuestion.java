package com.example.demo.sudoku;

import java.util.HashMap;
import java.util.Random;

public class SudokuQuestion {
  public SudokuData getSudokuData(String difficulty) {
    Random rand = new Random();
    SudokuData puzzle = null;
    SudokuGenerator sudokuGenerator = new SudokuGenerator();
    int randomNumber;
    HashMap<String, int[][]> map = new HashMap<>();
    int[][] question;
    int[][] answer;

    switch (difficulty.toLowerCase()){
      case "easy":

        // 34 - 41
        randomNumber = rand.nextInt(8) + 34;
        map = sudokuGenerator.generate(randomNumber);

        question = map.get("question");
        answer = map.get("answer");

        puzzle = new SudokuData(question, answer);

        break;
      case "normal":    

        // 27 - 33
        randomNumber = rand.nextInt(7) + 27;
        map = sudokuGenerator.generate(randomNumber);

        question = map.get("question");
        answer = map.get("answer");

        puzzle = new SudokuData(question, answer);

        break;
      case "hard":

        randomNumber = rand.nextInt(3);
        switch(randomNumber){
          case 0:
            HashMap<Integer, SudokuData> hardMap_zero = new HardQuestionZero().getQuestion();
            puzzle = hardMap_zero.get(rand.nextInt(hardMap_zero.size()));
            break;
          case 1:
            HashMap<Integer, SudokuData> hardMap_one = new HardQuestionOne().getQuestion();
            puzzle = hardMap_one.get(rand.nextInt(hardMap_one.size()));
            break;
          case 2:
            HashMap<Integer, SudokuData> hardMap_two = new HardQuestionTwo().getQuestion();
            puzzle = hardMap_two.get(rand.nextInt(hardMap_two.size()));
            break;
        }

        break;
    } // switch-difficulty
    return puzzle;
  } // getSudokuData
} // class
