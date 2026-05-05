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
import com.omnibuscode.utils.ExceptionUtil;

/**
 * @author KIUNSEA
 *
 */
public class StoreDataAccessObject extends CommonDataAccessObject {

    /**
     * 서버 환경 변경시 log4j.properties 설정 정보도 함께 변경 필요
     */
//    private Log log = LogFactory.getLog(StoreDataAccessObject.class);
    private Logger log = LogManager.getLogger(StoreDataAccessObject.class);

    public int IMG_USE_FALSE = 0;
    public int IMG_USE_TRUE = 1;
    /**
     * 0(jbs local)
     */
    public int STORAGE_TYPE_LOCAL = 0;
    /**
     * 1(google)
     */
    public int STORAGE_TYPE_GOOGLE = 1; 
    
    public StoreDataAccessObject() {;}
    
    /**
     * 저장소 정보 반환
     * 
     * @param seqStore
     * @return
     * @throws Exception
     */
    public JSONObject getStore(String seqStore) throws Exception {
        LocalDBConnection conn = null;
        try {
            conn = new LocalDBConnection();
            StringBuffer querySb = new StringBuffer("SELECT seq, name, details, seq_owner, insert_time, img_use, storage_type, standby_days from store");
            querySb.append(" WHERE seq =" + seqStore);
            log.debug("LOCALDB-QUERY------------------------------------------------------------------------------");
            log.debug(querySb);
            ResultSet rset = conn.executeQuery(querySb.toString());

            JSONObject jsonObj = null;
            if (rset != null) {
                while (rset.next()) {
                    jsonObj = new JSONObject();
                    jsonObj.put("seq", rset.getString("seq"));
                    jsonObj.put("name", rset.getString("name"));
                    jsonObj.put("details", rset.getString("details"));
                    jsonObj.put("seq_owner", rset.getString("seq_owner"));
                    jsonObj.put("insert_time", rset.getString("insert_time"));
                    jsonObj.put("img_use", rset.getInt("img_use"));
                    jsonObj.put("storage_type", rset.getInt("storage_type"));
                    jsonObj.put("standby_days", rset.getInt("standby_days"));
                }
            }
            return jsonObj;
        } catch (Exception e) {
            log.error("* 프로그램 수행중 에러 발생");
            log.error(ExceptionUtil.getExceptionInfo(e));
            throw e;
        } finally {
            conn.close();
        }
    }
    
    /**
     * 저장소 정보 반환 (by owner seq)
     * @param seqBoxes
     * @return
     * @throws Exception
     */
    public JSONObject getOwnStore(String seqOwner) throws Exception {
        LocalDBConnection conn = null;
        try {
            conn = new LocalDBConnection();
            StringBuffer querySb = new StringBuffer("SELECT seq, name, details, storage_type, insert_time from store");
            querySb.append(" WHERE seq_owner =" + seqOwner);
            log.debug("LOCALDB-QUERY------------------------------------------------------------------------------");
            log.debug(querySb);
            ResultSet rset = conn.executeQuery(querySb.toString());

            JSONObject jsonObj = null;
            if (rset != null) {
                while (rset.next()) {
                    jsonObj = new JSONObject();
                    jsonObj.put("seq", rset.getInt("seq"));
                    jsonObj.put("name", rset.getString("name"));
                    jsonObj.put("details", rset.getString("details"));
                    jsonObj.put("storage_type", rset.getInt("storage_type"));
                    jsonObj.put("insert_time", rset.getString("insert_time"));
                }
            }
            return jsonObj;
        } catch (Exception e) {
            log.error("* 프로그램 수행중 에러 발생");
            log.error(ExceptionUtil.getExceptionInfo(e));
            throw e;
        } finally {
            conn.close();
        }
    }
    
