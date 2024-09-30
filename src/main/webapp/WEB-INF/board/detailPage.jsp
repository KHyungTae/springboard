<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>게시글(답글) 상세/수정/삭제/댓글</title>
<!--<script src="https://code.jquery.com/jquery-3.6.0.min.js"></script>-->
<script src="/js/jquery-3.6.0.min.js"></script>
<link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet" integrity="sha384-QWTKZyjpPEjISv5WaRU9OFeRpok6YctnYmDr5pNlyT2bRjXh0JMhjY6hW+ALEwIH" crossorigin="anonymous">
<!-- 스마트에디터2 -->
<link rel="stylesheet" href="/smarteditor2/css/ko_KR/smart_editor2.css" type="text/css">
<link href="/css/form_container.css" rel="stylesheet">
<link href="/css/comment_group.css" rel="stylesheet">
<script type="text/javascript" src="/smarteditor2/js/service/HuskyEZCreator.js" charset="utf-8"></script>
<script type="text/javascript" src="/smarteditor2/js/service/SE2BasicCreator.js" charset="utf-8"></script>
</head>
<body>
	<div class="container">
	<header>
		<h1>게시판 글(답글) 상세 정보(수정 삭제)</h1>
	</header>
	<form id="boardForm">
	<div class="form-container">
	<table class="table">
		<tr>
			<th width="15%">게시글번호</th>
			<td id="board_id"></td>
		</tr>
		<tr>
			<th width="15%">제목</th>
			<td>
				<input type="text" id="title" name="title" size="120%"/>
			</td>
		</tr>
		<tr>
			<th style="align-content: center">내용</th>
			<td>
				<textarea id="contents" name="contents" rows="10" cols="122"></textarea>
			</td>
		</tr>
		<tr>
			<th width="15%">작성자</th>
			<td id="regt_nm"></td>
		</tr>
		<tr>
			<th width="15%">작성자ID</th>
			<td id="regt_id"></td>
		</tr>
		<tr>
			<th width="15%">작성일자</th>
			<td id="regt_date"></td>
		</tr>
		<tr>
			<th width="15%">수정자</th>
			<td>
				<input type="text" id="update_nm" name="update_nm" size="120%"/>
			</td>
		</tr>
		<tr>
			<th width="15%">수정자ID</th>
			<td>
				<input type="text" id="update_id" name="update_id" size="120%"/>
			</td>
		</tr>
		<tr>
			<th width="15%">수정일자</th>
			<td id="update_date"></td>
		</tr>
	</table>
	</div>
	<div class="blueButton">
		<button type="button" onclick="javascript:list()">목록</button>
		<button type="button" id="delete">삭제</button>
		<button type="submit" id="update">수정</button>
		<button type="button" id="insert" onclick="javascript:insertComment()">답글 등록</button>
	</div>
	
	</form>
	</div>
</body>
</html>


<script type="text/javascript">
	
	let localStorage = window.localStorage.getItem("boardList");
	let board_id = localStorage;
	let page = "";
	let oEditors = [];
	
	$(document).ready(function() {
		//현제 페이지 URL에서 board_id값을 추출함.
		//let board_id = window.location.pathname.split('/').pop();
		
		setPageView();
		setPageUpdate();
		setPageDelete();
		
	});
	
	//상세
	function setPageView() {
		$.ajax({
			type: "GET",
			url: "/api/springboard/detail",
			dataType: "JSON",
			data: {"board_id": board_id},
			success: function(res) {
				
				const view = res.resultInfo;
				
				$("#board_id").text(view.board_id);
				$("#title").val(view.title);
				//smarteditor2 초기화
				nhn.husky.EZCreator.createInIFrame({
					oAppRef: oEditors,
					elPlaceHolder: "contents",
					sSkinURI: "/smarteditor2/SmartEditor2Skin.html", //스킨파일 경로
					htParams: {
						bUseToolbar: true, //툴바 사용 여부
						bUseVerticalResizer: true, //세로 리사이저 사용 여부
						bUseModeChanger: true, //모드 변경(HTML, TEXT) 사용 여부
						fOnBeforeUnload: function() {
							//페이지 벗어나기 전에 호출되는 콜백
							
						}
					},
					fOnAppLoad: function() {
						//에디터 로드 후 실행할 코드
						oEditors.getById["contents"].exec("SET_IR",[view.contents]);
					},
					fCreator: "createSEditor2"
				});
				$("#regt_nm").text(view.regt_nm);
				$("#regt_id").text(view.regt_id);
				$("#regt_date").text(view.regt_date);
				$("#update_nm").val(view.update_nm);
				$("#update_id").val(view.update_id);
				$("#update_date").text(view.update_date);
				
			},
			error: function(xhr, status, error) {
				alert("데이터를 불러오는데 실패했습니다. 에러:" + xhr.status);
			}
			
		});
	}
		
		
	//수정
	function setPageUpdate() {
		$("#boardForm").submit(function(event) {
			event.preventDefault();
			
			//수정버튼 클릭시 메시지
			if(!confirm("수정하신 내용을 등록하겠습니까?")) {
				return;
			}
			
			//에디터 내용 동기화(에디터 내용을 textarea에 반영)
			oEditors.getById["contents"].exec("UPDATE_CONTENTS_FIELD", []);
			
			const formData = {};
			formData.title = $("#title").val();
			formData.contents = $("#contents").val();
			formData.update_nm = $("#update_nm").val();
			formData.update_id = $("#update_id").val();
			formData.board_id = board_id;
			
			$.ajax({
				type: "PUT",
				url: "/api/springboard/update",
				contentType: "application/json",
				data: JSON.stringify(formData),
				success: function() {
					alert("게시글이 수정되었습니다.");
					window.location.href="/springboard/list";
				},
				error: function(xhr, status, error) {
					alert("게시글 수정에 실패했습니다. 에러:" + xhr.status);
				}
			});
			
		});
	}
		
	//삭제
	function setPageDelete() {
		$("#delete").click(function() {
			if(!confirm("게시글을 삭제하시겠습니까?")) {
				return;
			}
			
			$.ajax({
				type: "DELETE",
				url: "/api/springboard/delete",
				data: {"board_id":board_id},
				success: function() {
					alert("게시글이 삭제되었습니다.");
					window.location.href="/springboard/list";
				},
				error: function(xhr, status, error) {
					alert("게시글 삭제 중 오류가 발생하였습니다. 에러:" + xhr.status);
				}
			});
		});	
	}
	
	//게시글 답글 등록
	function insertComment() {
		window.localStorage.setItem("boardInsert", board_id);
		window.location.href="/springboard/comments"
	}
	
	//목록이동
	function list() {
		//window.location.href="/springboard";
		window.history.back();
	}
	
</script>