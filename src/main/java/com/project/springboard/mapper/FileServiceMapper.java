package com.project.springboard.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.project.springboard.vo.FileVO;

@Mapper
public interface FileServiceMapper {

	void insertFileInfo(FileVO fileVO); //파일 업로드

	List<FileVO> selectFilesByBoardId(int board_id); //파일정보
	
	Integer getMaxId(); //가장 큰 seq값을 가져옴 (Impl에서 가장큰값에 +1을 더해 순번이 순차적으로 등록되게함.)
	
	int deleteFiles(int board_id); //파일 여러게 삭제
	
	int deleteFile(int file_id); //파일 하나만 삭제

}
