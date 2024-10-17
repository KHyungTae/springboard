package com.project.springboard.service;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.project.springboard.mapper.BoardServiceMapper;
import com.project.springboard.mapper.FileServiceMapper;
import com.project.springboard.vo.BoardVO;
import com.project.springboard.vo.FileVO;
import com.project.springboard.vo.PageVO;

import net.coobird.thumbnailator.Thumbnails;

@Service
public class BoardServiceImpl implements BoardService {
	
	@Autowired
	private BoardServiceMapper mapper;
	
	@Autowired
	private FileService service;
	
	@Autowired
	private FileServiceMapper fileMapper;
	
	//파일이 저장될 디렉토리 경로(업로드)
	private final String uploadDir = "C:/mine/fileDir";
	//썸네일 이미지 저장될 디렉토리 경로(업로드)
	private final String thumbnailDir = "C:/mine/thumbnailDir";
	
	//MIME타입 어러 확장자 추가하여 업로드
	private static final List<String> ALLOWED_MIME_TYPES = Arrays.asList(
			"image/gif",	//.gif
			"image/jpeg",	//.jpeg, .jpg
			"video/mp4",	//.mp4
			"audio/mpeg",	//.mp3
			"image/png",	//.png
			"application/pdf",	//.pdf
			"application/vnd.ms-powerpoint", //.ppt
			"application/vnd.openxmlformats-officedocument.presentationml.presentation", //.pptx
			"text/plain",	//.txt
			"application/vnd.ms-excel",	//xls
			"application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",	//xlsx
			"application/zip",	//.zip
			"application/msword",	//.doc
			"application/vnd.openxmlformats-officedocument.wordprocessingml.document" //.docx
			);

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
	public Map<String, Object> getBoardById(BoardVO vo) {
		// TODO 게시글 상세(답글 상세)
		Map<String, Object> resultInfo = new HashMap<String, Object>();
		
		//게시글에 등록된 정보를 가져옴.
		BoardVO boardVO = mapper.getBoardById(vo);
		//게시글에 등록된 파일,이미지 등을 불러옴
		List<FileVO> fileVO = fileMapper.selectFilesByBoardId(boardVO.getBoard_id());
		
		resultInfo.put("board", boardVO);
		resultInfo.put("files", fileVO);
		return  resultInfo;
	}

