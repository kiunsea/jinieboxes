package com.omnibuscode.dao;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.json.simple.JSONObject;

import com.omnibuscode.base.LocalDBConnection;
import com.omnibuscode.utils.ExceptionUtil;

/**
 * 장보고 사용자 정보 DAO
 * 
 * jbg_info 테이블: 지니박스 사용자별 장보고 관련 정보 관리
 * user 테이블과 1:1 매핑 (user.seq = jbg_info.seq_user)
 * 
 * @author KIUNSEA
 */
public class JbgInfoDataAccessObject extends CommonDataAccessObject {

    private static final Logger log = LogManager.getLogger(JbgInfoDataAccessObject.class);

    public JbgInfoDataAccessObject() {
        // 기본 생성자
    }
    
//    /**
//     * jbg_info 테이블 생성 및 ftp_id 컬럼 확인
//     * 
//     * @param conn LocalDBConnection
//     */
//    private void ensureJbgInfoTable(LocalDBConnection conn) {
//        try {
//            // 테이블 존재 여부 확인
//            conn.executeQuery("SELECT seq FROM jbg_info LIMIT 1");
//            log.debug("jbg_info 테이블이 이미 존재합니다.");
//            
//            // ftp_id 컬럼 존재 여부 확인 및 추가
//            ensureFtpIdColumn(conn);
//            
//            // auto_collect 컬럼들 존재 여부 확인 및 추가
//            ensureAutoCollectColumns(conn);
//            
//        } catch (Exception e) {
//            // 테이블이 없으면 생성
//            try {
//                conn.txOpen();
//                StringBuffer createTableSb = new StringBuffer();
//                createTableSb.append("CREATE TABLE jbg_info (");
//                createTableSb.append("  seq INTEGER PRIMARY KEY AUTOINCREMENT,");
//                createTableSb.append("  seq_user INTEGER NOT NULL UNIQUE,");
//                createTableSb.append("  sec_priv_key TEXT DEFAULT '',");
//                createTableSb.append("  sec_pub_key TEXT DEFAULT '',");
//                createTableSb.append("  ftp_id TEXT DEFAULT '',");
//                createTableSb.append("  auto_collect_enabled INTEGER DEFAULT 0,");
//                createTableSb.append("  auto_collect_interval INTEGER DEFAULT 5,");
//                createTableSb.append("  insert_time INTEGER DEFAULT 0,");
//                createTableSb.append("  update_time INTEGER DEFAULT 0");
//                createTableSb.append(")");
//                
//                conn.txExecuteUpdate(createTableSb.toString());
//                conn.txCommit();
//                log.info("jbg_info 테이블 생성 완료");
//            } catch (Exception ex) {
//                try { 
//                    conn.txRollBack(); 
//                } catch (Exception ignore) {}
//                log.error("jbg_info 테이블 생성 실패: " + ex.getMessage());
//            }
//        }
//    }
    
//    /**
//     * jbg_info 테이블에 ftp_id 컬럼이 있는지 확인하고 없으면 추가
//     * 
//     * @param conn LocalDBConnection
//     */
//    private void ensureFtpIdColumn(LocalDBConnection conn) {
//        try {
//            // ftp_id 컬럼 존재 여부 확인
//            conn.executeQuery("SELECT ftp_id FROM jbg_info LIMIT 1");
//            log.debug("ftp_id 컬럼이 이미 존재합니다.");
//        } catch (Exception e) {
//            // ftp_id 컬럼이 없으면 추가
//            try {
//                conn.txOpen();
//                conn.txExecuteUpdate("ALTER TABLE jbg_info ADD COLUMN ftp_id TEXT DEFAULT ''");
//                conn.txCommit();
//                log.info("jbg_info 테이블에 ftp_id 컬럼 추가 완료");
//            } catch (Exception ex) {
//                try { 
//                    conn.txRollBack(); 
//                } catch (Exception ignore) {}
//                log.error("ftp_id 컬럼 추가 실패: " + ex.getMessage());
//            }
//        }
//    }
//    
//    /**
//     * jbg_info 테이블에 auto_collect 컬럼들이 있는지 확인하고 없으면 추가
//     * 
//     * @param conn LocalDBConnection
//     */
//    private void ensureAutoCollectColumns(LocalDBConnection conn) {
//        // auto_collect_enabled 컬럼 추가
//        try {
//            conn.executeQuery("SELECT auto_collect_enabled FROM jbg_info LIMIT 1");
//            log.debug("auto_collect_enabled 컬럼이 이미 존재합니다.");
//        } catch (Exception e) {
//            try {
//                conn.txOpen();
//                conn.txExecuteUpdate("ALTER TABLE jbg_info ADD COLUMN auto_collect_enabled INTEGER DEFAULT 0");
//                conn.txCommit();
//                log.info("jbg_info 테이블에 auto_collect_enabled 컬럼 추가 완료");
//            } catch (Exception ex) {
//                try { 
//                    conn.txRollBack(); 
//                } catch (Exception ignore) {}
//                log.error("auto_collect_enabled 컬럼 추가 실패: " + ex.getMessage());
//            }
//        }
//        
//        // auto_collect_interval 컬럼 추가
//        try {
//            conn.executeQuery("SELECT auto_collect_interval FROM jbg_info LIMIT 1");
//            log.debug("auto_collect_interval 컬럼이 이미 존재합니다.");
//        } catch (Exception e) {
//            try {
//                conn.txOpen();
//                conn.txExecuteUpdate("ALTER TABLE jbg_info ADD COLUMN auto_collect_interval INTEGER DEFAULT 5");
//                conn.txCommit();
//                log.info("jbg_info 테이블에 auto_collect_interval 컬럼 추가 완료");
//            } catch (Exception ex) {
//                try { 
//                    conn.txRollBack(); 
//                } catch (Exception ignore) {}
//                log.error("auto_collect_interval 컬럼 추가 실패: " + ex.getMessage());
//            }
//        }
//    }
    
