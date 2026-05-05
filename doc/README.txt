# 자바 패키지 상세 (com.omnibuscode.*)
    - auth : 타 서비스와 연동하기 위한 사용자 인증 정보를 관리
    - base : 사용자 세션 정보와 어플리케이션 환경 정보를 관리
    - ctrl : 클라이언트의 요청을 처리하는 Servlet 을 관리
    - dao : 데이터베이스에 억세스
    - logic : 비즈니스 레이어를 담당하며 반환하는 데이터의 구성 규칙은 Json으로 정의
      > chatgpt : openai api를 위한 패키지
      > file : 서비스 저장소(지니박스, 구글 드라이브)를 접근하여 파일을 관리
      > jbg : 장보고 기능 관련 클래스 모음
      > mon : 모니터용 데몬 쓰레드 클래스 모음
    - util : 데이터 처리 및 유틸성 프로그램을 관리
  
# 배포 절차
    - 톰캣 정지
    - C:\Users\11A\apache-tomcat\apache-tomcat-9.0.76\webapps\ 폴더에서 jbs.war 와 jbs 폴더 삭제
    - 이클립스에서 프로젝트를 C:\Users\11A\apache-tomcat\apache-tomcat-9.0.76\webapps\ 경로로 익스포트
    - 톰캣 시작후 정지하여 어플리케이션 배포
    - 도스커맨드에서 다음의 명령어 실행
        > mklink /j "C:\Users\11A\apache-tomcat\apache-tomcat-9.0.76\webapps\jbs\user_files" "D:\SVN\BOX_MANAGER\JINIEBOX\output\deploy\user_files"
        > xcopy "D:\SVN\boxes\jiniebox\output\deploy\jbs" "C:\Users\11A\apache-tomcat\apache-tomcat-9.0.76\webapps\jbs" /e /h /k /y
        > 위의 명령어는 [jiniebox 프로젝트]\doc\ 경로에서 deploy.bat 로 작성되어 있다.
    - 톰캣 시작

# build.gradle refresh 이후 조치 절차
    - [Properties / Java Build Path / Source] 에 [jiniebox/app/src] 하위의 main, test 를 추가
    - [Properties / Java Build Path / Libraries / Classpath / JRE System Library] -> java-11 로 변경
    - [Properties / Java Build Path / Libraries / Classpath / Project and External Dependencies] 의 jar 들의 경로에서 jar 파일들을 copy 하여 [WEB-INF/lib] 경로로 복사후 Classpath 에서 삭제
    - [WEB-INF/lib] 경로의 jar 들의 버전들을 검토하여 삭제
    - [Properties / Java Build Path / Libraries / Classpath / Server Runtime] 추가
    - [Properties / Java Compiler / Compiler compliance level] 을 11 로 변경
    - [Properties / Project Facets / Java] 를 11로 변경
    - [Project Explorer / jiniebox / app] 에 대해서도 위와 동일하게 적용