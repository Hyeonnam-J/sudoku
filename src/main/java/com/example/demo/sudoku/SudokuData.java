package com.example.demo.sudoku;

public class SudokuData {
  private int[][] question;
  private int[][] answer;

  public SudokuData(int[][] question, int[][] answer) {
    this.question = question;
    this.answer = answer;
  }

  /**
   * Jackson library는 getter 메서드를 이용하여 해당 객체의 데이터를 Json 형식으로 변환한다.
   * getQuestion일 경우 JSON 응답에서 question이 key로 사용
   */
  public int[][] getQuestion() {
    return deepCopy(question);
  }

  public int[][] getAnswer() {
    return deepCopy(answer);
  }

  private int[][] deepCopy(int[][] original) {
    if (original == null) {
      return null;
    }

    final int[][] result = new int[original.length][];
    for (int i = 0; i < original.length; i++) {
      result[i] = original[i].clone();
    }
    return result;
  }
}
