package com.omnibuscode.ftp;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.ftpserver.ftplet.Authority;
import org.apache.ftpserver.ftplet.User;
import org.apache.ftpserver.usermanager.PropertiesUserManagerFactory;
import org.apache.ftpserver.usermanager.impl.BaseUser;
import org.apache.ftpserver.usermanager.impl.WritePermission;

/**
 * Apache FtpServer 에 사용자 계정 생성 (users.properties 에 계정 추가)
 */
public class FtpUserManager {
    
    /**
     * FTP 계정 정보를 담는 클래스
     */
    public static class AccountInfo {
        public String userId;
        public String password;
        public String homeDirectory;
        
        public AccountInfo(String userId, String password, String homeDirectory) {
            this.userId = userId;
            this.password = password;
            this.homeDirectory = homeDirectory;
        }
    }
    
    /**
     * 단일 사용자 계정을 생성/업데이트합니다
     * @param ftpServerPath FTP 서버 설치 경로
     * @param accountInfo 계정 정보
     * @return true: 성공, false: 실패
     * @throws Exception
     */
    public static boolean createUser(String ftpServerPath, AccountInfo accountInfo) throws Exception {
        return createUser(ftpServerPath, accountInfo.userId, accountInfo.password, accountInfo.homeDirectory);
    }
    
    /**
     * 단일 사용자 계정을 생성/업데이트합니다 (기존 사용자는 비밀번호와 홈경로 변경)
     * @param ftpServerPath FTP 서버 설치 경로
     * @param userId 사용자 ID
     * @param password 비밀번호
     * @param homeDirectory 홈 디렉토리 경로
     * @return true: 성공, false: 실패
     * @throws Exception
     */
    public static boolean createUser(String ftpServerPath, String userId, String password, String homeDirectory) throws Exception {
        try {
            // users.properties 경로 지정
            File usersFile = new File(ftpServerPath + "/res/conf/users.properties");
            PropertiesUserManagerFactory umf = new PropertiesUserManagerFactory();
            umf.setFile(usersFile);
            var userManager = umf.createUserManager();
            
            BaseUser user;
            boolean isNewUser = false;
            
            // 사용자 계정이 이미 존재하는지 확인
            if (userManager.doesExist(userId)) {
                System.out.println("사용자 '" + userId + "' 계정이 이미 있습니다, 비밀번호를 수정합니다");
                
                // 기존 사용자 정보 가져오기
                user = (BaseUser) userManager.getUserByName(userId);
                
                // 비밀번호만 변경
                user.setPassword(password);
            } else {
                System.out.println("새로운 사용자 '" + userId + "' 계정을 생성합니다");
                isNewUser = true;
                
                // 새 사용자 생성
                user = new BaseUser();
                user.setName(userId);
                user.setPassword(password);
                user.setHomeDirectory(homeDirectory);
                user.setEnabled(true);
                
                // 권한 설정 - 수정 가능한 리스트를 생성하여 설정
                List<Authority> authorities = new ArrayList<>();
                authorities.add(new WritePermission());
                user.setAuthorities(authorities);
            }
            
            // 홈 폴더 존재 보장
            File homeDir = new File(user.getHomeDirectory());
            if (!homeDir.exists()) {
                homeDir.mkdirs();
                System.out.println("홈 디렉토리 생성: " + user.getHomeDirectory());
            }
            
            // 사용자 저장
            userManager.save(user);
            
            System.out.println("사용자 '" + userId + "' " + (isNewUser ? "생성" : "업데이트") + " 완료");
            System.out.println("  - 홈 디렉토리: " + user.getHomeDirectory());
            System.out.println("  - 활성화: " + (user.getEnabled() ? "예" : "아니오"));
            
            return true;
            
        } catch (Exception e) {
            System.err.println("사용자 '" + userId + "' 생성/업데이트 실패: " + e.getMessage());
            throw e;
        }
    }
    
