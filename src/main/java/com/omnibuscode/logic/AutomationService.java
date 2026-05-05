package com.omnibuscode.logic;

import java.util.Iterator;
import java.util.List;

import org.json.simple.JSONObject;

import com.omnibuscode.dao.AutoKeywordDataAccessObject;

/**
 * 분류 자동화 로직 어시스트 클래스
 * @author KIUNSEA
 *
 */
public class AutomationService {
    
    /**
     * 분류 자동화를 수행하여 이동할 보관함의 시퀀스를 전달 받는다.
     * 
     * @param itemName
     * @param seqStore
     * @return seqBox : 이동할 보관함의 시퀀스, 찾지 못한 경우엔 -1
     * @throws Exception
     */
    public int checkRules(String itemName, String seqStore) throws Exception {
        
        AutoKeywordDataAccessObject autokeyDao = new AutoKeywordDataAccessObject();
        List<JSONObject> rules = autokeyDao.getRules(seqStore);
        
        boolean enable = false;
        int matchCnt = 0;
        String[] keywordArr = null;
        String keywords = null;
        JSONObject rule = null;
        Iterator<JSONObject> ruleIter = rules.iterator();
        while (ruleIter.hasNext()) {
            rule = (JSONObject) ruleIter.next();
            
            enable = "1".equals(rule.get("status").toString()) ? true : false;
            if (enable) {
                keywords = rule.get("keywords").toString();
                keywordArr = this.getKeywordArray(keywords);

                for (String keyword : keywordArr) {
                    if (itemName.indexOf(keyword.trim()) > -1) {
                        matchCnt++;
                    }
                }

                boolean ruleMatched = false;
                int oper = Integer.parseInt(rule.get("keyoper").toString());
                if (oper == 0) { // OR operation
                    int mcnt = Integer.parseInt(rule.get("matchcnt").toString());
                    if (matchCnt > mcnt - 1) {
                        ruleMatched = true;
                    }
                } else if (oper == 1) { // AND operation
                    if (matchCnt == keywordArr.length) {
                        ruleMatched = true;
                    }
                }
                matchCnt = 0; // 초기화

                if (ruleMatched) {
                    String seqBox = rule.get("seq_box").toString();
                    return Integer.parseInt(seqBox);
                }
            }
        }
        
        return -1;
    }
    
    /**
     * 다음과 같은 형태의 문자열을 분석하여 문자열 배열 객체로 반환
     * String input = "[\"피코크\",\"조선호텔\",\"김치\"]";
     * @param keywords
     * @return
     */
    protected String[] getKeywordArray(String keywords) {

        // 문자열에서 대괄호 제거 및 트리밍
        String trimmedInput = keywords.substring(1, keywords.length() - 1).trim();

        // 큰따옴표 제거 및 콤마로 분할
        String[] elements = trimmedInput.split("\",\"");

        // 앞뒤 큰따옴표 제거
        for (int i = 0; i < elements.length; i++) {
            elements[i] = elements[i].replaceAll("^\"|\"$", "");
        }

        return elements;
    }
}
