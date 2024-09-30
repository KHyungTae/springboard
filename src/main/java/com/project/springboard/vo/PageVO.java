package com.project.springboard.vo;

import lombok.Data;

@Data
public class PageVO {
	
	private int page = 1; //현재 페이지 번호currentPage
	private int size = 10; //한 페이지당 표시할 데이터 수countPerPage
	
	private int startRow; //시작페이지
	private int endRow; //마지막페이지
	
	private int totalPages;	//총 페이지 수
	private int totalItems; //총 게시물 수
	
	private String searchWord = "";	//검색어
	private String searchOption = "";	//검색구분

}