    /**
     * 여러 계정을 한번에 생성/업데이트합니다
     * @param ftpServerPath FTP 서버 설치 경로
     * @param accounts 계정 정보 맵
     * @throws Exception
     */
    public static void createUsers(String ftpServerPath, Map<String, AccountInfo> accounts) throws Exception {
        // users.properties 경로 지정
        File usersFile = new File(ftpServerPath + "/res/conf/users.properties");
        PropertiesUserManagerFactory umf = new PropertiesUserManagerFactory();
        umf.setFile(usersFile);
        // 평문 비밀번호 사용 시 암호화 미지정(운영은 해시 권장)
        var userManager = umf.createUserManager();

        System.out.println("총 " + accounts.size() + "개의 계정을 처리합니다.\n");
        
        // 각 계정을 순회하며 처리
        int processCount = 0;
        for (Map.Entry<String, AccountInfo> entry : accounts.entrySet()) {
            processCount++;
            AccountInfo accountInfo = entry.getValue();
            
            System.out.println("[" + processCount + "/" + accounts.size() + "] 처리 중: " + accountInfo.userId);
            
            BaseUser user;
            
            // 사용자 계정이 이미 존재하는지 확인
            if (userManager.doesExist(accountInfo.userId)) {
                System.out.println("  → 사용자 계정이 이미 있습니다, 비밀번호를 수정합니다");
                
                // 기존 사용자 정보 가져오기
                user = (BaseUser) userManager.getUserByName(accountInfo.userId);
                
                // 비밀번호만 변경
                user.setPassword(accountInfo.password);
            } else {
                System.out.println("  → 새로운 사용자 계정을 생성합니다");
                
                // 새 사용자 생성
                user = new BaseUser();
                user.setName(accountInfo.userId);
                user.setPassword(accountInfo.password);
                user.setHomeDirectory(accountInfo.homeDirectory);
                user.setEnabled(true);
                
                // 권한 설정 - 수정 가능한 리스트를 생성하여 설정
                List<Authority> authorities = new ArrayList<>();
                authorities.add(new WritePermission());
                user.setAuthorities(authorities);
            }

            // 홈 폴더 존재 보장
            new File(user.getHomeDirectory()).mkdirs();

            userManager.save(user);
            System.out.println("  → 작업 완료: " + accountInfo.userId + "\n");
        }
        
        System.out.println("=".repeat(100));
        System.out.println("모든 계정 처리가 완료되었습니다. (총 " + processCount + "개)");
        System.out.println("=".repeat(100));
        
        // users.properties에 저장된 모든 계정 정보 출력
        printAllUsers(userManager);
    }
    
    /**
     * 단일 사용자 계정을 삭제합니다
     * @param ftpServerPath FTP 서버 설치 경로
     * @param userId 사용자 ID
     * @return true: 성공, false: 실패
     * @throws Exception
     */
    public static boolean deleteUser(String ftpServerPath, String userId) throws Exception {
        try {
            // users.properties 경로 지정
            File usersFile = new File(ftpServerPath + "/res/conf/users.properties");
            PropertiesUserManagerFactory umf = new PropertiesUserManagerFactory();
            umf.setFile(usersFile);
            var userManager = umf.createUserManager();
            
            // 사용자 계정이 존재하는지 확인
            if (!userManager.doesExist(userId)) {
                System.out.println("사용자 '" + userId + "' 계정이 존재하지 않습니다.");
                return false;
            }
            
            // 사용자 삭제
            userManager.delete(userId);
            System.out.println("사용자 '" + userId + "' 계정이 삭제되었습니다.");
            
            return true;
            
        } catch (Exception e) {
            System.err.println("사용자 '" + userId + "' 삭제 실패: " + e.getMessage());
            throw e;
        }
    }
    
    /**
     * 여러 사용자 계정을 일괄 삭제합니다
     * @param ftpServerPath FTP 서버 설치 경로
     * @param userIds 삭제할 사용자 ID 배열
     * @throws Exception
     */
    public static void deleteUsers(String ftpServerPath, String[] userIds) throws Exception {
        // users.properties 경로 지정
        File usersFile = new File(ftpServerPath + "/res/conf/users.properties");
        PropertiesUserManagerFactory umf = new PropertiesUserManagerFactory();
        umf.setFile(usersFile);
        var userManager = umf.createUserManager();

        System.out.println("총 " + userIds.length + "개의 계정을 삭제합니다.\n");
        
        int successCount = 0;
        int failCount = 0;
        
        for (int i = 0; i < userIds.length; i++) {
            String userId = userIds[i];
            System.out.println("[" + (i + 1) + "/" + userIds.length + "] 삭제 중: " + userId);
            
            try {
                // 사용자 계정이 존재하는지 확인
                if (!userManager.doesExist(userId)) {
                    System.out.println("  → 계정이 존재하지 않습니다.");
                    failCount++;
                } else {
                    // 사용자 삭제
                    userManager.delete(userId);
                    System.out.println("  → 삭제 완료");
                    successCount++;
                }
            } catch (Exception e) {
                System.out.println("  → 삭제 실패: " + e.getMessage());
                failCount++;
            }
            System.out.println();
        }
        
        System.out.println("=".repeat(100));
        System.out.println("계정 삭제 완료");
        System.out.println("  - 성공: " + successCount + "개");
        System.out.println("  - 실패: " + failCount + "개");
        System.out.println("=".repeat(100));
        
        // users.properties에 저장된 모든 계정 정보 출력
        printAllUsers(userManager);
    }
    
