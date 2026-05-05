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

public class BoxDataAccessObject extends CommonDataAccessObject {

    /**
     * 서버 환경 변경시 log4j.properties 설정 정보도 함께 변경 필요
     */
	private Logger log = LogManager.getLogger(BoxDataAccessObject.class);

    public BoxDataAccessObject() {;}

    /**
     * 보관함 정보를 반환
     * @param seqBox
     * @return
     * @throws Exception
     */
    public JSONObject getBox(String seqBox) throws Exception {
        LocalDBConnection conn = null;
        try {
            conn = new LocalDBConnection();
            StringBuffer querySb = new StringBuffer("SELECT seq, name, details, type, hide_after, seq_store from box");
            querySb.append(" WHERE seq = "+seqBox);
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
                    jsonObj.put("type", rset.getString("type"));
                    jsonObj.put("hide_after", rset.getString("hide_after"));
                    jsonObj.put("seq_store", rset.getInt("seq_store"));
                }
                return jsonObj;
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
     * 등록대기 보관함 정보를 반환
     * @param seqStore
     * @return
     * @throws Exception
     */
    public JSONObject getPendBox(String seqStore) throws Exception {
        LocalDBConnection conn = null;
        try {
            conn = new LocalDBConnection();
            StringBuffer querySb = new StringBuffer("SELECT seq, name, details, type, seq_store from box");
            querySb.append(" WHERE seq_store = " + seqStore);
            querySb.append(" AND type = -1");
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
                }
                return jsonObj;
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
     * 박스 seq 목록으로 박스 정보 목록을 반환
     * @param seqBoxes
     * @return
     * @throws Exception
     */
    public List<JSONObject> getBoxes(int[] seqBoxes) throws Exception {
        
        if (seqBoxes == null)
            return null;
        
        LocalDBConnection conn = null;
        try {
            conn = new LocalDBConnection();
            StringBuffer querySb = new StringBuffer("SELECT seq, name, type, seq_store from box");
            querySb.append(" WHERE seq in (");
            for (int i = 0; i < seqBoxes.length; i++) {
                querySb.append(seqBoxes[i]);
                if (i < seqBoxes.length - 1) {
                    querySb.append(",");
                }
            }
            querySb.append(")");
            log.debug("LOCALDB-QUERY------------------------------------------------------------------------------");
            log.debug(querySb);
            ResultSet rset = conn.executeQuery(querySb.toString());

            if (rset != null) {
                List<JSONObject> objList = new ArrayList<JSONObject>();
                JSONObject jsonObj = null;
                while(rset.next()) {
                    jsonObj = new JSONObject();
                    jsonObj.put("seq", rset.getInt("seq"));
                    jsonObj.put("name", rset.getString("name"));
                    jsonObj.put("type", rset.getString("type"));
                    jsonObj.put("seq_store", rset.getInt("seq_store"));
                    objList.add(jsonObj);
                }
                return objList;
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
     * 시스템의 모든 보관함을 반환
     * @return
     * @throws Exception
     */
    public List<JSONObject> getAllBoxes() throws Exception {
        
        LocalDBConnection conn = null;
        try {
            conn = new LocalDBConnection();
            StringBuffer querySb = new StringBuffer("SELECT seq, name, type, hide_after, seq_store from box");
            log.debug("LOCALDB-QUERY------------------------------------------------------------------------------");
            log.debug(querySb);
            ResultSet rset = conn.executeQuery(querySb.toString());

            if (rset != null) {
                List<JSONObject> objList = new ArrayList<JSONObject>();
                JSONObject jsonObj = null;
                while(rset.next()) {
                    jsonObj = new JSONObject();
                    jsonObj.put("seq", rset.getInt("seq"));
                    jsonObj.put("name", rset.getString("name"));
                    jsonObj.put("type", rset.getString("type"));
                    jsonObj.put("hide_after", rset.getInt("hide_after"));
                    jsonObj.put("seq_store", rset.getInt("seq_store"));
                    objList.add(jsonObj);
                }
                return objList;
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
     * name으로 해당되는 store 내의 박스를 조회  
     * @param seqStore
     * @param name null is all
     * @return
     * @throws Exception
     */
    public JSONObject getStoreBox(String seqStore, String boxName) throws Exception {

        LocalDBConnection conn = null;
        try {
            conn = new LocalDBConnection();
            StringBuffer querySb = new StringBuffer("SELECT seq, name from box");
            querySb.append(" WHERE seq_store =" + seqStore);
            querySb.append(" AND name ='" + boxName + "'");
            log.debug("LOCALDB-QUERY------------------------------------------------------------------------------");
            log.debug(querySb);
            ResultSet rset = conn.executeQuery(querySb.toString());

            JSONObject jsonObj = null;
            if (rset != null) {
                while (rset.next()) {
                    jsonObj = new JSONObject();
                    jsonObj.put("seq", rset.getInt("seq"));
                    jsonObj.put("name", rset.getString("name"));
                }
                return jsonObj;
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
     * store 내의 모든 박스를 조회  
     * @param seqStore
     * @param name null is all
     * @return List<JSONObject>
     * @throws Exception
     */
    public List<JSONObject> getStoreBoxes(String seqStore) throws Exception {
        
        List<JSONObject> boxes = null;
        LocalDBConnection conn = null;
        try {
            conn = new LocalDBConnection();
            StringBuffer querySb = new StringBuffer("SELECT seq, name, details, type from box");
            querySb.append(" WHERE seq_store =" + seqStore);
//            querySb.append(" ORDER BY name");
            log.debug("LOCALDB-QUERY------------------------------------------------------------------------------");
            log.debug(querySb);
            ResultSet rset = conn.executeQuery(querySb.toString());

            if (rset != null) {
                boxes = new ArrayList<JSONObject>();
                JSONObject jsonObj = null;
                while (rset.next()) {
                    jsonObj = new JSONObject();
                    jsonObj.put("seq", rset.getInt("seq"));
                    jsonObj.put("name", rset.getString("name"));
                    jsonObj.put("details", rset.getString("details"));
                    jsonObj.put("type", rset.getInt("type"));
                    boxes.add(jsonObj);
                }
            }
            return boxes;
        } catch (Exception e) {
            log.error("* 프로그램 수행중 에러 발생");
            log.error(ExceptionUtil.getExceptionInfo(e));
            throw e;
        } finally {
            conn.close();
        }
    }
    
    /**
     * 저장소의 모든 보관함의 seq를 반환
     * @param seqStore
     * @return
     * @throws Exception
     */
    public int[] getAllSeq(String seqStore) throws Exception {
        
        LocalDBConnection conn = null;
        try {
            conn = new LocalDBConnection();
            StringBuffer querySb = new StringBuffer("SELECT seq from box");
            querySb.append(" WHERE seq_store =" + seqStore);
            log.debug("LOCALDB-QUERY------------------------------------------------------------------------------");
            log.debug(querySb);
            ResultSet rset = conn.executeQuery(querySb.toString());

            if (rset != null) {
                int rsetSize = this.getResultSetSize(rset);
                if (rsetSize > 0) {
                    int[] rtnVal = new int[rsetSize];
                    int i = 0;
                    do {
                        rtnVal[i] = rset.getInt("seq");
                        i++;
                    } while (rset.next());
                    return rtnVal;
                }
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
     * 시스템의 모든 등록대기 보관함의 seq를 반환
     * 
     * @return
     * @throws Exception
     */
    public List<JSONObject> getAllStandbyBoxes() throws Exception {

        List<JSONObject> boxes = null;
        LocalDBConnection conn = null;
        try {
            conn = new LocalDBConnection();
            StringBuffer querySb = new StringBuffer("SELECT seq, seq_store from box");
            querySb.append(" WHERE type=-1");
            log.debug("LOCALDB-QUERY------------------------------------------------------------------------------");
            log.debug(querySb);
            ResultSet rset = conn.executeQuery(querySb.toString());

            if (rset != null) {
                boxes = new ArrayList<JSONObject>();
                JSONObject jsonObj = null;
                while (rset.next()) {
                    jsonObj = new JSONObject();
                    jsonObj.put("seq", rset.getInt("seq"));
                    jsonObj.put("seq_store", rset.getInt("seq_store"));
                    boxes.add(jsonObj);
                }
            }
            return boxes;
        } catch (Exception e) {
            log.error("* 프로그램 수행중 에러 발생");
            log.error(ExceptionUtil.getExceptionInfo(e));
            throw e;
        } finally {
            conn.close();
        }
    }
    
    /**
     * 해당 box 의 소유자 seq 를 반환
     * @param seqBox
     * @return
     * @throws Exception
     */
    public int getSeqOwner(String seqBox) throws Exception {
        LocalDBConnection conn = null;
        try {
            conn = new LocalDBConnection();
            StringBuffer querySb = new StringBuffer("SELECT s.seq_owner seq_owner");
            querySb.append(" FROM box b, store s");
            querySb.append(" WHERE b.seq_store = s.seq");
            querySb.append(" AND b.seq = " + seqBox);
            log.debug("LOCALDB-QUERY------------------------------------------------------------------------------");
            log.debug(querySb);
            ResultSet rset = conn.executeQuery(querySb.toString());

            if (rset != null && rset.next()) {
                return rset.getInt("seq_owner");
            }
            return -1;
        } catch (Exception e) {
            log.error("* 프로그램 수행중 에러 발생");
            log.error(ExceptionUtil.getExceptionInfo(e));
            throw e;
        } finally {
            conn.close();
        }
    }

    /**
     * 저장소에 박스를 추가
     * 
     * @param name
     * @param details
     * @param isDefbox (1 or 0, 스토어를 신규 등록시 기본박스를 생성 할 수 있다)
     * @param seqStore
     * @return 추가된 box의 seq
     * @throws Exception
     */
    public int insert(String name, String details, String isDefbox, String seqStore) throws Exception {

        int seqBox = -1;
        
        StringBuffer querySb = new StringBuffer();
        querySb.append("INSERT INTO box (");
        querySb.append("name,");
        querySb.append("details,");
        if (!JinieboxUtil.isEmpty(isDefbox))
            querySb.append("type,");
        querySb.append("seq_store");
        querySb.append(") values (");
        querySb.append("'" + name + "', ");
        querySb.append("'" + details + "', ");
        if (!JinieboxUtil.isEmpty(isDefbox))
            querySb.append(isDefbox + ", ");
        querySb.append(seqStore);
        querySb.append(")");
        log.debug("LOCALDB-QUERY------------------------------------------------------------------------------");
        log.debug(querySb);

        LocalDBConnection conn = null;
        try {
            conn = new LocalDBConnection();
            conn.txOpen();
            conn.txExecuteUpdate(querySb.toString());
            seqBox = this.getLastInsertSeq(conn);
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

        return seqBox;
    }
    
	/**
     * seq를 검색하여 box 정보를 갱신한다<br/>
     * parameter 의 값을 null check 하여 값이 없는 경우엔 설정하지 않는다
	 * 
	 * @param seq
	 * @param name
	 * @param details
	 * @param isDefbox
	 * @param seqStore
	 * @throws Exception
	 */
	public void update(String seq, String name, String details, String isDefbox, String seqStore) throws Exception {

		JSONObject boxJson = this.getBox(seq);
		if (boxJson != null) {
			StringBuffer querySb = new StringBuffer();
			querySb.append("UPDATE box SET");
			querySb.append(" seq=" + seq);
		    if (!JinieboxUtil.isEmpty(name))
				querySb.append(", name='" + name + "'");
		    if (!JinieboxUtil.isEmpty(details))
				querySb.append(", details='" + details + "'");
			if (!JinieboxUtil.isEmpty(isDefbox) && NumberUtil.isNumber(isDefbox))
			    querySb.append(", type=" + isDefbox);
			if (NumberUtil.isNumber(seqStore))
				querySb.append(", seq_store=" + seqStore);
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
				conn.close();
			}
		}
	}
	
    /**
     * 보관함에서의 아이템 저장 일수 설정
     * 
     * @param seqBox
     * @param delAfter
     * @throws Exception
     */
    public void setItemDeleteAfterDays(String seqBox, int delAfter) throws Exception {
        LocalDBConnection conn = null;
        try {
            conn = new LocalDBConnection();
            conn.txOpen();

            StringBuffer querySb = new StringBuffer();
            querySb.append("UPDATE box set");
            querySb.append(" del_after=" + delAfter);
            querySb.append(" where seq=" + seqBox);

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
	 * box를 삭제한다
	 * @param seq
	 * @throws Exception
	 */
	public void delete(String seq) throws Exception {

		JSONObject boxJson = this.getBox(seq);
		if (boxJson != null) {
			StringBuffer querySb = new StringBuffer();
			querySb.append("DELETE from box");
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
				conn.close();
			}
		}
	}
    
    /**
     * 박스의 아이템을 모두 삭제
     * @param seqBox
     * @return
     * @throws Exception
     */
    public boolean truncate(String seqBox) throws Exception {

        StringBuffer querySb = new StringBuffer();
        querySb.append("DELETE FROM item");
        querySb.append(" WHERE seq_box=" + seqBox);
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

}
