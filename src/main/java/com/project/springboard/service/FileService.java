package com.project.springboard.service;

import java.util.List;

import com.project.springboard.vo.FileVO;

public interface FileService {
	
	//파일 삭제
	void deleteFile(FileVO vo);
	
	//파일 정보를 데이터베이스에 저장
	void saveFileInfo(FileVO fileVO);
	
	//특정 게시글의 파일 목록을 조회
	List<FileVO> getFileByBoardId(int board_id);
	
}
