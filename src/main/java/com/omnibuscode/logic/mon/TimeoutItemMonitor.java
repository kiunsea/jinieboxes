package com.omnibuscode.logic.mon;

import java.util.Iterator;
import java.util.List;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.json.simple.JSONObject;

import com.omnibuscode.dao.BoxDataAccessObject;
import com.omnibuscode.dao.ItemDataAccessObject;
import com.omnibuscode.dao.StoreDataAccessObject;
import com.omnibuscode.utils.ExceptionUtil;

/**
 * 보관함들의 아이템들을 정리
 * 
 * @author KIUNSEA
 *
 */
public class TimeoutItemMonitor implements Runnable {

    private Logger log = LogManager.getLogger(TimeoutItemMonitor.class);
    
    @Override
    public void run() {
        
        log.debug("# TimeoutItemManager Start~");
        
        while (true) {
	        StoreDataAccessObject storeDao = new StoreDataAccessObject();
	        BoxDataAccessObject boxDao = new BoxDataAccessObject();
	        ItemDataAccessObject itemDao = new ItemDataAccessObject();
	        
	        List<JSONObject> boxes = null;
	        try {
	            boxes = boxDao.getAllBoxes();
	        } catch (Exception e) {
	            log.error(ExceptionUtil.getExceptionInfo(e));
	        }
	        if (boxes == null) {
	            // DB 미설정/연결 실패 등으로 조회 불가 — 다음 주기까지 sleep
	            try {
	                Thread.sleep(1000 * 60 * 60 * 24);
	            } catch (Exception e) {
	                log.error(ExceptionUtil.getExceptionInfo(e));
	            }
	            continue;
	        }
	        Iterator<JSONObject> boxIter = boxes.iterator();
	        JSONObject boxInfo = null;
	        String seqBox = null;
	        int standbyDays = -1;
	        while (boxIter.hasNext()) { // 보관함들을 순회
	            boxInfo = (JSONObject) boxIter.next();
	
	            try {
	                seqBox = boxInfo.get("seq").toString();
					if ("-1".equals(boxInfo.get("type").toString())) {
						standbyDays = storeDao.getItemStandbyDays(boxInfo.get("seq_store").toString());
					} else {
						standbyDays = Integer.parseInt(boxInfo.get("hide_after").toString());
					}
					if (standbyDays > 0) {
						itemDao.updateToClose(seqBox, standbyDays);
					}
	            } catch (Exception e) {
	                log.error(ExceptionUtil.getExceptionInfo(e));
	            }
	        }
	
	        try {
	            Thread.sleep(1000 * 60 * 60 * 24); // 반영 주기(초/SEC) - 현재 1일
	        } catch (Exception e) {
	            log.error(ExceptionUtil.getExceptionInfo(e));
	        }
        }
        
    }
}