    /**
     * 보안 키 쌍 저장 또는 업데이트 (UPSERT)
     * 
     * @param seqUser 사용자 seq
     * @param secPrivKey 보안 Private Key (Base64 인코딩)
     * @param secPubKey 보안 Public Key (Base64 인코딩)
     * @throws Exception
     */
    public void saveSecurityKeys(String seqUser, String secPrivKey, String secPubKey) throws Exception {
        LocalDBConnection conn = null;
        try {
            conn = new LocalDBConnection();
            
            // 테이블 존재 여부 확인 및 생성
//            ensureJbgInfoTable(conn);
            
            // 해당 사용자의 레코드가 있는지 확인
            StringBuffer checkQuery = new StringBuffer("SELECT seq FROM jbg_info");
            checkQuery.append(" WHERE seq_user=" + seqUser);
            
            log.debug("LOCAL-QUERY------------------------------------------------------------------------------");
            log.debug(checkQuery);
            ResultSet rset = conn.executeQuery(checkQuery.toString());
            
            boolean hasRecord = (rset != null && rset.next());
            
            conn.txOpen();
            
            long currentTime = System.currentTimeMillis();
            
            if (hasRecord) {
                // 레코드가 있으면 UPDATE
                StringBuffer updateSb = new StringBuffer();
                updateSb.append("UPDATE jbg_info SET");
                updateSb.append(" sec_priv_key='" + (secPrivKey != null ? secPrivKey : "") + "'");
                updateSb.append(", sec_pub_key='" + (secPubKey != null ? secPubKey : "") + "'");
                updateSb.append(", update_time=" + currentTime);
                updateSb.append(" WHERE seq_user=" + seqUser);

                String query = updateSb.toString();
                log.debug("LOCAL-QUERY------------------------------------------------------------------------------");
                log.debug(query);
                conn.txExecuteUpdate(query);
                log.info("보안 키 쌍 업데이트 완료 - seq_user: " + seqUser);
            } else {
                // 레코드가 없으면 INSERT
                StringBuffer insertSb = new StringBuffer();
                insertSb.append("INSERT INTO jbg_info (");
                insertSb.append("seq_user, sec_priv_key, sec_pub_key, insert_time, update_time");
                insertSb.append(") VALUES (");
                insertSb.append(seqUser);
                insertSb.append(", '" + (secPrivKey != null ? secPrivKey : "") + "'");
                insertSb.append(", '" + (secPubKey != null ? secPubKey : "") + "'");
                insertSb.append(", " + currentTime);
                insertSb.append(", " + currentTime);
                insertSb.append(")");

                String query = insertSb.toString();
                log.debug("LOCAL-QUERY------------------------------------------------------------------------------");
                log.debug(query);
                conn.txExecuteUpdate(query);
                log.info("보안 키 쌍 신규 생성 완료 - seq_user: " + seqUser);
            }
            
            conn.txCommit();
            
        } catch (SQLException e) {
            log.error("* 데이터베이스 업데이트 에러 발생");
            log.error(ExceptionUtil.getExceptionInfo(e));
            if (conn != null) conn.txRollBack();
            throw e;
        } finally {
            if (conn != null) conn.close();
        }
    }
    
