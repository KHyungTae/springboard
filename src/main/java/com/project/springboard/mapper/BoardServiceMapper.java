package com.project.springboard.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.project.springboard.vo.BoardVO;

@Mapper
public interface BoardServiceMapper {
	
	//@Param 사용하려다 안함.
	List<BoardVO> getBoardList(BoardVO vo); //모든 게시글 조회 (답글까지)
	
	int totalCountBoard(BoardVO vo); //총 게시글 수 (답글포함)
	
	BoardVO getBoardById(BoardVO vo); //상세 게시글 조회 (답글까지)
	
	void insertBoard(BoardVO vo); //게시글 등록 (답글까지)
	
	int updateBoard(BoardVO vo); //게시글 수정 (답글까지)
	
	int deleteBoard(int board_id); //특정 게시글 삭제 (답글까지)
	
	Integer getMaxId(); //가장 큰 seq값을 가져옴 (Impl에서 가장큰값에 +1을 더해 순번이 순차적으로 등록되게함.)
	
	//추가 (답글 추가하기위해 특정 게시글(특정 답글) 조회)
	BoardVO getBoardByCommentId(int board_id);
	
}
