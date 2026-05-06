package com.omnibuscode.dao;

import java.net.URLEncoder;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
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
public class UserDataAccessObject extends CommonDataAccessObject {

    /**
     * 서버 환경 변경시 log4j.properties 설정 정보도 함께 변경 필요
     */
//    private Log log = LogFactory.getLog(UserDataAccessObject.class);
	private Logger log = LogManager.getLogger(UserDataAccessObject.class);

    public UserDataAccessObject() {
        ;
    }

    /**
     * 이메일 인증된 사용자임을 설정
     * @param juid
     * @throws Exception
     */
    public void verifyUser(String juid) throws Exception {
        StringBuffer querySb = new StringBuffer();
        querySb.append("UPDATE user set");
        querySb.append(" verified = 1");
        querySb.append(" WHERE");
        querySb.append(" juid = '" + juid + "'");

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
     * 자동완성용 아이디 조회
     * @param inputTxt
     * @param exceptId
     * @return
     * @throws Exception
     */
    public JSONObject predictiveJuids(String inputTxt, String exceptId) throws Exception {

        String selQry = "SELECT juid FROM user WHERE juid LIKE '" + inputTxt + "%'";
        log.debug("LOCALDB-QUERY------------------------------------------------------------------------------");
        log.debug(selQry);
        
        LocalDBConnection conn = null;
        try {
            conn = new LocalDBConnection();
            ResultSet rset = conn.executeQuery(selQry);
            JSONObject jsonObj = null;
            if (rset != null) {
                jsonObj = new JSONObject();
                List<String> idList = new ArrayList<String>();
                String juid = null;
                while (rset.next()) {
                    juid = rset.getString("juid");
                    if (!juid.equals(exceptId)) {
                        idList.add(rset.getString("juid"));
                    }
                }
                jsonObj.put("juids", idList);
            }
            return jsonObj;
        } catch (Exception e) {
            log.error("* 프로그램 수행중 에러 발생");
            log.error(ExceptionUtil.getExceptionInfo(e));
            throw e;
        } finally {
            if (conn != null) try { conn.close(); } catch (Exception ignored) {}
        }
    }
    
    /**
     * 이미 등록된 메일인지 확인
     * @param email
     * @return
     * @throws Exception
     */
    public boolean existJuid(String email) throws Exception {

        boolean result = false;

        String selQry = "SELECT * FROM user WHERE juid='" + email + "'";
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
     * 이미 등록된 회원 아이디인지 확인 (by bixby user id)
     * @param buid
     * @throws ClassNotFoundException
     * @throws IllegalAccessException
     * @throws InstantiationException
     * @throws SQLException
     */
    public boolean existBuid(String buid) throws Exception {

        boolean result = false;

        String selQry = "SELECT * FROM user WHERE buid='" + buid + "'";
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
    
//    /**
//     * 이미 등록된 회원 아이디인지 확인 (by jinie user id)
//     * @param buid
//     * @throws ClassNotFoundException
//     * @throws IllegalAccessException
//     * @throws InstantiationException
//     * @throws SQLException
//     */
//    public boolean existJuid(String juid) throws Exception {
//
//        boolean result = false;
//
//        String selQry = "SELECT * FROM user WHERE juid='" + juid + "'";
//        log.debug("LOCALDB-QUERY------------------------------------------------------------------------------");
//        log.debug(selQry);
//        try {
//            conn = new LocalDBConnection();
//            ResultSet rset = conn.executeQuery(selQry);
//            result = this.getResultSetSize(rset) > 0;
//        } catch (Exception e) {
//            log.error(ExceptionUtil.getExceptionInfo(e));
//        } finally {
//            conn.close();
//        }
//        
//        return result;
//    }

    /**
     * 이미 등록된 회원 아이디인지 확인 (by kakao user id)
     * @param kakaoid
     * @return
     * @throws Exception
     */
    public boolean existKakaoid(String kakaoid) throws Exception {

        boolean result = false;

        String selQry = "SELECT * FROM user WHERE kakaoid='" + kakaoid + "'";
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
     * 사용자 등록
     * @param juid jiniebox user id
     * @param jupw jiniebox user pw
     * @param buid bixby user id
     * @param juname [옵션]사용자명
     * @param seqDefStore [옵션]기본저장소
     * @return 저장 성공 여부
     * @throws Exception
     */
    public boolean regist(String juid, String jupw, String juname, String buid) throws Exception {

        StringBuffer querySb = new StringBuffer();
        querySb.append("INSERT INTO user (");
        querySb.append("juid,");
        querySb.append("jupw,");
        if (!JinieboxUtil.isEmpty(buid))
            querySb.append("buid,");
        if (!JinieboxUtil.isEmpty(juname))
            querySb.append("juname,");
        querySb.append("insert_time");
        querySb.append(") values ('");
        querySb.append(juid + "', '");
        querySb.append(jupw + "', ");
        if (!JinieboxUtil.isEmpty(buid))
            querySb.append("'" + buid + "', ");
        if (!JinieboxUtil.isEmpty(juname))
            querySb.append("'" + juname + "', ");
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
    
//    /**
//     * 사용자 파일 저장 위치
//     * 
//     * @param seqUser
//     * @return
//     * @throws Exception
//     */
//    public int getStorageType(String seqUser) throws Exception {
//        LocalDBConnection conn = null;
//        try {
//            conn = new LocalDBConnection();
//            StringBuffer querySb = new StringsBuffer("SELECT storage_type from user");
//            querySb.append(" WHERE seq = " + seqUser);
//            log.debug("LOCALDB-QUERY------------------------------------------------------------------------------");
//            log.debug(querySb);
//            ResultSet rset = conn.executeQuery(querySb.toString());
//
//            if (rset != null && this.getResultSetSize(rset) > 0) {
//                return rset.getInt("storage_type");
//            }
//            return 0;
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

    /**
     * verifycode 를 조회하여 juid 를 반환
     * @param verifycd
     * @return
     * @throws Exception
     */
    public String getJuidByVerifycode(String verifycd) throws Exception {
        LocalDBConnection conn = null;
        try {
            conn = new LocalDBConnection();
            StringBuffer querySb = new StringBuffer("SELECT juid from user");
            querySb.append(" WHERE verifycd ='" + verifycd + "'");
            log.debug("LOCALDB-QUERY------------------------------------------------------------------------------");
            log.debug(querySb);
            ResultSet rset = conn.executeQuery(querySb.toString());

            if (rset != null && this.getResultSetSize(rset) > 0) {
                return rset.getString("juid");
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
     * 기본 저장소의 seq를 반환한다
     * @param seqUser
     * @return
     * @throws Exception
     */
    public String getDefaultStoreSeq(String seqUser) throws Exception {
        LocalDBConnection conn = null;
        try {
            conn = new LocalDBConnection();
            StringBuffer querySb = new StringBuffer("SELECT seq_defstore from user");
            querySb.append(" WHERE seq ='" + seqUser + "'");
            log.debug("LOCALDB-QUERY------------------------------------------------------------------------------");
            log.debug(querySb);
            ResultSet rset = conn.executeQuery(querySb.toString());

            if (rset != null && this.getResultSetSize(rset) > 0) {
                return rset.getString("seq_defstore");
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
     * bixby user id로 사용자 seq 조회
     * @param buid
     * @return 조회 오류시 -1 반환
     * @throws Exception
     */
    public String getSeqByBuid(String buid) throws Exception {
        LocalDBConnection conn = null;
        try {
            conn = new LocalDBConnection();
            StringBuffer querySb = new StringBuffer("SELECT seq from user");
            querySb.append(" WHERE buid ='" + buid + "'");
            log.debug("LOCALDB-QUERY------------------------------------------------------------------------------");
            log.debug(querySb);
            ResultSet rset = conn.executeQuery(querySb.toString());

            if (rset != null && this.getResultSetSize(rset) > 0) {
                return rset.getString("seq");
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
     * user id와 pass로 저장소 seq 조회
     * @param juid
     * @param jupw
     * @return 조회 오류시 -1 반환
     * @throws Exception
     */
    public String getSeq(String juid, String jupw) throws Exception {
        LocalDBConnection conn = null;
        try {
            conn = new LocalDBConnection();
            StringBuffer querySb = new StringBuffer("SELECT seq from user");
            querySb.append(" WHERE juid = '" + juid + "' AND jupw = '" + jupw + "'");
            log.debug("LOCALDB-QUERY------------------------------------------------------------------------------");
            log.debug(querySb);
            ResultSet rset = conn.executeQuery(querySb.toString());

            if (rset != null && this.getResultSetSize(rset) > 0) {
                return rset.getString("seq");
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
     * 인증기간이 만료된 사용자 seq 목록을 반환
     * @param verifyed
     * @return
     * @throws Exception
     */
    public List getExpiredSeqList(String verifyed) throws Exception {
        LocalDBConnection conn = null;
        try {
            conn = new LocalDBConnection();
            StringBuffer querySb = new StringBuffer("SELECT seq from user");
            querySb.append(" WHERE verified != 1 AND verifyed < " + verifyed);
            log.debug("LOCALDB-QUERY------------------------------------------------------------------------------");
            log.debug(querySb);
            ResultSet rset = conn.executeQuery(querySb.toString());

            if (rset != null) {
                List seqList = new ArrayList();
                while(rset.next()) {
                    seqList.add(rset.getString("seq"));
                }
                return seqList;
            }
            return null;
        } catch (Exception e) {
            log.error("* 프로그램 수행중 에러 발생");
            log.error(ExceptionUtil.getExceptionInfo(e));
            log.error(e.getMessage());
            throw e;
        } finally {
            if (conn != null) try { conn.close(); } catch (Exception ignored) {}
        }
    }
    
    /**
     * jiniebox user id로 저장소 seq 조회
     * @param juid
     * @return
     * @throws Exception
     */
    public String getSeqByJuid(String juid) throws Exception {
        LocalDBConnection conn = null;
        try {
            conn = new LocalDBConnection();
            StringBuffer querySb = new StringBuffer("SELECT seq from user");
            querySb.append(" WHERE juid = '" + juid + "'");
            log.debug("LOCALDB-QUERY------------------------------------------------------------------------------");
            log.debug(querySb);
            ResultSet rset = conn.executeQuery(querySb.toString());

            if (rset != null && this.getResultSetSize(rset) > 0) {
                return rset.getString("seq");
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
//     * google user id로 저장소 seq 조회
//     * @param googleid
//     * @return
//     * @throws Exception
//     */
//    public String getSeqByGoogleid(String googleid) throws Exception {
//        LocalDBConnection conn = null;
//        try {
//            conn = new LocalDBConnection();
//            StringBuffer querySb = new StringBuffer("SELECT seq from user");
//            querySb.append(" WHERE googleid = '" + googleid + "'");
//            log.debug("LOCALDB-QUERY------------------------------------------------------------------------------");
//            log.debug(querySb);
//            ResultSet rset = conn.executeQuery(querySb.toString());
//
//            if (rset != null && this.getResultSetSize(rset) > 0) {
//                return rset.getString("seq");
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
    
    /**
     * jiniebox user id 로 사용자 정보 조회
     * @param juid
     * @return
     * @throws Exception
     */
    public JSONObject getUserByJuid(String juid) throws Exception {
        LocalDBConnection conn = null;
        try {
            conn = new LocalDBConnection();
            StringBuffer querySb = new StringBuffer("SELECT");
            querySb.append(" seq");
            querySb.append(", juid");
            querySb.append(", jupw");
            querySb.append(", buid");
            querySb.append(", juname");
            querySb.append(", insert_time");
            querySb.append(", seq_defstore");
            querySb.append(", verified");
            querySb.append(", is_partner");
            querySb.append(", first_visit");
            querySb.append(" FROM user");
            querySb.append(" WHERE juid ='" + juid + "'");
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
     * bixby user id 로 사용자 정보 조회
     * @param buid
     * @return
     * @throws Exception
     */
    public JSONObject getUserByBuid(String buid) throws Exception {
        LocalDBConnection conn = null;
        try {
            conn = new LocalDBConnection();
            StringBuffer querySb = new StringBuffer("SELECT");
            querySb.append(" seq");
            querySb.append(", juid");
            querySb.append(", jupw");
            querySb.append(", buid");
            querySb.append(", juname");
            querySb.append(", insert_time");
            querySb.append(", seq_defstore");
            querySb.append(", verified");
            querySb.append(", is_partner");
            querySb.append(", first_visit");
            querySb.append(" FROM user");
            querySb.append(" WHERE buid ='" + buid + "'");
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
     * kakao user id 로 사용자 정보 조회
     * @param kakaoid
     * @return
     * @throws Exception
     */
    public JSONObject getUserByKakaoid(String kakaoid) throws Exception {
        LocalDBConnection conn = null;
        try {
            conn = new LocalDBConnection();
            StringBuffer querySb = new StringBuffer("SELECT");
            querySb.append(" seq");
            querySb.append(", juid");
            querySb.append(", jupw");
            querySb.append(", buid");
            querySb.append(", juname");
            querySb.append(", insert_time");
            querySb.append(", seq_defstore");
            querySb.append(", verified");
            querySb.append(", is_partner");
            querySb.append(", first_visit");
            querySb.append(" FROM user");
            querySb.append(" WHERE kakaoid ='" + kakaoid + "'");
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
     * google user id 로 사용자 정보 조회
     * @param googleid
     * @return
     * @throws Exception
     */
    public JSONObject getUserByGoogleid(String googleid) throws Exception {
        LocalDBConnection conn = null;
        try {
            conn = new LocalDBConnection();
            StringBuffer querySb = new StringBuffer("SELECT");
            querySb.append(" seq");
            querySb.append(", juid");
            querySb.append(", jupw");
            querySb.append(", buid");
            querySb.append(", juname");
            querySb.append(", insert_time");
            querySb.append(", seq_defstore");
            querySb.append(", verified");
            querySb.append(", is_partner");
            querySb.append(", first_visit");
            querySb.append(" FROM user");
            querySb.append(" WHERE googleid ='" + googleid + "'");
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
     * id, pass 로 사용자 정보 조회
     * @param juid
     * @param jupw
     * @return
     * @throws Exception
     */
    public JSONObject getUser(String juid, String jupw) throws Exception {
        LocalDBConnection conn = null;
        try {
            conn = new LocalDBConnection();
            StringBuffer querySb = new StringBuffer("SELECT");
            querySb.append(" seq");
            querySb.append(", buid");
            querySb.append(", kakaoid");
            querySb.append(", googleid");
            querySb.append(", juname");
            querySb.append(", insert_time");
            querySb.append(", seq_defstore");
            querySb.append(", verified");
            querySb.append(", is_partner");
            querySb.append(", first_visit");
            querySb.append(" FROM user");
            querySb.append(" WHERE juid ='" + juid + "' AND jupw='" + jupw + "'");
            log.debug("LOCALDB-QUERY------------------------------------------------------------------------------");
            log.debug(querySb);
            ResultSet rset = conn.executeQuery(querySb.toString());

            if (rset != null && this.getResultSetSize(rset) > 0) {
                JSONObject rtnJson = new JSONObject();
                rtnJson.put("seq", rset.getInt("seq"));
                rtnJson.put("buid", rset.getString("buid"));
                rtnJson.put("kakaoid", rset.getString("kakaoid"));
                rtnJson.put("googleid", rset.getString("googleid"));
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
     * seq로 사용자 조회
     * @param seq
     * @return
     * @throws Exception
     */
    public JSONObject getUser(String seq) throws Exception {
        LocalDBConnection conn = null;
        try {
            conn = new LocalDBConnection();
            StringBuffer querySb = new StringBuffer("SELECT");
            querySb.append(" seq");
            querySb.append(", juid");
            querySb.append(", jupw");
            querySb.append(", buid");
            querySb.append(", juname");
            querySb.append(", insert_time");
            querySb.append(", seq_defstore");
            querySb.append(", verified");
            querySb.append(", is_partner");
            querySb.append(", first_visit");
            querySb.append(" FROM user");
            querySb.append(" WHERE seq = "+seq);
            log.debug("LOCALDB-QUERY------------------------------------------------------------------------------");
            log.debug(querySb);
            ResultSet rset = conn.executeQuery(querySb.toString());

            if (rset != null && this.getResultSetSize(rset) > 0) {
                JSONObject rtnJson = new JSONObject();
                rtnJson.put("seq", rset.getInt("seq"));
                rtnJson.put("buid", rset.getString("buid"));
                rtnJson.put("juname", rset.getString("juname"));
                rtnJson.put("insert_time", rset.getString("insert_time"));
                rtnJson.put("seq_defstore", rset.getString("seq_defstore"));
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
     * seq로 사용자 조회
     * @param seqList
     * @return
     * @throws Exception
     */
    public JSONObject getUsers(List seqList) throws Exception {
        LocalDBConnection conn = null;
        try {
            Iterator seqIter = seqList.iterator();
            StringBuffer seqsStr = new StringBuffer();
            while (seqIter.hasNext()) {
                seqsStr.append(seqIter.next() + ",");
            }
            
            conn = new LocalDBConnection();
            StringBuffer querySb = new StringBuffer("SELECT");
            querySb.append(" seq");
            querySb.append(", buid");
            querySb.append(", juid");
            querySb.append(", juname");
            querySb.append(", insert_time");
            querySb.append(", seq_defstore");
            querySb.append(", is_partner");
            querySb.append(", first_visit");
            querySb.append(" FROM user");
            querySb.append(" WHERE seq in (" + seqsStr.substring(0, seqsStr.length()-1) + ")");
            log.debug("LOCALDB-QUERY------------------------------------------------------------------------------");
            log.debug(querySb);
            ResultSet rset = conn.executeQuery(querySb.toString());

            if (rset != null) {
                JSONObject users = new JSONObject();
                JSONObject jsonObj = null;
                while(rset.next()) {
                    jsonObj = new JSONObject();
                    jsonObj.put("seq", rset.getInt("seq"));
                    jsonObj.put("buid", rset.getString("buid"));
                    jsonObj.put("juid", rset.getString("juid"));
                    jsonObj.put("juname", rset.getString("juname"));
                    jsonObj.put("insert_time", rset.getString("insert_time"));
                    jsonObj.put("seq_defstore", rset.getString("seq_defstore"));
                    jsonObj.put("is_partner", rset.getInt("is_partner"));
                    jsonObj.put("first_visit", rset.getInt("first_visit"));
                    users.put(rset.getInt("seq"), jsonObj);
                }
                return users;
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

    public void setVerifyCode(String juid, String verifyCode, String expiryDate) throws Exception {
		StringBuffer querySb = new StringBuffer();
		querySb.append("UPDATE user set");
		querySb.append(" verifycd = '" + verifyCode + "',");
		querySb.append(" verifyed = " + expiryDate);
		querySb.append(" WHERE");
		querySb.append(" juid = '" + juid + "'");

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
     * 구글 아이디 저장
     * @param seqUser
     * @param useFlag
     * @throws Exception
     */
    public void setGoogleid(String seqUser, String gmail) throws Exception {
        LocalDBConnection conn = null;
        try {
            conn = new LocalDBConnection();
            conn.txOpen();

            StringBuffer querySb = new StringBuffer();
            querySb.append("UPDATE user set");
            querySb.append(" googleid='" + gmail + "'");
            querySb.append(" where seq=" + seqUser);

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
            if (conn != null) try { conn.close(); } catch (Exception ignored) {}
        }
    }
    
    /**
     * 구글 아이디 조회
     * @param seqUser
     * @return gmail
     * @throws Exception
     */
    public String getGoogleid(String seqUser) throws Exception {
        LocalDBConnection conn = null;
        try {
            conn = new LocalDBConnection();
            StringBuffer querySb = new StringBuffer("SELECT googleid from user");
            querySb.append(" WHERE seq = " + seqUser);
            log.debug("LOCALDB-QUERY------------------------------------------------------------------------------");
            log.debug(querySb);
            ResultSet rset = conn.executeQuery(querySb.toString());

            if (rset != null && this.getResultSetSize(rset) > 0) {
                return rset.getString("googleid");
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
     * 카카오 아이디를 저장한다
     * @param seqUser
     * @param seqStore
     * @throws Exception
     */
    public void setKakaoid(String seqUser, String kakaoid) throws Exception {
        LocalDBConnection conn = null;
        try {
            conn = new LocalDBConnection();
            conn.txOpen();

            StringBuffer querySb = new StringBuffer();
            querySb.append("UPDATE user set");
            querySb.append(" kakaoid='" + kakaoid + "'");
            querySb.append(" where seq=" + seqUser);

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
            if (conn != null) try { conn.close(); } catch (Exception ignored) {}
        }
    }
    
    /**
     * 빅스비 아이디를 저장한다
     * @param seqUser
     * @param seqStore
     * @throws Exception
     */
    public void setBixbyUserid(String seqUser, String buid) throws Exception {
        LocalDBConnection conn = null;
        try {
            conn = new LocalDBConnection();
            conn.txOpen();

            StringBuffer querySb = new StringBuffer();
            querySb.append("UPDATE user set");
            querySb.append(" buid='" + buid + "'");
            querySb.append(" where seq=" + seqUser);

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
            if (conn != null) try { conn.close(); } catch (Exception ignored) {}
        }
    }

    /**
     * 기본 저장소의 seq를 설정한다
     * @param seqUser
     * @param seqStore
     * @throws Exception
     */
    public void setDefaultStoreSeq(String seqUser, String seqStore) throws Exception {
        LocalDBConnection conn = null;
        try {
            conn = new LocalDBConnection();
            conn.txOpen();

            StringBuffer querySb = new StringBuffer();
            querySb.append("UPDATE user set");
            querySb.append(" seq_defstore=" + seqStore);
            querySb.append(" where seq=" + seqUser);

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
            if (conn != null) try { conn.close(); } catch (Exception ignored) {}
        }
    }
    
//    /**
//     * 구글 드라이브 사용 여부 설정
//     * @param seqUser
//     * @param storageType
//     * @throws Exception
//     */
//    public void setGoogleDriveUse(String seqUser, String storageType) throws Exception {
//        LocalDBConnection conn = null;
//        try {
//            conn = new LocalDBConnection();
//            conn.txOpen();
//
//            StringBuffer querySb = new StringBuffer();
//            querySb.append("UPDATE user set");
//            querySb.append(" storage_type=" + storageType);
//            querySb.append(" where seq=" + seqUser);
//
//            String query = querySb.toString();
//            log.debug("LOCAL-QUERY------------------------------------------------------------------------------");
//            log.debug(query);
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
     * 첫방문 무효로 설정
     * @param seqUser
     * @throws Exception
     */
    public void setVisited(String seqUser) throws Exception {
        LocalDBConnection conn = null;
        try {
            conn = new LocalDBConnection();
            conn.txOpen();

            StringBuffer querySb = new StringBuffer();
            querySb.append("UPDATE user set");
            querySb.append(" first_visit=0");
            querySb.append(" where seq=" + seqUser);

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
            if (conn != null) try { conn.close(); } catch (Exception ignored) {}
        }
    }
    
    public void setLastSignin(String seqUser) throws Exception {
        LocalDBConnection conn = null;
        try {
            conn = new LocalDBConnection();
            conn.txOpen();

            StringBuffer querySb = new StringBuffer();
            querySb.append("UPDATE user set");
            querySb.append(" last_signin="+JinieboxUtil.getNowString());
            querySb.append(" where seq=" + seqUser);

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
            if (conn != null) try { conn.close(); } catch (Exception ignored) {}
        }
    }

    /**
     * row delete
     * @param seq
     * @return
     * @throws Exception
     */
    public boolean delete(String seq) throws Exception {

        StringBuffer querySb = new StringBuffer();
        querySb.append("DELETE FROM user");
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
