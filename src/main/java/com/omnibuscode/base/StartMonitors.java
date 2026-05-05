package com.omnibuscode.base;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import jakarta.servlet.http.HttpServlet;

import com.omnibuscode.logic.jbg.ftp.FtpFileProcessorScheduler;
import com.omnibuscode.logic.mon.ExpiredItemMonitor;
import com.omnibuscode.logic.mon.TimeoutItemMonitor;

/**
 * web.xml 에 추가하여 시스템 기동시 Thread Class 들을 자동으로 실행하게 하는 클래스
 * 
 * @author KIUNSEA
 *
 */
public class StartMonitors extends HttpServlet {

    private static final long serialVersionUID = 1L;
    
    private static final Logger log = LogManager.getLogger(StartMonitors.class);

    public void init() {
    	
			new Thread(new ExpiredItemMonitor()).start(); log.info("유효기간 체크 모니터 시작!");
			new Thread(new TimeoutItemMonitor()).start(); log.info("숨김항목 체크 모니터 시작!");
			
			// FTP 파일 처리 스케줄러 초기화 (Jangbogo 활성 시에만)
			if (!IntegrationGate.isJangbogoEnabled()) {
			    log.info("Jangbogo 비활성화 - FTP 파일 처리 스케줄러를 시작하지 않습니다.");
			} else {
			    try {
			        FtpFileProcessorScheduler scheduler = FtpFileProcessorScheduler.getInstance();
			        // DB에서 초기 enabled 상태 로드
			        scheduler.loadEnabledFromDatabase();
			        // 스케줄러 시작
			        scheduler.start();
			        log.info("FTP 파일 처리 스케줄러 초기화 완료 (StartMonitors) - 자동 수집: " +
			                (scheduler.isEnabled() ? "활성화" : "비활성화"));
			    } catch (Exception e) {
			        log.error("FTP 파일 처리 스케줄러 초기화 실패", e);
			    }
			}
		
    }
}
