package com.omnibuscode.ftp;

import java.io.File;

import org.apache.ftpserver.FtpServer;
import org.apache.ftpserver.FtpServerFactory;
import org.apache.ftpserver.ftplet.FtpStatistics;
import org.apache.ftpserver.listener.ListenerFactory;
import org.apache.ftpserver.usermanager.PropertiesUserManagerFactory;

/**
 * Apache FTP Server를 프로그래밍 방식으로 시작/정지하는 관리 클래스
 */
public class FtpServerManager {
    
    private FtpServer ftpServer;
    private String ftpServerPath;
    private int port;
    private boolean isRunning = false;
    private org.apache.ftpserver.ftplet.UserManager userManager;  // 실행 중인 UserManager 인스턴스
    
    /**
     * FtpServerManager 생성자
     * @param ftpServerPath FTP 서버 설치 경로
     * @param port FTP 서버 포트 번호
     */
    public FtpServerManager(String ftpServerPath, int port) {
        this.ftpServerPath = ftpServerPath;
        this.port = port;
    }
    
    /**
     * FTP 서버를 시작합니다
     * @throws Exception
     */
    public void startServer() throws Exception {
        if (isRunning) {
            System.out.println("FTP 서버가 이미 실행 중입니다.");
            return;
        }
        
        System.out.println("FTP 서버를 시작합니다...");
        
        // FTP 서버 팩토리 생성
        FtpServerFactory serverFactory = new FtpServerFactory();
        
        // 리스너 설정
        ListenerFactory listenerFactory = new ListenerFactory();
        listenerFactory.setPort(port);
        serverFactory.addListener("default", listenerFactory.createListener());
        
        // 사용자 관리자 설정
        PropertiesUserManagerFactory userManagerFactory = new PropertiesUserManagerFactory();
        File usersFile = new File(ftpServerPath + "/res/conf/users.properties");
        userManagerFactory.setFile(usersFile);
        
        // UserManager 인스턴스 생성 및 저장
        this.userManager = userManagerFactory.createUserManager();
        serverFactory.setUserManager(this.userManager);
        
        // FTP 서버 생성
        ftpServer = serverFactory.createServer();
        
        // 서버 시작
        ftpServer.start();
        isRunning = true;
        
        System.out.println("FTP 서버가 포트 " + port + "에서 시작되었습니다.");
        System.out.println("사용자 정보 파일: " + usersFile.getAbsolutePath());
    }
    
    /**
     * FTP 서버를 정지합니다
     */
    public void stopServer() {
        if (!isRunning || ftpServer == null) {
            System.out.println("FTP 서버가 실행 중이 아닙니다.");
            return;
        }
        
        System.out.println("FTP 서버를 정지합니다...");
        ftpServer.stop();
        isRunning = false;
        System.out.println("FTP 서버가 정지되었습니다.");
    }
    
    /**
     * FTP 서버 실행 상태를 확인합니다
     * @return true: 실행 중, false: 정지 상태
     */
    public boolean isRunning() {
        return isRunning && ftpServer != null && !ftpServer.isStopped();
    }
    
    /**
     * FTP 서버 상태를 출력합니다
     */
    public void printStatus() {
        System.out.println("\n" + "=".repeat(100));
        System.out.println("FTP 서버 상태");
        System.out.println("=".repeat(100));
        System.out.println("서버 상태: " + (isRunning() ? "실행 중" : "정지됨"));
        System.out.println("서버 경로: " + ftpServerPath);
        System.out.println("포트 번호: " + port);
        System.out.println("=".repeat(100));
    }
    
    /**
     * 현재 연결된 사용자 정보를 출력합니다
     */
    public void printConnectedUsers() {
        if (!isRunning() || ftpServer == null) {
            System.out.println("FTP 서버가 실행 중이 아닙니다.");
            return;
        }
        
        try {
            // 리플렉션을 사용하여 API 호환성 확보
            java.lang.reflect.Method getContextMethod = ftpServer.getClass().getMethod("getServerContext");
            Object serverContext = getContextMethod.invoke(ftpServer);
            
            if (serverContext != null) {
                java.lang.reflect.Method getStatsMethod = serverContext.getClass().getMethod("getFtpStatistics");
                FtpStatistics stats = (FtpStatistics) getStatsMethod.invoke(serverContext);
                
                if (stats != null) {
                    System.out.println("\n=== FTP 서버 통계 ===");
                    System.out.println("현재 연결된 사용자 수: " + stats.getCurrentConnectionNumber());
                    System.out.println("총 로그인 수: " + stats.getTotalLoginNumber());
                    System.out.println("총 업로드 파일 수: " + stats.getTotalUploadNumber());
                    System.out.println("총 다운로드 파일 수: " + stats.getTotalDownloadNumber());
                    return;
                }
            }
            System.out.println("\n서버 통계 정보를 사용할 수 없습니다.");
        } catch (NoSuchMethodException e) {
            System.out.println("\n이 버전의 FTP 서버는 통계 기능을 지원하지 않습니다.");
        } catch (Exception e) {
            System.out.println("\n서버 통계 조회 중 오류 발생: " + e.getMessage());
        }
    }
    
