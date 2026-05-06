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
 * 찜 DAO 클래스
 * @author KIUNSEA
 *
 */
public class ZZimDataAccessObject extends CommonDataAccessObject {

    /**
     * 서버 환경 변경시 log4j.properties 설정 정보도 함께 변경 필요
     */
	private Logger log = LogManager.getLogger(ZZimDataAccessObject.class);

    public ZZimDataAccessObject() {;}

    public int addZZim(String seqNitem, String seqUser, String zzimQty) throws Exception {
        int seqNanum = -1;

        StringBuffer querySb = new StringBuffer();
        querySb.append("INSERT INTO zzim (");
        querySb.append("seq_nitem");
        querySb.append(",seq_user");
        querySb.append(",zzim_qty");
        querySb.append(",insert_time");
        querySb.append(") values (");
        querySb.append(seqNitem);
        querySb.append(", " + seqUser);
        querySb.append(", " + zzimQty);
        querySb.append(", " + System.currentTimeMillis());
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
    
    public boolean hasAlready(String seqNitem, String seqUser) throws Exception {
        LocalDBConnection conn = null;
        try {
            conn = new LocalDBConnection();
            StringBuffer querySb = new StringBuffer("SELECT seq FROM zzim");
            querySb.append(" WHERE seq_nitem=" + seqNitem);
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
    
    /**
     * 나눔아이템에 대한 찜 목록 정보를 가져온다.
     * @param seqNitem
     * @return [{uname:유저명, nname:나눔함명, iname:아이템명}]
     * @throws Exception
     */
    public List<JSONObject> getZZimInfo(String seqNitem) throws Exception {
        
        List<JSONObject> zzims = null;
        
        LocalDBConnection conn = null;
        try {
            conn = new LocalDBConnection();
            StringBuffer querySb = new StringBuffer("SELECT");
            querySb.append(" u.juname uname");
            querySb.append(", n.name nname");
            querySb.append(", i.name iname");
            querySb.append(", i.seq_user iowner");
            querySb.append(" FROM nanum_item ni");
            querySb.append(" JOIN zzim z ON ni.seq = z.seq_nitem");
            querySb.append(" JOIN nanum n ON ni.seq_nanum = n.seq");
            querySb.append(" JOIN item i ON ni.seq_item = i.seq");
            querySb.append(" JOIN user u ON z.seq_user = u.seq");
            querySb.append(" WHERE ni.seq = " + seqNitem);
            log.debug("LOCALDB-QUERY------------------------------------------------------------------------------");
            log.debug(querySb);
            ResultSet rset = conn.executeQuery(querySb.toString());

            if (rset != null) {
                zzims = new ArrayList<JSONObject>();
                JSONObject jsonObj = null;
                while (rset.next()) {
                    jsonObj = new JSONObject();
                    jsonObj.put("uname", rset.getString("uname"));
                    jsonObj.put("nname", rset.getString("nname"));
                    jsonObj.put("iname", rset.getString("iname"));
                    jsonObj.put("iowner", rset.getString("iowner"));
                    
                    zzims.add(jsonObj);
                }
                return zzims;
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
    
    public int getZZimCount(String seqNitem) throws Exception {
        int rtnVal = 0;
        LocalDBConnection conn = null;
        try {
            conn = new LocalDBConnection();
            StringBuffer querySb = new StringBuffer("SELECT count(seq) cnt from zzim");
            querySb.append(" WHERE seq_nitem=" + seqNitem);
            log.debug("LOCALDB-QUERY------------------------------------------------------------------------------");
            log.debug(querySb);
            ResultSet rset = conn.executeQuery(querySb.toString());

            if (rset != null && rset.next()) {
                rtnVal = rset.getInt("cnt");
            }
        } catch (Exception e) {
            log.error("* 프로그램 수행중 에러 발생");
            log.error(ExceptionUtil.getExceptionInfo(e));
            throw e;
        } finally {
            if (conn != null) try { conn.close(); } catch (Exception ignored) {}
        }
        
        return rtnVal;
    }
    
    public List<JSONObject> getZZims(String seqNitem) throws Exception {
        
        List<JSONObject> zzims = null;
        LocalDBConnection conn = null;
        try {
            conn = new LocalDBConnection();
            StringBuffer querySb = new StringBuffer("SELECT");
            querySb.append(" z.seq seq,");
            querySb.append(" z.seq_user seq_user,");
            querySb.append(" u.juname juname,");
            querySb.append(" z.zzim_qty zzim_qty,");
            querySb.append(" z.insert_time insert_time,");
            querySb.append(" z.shared shared");
            querySb.append(" FROM zzim z, user u");
            querySb.append(" WHERE z.seq_user = u.seq AND seq_nitem =" + seqNitem);
            log.debug("LOCALDB-QUERY------------------------------------------------------------------------------");
            log.debug(querySb);
            ResultSet rset = conn.executeQuery(querySb.toString());

            JSONObject jsonObj = null;
            if (rset != null) {
                zzims = new ArrayList<JSONObject>();
                while (rset.next()) {
                    jsonObj = new JSONObject();
                    jsonObj.put("seq", rset.getInt("seq"));
                    jsonObj.put("seq_user", rset.getInt("seq_user"));
                    jsonObj.put("juname", rset.getString("juname"));
                    jsonObj.put("zzim_qty", rset.getInt("zzim_qty"));
                    jsonObj.put("insert_time", rset.getLong("insert_time"));
                    jsonObj.put("shared", rset.getInt("shared"));
                    zzims.add(jsonObj);
                }
            }
            return zzims;
        } catch (Exception e) {
            log.error("* 프로그램 수행중 에러 발생");
            log.error(ExceptionUtil.getExceptionInfo(e));
            throw e;
        } finally {
            if (conn != null) try { conn.close(); } catch (Exception ignored) {}
        }
    }
    
}
