<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>게시글(답글) 목록</title>
<!-- script -->
<script src="/js/jquery-3.6.0.min.js"></script>
<script src="/js/utils.js"></script>
<!-- css -->
<link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet" integrity="sha384-QWTKZyjpPEjISv5WaRU9OFeRpok6YctnYmDr5pNlyT2bRjXh0JMhjY6hW+ALEwIH" crossorigin="anonymous">
<link href="/css/pagination.css" rel="stylesheet">
<link href="/css/list_page.css" rel="stylesheet">
<link href="/css/search_box.css" rel="stylesheet">
</head>
<body>
	<div class="container">
		<header>
			<h1>게시판 글(답글) 목록</h1>
		</header>
		<div class="search-container">
			<select id="searchOption" title="검색구분">
				<option value="">:: 선택 ::</option>
				<option value="title">제목</option>
				<option value="regt_nm">작성자</option>
				<option value="update_nm">수정자</option>
			</select>
			<input type="text" id="searchWord" placeholder="검색어를 입력하세요">
			<button id="searchButton">검색</button>
		</div>
		<div style="text-align: right;">
        	총 <span id="total_items">0</span>건
        </div>
		<table class="table">
			<colgroup>
				<col width="7%"/>
				<col width="43"/>
				<col width="10%"/>
				<col width="15%"/>
				<col width="10%"/>
				<col width="15%"/>
			</colgroup>
			<thead>
				<tr>
					<th>순번</th>
					<th>제목</th>
					<th>작성자</th>
					<th>작성일자</th>
					<th>수정자</th>
					<th>수정일자</th>
				</tr>
			</thead>
			<tbody>
				
			</tbody>
		</table>
		<div id="pagination" class="pagination">
			
		</div>
		
		<div class="blueButton">
			<button type="button" onclick="javascript:insert()">글쓰기</button>
		</div>
	</div>
</body>
</html>


<script type="text/javascript">
	
	let currentPage = 1;	//현재 페이지
	let totalPages = 0;		//모든 페이지
	
	//실행할 준비가 되면 차례대로 실행.
	$(document).ready(function() {
		//URL의 page 파라미터 값을 넣어줌.
		//currentPage = getPageUrl();
		//페이지 첫 로드시
		setPageList(1);
		
		//페이지 클릭시
		$("#pagination").on("click", ".page-link", function(event) {
			event.preventDefault();
			let page = $(this).attr("data-page");
			if(page && page != currentPage) {
				setPageList(page); //클릭한 페이지
			}
		});
		
		//검색어 엔터 클릭
		$("#searchWord").on("keyup", function(key) {
			if(key.keyCode == 13) {
				$("#searchWord").val();
				$("#searchOption").val();
				
				setPageList(1);
			}
		});
		
	});
		
	/*URL의 page 파라미터값을 읽어옴. 못읽어오면 기본값 1
	function getPageUrl() {
		let params = new URLSearchParams(window.location.search);
		return parseInt(params.get('page')) || 1;
	}*/
	
	function setPageList(page) {
		
		//parseInt(문자열, radix): radix는 진수를 뜻함. 항상 10진수가 디폴트값이 아니기때문에 지정해주는것이 좋다고한다.
		//페이지 값이 문자열로 들어가 계속 더하기가 돼서 페이지가 안떠서 정수로 반환해줌.
		page = parseInt(page, 10);
		
		let datas = {
			"page": page,
			"searchWord": $("#searchWord").val(),
			"searchOption": $("#searchOption").val()
		};
		
		$.ajax({
			type: "GET",
			url: "/api/springboard",
			dataType: "JSON",
			data: datas, //서버에 보낼 데이터.
			success: function(res) {

				let list = res.resultList;
				let pageInfo = res.pageInfo;
				
				totalPages = pageInfo.totalPages; //총 페이지 수
				currentPage = page; //현재 페이지 초기화
				let totalItems = pageInfo.totalItems; //총 게시글 수
				let htmls = '';
				
				//게시글 리스트 ((result.total_count)-(result.rn)+1)
				$.each(list, function(idx, board) {
					htmls += '<tr>';
					htmls += '<td>'+((totalItems)-(board.rn)+1)+'</td>';	
					htmls += '<td style="text-align:left;">'
					if(board.parent_id == 0) {
						htmls += '<a href="#" role="button" class="view_page" data_id="'+board.board_id+'" style="color: black;">'+board.title+'</a>';
					} else {
						htmls += '<a href="#" role="button" class="view_page" data_id="'+board.board_id+'" style="margin-left: '+board.depth * 20+'px; color: black;">└ '+board.title+'</a>';
					}
					htmls += '</td>';
					htmls += '<td>'+board.regt_nm+'</td>';
					htmls += '<td>'+board.regt_date+'</td>';
					if(board.update_nm != null) {
						htmls += '<td>'+board.update_nm+'</td>';	
					} else {
						htmls += '<td></td>';
					}
					if(board.update_date != null) {
						htmls += '<td>'+board.update_date+'</td>';	
					} else {
						htmls += '<td></td>';
					}
					htmls += '</tr>';
				});
				
				if(list == '') {
					htmls = '<tr><td colspan="6">목록 데이터가 없습니다.</td></tr>';
				}
				
				$(".table > tbody").html(htmls);
				
				//페이징 설정(공통처리).
				kht.pagination.setData(currentPage, totalPages);
				
				//현재 페이지를 url에 반영하기.
				//history.pushState(null, '', '?page='+currentPage);
				
				$("#total_items").html(totalItems);
				
			},
			error: function() {
				alert("데이터를 불러오는데 실패했습니다. 에러:" + xhr.status);
			}

		});
	}
	
	$(document).on("click", '.view_page', function() {
		let board_id = $(this).attr("data_id");
		
		board_id = parseInt(board_id, 10);
		
		window.localStorage.setItem("boardList", board_id);
		window.location.href="/springboard/detail";
	});
	
	//검색 버튼 클릭시 검색 결과 보여주기
	$("#searchButton").on("click", function() {
		$("#searchWord").val();
		$("#searchOption").val();
		
		setPageList(1);
	});

	//등록 페이지 이동.
	function insert() {
		window.localStorage.removeItem("boardList");
		window.location.href="/springboard/insert";
	}
	
</script>