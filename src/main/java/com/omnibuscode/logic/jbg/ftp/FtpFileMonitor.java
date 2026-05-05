package com.omnibuscode.logic.jbg.ftp;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.*;
import java.text.SimpleDateFormat;
import java.util.*;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.omnibuscode.base.EnvSYS;
import com.omnibuscode.dao.JbgInfoDataAccessObject;
import com.omnibuscode.utils.PropertiesUtil;

/**
 * FTP 홈 디렉토리의 파일 모니터링
 * 
 * - 처리 대기 파일 스캔
 * - committed 폴더로 이동
 * - failed 폴더로 이동 및 로그 저장
 * 
 * @author KIUNSEA
 */
public class FtpFileMonitor {
    
    private Logger log = LogManager.getLogger(FtpFileMonitor.class);
    
    /**
     * 모든 FTP 사용자의 홈 디렉토리를 스캔하여 처리 대기 파일 목록 반환
     * 
     * @return Map<ftpUserId, List<File>>
     * @throws Exception
     */
    public Map<String, List<File>> scanUnprocessedFiles() throws Exception {
        
        Map<String, List<File>> filesByUser = new HashMap<>();
        
        // 1. FTP 서버 홈 디렉토리 경로
        String ftpHomePath = getFtpHomePath();
        log.debug("FTP 홈 경로: " + ftpHomePath);
        
        // 2. 모든 FTP ID 조회
        JbgInfoDataAccessObject jbgInfoDao = new JbgInfoDataAccessObject();
        List<String> ftpIds = jbgInfoDao.getAutoCollectEnabledFtpIds();
        
        log.info("자동 수집 활성 계정 수: " + ftpIds.size());
        if (ftpIds.isEmpty()) {
            log.debug("자동 수집이 활성화된 FTP 계정이 없어 모니터링을 종료합니다.");
            return filesByUser;
        }
        
        // 3. 각 FTP 계정별 파일 스캔
        for (String ftpId : ftpIds) {
            String userHomePath = ftpHomePath + File.separator + ftpId;
            File userHomeDir = new File(userHomePath);
            
            if (!userHomeDir.exists() || !userHomeDir.isDirectory()) {
                log.warn("FTP 홈 디렉토리가 존재하지 않습니다: " + userHomePath);
                continue;
            }
            
            List<File> userFiles = new ArrayList<>();
            
            // *.encrypted 파일 검색 (committed, failed 폴더 제외)
            File[] files = userHomeDir.listFiles((dir, name) -> {
                // 숨김 파일 제외
                if (name.startsWith(".")) return false;
                
                // 하위 폴더 제외
                File file = new File(dir, name);
                if (file.isDirectory()) return false;
                
                // .encrypted 확장자만
                return name.endsWith(".encrypted");
            });
            
            if (files != null && files.length > 0) {
                for (File file : files) {
                    userFiles.add(file);
                    log.debug("  - " + file.getName());
                }
                log.info("FTP ID [" + ftpId + "] - 처리 대기 파일: " + userFiles.size() + "개");
            }
            
            if (!userFiles.isEmpty()) {
                filesByUser.put(ftpId, userFiles);
            }
        }
        
        return filesByUser;
    }
    
    /**
     * 처리 완료된 파일을 committed 폴더로 이동
     * 
     * @param ftpUserId FTP 계정 ID
     * @param file 처리 완료된 파일
     * @throws IOException
     */
    public void moveToCommitted(String ftpUserId, File file) throws IOException {
        
        String ftpHomePath = getFtpHomePath();
        String committedPath = ftpHomePath + File.separator + ftpUserId + File.separator + "committed";
        File committedDir = new File(committedPath);
        
        // committed 폴더가 없으면 생성
        if (!committedDir.exists()) {
            committedDir.mkdirs();
            log.info("committed 폴더 생성: " + committedPath);
        }
        
        // 파일 이동
        Path source = file.toPath();
        Path target = Paths.get(committedPath, file.getName());
        
        Files.move(source, target, StandardCopyOption.REPLACE_EXISTING);
        
        log.info("파일 이동 완료: " + file.getName() + " -> committed/");
    }
    
