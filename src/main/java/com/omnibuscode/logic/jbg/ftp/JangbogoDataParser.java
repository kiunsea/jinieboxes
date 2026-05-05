package com.omnibuscode.logic.jbg.ftp;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * jangbogo JSON 파싱 및 검증
 * 
 * jangbogo에서 생성한 JSON 파일을 파싱하고
 * 필수 필드 및 데이터 형식을 검증합니다.
 * 
 * @author KIUNSEA
 */
public class JangbogoDataParser {
    
    private Logger log = LogManager.getLogger(JangbogoDataParser.class);
    
    /**
     * jangbogo JSON을 파싱
     * 
     * @param jsonContent JSON 문자열
     * @return JsonNode (Jackson)
     * @throws Exception
     */
    public JsonNode parse(String jsonContent) throws Exception {
        
        log.info("JSON 파싱 시작");
        
        ObjectMapper mapper = new ObjectMapper();
        JsonNode root = mapper.readTree(jsonContent);
        
        // 배열 형식 검증
        if (!root.isArray()) {
			throw new IllegalArgumentException("JSON은 배열 형식이어야 합니다. - " + root.asText());
        }
        
        log.info("JSON 파싱 완료 - 주문 개수: " + root.size());
        
        // 각 주문의 필수 필드 검증
        int index = 0;
        for (JsonNode order : root) {
            validateOrder(order, index);
            index++;
        }
        
        log.info("JSON 검증 완료 - 모든 필드 정상");
        
        return root;
    }
    
    /**
     * 주문 데이터 검증
     * 
     * @param order 주문 JsonNode
     * @param index 주문 인덱스
     * @throws Exception
     */
    private void validateOrder(JsonNode order, int index) throws Exception {
        
        // serial 검증
        if (!order.has("serial") || order.get("serial").asText().isEmpty()) {
            throw new IllegalArgumentException("주문 #" + index + ": serial 필드가 없습니다");
        }
        
        // datetime 검증
        if (!order.has("datetime") || order.get("datetime").asText().isEmpty()) {
            throw new IllegalArgumentException("주문 #" + index + ": datetime 필드가 없습니다");
        }
        
        String datetime = order.get("datetime").asText();
        if (!datetime.matches("\\d{8}")) {
            throw new IllegalArgumentException(
                "주문 #" + index + ": datetime 형식 오류 (YYYYMMDD 필요): " + datetime
            );
        }
        
        // mall_id 검증
        if (!order.has("mall_id") || order.get("mall_id").asText().isEmpty()) {
            throw new IllegalArgumentException("주문 #" + index + ": mall_id 필드가 없습니다");
        }
        
        // items 검증
        if (!order.has("items") || !order.get("items").isArray()) {
            throw new IllegalArgumentException("주문 #" + index + ": items 필드가 없거나 배열이 아닙니다");
        }
        
        // 각 상품 검증
        JsonNode items = order.get("items");
        for (int i = 0; i < items.size(); i++) {
            JsonNode item = items.get(i);
            
            if (!item.has("name") || item.get("name").asText().isEmpty()) {
                throw new IllegalArgumentException(
                    "주문 #" + index + ", 상품 #" + i + ": name 필드가 없습니다"
                );
            }
            
            if (!item.has("qty") || item.get("qty").asText().isEmpty()) {
                throw new IllegalArgumentException(
                    "주문 #" + index + ", 상품 #" + i + ": qty 필드가 없습니다"
                );
            }
            
            // qty 숫자 형식 검증
            String qty = item.get("qty").asText();
            try {
                Integer.parseInt(qty);
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException(
                    "주문 #" + index + ", 상품 #" + i + ": qty는 숫자여야 합니다 (현재: " + qty + ")"
                );
            }
        }
        
        log.debug("주문 #" + index + " 검증 완료: serial=" + order.get("serial").asText() + 
                 ", mall_id=" + order.get("mall_id").asText() + 
                 ", items=" + items.size() + "개");
    }
}

