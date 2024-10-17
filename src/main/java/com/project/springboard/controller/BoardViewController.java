package com.project.springboard.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/springboard")
public class BoardViewController {
	
	/**
	 * 게시글 목록 페이지 (답글 목록까지)
	 * @return
	 */
	@GetMapping("/list")
	public String listPage() {
		return "listPage";
	}
	
	/**
	 * 게시글 상세 페이지 (답글 상세까지)
	 * @return
	 */
	@GetMapping("/detail")
	public String getBoardBySeqPage() {
		return "detailPage";
	}
	
	/**
	 * 게시글 등록 페이지
	 * @return
	 */
	@GetMapping("/insert")
	public String insertFormPage() {
		return "insertPage";
	}
	
	/**
	 * 게시글 답글 등록 페이지(추가함)
	 * @return
	 */
	@GetMapping("/comments")
	public String insertFormCommentPage() {
		return "insertCommentPage";
	}
}