    /**
     * 처리 실패한 파일을 failed 폴더로 이동하고 오류 로그 저장
     * 
     * @param ftpUserId FTP 계정 ID
     * @param file 처리 실패한 파일
     * @param errorMessage 오류 메시지
     * @param exception 예외 객체 (선택)
     * @throws IOException
     */
    public void moveToFailed(String ftpUserId, File file, String errorMessage, Exception exception) throws IOException {
        
        String ftpHomePath = getFtpHomePath();
        String failedPath = ftpHomePath + File.separator + ftpUserId + File.separator + "failed";
        File failedDir = new File(failedPath);
        
        // failed 폴더가 없으면 생성
        if (!failedDir.exists()) {
            failedDir.mkdirs();
            log.info("failed 폴더 생성: " + failedPath);
        }
        
        // 파일 이동
        Path source = file.toPath();
        Path target = Paths.get(failedPath, file.getName());
        
        Files.move(source, target, StandardCopyOption.REPLACE_EXISTING);
        
        // 실패 로그 파일 생성
        String logFileName = file.getName() + ".failed.log";
        String logFilePath = failedPath + File.separator + logFileName;
        
        try (FileWriter logWriter = new FileWriter(logFilePath, java.nio.charset.StandardCharsets.UTF_8)) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            
            logWriter.write("========================================\n");
            logWriter.write("파일 처리 실패 로그\n");
            logWriter.write("========================================\n\n");
            logWriter.write("파일명: " + file.getName() + "\n");
            logWriter.write("FTP ID: " + ftpUserId + "\n");
            logWriter.write("실패 시각: " + sdf.format(new Date()) + "\n\n");
            logWriter.write("오류 메시지:\n");
            logWriter.write(errorMessage + "\n\n");
            
            if (exception != null) {
                logWriter.write("상세 오류:\n");
                logWriter.write(exception.getClass().getName() + ": " + exception.getMessage() + "\n\n");
                
                logWriter.write("스택 트레이스:\n");
                for (StackTraceElement element : exception.getStackTrace()) {
                    logWriter.write("  at " + element.toString() + "\n");
                }
            }
            
            logWriter.write("\n========================================\n");
            logWriter.flush();
        }
        
        // 복호화된 JSON 파일 생성 (pretty print)
        try {
            saveDecryptedJsonFile(ftpUserId, file, target, failedPath);
        } catch (Exception e) {
            log.warn("복호화된 JSON 파일 저장 실패: " + file.getName(), e);
            // JSON 파일 저장 실패는 전체 프로세스를 중단하지 않음
        }
        
