package com.project.springboard.service;

import java.util.Map;

import org.springframework.web.multipart.MultipartFile;

import com.project.springboard.vo.BoardVO;

public interface BoardService {

	Map<String, Object> getBoardList(BoardVO vo); //모든 게시글 조회(답글까지)
	
	Map<String, Object> getBoardById(BoardVO vo); //상세 게시글 조회 (답글까지)
	
	void insertBoard(Map<String, Object> commentData, MultipartFile[] files); //게시글 등록 (답글까지)
	
	void updateBoard(Map<String, Object> commentData, MultipartFile[] files); //게시글 수정 (답글까지)
	
	void deleteBoard(BoardVO vo); //특정 게시글 삭제 (답글까지)
	
}
