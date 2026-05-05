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
public class AccountNaverDataAccessObject extends CommonDataAccessObject {

    /**
     * 서버 환경 변경시 log4j.properties 설정 정보도 함께 변경 필요
     */
//    private Log log = LogFactory.getLog(UserDataAccessObject.class);
	private Logger log = LogManager.getLogger(AccountNaverDataAccessObject.class);

    public AccountNaverDataAccessObject() {
        ;
    }
    
    /**
     * 이미 등록된 회원 아이디인지 확인 (by naver user id)
     * @param naverid
     * @return
     * @throws Exception
     */
    public boolean existNaverid(String naverid) throws Exception {

        boolean result = false;

        String selQry = "SELECT * FROM account_naver WHERE user_id='" + naverid + "'";
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
            conn.close();
        }
        
        return result;
    }
    
    /**
     * 이미 등록된 회원 아이디인지 확인 (by naver user id & user seq)
     * @param naverid
     * @return
     * @throws Exception
     */
    public boolean existNaverid(String naverid, String seqUser) throws Exception {

        boolean result = false;

        String selQry = "SELECT * FROM account_naver WHERE user_id='" + naverid + "' AND seq_user = " + seqUser;
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
            conn.close();
        }
        
        return result;
    }
    
    /**
     * naver user id 로 사용자 정보 조회
     * @param naverId
     * @return
     * @throws Exception
     */
    public JSONObject getUserByNaverid(String naverId) throws Exception {
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
            querySb.append(" FROM user u, account_naver n");
            querySb.append(" WHERE u.seq = n.seq_user");
            querySb.append(" AND n.user_id ='" + naverId + "'");
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
            conn.close();
        }
    }
    
    /**
     * 네이버 아이디를 추가한다
     * @param naverId
     * @param seqUser
     * @return
     * @throws Exception
     */
    public boolean addNaverid(String naverId, String seqUser) throws Exception {
        StringBuffer querySb = new StringBuffer();
        querySb.append("INSERT INTO account_naver (");
        querySb.append("user_id,");
        querySb.append("seq_user");
        querySb.append(") values ('");
        querySb.append(naverId + "', '");
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
            conn.close();
        }
        
        return true;
    }
    
}
