package com.omnibuscode.dao;

import java.net.URLDecoder;
import java.net.URLEncoder;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.json.simple.JSONObject;

import com.omnibuscode.base.LocalDBConnection;
import com.omnibuscode.util.JinieboxUtil;
import com.omnibuscode.utils.ExceptionUtil;
import com.omnibuscode.utils.NumberUtil;

/**
 * 쇼핑 아이템 분류 자동화 키워드
 */
public class AutoKeywordDataAccessObject extends CommonDataAccessObject {

    /**
     * 서버 환경 변경시 log4j.properties 설정 정보도 함께 변경 필요
     */
	private Logger log = LogManager.getLogger(AutoKeywordDataAccessObject.class);

	public int NUM_OR_OPERATION = 0;
	public int NUM_AND_OPERATION = 1;
	
    public AutoKeywordDataAccessObject() {;}
    
    public int add(String keywords, int keyoper, int matchcnt, String seqBox, int status, String seqStore) throws Exception {

        int seqOrder = -1;
        
        StringBuffer querySb = new StringBuffer();
        querySb.append("INSERT INTO auto_keyword (");
        querySb.append("keywords");
        querySb.append(",keyoper");
        querySb.append(",matchcnt");
        querySb.append(",seq_box");
        querySb.append(",status");
        querySb.append(",seq_store");
        querySb.append(") values (");
        querySb.append("'" + URLEncoder.encode(keywords, "UTF-8") + "'");
        querySb.append(", " + keyoper);
        querySb.append(", " + matchcnt);
        querySb.append(", " + seqBox);
        querySb.append(", " + status);
        querySb.append(", " + seqStore);
        querySb.append(")");
        log.debug("LOCALDB-QUERY------------------------------------------------------------------------------");
        log.debug(querySb);

        LocalDBConnection conn = null;
        try {
            conn = new LocalDBConnection();
            conn.txOpen();
            conn.txExecuteUpdate(querySb.toString());
            seqOrder = this.getLastInsertSeq(conn);
            conn.txCommit();
        } catch (SQLException e) {
            log.error("* 아이고!! ㅜ.ㅜ 데이터베이스 업데이트 에러 발생");
            log.error(ExceptionUtil.getExceptionInfo(e));
            throw e;
        } catch (Exception e) {
            log.error("* 데이터베이스 업데이트 에러 발생");
            log.error(ExceptionUtil.getExceptionInfo(e));
            conn.txRollBack();
            throw e;
        } finally {
            conn.close();            
        }

        return seqOrder;
    }
    
    /**
     * 규칙 정보를 반환
     * @param seqRule
     * @return
     * @throws Exception
     */
    public JSONObject getRule(String seqRule) throws Exception {
        LocalDBConnection conn = null;
        try {
            conn = new LocalDBConnection();
            StringBuffer querySb = new StringBuffer("SELECT seq, keywords, keyoper, matchcnt, seq_box, status from auto_keyword");
            querySb.append(" WHERE seq=" + seqRule);
            log.debug("LOCALDB-QUERY------------------------------------------------------------------------------");
            log.debug(querySb);
            ResultSet rset = conn.executeQuery(querySb.toString());

            if (rset != null) {
                JSONObject jsonObj = null;
                while(rset.next()) {
                    jsonObj = new JSONObject();
                    jsonObj.put("seq", rset.getInt("seq"));
                    jsonObj.put("keywords", URLDecoder.decode(rset.getString("keywords"), "UTF-8"));
                    jsonObj.put("keyoper", rset.getString("keyoper"));
                    jsonObj.put("matchcnt", rset.getString("matchcnt"));
                    jsonObj.put("seq_box", rset.getString("seq_box"));
                    jsonObj.put("status", rset.getInt("status"));
                }
                return jsonObj;
            }
            return null;
        } catch (Exception e) {
            log.error("* 프로그램 수행중 에러 발생");
            log.error(ExceptionUtil.getExceptionInfo(e));
            throw e;
        } finally {
            conn.close();
        }
    }