    /**
     * 보안 키 쌍 조회
     * 
     * @param seqUser 사용자 seq
     * @return JSONObject { secPrivKey: "...", secPubKey: "..." }
     * @throws Exception
     */
    public JSONObject getSecurityKeys(String seqUser) throws Exception {
        LocalDBConnection conn = null;
        try {
            conn = new LocalDBConnection();
            
            // 테이블 존재 여부 확인 및 생성
//            ensureJbgInfoTable(conn);
            
            StringBuffer querySb = new StringBuffer("SELECT sec_priv_key, sec_pub_key FROM jbg_info");
            querySb.append(" WHERE seq_user=" + seqUser);
            
            log.debug("LOCALDB-QUERY------------------------------------------------------------------------------");
            log.debug(querySb);
            ResultSet rset = conn.executeQuery(querySb.toString());

            JSONObject keys = new JSONObject();
            if (rset != null && rset.next()) {
                String secPrivKey = rset.getString("sec_priv_key");
                String secPubKey = rset.getString("sec_pub_key");
                keys.put("secPrivKey", secPrivKey != null ? secPrivKey : "");
                keys.put("secPubKey", secPubKey != null ? secPubKey : "");
            } else {
                keys.put("secPrivKey", "");
                keys.put("secPubKey", "");
            }
            return keys;
        } catch (Exception e) {
            log.error("* 프로그램 수행중 에러 발생");
            log.error(ExceptionUtil.getExceptionInfo(e));
            throw e;
        } finally {
            if (conn != null) conn.close();
        }
    }
    
    /**
     * 보안 Public Key만 조회
     * 
     * @param seqUser 사용자 seq
     * @return Public Key (Base64 인코딩) 또는 빈 문자열
     * @throws Exception
     */
    public String getPublicKey(String seqUser) throws Exception {
        JSONObject keys = getSecurityKeys(seqUser);
        return keys.get("secPubKey") != null ? keys.get("secPubKey").toString() : "";
    }
    
    /**
     * 보안 Private Key만 조회
     * 
     * @param seqUser 사용자 seq
     * @return Private Key (Base64 인코딩) 또는 빈 문자열
     * @throws Exception
     */
    public String getPrivateKey(String seqUser) throws Exception {
        JSONObject keys = getSecurityKeys(seqUser);
        return keys.get("secPrivKey") != null ? keys.get("secPrivKey").toString() : "";
    }
    