    /**
     * users.properties에 저장된 모든 계정 정보를 테이블 형태로 출력
     */
    private static void printAllUsers(org.apache.ftpserver.ftplet.UserManager userManager) throws Exception {
        System.out.println("\n" + "=".repeat(100));
        System.out.println("FTP 서버 등록 계정 목록");
        System.out.println("=".repeat(100));
        
        String[] allUsers = userManager.getAllUserNames();
        
        if (allUsers == null || allUsers.length == 0) {
            System.out.println("등록된 계정이 없습니다.");
            System.out.println("=".repeat(100));
            return;
        }
        
        // 테이블 헤더 출력
        System.out.printf("%-20s %-10s %-50s %-30s%n", 
                "사용자명", "활성화", "홈 디렉토리", "권한");
        System.out.println("-".repeat(100));
        
        // 각 사용자 정보 출력
        for (String username : allUsers) {
            User user = userManager.getUserByName(username);
            if (user != null) {
                String enabled = user.getEnabled() ? "활성" : "비활성";
                String homeDir = user.getHomeDirectory();
                
                // 권한 정보 추출
                StringBuilder authStr = new StringBuilder();
                List<? extends Authority> authorities = user.getAuthorities();
                if (authorities != null && !authorities.isEmpty()) {
                    for (Authority auth : authorities) {
                        if (authStr.length() > 0) authStr.append(", ");
                        authStr.append(auth.getClass().getSimpleName());
                    }
                } else {
                    authStr.append("없음");
                }
                
                System.out.printf("%-20s %-10s %-50s %-30s%n", 
                        username, enabled, homeDir, authStr.toString());
            }
        }
        
        System.out.println("=".repeat(100));
        System.out.println("총 " + allUsers.length + "개의 계정이 등록되어 있습니다.");
        System.out.println("=".repeat(100));
    }
    
    /**
     * 테스트용 메인 메서드
     */
    public static void main(String[] args) throws Exception {
        
        String ftpserver_path = "D:/DEV/ftp/apache-ftpserver-1.1.4";
        
        System.out.println("=".repeat(100));
        System.out.println("테스트 1: 단일 사용자 생성");
        System.out.println("=".repeat(100));
        
        // 단일 사용자 생성 테스트 (방법 1: 파라미터로 직접 전달)
        boolean result1 = createUser(
            ftpserver_path,
            "newuser",
            "newuser123",
            ftpserver_path + "/res/home/newuser"
        );
        System.out.println("생성 결과: " + (result1 ? "성공" : "실패") + "\n");
        
        // 단일 사용자 생성 테스트 (방법 2: AccountInfo 객체 사용)
        AccountInfo singleAccount = new AccountInfo(
            "singleuser",
            "single123",
            ftpserver_path + "/res/home/singleuser"
        );
        boolean result2 = createUser(ftpserver_path, singleAccount);
        System.out.println("생성 결과: " + (result2 ? "성공" : "실패") + "\n");
        
        System.out.println("\n" + "=".repeat(100));
        System.out.println("테스트 2: 여러 사용자 일괄 생성");
        System.out.println("=".repeat(100) + "\n");
        
        // 여러 계정 정보를 Map 형태로 정의
        Map<String, AccountInfo> accounts = new LinkedHashMap<>();
        
        // 계정 정보 추가 (아이디, 비밀번호, 홈 경로)
        accounts.put("jinie", new AccountInfo(
            "jinie",
            "bobopa",
            ftpserver_path + "/res/home/jinie"
        ));
        
        accounts.put("admin", new AccountInfo(
            "admin",
            "ftpserver_admin25",
            ftpserver_path + "/res/home/admin"
        ));
        
        accounts.put("testuser", new AccountInfo(
            "testuser",
            "test1234",
            ftpserver_path + "/res/home/testuser"
        ));
        
        accounts.put("anonymous", new AccountInfo(
            "anonymous",
            "anonymous_heyhey",
            ftpserver_path + "/res/home/anonymous"
        ));

        // 여러 계정 일괄 생성 실행
        createUsers(ftpserver_path, accounts);
        
        System.out.println("\n" + "=".repeat(100));
        System.out.println("테스트 3: 단일 사용자 삭제");
        System.out.println("=".repeat(100));
        
        // 단일 사용자 삭제 테스트
        boolean deleteResult = deleteUser(ftpserver_path, "testuser");
        System.out.println("삭제 결과: " + (deleteResult ? "성공" : "실패") + "\n");
        
        System.out.println("\n" + "=".repeat(100));
        System.out.println("테스트 4: 여러 사용자 일괄 삭제");
        System.out.println("=".repeat(100) + "\n");
        
        // 여러 사용자 일괄 삭제 테스트
        String[] usersToDelete = {"newuser", "singleuser", "anonymous"};
        deleteUsers(ftpserver_path, usersToDelete);
    }
}

