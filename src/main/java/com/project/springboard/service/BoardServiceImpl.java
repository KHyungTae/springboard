package com.project.springboard.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.project.springboard.mapper.BoardServiceMapper;
import com.project.springboard.vo.BoardVO;
import com.project.springboard.vo.PageVO;

@Service
public class BoardServiceImpl implements BoardService {
	
	@Autowired
	private BoardServiceMapper mapper;

	@Override
	public Map<String, Object> getBoardList(BoardVO vo) {
		// TODO 게시글 목록(검색, 페이징, 답글)
		Map<String, Object> resultMap = new HashMap<String, Object>();
		
		//총 게시글 수
		int totalItems = mapper.totalCountBoard(vo);
		
		//총페이지 수 (ceil 올림.)  
		int totalPages = (int) Math.ceil((double) totalItems / vo.getSize());
		
		//페이지당 시작 번호(1 - 11 - 21 - 31 ...)
		int startRow = (vo.getPage() -1) * vo.getSize() +1;
		
		//페이지당 끝 번호(10 - 20 - 30 ...)
		int endRow = vo.getPage() * vo.getSize();
		
		vo.setStartRow(startRow); //한페이지에 페이징 시작번호
		vo.setEndRow(endRow);	//한페이지에 페이징 끝번호
		vo.setSearchOption(vo.getSearchOption()); //검색구분
		vo.setSearchWord(vo.getSearchWord());	//검색어
		List<BoardVO> resultList = mapper.getBoardList(vo);
		
		PageVO pageVO = new PageVO();
		pageVO.setPage(vo.getPage());
		pageVO.setSize(vo.getSize());
		pageVO.setTotalPages(totalPages);
		pageVO.setTotalItems(totalItems);
		
		resultMap.put("resultList", resultList);
		resultMap.put("pageInfo", pageVO);
		
		return resultMap;
	}

	@Override
	public BoardVO getBoardById(BoardVO vo) {
		// TODO 게시글 상세(답글 상세)
		return  mapper.getBoardById(vo);
	}

	@Override
	public void insertBoard(Map<String, Object> commentData) {
		// TODO 게시글 등록(답글 등록)
		BoardVO boardVO = new BoardVO();
		
		//화면단에서 보낸 데이터를 맵으로 받아 등록할때 넣어주려는 데이터
		boardVO.setBoard_id((int) commentData.get("board_id"));
		boardVO.setParent_id((int) commentData.get("parent_id"));
		boardVO.setTitle((String) commentData.get("title"));
		boardVO.setContents((String) commentData.get("contents"));
		boardVO.setRegt_id((String) commentData.get("regt_id"));
		boardVO.setRegt_nm((String) commentData.get("regt_nm"));
		
		//부모 댓글이 있을 경우 depth와 groupId 설정
		if(boardVO.getParent_id() != 0) {
			//화면단에서 comment_seq를 parent_id에 넣어 보내준 값으로 특정 댓글 조회
			BoardVO parentComment = mapper.getBoardByCommentId(boardVO.getParent_id());
			//부모 댓글이 있으므로 +1을 해줌으로 댓글의 깊이를 정함.(최상위 댓글 깊이 0)
			boardVO.setDepth(parentComment.getDepth() + 1);
			//최초등록한 댓글에 그룹아이디를 가져와 그 댓글에 대댓글도 그 그룹에 포함시켜 같은 그룹번호를 갖을 수 있게 함.
			boardVO.setGroup_id(parentComment.getGroup_id());
			
		//부모 댓글이 없을 경우
		} else {
			//최상위 깊이 0
			boardVO.setDepth(0);
			//부모 댓글이 없으므로 최초댓글 등록시 Group_id에 자기 댓글 번호를 그룹아이디로 저장.
			boardVO.setGroup_id(boardVO.getBoard_id());
		}
		
		//comment_seq값이 일정하게 들어갈 수 있게 마지막 seq를 찾아서 +1준다.
		Integer maxSeq = mapper.getMaxId();
		boardVO.setBoard_id(maxSeq + 1);
		mapper.insertBoard(boardVO);
	}

	@Override
	public void updateBoard(BoardVO vo) {
		// TODO 게시글 수정(답글 수정)
		mapper.updateBoard(vo);
	}

	@Override
	public void deleteBoard(BoardVO vo) {
		// TODO 게시글 삭제(답글 삭제)
		mapper.deleteBoard(vo);
	}
	
}