    /**
     * FTP 서버를 재시작합니다
     * @throws Exception
     */
    public void restartServer() throws Exception {
        System.out.println("FTP 서버를 재시작합니다...");
        stopServer();
        Thread.sleep(2000); // 2초 대기
        startServer();
    }
    
    /**
     * 실행 중인 FTP 서버의 UserManager를 반환합니다
     * @return UserManager 인스턴스 (서버가 실행 중이 아니면 null)
     */
    public org.apache.ftpserver.ftplet.UserManager getUserManager() {
        return this.userManager;
    }
    
    /**
     * 실행 중인 FTP 서버에 동적으로 사용자를 추가합니다
     * (서버 재시작 불필요)
     * 
     * @param userId 사용자 ID
     * @param password 비밀번호
     * @param homeDirectory 홈 디렉토리 경로
     * @return true: 성공, false: 실패
     * @throws Exception
     */
    public boolean addUserDynamic(String userId, String password, String homeDirectory) throws Exception {
        if (!isRunning || userManager == null) {
            throw new IllegalStateException("FTP 서버가 실행 중이 아닙니다.");
        }
        
        try {
            org.apache.ftpserver.usermanager.impl.BaseUser user;
            boolean isNewUser = false;
            
            // 사용자 계정이 이미 존재하는지 확인
            if (userManager.doesExist(userId)) {
                System.out.println("사용자 '" + userId + "' 계정 업데이트 (비밀번호 변경)");
                
                // 기존 사용자 정보 가져오기
                user = (org.apache.ftpserver.usermanager.impl.BaseUser) userManager.getUserByName(userId);
                
                // 비밀번호 변경
                user.setPassword(password);
            } else {
                System.out.println("새로운 사용자 '" + userId + "' 계정 생성");
                isNewUser = true;
                
                // 새 사용자 생성
                user = new org.apache.ftpserver.usermanager.impl.BaseUser();
                user.setName(userId);
                user.setPassword(password);
                user.setHomeDirectory(homeDirectory);
                user.setEnabled(true);
                
                // 권한 설정
                java.util.List<org.apache.ftpserver.ftplet.Authority> authorities = new java.util.ArrayList<>();
                authorities.add(new org.apache.ftpserver.usermanager.impl.WritePermission());
                user.setAuthorities(authorities);
            }
            
            // 홈 폴더 존재 보장
            File homeDir = new File(user.getHomeDirectory());
            if (!homeDir.exists()) {
                homeDir.mkdirs();
                System.out.println("홈 디렉토리 생성: " + user.getHomeDirectory());
            }
            
            // 실행 중인 서버의 UserManager에 저장 (즉시 반영)
            userManager.save(user);
            
            System.out.println("사용자 '" + userId + "' " + (isNewUser ? "생성" : "업데이트") + " 완료 (즉시 사용 가능)");
            System.out.println("  - 홈 디렉토리: " + user.getHomeDirectory());
            
            return true;
            
        } catch (Exception e) {
            System.err.println("사용자 '" + userId + "' 추가 실패: " + e.getMessage());
            throw e;
        }
    }
    
    /**
     * 테스트용 메인 메서드
     */
    public static void main(String[] args) {
        String ftpServerPath = "D:/DEV/ftp/apache-ftpserver-1.1.4";
        int port = 2121;
        
        FtpServerManager serverManager = new FtpServerManager(ftpServerPath, port);
        
        try {
            // 서버 시작
            serverManager.startServer();
            serverManager.printStatus();
            
            System.out.println("\nFTP 서버가 실행 중입니다.");
            System.out.println("테스트를 위해 30초 동안 대기합니다...");
            System.out.println("FTP 클라이언트로 접속 테스트를 진행하세요.");
            System.out.println("접속 정보 - 호스트: localhost, 포트: " + port);
            
            // 30초 동안 실행
            for (int i = 1; i <= 30; i++) {
                Thread.sleep(1000);
                if (i % 5 == 0) {
                    System.out.println(i + "초 경과...");
                    serverManager.printConnectedUsers();
                }
            }
            
            // 서버 정지
            serverManager.stopServer();
            serverManager.printStatus();
            
        } catch (Exception e) {
            System.err.println("오류 발생: " + e.getMessage());
            e.printStackTrace();
            
            // 오류 발생 시 서버 정지
            if (serverManager.isRunning()) {
                serverManager.stopServer();
            }
        }
    }
}

