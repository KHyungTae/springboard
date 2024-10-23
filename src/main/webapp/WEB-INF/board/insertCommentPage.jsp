<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>답글 등록 페이지</title>
<script src="/js/jquery-3.6.0.min.js"></script>
<link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet" integrity="sha384-QWTKZyjpPEjISv5WaRU9OFeRpok6YctnYmDr5pNlyT2bRjXh0JMhjY6hW+ALEwIH" crossorigin="anonymous">
<!-- 스마트에디터2 -->
<link rel="stylesheet" href="/smarteditor2/css/smart_editor2.css" type="text/css">
<link href="/css/form_container.css" rel="stylesheet">
<script type="text/javascript" src="/smarteditor2/js/HuskyEZCreator.js" charset="utf-8"></script>
<!--<script type="text/javascript" src="/smarteditor2/js/service/SE2BasicCreator.js" charset="utf-8"></script>-->
</head>
<body>
	<div class="container">
	<header>
		<h1>게시판 답글 등록</h1>
	</header>
	<form id="boardForm" method="POST" enctype="multipart/form-data">
		<div class="form-container">
		<table class="table">
			<tr>
				<th width="15%">제목</th>
				<td>
					<input type="text" id="title" name="title" size="120%"/>
				</td>
			</tr>
			<tr>
				<th style="align-content: center;">내용</th>
				<td>
					<textarea id="contents" name="contents" rows="10" cols="122"></textarea>
					<p class="count"><span>0</span> / 4000자까지</p>
				</td>
			</tr>
			<tr>
				<th width="15%">파일 첨부</th>
				<td>
					<input type="file" id="fileUpload" name="fileUpload" size="120%" multiple accept="image/*" />
				</td>
			</tr>
			<tr>
				<th width="15%">작성자</th>
				<td>
					<input type="text" id="regt_nm" name="regt_nm" size="120%"/>
				</td>
			</tr>
			<tr>
				<th width="15%">작성자ID</th>
				<td>
					<input type="text" id="regt_id" name="regt_id" size="120%"/>
				</td>
			</tr>
		</table>
		</div>
		<div class="blueButton">
			<button type="button" id="list">목록</button>
			<button type="submit" id="insert">등록</button>
		</div>
	</form>
	</div>
</body>
</html>

<script type="text/javascript">
	
	let localStorage = window.localStorage.getItem("boardInsert");
	let board_id = localStorage;
	let oEditors = [];
	
	$(document).ready(function() {
		
		setSmartEditor();
		setCommentPageInsert();
		
		$("#list").on("click", function() {
			window.location.href="/springboard/list";
		});
		
	});
	
	//smartEditor2 설정
	function setSmartEditor() {
		nhn.husky.EZCreator.createInIFrame({
			oAppRef: oEditors,
			elPlaceHolder: "contents", //textarea id
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
			},
			fCreator: "createSEditor2"
		});
		
		// editor에 글자를 쳤을 때 글자 수 표출되는 이벤트
		// setTimeout 을 안하면 iframe이 만들어지기 전에 이벤트가 등록되어 영역을 찾지 못한다
		setTimeout(function() {
			var ctntarea = document.querySelector("iframe").contentWindow.document.querySelector("iframe").contentWindow.document.querySelector(".se2_inputarea");
			ctntarea.addEventListener("keyup", function(e) {
				var text = this.innerHTML;
				text = text.replace(/<br>/ig, "");	// br 제거
				text = text.replace(/&nbsp;/ig, "");// 공백 제거
				text = text.replace(/<(\/)?([a-zA-Z]*)(\s[a-zA-Z]*=[^>]*)?(\s)*(\/)?>/ig, "");	// html 제거
				
				var len = text.length;
				document.querySelector(".count span").innerHTML = len;
				
				if(len > 4000) {
					alert("최대 4000 자까지 입력 가능합니다.");
				}
			});	
		}, 1000)
	}
	
	
	//답글 등록
	function setCommentPageInsert() {
		
		$("#boardForm").submit(function(event) {
			event.preventDefault();
			
			oEditors.getById["contents"].exec("UPDATE_CONTENTS_FIELD", []);
			
			
			if(validate()) {
				
				//const boardId = parseInt(board_id, 10);
				
				let formData = new FormData();
				
				formData.append("board_id", board_id);
				formData.append("parent_id", board_id);
				formData.append("title", $("input[name=title]").val());
				formData.append("contents", $("textarea[name=contents]").val());
				formData.append("regt_nm", $("input[name=regt_nm]").val());
				formData.append("regt_id", $("input[name=regt_id]").val());
				
				//다중파일업로드
				let fileInput = document.getElementById("fileUpload");
				
				for(let i = 0; i < fileInput.files.length; i++) {
					formData.append("fileUploads", fileInput.files[i]);
				}
				
				if(confirm("입력하신 내용을 등록하시겠습니까?")) {
				
					$.ajax({
						type: "POST",
						url: "/api/springboard",
						data: formData,
						processData: false,
						contentType: false,
						success: function() {
							alert("게시글이 등록되었습니다.");
							window.location.href="/springboard/list";
						},
						error: function(xhr, status, error) {
							alert("답글 등록에 실패했습니다. 에러" + xhr.status);
						}
					});
				}
				
			}
			
		});
	}
	
	//답글등록 검증
	function validate() {
		var content = $("#contents").val();
		
		if($("#title").val().trim() == '') {
			alert("답변 제목을 입력해주세요.");
			$("#title").focus();
			return false;
		}
		
		if(content == "" || content == null || content == '&nbsp;' || content == '<p>&nbsp;</p>') {
			alert("답변 내용을 작성하세요.");
			oEditors.getById["contents"].exec("FOCUS");
			return false;
		}
		
		if($("#regt_nm").val().trim() == '') {
			alert("답변등록 이름을 입력해주세요.");
			$("#regt_nm").focus();
			return false;
		}
		
		if($("#regt_id").val().trim() == '') {
			alert("답변등록 아이디를 입력해주세요.");
			$("#regt_id").focus();
			return false;
		}
		return true;
	}
</script>