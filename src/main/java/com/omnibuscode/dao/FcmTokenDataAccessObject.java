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

/**
 * @author KIUNSEA
 *
 */
public class FcmTokenDataAccessObject extends CommonDataAccessObject {

    /**
     * 서버 환경 변경시 log4j.properties 설정 정보도 함께 변경 필요
     */
//    private Log log = LogFactory.getLog(UserDataAccessObject.class);
	private Logger log = LogManager.getLogger(FcmTokenDataAccessObject.class);

    public FcmTokenDataAccessObject() {
        ;
    }

    /**
     * 이미 등록된 토큰인지 확인
     * @param token
     * @param seqUser
     * @return
     * @throws Exception
     */
    public boolean existToken(String token, String seqUser) throws Exception {

        boolean result = false;

        String selQry = "SELECT * FROM fcm_token WHERE seq_user=" + seqUser + " AND token='" + token + "'";
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
     * 토큰 저장
     * @param token
     * @param seqUser
     * @return
     * @throws Exception
     */
    public boolean addToken(String token, String seqUser) throws Exception {

        StringBuffer querySb = new StringBuffer();
        querySb.append("INSERT INTO fcm_token (");
        querySb.append("token,");
        querySb.append("seq_user");
        querySb.append(") values ('");
        querySb.append(token + "', ");
        querySb.append(seqUser);
        querySb.append(")");
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
    
    /**
     * seq로 사용자의 토큰 조회
     * @param seqUser
     * @return
     * @throws Exception
     */
    public List<String> getTokens(String seqUser) throws Exception {
        LocalDBConnection conn = null;
        try {
            conn = new LocalDBConnection();
            StringBuffer querySb = new StringBuffer("SELECT seq, token from fcm_token");
            querySb.append(" WHERE seq_user = "+seqUser);
            log.debug("LOCALDB-QUERY------------------------------------------------------------------------------");
            log.debug(querySb);
            ResultSet rset = conn.executeQuery(querySb.toString());

            if (rset != null) {
                List<String> usrTokens = new ArrayList<String>();
                while (rset.next()) {
                    usrTokens.add(rset.getString("token"));
                }
                return usrTokens;
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
    
    public boolean deleteToken(String token) throws Exception {
        LocalDBConnection conn = null;
        try {
            conn = new LocalDBConnection();
            conn.txOpen();

            StringBuffer querySb = new StringBuffer();
            querySb.append("DELETE from fcm_token");
            querySb.append(" WHERE token = '" + token + "'");

            String query = querySb.toString();
            log.debug("LOCAL-QUERY------------------------------------------------------------------------------");
            log.debug(query);
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
        return true;
    }
}
