package com.omnibuscode.logic.jbg.ftp;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.json.simple.JSONObject;

import com.fasterxml.jackson.databind.JsonNode;
import com.omnibuscode.dao.BoxDataAccessObject;
import com.omnibuscode.dao.JbgInfoDataAccessObject;
import com.omnibuscode.dao.StoreDataAccessObject;
import com.omnibuscode.logic.jbg.MallOrderUpdater;

/**
 * jangbogo 데이터를 jiniebox DB에 저장
 * 
 * FTP로 전송된 jangbogo JSON 파일을 파싱하여
 * jiniebox의 item, jbg_order 테이블에 저장합니다.
 * 
 * @author KIUNSEA
 */
public class JangbogoOrderImporter {
    
    private Logger log = LogManager.getLogger(JangbogoOrderImporter.class);
    
    /**
     * jangbogo JSON 데이터를 jiniebox DB에 저장
     * 
     * @param jsonNode Jackson JsonNode
     * @param ftpUserId FTP 사용자 ID
     * @return ImportResult (성공/실패 건수)
     * @throws Exception
     */
    public ImportResult importOrders(JsonNode jsonNode, String ftpUserId) throws Exception {
        
        log.info("주문 import 시작 - FTP ID: " + ftpUserId);
        
        // 1. FTP ID로 jiniebox 사용자 조회
        JbgInfoDataAccessObject jbgInfoDao = new JbgInfoDataAccessObject();
        String seqUser = jbgInfoDao.getUserSeqByFtpId(ftpUserId);
        
        if (seqUser == null) {
            throw new IllegalArgumentException("FTP ID에 매핑된 사용자 없음: " + ftpUserId);
        }
        
        log.debug("seq_user: " + seqUser);
        
        // 2. 사용자의 기본 store 조회
        StoreDataAccessObject storeDao = new StoreDataAccessObject();
        String seqStore = storeDao.getDefaultStoreSeq(seqUser);
        
        if (seqStore == null) {
            throw new IllegalStateException("사용자 " + seqUser + "의 store가 없습니다.");
        }
        
        log.debug("seq_store: " + seqStore);
        
        // 3. 등록대기 박스 조회 (type=-1)
        BoxDataAccessObject boxDao = new BoxDataAccessObject();
        JSONObject pendBox = boxDao.getPendBox(seqStore);
        String seqPendBox;
        
        if (pendBox == null) {
            log.warn("등록대기 박스가 없습니다. 생성 필요.");
            throw new IllegalStateException("등록대기 박스가 없습니다. store " + seqStore);
        } else {
            seqPendBox = pendBox.get("seq").toString();
            log.debug("seq_pend_box: " + seqPendBox);
        }
        
        // 4. MallOrderUpdater의 updateItems() 재사용
        log.info("MallOrderUpdater.updateItems() 호출 - seqStore: " + seqStore + 
                ", seqUser: " + seqUser + ", seqBox: " + seqPendBox);
        
        MallOrderUpdater updater = new MallOrderUpdater();
        JsonNode result = updater.updateItems(
            jsonNode,
            seqStore,
            seqUser,
            null,  // seqMall은 mall_id로 조회
            seqPendBox
        );
        
        int itemCount = result.get("item_count").asInt();
        
        log.info("주문 import 완료 - 상품 " + itemCount + "개 저장");
        
        return new ImportResult(itemCount, 0, jsonNode.size());
    }
    
    /**
     * Import 결과
     */
    public static class ImportResult {
        private int itemCount;      // 저장된 상품 개수
        private int failCount;      // 실패 건수
        private int orderCount;     // 처리한 주문 개수
        
        public ImportResult(int itemCount, int failCount, int orderCount) {
            this.itemCount = itemCount;
            this.failCount = failCount;
            this.orderCount = orderCount;
        }
        
        public int getItemCount() { return itemCount; }
        public int getFailCount() { return failCount; }
        public int getOrderCount() { return orderCount; }
        
        @Override
        public String toString() {
            return String.format("ImportResult{주문: %d개, 상품: %d개, 실패: %d개}", 
                               orderCount, itemCount, failCount);
        }
    }
}

