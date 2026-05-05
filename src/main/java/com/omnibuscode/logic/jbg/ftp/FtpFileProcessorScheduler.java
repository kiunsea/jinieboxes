package com.omnibuscode.logic.jbg.ftp;

import java.util.Timer;
import java.util.TimerTask;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.omnibuscode.dao.JbgInfoDataAccessObject;

/**
 * FTP 파일 처리 스케줄러
 * 
 * 5분마다 FTP 서버의 업로드 파일을 스캔하여 처리합니다.
 * UI 체크박스를 통해 자동 수집 활성화/비활성화 제어가 가능합니다.
 * DB의 jbg_info.auto_collect_enabled 값을 확인하여 활성화된 경우에만 실행됩니다.
 * 
 * @author KIUNSEA
 */
public class FtpFileProcessorScheduler {
    
    private static final Logger log = LogManager.getLogger(FtpFileProcessorScheduler.class);
    private static FtpFileProcessorScheduler instance;
    
    private Timer timer;
    private long interval = 5 * 60 * 1000; // 기본 5분 (밀리초)
    private boolean enabled = false;  // 자동 수집 활성화 여부
    
    private FtpFileProcessorScheduler() {;}
    
    /**
     * 스케줄러 시작
     */
    public void start() {
        if (timer != null) {
            log.warn("스케줄러가 이미 실행 중입니다.");
            return;
        }
        
        timer = new Timer("FtpFileProcessor", true);
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                // DB에서 최신 enabled 상태 확인
                loadEnabledFromDatabase();
                
                // 자동 수집이 활성화된 경우에만 실행
                if (enabled) {
                    try {
                        log.debug("자동 수집 활성화 - FTP 파일 처리 시작");
                        FtpFileProcessor processor = new FtpFileProcessor();
                        processor.processAllFiles();
                    } catch (Exception e) {
                        log.error("FTP 파일 처리 중 오류 발생", e);
                    }
                } else {
                    log.debug("자동 수집 비활성화 - 처리 스킵");
                }
            }
        }, 5000, interval);  // 5초 후 시작, interval 주기로 반복
        
        // 초기 enabled 상태 로드
        loadEnabledFromDatabase();
        
		log.info("FTP 파일 처리 스케줄러 시작 (" + (interval / 60000) + "분 간격, 자동 수집: " + 
		        (enabled ? "활성화" : "비활성화") + ")");
    }
    
    /**
     * 스케줄러 중지
     */
    public void stop() {
        if (timer != null) {
            timer.cancel();
            timer = null;
            log.info("FTP 파일 처리 스케줄러 중지");
        }
    }
    
    /**
     * 수집 주기 변경 (분 단위)
     * 
     * @param intervalMinutes 주기 (분)
     */
    public void setInterval(int intervalMinutes) {
        long newInterval = intervalMinutes * 60 * 1000L; // 분 → 밀리초 변환
        
        if (this.interval == newInterval) {
            log.info("수집 주기 동일 - 변경 불필요: " + intervalMinutes + "분");
            return;
        }
        
        this.interval = newInterval;
        log.info("수집 주기 변경됨: " + intervalMinutes + "분 (" + newInterval + "ms)");
        
        // 스케줄러 재시작 (새로운 주기 적용)
        if (timer != null) {
            log.info("새로운 주기 적용을 위해 스케줄러 재시작");
            stop();
            start();
        }
    }
    
    /**
     * 현재 수집 주기 조회 (분 단위)
     */
    public int getIntervalMinutes() {
        return (int) (interval / 60000);
    }
    
    /**
     * 자동 수집 활성화 여부 설정
     * 
     * @param enabled 활성화 여부
     */
    public void setEnabled(boolean enabled) {
        boolean changed = this.enabled != enabled;
        this.enabled = enabled;
        
        if (changed) {
            log.info("자동 수집 상태 변경: " + (enabled ? "활성화" : "비활성화"));
        }
    }
    
    /**
     * 현재 자동 수집 활성화 여부 조회
     * 
     * @return 활성화 여부
     */
    public boolean isEnabled() {
        return enabled;
    }
    
    /**
     * DB에서 자동 수집 활성화 상태 로드
     * jbg_info 테이블에서 auto_collect_enabled = 1인 사용자가 있는지 확인
     */
    public void loadEnabledFromDatabase() {
        try {
            JbgInfoDataAccessObject jbgInfoDao = new JbgInfoDataAccessObject();
            java.util.List<String> enabledFtpIds = jbgInfoDao.getAutoCollectEnabledFtpIds();
            boolean hasEnabledUsers = enabledFtpIds != null && !enabledFtpIds.isEmpty();
            
            if (this.enabled != hasEnabledUsers) {
                log.info("DB에서 자동 수집 상태 변경 감지: " + (hasEnabledUsers ? "활성화" : "비활성화") + 
                        " (활성화된 사용자 수: " + (enabledFtpIds != null ? enabledFtpIds.size() : 0) + ")");
            }
            
            this.enabled = hasEnabledUsers;
        } catch (Exception e) {
            log.error("DB에서 자동 수집 상태 로드 중 오류 발생", e);
            // 오류 발생 시 기존 상태 유지
        }
    }
    
    /**
     * 즉시 실행 (수동 트리거)
     */
    public void executeNow() {
        log.info("수동 실행 트리거");
        try {
            FtpFileProcessor processor = new FtpFileProcessor();
            processor.processAllFiles();
        } catch (Exception e) {
            log.error("수동 실행 중 오류 발생", e);
            throw new RuntimeException("FTP 파일 처리 실패: " + e.getMessage(), e);
        }
    }
    
    public static synchronized FtpFileProcessorScheduler getInstance() {
        if (instance == null) {
        	System.out.println("[FtpFileProcessorScheduler.java] Create New FtpFileProcessorScheduler !!");
            instance = new FtpFileProcessorScheduler();
        }
        return instance;
    }
}

