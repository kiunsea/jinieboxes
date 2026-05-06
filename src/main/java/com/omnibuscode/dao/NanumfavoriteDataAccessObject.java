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
import com.omnibuscode.utils.NumberUtil;

public class NanumfavoriteDataAccessObject extends CommonDataAccessObject {

    /**
     * 서버 환경 변경시 log4j.properties 설정 정보도 함께 변경 필요
     */
	private Logger log = LogManager.getLogger(NanumfavoriteDataAccessObject.class);

    public NanumfavoriteDataAccessObject() {;}

    /**
     * 저장소에 나눔을 추가
     * 
     * @param seqNanum
     * @param seqUser
     * @return
     * @throws Exception
     */
    public int insert(String seqNanum, String seqUser) throws Exception {

        int seqNF = -1;

        StringBuffer querySb = new StringBuffer();
        querySb.append("INSERT INTO nanum_favorite (");
        querySb.append("seq_nanum,");
        querySb.append("seq_user");
        querySb.append(") values (");
        querySb.append(seqNanum + ", ");
        querySb.append(seqUser);
        querySb.append(")");
        log.debug("LOCALDB-QUERY------------------------------------------------------------------------------");
        log.debug(querySb);

        LocalDBConnection conn = null;
        try {
            conn = new LocalDBConnection();
            conn.txOpen();
            conn.txExecuteUpdate(querySb.toString());
            seqNF = this.getLastInsertSeq(conn);
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

        return seqNF;
    }
	
    /**
     * favoite 를 삭제한다
     * 
     * @param seq
     * @throws Exception
     */
    public void delete(String seqNanum, String seqUser) throws Exception {

        StringBuffer querySb = new StringBuffer();
        querySb.append("DELETE from nanum_favorite");
        querySb.append(" WHERE seq_nanum=" + seqNanum);
        querySb.append(" AND seq_user=" + seqUser);

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
            if (conn != null) try { conn.close(); } catch (Exception ignored) {}
        }
    }
    
    /**
     * 나눔함을 즐겨찾기 했던 모든 정보를 삭제
     * 
     * @param seqNanum
     * @throws Exception
     */
    public void delete(String seqNanum) throws Exception {

        StringBuffer querySb = new StringBuffer();
        querySb.append("DELETE from nanum_favorite");
        querySb.append(" WHERE seq_nanum=" + seqNanum);

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
            if (conn != null) try { conn.close(); } catch (Exception ignored) {}
        }
    }

    /**
     * 사용자가 즐겨찾기로 등록한 나눔 목록을 반환한다
     * 
     * @param seqUser
     * @return
     * @throws Exception
     */
    public List<JSONObject> getUserFavorites(String seqUser) throws Exception {
        
        List<JSONObject> nanums = null;
        LocalDBConnection conn = null;
        try {
            conn = new LocalDBConnection();
            StringBuffer querySb = new StringBuffer(
                    "SELECT n.seq n_seq, n.name n_name, n.details n_details, n.seq_store n_seq_store, n.access_level n_accesslevel, n.share_code n_sharecode");
            querySb.append(" FROM nanum_favorite nf, nanum n");
            querySb.append(" WHERE nf.seq_nanum = n.seq");
            querySb.append(" AND nf.seq_user = " + seqUser);
            log.debug("LOCALDB-QUERY------------------------------------------------------------------------------");
            log.debug(querySb);
            ResultSet rset = conn.executeQuery(querySb.toString());

            if (rset != null) {
                nanums = new ArrayList<JSONObject>();
                JSONObject jsonObj = null;
                while (rset.next()) {
                    jsonObj = new JSONObject();
                    jsonObj.put("seq", rset.getInt("n_seq"));
                    jsonObj.put("name", rset.getString("n_name"));
                    jsonObj.put("details", rset.getString("n_details"));
                    jsonObj.put("seq_store", rset.getInt("n_seq_store"));
                    jsonObj.put("accesslevel", rset.getString("n_accesslevel"));
                    jsonObj.put("share_code", rset.getString("n_sharecode"));
                    
                    nanums.add(jsonObj);
                }
                return nanums;
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
     * 사용자의 즐겨찾기에 나눔 등록 여부
     * 
     * @param seqNanum
     * @param seqUser
     * @return
     * @throws Exception
     */
    public boolean hasAlready(String seqNanum, String seqUser) throws Exception {
        LocalDBConnection conn = null;
        try {
            conn = new LocalDBConnection();
            StringBuffer querySb = new StringBuffer("SELECT seq_nanum, seq_user from nanum_favorite");
            querySb.append(" WHERE seq_nanum = " + seqNanum);
            querySb.append(" AND seq_user = " + seqUser);
            log.debug("LOCALDB-QUERY------------------------------------------------------------------------------");
            log.debug(querySb);
            ResultSet rset = conn.executeQuery(querySb.toString());

            if (rset != null) {
                if (rset.next()) {
                    return true;
                }
            }
            return false;
        } catch (Exception e) {
            log.error("* 프로그램 수행중 에러 발생");
            log.error(ExceptionUtil.getExceptionInfo(e));
            throw e;
        } finally {
            if (conn != null) try { conn.close(); } catch (Exception ignored) {}
        }
    }

}
