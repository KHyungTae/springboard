package com.project.springboard.vo;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FileVO {

	
	private int file_id;
	private int board_id;
	private String original_file_nm;
	private String save_file_nm;
	private int file_size;
	private String file_type;
	private String upload_date;

}