	@Override
	public void insertBoard(Map<String, Object> commentData, MultipartFile[] files) {
		// TODO 게시글 등록(답글 등록 + 파일업로드)
		BoardVO boardVO = new BoardVO();
		
		//캐스팅
		int boardIdCasting = Integer.parseInt((String) commentData.get("board_id"));
		int parentIdCasting = Integer.parseInt((String) commentData.get("parent_id"));
		
		//화면단에서 보낸 데이터를 맵으로 받아 등록할때 넣어주려는 데이터
		boardVO.setBoard_id(boardIdCasting);
		boardVO.setParent_id(parentIdCasting);
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
		
		if(files != null) {
			/* 다중 파일업로드(단일도됨) */
			for(MultipartFile file : files) {
				String saveName = null; //서버에 저장될 파일
				FileVO fileVO = null;	
				String thumbnailPath = null; //서버에 저장될 썸네일이미지
				
				if(!file.isEmpty()) {
					try {
						if(file.isEmpty()) {
							throw new IOException("파일이 비어 있습니다.");
						}
						//원본파일명
						String originalName = file.getOriginalFilename();
						//mime타입 사용하여 여러 확장자 업로드할 수 있게하려고 만든 변수
						String mimeType = file.getContentType();
						//MIME 타입 검증
						//contains(): List에 특정 값이 포함되어 있는지 확인
						if(!ALLOWED_MIME_TYPES.contains(mimeType)) {
							throw new IllegalArgumentException("허용되지 않는 파일 형식입니다: " + mimeType);
						}
						
						//저장될 파일명(고유하게 생성)
						saveName = UUID.randomUUID().toString() + "_" + originalName;
						//파일 저장 경로
						Path filePath = Paths.get(uploadDir, saveName); //java7~107~10q
						//Path filePath = Path.of(uploadDir, saveName); //java11이상
						
						//파일 크기(바이트 단위)
						//long fileSize = file.getSize();
						//파일 타입(MIME 타입)
						String fileType = file.getContentType();
						//이미지 여부 판단(MIME 타입이 "image"로 시작하면 이미지로 간주)
						//startsWith(): 비교 대상 문자열이 입력된 문자열(prefix)값으로 시작되는지 여부를 확인
						String isImage = fileType.startsWith("image") ? "Y" : "N";
						
						//데이터가 전송되는 통로 
						//getInputStream(): 업로드된 파일의 내용을 InputStream 형태로 반환, 이를 사용하여 파일 데이터를 읽을 수 있다.
						InputStream inputStream = file.getInputStream();
						//파일 저장(source, target, 파일이 이미 존재할 경우 덮어쓰기)
						Files.copy(inputStream, filePath, StandardCopyOption.REPLACE_EXISTING);
						
						//이미지 파일인 경우 썸네일 생성
						if(isImage.equals("Y")) {
							String thumbnailName = "thumb_" + saveName;
							Path thumbnailFilePath = Paths.get(thumbnailDir, thumbnailName);
							
							//thumbnailator 라이브러리 사용하여 썸네일 생성
							//.of(이미지 파일, URL, BufferedImage 또는 InputStream등 다양한 소스로부터 이미지를 불러옴)
							//.size(이미지의 크기를 변경, 비율을 유지, 지정된 크기에 맞춰 이미지가 리사이징)
							//.toFile(리사이즈나 포맷 변경 등 작업된 이미지를 파일로 저장)
							Thumbnails.of(filePath.toFile()).size(100, 100).toFile(thumbnailFilePath.toFile());
							thumbnailPath = thumbnailName; //썸네일 경로 설정(서버에 저장할 경로)
						}
						
						inputStream.close();
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				
				fileVO = new FileVO();
				
				fileVO.setBoard_id(boardVO.getBoard_id());	//게시판ID
				fileVO.setOriginal_file_nm(file.getOriginalFilename()); //원본파일명
				fileVO.setSave_file_nm(saveName);	//저장될 파일명
				fileVO.setFile_size(file.getSize());	//파일크기
				fileVO.setFile_type(file.getContentType()); //파일타입
				fileVO.setIs_image(file.getContentType().startsWith("image") ? "Y" : "N"); //이미지여부
				/* 이미지가 아닌경우 null값때문에 쿼리부분에서 JdbcType=VARCHAR를 넣어주니 null값이 들어감. */
				fileVO.setThumbnailpath(thumbnailPath); //썸네일 이미지 경로	
				
				Integer maxFileSeq = fileMapper.getMaxId();
				fileVO.setFile_id(maxFileSeq + 1);
				service.saveFileInfo(fileVO);
			}
		}
	}

	@Override
	public void updateBoard(Map<String, Object> commentData, MultipartFile[] files) {
		// TODO 게시글 수정(답글 수정)
		BoardVO boardVO = new BoardVO();
		
		//캐스팅
		int boardIdCasting = Integer.parseInt((String) commentData.get("board_id"));
		
		boardVO.setBoard_id(boardIdCasting);
		boardVO.setTitle((String) commentData.get("title"));
		boardVO.setContents((String) commentData.get("contents"));
		boardVO.setUpdate_nm((String) commentData.get("update_nm"));
		boardVO.setUpdate_id((String) commentData.get("update_id"));
		
		mapper.updateBoard(boardVO);
		
		if(files != null) {
			/* 다중 파일업로드(단일도됨) */
			for(MultipartFile file : files) {
				String saveName = null;
				FileVO fileVO = null;
				String thumbnailPath = null;
				
				if(!file.isEmpty()) {
					try {
						if(file.isEmpty()) {
							throw new IOException("파일이 비어 있습니다.");
						}
						//원본파일명
						String originalName = file.getOriginalFilename();
						//mime타입 사용하여 여러 확장자 업로드할 수 있게하려고 만든 변수
						String mimeType = file.getContentType();
						//MIME 타입 검증
						if(!ALLOWED_MIME_TYPES.contains(mimeType)) {
							throw new IllegalArgumentException("허용되지 않는 파일 형식입니다: " + mimeType);
						}
						
						//저장될 파일명(고유하게 생성)
						saveName = UUID.randomUUID().toString() + "_" + originalName;
						//파일 저장 경로
						Path filePath = Paths.get(uploadDir, saveName);
						//파일 크기(바이트 단위)
						//long fileSize = file.getSize();
						//파일 타입(MIME 타입)
						String fileType = file.getContentType();
						//이미지 여부 판단(MIME 타입이 "image"로 시작하면 이미지로 간주)
						String isImage = fileType.startsWith("image") ? "Y" : "N";
						
						InputStream inputStream = file.getInputStream();
						//파일 저장
						Files.copy(inputStream, filePath, StandardCopyOption.REPLACE_EXISTING);
						
						//이미지 파일인 경우 썸네일 생성
						if(isImage.equals("Y")) {
							String thumbnailName = "thumb_" + saveName;
							Path thumbnailFilePath = Paths.get(thumbnailDir, thumbnailName);
							
							//thumbnailator 라이브러리 사용하여 썸네일 생성
							Thumbnails.of(filePath.toFile()).size(140, 140).toFile(thumbnailFilePath.toFile());
							thumbnailPath = thumbnailName; //썸네일 경로 설정
						}
						
						inputStream.close();
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				
				fileVO = new FileVO();
				
				fileVO.setBoard_id(boardVO.getBoard_id());	//게시판ID
				fileVO.setOriginal_file_nm(file.getOriginalFilename()); //원본파일명
				fileVO.setSave_file_nm(saveName);	//저장될 파일명
				fileVO.setFile_size(file.getSize());	//파일크기
				fileVO.setFile_type(file.getContentType()); //파일타입
				fileVO.setIs_image(file.getContentType().startsWith("image") ? "Y" : "N"); //이미지여부
				/* 이미지가 아닌경우 null값때문에 쿼리부분에서 JdbcType=VARCHAR를 넣어주니 null값이 들어감. */
				fileVO.setThumbnailpath(thumbnailPath); //썸네일 이미지 경로	
				
				Integer maxFileSeq = fileMapper.getMaxId();
				fileVO.setFile_id(maxFileSeq + 1);
				service.saveFileInfo(fileVO);
			}
		}
	}

	@Override
	public void deleteBoard(BoardVO vo) {
		// TODO 게시글 삭제(답글 삭제)
		
		//board_id에 관련된 파일정보
		List<FileVO> fileVO = fileMapper.selectFilesByBoardId(vo.getBoard_id());
		
		//파일 삭제
		for(FileVO fileInfo : fileVO) {
			try {
				//저장된 파일 삭제
				Path filePath = Paths.get(uploadDir, fileInfo.getSave_file_nm());
				Files.deleteIfExists(filePath);
				
				//썸네일이 있을 경우 썸네일도 삭제
				if(fileInfo.getThumbnailpath() != null) {
					Path thumbnailPath = Paths.get(thumbnailDir, fileInfo.getThumbnailpath());
					Files.deleteIfExists(thumbnailPath);
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		fileMapper.deleteFiles(vo.getBoard_id()); //파일정보삭제
		mapper.deleteBoard(vo.getBoard_id());	//게시글정보삭제
	}
}
