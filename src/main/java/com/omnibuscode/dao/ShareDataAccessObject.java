package com.omnibuscode.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.json.simple.JSONObject;

import com.omnibuscode.base.LocalDBConnection;
import com.omnibuscode.utils.ExceptionUtil;
import com.omnibuscode.utils.NumberUtil;

public class ShareDataAccessObject extends CommonDataAccessObject {

    /**
     * 서버 환경 변경시 log4j.properties 설정 정보도 함께 변경 필요
     */
	private Logger log = LogManager.getLogger(ShareDataAccessObject.class);

    public ShareDataAccessObject() {;}
    
    /**
     * store, box, nanum 의 공유 정보 반환
     * @param typeObject
     * @param seqObject
     * @return
     * @throws Exception
     */
    public JSONObject list(String typeObject, String seqObject) throws Exception {
        
        if (typeObject == null) {
            throw new Exception("타입을 지정해야 합니다");
        }
        
        LocalDBConnection conn = null;
    	try {
			conn = new LocalDBConnection();
            StringBuffer querySb = new StringBuffer(
                    "SELECT s.seq seq, s.seq_object seq_object, s.type_object type, s.seq_user seq_user, u.juid juid, s.authority authority");
            querySb.append(" FROM share s, user u");
            querySb.append(" WHERE s.seq_user=u.seq");
            querySb.append(" AND type_object='" + typeObject + "'");
            querySb.append(" AND seq_object=" + seqObject);
            log.debug("LOCALDB-QUERY------------------------------------------------------------------------------");
            log.debug(querySb);
            ResultSet rset = conn.executeQuery(querySb.toString());

			if (rset != null) {
				JSONObject listShared = new JSONObject();
				JSONObject jsonObj = null;
				while (rset.next()) {
					jsonObj = new JSONObject();
					jsonObj.put("seq", rset.getString("seq"));
					jsonObj.put("seq_object", rset.getString("seq_object"));
					jsonObj.put("type", rset.getString("type"));
					jsonObj.put("seq_user", rset.getString("seq_user"));
					jsonObj.put("juid", rset.getString("juid"));
					jsonObj.put("authority", rset.getString("authority"));
					listShared.put(rset.getString("seq"), jsonObj);
				}
				return listShared;
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
     * 공유 받은 목록을 반환
	 * @param seqUser
	 * @return
	 * @throws Exception
	 */
	public List<JSONObject> list(String seqUser) throws Exception {
	    LocalDBConnection conn = null;
		try {
            conn = new LocalDBConnection();
            StringBuffer querySb = new StringBuffer("SELECT seq_object, type_object, seq_user, authority");
            querySb.append(" FROM share");
            querySb.append(" WHERE seq_user=" + seqUser);
            log.debug("LOCALDB-QUERY------------------------------------------------------------------------------");
            log.debug(querySb);
            ResultSet rset = conn.executeQuery(querySb.toString());

            if (rset != null) {
                List<JSONObject> listShared = new ArrayList<JSONObject>();
                JSONObject jsonObj = null;
                while (rset.next()) {
                    jsonObj = new JSONObject();
                    jsonObj.put("seq_object", rset.getInt("seq_object"));
                    jsonObj.put("type_object", rset.getString("type_object"));
                    jsonObj.put("seq_user", rset.getInt("seq_user"));
                    jsonObj.put("authority", rset.getString("authority"));
                    listShared.add(jsonObj);
                }
                return listShared;
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
     * 저장소 공유 정보 추가 <br/>
     * authority : 'R' - 읽기전용(기본값), 'M' - 등록/수정/삭제 가능
     * @param seqUser
     * @param typeObject
     * @param seqObject
     * @param authority
     * @return
     * @throws Exception
     */
    public boolean add(String seqUser, String typeObject, String seqObject, String authority) throws Exception {

        StringBuffer querySb = new StringBuffer();
        querySb.append("INSERT INTO share (");
        querySb.append("seq_user,");
        querySb.append("type_object,");
        querySb.append("seq_object,");
        querySb.append("authority");
        querySb.append(") values (");
        querySb.append(seqUser + ", ");
        querySb.append("'" + typeObject + "', ");
        querySb.append(seqObject + ", ");
        querySb.append("'" + authority + "'");
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
     * 저장소 공유 정보 등록 여부
     * @param seqUser
     * @param typeObject
     * @param seqObject
     * @return
     * @throws Exception
     */
    public String getAuthority(String seqUser, String typeObject, String seqObject) throws Exception {
        LocalDBConnection conn = null;
        try {
            conn = new LocalDBConnection();
            StringBuffer querySb = new StringBuffer("SELECT authority from share");
            querySb.append(" WHERE seq_user=" + seqUser);
            querySb.append(" AND type_object='" + typeObject + "' AND seq_object=" + seqObject);

            log.debug("LOCALDB-QUERY------------------------------------------------------------------------------");
            log.debug(querySb);
            ResultSet rset = conn.executeQuery(querySb.toString());

            if (rset != null) {
                String valAuth = null;
                if (rset.next()) {
                    valAuth = rset.getString("authority");
                    return valAuth;
                }
            }
            return null;
            
        } catch (Exception e) {
            log.error("* 프로그램 수행중 에러 발생");
            log.debug(ExceptionUtil.getExceptionInfo(e));
            throw e;
        } finally {
            conn.close();
        }
    }
    
    /**
     * 특정 객체(저장소, 보관함, 나눔함)에 대한 공유 정보 삭제
     * @param typeObject
     * @param seqObject
     * @return
     * @throws Exception
     */
    public boolean delete(String typeObject, String seqObject) throws Exception {

        boolean chkSeqObj = NumberUtil.isNumber(seqObject);

        if (typeObject == null || !chkSeqObj) {
            // 모든 공유정보를 삭제하게 되므로 실패해야 한다.
            return false;
        }

        StringBuffer querySb = new StringBuffer();
        querySb.append("DELETE from share");
        querySb.append(" WHERE 1=1");
        if (chkSeqObj) {
            querySb.append(" AND type_object='" + typeObject + "'");
            querySb.append(" AND seq_object=" + seqObject);
        }

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
        
        return true;
    }
    
    /**
     * 사용자의 공유정보에 나눔 등록 여부
     * 
     * @param seqUser
     * @param typeObject
     * @param seqObject
     * @return
     * @throws Exception
     */
    public boolean hasAlready(String seqUser, String typeObject, String seqObject) throws Exception {
        LocalDBConnection conn = null;
        try {
            conn = new LocalDBConnection();
            StringBuffer querySb = new StringBuffer("SELECT seq_object, type_object, seq_user, authority FROM share");
            querySb.append(" WHERE seq_user=" + seqUser);
            querySb.append(" AND type_object = '" + typeObject + "'");
            querySb.append(" AND seq_object = " + seqObject);
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
            conn.close();
        }
    }
}
