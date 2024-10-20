# 게시판 혼자 만들어보기!

## 개발환경
  1. STS4툴(sts-4.24.0.RELEASE)
  2. springboot 2.5.5 (Maven)
  3. 외장 톰캣 9
  4. JDK 11
  5. Jquery-3.6.0
  6. .JSP(JSTL)
  7. .XML(Mybatis 2.2.0)
  8. Oracle(ojdbc6/11.2.0.4)
  9. lombok
  10. 스마트에디터2 사용 (이미지 등록 가능, 2.8.2.3)
---
## 구성
  1. ViewController (.jsp연결 화면)
  2. APIComtroller (ajax통신)
  3. Service (인터페이스)
  4. ServiceImpl (비즈니스로직)
  5. ServiceMapper (인터페이스)
  6. VO (데이터 저장)
  7. .xml (쿼리)
  8. .jsp (화면, JavaScript(Jquery))
---
## 기능들
  1. 목록: 게시글 목록(답글에 답글 목록), 검색, 페이징
  2. 등록: 게시글 등록, 답글 등록, 파일업로드 등록, 썸네일 생성 
  3. 상세: 상세 게시글, 파일다운로드
  4. 수정: 게시글 수정, 답글 수정, 파일업로드 추가, 썸네일 추가 생성
  5. 삭제: 게시글 삭제(파일까지), 답글 삭제, 파일 개별 삭제
---
### 수정할 부분
  1. max seq 주의 (동시 글등록 시 이슈발생)
  2. exception 따로 만들어서 화면에 던지기. (try{}catch(){} 막 쓰지 않기)
