package com.omnibuscode.ctrl;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Enumeration;
import java.util.List;

import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.json.simple.JSONObject;

import com.omnibuscode.auth.AuthInfo;
import com.omnibuscode.auth.AuthManager;
import com.omnibuscode.base.EnvSYS;
import com.omnibuscode.base.IntegrationGate;
import com.omnibuscode.base.UserSession;
import com.omnibuscode.dao.AutoKeywordDataAccessObject;
import com.omnibuscode.dao.BoxDataAccessObject;
import com.omnibuscode.dao.JbgAccessDataAccessObject;
import com.omnibuscode.dao.JbgInfoDataAccessObject;
import com.omnibuscode.logic.jbg.JangBoGoManager;
import com.omnibuscode.logic.jbg.ftp.FtpFileProcessorScheduler;
import com.omnibuscode.util.JinieboxUtil;
import com.omnibuscode.util.StringEncrypter;
import com.omnibuscode.utils.ExceptionUtil;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * @author KIUNSEA
 *
 */
@WebServlet("/jbg")
public class JbgServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    /**
     * 서버 환경 변경시 log4j.properties 설정 정보도 함께 변경 필요
     */
    private Logger log = LogManager.getLogger(JbgServlet.class);
    
    // 주의: FTP 파일 처리 스케줄러는 origin/main(8387755) 변경으로 StartMonitors 가 초기화한다.
    // 이 서블릿에서는 Jangbogo 비활성 시 doGet 진입에서 503 으로 막는 것만 책임진다.

    public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        // request parameter 로깅
        String pName = null;
        Enumeration<String> enums = req.getParameterNames();
        while (enums.hasMoreElements()) {
            pName = enums.nextElement().toString();
            log.debug("(param) " + pName + " - " + req.getParameter(pName));
        }

        doGet(req, res);
    }

    public void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {

        if (!IntegrationGate.isJangbogoEnabled()) {
            IntegrationGate.writeDisabled(res, "Jangbogo");
            return;
        }

        JSONObject resJson = null;

        String cmdHttp = req.getParameter("cmd");

        try {
            if (AuthManager.getInstance().hasUserAuthed(req)) {
                
                if (cmdHttp != null) {
                    if ("getJangbogoPublicKey".equals(cmdHttp)) { // 장보고 RSA 키 조회 (자동 생성)
                        resJson = this.getJangbogoPublicKey(req);
                    } else if ("regenerateJangbogoKeys".equals(cmdHttp)) { // 장보고 RSA 키 재생성
                        resJson = this.regenerateJangbogoKeys(req);
                    } else if ("generatePublicKeyOnly".equals(cmdHttp)) { // Public Key만 생성 (저장하지 않음)
                        resJson = this.generatePublicKeyOnly(req);
                    } else if ("checkFtpAccountExists".equals(cmdHttp)) { // FTP 계정 존재 여부 확인
                        resJson = this.checkFtpAccountExists(req);
                    } else if ("createFtpAccount".equals(cmdHttp)) { // FTP 계정 생성/갱신
                        resJson = this.createFtpAccount(req);
                    } else if ("toggleAutoCollect".equals(cmdHttp)) { // 자동 수집 활성화/비활성화
                        resJson = this.toggleAutoCollect(req);
                    } else if ("changeCollectInterval".equals(cmdHttp)) { // 수집 주기 변경
                        resJson = this.changeCollectInterval(req);
                    } else if ("executeProcessorNow".equals(cmdHttp)) { // FTP 파일 처리 즉시 실행
                        resJson = this.executeProcessorNow(req);
                    } else if ("getInfo".equals(cmdHttp)) { // 장보고 정보 (모든 쇼핑몰 목록, 인증 실패한 쇼핑몰 목록, 자동화 목록)
                        resJson = this.getInfo(req);
                    } else if ("connectToMall".equals(cmdHttp)) { // 쇼핑몰 구매내역 조회를 위해 등록
                        resJson = this.connectToMall(req);
                    } else if ("fetchOrders".equals(cmdHttp)) { // 쇼핑몰 구매내역 조회 요청
                        this.fetchOrders(req);
                    }
                } else {
                    resJson = new JSONObject();
                    resJson.put("code", EnvSYS.RESCODE_FAIL);
                    resJson.put("msg", EnvSYS.RESMSG_INVREQ);
                }
            } else {
                resJson = new JSONObject();
                AuthInfo uai = AuthManager.getInstance().getUserAuthInfo(req);
                if (uai != null) {
                    resJson.put("code", uai.getValidcode());
                    resJson.put("msg", uai.getValidmsg());
                } else {
                    resJson.put("code", EnvSYS.RESCODE_FAIL);
                    resJson.put("msg", EnvSYS.RESMSG_USERSESSION_FAIL);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (resJson == null)
            resJson = new JSONObject();
        /**
         * (Bixby)html 페이지간의 redirect 이동을 위한 parameter 전달용
         */
        resJson.put("buid", req.getParameter("buid"));
        resJson.put("authkey", AuthManager.getInstance().getRandomAuthKey(req.getParameter("buid")));

//        log.debug("res - " + resJson);

        res.setCharacterEncoding("UTF-8");
        res.setContentType("application/json;charset=UTF-8");
        res.setHeader("Cache-Control", "no-cache");
        PrintWriter out = res.getWriter();
        out.println(resJson);
        out.flush();
        out.close();
    }
    
    /**
     * 장보고 보안 Public Key 및 FTP ID 조회 (없으면 자동 생성)
     * 
     * @param req HttpServletRequest
     * @return JSONObject { code: "000", publicKey: "공개키", ftpId: "FTP계정ID" }
     */
    private JSONObject getJangbogoPublicKey(HttpServletRequest req) {
        JSONObject resObj = new JSONObject();
        
        try {
            // 사용자 인증 확인
            UserSession us = AuthManager.getInstance().getUserSession(req);
            if (us == null) {
                resObj.put("code", "401");
                resObj.put("msg", "로그인이 필요합니다.");
                return resObj;
            }
            
            String seqUser = us.getSeq();
            
            // jbg_info 테이블에서 보안 키 쌍 조회
            com.omnibuscode.dao.JbgInfoDataAccessObject jbgInfoDao = new com.omnibuscode.dao.JbgInfoDataAccessObject();
            JSONObject secKeys = jbgInfoDao.getSecurityKeys(seqUser);
            
            String secPrivKey = secKeys.get("secPrivKey") != null ? secKeys.get("secPrivKey").toString() : "";
            String secPubKey = secKeys.get("secPubKey") != null ? secKeys.get("secPubKey").toString() : "";
            
            // FTP ID 조회
            String ftpId = jbgInfoDao.getFtpId(seqUser);
            
            // 자동 수집 설정 조회
            JSONObject autoCollectConfig = jbgInfoDao.getAutoCollectConfig(seqUser);
            
            // org.json.simple.JSONObject는 getInt()가 없으므로 get()으로 조회 후 형변환
            Object enabledObj = autoCollectConfig.get("enabled");
            Object intervalObj = autoCollectConfig.get("interval");
            int autoCollectEnabled = (enabledObj instanceof Number) ? ((Number) enabledObj).intValue() : 0;
            int autoCollectInterval = (intervalObj instanceof Number) ? ((Number) intervalObj).intValue() : 5;
            
            resObj.put("code", "000");
            resObj.put("publicKey", secPubKey); // Public Key만 반환
            resObj.put("ftpId", ftpId); // FTP ID 반환
            resObj.put("autoCollectEnabled", autoCollectEnabled); // 자동 수집 활성화 여부
            resObj.put("autoCollectInterval", autoCollectInterval); // 자동 수집 주기 (분)
            resObj.put("publicKeyMissing", secPubKey == null || secPubKey.isEmpty());
            resObj.put("privateKeyMissing", secPrivKey == null || secPrivKey.isEmpty());
            resObj.put("success", true);
            
            log.debug("장보고 보안 Public Key 조회 완료 - seq_user: " + seqUser + ", ftpId: " + ftpId + 
                     ", autoCollectEnabled: " + autoCollectEnabled + ", autoCollectInterval: " + autoCollectInterval +
                     ", publicKeyMissing: " + (secPubKey == null || secPubKey.isEmpty()));
            
        } catch (Exception e) {
            log.error("장보고 보안 키 조회 중 오류 발생", e);
            resObj.put("code", "999");
            resObj.put("msg", "조회 중 오류가 발생했습니다: " + e.getMessage());
            resObj.put("success", false);
        }
        
        return resObj;
    }
    
    /**
     * 장보고 보안 키 쌍 재생성
     * @param req
     * @return JSONObject { code: "000", publicKey: "공개키" }
     */
    private JSONObject regenerateJangbogoKeys(HttpServletRequest req) {
        JSONObject resObj = new JSONObject();
        
        try {
            UserSession us = AuthManager.getInstance().getUserSession(req);
            if (us == null) {
                resObj.put("code", "401");
                resObj.put("msg", "로그인이 필요합니다.");
                return resObj;
            }
            
            String seqUser = us.getSeq();
            
            // 새로운 보안 키 쌍 생성
            log.info("새로운 보안 키 쌍 생성 시작 - seq_user: " + seqUser);
            java.security.KeyPair keyPair = com.omnibuscode.util.security.RsaKeyGenerator.generateRsaKeyPair(2048);
            String secPrivKey = com.omnibuscode.util.security.RsaKeyGenerator.privateKeyToBase64(keyPair.getPrivate());
            String secPubKey = com.omnibuscode.util.security.RsaKeyGenerator.publicKeyToBase64(keyPair.getPublic());
            
            // jbg_info 테이블에 저장
            com.omnibuscode.dao.JbgInfoDataAccessObject jbgInfoDao = new com.omnibuscode.dao.JbgInfoDataAccessObject();
            jbgInfoDao.saveSecurityKeys(seqUser, secPrivKey, secPubKey);
            
            log.info("새로운 보안 키 쌍 생성 및 저장 완료 - seq_user: " + seqUser);
            
            resObj.put("code", "000");
            resObj.put("publicKey", secPubKey); // Public Key만 반환
            resObj.put("success", true);
            resObj.put("msg", "새로운 키가 생성되었습니다.");
            
        } catch (Exception e) {
            log.error("장보고 보안 키 재생성 중 오류 발생", e);
            resObj.put("code", "999");
            resObj.put("msg", "키 생성 중 오류가 발생했습니다: " + e.getMessage());
            resObj.put("success", false);
        }
        
        return resObj;
    }
    
    /**
     * Public Key 생성 (세션에 임시 저장)
     * Private Key와 Public Key를 생성하여 세션에 임시 저장
     * 실제 DB 저장은 createFtpAccount에서 수행
     * 
     * @param req
     * @return JSONObject { code: "000", publicKey: "공개키" }
     */
    private JSONObject generatePublicKeyOnly(HttpServletRequest req) {
        JSONObject resObj = new JSONObject();
        
        try {
            UserSession us = AuthManager.getInstance().getUserSession(req);
            if (us == null) {
                resObj.put("code", "401");
                resObj.put("msg", "로그인이 필요합니다.");
                return resObj;
            }
            
            String seqUser = us.getSeq();
            
            // 새로운 보안 키 쌍 생성
            log.info("Public Key 생성 (세션에 임시 저장) - seq_user: " + seqUser);
            java.security.KeyPair keyPair = com.omnibuscode.util.security.RsaKeyGenerator.generateRsaKeyPair(2048);
            String secPrivKey = com.omnibuscode.util.security.RsaKeyGenerator.privateKeyToBase64(keyPair.getPrivate());
            String secPubKey = com.omnibuscode.util.security.RsaKeyGenerator.publicKeyToBase64(keyPair.getPublic());
            
            // 세션에 임시 저장 (createFtpAccount에서 사용)
            req.getSession().setAttribute("temp_private_key_" + seqUser, secPrivKey);
            req.getSession().setAttribute("temp_public_key_" + seqUser, secPubKey);
            
            // DB에 즉시 저장
            com.omnibuscode.dao.JbgInfoDataAccessObject jbgInfoDao = new com.omnibuscode.dao.JbgInfoDataAccessObject();
            jbgInfoDao.saveSecurityKeys(seqUser, secPrivKey, secPubKey);
            log.info("새로 생성된 키 쌍을 DB에 저장했습니다 - seq_user: " + seqUser);
            
            resObj.put("code", "000");
            resObj.put("publicKey", secPubKey);
            resObj.put("success", true);
            resObj.put("msg", "Public Key가 생성되어 저장되었습니다.");
            
            log.info("Public Key 생성 완료 (세션에 임시 저장) - 길이: " + secPubKey.length());
            
        } catch (Exception e) {
            log.error("Public Key 생성 중 오류 발생", e);
            resObj.put("code", "999");
            resObj.put("msg", "Public Key 생성 중 오류가 발생했습니다: " + e.getMessage());
            resObj.put("success", false);
        }
        
        return resObj;
    }
    
    /**
     * FTP 계정 존재 여부 확인
     * @param req
     * @return JSONObject { code: "000", exists: true/false }
     */
    private JSONObject checkFtpAccountExists(HttpServletRequest req) {
        JSONObject resObj = new JSONObject();
        
        try {
            UserSession us = AuthManager.getInstance().getUserSession(req);
            if (us == null) {
                resObj.put("code", "401");
                resObj.put("msg", "로그인이 필요합니다.");
                return resObj;
            }
            
            String accountId = req.getParameter("accountId");
            
            if (accountId == null || accountId.trim().isEmpty()) {
                resObj.put("code", "400");
                resObj.put("msg", "계정 ID가 필요합니다.");
                return resObj;
            }
            
            // FTP 서버 경로 가져오기
            String ftpServerPath = getFtpServerPath();
            
            // FTP 계정 존재 여부 확인
            java.io.File usersFile = new java.io.File(ftpServerPath + "/res/conf/users.properties");
            org.apache.ftpserver.usermanager.PropertiesUserManagerFactory umf = 
                new org.apache.ftpserver.usermanager.PropertiesUserManagerFactory();
            umf.setFile(usersFile);
            var userManager = umf.createUserManager();
            
            boolean exists = userManager.doesExist(accountId);
            
            resObj.put("code", "000");
            resObj.put("exists", exists);
            resObj.put("accountId", accountId);
            resObj.put("success", true);
            
            log.debug("FTP 계정 존재 확인 - accountId: " + accountId + ", exists: " + exists);
            
        } catch (Exception e) {
            log.error("FTP 계정 확인 중 오류 발생", e);
            resObj.put("code", "999");
            resObj.put("msg", "계정 확인 중 오류가 발생했습니다: " + e.getMessage());
            resObj.put("success", false);
        }
        
        return resObj;
    }
    
    /**
     * FTP 계정 생성/갱신
     * jbg_info.ftp_id를 기준으로 계정 생성 또는 비밀번호 갱신
     * 
     * @param req
     * @return JSONObject { code: "000", isNew: true/false, accountId: "계정ID" }
     */
    private JSONObject createFtpAccount(HttpServletRequest req) {
        JSONObject resObj = new JSONObject();
        
        try {
            UserSession us = AuthManager.getInstance().getUserSession(req);
            if (us == null) {
                resObj.put("code", "401");
                resObj.put("msg", "로그인이 필요합니다.");
                return resObj;
            }
            
            String seqUser = us.getSeq();
            String accountId = req.getParameter("accountId");
            String accountPassword = req.getParameter("accountPassword");
            String publicKey = req.getParameter("publicKey");
            
            // JbgInfoDataAccessObject 생성
            com.omnibuscode.dao.JbgInfoDataAccessObject jbgInfoDao = new com.omnibuscode.dao.JbgInfoDataAccessObject();
            
            // DB에서 기존 FTP ID 및 Public Key 조회
            String existingFtpId = jbgInfoDao.getFtpId(seqUser);
            org.json.simple.JSONObject secKeys = jbgInfoDao.getSecurityKeys(seqUser);
            String existingPublicKey = secKeys != null && secKeys.get("secPubKey") != null ? 
                                       secKeys.get("secPubKey").toString() : "";
            
            // 파라미터 검증
            if (accountId == null || accountId.trim().isEmpty()) {
                resObj.put("code", "400");
                resObj.put("msg", "계정 ID가 필요합니다.");
                return resObj;
            }
            
            accountId = accountId.trim();
            accountPassword = accountPassword != null ? accountPassword.trim() : "";
            publicKey = publicKey != null ? publicKey.trim() : "";
            
            // ID 유효성 검사 (영문, 숫자, 언더스코어만 허용)
            if (!accountId.matches("^[a-zA-Z0-9_]+$")) {
                resObj.put("code", "400");
                resObj.put("msg", "계정 ID는 영문, 숫자, 언더스코어(_)만 사용할 수 있습니다.");
                return resObj;
            }
            
            // 요청한 accountId와 기존 FTP ID 비교
            boolean isNewAccount = (existingFtpId == null || existingFtpId.isEmpty() || 
                                   !existingFtpId.equals(accountId));

            // 세션에 임시 저장된 키 (키 생성 버튼으로 생성한 경우)
            String tempPrivateKeySession = (String) req.getSession().getAttribute("temp_private_key_" + seqUser);
            String tempPublicKeySession = (String) req.getSession().getAttribute("temp_public_key_" + seqUser);
            boolean isAutoGeneratedKey = false;
            
            // 케이스별 필수값 검증
            if (isNewAccount) {
                // 케이스 1: 새로운 FTP ID → ID, Pass 필수
                if (accountPassword.isEmpty()) {
                    resObj.put("code", "400");
                    resObj.put("msg", "새 계정 생성 시 비밀번호가 필요합니다.");
                    return resObj;
                }
                
                if (accountPassword.length() < 4) {
                    resObj.put("code", "400");
                    resObj.put("msg", "비밀번호는 최소 4자 이상이어야 합니다.");
                    return resObj;
                }
            } else {
                // 케이스 2 & 3: 기존 계정 갱신
                boolean hasPassword = !accountPassword.isEmpty();
                boolean hasPublicKey = !publicKey.isEmpty();
                
                // 하나 이상은 입력되어야 함
                if (!hasPassword && !hasPublicKey) {
                    resObj.put("code", "400");
                    resObj.put("msg", "비밀번호 또는 Public Key 중 하나 이상을 입력해주세요.");
                    return resObj;
                }
                
                // 비밀번호가 입력된 경우 길이 검증
                if (hasPassword && accountPassword.length() < 4) {
                    resObj.put("code", "400");
                    resObj.put("msg", "비밀번호는 최소 4자 이상이어야 합니다.");
                    return resObj;
                }
            }
            
            // FTP 서버 경로 가져오기
            String ftpServerPath = getFtpServerPath();
            
            boolean isNew = isNewAccount;
            boolean updatePassword = !accountPassword.isEmpty();
            boolean updatePublicKey = !publicKey.isEmpty();
            
            // FTP 계정 처리
            if (!isNewAccount) {
                // 기존 계정 갱신
                log.info("기존 FTP 계정 갱신 - accountId: " + accountId
                        + ", 비밀번호: " + (updatePassword ? "변경" : "유지")
                        + ", Public Key: " + (updatePublicKey ? "변경" : "유지"));
                
                // 홈 디렉토리 경로 설정
                String homeDirectory = ftpServerPath + "/res/home/" + existingFtpId;
                
                // 비밀번호 갱신 처리
                if (updatePassword) {
                    boolean success;
                    
                    // 실행 중인 FTP 서버가 있으면 동적으로 추가 (재시작 불필요)
                    if (EnvSYS.ftpServerManager != null && EnvSYS.ftpServerManager.isRunning()) {
                        log.info("실행 중인 FTP 서버에 동적으로 사용자 업데이트 (재시작 불필요)");
                        success = EnvSYS.ftpServerManager.addUserDynamic(
                            existingFtpId,
                            accountPassword,
                            homeDirectory
                        );
                    } else {
                        // FTP 서버가 실행 중이 아니면 파일에만 저장
                        log.warn("FTP 서버가 실행 중이 아님 - users.properties 파일에만 저장 (서버 재시작 필요)");
                        success = com.omnibuscode.ftp.FtpUserManager.createUser(
                            ftpServerPath, 
                            existingFtpId, 
                            accountPassword, 
                            homeDirectory
                        );
                    }
                    
                    if (!success) {
                        resObj.put("code", "500");
                        resObj.put("msg", "FTP 계정 비밀번호 갱신에 실패했습니다.");
                        resObj.put("success", false);
                        return resObj;
                    }
                    
                    log.info("FTP 비밀번호 갱신 완료 - accountId: " + existingFtpId);
                }
                
                // Public Key 갱신 처리
                boolean hasPrivateKey = false;
                if (updatePublicKey) {
                    // Public Key 유효성 검사
                    try {
                        java.util.Base64.getDecoder().decode(publicKey);
                        log.info("Public Key 포맷 검증 완료 (길이: " + publicKey.length() + ")");
                    } catch (IllegalArgumentException ex) {
                        resObj.put("code", "400");
                        resObj.put("msg", "Public Key 포맷이 올바르지 않습니다. Base64 형식이어야 합니다.");
                        resObj.put("success", false);
                        return resObj;
                    }
                    
                    String privateKeyToSave;
                    
                    if (tempPublicKeySession != null && tempPublicKeySession.equals(publicKey)
                            && tempPrivateKeySession != null) {
                        // 세션의 Public Key와 입력된 Public Key가 같으면 Private Key도 함께 저장
                        privateKeyToSave = tempPrivateKeySession;
                        hasPrivateKey = true;
                        isAutoGeneratedKey = true;
                        log.info("세션의 Private Key를 함께 저장 (키 생성 버튼으로 생성됨)");
                        
                        // 세션에서 제거
                        req.getSession().removeAttribute("temp_private_key_" + seqUser);
                        req.getSession().removeAttribute("temp_public_key_" + seqUser);
                        tempPrivateKeySession = null;
                        tempPublicKeySession = null;
                    } else {
                        // 사용자가 직접 입력한 Public Key면 기존 Private Key 유지
                        String existingPrivKey = secKeys != null && secKeys.get("secPrivKey") != null ? 
                                                secKeys.get("secPrivKey").toString() : "";
                        privateKeyToSave = existingPrivKey;
                        hasPrivateKey = !existingPrivKey.isEmpty();
                        
                        if (!hasPrivateKey) {
                            log.warn("Private Key 없이 Public Key만 저장 - 복호화 불가능!");
                        } else {
                            log.info("기존 Private Key 유지");
                        }
                    }
                    
                    // jbg_info 테이블에 저장
                    jbgInfoDao.saveSecurityKeys(seqUser, privateKeyToSave, publicKey);
                    log.info("Public Key 갱신 완료 - seq_user: " + seqUser
                            + ", Private Key: " + (hasPrivateKey ? "저장됨" : "없음 (복호화 불가)"));
                }
                
                resObj.put("accountId", existingFtpId);
                resObj.put("homeDirectory", homeDirectory);
                
                // 갱신 내용에 따른 메시지
                String updateMsg = "";
                if (updatePassword && updatePublicKey) {
                    if (hasPrivateKey) {
                        updateMsg = "FTP 계정 비밀번호와 Public Key(Private Key 포함)가 갱신되었습니다.";
                    } else {
                        updateMsg = "FTP 계정 비밀번호와 Public Key가 갱신되었습니다.\n\n⚠️ 경고: Private Key가 없어 파일 복호화가 불가능합니다.\n'키 생성' 버튼으로 키 쌍을 생성하는 것을 권장합니다.";
                    }
                } else if (updatePassword) {
                    updateMsg = "FTP 계정 비밀번호가 갱신되었습니다.";
                } else if (updatePublicKey) {
                    if (hasPrivateKey) {
                        updateMsg = "Public Key와 Private Key가 갱신되었습니다.";
                    } else {
                        updateMsg = "Public Key가 갱신되었습니다.\n\n⚠️ 경고: Private Key가 없어 파일 복호화가 불가능합니다.\n'키 생성' 버튼으로 키 쌍을 생성하는 것을 권장합니다.";
                    }
                }
                resObj.put("msg", updateMsg);
                
            } else {
                // 새로운 FTP ID로 계정 생성 (ID, Pass, Public Key 모두 필수)
                if (existingFtpId != null && !existingFtpId.isEmpty()) {
                    log.info("FTP 계정 ID 변경 - 기존: " + existingFtpId + " → 새: " + accountId);
                } else {
                    log.info("새로운 FTP 계정 생성 - accountId: " + accountId);
                }
                
                if (!publicKey.isEmpty()) {
                    try {
                        java.util.Base64.getDecoder().decode(publicKey);
                        log.info("Public Key 포맷 검증 완료 (길이: " + publicKey.length() + ")");
                    } catch (IllegalArgumentException ex) {
                        resObj.put("code", "400");
                        resObj.put("msg", "Public Key 포맷이 올바르지 않습니다. Base64 형식이어야 합니다.");
                        resObj.put("success", false);
                        return resObj;
                    }
                }
                
                // 홈 디렉토리 경로 설정
                String homeDirectory = ftpServerPath + "/res/home/" + accountId;
                
                // FTP 계정 생성
                boolean success;
                
                // 실행 중인 FTP 서버가 있으면 동적으로 추가 (재시작 불필요)
                if (EnvSYS.ftpServerManager != null && EnvSYS.ftpServerManager.isRunning()) {
                    log.info("실행 중인 FTP 서버에 동적으로 사용자 추가 (즉시 사용 가능)");
                    success = EnvSYS.ftpServerManager.addUserDynamic(
                        accountId,
                        accountPassword,
                        homeDirectory
                    );
                } else {
                    // FTP 서버가 실행 중이 아니면 파일에만 저장
                    log.warn("FTP 서버가 실행 중이 아님 - users.properties 파일에만 저장 (서버 재시작 필요)");
                    success = com.omnibuscode.ftp.FtpUserManager.createUser(
                        ftpServerPath, 
                        accountId, 
                        accountPassword, 
                        homeDirectory
                    );
                }
                
                if (!success) {
                    resObj.put("code", "500");
                    resObj.put("msg", "FTP 계정 생성에 실패했습니다.");
                    resObj.put("success", false);
                    return resObj;
                }
                
                // DB에 새 FTP ID 저장 (기존 ID를 덮어씀)
                jbgInfoDao.saveFtpId(seqUser, accountId);
                log.info("jbg_info에 FTP ID 저장 완료 - seq_user: " + seqUser + ", ftp_id: " + accountId);
                
                // Public Key와 Private Key 저장
                String privateKeyToSave;
                boolean hasPrivateKey = false;
                
                if (tempPublicKeySession != null && tempPublicKeySession.equals(publicKey)
                        && tempPrivateKeySession != null) {
                    // 세션의 Public Key와 입력된 Public Key가 같으면 Private Key도 함께 저장
                    privateKeyToSave = tempPrivateKeySession;
                    hasPrivateKey = true;
                    isAutoGeneratedKey = true;
                    log.info("세션의 Private Key를 함께 저장 (키 생성 버튼으로 생성됨)");
                    
                    // 세션에서 제거
                    req.getSession().removeAttribute("temp_private_key_" + seqUser);
                    req.getSession().removeAttribute("temp_public_key_" + seqUser);
                    tempPrivateKeySession = null;
                    tempPublicKeySession = null;
                } else {
                    // 사용자가 직접 입력한 Public Key면 Private Key는 비움
                    privateKeyToSave = "";
                    hasPrivateKey = false;
                    if (!publicKey.isEmpty()) {
                        log.warn("Private Key 없이 Public Key만 저장 - 복호화 불가능!");
                    } else {
                        log.info("Public Key 미설정 상태로 계정 정보를 저장합니다.");
                    }
                }
                
                jbgInfoDao.saveSecurityKeys(seqUser, privateKeyToSave, publicKey);
                log.info("Public Key 저장 완료 - seq_user: " + seqUser
                        + ", Private Key: " + (hasPrivateKey ? "저장됨" : "없음 (복호화 불가)"));
                
                isNew = true;
                resObj.put("accountId", accountId);
                resObj.put("homeDirectory", homeDirectory);
                
                // 기존 계정이 있었으면 안내 추가
                String newAccountMsg;
                
                if (publicKey.isEmpty()) {
                    newAccountMsg = "새 FTP 계정이 생성되었습니다.\n\n현재 Public Key가 설정되지 않았습니다.\n'키 생성' 버튼을 눌러 키를 생성한 뒤 \"계정 생성/갱신\"으로 저장해주세요.";
                } else {
                    if (hasPrivateKey) {
                        if (isAutoGeneratedKey) {
                            newAccountMsg = "새 FTP 계정과 Public Key(Private Key 포함)가 자동 생성되었습니다. (즉시 사용 가능)";
                        } else {
                            newAccountMsg = "새 FTP 계정과 Public Key(Private Key 포함)가 생성되었습니다. (즉시 사용 가능)";
                        }
                    } else {
                        newAccountMsg = "새 FTP 계정과 Public Key가 생성되었습니다. (즉시 사용 가능)\n\n⚠️ 경고: Private Key가 없어 파일 복호화가 불가능합니다.\n'키 생성' 버튼으로 키 쌍을 생성하는 것을 권장합니다.";
                    }
                }
                
                if (existingFtpId != null && !existingFtpId.isEmpty()) {
                    resObj.put("previousAccountId", existingFtpId);
                    resObj.put("msg", newAccountMsg + "\n\n이전 계정 '" + existingFtpId + "'은(는) 더 이상 사용되지 않습니다.");
                } else {
                    resObj.put("msg", newAccountMsg);
                }
            }
            
            // 공통 응답 설정
            resObj.put("code", "000");
            resObj.put("success", true);
            resObj.put("isNew", isNew);
            
            // 최종 저장된 Public Key 반환 (자동 생성되었거나 갱신된 경우)
            if (!publicKey.isEmpty()) {
                resObj.put("publicKey", publicKey);
            }
            
            log.info("FTP 계정 작업 완료 - accountId: " + accountId
                    + ", isNew: " + isNew
                    + ", Public Key: " + (publicKey.isEmpty() ? "없음" : "저장됨"));
            
        } catch (Exception e) {
            log.error("FTP 계정 생성 중 오류 발생", e);
            resObj.put("code", "999");
            resObj.put("msg", "계정 생성 중 오류가 발생했습니다: " + e.getMessage());
            resObj.put("success", false);
        }
        
        return resObj;
    }
    
    /**
     * FTP 서버 경로 가져오기
     * @return FTP 서버 설치 경로
     */
    private String getFtpServerPath() {
        // TODO: PropertiesUtil이나 환경변수에서 가져오도록 수정 필요
        // 임시로 기본 경로 사용
        String ftpPath = System.getProperty("ftp.server.path");
        if (ftpPath == null || ftpPath.isEmpty()) {
            ftpPath = "D:/DEV/ftp/apache-ftpserver-1.1.4"; // 기본 경로
        }
        return ftpPath;
    }

    private JSONObject getInfo(HttpServletRequest req) throws Exception {
        JSONObject resObj = new JSONObject();

        UserSession us = AuthManager.getInstance().getUserSession(req);
        JbgAccessDataAccessObject jaDao = new JbgAccessDataAccessObject();
        List<JSONObject> malls = jaDao.getAccessInfos(us.getSeq());

        JangBoGoManager.addCippKeys(malls, us.getSeq()); // browser local storage 에서 조회하기 위한 id key 와 pw key 를 설정
        JangBoGoManager.addMallUsrid(malls, us); // 장보고 페이지에서 쇼핑몰의 사용자 계정 아이디를 출력하기 위해 설정

        resObj.put("malls", JinieboxUtil.listToMap(malls));
        resObj.put("failedAuthMalls", us.getFailedAuthMalls());
        
        BoxDataAccessObject boxDao = new BoxDataAccessObject();
        List<JSONObject> boxes = boxDao.getStoreBoxes(us.getOwnStoreInfo().get("seq").toString());
        resObj.put("boxes", JinieboxUtil.listToMap(boxes));
        
        AutoKeywordDataAccessObject autokeyDao = new AutoKeywordDataAccessObject();
        List<JSONObject> rules = autokeyDao.getRules(us.getSeqDefstore());
        resObj.put("rules", JinieboxUtil.listToMap(rules));
        resObj.put("code", EnvSYS.RESCODE_SUCC);

        return resObj;
    }

    private JSONObject connectToMall(HttpServletRequest req) throws Exception {

        JSONObject resObj = new JSONObject();
        String resCode = EnvSYS.RESCODE_FAIL;
        String resMsg = EnvSYS.RESMSG_FAIL;

        UserSession us = AuthManager.getInstance().getUserSession(req);
        String seqUser = us.getSeq();
        String seqMall = req.getParameter("seq");
        String usrid = req.getParameter("usrid").trim();
        String usrpw = req.getParameter("usrpass").trim();
        String statusTo = req.getParameter("statusTo");

        if (statusTo != null && seqMall != null) {

            JbgAccessDataAccessObject jaDao = new JbgAccessDataAccessObject();
            
            if ("1".equals(statusTo)) {
                JangBoGoManager jbgMan = new JangBoGoManager();

                int resultConn = jbgMan.connectToMall(seqMall, seqUser, usrid, usrpw);
                if (resultConn == 1) {
                    /**
                     * 연결테스트에 성공한 경우
                     */
                    SecretKey key = null;
                    IvParameterSpec iv = null;
                    
                    JSONObject accInfoJson = jaDao.getAccessInfo(seqMall, seqUser);
                    if (accInfoJson != null && Integer.parseInt(accInfoJson.get("status").toString()) < 2) { //사용 가능한 키가 존재
                        if (accInfoJson.containsKey("enckey") && accInfoJson.get("enckey") != null) { //방어 코드
                            try {
                                String keyBase64 = accInfoJson.get("enckey").toString().trim();
                                String ivBase64 = accInfoJson.get("enciv").toString().trim();
                                if (keyBase64.length() > 0 && ivBase64.length() > 0) {
                                    // 저장된 encrypt 키를 사용
                                    key = StringEncrypter.decodeBase64ToSecretKey(keyBase64);
                                    iv = StringEncrypter.decodeBase64ToIv(ivBase64);
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                                log.error(ExceptionUtil.getExceptionInfo(e));
                            }
                        }
                    }
                    
                    /**
                     * 키 집합에 오류가 발생한 경우 새로 생성
                     */
                    if (key == null || iv == null) {
                        log.error("암호화 키 정보에 오류가 있어 새로 생성하였습니다.");
                        
                        // encrypt 키를 새로 생성
                        key = StringEncrypter.generateKey(256);
                        iv = StringEncrypter.generateIv();

                        // encrypt 키들을 db에 저장
                        String keyBase64 = StringEncrypter.encodeSecretKeyToBase64(key);
                        String ivBase64 = StringEncrypter.encodeIvToBase64(iv);
                        if (accInfoJson == null) {
                            jaDao.add(seqMall, seqUser, 1, keyBase64, ivBase64);
                        } else {
                            jaDao.update(seqMall, seqUser, 1, keyBase64, ivBase64);
                        }
                    }
                    
                    us.removeFailedAuthMallSeq(seqMall); // 실패 목록에서 찾아서 제거
                    us.addMallUsrid(seqMall, usrid); // 세션에 몰아이디 저장

                    String cipherUsrId = StringEncrypter.encrypt(StringEncrypter.ALGORITHM, seqUser + "%" + usrid, key, iv);
                    String cipherUsrPw = StringEncrypter.encrypt(StringEncrypter.ALGORITHM, seqUser + "%" + usrpw, key, iv);

                    // 암호화된 id 와 pw를 전달
                    resObj.put("cip_id_key", JangBoGoManager.getCipidkey(seqMall, us.getSeq()));
                    resObj.put("cip_id_val", cipherUsrId);
                    resObj.put("cip_pw_key", JangBoGoManager.getCippwkey(seqMall, us.getSeq()));
                    resObj.put("cip_pw_val", cipherUsrPw);

                    resObj.put("status", 1);
                    resCode = EnvSYS.RESCODE_SUCC;
                    resMsg = "연결에 성공하였습니다.\n지니박스에 로그인시 쇼핑 내역을 조회할 수 있습니다.";
                } else if (resultConn == 0) {
                    // 연결테스트에 실패한 경우
                    resObj.put("status", 2);
                    resCode = EnvSYS.RESCODE_FAIL;
                    resMsg = "연결에 실패하였습니다.";
                } else if (resultConn == 2) {
                    resObj.put("status", 0);
                    resCode = EnvSYS.RESCODE_FAIL;
                    resMsg = "마지막 시도 이후 일정 시간이 경과되어야만 합니다.(30분 이상)";
                }
            } else if ("0".equals(statusTo)) {
                jaDao.setAccountStatus(seqMall, seqUser, 0);
                resObj.put("status", 0);
                resCode = EnvSYS.RESCODE_SUCC;
                resMsg = "연결을 해제 하였습니다.";
            }

            resObj.put("sequ", seqUser);
            resObj.put("seqm", seqMall);
            resObj.put("id", usrid);
        }

        resObj.put("code", resCode);
        resObj.put("msg", resMsg);

        return resObj;
    }

    private void fetchOrders(HttpServletRequest req) throws Exception {
        UserSession us = AuthManager.getInstance().getUserSession(req);
        String seqUser = us.getSeq();
        log.debug(">> sequ-" + seqUser);
        log.debug(">> seqm-" + req.getParameter("seqm"));
        log.debug(">> id-" + req.getParameter("id"));
        log.debug(">> pw-" + req.getParameter("pw"));

        BoxDataAccessObject boxDao = new BoxDataAccessObject();
        JSONObject pendboxJson = boxDao.getPendBox(us.getSeqDefstore());
        String seqPendBox = pendboxJson.get("seq").toString();

        String seqMall = req.getParameter("seqm");
        String cipherUsrId = req.getParameter("id").trim();
        String cipherUsrPw = req.getParameter("pw").trim();

        /*
         * 사용자 계정 정보 복호화
         */
        JbgAccessDataAccessObject jaDao = new JbgAccessDataAccessObject();
        JSONObject accInfoJson = jaDao.getAccessInfo(seqMall, seqUser);
        if (accInfoJson != null && accInfoJson.containsKey("enckey") && accInfoJson.get("enckey") != null) {
            try {
                String keyBase64 = accInfoJson.get("enckey").toString().trim();
                String ivBase64 = accInfoJson.get("enciv").toString().trim();
                SecretKey key = StringEncrypter.decodeBase64ToSecretKey(keyBase64);
                IvParameterSpec iv = StringEncrypter.decodeBase64ToIv(ivBase64);
                String hashed_usrId = StringEncrypter.decrypt(StringEncrypter.ALGORITHM, cipherUsrId, key, iv);
                String hashed_usrPw = StringEncrypter.decrypt(StringEncrypter.ALGORITHM, cipherUsrPw, key, iv);
                String usrId = hashed_usrId.substring(hashed_usrId.indexOf('%') + 1);
                String usrPw = hashed_usrPw.substring(hashed_usrPw.indexOf('%') + 1);

                log.debug("decrypted mall id : " + usrId);
                log.debug("decrypted mall pw : 비밀");
                us.addMallUsrid(seqMall, usrId);

                JangBoGoManager jbgman = new JangBoGoManager();
                jbgman.updateItems(us.getSeqDefstore(), seqUser, seqPendBox, seqMall, usrId, usrPw);
            } catch (Exception e) {
                // Error : Given final block not properly padded. Such issues can arise if a bad
                // key is used during decryption.
                // 대응 : 다른 브라우저에서 암호화키를 갱신하였으므로 현재 브라우저에 저장된 사용자 계정은 복호화 할 수 없음
                log.error(e.getLocalizedMessage());
                log.error(ExceptionUtil.getExceptionInfo(e));
                us.setInstantMessage("장보고에서 쇼핑몰 계정 정보가 갱신되어 쇼핑몰 계정을 다시 저장해야 합니다.");
                us.addFailedAuthMallSeq(seqMall);
            }
        } else {
            log.debug("계정 암호화 키가 없음");
        }
    }
    
    /**
     * 자동 수집 활성화/비활성화
     * 
     * @param req HttpServletRequest
     * @return JSONObject { code, msg, enabled }
     */
    private JSONObject toggleAutoCollect(HttpServletRequest req) {
        JSONObject resObj = new JSONObject();
        
        try {
            UserSession us = AuthManager.getInstance().getUserSession(req);
            if (us == null) {
                resObj.put("code", "401");
                resObj.put("msg", "로그인이 필요합니다.");
                return resObj;
            }
            
            String enabledParam = req.getParameter("enabled");
            boolean enabled = "1".equals(enabledParam);
            
            // 수집 주기 파라미터
            String intervalParam = req.getParameter("interval");
            int intervalMinutes = 5;  // 기본값
            if (intervalParam != null && !intervalParam.isEmpty()) {
                try {
                    intervalMinutes = Integer.parseInt(intervalParam);
                } catch (NumberFormatException e) {
                    log.warn("잘못된 interval 값: " + intervalParam + ", 기본값 5분 사용");
                }
            }
            
            log.info("자동 수집 상태 변경 요청 - seqUser: " + us.getSeq() + 
                    ", enabled: " + enabled + ", interval: " + intervalMinutes + "분");
            
            // DB에 설정 저장
            JbgInfoDataAccessObject jbgInfoDao = new JbgInfoDataAccessObject();
            
            // 1. 사용자별 자동 수집 설정 저장 (jbg_info)
            jbgInfoDao.setAutoCollectConfig(us.getSeq(), enabled ? 1 : 0, intervalMinutes);
            log.info("자동 수집 설정 DB 저장 완료 (jbg_info)");
            
            // 스케줄러 상태 변경
            FtpFileProcessorScheduler ftpScheduler = FtpFileProcessorScheduler.getInstance();
            if (ftpScheduler != null) {
                // DB에서 최신 상태 다시 확인 (다른 사용자가 변경했을 수 있음)
                ftpScheduler.loadEnabledFromDatabase();
                
                // 주기 설정
                ftpScheduler.setInterval(intervalMinutes);
                
                // enabled 상태 설정 (DB에 저장된 값 반영)
                ftpScheduler.setEnabled(enabled);
                
                // 활성화 시 즉시 1회 실행
                if (enabled) {
                    log.info("자동 수집 활성화 - 즉시 1회 실행");
                    ftpScheduler.executeNow();
                }
                
                resObj.put("code", "000");
                resObj.put("msg", enabled ? "자동 수집이 활성화되었습니다." : "자동 수집이 비활성화되었습니다.");
                resObj.put("enabled", enabled);
                resObj.put("interval", intervalMinutes);
                
                log.info("자동 수집 상태 변경 완료 - enabled: " + enabled + ", interval: " + intervalMinutes + "분");
            } else {
                resObj.put("code", "500");
                resObj.put("msg", "스케줄러가 초기화되지 않았습니다.");
                resObj.put("enabled", false);
                
                log.error("스케줄러가 null입니다.");
            }
            
        } catch (Exception e) {
            log.error("자동 수집 상태 변경 중 오류 발생", e);
            resObj.put("code", "999");
            resObj.put("msg", "오류가 발생했습니다: " + e.getMessage());
            resObj.put("enabled", false);
        }
        
        return resObj;
    }
    
    /**
     * 수집 주기 변경
     * 
     * @param req HttpServletRequest
     * @return JSONObject { code, msg, interval }
     */
    private JSONObject changeCollectInterval(HttpServletRequest req) {
        JSONObject resObj = new JSONObject();
        
        try {
            UserSession us = AuthManager.getInstance().getUserSession(req);
            if (us == null) {
                resObj.put("code", "401");
                resObj.put("msg", "로그인이 필요합니다.");
                return resObj;
            }
            
            String intervalParam = req.getParameter("interval");
            int intervalMinutes = 5; // 기본값
            
            try {
                intervalMinutes = Integer.parseInt(intervalParam);
            } catch (NumberFormatException e) {
                log.warn("잘못된 interval 값: " + intervalParam);
            }
            
            // 유효한 값인지 검증
            if (intervalMinutes != 1 && intervalMinutes != 5 && intervalMinutes != 10 && 
                intervalMinutes != 30 && intervalMinutes != 60) {
                resObj.put("code", "400");
                resObj.put("msg", "유효하지 않은 주기입니다. 1, 5, 10, 30, 60분 중 선택하세요.");
                return resObj;
            }
            
            log.info("수집 주기 변경 요청 - interval: " + intervalMinutes + "분");
            
            // DB에서 현재 enabled 상태 조회 후 interval만 변경하여 저장
            JbgInfoDataAccessObject jbgInfoDao = new JbgInfoDataAccessObject();
            JSONObject currentConfig = jbgInfoDao.getAutoCollectConfig(us.getSeq());
            
            // org.json.simple.JSONObject는 getInt()가 없으므로 get()으로 조회 후 형변환
            Object enabledObj = currentConfig.get("enabled");
            int currentEnabled = (enabledObj instanceof Number) ? ((Number) enabledObj).intValue() : 0;
            
            jbgInfoDao.setAutoCollectConfig(us.getSeq(), currentEnabled, intervalMinutes);
            log.info("수집 주기 DB 저장 완료");
            
            // 스케줄러 주기 변경
            FtpFileProcessorScheduler ftpScheduler = FtpFileProcessorScheduler.getInstance();
            if (ftpScheduler != null) {
                ftpScheduler.setInterval(intervalMinutes);
                
                resObj.put("code", "000");
                resObj.put("msg", "수집 주기가 변경되었습니다.");
                resObj.put("interval", intervalMinutes);
                
                log.info("수집 주기 변경 완료 - interval: " + intervalMinutes + "분");
            } else {
                resObj.put("code", "500");
                resObj.put("msg", "스케줄러가 초기화되지 않았습니다.");
                
                log.error("스케줄러가 null입니다.");
            }
            
        } catch (Exception e) {
            log.error("수집 주기 변경 중 오류 발생", e);
            resObj.put("code", "999");
            resObj.put("msg", "오류가 발생했습니다: " + e.getMessage());
        }
        
        return resObj;
    }
    
    /**
     * FTP 파일 처리 즉시 실행
     * 
     * @param req HttpServletRequest
     * @return JSONObject { code, msg, result }
     */
    private JSONObject executeProcessorNow(HttpServletRequest req) {
        JSONObject resObj = new JSONObject();
        
        try {
            UserSession us = AuthManager.getInstance().getUserSession(req);
            if (us == null) {
                resObj.put("code", "401");
                resObj.put("msg", "로그인이 필요합니다.");
                return resObj;
            }
            
            log.info("FTP 파일 처리 즉시 실행 요청 - seqUser: " + us.getSeq());
            
            // 스케줄러 즉시 실행
            FtpFileProcessorScheduler ftpScheduler = FtpFileProcessorScheduler.getInstance();
            if (ftpScheduler != null) {
                ftpScheduler.executeNow();
                
                resObj.put("code", "000");
                resObj.put("msg", "FTP 파일 처리가 완료되었습니다.");
                resObj.put("result", "처리 완료 (상세 내용은 로그 참조)");
                
                log.info("FTP 파일 처리 즉시 실행 완료");
            } else {
                resObj.put("code", "500");
                resObj.put("msg", "스케줄러가 초기화되지 않았습니다.");
                
                log.error("스케줄러가 null입니다.");
            }
            
        } catch (Exception e) {
            log.error("즉시 실행 중 오류 발생", e);
            resObj.put("code", "999");
            resObj.put("msg", "오류가 발생했습니다: " + e.getMessage());
        }
        
        return resObj;
    }
}
