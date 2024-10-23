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
<link rel="stylesheet" href="/smarteditor2/css/smart_editor2.css" type="text/css">
<link href="/css/form_container.css" rel="stylesheet">
<link href="/css/comment_group.css" rel="stylesheet">
<script type="text/javascript" src="/smarteditor2/js/HuskyEZCreator.js" charset="utf-8"></script>
<!--<script type="text/javascript" src="/smarteditor2/js/service/SE2BasicCreator.js" charset="utf-8"></script>-->
<style>
a {
	text-decoration: none;
	color: black;
}
</style>
</head>
<body>
	<div class="container">
	<header>
		<h1>게시판 글(답글) 상세 정보(수정 삭제)</h1>
	</header>
	<form id="boardForm" method="POST" enctype="multipart/form-data">
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
				<p class="count"><span>0</span> / 4000자까지</p>
			</td>
		</tr>
		<tr>
			<th style="align-content: center">첨부파일</br>(파일, 이미지 클릭시 다운로드)</th>
			<td>
				<div id="fileList">
					<p>첨부 파일이 없습니다.<p>
				</div>
				</br>
				<input type="file" id="fileUpload" name="fileUpload" size="120%" multiple accept="/image/**" />
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
	let oEditors = [];
	
	$(document).ready(function() {
		//현제 페이지 URL에서 board_id값을 추출함.
		//let board_id = window.location.pathname.split('/').pop();
		
		setPageView();		//상세
		setPageUpdate();	//수정
		setPageDelete();	//전체삭제
		setFileDelete();	//단일삭제
		
	});
	
	//상세
	function setPageView() {
		$.ajax({
			type: "GET",
			url: "/api/springboard/detail",
			dataType: "JSON",
			data: {"board_id": board_id},
			success: function(res) {
				
				const view = res.board;
				const fileImage = res.files;
				
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
				
				let fileListDiv = $("#fileList");
				fileListDiv.empty();
				let htmls = '';
				if(fileImage && fileImage.length > 0) {
					$.each(fileImage, function(idx, filei) {
						if(filei.is_image == "Y" && filei.thumbnailpath) {
							//이미지 파일인 경우 썸네일을 표시
							//htmls += '<a href="/thumbnailDir/'+filei.thumbnailpath+'" download="'+filei.original_file_nm+'">';
							htmls += '<div file_id="'+filei.file_id+'" style="display: ruby;">';
							htmls += '<a href="/api/file/thumbdownload?thumbnailpath='+encodeURIComponent(filei.thumbnailpath)+'&original_file_nm='+encodeURIComponent(filei.original_file_nm)+'">';
							htmls += '<img src="/thumbnailDir/'+filei.thumbnailpath+'" alt="'+filei.original_file_nm+'"/>&nbsp;';
							htmls += '</a>';
							htmls += '<button type="button" class="fileDelete">삭제</button>&nbsp;';
							htmls += '</div>';
							
						} else {
							//이미지 파일이 아닌 경우 텍스트 표시
							//htmls += '<a href="/fileDir/'+filei.save_file_nm+'" download="'+filei.original_file_nm+'">';
							htmls += '<div file_id="'+filei.file_id+'" style="display: ruby;">';
							htmls += '<a href="/api/file/download?save_file_nm='+encodeURIComponent(filei.save_file_nm)+'&original_file_nm='+encodeURIComponent(filei.original_file_nm)+'">';
							htmls += filei.original_file_nm+'&nbsp;';
							htmls += '</a>';
							htmls += '<button type="button" class="fileDelete">삭제</button>&nbsp;';
							htmls += '</div>';
						}
					});
				} else {
					htmls += '<p>첨부파일이 없습니다.</p>';
				}
				$("#fileList").html(htmls);
				
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
			
			//에디터 내용 동기화(에디터 내용을 textarea에 반영)
			oEditors.getById["contents"].exec("UPDATE_CONTENTS_FIELD", []);
			
			if(validate()) {
				let formData = new FormData();
							
				formData.append("title", $("#title").val());
				formData.append("contents", $("#contents").val());
				formData.append("update_nm", $("#update_nm").val());
				formData.append("update_id", $("#update_id").val());
				formData.append("board_id", board_id);
				
				//파일업로드 추가
				let fileInput = document.getElementById("fileUpload");
				for(let i = 0; i < fileInput.files.length; i++) {
					formData.append("fileUploads", fileInput.files[i]);
				}
				
				console.log("formData: " + JSON.stringify(formData));
				
				if(confirm("수정하신 내용을 등록하겠습니까?")) {
					
					$.ajax({
						type: "POST",
						url: "/api/springboard/update",
						data: formData,
						processData: false,
						contentType: false,
						success: function() {
							alert("게시글이 수정되었습니다.");
							window.location.href="/springboard/list";
						},
						error: function(xhr, status, error) {
							alert("게시글 수정에 실패했습니다. 에러:" + xhr.status);
						}
					});
				}
			}
			
		});
	}
		
	//전체삭제(게시글, 파일)
	function setPageDelete() {
		$("#delete").click(function() {
			if(!confirm("게시글을 삭제하시겠습니까?")) {
				return;
			}
			
			$.ajax({
				type: "POST",
				url: "/api/springboard/delete",
				contentType: "application/json",
				data: JSON.stringify({"board_id":board_id}),
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
	
	//단일삭제(파일)
	function setFileDelete() {
		$(document).on("click", ".fileDelete", function() {
			let file_id = $(this).parent("div").attr("file_id");
			
			$.ajax({
				type: "POST",
				url: "/api/file/delete",
				contentType: "application/json",
				data: JSON.stringify({"file_id":file_id}),
				success: function() {
					alert("이미지/파일이 삭제되었습니다.");	
					window.location.reload();
				},
				error: function(xhr, status, error) {
					alert("이미지/파일이 삭제 중 오류가 발생하였습니다. 에러:" + xhr.status);
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
	
	
	//게시글등록 검증
	function validate() {
		var content = $("#contents").val();
		
		if($("#title").val().trim() == '') {
			alert("수정할 제목을 입력해주세요.");
			$("#title").focus();
			return false;
		}
					
		if(content == "" || content == null || content == '&nbsp;' || content == '<p>&nbsp;</p>') {
			alert("수정할 내용을 작성하세요.");
			oEditors.getById["contents"].exec("FOCUS");
			return false;
		}
		
		if($("#update_nm").val().trim() == '') {
			alert("수정자 이름을 입력해주세요.");
			$("#regt_nm").focus();
			return false;
		}
		
		if($("#update_id").val().trim() == '') {
			alert("수정자 아이디를 입력해주세요.");
			$("#regt_id").focus();
			return false;
		}
		return true;
	}
	
</script>