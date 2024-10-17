package com.project.springboard.vo;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BoardVO extends PageVO {
	
	private int board_id = 0;		//게시글 ID
	private String title;			//제목
	private String contents;		//내용
	private String regt_nm;			//작성자 이름
	private String regt_id;			//작성자 아이디
	private String regt_date;		//작성날짜
	private String update_nm;		//수정자 이름
	private String update_id;		//수정자 아이디
	private String update_date;		//수정날짜
	
	private int total_count = 0; 	//총 게시물 수 (답글 추가로 필요없어짐.)
	private int rn = 0;				//게시물 순서
	
	/* 답글 계층구조 */
	private int parent_id = 0;		//부모 답글 ID
	private int depth;				//답글의 깊이
	private int group_id;			//답글 그룹
	private int order_in_group;		//답글 그룹 내 순서
	
}
