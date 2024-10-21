package com.project.springboard.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.project.springboard.mapper.FileServiceMapper;
import com.project.springboard.vo.FileVO;

@Service
//@Transactional(readOnly = true)
public class FileServiceImpl implements FileService {
	
	//파일이 저장될 디렉토리 경로(업로드)
	private final String uploadDir = "C:/mine/fileDir";
	//썸네일 이미지 저장될 디렉토리 경로(업로드)
	private final String thumbnailDir = "C:/mine/thumbnailDir";
	
	@Autowired
	private FileServiceMapper fileServiceMapper;

	@Override
	//@Transactional
	public void deleteFile(FileVO vo) {
		// TODO 파일 하나씩 삭제
		
		//file_id로 관련된 파일정보
		List<FileVO> fileVO = fileServiceMapper.selectFilesByBoardId(vo.getFile_id());
		//파일 삭제
		for(FileVO fileInfo : fileVO) {
			try {
				//저장된 파일 삭제
				Path filePath = Paths.get(uploadDir, fileInfo.getSave_file_nm());
				Files.deleteIfExists(filePath);
				
				//썸네일이 있을 경우 썸네일도 삭제
				if(fileInfo.getThumbnailpath() != null) {
					Path thumbnailPath = Paths.get(thumbnailDir, fileInfo.getThumbnailpath());
					Files.deleteIfExists(thumbnailPath);
				}
			} catch(IOException e) {
				e.printStackTrace();
			}
		}
		fileServiceMapper.deleteFile(vo.getFile_id()); //파일정보삭제
	}

	@Override
	//@Transactional
	public void saveFileInfo(FileVO fileVO) {
		//파일 정보를 DB에 저장
		fileServiceMapper.insertFileInfo(fileVO);		
	}

	@Override
	public List<FileVO> getFileByBoardId(int board_id) {
		//특정 게시글의 파일 목록 조회
		return fileServiceMapper.selectFilesByBoardId(board_id);
	}

}