    /**
     * 공유받은 저장소 정보 목록 반환
     * 
     * @param storeName
     * @return
     * @throws Exception
     */
    public List<JSONObject> getSharedStores(String seqUser) throws Exception {

        List<JSONObject> stores = null;
        LocalDBConnection conn = null;
        try {
            conn = new LocalDBConnection();
            StringBuffer querySb = new StringBuffer(
                    "SELECT st.seq seq, st.name name, st.details details, st.seq_owner seq_owner, st.insert_time insert_time, sh.seq_object seq_store, sh.authority authority"
                    + " from store st, share sh");
            querySb.append(" WHERE st.seq = sh.seq_object");
            querySb.append(" AND sh.type_object = '" + EnvSYS.CLASS_TYPE_STORE + "'");
            querySb.append(" AND sh.seq_user =" + seqUser);
            log.debug("LOCALDB-QUERY------------------------------------------------------------------------------");
            log.debug(querySb);
            ResultSet rset = conn.executeQuery(querySb.toString());

            JSONObject jsonObj = null;
            if (rset != null) {
                stores = new ArrayList();
                while (rset.next()) {
                    jsonObj = new JSONObject();
                    jsonObj.put("seq", rset.getString("seq"));
                    jsonObj.put("name", rset.getString("name"));
                    jsonObj.put("details", rset.getString("details"));
                    jsonObj.put("seq_owner", rset.getString("seq_owner"));
                    jsonObj.put("authority", rset.getString("authority"));
                    jsonObj.put("insert_time", rset.getString("insert_time"));
                    stores.add(jsonObj);
                }
            }
            return stores;
        } catch (Exception e) {
            log.error("* 프로그램 수행중 에러 발생");
            log.error(ExceptionUtil.getExceptionInfo(e));
            throw e;
        } finally {
            conn.close();
        }
    }
    
    /**
     * owner seq로 저장소 seq 조회
     * @param seqOwner
     * @return 조회 오류시 -1 반환
     * @throws Exception
     */
    public String getSeq(String seqOwner) throws Exception {
        LocalDBConnection conn = null;
        try {
            conn = new LocalDBConnection();
            StringBuffer querySb = new StringBuffer("SELECT seq from store");
            querySb.append(" WHERE seq_owner = " + seqOwner);
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
            conn.close();
        }
    }

    /**
     * 저장소 등록
     * @param juid jiniebox user id
     * @param jupw jiniebox user pw
     * @param buid bixby user id
     * @param juname [옵션]사용자명
     * @param sname [옵션]저장소명
     * @return 저장 성공 여부
     * @throws Exception
     */
    public boolean regist(String sname, String seqOwner) throws Exception {

        StringBuffer querySb = new StringBuffer();
        querySb.append("INSERT INTO store (");
        querySb.append("name,");
        querySb.append("seq_owner,");
        querySb.append("insert_time");
        querySb.append(") values (");
        querySb.append("'" + sname + "', ");
        querySb.append(seqOwner + ", ");
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
            conn.close();
        }
        
