package com.omnibuscode.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.json.simple.JSONObject;

import com.omnibuscode.base.LocalDBConnection;
import com.omnibuscode.utils.ExceptionUtil;

public class AutoBarcodeDataAccessObject extends CommonDataAccessObject {

    /**
     * 서버 환경 변경시 log4j.properties 설정 정보도 함께 변경 필요
     */
	private Logger log = LogManager.getLogger(AutoBarcodeDataAccessObject.class);

    public AutoBarcodeDataAccessObject() {;}
    
    public int add(String barcode, String seqStore) throws Exception {

        int seqOrder = -1;
        
        StringBuffer querySb = new StringBuffer();
        querySb.append("INSERT INTO auto_barcode (");
        querySb.append("barcode");
        querySb.append(",seq_store");
        querySb.append(") values (");
        querySb.append("'" + barcode + "'");
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
    
    public List<JSONObject> getRules(String seqStore) throws Exception {

        List<JSONObject> rules = null;
        
        LocalDBConnection conn = null;
        try {
            conn = new LocalDBConnection();
            StringBuffer querySb = new StringBuffer("SELECT seq, barcode FROM auto_barcode");
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
                    jsonObj.put("barcode", rset.getString("barcode"));

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

}
