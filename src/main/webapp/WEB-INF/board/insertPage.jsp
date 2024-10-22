<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>게시글 등록</title>
<script src="/js/jquery-3.6.0.min.js"></script>
<link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet" integrity="sha384-QWTKZyjpPEjISv5WaRU9OFeRpok6YctnYmDr5pNlyT2bRjXh0JMhjY6hW+ALEwIH" crossorigin="anonymous">
<link href="/css/form_container.css" rel="stylesheet">
<!-- 스마트에디터2 -->
<link rel="stylesheet" href="/smarteditor2/css/smart_editor2.css" type="text/css">
<script type="text/javascript" src="/smarteditor2/js/HuskyEZCreator.js" charset="utf-8"></script>
<!--<script type="text/javascript" src="/smarteditor2/js/SE2BasicCreator.js" charset="utf-8"></script>-->
</head>
<body>
	<div class="container">
	<header>
		<h1>게시판 글 등록</h1>
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
	
	let oEditors = []; //에디터
	
	$(document).ready(function() {
		
		setSmartEditor();
		setPageInsert();
		
		//목록 이동.
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

	//게시글 등록
	function setPageInsert() {
		//폼 제출 시 에디터 내용 동기화 및 전송
		$("#boardForm").submit(function(event) {
			event.preventDefault();
			
			//에디터 내용 동기화(에디터 내용을 textarea에 반영)
			oEditors.getById["contents"].exec("UPDATE_CONTENTS_FIELD", []);
						
			if(validate()) {
				
				const parentId = 0;
				const boardId = 0;
				
				let formData = new FormData();
				
				formData.append("board_id", boardId);
				formData.append("parent_id", parentId);
				formData.append("title", $("input[name=title]").val());
				formData.append("contents", $("textarea[name=contents]").val());
				formData.append("regt_nm", $("input[name=regt_nm").val());
				formData.append("regt_id", $("input[name=regt_id").val());
				
				//다중파일업로드
				let fileInput = document.getElementById("fileUpload");
				//let fileInput = $("input[name=fileUpload]");
				for(let i = 0; i < fileInput.files.length; i++) {
					formData.append("files", fileInput.files[i]);
				}
				
				console.log("formData: " + JSON.stringify(formData));
				
				if(confirm("입력하신 내용을 등록하시겠습니까?")) {
				
					$.ajax({
						type: "POST",
						url: "/api/springboard",
						data: formData,
						processData: false, //파일 데이터를 변환하지 않도록 설정 
						contentType: false, //파일 업로드 시 반드시 false로 설정
						success: function() {
							alert("게시글이 등록되었습니다.");
							window.location.href="/springboard/list";
						},
						error: function(xhr, status, error) {
							alert("게시글 등록에 실패했습니다. 에러" + xhr.status);
						}
					});
				}
			}
			
		});
	}
	
	//게시글등록 검증
	function validate() {
		var content = $("#contents").val();
		
		if($("#title").val().trim() == '') {
			alert("등록할 제목을 입력해주세요.");
			$("#title").focus();
			return false;
		}
		
		if(content == "" || content == null || content == '&nbsp;' || content == '<p>&nbsp;</p>') {
			alert("등록할 내용을 작성하세요.");
			oEditors.getById["contents"].exec("FOCUS");
			return false;
		}
		
		if($("#regt_nm").val().trim() == '') {
			alert("작성자 이름을 입력해주세요.");
			$("#regt_nm").focus();
			return false;
		}
		
		if($("#regt_id").val().trim() == '') {
			alert("작성자 아이디를 입력해주세요.");
			$("#regt_id").focus();
			return false;
		}
		return true;
	}
	
</script>