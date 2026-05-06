package com.omnibuscode.dao;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.json.simple.JSONObject;

import com.omnibuscode.base.LocalDBConnection;
import com.omnibuscode.utils.ExceptionUtil;

/**
 * @author KIUNSEA
 *
 */
public class AccountClovaDataAccessObject extends CommonDataAccessObject {

    /**
     * 서버 환경 변경시 log4j.properties 설정 정보도 함께 변경 필요
     */
//    private Log log = LogFactory.getLog(UserDataAccessObject.class);
	private Logger log = LogManager.getLogger(AccountClovaDataAccessObject.class);

    public AccountClovaDataAccessObject() {
        ;
    }
    
    /**
     * clova id 로 user sequence 반환
     * @param clovaid
     * @return
     * @throws Exception
     */
    public String getUserSeq(String clovaid) throws Exception {
        LocalDBConnection conn = null;
        try {
            conn = new LocalDBConnection();
            StringBuffer querySb = new StringBuffer("SELECT seq_user from account_clova");
            querySb.append(" WHERE user_id = '" + clovaid + "'");
            log.debug("LOCALDB-QUERY------------------------------------------------------------------------------");
            log.debug(querySb);
            ResultSet rset = conn.executeQuery(querySb.toString());

            if (rset != null && this.getResultSetSize(rset) > 0) {
                return rset.getString("seq_user");
            }
            return null;
        } catch (Exception e) {
            log.error("* 프로그램 수행중 에러 발생");
            log.error(ExceptionUtil.getExceptionInfo(e));
            throw e;
        } finally {
            if (conn != null) try { conn.close(); } catch (Exception ignored) {}
        }
    }
    
    /**
     * clova user id 로 사용자 정보 조회
     * @param clovaId
     * @return
     * @throws Exception
     */
    public JSONObject getUserByClovaid(String clovaId) throws Exception {
        LocalDBConnection conn = null;
        try {
            conn = new LocalDBConnection();
            StringBuffer querySb = new StringBuffer("SELECT");
            querySb.append(" u.seq seq");
            querySb.append(", u.juid juid");
            querySb.append(", u.jupw jupw");
            querySb.append(", u.buid buid");
            querySb.append(", u.juname juname");
            querySb.append(", u.insert_time insert_time");
            querySb.append(", u.seq_defstore seq_defstore");
            querySb.append(", u.verified verified");
            querySb.append(", u.is_partner is_partner");
            querySb.append(", u.first_visit first_visit");
            querySb.append(" FROM user u, account_clova c");
            querySb.append(" WHERE u.seq = c.seq_user");
            querySb.append(" AND c.user_id ='" + clovaId + "'");
            log.debug("LOCALDB-QUERY------------------------------------------------------------------------------");
            log.debug(querySb);
            ResultSet rset = conn.executeQuery(querySb.toString());

            if (rset != null && this.getResultSetSize(rset) > 0) {
                JSONObject rtnJson = new JSONObject();
                rtnJson.put("seq", rset.getInt("seq"));
                rtnJson.put("juid", rset.getString("juid"));
                rtnJson.put("jupw", rset.getString("jupw"));
                rtnJson.put("buid", rset.getString("buid"));
                rtnJson.put("juname", rset.getString("juname"));
                rtnJson.put("insert_time", rset.getString("insert_time"));
                rtnJson.put("seq_defstore", rset.getString("seq_defstore"));
                rtnJson.put("verified", rset.getString("verified"));
                rtnJson.put("is_partner", rset.getInt("is_partner"));
                rtnJson.put("first_visit", rset.getInt("first_visit"));
                return rtnJson;
            }
            return null;
        } catch (Exception e) {
            log.error("* 프로그램 수행중 에러 발생");
            log.error(ExceptionUtil.getExceptionInfo(e));
            throw e;
        } finally {
            if (conn != null) try { conn.close(); } catch (Exception ignored) {}
        }
    }
    
    /**
     * 이미 등록된 회원 아이디인지 확인 (by clova user id & user seq)
     * @param clovaId
     * @return
     * @throws Exception
     */
    public boolean existClovaid(String clovaId, String seqUser) throws Exception {

        boolean result = false;

        String selQry = "SELECT * FROM account_clova WHERE user_id='" + clovaId + "' AND seq_user = " + seqUser;
        log.debug("LOCALDB-QUERY------------------------------------------------------------------------------");
        log.debug(selQry);
        
        LocalDBConnection conn = null;
        try {
            conn = new LocalDBConnection();
            ResultSet rset = conn.executeQuery(selQry);
            result = this.getResultSetSize(rset) > 0;
        } catch (Exception e) {
            log.error(ExceptionUtil.getExceptionInfo(e));
        } finally {
            if (conn != null) try { conn.close(); } catch (Exception ignored) {}
        }
        
        return result;
    }
    
    /**
     * 클로바 아이디를 추가한다
     * @param clovaId
     * @param seqUser
     * @return
     * @throws Exception
     */
    public boolean addClovaid(String clovaId, String seqUser) throws Exception {
        StringBuffer querySb = new StringBuffer();
        querySb.append("INSERT INTO account_clova (");
        querySb.append("user_id,");
        querySb.append("seq_user");
        querySb.append(") values ('");
        querySb.append(clovaId + "', '");
        querySb.append(seqUser);
        querySb.append("')");
        log.debug("LOCALDB-QUERY------------------------------------------------------------------------------");
        log.debug(querySb);

        LocalDBConnection conn = null;
        try {
            conn = new LocalDBConnection();
            conn.txOpen();
            conn.txExecuteUpdate(querySb.toString());
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
            if (conn != null) try { conn.close(); } catch (Exception ignored) {}
        }
        
        return true;
    }
    
}
