package com.project.springboard.controller;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.URLEncoder;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.UUID;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.project.springboard.service.FileService;
import com.project.springboard.vo.FileVO;

@RestController
@RequestMapping("/api/file")
public class FileAPIController {

	@Autowired
	private FileService fileService;
	
	//파일이 저장될 디렉토리 경로(업로드)
	private final String uploadDir = "C:/mine/fileDir";
	//썸네일 이미지 저장될 디렉토리 경로(업로드)
	private final String thumbnailDir = "C:/mine/thumbnailDir";
	//에디터 이미지 저장될 디렉토리 경로(업로드)
	//private final String smarteditorDir = "C:/mine/smarteditorDir/";
	
	
	//파일 다운로드
	@GetMapping("/download")
	public ResponseEntity<Resource> downloadFile(@RequestParam String save_file_nm, @RequestParam String original_file_nm) {
		try {
			//Path클래스는 파일이나 디렉터리의 경로를 나타내는 객체, Paths클래스는 주로 경로를 문자열에서 Path객체로 변환하는 데 사용
			//Path - resolve(): 현재 경로에 다른 경로를 연결하여 새로운 경로를 반환함.
			//Path - normalize(): 경로에 포함된 또는 ..등을 제거하여 표준 경로를 반환함.
			//Paths - get(): 경로를 문자열로부터 Path객체를 생성, 여러 개의 문자열을 연결하여 하나의 경로로 만들 수 있음.
			Path filePath = Paths.get(uploadDir).resolve(save_file_nm).normalize();
			
			//toUri(): 경로를 웹 브라우저에서 사용하는 형식의 문자열로 변환하여 반환(반환된 URI는 파일 경로를 가리키는 절대 URI)
			Resource resource = new UrlResource(filePath.toUri());
			
			//한글이 들어갔을경우 인코딩을 해줄 수 없기 때문에 이 메서드를 사용하여 변환해준다.
			original_file_nm = URLEncoder.encode(original_file_nm, "utf-8");
			
			//존재 확인
			if(resource.exists()) {
				//파일 다운로드 시 원래 파일 이름을 설정
				return ResponseEntity.ok().contentType(MediaType.APPLICATION_OCTET_STREAM)
						.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + original_file_nm + "\"")
						.body(resource); 
			} else {
				return ResponseEntity.notFound().build();
			}
			
		} catch (Exception e) {
			// TODO: handle exception
			return ResponseEntity.status(500).build();
		}
	}
	
	//썸네일 다운로드
	@GetMapping("/thumbdownload")
	public ResponseEntity<Resource> thumbnailImage(@RequestParam String thumbnailpath, @RequestParam String original_file_nm) {
		try {
			Path filePath = Paths.get(thumbnailDir).resolve(thumbnailpath).normalize();
			
			Resource resource = new UrlResource(filePath.toUri());
			
			//한글이 들어갔을경우 인코딩을 해줄 수 없기 때문에 이 메서드를 사용하여 변환해준다.
			original_file_nm = URLEncoder.encode(original_file_nm, "utf-8");
			
			if(resource.exists()) {
				//파일 다운로드 시 원래 파일 이름을 설정
				return ResponseEntity.ok().contentType(MediaType.APPLICATION_OCTET_STREAM).header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + original_file_nm + "\"").body(resource); 
			} else {
				return ResponseEntity.notFound().build();
			}
			
		} catch (Exception e) {
			// TODO: handle exception
			return ResponseEntity.status(500).build();
		}
	}
	
	//파일 하나씩 삭제
	@PostMapping("/delete")
	public ResponseEntity<Void> deleteFile(@RequestBody FileVO vo) {
		fileService.deleteFile(vo);
		
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}
	
	//에디터 멀티 이미지업로드
	@PostMapping("/multiPhotoUpload")
    public void multiplePhotoUpload(HttpServletRequest req, HttpServletResponse res){
        try {
        	//파일정보
        	String sFileInfo = "";
        	//일반 원본파일명
        	String original_file_nm = req.getHeader("file-name");
        	String filePath = "C:/mine/smarteditorDir/";
        	
        	//경로
        	File uploadPath = new File(filePath);
        	if(!uploadPath.exists()) {
        		uploadPath.mkdirs();
        	}
        	
        	//서버저장 파일명
        	String save_file_nm = UUID.randomUUID().toString() + "_" + original_file_nm;
        	String realFileNm = filePath + save_file_nm;
        	
        	InputStream is = req.getInputStream();
        	OutputStream os=new FileOutputStream(realFileNm);
        	int numRead;
        	byte b[] = new byte[Integer.parseInt(req.getHeader("file-size"))];
        	while((numRead = is.read(b,0,b.length)) != -1){
        		os.write(b,0,numRead);
        	}
        	if(is != null) {
        		is.close();
        	}
        	os.flush();
        	os.close();

        	//정보 출력
        	sFileInfo += "&bNewLine=true";
        	//img 태그의 title 속성을 원본파일명으로 적용시켜주기 위함
        	sFileInfo += "&sFileName="+ original_file_nm;
        	sFileInfo += "&sFileURL="+"/smarteditorDir/"+save_file_nm;
        	
        	//바이트를 문자 형태를 가지는 객체로 바꿔준다.
			//응답으로 내보낼 출력 스트림을 얻어낸 후 text/html형태로 작성하여 스트림에 텍스트를 기록하는 것.
        	PrintWriter print = res.getWriter();
        	print.print(sFileInfo);
        	print.flush();
        	print.close();
        	
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
	
	//에디터 단일 이미지 업로드
	/*@PostMapping("/multiPhotoUpload")
    public ResponseEntity<Void> multiplePhotoUpload(MultipartFile file, HttpServletRequest req, HttpServletResponse res) {
		try {
					
				if(file.isEmpty()) {
					throw new IOException("파일이 비어 있습니다.");
				}
				
				String filePath = smarteditorDir;
				File saveDir = new File(filePath);
				if(!saveDir.exists()) {
					saveDir.mkdirs();
				}
				SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
				String today = formatter.format(new java.util.Date());
				String original_file_nm = file.getOriginalFilename();
				String file_ext = file.getOriginalFilename().split("\\.")[1];
				String save_file_nm = file.getOriginalFilename().split("\\.")[0] + UUID.randomUUID() + "-" + today + "." + file_ext;
				File save_file = new File(saveDir, save_file_nm);
				try {
					//Ajax를 이용해 Spring에서 지원하는 Multipart타입으로 받은 파일을 저장
					file.transferTo(save_file);
				} catch (Exception e) {
					// TODO: handle exception
					e.printStackTrace();
				}
				String sFileInfo = "";
				sFileInfo += "&bNewLine=true";
				//img 태그의 title 속성을 원본파일명으로 적용시켜주기 위함
				sFileInfo += "&sFileName=" + original_file_nm;
				sFileInfo += "&sFileURL=" + "/smarteditorDir/" + save_file_nm;
				
				
				//바이트를 문자 형태를 가지는 객체로 바꿔준다.
				//응답으로 내보낼 출력 스트림을 얻어낸 후 text/html형태로 작성하여 스트림에 텍스트를 기록하는 것.
				
				PrintWriter print = res.getWriter();
				print.print(sFileInfo);
				print.flush();
				print.close();
			}
			
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}*/
}