    /**
     * 보안 키 쌍 삭제
     * 
     * @param seqUser 사용자 seq
     * @throws Exception
     */
    public void deleteSecurityKeys(String seqUser) throws Exception {
        LocalDBConnection conn = null;
        try {
            conn = new LocalDBConnection();
            
            // 테이블 존재 여부 확인 및 생성
//            ensureJbgInfoTable(conn);
            
            conn.txOpen();
            
            StringBuffer deleteSb = new StringBuffer();
            deleteSb.append("DELETE FROM jbg_info WHERE seq_user=" + seqUser);
            
            String query = deleteSb.toString();
            log.debug("LOCAL-QUERY------------------------------------------------------------------------------");
            log.debug(query);
            conn.txExecuteUpdate(query);
            conn.txCommit();
            
            log.info("보안 키 쌍 삭제 완료 - seq_user: " + seqUser);
        } catch (SQLException e) {
            log.error("* 데이터베이스 삭제 에러 발생");
            log.error(ExceptionUtil.getExceptionInfo(e));
            if (conn != null) conn.txRollBack();
            throw e;
        } finally {
            if (conn != null) conn.close();
        }
    }
    
    /**
     * FTP ID 저장 또는 업데이트
     * 
     * @param seqUser 사용자 seq
     * @param ftpId FTP 계정 ID
     * @throws Exception
     */
    public void saveFtpId(String seqUser, String ftpId) throws Exception {
        LocalDBConnection conn = null;
        try {
            conn = new LocalDBConnection();
            
            // 테이블 존재 여부 확인 및 생성
//            ensureJbgInfoTable(conn);
            
            // 해당 사용자의 레코드가 있는지 확인
            StringBuffer checkQuery = new StringBuffer("SELECT seq FROM jbg_info");
            checkQuery.append(" WHERE seq_user=" + seqUser);
            
            log.debug("LOCAL-QUERY------------------------------------------------------------------------------");
            log.debug(checkQuery);
            ResultSet rset = conn.executeQuery(checkQuery.toString());
            
            boolean hasRecord = (rset != null && rset.next());
            
            conn.txOpen();
            
            long currentTime = System.currentTimeMillis();
            
            if (hasRecord) {
                // 레코드가 있으면 UPDATE
                StringBuffer updateSb = new StringBuffer();
                updateSb.append("UPDATE jbg_info SET");
                updateSb.append(" ftp_id='" + (ftpId != null ? ftpId : "") + "'");
                updateSb.append(", update_time=" + currentTime);
                updateSb.append(" WHERE seq_user=" + seqUser);

                String query = updateSb.toString();
                log.debug("LOCAL-QUERY------------------------------------------------------------------------------");
                log.debug(query);
                conn.txExecuteUpdate(query);
                log.info("FTP ID 업데이트 완료 - seq_user: " + seqUser + ", ftp_id: " + ftpId);
            } else {
                // 레코드가 없으면 INSERT
                StringBuffer insertSb = new StringBuffer();
                insertSb.append("INSERT INTO jbg_info (");
                insertSb.append("seq_user, ftp_id, insert_time, update_time");
                insertSb.append(") VALUES (");
                insertSb.append(seqUser);
                insertSb.append(", '" + (ftpId != null ? ftpId : "") + "'");
                insertSb.append(", " + currentTime);
                insertSb.append(", " + currentTime);
                insertSb.append(")");

                String query = insertSb.toString();
                log.debug("LOCAL-QUERY------------------------------------------------------------------------------");
                log.debug(query);
                conn.txExecuteUpdate(query);
                log.info("FTP ID 신규 생성 완료 - seq_user: " + seqUser + ", ftp_id: " + ftpId);
            }
            
            conn.txCommit();
            
        } catch (SQLException e) {
            log.error("* 데이터베이스 업데이트 에러 발생");
            log.error(ExceptionUtil.getExceptionInfo(e));
            if (conn != null) conn.txRollBack();
            throw e;
        } finally {
            if (conn != null) conn.close();
        }
    }
    
