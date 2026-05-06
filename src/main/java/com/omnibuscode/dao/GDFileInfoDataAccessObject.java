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
public class GDFileInfoDataAccessObject extends CommonDataAccessObject {

    /**
     * 서버 환경 변경시 log4j.properties 설정 정보도 함께 변경 필요
     */
	private Logger log = LogManager.getLogger(GDFileInfoDataAccessObject.class);
	
	public int GD_FILE_TYPE_FILE = 0;
	public int GD_FILE_TYPE_FOLDER = 1;

    public GDFileInfoDataAccessObject() {;}
    
//    /**
//     * 구글드라이브의 폴더중 시스템에 예약된 경로의 폴더를 조회
//     * @param seqStore
//     * @param folderName
//     * @return
//     * @throws Exception
//     */
//    public JSONObject getSysFolder(String seqStore, String folderName) throws Exception {
//        
//        LocalDBConnection conn = null;
//        try {
//            conn = new LocalDBConnection();
//            StringBuffer querySb = new StringBuffer("SELECT seq, gd_file_id from gd_file_info");
//            querySb.append(" WHERE gd_file_type = 1");
//            querySb.append(" AND seq_store = " + seqStore);
//            querySb.append(" AND name = '" + folderName + "'");
//            log.debug("LOCALDB-QUERY------------------------------------------------------------------------------");
//            log.debug(querySb);
//            ResultSet rset = conn.executeQuery(querySb.toString());
//
//            if (rset != null && this.getResultSetSize(rset) > 0) {
//                JSONObject rtnJson = new JSONObject();
//                rtnJson.put("seq", rset.getInt("seq"));
//                rtnJson.put("gd_file_id", rset.getString("gd_file_id"));
//                return rtnJson;
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
    
    public List<JSONObject> getFiles(String seq_class, String type_class) throws Exception {
        
        List<JSONObject> items = null;
        LocalDBConnection conn = null;
        try {
            conn = new LocalDBConnection();
            StringBuffer querySb = new StringBuffer("SELECT seq, name, gd_file_id, gd_file_type from gd_file_info");
            querySb.append(" WHERE type_class = '" + type_class+"'");
            querySb.append(" AND seq_class = " + seq_class);
            log.debug("LOCALDB-QUERY------------------------------------------------------------------------------");
            log.debug(querySb);
            ResultSet rset = conn.executeQuery(querySb.toString());

            JSONObject jsonObj = null;
            if (rset != null) {
                items = new ArrayList();
                while (rset.next()) {
                    jsonObj = new JSONObject();
                    jsonObj.put("seq", rset.getInt("seq"));
                    jsonObj.put("name", rset.getString("name"));
                    jsonObj.put("gd_file_id", rset.getString("gd_file_id"));
                    jsonObj.put("gd_file_type", rset.getInt("gd_file_type"));
                    items.add(jsonObj);
                }
                return items;
            } else {
                return null;
            }
        } catch (Exception e) {
            log.error("* 프로그램 수행중 에러 발생");
            log.error(ExceptionUtil.getExceptionInfo(e));
            throw e;
        } finally {
            if (conn != null) try { conn.close(); } catch (Exception ignored) {}
        }
    }
    
    public void addFile(String file_name, String file_id, int file_type, String type_class, String seq_class) throws Exception {
        
        StringBuffer querySb = new StringBuffer();
        querySb.append("INSERT INTO gd_file_info (");
        querySb.append("name");
        querySb.append(",gd_file_id");
        querySb.append(",gd_file_type");
        querySb.append(",type_class");
        querySb.append(",seq_class");
        querySb.append(")");
        querySb.append(" VALUES ");
        querySb.append("(");
        querySb.append("'" + file_name + "'");
        querySb.append(",'" + file_id + "'");
        querySb.append("," + file_type);
        querySb.append(",'" + type_class + "'");
        querySb.append("," + seq_class);
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
            if (conn != null) try { conn.close(); } catch (Exception ignored) {}
        }
        
    }
    
    public void deleteFile(String file_id, String seqStore) throws Exception {
        StringBuffer querySb = new StringBuffer();
        querySb.append("DELETE from gd_file_info");
        querySb.append(" WHERE gd_file_id='" + file_id + "'");

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
