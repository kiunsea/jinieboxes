package com.omnibuscode.base;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.omnibuscode.auth.AuthManager;
import com.omnibuscode.auth.VerifyManager;
import com.omnibuscode.ftp.FtpServerManager;
import com.omnibuscode.ftp.FtpUserManager;
import com.omnibuscode.util.KeycodeGenerator;
import com.omnibuscode.utils.ExceptionUtil;
import com.omnibuscode.utils.PropertiesUtil;

import jakarta.servlet.http.HttpServlet;

/**
 * 시스템 환경 변수를 초기화할 서블릿
 * @author KIUNSEA
 *
 */
public class InitializeEnv extends HttpServlet {

    private static final long serialVersionUID = 1L;
    /**
     * 서버 환경 변경시 log4j.properties 설정 정보도 함께 변경 필요
     */
    private Logger log = LogManager.getLogger(InitializeEnv.class);

    public void init() {

        String webAppRoot = this.getServletContext().getRealPath("/");
        resolveEnvPaths(webAppRoot);

        keycodeInitialize();

        startFtpServerIfConfigured(webAppRoot);
    }

    /**
     * standalone 모드(시스템 프로퍼티 {@code jiniebox.standalone=true})와
     * WAR 모드(외부 Tomcat 등 일반 서블릿 컨테이너) 양쪽에서 동작하도록
     * 리소스/설정 파일 경로를 결정한다.
     *
     * <ul>
     *   <li>SYS_RES_PATH (keycode 등 영속 자원): standalone 이면 {@code <dataDir>/res/},
     *       그 외엔 {@code <webAppRoot>/WEB-INF/classes/res/}</li>
     *   <li>JINIEBOX.PROPERTIES: 외부 {@code jiniebox.config.path} 가 지정되어 실제 파일이면
     *       그 경로, 그 외엔 SYS_RES_PATH 아래 파일</li>
     * </ul>
     */
    private void resolveEnvPaths(String webAppRoot) {
        boolean standalone = "true".equalsIgnoreCase(System.getProperty("jiniebox.standalone"));
        String dataDir = System.getProperty("jiniebox.data.dir");

        if (standalone && dataDir != null) {
            File resDir = new File(dataDir, "res");
            if (!resDir.exists()) resDir.mkdirs();
            EnvSYS.SYS_RES_PATH = resDir.getAbsolutePath() + File.separator;
            log.info("[standalone] SYS_RES_PATH = " + EnvSYS.SYS_RES_PATH);
        } else {
            EnvSYS.SYS_RES_PATH = webAppRoot + "WEB-INF/classes/res/";
        }

        String externalConfig = System.getProperty("jiniebox.config.path");
        if (externalConfig != null && new File(externalConfig).isFile()) {
            PropertiesUtil.USER_PROPERTIES_PATH = new File(externalConfig).getAbsolutePath();
            log.info("외부 설정 파일 사용: " + PropertiesUtil.USER_PROPERTIES_PATH);
        } else {
            if (externalConfig != null) {
                log.warn("jiniebox.config.path 가 지정되었으나 파일이 없습니다: " + externalConfig
                        + " — webapp 내장 설정 사용");
            }
            PropertiesUtil.USER_PROPERTIES_PATH = EnvSYS.SYS_RES_PATH + "JINIEBOX.PROPERTIES";
        }

        // PropertiesUtil.get() 은 파일 부재 시 매 호출마다 FileNotFoundException 을 던지므로
        // 백그라운드 스레드 로그가 폭주한다. 빈 파일이라도 만들어 두면 get() 이 null 반환 → SafeProps 가 처리.
        File propsFile = new File(PropertiesUtil.USER_PROPERTIES_PATH);
        if (!propsFile.exists()) {
            try {
                File parent = propsFile.getParentFile();
                if (parent != null && !parent.exists()) parent.mkdirs();
                if (propsFile.createNewFile()) {
                    log.info("빈 JINIEBOX.PROPERTIES 생성: " + propsFile.getAbsolutePath()
                            + " (필요한 키를 직접 채워주세요)");
                }
            } catch (IOException e) {
                log.warn("JINIEBOX.PROPERTIES 자동 생성 실패: " + e.getMessage());
            }
        }
    }