    /**
     * FTP ID 조회
     * 
     * @param seqUser 사용자 seq
     * @return FTP 계정 ID 또는 빈 문자열
     * @throws Exception
     */
    public String getFtpId(String seqUser) throws Exception {
        LocalDBConnection conn = null;
        try {
            conn = new LocalDBConnection();
            
            // 테이블 존재 여부 확인 및 생성
//            ensureJbgInfoTable(conn);
            
            StringBuffer querySb = new StringBuffer("SELECT ftp_id FROM jbg_info");
            querySb.append(" WHERE seq_user=" + seqUser);
            
            log.debug("LOCALDB-QUERY------------------------------------------------------------------------------");
            log.debug(querySb);
            ResultSet rset = conn.executeQuery(querySb.toString());

            if (rset != null && rset.next()) {
                String ftpId = rset.getString("ftp_id");
                return ftpId != null ? ftpId : "";
            }
            return "";
        } catch (Exception e) {
            log.error("* 프로그램 수행중 에러 발생");
            log.error(ExceptionUtil.getExceptionInfo(e));
            throw e;
        } finally {
            if (conn != null) conn.close();
        }
    }
    
    /**
     * 사용자 정보 전체 조회
     * 
     * @param seqUser 사용자 seq
     * @return JSONObject { seq, seq_user, sec_priv_key, sec_pub_key, ftp_id, insert_time, update_time }
     * @throws Exception
     */
    public JSONObject getInfo(String seqUser) throws Exception {
        LocalDBConnection conn = null;
        try {
            conn = new LocalDBConnection();
            
            // 테이블 존재 여부 확인 및 생성
//            ensureJbgInfoTable(conn);
            
            StringBuffer querySb = new StringBuffer("SELECT * FROM jbg_info");
            querySb.append(" WHERE seq_user=" + seqUser);
            
            log.debug("LOCALDB-QUERY------------------------------------------------------------------------------");
            log.debug(querySb);
            ResultSet rset = conn.executeQuery(querySb.toString());

            JSONObject info = new JSONObject();
            if (rset != null && rset.next()) {
                info.put("seq", rset.getInt("seq"));
                info.put("seq_user", rset.getInt("seq_user"));
                info.put("sec_priv_key", rset.getString("sec_priv_key"));
                info.put("sec_pub_key", rset.getString("sec_pub_key"));
                info.put("ftp_id", rset.getString("ftp_id"));
                
                // auto_collect 설정 (컬럼이 없을 수도 있으므로 try-catch로 처리)
                try {
                    info.put("auto_collect_enabled", rset.getInt("auto_collect_enabled"));
                    info.put("auto_collect_interval", rset.getInt("auto_collect_interval"));
                } catch (Exception e) {
                    // 컬럼이 없으면 기본값
                    info.put("auto_collect_enabled", 0);
                    info.put("auto_collect_interval", 5);
                }
                
                info.put("insert_time", rset.getLong("insert_time"));
                info.put("update_time", rset.getLong("update_time"));
            }
            return info;
        } catch (Exception e) {
            log.error("* 프로그램 수행중 에러 발생");
            log.error(ExceptionUtil.getExceptionInfo(e));
            throw e;
        } finally {
            if (conn != null) conn.close();
        }
    }
    
    /**
     * FTP ID로 jiniebox 사용자 seq 조회
     * 
     * @param ftpId FTP 계정 ID
     * @return 사용자 seq (문자열) 또는 null
     * @throws Exception
     */
    public String getUserSeqByFtpId(String ftpId) throws Exception {
        LocalDBConnection conn = null;
        try {
            conn = new LocalDBConnection();
//            ensureJbgInfoTable(conn);
            
            String query = "SELECT seq_user FROM jbg_info WHERE ftp_id = '" + ftpId + "'";
            log.debug("LOCALDB-QUERY------------------------------------------------------------------------------");
            log.debug(query);
            
            ResultSet rset = conn.executeQuery(query);
            
            if (rset != null && rset.next()) {
                String seqUser = String.valueOf(rset.getInt("seq_user"));
                log.debug("FTP ID [" + ftpId + "] -> seq_user [" + seqUser + "]");
                return seqUser;
            }
            
            log.warn("FTP ID [" + ftpId + "]에 매핑된 사용자가 없습니다.");
            return null;
        } finally {
            if (conn != null) conn.close();
        }
    }
    
