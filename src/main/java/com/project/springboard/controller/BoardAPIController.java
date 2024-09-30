package com.project.springboard.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.project.springboard.service.BoardService;
import com.project.springboard.vo.BoardVO;

@RestController
@RequestMapping("/api/springboard")
public class BoardAPIController {

	@Autowired
	private BoardService service;
	
	/**
	 * 게시글 목록(답글 추가)
	 * @param vo
	 * @return
	 */
	@GetMapping
	public ResponseEntity<Map<String, Object>> getAllBoard(BoardVO vo) {
		Map<String, Object> resultMap = service.getBoardList(vo);
		//200(성공) 서버가 요청을 제대로 처리했다
		return new ResponseEntity<Map<String, Object>>(resultMap, HttpStatus.OK);
	}
	
	/**
	 * 게시글 상세(답글까지)
	 * @param vo
	 * @return
	 */
	@GetMapping("/detail")
	public ResponseEntity<Map<String, Object>> getBoardById(BoardVO vo) {
		Map<String, Object> resultMap = new HashMap<>();
		
		BoardVO resultInfo = service.getBoardById(vo);
		
		resultMap.put("resultInfo", resultInfo);
		
		//200(성공) 서버가 요청을 제대로 처리했다
		return new ResponseEntity<>(resultMap, HttpStatus.OK);
	}
	
	/**
	 * 게시글 등록 (답글 추가)
	 * @param vo
	 * @return
	 */
	@PostMapping
	public ResponseEntity<Void> insertBoard(@RequestBody Map<String, Object> commentData) {
		service.insertBoard(commentData);
		//201(작성됨) 성공적으로 요청되었으며 서버가 새 리소스를 작성했다.
		return new ResponseEntity<>(HttpStatus.CREATED);
	}
	
	/**
	 * 게시글 수정(답글까지)
	 * @param vo
	 * @return
	 */
	@PutMapping("/update")
	public ResponseEntity<Void> updateBoard(@RequestBody BoardVO vo) {
		service.updateBoard(vo);
		//200(성공) 서버가 요청을 제대로 처리했다
		return new ResponseEntity<>(HttpStatus.OK);
	}
	
	/**
	 * 게시글 삭제(답글까지)
	 * @param vo
	 * @return
	 */
	@DeleteMapping("/delete")
	public ResponseEntity<Void> deleteBoard(BoardVO vo) {
		service.deleteBoard(vo);
		//204(콘텐츠 없음) 서버가 요청을 성공적으로 처리했지만 콘텐츠를 제공하지 않는다.
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}
	
}
