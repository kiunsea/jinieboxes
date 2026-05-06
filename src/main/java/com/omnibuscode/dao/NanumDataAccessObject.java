package com.omnibuscode.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.json.simple.JSONObject;

import com.omnibuscode.base.EnvSYS;
import com.omnibuscode.base.LocalDBConnection;
import com.omnibuscode.util.JSONObjectExt;
import com.omnibuscode.utils.ExceptionUtil;
import com.omnibuscode.utils.NumberUtil;

/**
 * 나눔박스 DAO 클래스
 * @author KIUNSEA
 *
 */
public class NanumDataAccessObject extends CommonDataAccessObject {

    /**
     * 서버 환경 변경시 log4j.properties 설정 정보도 함께 변경 필요
     */
	private Logger log = LogManager.getLogger(NanumDataAccessObject.class);

    public NanumDataAccessObject() {;}

    /**
     * 보관함 정보를 반환
     * @param seqNanum
     * @return
     * @throws Exception
     */
    public JSONObject getNanum(String seqNanum) throws Exception {
        
        LocalDBConnection conn = null;
        try {
            conn = new LocalDBConnection();
            StringBuffer querySb = new StringBuffer("SELECT seq, name, details, seq_store, access_code, access_level from nanum");
            querySb.append(" WHERE seq = "+seqNanum);
            log.debug("LOCALDB-QUERY------------------------------------------------------------------------------");
            log.debug(querySb);
            ResultSet rset = conn.executeQuery(querySb.toString());

            if (rset != null) {
                JSONObject jsonObj = null;
                while(rset.next()) {
                    jsonObj = new JSONObject();
                    jsonObj.put("seq", rset.getInt("seq"));
                    jsonObj.put("name", rset.getString("name"));
                    jsonObj.put("details", rset.getString("details"));
                    jsonObj.put("seq_store", rset.getInt("seq_store"));
                    jsonObj.put("access_code", rset.getInt("access_code"));
                    jsonObj.put("access_level", rset.getString("access_level"));
                }
                return jsonObj;
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
     * 보관함 정보를 반환
     * @param seqNanum
     * @return
     * @throws Exception
     */
    public JSONObjectExt getNanumBySharecode(String shareCode) throws Exception {
        
        LocalDBConnection conn = null;
        try {
            conn = new LocalDBConnection();
            StringBuffer querySb = new StringBuffer("SELECT n.seq seq, n.name name, n.details details, n.seq_store seq_store, s.seq_owner seq_owner, n.access_level access_level, n.access_code access_code");
            querySb.append(" FROM nanum n, store s");
            querySb.append(" WHERE n.seq_store = s.seq");
            querySb.append(" AND n.share_code = '" + shareCode + "'");
            
            log.debug("LOCALDB-QUERY------------------------------------------------------------------------------");
            log.debug(querySb);
            ResultSet rset = conn.executeQuery(querySb.toString());

            if (rset != null) {
                JSONObjectExt jsonObj = null;
                while (rset.next()) {
                    jsonObj = new JSONObjectExt(EnvSYS.CLASS_TYPE_NANUM);
                    jsonObj.put("seq", rset.getInt("seq"));
                    jsonObj.put("name", rset.getString("name"));
                    jsonObj.put("details", rset.getString("details"));
                    jsonObj.put("seq_store", rset.getInt("seq_store"));
                    jsonObj.put("seq_owner", rset.getInt("seq_owner"));
                    jsonObj.put("access_level", rset.getString("access_level"));
                    jsonObj.put("access_code", rset.getString("access_code"));
                }
                return jsonObj;
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
     * 저장소에 나눔을 추가
     * 
     * @param name
     * @param details
     * @param seqStore
     * @param shareType
     * @param shareCode
     * @param accessCode
     * @return 추가된 nanum의 seq
     * @throws Exception
     */
    public int insert(String name, String details, String seqStore, String shareType, String shareCode, String accessCode) throws Exception {

        int seqNanum = -1;
        
        StringBuffer querySb = new StringBuffer();
        querySb.append("INSERT INTO nanum (");
        querySb.append("name");
        querySb.append(",details");
        querySb.append(",seq_store");
        querySb.append(",access_level");
        querySb.append(",share_code");
        if (accessCode != null) {
            querySb.append(",access_code");
        }
        querySb.append(") values (");
        querySb.append("'" + name + "'");
        querySb.append(", '" + details + "'");
        querySb.append(", "+seqStore);
        querySb.append(", '" + shareType + "'");
        querySb.append(", '" + shareCode + "'");
        if (accessCode != null) {
            querySb.append(", '" + accessCode + "'");
        }
        querySb.append(")");
        log.debug("LOCALDB-QUERY------------------------------------------------------------------------------");
        log.debug(querySb);

        LocalDBConnection conn = null;
        try {
            conn = new LocalDBConnection();
            conn.txOpen();
            conn.txExecuteUpdate(querySb.toString());
            seqNanum = this.getLastInsertSeq(conn);
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

        return seqNanum;
    }
    
	/**
     * seq를 검색하여 nanum 정보를 갱신한다<br/>
     * parameter 의 값을 null check 하여 값이 없는 경우엔 설정하지 않는다
     * 
	 * @param seq
     * @param name (null check)
     * @param details (null check)
     * @param seqStore (null check)
	 * @param accessLevel (null check)
	 * @param shareCode (null check)
	 * @param accessCode (null check)
	 * @throws Exception
	 */
	public void update(String seq, String name, String details, String seqStore, String accessLevel, String shareCode, String accessCode) throws Exception {

		JSONObject boxJson = this.getNanum(seq);
		if (boxJson != null) {
			StringBuffer querySb = new StringBuffer();
			querySb.append("UPDATE nanum SET");
			querySb.append(" seq=" + seq);
			if (accessLevel != null)
                querySb.append(", access_level='" + accessLevel + "'");
			if (shareCode != null)
                querySb.append(", share_code='" + shareCode + "'");
            if (accessCode != null)
                querySb.append(", access_code='" + accessCode + "'");
			if (name != null)
				querySb.append(", name='" + name + "'");
			if (details != null)
				querySb.append(", details='" + details + "'");
			if (NumberUtil.isNumber(seqStore))
				querySb.append(", seq_store=" + seqStore);
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
				if (conn != null) try { conn.close(); } catch (Exception ignored) {}
			}
		}
	}
	
	/**
	 * 나눔함을 삭제한다
	 * 
	 * @param seq
	 * @throws Exception
	 */
	public void delete(String seq) throws Exception {

		JSONObject boxJson = this.getNanum(seq);
		if (boxJson != null) {
			StringBuffer querySb = new StringBuffer();
			querySb.append("DELETE from nanum");
			querySb.append(" WHERE seq=" + boxJson.get("seq"));

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
	}
    
    /**
     * store 내의 모든 나눔함을 조회 (key is seq)
     * 
     * @param seqStore
     * @return JSONObject
     * @throws Exception
     */
    public List<JSONObject> getStoreNanums(String seqStore) throws Exception {
        
        List<JSONObject> nanums = null;
        LocalDBConnection conn = null;
        try {
            conn = new LocalDBConnection();
            StringBuffer querySb = new StringBuffer("SELECT seq, name, details, access_level, share_code, access_code from nanum");
            querySb.append(" WHERE seq_store =" + seqStore);
            log.debug("LOCALDB-QUERY------------------------------------------------------------------------------");
            log.debug(querySb);
            ResultSet rset = conn.executeQuery(querySb.toString());

            JSONObject jsonObj = null;
            if (rset != null) {
                nanums = new ArrayList<JSONObject>();
                while (rset.next()) {
                    jsonObj = new JSONObject();
                    jsonObj.put("seq", rset.getInt("seq"));
                    jsonObj.put("name", rset.getString("name"));
                    jsonObj.put("details", rset.getString("details"));
                    jsonObj.put("access_level", rset.getString("access_level"));
                    jsonObj.put("share_code", rset.getString("share_code"));
                    jsonObj.put("access_code", rset.getString("access_code"));
                    nanums.add(jsonObj);
                }
            }
            return nanums;
        } catch (Exception e) {
            log.error("* 프로그램 수행중 에러 발생");
            log.error(ExceptionUtil.getExceptionInfo(e));
            throw e;
        } finally {
            if (conn != null) try { conn.close(); } catch (Exception ignored) {}
        }
    }
    
    /**
     * 사용자에게 공유된 모든 나눔박스 목록을 반환
     * 
     * @param seqUser
     * @return
     * @throws Exception
     */
    public List<JSONObject> getNearbyNanums(String seqUser) throws Exception {
        
        List<JSONObject> nanums = null;
        LocalDBConnection conn = null;
        try {
            conn = new LocalDBConnection();
            StringBuffer querySb = new StringBuffer(
                    "SELECT n.seq seq, n.name name, n.details details, n.access_level access_level, n.share_code share_code, n.access_code access_code, s.authority authority");
            querySb.append(" from nanum n, share s");
            querySb.append(" WHERE n.seq = s.seq_object");
            querySb.append(" AND s.type_object = '" + EnvSYS.CLASS_TYPE_NANUM + "'");
            querySb.append(" AND s.seq_user = " + seqUser);
            log.debug("LOCALDB-QUERY------------------------------------------------------------------------------");
            log.debug(querySb);
            ResultSet rset = conn.executeQuery(querySb.toString());

            JSONObject jsonObj = null;
            if (rset != null) {
                nanums = new ArrayList<JSONObject>();
                while (rset.next()) {
                    jsonObj = new JSONObject();
                    jsonObj.put("seq", rset.getInt("seq"));
                    jsonObj.put("name", rset.getString("name"));
                    jsonObj.put("details", rset.getString("details"));
                    jsonObj.put("access_level", rset.getString("access_level"));
                    jsonObj.put("share_code", rset.getString("share_code"));
                    jsonObj.put("access_code", rset.getString("access_code"));
                    jsonObj.put("authority", rset.getString("authority"));
                    nanums.add(jsonObj);
                }
            }
            return nanums;
        } catch (Exception e) {
            log.error("* 프로그램 수행중 에러 발생");
            log.error(ExceptionUtil.getExceptionInfo(e));
            throw e;
        } finally {
            if (conn != null) try { conn.close(); } catch (Exception ignored) {}
        }
    }

    /**
     * 서비스 내의 모든 열린나눔박스를 조회
     * 
     * @param seqStore
     * @return JSONObject
     * @throws Exception
     */
    public List<JSONObject> getOpenNanums() throws Exception {
        
        List<JSONObject> nanums = null;
        LocalDBConnection conn = null;
        try {
            conn = new LocalDBConnection();
            StringBuffer querySb = new StringBuffer("SELECT seq, name, details, seq_store, share_code, access_code from nanum");
            querySb.append(" WHERE access_level ='O'");
            log.debug("LOCALDB-QUERY------------------------------------------------------------------------------");
            log.debug(querySb);
            ResultSet rset = conn.executeQuery(querySb.toString());

            JSONObject jsonObj = null;
            if (rset != null) {
                nanums = new ArrayList<JSONObject>();
                while (rset.next()) {
                    jsonObj = new JSONObject();
                    jsonObj.put("seq", rset.getInt("seq"));
                    jsonObj.put("name", rset.getString("name"));
                    jsonObj.put("details", rset.getString("details"));
                    jsonObj.put("seq_store", rset.getInt("seq_store"));
                    jsonObj.put("share_code", rset.getString("share_code"));
                    jsonObj.put("access_code", rset.getString("access_code"));
                    nanums.add(jsonObj);
                }
            }
            return nanums;
        } catch (Exception e) {
            log.error("* 프로그램 수행중 에러 발생");
            log.error(ExceptionUtil.getExceptionInfo(e));
            throw e;
        } finally {
            if (conn != null) try { conn.close(); } catch (Exception ignored) {}
        }
    }
    
    /**
     * 사용중인 공유코드인지 여부
     * 
     * @param sharecode
     * @return
     * @throws Exception
     */
    public boolean usedShareCode(String sharecode) throws Exception {

        LocalDBConnection conn = null;
        try {
            conn = new LocalDBConnection();
            StringBuffer querySb = new StringBuffer("SELECT seq from nanum");
            querySb.append(" WHERE share_code ='" + sharecode + "'");
            log.debug("LOCALDB-QUERY------------------------------------------------------------------------------");
            log.debug(querySb);
            ResultSet rset = conn.executeQuery(querySb.toString());

            JSONObject jsonObj = null;
            if (rset != null && rset.next()) {
                return true;
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
