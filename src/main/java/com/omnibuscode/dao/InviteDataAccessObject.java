package com.omnibuscode.dao;

import java.net.URLDecoder;
import java.net.URLEncoder;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.json.simple.JSONObject;

import com.omnibuscode.base.LocalDBConnection;
import com.omnibuscode.util.JinieboxUtil;
import com.omnibuscode.utils.ExceptionUtil;

public class InviteDataAccessObject extends CommonDataAccessObject {

    /**
     * 서버 환경 변경시 log4j.properties 설정 정보도 함께 변경 필요
     */
//    private Log log = LogFactory.getLog(InviteDataAccessObject.class);
    private Logger log = LogManager.getLogger(InviteDataAccessObject.class);

    public InviteDataAccessObject() {
        ;
    }
    
    /**
     * 이미 등록한 값인지 확인
     * @param code
     * @return
     * @throws Exception
     */
    public boolean existCode(String inviteCode) throws Exception {

        boolean result = false;

        String selQry = "SELECT seq FROM invite WHERE BINARY invite_code='" + inviteCode + "'";
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
     * 초대 정보가 있는지 확인
     * @param code
     * @return
     * @throws Exception
     */
    public boolean existInvite(String seqOwner, String seqUser, String juid, String typeObject, String seqObject) throws Exception {

        boolean result = false;

        StringBuffer querySb = new StringBuffer("SELECT * FROM invite");
        querySb.append(" WHERE seq_owner = " + seqOwner 
                + " AND seq_user = " + seqUser 
                + " AND juid = '" + juid + "'"
                + " AND type_object = '" + typeObject + "'" 
                + " AND seq_object = " + seqObject);
        log.debug("LOCALDB-QUERY------------------------------------------------------------------------------");
        log.debug(querySb);

        LocalDBConnection conn = null;
        try {
            conn = new LocalDBConnection();
            ResultSet rset = conn.executeQuery(querySb.toString());
            result = this.getResultSetSize(rset) > 0;
        } catch (Exception e) {
            log.error(ExceptionUtil.getExceptionInfo(e));
        } finally {
            if (conn != null) try { conn.close(); } catch (Exception ignored) {}
        }

        return result;
    }
    
    /**
     * 초대 정보를 json object 로 반환
     * @param code
     * @return
     * @throws Exception
     */
    public JSONObject getInvite(String inviteCode) throws Exception {
        LocalDBConnection conn = null;
        try {
            conn = new LocalDBConnection();
            StringBuffer querySb = new StringBuffer("SELECT seq, seq_user, juid, authority, expiry_date, seq_owner, type_object, seq_object, insert_time from invite");
            querySb.append(" WHERE BINARY invite_code = '" + inviteCode + "'");
            log.debug("LOCALDB-QUERY------------------------------------------------------------------------------");
            log.debug(querySb);
            ResultSet rset = conn.executeQuery(querySb.toString());

            if (rset != null) {
                JSONObject jsonObj = null;
                while(rset.next()) {
                    jsonObj = new JSONObject();
                    jsonObj.put("seq", rset.getInt("seq"));
                    jsonObj.put("seq_user", rset.getInt("seq_user"));
                    jsonObj.put("juid", rset.getString("juid"));
                    jsonObj.put("authority", rset.getString("authority"));
                    jsonObj.put("expiry_date", rset.getInt("expiry_date"));
                    jsonObj.put("seq_owner", rset.getInt("seq_owner"));
                    jsonObj.put("type_object", rset.getString("type_object"));
                    jsonObj.put("seq_object", rset.getInt("seq_object"));
                    jsonObj.put("insert_time", rset.getString("insert_time"));
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
     * 초대 정보를 json object 로 반환
     * @param code
     * @return
     * @throws Exception
     */
    public JSONObject getInvite(String seqOwner, String typeObject, String seqObject) throws Exception {
        LocalDBConnection conn = null;
        try {
            conn = new LocalDBConnection();
            StringBuffer querySb = new StringBuffer("SELECT seq, seq_user, juid, authority, expiry_date, seq_owner, type_object, seq_object, insert_time");
            querySb.append(" FROM invite");
            querySb.append(" WHERE seq_owner = " + seqOwner);
            if (typeObject != null && seqObject != null) {
                querySb.append(" AND type_object = '" + typeObject + "'");
                querySb.append(" AND seq_object = " + seqObject);
            }
            log.debug("LOCALDB-QUERY------------------------------------------------------------------------------");
            log.debug(querySb);
            ResultSet rset = conn.executeQuery(querySb.toString());

            if (rset != null) {
                JSONObject jsonObj = null;
                while(rset.next()) {
                    jsonObj = new JSONObject();
                    jsonObj.put("seq", rset.getInt("seq"));
                    jsonObj.put("seq_user", rset.getInt("seq_user"));
                    jsonObj.put("juid", rset.getString("juid"));
                    jsonObj.put("authority", rset.getString("authority"));
                    jsonObj.put("expiry_date", rset.getInt("expiry_date"));
                    jsonObj.put("seq_owner", rset.getInt("seq_owner"));
                    jsonObj.put("type_object", rset.getString("type_object"));
                    jsonObj.put("seq_object", rset.getInt("seq_object"));
                    jsonObj.put("insert_time", rset.getString("insert_time"));
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
     * 초대 정보를 JSON Map 으로 반환 (key is seq)
     * 
     * @param seqOwner
     * @param typeObject
     * @param seqObject
     * @return
     * @throws Exception
     */
    public JSONObject getInvitesJson(String seqOwner, String typeObject, String seqObject) throws Exception {
        LocalDBConnection conn = null;
        try {
            conn = new LocalDBConnection();
            StringBuffer querySb = new StringBuffer("SELECT seq, seq_user, juid, authority, type_object, seq_object, invite_code, invite_url, expiry_date, insert_time");
            querySb.append(" FROM invite");
            querySb.append(" WHERE seq_owner = " + seqOwner);
            if (typeObject != null && seqObject != null) {
                querySb.append(" AND type_object = '" + typeObject + "'");
                querySb.append(" AND seq_object = " + seqObject);
            }
            log.debug("LOCALDB-QUERY------------------------------------------------------------------------------");
            log.debug(querySb);
            ResultSet rset = conn.executeQuery(querySb.toString());

            if (rset != null) {
                JSONObject invites = new JSONObject();
                JSONObject jsonObj = null;
                while(rset.next()) {
                    jsonObj = new JSONObject();
                    jsonObj.put("seq", rset.getInt("seq"));
                    jsonObj.put("seq_user", rset.getInt("seq_user"));
                    jsonObj.put("juid", rset.getString("juid"));
                    jsonObj.put("authority", rset.getString("authority"));
                    jsonObj.put("type_object", rset.getString("type_object"));
                    jsonObj.put("seq_object", rset.getInt("seq_object"));
                    jsonObj.put("invite_code", URLDecoder.decode(rset.getString("invite_code"), "UTF-8"));
                    jsonObj.put("invite_url", URLDecoder.decode(rset.getString("invite_url"), "UTF-8"));
                    jsonObj.put("expiry_date", rset.getInt("expiry_date"));
                    jsonObj.put("insert_time", rset.getLong("insert_time"));
                    invites.put(rset.getInt("seq"), jsonObj);
                }
                return invites;
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
    
//    /**
//     * 초대 정보를 반환 (seqStore 와 seqBox 는 and 조회함)
//     * @param seqOwner
//     * @param seqBox
//     * @param seqStore
//     * @return
//     * @throws Exception
//     */
//    public List<JSONObject> list(String seqOwner, String seqBox, String seqStore) throws Exception {
//        
//        try {
//            conn = new LocalDBConnection();
//            StringBuffer querySb = new StringBuffer("SELECT i.seq seq, i.seq_user seq_user, u.juid mem_id, i.auth_right auth_right, i.seq_store seq_store");
//            querySb.append(", i.seq_box seq_box, i.invite_code invite_code, i.invite_url invite_url, i.expiry_date expiry_date, i.insert_time insert_time");
//            querySb.append(" FROM invite i, user u");
//            querySb.append(" WHERE i.seq_owner = " + seqOwner);
//            if (seqStore != null)
//                querySb.append(" AND i.seq_store = " + seqStore);
//            if (seqBox != null)
//                querySb.append(" AND i.seq_box = " + seqBox);
//            log.debug("LOCALDB-QUERY------------------------------------------------------------------------------");
//            log.debug(querySb);
//            ResultSet rset = conn.executeQuery(querySb.toString());
//
//            if (rset != null) {
//                List<JSONObject> invites = new ArrayList<JSONObject>();
//                JSONObject jsonObj = null;
//                while(rset.next()) {
//                    jsonObj = new JSONObject();
//                    jsonObj.put("seq", rset.getInt("seq"));
//                    jsonObj.put("seq_user", rset.getInt("seq_user"));
//                    jsonObj.put("mem_id", rset.getString("mem_id"));
//                    jsonObj.put("auth_right", rset.getString("auth_right"));
//                    jsonObj.put("seq_store", rset.getInt("seq_store"));
//                    jsonObj.put("seq_box", rset.getInt("seq_box"));
//                    jsonObj.put("invite_code", rset.getString("invite_code"));
//                    jsonObj.put("invite_url", rset.getString("invite_url"));
//                    jsonObj.put("expiry_date", rset.getInt("expiry_date"));
//                    jsonObj.put("insert_time", rset.getLong("insert_time"));
//                    invites.add(jsonObj);
//                }
//                return invites;
//            }
//            return null;
//        } catch (Exception e) {
//            log.error("* 프로그램 수행중 에러 발생");
//            log.error(ExceptionUtil.getExceptionInfo(e));
//            log.error(e.getMessage());
//            log.debug(ExceptionUtil.getExceptionInfo(e.getStackTrace()));
//            throw e;
//        } finally {
//            conn.close();
//        }
//    }
    
//    /**
//     * 새로운 초대 추가
//     * @param seqOwner
//     * @param seqUser
//     * @param authority
//     * @param seqStore
//     * @param seqBox
//     * @param inviteCode
//     * @param inviteUrl
//     * @param expiryDate
//     * @return
//     * @throws Exception
//     */
//    public boolean insert(String seqOwner, String seqUser, String juid, String seqStore, String seqBox, String authority, String inviteCode, String inviteUrl, String expiryDate) throws Exception {
//
//        StringBuffer querySb = new StringBuffer();
//        querySb.append("INSERT INTO invite (");
//        querySb.append("seq_owner,");
//        if (seqUser != null)
//        	querySb.append("seq_user,");
//        querySb.append("juid,");
//        querySb.append("authority,");
//        if (seqStore != null)
//            querySb.append("seq_store,");
//        if (seqBox != null)
//            querySb.append("seq_box,");
//        querySb.append("invite_code,");
//        querySb.append("invite_url,");
//        querySb.append("expiry_date,");
//        querySb.append("insert_time");
//        querySb.append(") VALUES (");
//        querySb.append(seqOwner + ", ");
//        if (seqUser != null)
//        	querySb.append(seqUser + ", ");
//        querySb.append("'" + juid + "', ");
//        querySb.append("'" + authority + "', ");
//        if (seqStore != null)
//            querySb.append(seqStore + ", ");
//        if (seqBox != null)
//            querySb.append(seqBox + ", ");
//        querySb.append("'" + URLEncoder.encode(inviteCode, "UTF-8") + "', ");
//        querySb.append("'" + URLEncoder.encode(inviteUrl, "UTF-8") + "', ");
//        querySb.append(expiryDate + ", ");
//        querySb.append(System.currentTimeMillis());
//        querySb.append(")");
//        log.debug("LOCALDB-QUERY------------------------------------------------------------------------------");
//        log.debug(querySb);
//
//        LocalDBConnection conn = null;
//        try {
//            conn = new LocalDBConnection();
//            conn.txOpen();
//            conn.txExecuteUpdate(querySb.toString());
//            conn.txCommit();
//        } catch (SQLException sqle) {
//            log.error("* 아이고!! ㅜ.ㅜ 데이터베이스 업데이트 에러 발생");
//            log.error(sqle.getMessage());
//            log.debug(ExceptionUtil.getExceptionInfo(sqle.getStackTrace()));
//            sqllog.error(ExceptionUtil.getExceptionInfo(e));
//            throw sqle;
//        } catch (Exception e) {
//            log.error("* 데이터베이스 업데이트 에러 발생");
//            log.error(e.getMessage());
//            log.debug(ExceptionUtil.getExceptionInfo(e.getStackTrace()));
//            log.error(ExceptionUtil.getExceptionInfo(e));
//            conn.txRollBack();
//            throw e;
//        } finally {
//            conn.close();            
//        }
//
//        return true;
//    }
    
    /**
     * 새로운 초대 추가
     * @param seqOwner
     * @param seqUser
     * @param juid
     * @param typeObject
     * @param seqObject
     * @param authority
     * @param inviteCode
     * @param inviteUrl
     * @param expiryDate
     * @return
     * @throws Exception
     */
    public boolean insertInvite(String seqOwner, String seqUser, String juid, String typeObject, String seqObject, String authority, String inviteCode, String inviteUrl, String expiryDate) throws Exception {

        StringBuffer querySb = new StringBuffer();
        querySb.append("INSERT INTO invite (");
        querySb.append("seq_owner,");
        if (seqUser != null)
            querySb.append("seq_user,");
        querySb.append("juid,");
        querySb.append("authority,");
        querySb.append("type_object,");
        querySb.append("seq_object,");
        querySb.append("invite_code,");
        querySb.append("invite_url,");
        querySb.append("expiry_date,");
        querySb.append("insert_time");
        querySb.append(") VALUES (");
        querySb.append(seqOwner + ", ");
        if (seqUser != null)
            querySb.append(seqUser + ", ");
        querySb.append("'" + juid + "', ");
        querySb.append("'" + authority + "', ");
        querySb.append("'" + typeObject + "', ");
        querySb.append(seqObject + ", ");
        querySb.append("'" + URLEncoder.encode(inviteCode, "UTF-8") + "', ");
        querySb.append("'" + URLEncoder.encode(inviteUrl, "UTF-8") + "', ");
        querySb.append(expiryDate + ", ");
        querySb.append(System.currentTimeMillis());
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
            if (conn != null) try { conn.close(); } catch (Exception ignored) {}
        }

        return true;
    }
    
    /**
     * 초대 수정
     * @param seqOwner
     * @param seqUser
     * @param juid
     * @param typeObject
     * @param seqObject
     * @param authority
     * @param inviteCode
     * @param inviteUrl
     * @param expiryDate
     * @throws Exception
     */
    public void updateInvite(String seqOwner, String seqUser, String juid, String typeObject, String seqObject, String authority, String inviteCode, String inviteUrl, String expiryDate) throws Exception {

        StringBuffer querySb = new StringBuffer();
        querySb.append("UPDATE invite set");
        querySb.append(" juid ='" + juid + "',");
        querySb.append(" authority ='" + authority + "',");
        querySb.append(" invite_code = '" + URLEncoder.encode(inviteCode, "UTF-8") + "',");
        querySb.append(" invite_url = '" + URLEncoder.encode(inviteUrl, "UTF-8") + "',");
        querySb.append(" expiry_date = " + expiryDate);
        querySb.append(" WHERE");
        querySb.append(" seq_owner = " + seqOwner);
        querySb.append(" AND seq_user = " + seqUser);
        querySb.append(" AND type_object = '" + typeObject + "'");
        querySb.append(" AND seq_object = " + seqObject);

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
    
//    /**
//     * Box 초대 수정
//     * @param seqOwner
//     * @param seqUser
//     * @param seqBox
//     * @param authority
//     * @param inviteCode
//     * @param inviteUrl
//     * @param expiryDate
//     * @throws Exception
//     */
//    public void updateBoxInvite(String seqOwner, String seqUser, String juid, String seqBox, String authority, String inviteCode, String inviteUrl, String expiryDate) throws Exception {
//
//        StringBuffer querySb = new StringBuffer();
//        querySb.append("UPDATE invite set");
//        querySb.append(" juid ='" + juid + "',");
//        querySb.append(" authority ='" + authority + "',");
//        querySb.append(" invite_code = '" + URLEncoder.encode(inviteCode, "UTF-8") + "',");
//        querySb.append(" invite_url = '" + URLEncoder.encode(inviteUrl, "UTF-8") + "',");
//        querySb.append(" expiry_date = " + expiryDate);
//        querySb.append(" WHERE");
//        querySb.append(" seq_owner = " + seqOwner);
//        querySb.append(" AND seq_user = " + seqUser);
//        querySb.append(" AND seq_box = " + seqBox);
//
//        String query = querySb.toString();
//        log.debug("LOCAL-QUERY------------------------------------------------------------------------------");
//        log.debug(query);
//
//        LocalDBConnection conn = null;
//        try {
//            conn = new LocalDBConnection();
//            conn.txOpen();
//            conn.txExecuteUpdate(query);
//            conn.txCommit();
//        } catch (SQLException sqle) {
//            log.error("* 아이고!! ㅜ.ㅜ 데이터베이스 업데이트 에러 발생");
//            log.error(sqle.getMessage());
//            log.debug(ExceptionUtil.getExceptionInfo(sqle.getStackTrace()));
//            sqllog.error(ExceptionUtil.getExceptionInfo(e));
//            throw sqle;
//        } catch (Exception e) {
//            log.error("* 아이고!! ㅜ.ㅜ 데이터베이스 업데이트 에러 발생");
//            log.error(e.getMessage());
//            log.debug(ExceptionUtil.getExceptionInfo(e.getStackTrace()));
//            log.error(ExceptionUtil.getExceptionInfo(e));
//            conn.txRollBack();
//            throw e;
//        } finally {
//            conn.close();
//        }
//    }
//    
//    /**
//     * Store 초대 수정
//     * @param seqOwner
//     * @param seqUser
//     * @param seqStore
//     * @param authority
//     * @param inviteCode
//     * @param inviteUrl
//     * @param expiryDate
//     * @throws Exception
//     */
//    public void updateStoreInvite(String seqOwner, String seqUser, String juid, String seqStore, String authority, String inviteCode, String inviteUrl, String expiryDate) throws Exception {
//
//        StringBuffer querySb = new StringBuffer();
//        querySb.append("UPDATE invite set");
//        querySb.append(" juid ='" + juid + "',");
//        querySb.append(" authority ='" + authority + "',");
//        querySb.append(" invite_code = '" + URLEncoder.encode(inviteCode, "UTF-8") + "',");
//        querySb.append(" invite_url = '" + URLEncoder.encode(inviteUrl, "UTF-8") + "',");
//        querySb.append(" expiry_date = " + expiryDate);
//        querySb.append(" WHERE");
//        querySb.append(" seq_owner = " + seqOwner);
//        querySb.append(" AND seq_user = " + seqUser);
//        querySb.append(" AND seq_store = " + seqStore);
//
//        String query = querySb.toString();
//        log.debug("LOCAL-QUERY------------------------------------------------------------------------------");
//        log.debug(query);
//
//        LocalDBConnection conn = null;
//        try {
//            conn = new LocalDBConnection();
//            conn.txOpen();
//            conn.txExecuteUpdate(query);
//            conn.txCommit();
//        } catch (SQLException sqle) {
//            log.error("* 아이고!! ㅜ.ㅜ 데이터베이스 업데이트 에러 발생");
//            log.error(sqle.getMessage());
//            log.debug(ExceptionUtil.getExceptionInfo(sqle.getStackTrace()));
//            sqllog.error(ExceptionUtil.getExceptionInfo(e));
//            throw sqle;
//        } catch (Exception e) {
//            log.error("* 아이고!! ㅜ.ㅜ 데이터베이스 업데이트 에러 발생");
//            log.error(e.getMessage());
//            log.debug(ExceptionUtil.getExceptionInfo(e.getStackTrace()));
//            log.error(ExceptionUtil.getExceptionInfo(e));
//            conn.txRollBack();
//            throw e;
//        } finally {
//            conn.close();
//        }
//    }
    
    /**
     * row delete
     * @param seq
     * @return
     * @throws Exception
     */
    public boolean delete(String seq) throws Exception {

        StringBuffer querySb = new StringBuffer();
        querySb.append("DELETE FROM invite");
        querySb.append(" WHERE seq=" + seq);
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