    /**
     * FTP 서버를 설정된 경우에 한해 시작한다.
     * 경로(bundled/PROPERTIES) 또는 포트가 미설정이면 깔끔히 건너뛰며,
     * 서버 자체는 정상 부팅된다.
     */
    private void startFtpServerIfConfigured(String webAppRoot) {
        String ftpServerPath = resolveFtpServerPath(webAppRoot);
        if (ftpServerPath == null) {
            log.info("FTP 서버를 건너뜁니다: bundled 경로도, FTP_SERVER_PATH 설정도 없습니다.");
            return;
        }

        int port = SafeProps.getInt("FTP_SERVER_PORT", -1);
        if (port <= 0) {
            log.info("FTP 서버를 건너뜁니다: FTP_SERVER_PORT가 설정되지 않았습니다.");
            return;
        }

        EnvSYS.FTP_SERVER_PATH = ftpServerPath;
        System.setProperty("ftp.server.path", ftpServerPath);

        FtpServerManager serverManager = new FtpServerManager(ftpServerPath, port);
        try {
            serverManager.startServer();
            EnvSYS.ftpServerManager = serverManager;
            log.info("FTP 서버 시작 완료 - 포트: " + port + ", 경로: " + ftpServerPath);
        } catch (Exception e) {
            log.error("FTP 서버 시작 실패", e);
        }
    }

    /**
     * FTP 서버 실행 경로를 결정한다. 찾지 못하면 {@code null}을 반환한다.
     * 우선순위 1) 웹앱 내 bundled FTP 서버 2) JINIEBOX.PROPERTIES 의 FTP_SERVER_PATH
     */
    private String resolveFtpServerPath(String webAppRoot) {
        File bundledDir = new File(webAppRoot, "ext/ftp_server/apache-ftpserver-1.2.1");
        if (bundledDir.exists() && bundledDir.isDirectory()) {
            String path = bundledDir.getAbsolutePath();
            log.info("Bundled FTP Server 경로 감지: " + path);
            return path;
        }

        String configuredPath = SafeProps.getString("FTP_SERVER_PATH");
        if (configuredPath != null) {
            log.info("FTP Server 경로를 JINIEBOX.PROPERTIES에서 로드: " + configuredPath);
            return configuredPath;
        }

        return null;
    }
    
    /**
     * 인증키코드 초기화
     */
    private void keycodeInitialize() {
        // keycode_*.dat 파일이 없으면 자동 생성
        // (공개 클론/standalone 환경처럼 첫 부팅 시 키 파일이 없는 경우 대응)
        KeycodeGenerator.generateIfMissing(EnvSYS.SYS_RES_PATH);

        Map<String, String> authKeycode = new HashMap<String, String>();
        String kcFilePath = EnvSYS.SYS_RES_PATH + KeycodeGenerator.FILE_KEYCODE_JINIE;
        File fileObj = new File(kcFilePath);
        if (fileObj.exists()) {
            BufferedReader bufferedReader = null;
            try {
                bufferedReader = new BufferedReader(new FileReader(fileObj));   
                String textLine = null;
                String[] keycode = null;
                while ((textLine = bufferedReader.readLine()) != null) {
                    keycode = textLine.split(":");
//                    System.out.println(keycode[0] +"   "+ keycode[1]);
                    authKeycode.put(keycode[0], keycode[1]);
                }
            } catch (FileNotFoundException e) {
                log.error(ExceptionUtil.getExceptionInfo(e));
            } catch (IOException e) {
                log.error(ExceptionUtil.getExceptionInfo(e));
            } finally {
                if (bufferedReader != null) {
                    try {
                        bufferedReader.close();
                    } catch (IOException e) {
                        log.error(ExceptionUtil.getExceptionInfo(e));
                    }
                }
            }
        }
        
        VerifyManager.getInstance(EnvSYS.SYS_RES_PATH, PropertiesUtil.USER_PROPERTIES_PATH);
        if (authKeycode != null) {
            AuthManager.getInstance().setAuthKeycode(authKeycode);
            log.info("[InitializeEnv.java] Keycode Initialized : KEYCODE 초기화를 완료하였습니다");
        } else {
        	log.info("[InitializeEnv.java] System Error : KEYCODE 초기화에 실패하였습니다");
        }
    }
}