    /**
     * 모든 FTP ID 목록 조회
     * 
     * @return FTP ID 목록
     * @throws Exception
     */
    public java.util.List<String> getAllFtpIds() throws Exception {
        LocalDBConnection conn = null;
        try {
            conn = new LocalDBConnection();
//            ensureJbgInfoTable(conn);
            
            String query = "SELECT ftp_id FROM jbg_info WHERE ftp_id IS NOT NULL AND ftp_id != ''";
            log.debug("LOCALDB-QUERY------------------------------------------------------------------------------");
            log.debug(query);
            
            ResultSet rset = conn.executeQuery(query);
            
            java.util.List<String> ftpIds = new java.util.ArrayList<>();
            while (rset != null && rset.next()) {
                String ftpId = rset.getString("ftp_id");
                if (ftpId != null && !ftpId.isEmpty()) {
                    ftpIds.add(ftpId);
                }
            }
            
            log.info("등록된 FTP ID 개수: " + ftpIds.size());
            return ftpIds;
        } finally {
            if (conn != null) conn.close();
        }
    }

    /**
     * 자동 수집이 활성화된 사용자들의 FTP ID 목록 조회
     *
     * @return auto_collect_enabled = 1 인 사용자들의 FTP ID 목록
     * @throws Exception
     */
    public java.util.List<String> getAutoCollectEnabledFtpIds() throws Exception {
        LocalDBConnection conn = null;
        try {
            conn = new LocalDBConnection();
//            ensureJbgInfoTable(conn);

            String query = "SELECT ftp_id FROM jbg_info " +
                           "WHERE ftp_id IS NOT NULL AND ftp_id != '' " +
                           "AND auto_collect_enabled = 1";
            log.debug("LOCALDB-QUERY------------------------------------------------------------------------------");
            log.debug(query);

            ResultSet rset = conn.executeQuery(query);

            java.util.List<String> ftpIds = new java.util.ArrayList<>();
            while (rset != null && rset.next()) {
                String ftpId = rset.getString("ftp_id");
                if (ftpId != null && !ftpId.isEmpty()) {
                    ftpIds.add(ftpId);
                }
            }

            log.info("자동 수집 활성화된 FTP ID 개수: " + ftpIds.size());
            return ftpIds;
        } finally {
            if (conn != null) conn.close();
        }
    }

    /**
     * 자동 수집이 활성화된 사용자가 존재하는지 여부 반환
     *
     * @return true if any user has auto_collect_enabled = 1
     * @throws Exception
     */
//    public boolean hasAutoCollectEnabledUsers() throws Exception {
//        LocalDBConnection conn = null;
//        try {
//            conn = new LocalDBConnection();
////            ensureJbgInfoTable(conn);
//
//            String query = "SELECT COUNT(1) cnt FROM jbg_info WHERE auto_collect_enabled = 1";
//            log.debug("LOCALDB-QUERY------------------------------------------------------------------------------");
//            log.debug(query);
//
//            ResultSet rset = conn.executeQuery(query);
//            if (rset != null && rset.next()) {
//                int count = rset.getInt("cnt");
//                log.info("자동 수집 활성 사용자 수: " + count);
//                return count > 0;
//            }
//            return false;
//        } finally {
//            if (conn != null) conn.close();
//        }
//    }
    
