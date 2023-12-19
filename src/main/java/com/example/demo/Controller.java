package com.example.demo;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.example.demo.sudoku.SudokuData;
import com.example.demo.sudoku.SudokuQuestion;

@org.springframework.stereotype.Controller
public class Controller {
	
	@GetMapping("/")
	public String sudoku() {
		return "sudoku";
	}

	@GetMapping("/privacy")
	public String privacy() {
		return "privacy";
	}

	@ResponseBody
	@GetMapping("/sudoku/data")
	public SudokuData sudokuData(@RequestParam(name="difficulty") String difficulty) {
		SudokuData sudokuData = new SudokuQuestion().getSudokuData(difficulty);

		// Spring에서 ResponseBody를 사용하면, 메서드 반환 값이 json으로 http 응답 본문에 직렬화.
		// 즉 sudokuData는 반환된 다음 json으로 직렬화하여 응답 본문으로 전송.
		return sudokuData;
	}
}