        log.warn("파일 failed 폴더로 이동 및 로그 저장 완료: " + file.getName() + " -> failed/" + file.getName() + ".failed.log");
    }
    
    /**
     * 로그 파일명에서 원본 파일명과 json 확장자까지만 추출하여 복호화된 JSON 파일 저장
     * 
     * 예: jangbogo_status_20251221_135030_ftp.json.encrypted.failed.log
     *  -> jangbogo_status_20251221_135030_ftp.json.encrypted (원본 파일명)
     *  -> jangbogo_status_20251221_135030_ftp.json (json 확장자까지만)
     * 
     * @param ftpUserId FTP 계정 ID
     * @param originalFile 원본 파일 (이미 failed 폴더로 이동된 파일)
     * @param movedFile failed 폴더로 이동된 파일 경로
     * @param failedPath failed 폴더 경로
     * @throws Exception
     */
    private void saveDecryptedJsonFile(String ftpUserId, File originalFile, Path movedFile, String failedPath) throws Exception {
        
        // 1. 로그 파일명에서 원본 파일명 추출
        // 예: jangbogo_status_20251221_135030_ftp.json.encrypted.failed.log
        //  -> jangbogo_status_20251221_135030_ftp.json.encrypted
        String originalFileName = originalFile.getName();
        
        // 2. 원본 파일명에서 .json 확장자까지만 추출
        // 예: jangbogo_status_20251221_135030_ftp.json.encrypted
        //  -> jangbogo_status_20251221_135030_ftp.json
        String jsonFileName = extractJsonFileName(originalFileName);
        
        if (jsonFileName == null) {
            log.warn("JSON 파일명 추출 실패: " + originalFileName);
            return;
        }
        
        log.debug("JSON 파일명 추출: " + originalFileName + " -> " + jsonFileName);
        
        // 3. 원본 encrypted 파일 복호화
        FtpFileDecryptor decryptor = new FtpFileDecryptor();
        File encryptedFile = movedFile.toFile(); // 이미 failed 폴더로 이동된 파일
        
        String decryptedJsonContent;
        try {
            decryptedJsonContent = decryptor.decryptFile(encryptedFile, ftpUserId);
            log.debug("파일 복호화 완료: " + encryptedFile.getName());
        } catch (Exception e) {
            log.warn("파일 복호화 실패: " + encryptedFile.getName() + " - " + e.getMessage());
            throw new Exception("복호화 실패: " + e.getMessage(), e);
        }
        
        // 4. JSON 파싱 및 Pretty Print 적용
        ObjectMapper mapper = new ObjectMapper();
        mapper.enable(SerializationFeature.INDENT_OUTPUT); // Pretty print 활성화
        
        String jsonFilePath = failedPath + File.separator + jsonFileName;
        
        try {
            // JSON 문자열을 JsonNode로 파싱
            JsonNode jsonNode = mapper.readTree(decryptedJsonContent);
            
            // Pretty print로 JSON 파일 저장
            try (FileWriter jsonWriter = new FileWriter(jsonFilePath, java.nio.charset.StandardCharsets.UTF_8)) {
                mapper.writeValue(jsonWriter, jsonNode);
                jsonWriter.flush();
            }
            
            log.info("복호화된 JSON 파일 저장 완료: " + jsonFileName);
            
        } catch (Exception e) {
            log.warn("JSON 파싱 실패 - 원본 텍스트로 저장: " + jsonFileName, e);
            
            // JSON 파싱 실패 시 원본 텍스트를 그대로 저장
            // FileWriter 리소스 누수 방지를 위해 별도 try-catch로 처리
            try (FileWriter jsonWriter = new FileWriter(jsonFilePath, java.nio.charset.StandardCharsets.UTF_8)) {
                jsonWriter.write(decryptedJsonContent);
                jsonWriter.flush();
                log.info("복호화된 원본 텍스트 파일 저장 완료: " + jsonFileName);
            } catch (IOException ioEx) {
                // FileWriter 생성 또는 쓰기 실패 시에도 리소스는 try-with-resources로 자동 해제됨
                log.error("원본 텍스트 파일 저장 실패: " + jsonFileName, ioEx);
                throw new Exception("JSON 파일 저장 실패: " + ioEx.getMessage(), ioEx);
            }
        }
    }
    
    /**
     * 파일명에서 .json 확장자까지만 추출
     * 
     * 예: 
     * - jangbogo_status_20251221_135030_ftp.json.encrypted -> jangbogo_status_20251221_135030_ftp.json
     * - jangbogo_status_20251221_135030_ftp.json -> jangbogo_status_20251221_135030_ftp.json
     * 
     * @param fileName 원본 파일명
     * @return .json 확장자까지만 포함된 파일명, 추출 실패 시 null
     */
    private String extractJsonFileName(String fileName) {
        if (fileName == null || fileName.isEmpty()) {
            return null;
        }
        
        // .json.encrypted 또는 .json으로 끝나는 경우
        if (fileName.endsWith(".json.encrypted")) {
            // .encrypted 제거
            return fileName.substring(0, fileName.length() - ".encrypted".length());
        } else if (fileName.endsWith(".json")) {
            return fileName;
        } else {
            // .json 확장자가 없는 경우, 파일명에 .json 추가
            // 예: jangbogo_status_20251221_135030_ftp -> jangbogo_status_20251221_135030_ftp.json
            int lastDotIndex = fileName.lastIndexOf('.');
            if (lastDotIndex > 0) {
                // 확장자가 있는 경우 제거 후 .json 추가
                String nameWithoutExt = fileName.substring(0, lastDotIndex);
                return nameWithoutExt + ".json";
            } else {
                // 확장자가 없는 경우 .json 추가
                return fileName + ".json";
            }
        }
    }
    
    /**
     * FTP 서버 홈 디렉토리 경로 조회
     * 
     * @return FTP 홈 디렉토리 절대 경로
     */
    private String getFtpHomePath() {
        // 1. System Property에서 FTP 서버 base 경로 조회
        String ftpServerBasePath = System.getProperty("ftp.server.path");
        
        // 2. 없으면 EnvSYS 전역 설정 사용
        if ((ftpServerBasePath == null || ftpServerBasePath.isEmpty())
                && EnvSYS.FTP_SERVER_PATH != null && !EnvSYS.FTP_SERVER_PATH.isEmpty()) {
            ftpServerBasePath = EnvSYS.FTP_SERVER_PATH;
        }
        
        // 3. 그래도 없으면 PropertiesUtil에서 조회
        if (ftpServerBasePath == null || ftpServerBasePath.isEmpty()) {
            ftpServerBasePath = PropertiesUtil.get("FTP_SERVER_PATH");
        }
        
        // 4. 여전히 없으면 기본 절대 경로 사용
        if (ftpServerBasePath == null || ftpServerBasePath.isEmpty()) {
            ftpServerBasePath = "D:/DEV/ftp/apache-ftpserver-1.1.4";
            log.debug("FTP 서버 경로 설정 없음 - 기본 경로 사용: " + ftpServerBasePath);
        }
        
        // 5. base 경로에 /res/home 추가하여 홈 디렉토리 경로 생성
        String ftpHomePath = ftpServerBasePath + File.separator + "res" + File.separator + "home";
        
        return ftpHomePath;
    }
}

