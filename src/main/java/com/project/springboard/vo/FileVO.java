package com.project.springboard.vo;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FileVO {

	private int file_id;			//파일ID
	private int board_id;			//게시글ID
	private String original_file_nm;//원본 파일명
	private String save_file_nm;	//저장된 파일명
	private long file_size;			//파일크기
	private String file_type;		//파일타입
	private String is_image;		//이미지여부 Y/N
	private String upload_date;		//파일업로드 날짜
	private String thumbnailpath;	//썸네일 이미지 경로
		
}