    /**
     * 자동 수집 설정 저장 (UPSERT)
     * 
     * @param seqUser 사용자 seq
     * @param enabled 자동 수집 활성화 여부 (0: 비활성화, 1: 활성화)
     * @param intervalMinutes 수집 주기 (분)
     * @throws Exception
     */
    public void setAutoCollectConfig(String seqUser, int enabled, int intervalMinutes) throws Exception {
        LocalDBConnection conn = null;
        try {
            conn = new LocalDBConnection();
//            ensureJbgInfoTable(conn);
            
            // 해당 사용자의 레코드가 있는지 확인
            StringBuffer checkQuery = new StringBuffer("SELECT seq FROM jbg_info");
            checkQuery.append(" WHERE seq_user=" + seqUser);
            
            log.debug("LOCALDB-QUERY------------------------------------------------------------------------------");
            log.debug(checkQuery);
            ResultSet rset = conn.executeQuery(checkQuery.toString());
            
            boolean hasRecord = (rset != null && rset.next());
            
            conn.txOpen();
            
            long currentTime = System.currentTimeMillis();
            
            if (hasRecord) {
                // 레코드가 있으면 UPDATE
                StringBuffer updateSb = new StringBuffer();
                updateSb.append("UPDATE jbg_info SET");
                updateSb.append(" auto_collect_enabled=" + enabled);
                updateSb.append(", auto_collect_interval=" + intervalMinutes);
                updateSb.append(", update_time=" + currentTime);
                updateSb.append(" WHERE seq_user=" + seqUser);
                
                log.debug("LOCALDB-UPDATE------------------------------------------------------------------------------");
                log.debug(updateSb);
                conn.txExecuteUpdate(updateSb.toString());
                log.info("자동 수집 설정 업데이트 완료 - seqUser: " + seqUser + ", enabled: " + enabled + ", interval: " + intervalMinutes);
            } else {
                // 레코드가 없으면 INSERT
                StringBuffer insertSb = new StringBuffer();
                insertSb.append("INSERT INTO jbg_info");
                insertSb.append(" (seq_user, sec_priv_key, sec_pub_key, ftp_id, auto_collect_enabled, auto_collect_interval, insert_time, update_time)");
                insertSb.append(" VALUES");
                insertSb.append(" (" + seqUser + ", '', '', '', " + enabled + ", " + intervalMinutes + ", " + currentTime + ", " + currentTime + ")");
                
                log.debug("LOCALDB-INSERT------------------------------------------------------------------------------");
                log.debug(insertSb);
                conn.txExecuteUpdate(insertSb.toString());
                log.info("자동 수집 설정 신규 생성 완료 - seqUser: " + seqUser + ", enabled: " + enabled + ", interval: " + intervalMinutes);
            }
            
            conn.txCommit();
        } catch (Exception e) {
            if (conn != null) {
                try {
                    conn.txRollBack();
                } catch (Exception ignore) {}
            }
            log.error("* 자동 수집 설정 저장 중 에러 발생");
            log.error(ExceptionUtil.getExceptionInfo(e));
            throw e;
        } finally {
            if (conn != null) conn.close();
        }
    }
    
    /**
     * 자동 수집 설정 조회
     * 
     * @param seqUser 사용자 seq
     * @return JSONObject (enabled, interval) 또는 기본값
     * @throws Exception
     */
    public JSONObject getAutoCollectConfig(String seqUser) throws Exception {
        LocalDBConnection conn = null;
        try {
            conn = new LocalDBConnection();
//            ensureJbgInfoTable(conn);
            
            StringBuffer querySb = new StringBuffer("SELECT auto_collect_enabled, auto_collect_interval");
            querySb.append(" FROM jbg_info");
            querySb.append(" WHERE seq_user=" + seqUser);
            
            log.debug("LOCALDB-QUERY------------------------------------------------------------------------------");
            log.debug(querySb);
            ResultSet rset = conn.executeQuery(querySb.toString());
            
            JSONObject config = new JSONObject();
            if (rset != null && rset.next()) {
                config.put("enabled", rset.getInt("auto_collect_enabled"));
                config.put("interval", rset.getInt("auto_collect_interval"));
                log.debug("자동 수집 설정 조회 완료 - seqUser: " + seqUser + ", enabled: " + config.get("enabled") + ", interval: " + config.get("interval"));
            } else {
                // 레코드가 없으면 기본값 반환
                config.put("enabled", 0);
                config.put("interval", 5);
                log.debug("자동 수집 설정 없음 - 기본값 반환 (enabled: 0, interval: 5)");
            }
            
            return config;
        } catch (Exception e) {
            log.error("* 자동 수집 설정 조회 중 에러 발생");
            log.error(ExceptionUtil.getExceptionInfo(e));
            throw e;
        } finally {
            if (conn != null) conn.close();
        }
    }
}

