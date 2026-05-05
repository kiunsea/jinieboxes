package com.omnibuscode.logic.jbg.ftp;

import java.io.File;
import java.util.*;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.fasterxml.jackson.databind.JsonNode;

/**
 * FTP 파일 처리 메인 클래스
 * 
 * FTP 서버에 업로드된 jangbogo 파일을 주기적으로 스캔하여
 * 복호화 → 파싱 → DB 저장 → committed/failed 이동 과정을 수행합니다.
 * 
 * @author KIUNSEA
 */
public class FtpFileProcessor {
    
    private Logger log = LogManager.getLogger(FtpFileProcessor.class);
    
    private FtpFileMonitor monitor = new FtpFileMonitor();
    private FtpFileDecryptor decryptor = new FtpFileDecryptor();
    private JangbogoDataParser parser = new JangbogoDataParser();
    private JangbogoOrderImporter importer = new JangbogoOrderImporter();
    
    /**
     * 스케줄러에서 주기적으로 호출
     * 모든 FTP 계정의 업로드 파일을 처리
     */
    public ProcessResult processAllFiles() {
        
        log.info("========================================");
        log.info("FTP 파일 처리 시작");
        log.info("========================================");
        
        ProcessResult totalResult = new ProcessResult();
        
        try {
            // 1. 처리 대기 파일 목록 조회
            Map<String, List<File>> filesByUser = monitor.scanUnprocessedFiles();
            
            if (filesByUser.isEmpty()) {
                log.info("처리할 파일이 없습니다.");
                return totalResult;
            }
            
            int totalFiles = filesByUser.values().stream()
                .mapToInt(List::size)
                .sum();
            
            log.info("총 " + totalFiles + "개 파일 처리 시작");
            
            // 2. 각 FTP 사용자별 파일 처리
            for (Map.Entry<String, List<File>> entry : filesByUser.entrySet()) {
                String ftpUserId = entry.getKey();
                List<File> files = entry.getValue();
                
                log.info("----------------------------------------");
                log.info("FTP ID: " + ftpUserId + " - " + files.size() + "개 파일");
                log.info("----------------------------------------");
                
                // 2.1 각 파일 처리
                for (File file : files) {
                    try {
                        log.info("파일 처리 시작: " + file.getName());
                        
                        // 복호화
                        String jsonContent = decryptor.decryptFile(file, ftpUserId);
                        
                        // 파싱
                        JsonNode jsonNode = parser.parse(jsonContent);
                        
                        // DB 저장
                        JangbogoOrderImporter.ImportResult result = 
                            importer.importOrders(jsonNode, ftpUserId);
                        
                        log.info("파일 처리 완료: " + file.getName() + " - " + result.toString());
                        
                        // committed 폴더로 이동
                        monitor.moveToCommitted(ftpUserId, file);
                        
                        // 결과 누적
                        totalResult.addSuccess(result.getOrderCount(), result.getItemCount());
                        
                    } catch (Exception e) {
                        log.error("파일 처리 실패: " + file.getName(), e);
                        
                        // failed 폴더로 이동 및 로그 저장
                        try {
                            String errorMsg = "파일 처리 중 오류가 발생했습니다.\n\n" +
                                            "오류 유형: " + e.getClass().getSimpleName() + "\n" +
                                            "오류 메시지: " + e.getMessage();
                            
                            monitor.moveToFailed(ftpUserId, file, errorMsg, e);
                            
                            totalResult.addFail();
                            
                        } catch (Exception moveEx) {
                            log.error("failed 폴더 이동 실패: " + file.getName(), moveEx);
                        }
                    }
                }
            }
            
            log.info("========================================");
            log.info("FTP 파일 처리 완료 - " + totalResult.toString());
            log.info("========================================");
            
        } catch (Exception e) {
            log.error("FTP 파일 처리 중 심각한 오류 발생", e);
        }
        
        return totalResult;
    }
    
    /**
     * 처리 결과
     */
    public static class ProcessResult {
        private int successFiles = 0;   // 성공한 파일 개수
        private int failFiles = 0;      // 실패한 파일 개수
        private int totalOrders = 0;    // 저장된 주문 개수
        private int totalItems = 0;     // 저장된 상품 개수
        
        public void addSuccess(int orders, int items) {
            this.successFiles++;
            this.totalOrders += orders;
            this.totalItems += items;
        }
        
        public void addFail() {
            this.failFiles++;
        }
        
        public int getSuccessFiles() { return successFiles; }
        public int getFailFiles() { return failFiles; }
        public int getTotalOrders() { return totalOrders; }
        public int getTotalItems() { return totalItems; }
        
        @Override
        public String toString() {
            return String.format("성공: %d파일 (주문: %d, 상품: %d), 실패: %d파일", 
                               successFiles, totalOrders, totalItems, failFiles);
        }
    }
}