    public List<JSONObject> getRules(String seqStore) throws Exception {

        List<JSONObject> rules = null;
        
        LocalDBConnection conn = null;
        try {
            conn = new LocalDBConnection();
            StringBuffer querySb = new StringBuffer("SELECT seq, keywords, keyoper, matchcnt, seq_box, status FROM auto_keyword");
            querySb.append(" WHERE seq_store=" + seqStore);
            log.debug("LOCALDB-QUERY------------------------------------------------------------------------------");
            log.debug(querySb);
            ResultSet rset = conn.executeQuery(querySb.toString());

            if (rset != null) {
                rules = new ArrayList<JSONObject>();
                JSONObject jsonObj = null;
                while (rset.next()) {
                    jsonObj = new JSONObject();
                    jsonObj.put("seq", rset.getInt("seq"));
                    jsonObj.put("keywords", URLDecoder.decode(rset.getString("keywords"), "UTF-8"));
                    jsonObj.put("keyoper", rset.getString("keyoper"));
                    jsonObj.put("matchcnt", rset.getString("matchcnt"));
                    jsonObj.put("seq_box", rset.getString("seq_box"));
                    jsonObj.put("status", rset.getInt("status"));
                    
                    rules.add(jsonObj);
                }
                return rules;
            } else {
                return null;
            }
        } catch (Exception e) {
            log.error("* 프로그램 수행중 에러 발생");
            log.error(ExceptionUtil.getExceptionInfo(e));
            throw e;
        } finally {
            conn.close();
        }
    }
    
    public void update(String seq, String keywords, int keyoper, int matchcnt, String seqBox, int status) throws Exception {

        JSONObject ruleJson = this.getRule(seq);
        if (ruleJson != null) {
            StringBuffer querySb = new StringBuffer();
            querySb.append("UPDATE auto_keyword SET");
            querySb.append(" seq=" + seq);
            if (!JinieboxUtil.isEmpty(keywords))
                querySb.append(", keywords='" + URLEncoder.encode(keywords, "UTF-8") + "'");
            if (keyoper >= 0)
                querySb.append(", keyoper=" + keyoper);
            if (matchcnt >= 0)
                querySb.append(", matchcnt=" + matchcnt);
            if (!JinieboxUtil.isEmpty(seqBox))
                querySb.append(", seq_box=" + seqBox);
            if (status >= 0)
                querySb.append(", status=" + status);
            querySb.append(" WHERE seq=" + seq);

            String query = querySb.toString();
            log.debug("LOCAL-QUERY------------------------------------------------------------------------------");
            log.debug(query);

            LocalDBConnection conn = null;
            try {
                conn = new LocalDBConnection();
                conn.txOpen();
                conn.txExecuteUpdate(query);
                conn.txCommit();
            } catch (SQLException e) {
                log.error("* 아이고!! ㅜ.ㅜ 데이터베이스 업데이트 에러 발생");
                log.error(ExceptionUtil.getExceptionInfo(e));
                throw e;
            } catch (Exception e) {
                log.error("* 아이고!! ㅜ.ㅜ 데이터베이스 업데이트 에러 발생");
                log.error(ExceptionUtil.getExceptionInfo(e));
                conn.txRollBack();
                throw e;
            } finally {
                conn.close();
            }
        }
    }
    
    public void delete(String seq) throws Exception {

        JSONObject ruleJson = this.getRule(seq);
        if (ruleJson != null) {
            StringBuffer querySb = new StringBuffer();
            querySb.append("DELETE from auto_keyword");
            querySb.append(" WHERE seq=" + ruleJson.get("seq"));

            String query = querySb.toString();
            log.debug("LOCAL-QUERY------------------------------------------------------------------------------");
            log.debug(query);

            LocalDBConnection conn = null;
            try {
                conn = new LocalDBConnection();
                conn.txOpen();
                conn.txExecuteUpdate(query);
                conn.txCommit();
            } catch (SQLException e) {
                log.error("* 아이고!! ㅜ.ㅜ 데이터베이스 업데이트 에러 발생");
                log.error(ExceptionUtil.getExceptionInfo(e));
                throw e;
            } catch (Exception e) {
                log.error("* 아이고!! ㅜ.ㅜ 데이터베이스 업데이트 에러 발생");
                log.error(ExceptionUtil.getExceptionInfo(e));
                conn.txRollBack();
                throw e;
            } finally {
                conn.close();
            }
        }
    }
}
