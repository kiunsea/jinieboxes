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
import com.omnibuscode.utils.NumberUtil;

public class NanumitemDataAccessObject extends CommonDataAccessObject {

    /**
     * 서버 환경 변경시 log4j.properties 설정 정보도 함께 변경 필요
     */
	private Logger log = LogManager.getLogger(NanumitemDataAccessObject.class);

    public NanumitemDataAccessObject() {;}

    /**
     * 나눔박스에 나눔아이템을 추가
     * 
     * @param seq_nanum
     * @param seq_item
     * @return
     * @throws Exception
     */
    public int insert(String seq_nanum, String seq_item, String detail) throws Exception {

        int seqNanum = -1;
        
        StringBuffer querySb = new StringBuffer();
        querySb.append("INSERT INTO nanum_item (");
        querySb.append("seq_nanum");
        querySb.append(", seq_item");
        if (!JinieboxUtil.isEmpty(detail))
            querySb.append(", detail");
        querySb.append(") values (");
        querySb.append(seq_nanum);
        querySb.append(", " + seq_item);
        if (!JinieboxUtil.isEmpty(detail))
            querySb.append(",' " + detail + "'");
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
            conn.close();            
        }

        return seqNanum;
    }
    
    /**
     * 이미 등록되었는지 여부
     * 
     * @param seq_nanum
     * @param seq_item
     * @return
     * @throws Exception
     */
    public boolean exist(String seq_nanum, String seq_item) throws Exception {
        LocalDBConnection conn = null;
        try {
            conn = new LocalDBConnection();
            StringBuffer querySb = new StringBuffer("SELECT seq from nanum_item");
            querySb.append(" WHERE seq_nanum =" + seq_nanum + " AND seq_item = "+seq_item);
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
            conn.close();
        }
    }
    
    public int getItemCount(String seqNanum) throws Exception {
        int rtnVal = 0;
        LocalDBConnection conn = null;
        try {
            conn = new LocalDBConnection();
            StringBuffer querySb = new StringBuffer("SELECT count(seq) cnt from nanum_item");
            querySb.append(" WHERE seq_nanum=" + seqNanum);
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
            conn.close();
        }
        
        return rtnVal;
    }
    
    /**
     * 나눔박스의 아이템 목록을 반환한다
     * 
     * @param seqNanum
     * @return
     * @throws Exception
     */
    public List<JSONObject> getNanumItems(String seqNanum) throws Exception {
        
        List<JSONObject> items = null;
        
        LocalDBConnection conn = null;
        try {
            conn = new LocalDBConnection();
            StringBuffer querySb = new StringBuffer(
                    "SELECT n.seq seq, i.seq seq_i, i.name i_name, i.qty i_qty, i.insert_date i_insert_date, i.insert_time i_insert_time, i.expiry_date i_expiry_date, i.seq_box i_seq_box, n.detail n_detail, i.seq_user i_seq_user");
            querySb.append(" FROM nanum_item n, item i");
            querySb.append(" WHERE n.seq_item = i.seq");
            querySb.append(" AND n.seq_nanum = " + seqNanum);
            log.debug("LOCALDB-QUERY------------------------------------------------------------------------------");
            log.debug(querySb);
            ResultSet rset = conn.executeQuery(querySb.toString());

            if (rset != null) {
                items = new ArrayList<JSONObject>();
                JSONObject jsonObj = null;
                while (rset.next()) {
                    jsonObj = new JSONObject();
                    jsonObj.put("seq", rset.getInt("seq"));
                    jsonObj.put("seq_i", rset.getInt("seq_i"));
                    jsonObj.put("name", rset.getString("i_name"));
                    jsonObj.put("qty", rset.getInt("i_qty"));
                    jsonObj.put("insert_date", rset.getInt("i_insert_date"));
                    jsonObj.put("insert_time", rset.getLong("i_insert_time"));
                    jsonObj.put("expiry_date", rset.getInt("i_expiry_date"));
                    jsonObj.put("seq_box", rset.getInt("i_seq_box"));
                    jsonObj.put("seq_user", rset.getInt("i_seq_user"));
                    jsonObj.put("detail", rset.getString("n_detail"));
                    
                    items.add(jsonObj);
                }
                return items;
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
     * 지정된 아이템의 등록 정보를 삭제
     * 
     * @param seqNanum
     * @param seqItem
     * @return
     * @throws Exception
     */
    public boolean delete(String seqNanum, String seqItem) throws Exception {
        LocalDBConnection conn = null;
        try {
            conn = new LocalDBConnection();
            conn.txOpen();

            StringBuffer querySb = new StringBuffer();
            querySb.append("DELETE from nanum_item");
            querySb.append(" WHERE seq_item=" + seqItem);
            querySb.append(" AND seq_nanum=" + seqNanum);

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
        return true;
    }
    
    /**
     * 지정된 나눔박스의 등록 정보를 모두 삭제
     * 
     * @param seqNanum
     * @return 성공여부
     * @throws Exception
     */
    public boolean deleteSeqNanum(String seqNanum) throws Exception {
        LocalDBConnection conn = null;
        try {
            conn = new LocalDBConnection();
            conn.txOpen();

            StringBuffer querySb = new StringBuffer();
            querySb.append("DELETE from nanum_item");
            querySb.append(" WHERE seq_nanum=" + seqNanum);

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
        return true;
    }
    
    /**
     * 지정된 아이템의 등록 정보를 모두 삭제
     * 
     * @param seqItem
     * @return 성공여부
     * @throws Exception
     */
    public boolean deleteSeqItem(String seqItem) throws Exception {
        LocalDBConnection conn = null;
        try {
            conn = new LocalDBConnection();
            conn.txOpen();

            StringBuffer querySb = new StringBuffer();
            querySb.append("DELETE from nanum_item");
            querySb.append(" WHERE seq_item=" + seqItem);

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
        return true;
    }
}