        return true;
    }
    
    /**
     * 이미지 저장 유무 설정<br/>
     * imgUse >> 0:false, 1:true
     * @param seqOwner
     * @param imgUse
     * @throws Exception
     */
    public void setImguse(String seqOwner, int imgUse) throws Exception {
        LocalDBConnection conn = null;
        try {
            conn = new LocalDBConnection();
            conn.txOpen();

            StringBuffer querySb = new StringBuffer();
            querySb.append("UPDATE store set");
            querySb.append(" img_use=" + imgUse);
            querySb.append(" where seq_owner=" + seqOwner);

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
    }
    
    /**
     * 파일 저장소 형태 설정<br/>
     * storageType >> 0:service local, 1:google drive
     * 
     * @param seqOwner
     * @param storageType
     * @throws Exception
     */
    public void setStorageType(String seqOwner, int storageType) throws Exception {
        LocalDBConnection conn = null;
        try {
            conn = new LocalDBConnection();
            conn.txOpen();

            StringBuffer querySb = new StringBuffer();
            querySb.append("UPDATE store set");
            querySb.append(" storage_type=" + storageType);
            querySb.append(" where seq_owner=" + seqOwner);

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
    }
    
    /**
     * 저장소 파일 저장 위치
     * 
     * @param seqOwner
     * @return
     * @throws Exception
     */
    public int getStorageType(String seqOwner) throws Exception {
        LocalDBConnection conn = null;
        try {
            conn = new LocalDBConnection();
            StringBuffer querySb = new StringBuffer("SELECT storage_type from store");
            querySb.append(" WHERE seq_owner = " + seqOwner);
            log.debug("LOCALDB-QUERY------------------------------------------------------------------------------");
            log.debug(querySb);
            ResultSet rset = conn.executeQuery(querySb.toString());

            if (rset != null && this.getResultSetSize(rset) > 0) {
                return rset.getInt("storage_type");
            }
            return 0;
        } catch (Exception e) {
            log.error("* 프로그램 수행중 에러 발생");
            log.error(ExceptionUtil.getExceptionInfo(e));
            throw e;
        } finally {
            conn.close();
        }
    }
    
    /**
     * 등록대기 보관함에서의 아이템 저장 일수 설정
     * 
     * @param seqStore
     * @param standbyDays
     * @throws Exception
     */
    public void setItemStandbyDays(String seqStore, int standbyDays) throws Exception {
        LocalDBConnection conn = null;
        try {
            conn = new LocalDBConnection();
            conn.txOpen();

            StringBuffer querySb = new StringBuffer();
            querySb.append("UPDATE store set");
            querySb.append(" standby_days=" + standbyDays);
            querySb.append(" where seq=" + seqStore);

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
    }
    
    /**
     * 등록대기 보관함에서의 아이템 저장 일수 반환
     * 
     * @param seqStore
     * @return
     * @throws Exception
     */
    public int getItemStandbyDays(String seqStore) throws Exception {
        LocalDBConnection conn = null;
        try {
            conn = new LocalDBConnection();
            StringBuffer querySb = new StringBuffer("SELECT standby_days from store");
            querySb.append(" WHERE seq = " + seqStore);
            log.debug("LOCALDB-QUERY------------------------------------------------------------------------------");
            log.debug(querySb);
            ResultSet rset = conn.executeQuery(querySb.toString());

            if (rset != null && this.getResultSetSize(rset) > 0) {
                return rset.getInt("standby_days");
            }
            return 0;
        } catch (Exception e) {
            log.error("* 프로그램 수행중 에러 발생");
            log.error(ExceptionUtil.getExceptionInfo(e));
            throw e;
        } finally {
            conn.close();
        }
    }
    
//    /**
//     * 구글 아이디 조회
//     * @param seqUser
//     * @return
//     * @throws Exception
//     */
//    public String getGmail(String seqUser) throws Exception {
//        LocalDBConnection conn = null;
//        try {
//            conn = new LocalDBConnection();
//            StringBuffer querySb = new StringBuffer("SELECT gmail from store");
//            querySb.append(" WHERE seq_owner = " + seqUser);
//            log.debug("LOCALDB-QUERY------------------------------------------------------------------------------");
//            log.debug(querySb);
//            ResultSet rset = conn.executeQuery(querySb.toString());
//
//            if (rset != null && this.getResultSetSize(rset) > 0) {
//                return rset.getString("gmail");
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
//     * 구글 아이디 저장
//     * @param seqUser
//     * @param useFlag
//     * @throws Exception
//     */
//    public void setGmail(String seqUser, String gmail) throws Exception {
//        LocalDBConnection conn = null;
//        try {
//            conn = new LocalDBConnection();
//            conn.txOpen();
//
//            StringBuffer querySb = new StringBuffer();
//            querySb.append("UPDATE store set");
//            querySb.append(" gmail='" + gmail + "'");
//            querySb.append(" where seq_owner=" + seqUser);
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
     * row delete
     * @param seq
     * @return
     * @throws Exception
     */
    public boolean delete(String seq) throws Exception {

        StringBuffer querySb = new StringBuffer();
        querySb.append("DELETE FROM store");
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
            conn.close();            
        }

        return true;
    }
    
    /**
     * 사용자의 기본 store seq 조회
     * 
     * @param seqUser 사용자 seq
     * @return store seq (문자열) 또는 null
     * @throws Exception
     */
    public String getDefaultStoreSeq(String seqUser) throws Exception {
        LocalDBConnection conn = null;
        try {
            conn = new LocalDBConnection();
            
            // 사용자가 소유한 첫 번째 store 조회
            StringBuffer querySb = new StringBuffer("SELECT seq FROM store WHERE seq_owner = ");
            querySb.append(seqUser);
            querySb.append(" LIMIT 1");
            
            log.debug("LOCALDB-QUERY------------------------------------------------------------------------------");
            log.debug(querySb);
            
            ResultSet rset = conn.executeQuery(querySb.toString());
            
            if (rset != null && rset.next()) {
                String seqStore = String.valueOf(rset.getInt("seq"));
                log.debug("seq_user [" + seqUser + "] -> seq_store [" + seqStore + "]");
                return seqStore;
            }
            
            log.warn("사용자 " + seqUser + "의 store가 없습니다.");
            return null;
        } catch (Exception e) {
            log.error("* 프로그램 수행중 에러 발생");
            log.error(ExceptionUtil.getExceptionInfo(e));
            throw e;
        } finally {
            if (conn != null) conn.close();
        }
    }

}
