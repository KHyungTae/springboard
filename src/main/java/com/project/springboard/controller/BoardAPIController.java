package com.project.springboard.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

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
	 * 게시글 상세(답글까지, 파일+이미지(썸네일))
	 * @param vo
	 * @return
	 */
	@GetMapping("/detail")
	public ResponseEntity<Map<String, Object>> getBoardById(BoardVO vo) {
		Map<String, Object> resultInfo = service.getBoardById(vo);
		
		//200(성공) 서버가 요청을 제대로 처리했다
		return new ResponseEntity<>(resultInfo, HttpStatus.OK);
	}
	
	/**
	 * 게시글 등록 (답글 추가, 파일+이미지(썸네일))
	 * @param vo
	 * @return
	 */
	@PostMapping	//required=false 해당 필드가 쿼리스트링에 존재하지 않아도 예외가 발생하지 않는다고 한다
	public ResponseEntity<Void> insertBoard(@RequestParam Map<String, Object> commentData,
							@RequestParam(value = "files", required=false) MultipartFile[] files) {
		service.insertBoard(commentData, files);
		//201(작성됨) 성공적으로 요청되었으며 서버가 새 리소스를 작성했다.
		return new ResponseEntity<>(HttpStatus.CREATED);
	}
	
	/**
	 * 게시글 수정(답글까지, 파일+이미지(썸네일))
	 * @param vo
	 * @return
	 */
	@PostMapping("/update")
	public ResponseEntity<Void> updateBoard(@RequestParam Map<String, Object> commentData,
							@RequestParam(value = "files", required=false) MultipartFile[] files) {
		service.updateBoard(commentData, files);
		//200(성공) 서버가 요청을 제대로 처리했다
		return new ResponseEntity<>(HttpStatus.OK);
	}
	
	/**
	 * 게시글 삭제(답글까지, 파일+이미지(썸네일))
	 * @param vo
	 * @return
	 */
	@PostMapping("/delete")
	public ResponseEntity<Void> deleteBoard(@RequestBody BoardVO vo) {
		service.deleteBoard(vo);
		//204(콘텐츠 없음) 서버가 요청을 성공적으로 처리했지만 콘텐츠를 제공하지 않는다.
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}
	
}
