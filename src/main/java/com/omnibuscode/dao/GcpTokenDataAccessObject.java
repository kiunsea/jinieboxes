package com.omnibuscode.dao;

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

/**
 * @author KIUNSEA
 *
 */
public class GcpTokenDataAccessObject extends CommonDataAccessObject {

    /**
     * 서버 환경 변경시 log4j.properties 설정 정보도 함께 변경 필요
     */
//    private Log log = LogFactory.getLog(UserDataAccessObject.class);
	private Logger log = LogManager.getLogger(GcpTokenDataAccessObject.class);

    public GcpTokenDataAccessObject() {
        ;
    }

    public void insertTokenInfo(String seqUser, String refreshToken, String refresh_expiry, String accessToken, String access_expiry) throws Exception {

        StringBuffer querySb = new StringBuffer();
        querySb.append("INSERT INTO gcp_token (");
        querySb.append("seq_user");
        if (!JinieboxUtil.isEmpty(refreshToken)) {
            querySb.append(",refreshToken");
            querySb.append(",refresh_expiry_date");
        }
        if (!JinieboxUtil.isEmpty(accessToken)) {
            querySb.append(",accessToken");
            querySb.append(",access_expiry_time");
        }
        querySb.append(")");
        querySb.append(" VALUES ");
        querySb.append("(");
        querySb.append(seqUser);
        if (!JinieboxUtil.isEmpty(refreshToken)) {
            querySb.append(",'" + refreshToken + "'");
            querySb.append("," + refresh_expiry);
        }
        if (!JinieboxUtil.isEmpty(accessToken)) {
            querySb.append(",'" + accessToken + "'");
            querySb.append("," + access_expiry);
        }
        querySb.append(")");

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
    
    /**
     *  access token 을 저장
     *  
     * @param seqUser
     * @param accessToken
     * @param accessExpTime 
     * @throws Exception
     */
    public void saveAccessToken(String seqUser, String accessToken, String accessExpTime) throws Exception {
        LocalDBConnection conn = null;
        try {
            conn = new LocalDBConnection();
            conn.txOpen();

            String queryStr = null;

            int pCnt = 0;
            String querySb = "SELECT COUNT(seq) AS pCnt FROM gcp_token WHERE seq_user = " + seqUser;
            log.debug("LOCALDB-QUERY------------------------------------------------------------------------------");
            log.debug(querySb);
            ResultSet rset = conn.executeQuery(querySb);
            if (rset != null && rset.next()) {
                pCnt = rset.getInt("pCnt");
            }
            if (pCnt > 0) {
                queryStr = this.getUpdateQueryOfAccessToken(seqUser, accessToken, accessExpTime);
            } else {
                queryStr = this.getInsertQueryOfAccessToken(seqUser, accessToken, accessExpTime);
            }
            log.debug("LOCAL-QUERY------------------------------------------------------------------------------");
            log.debug(queryStr);

            conn.txExecuteUpdate(queryStr);
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
    
    public void deleteToken(String seqUser) throws Exception {
        StringBuffer querySb = new StringBuffer();
        querySb.append("DELETE from gcp_token");
        querySb.append(" WHERE seq_user=" + seqUser);

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
    
    private String getUpdateQueryOfAccessToken(String seqUser, String accessToken, String access_expiry) {
        StringBuffer querySb = new StringBuffer();
        querySb.append("UPDATE gcp_token set");
        if (accessToken != null && access_expiry != null) {
            querySb.append(" access_token='" + accessToken + "'");
            querySb.append(", access_expiry_time=" + access_expiry);
        }
        querySb.append(" where seq_user=" + seqUser);

        return querySb.toString();
    }
    
    private String getInsertQueryOfAccessToken(String seqUser, String accessToken, String access_expiry) {
        StringBuffer querySb = new StringBuffer();
        querySb.append("INSERT INTO gcp_token (");
        querySb.append("seq_user");
        if (!JinieboxUtil.isEmpty(accessToken)) {
            querySb.append(",access_token");
            querySb.append(",access_expiry_time");
        }
        querySb.append(")");
        querySb.append(" VALUES ");
        querySb.append("(");
        querySb.append(seqUser);
        if (!JinieboxUtil.isEmpty(accessToken)) {
            querySb.append(",'" + accessToken + "'");
            querySb.append("," + access_expiry);
        }
        querySb.append(")");

        return querySb.toString();
    }
    
    /**
     * refresh token 을 저장
     * 
     * @param seqUser
     * @param refreshToken
     * @param refreshExpDate
     * @throws Exception
     */
    public void saveRefreshToken(String seqUser, String refreshToken, String refreshExpDate) throws Exception {
        LocalDBConnection conn = null;
        try {
            conn = new LocalDBConnection();
            conn.txOpen();

            String queryStr = null;

            int pCnt = 0;
            String querySb = "SELECT COUNT(seq) AS pCnt FROM gcp_token WHERE seq_user = " + seqUser;
            log.debug("LOCALDB-QUERY------------------------------------------------------------------------------");
            log.debug(querySb);
            ResultSet rset = conn.executeQuery(querySb);
            if (rset != null && rset.next()) {
                pCnt = rset.getInt("pCnt");
            }
            if (pCnt > 0) {
                queryStr = this.getUpdateQueryOfRefreshToken(seqUser, refreshToken, refreshExpDate);
            } else {
                queryStr = this.getInsertQueryOfRefreshToken(seqUser, refreshToken, refreshExpDate);
            }
            log.debug("LOCAL-QUERY------------------------------------------------------------------------------");
            log.debug(queryStr);

            conn.txExecuteUpdate(queryStr);
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
    
    private String getUpdateQueryOfRefreshToken(String seqUser, String refreshToken, String refresh_expiry) {
        StringBuffer querySb = new StringBuffer();
        querySb.append("UPDATE gcp_token set");
        if (refreshToken != null && refresh_expiry != null) {
            querySb.append(" refresh_token='" + refreshToken + "'");
            querySb.append(", refresh_expiry_date=" + refresh_expiry);
        }
        querySb.append(" where seq_user=" + seqUser);

        return querySb.toString();
    }
    
    private String getInsertQueryOfRefreshToken(String seqUser, String refreshToken, String refresh_expiry) {
        StringBuffer querySb = new StringBuffer();
        querySb.append("INSERT INTO gcp_token (");
        querySb.append("seq_user");
        if (!JinieboxUtil.isEmpty(refreshToken)) {
            querySb.append(",refresh_token");
            querySb.append(",refresh_expiry_date");
        }
        querySb.append(")");
        querySb.append(" VALUES ");
        querySb.append("(");
        querySb.append(seqUser);
        if (!JinieboxUtil.isEmpty(refreshToken)) {
            querySb.append(",'" + refreshToken + "'");
            querySb.append("," + refresh_expiry);
        }
        querySb.append(")");

        return querySb.toString();
    }
    
    /**
     * 저장된 구글인증 토큰 (refresh_token & access_token) 반환
     * 
     * @param seqUser
     * @return
     * @throws Exception
     */
    public JSONObject getTokens(String seqUser) throws Exception {
        LocalDBConnection conn = null;
        try {
            conn = new LocalDBConnection();
            StringBuffer querySb = new StringBuffer("SELECT refresh_token, refresh_expiry_date, access_token, access_expiry_time from gcp_token");
            querySb.append(" WHERE seq_user = '" + seqUser + "'");
            log.debug("LOCALDB-QUERY------------------------------------------------------------------------------");
            log.debug(querySb);
            ResultSet rset = conn.executeQuery(querySb.toString());

            if (rset != null && this.getResultSetSize(rset) > 0) {
                JSONObject rtnJson = new JSONObject();
                rtnJson.put("refresh_token", rset.getString("refresh_token"));
                rtnJson.put("refresh_expiry_date", rset.getInt("refresh_expiry_date"));
                rtnJson.put("access_token", rset.getString("access_token"));
                rtnJson.put("access_expiry_time", rset.getLong("access_expiry_time"));
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
}
