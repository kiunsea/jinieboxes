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
import com.omnibuscode.utils.PropertiesUtil;

public class ItemDataAccessObject extends CommonDataAccessObject {

	/**
	 * 서버 환경 변경시 log4j.properties 설정 정보도 함께 변경 필요
	 */
//    private Log log = LogFactory.getLog(ItemDataAccessObject.class);
	private Logger log = LogManager.getLogger(ItemDataAccessObject.class);

	public ItemDataAccessObject() {
		;
	}

    /**
     * 아이템을 등록한다.
     * 
     * @param seqUser
     * @param seqBox
     * @param itemName
     * @param qty 수량
     * @param expd 만료일자
     * @param insd 등록일자
     * @param seqOrder 구매정보
     * @return seqItem
     * @throws Exception
     */
    public int insertItem(int seqUser, int seqBox, String itemName, int qty, int expd, int insd, int seqOrder) throws Exception {

        int seqItem = -1;
        
        StringBuffer querySb = new StringBuffer();
        querySb.append("INSERT INTO item (");
        querySb.append("name");
        querySb.append(",qty");
        if (expd > 0)
            querySb.append(",expiry_date");
        querySb.append(",insert_date");
        querySb.append(",insert_time");
        querySb.append(",seq_box");
        querySb.append(",seq_user");
        if (seqOrder > 0)
            querySb.append(",seq_jbgorder");
        querySb.append(")");
        querySb.append(" VALUES ");
        querySb.append("(");
        querySb.append("'" + itemName + "'");
        querySb.append("," + qty);
        if (expd > 0)
            querySb.append("," + expd);
        querySb.append("," + insd);
        querySb.append("," + System.currentTimeMillis());
        querySb.append("," + seqBox);
        querySb.append("," + seqUser);
        if (seqOrder > 0)
            querySb.append("," + seqOrder);
        querySb.append(")");

        String query = querySb.toString();
        log.debug("LOCAL-QUERY------------------------------------------------------------------------------");
        log.debug(query);

        LocalDBConnection conn = null;
        try {
            conn = new LocalDBConnection();
            conn.txOpen();
            conn.txExecuteUpdate(query);
            seqItem = this.getLastInsertSeq(conn);
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
        
        return seqItem;
    }
	
	/**
	 * seq를 검색하여 item 정보를 갱신한다<br/>
	 * parameter 의 값을 null check 하여 값이 없는 경우엔 설정하지 않는다
	 * 
	 * @param seq
	 * @param name (null check)
	 * @param qty (null check)
	 * @param insd insert_date (null check)
	 * @param expd expiry_date (null check)
	 * @param seqBox (null check)
	 * @throws Exception
	 */
	public void updateItem(String seq, String name, String qty, String insd, String expd, String seqBox, int seqOrder) throws Exception {

		JSONObject itemJson = this.getItem(seq);
		if (itemJson != null) {
			StringBuffer querySb = new StringBuffer();
			querySb.append("UPDATE item SET");
			querySb.append(" seq=" + seq);
			if (name != null)
				querySb.append(", name='" + name + "'");
			if (NumberUtil.isNumber(qty))
				querySb.append(", qty=" + qty);
			if (NumberUtil.isNumber(insd))
				querySb.append(", insert_date=" + insd);
			if (NumberUtil.isNumber(expd))
				querySb.append(", expiry_date=" + expd);
			if (NumberUtil.isNumber(seqBox))
				querySb.append(", seq_box=" + seqBox);
			if (seqOrder > 0)
                querySb.append(", seq_jbgorder=" + seqOrder);
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
				conn.close();
			}
		}
	}

    /**
     * item 수량을 증감시킨다
     * 
     * @param seq
     * @param qty (양수이면 수량증가, 음수이면 수량감소)
     * @throws Exception
     */
    public void updateQty(String seq, int qty) throws Exception {

        JSONObject itemJson = this.getItem(seq);

        LocalDBConnection conn = null;
        try {
            conn = new LocalDBConnection();
            conn.txOpen();

            StringBuffer querySb = new StringBuffer();
            querySb.append("UPDATE item SET");
            querySb.append(" qty=" + (Integer.parseInt(itemJson.get("qty").toString()) + qty));
            querySb.append(" WHERE seq=" + seq);

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
     * 등록대기 일수가 초과한 아이템에 대해 box에서 등록해제 (seq_box = -1)
     * 
     * @param seqBox
     * @param standbyDays
     * @throws Exception
     */
    public void updateToClose(String seqBox, int standbyDays) throws Exception {

        int todayInt = Integer.parseInt(JinieboxUtil.getTodayString());

        LocalDBConnection conn = null;
        try {
            conn = new LocalDBConnection();
            conn.txOpen();

            StringBuffer querySb = new StringBuffer();
            querySb.append("UPDATE item SET");
            querySb.append(" hidden=1");
            querySb.append(" WHERE DATE_FORMAT(STR_TO_DATE(insert_date, '%Y%m%d') + INTERVAL " + standbyDays
                    + " DAY, '%Y%m%d') < CURDATE()");
            querySb.append(" AND seq_box=" + seqBox);

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

//	/**
//	 * box 내에서 아이템 이름으로 검색하여 item 수량을 증감시킨다 (당일 추가된 만료일이 동일한 아이템이 없는 경우 새롭게 insert 함)
//	 * 
//	 * @param seqUser
//	 * @param seqBox
//	 * @param itemName
//	 * @param qty  - 양수이면 수량증가, 음수이면 수량감소
//	 * @param expd - 0인 경우 반영안함
//	 * @param seqOrder - 쇼핑몰 구매정보
//	 * @return
//	 * @throws Exception
//	 */
//	public String updateBoxItemQty(int seqUser, int seqBox, String itemName, int qty, int expd, int seqOrder) throws Exception {
//
//        String today = JinieboxUtil.getTodayString();
//        String expdate = expd + "";
//        JSONObject itemJson = this.getTodayItem(seqBox, itemName, today, expdate);
//
//        StringBuffer querySb = new StringBuffer();
//        if (itemJson != null) {
//            querySb.append("UPDATE item SET");
//            querySb.append(" qty=" + (Integer.parseInt(itemJson.get("qty").toString()) + qty));
//            if (expd > 0)
//                querySb.append(", expiry_date=" + expd);
//            querySb.append(" WHERE seq=" + itemJson.get("seq"));
//        } else {
//            querySb.append("INSERT INTO item (");
//            querySb.append("name");
//            querySb.append(",qty");
//            if (expd > 0)
//                querySb.append(",expiry_date");
//            querySb.append(",insert_date");
//            querySb.append(",insert_time");
//            querySb.append(",seq_box");
//            querySb.append(",seq_user");
//            if (seqOrder > 0)
//                querySb.append(",seq_jbgorder");
//            querySb.append(")");
//            querySb.append(" VALUES ");
//            querySb.append("(");
//            querySb.append("'" + itemName + "'");
//            querySb.append("," + qty);
//            if (expd > 0)
//                querySb.append("," + expd);
//            querySb.append("," + today);
//            querySb.append("," + System.currentTimeMillis());
//            querySb.append("," + seqBox);
//            querySb.append("," + seqUser);
//            if (seqOrder > 0)
//                querySb.append("," + seqOrder);
//            querySb.append(")");
//        }
//
//		String query = querySb.toString();
//		log.debug("LOCAL-QUERY------------------------------------------------------------------------------");
//		log.debug(query);
//
//        String updatedSeq = null;
//        LocalDBConnection conn = null;
//		try {
//            if (itemJson != null) {
//                updatedSeq = itemJson.get("seq").toString();
//            } else {
//                updatedSeq = Integer.toString(this.getNextSeq("item"));
//            }
//			conn = new LocalDBConnection();
//			conn.txOpen();
//			conn.txExecuteUpdate(query);
//			conn.txCommit();
//		} catch (SQLException sqle) {
//			log.error("* 아이고!! ㅜ.ㅜ 데이터베이스 업데이트 에러 발생");
//			log.error(sqle.getMessage());
//			log.debug(ExceptionUtil.getExceptionInfo(sqle.getStackTrace()));
//			sqllog.error(ExceptionUtil.getExceptionInfo(e));
//			throw sqle;
//		} catch (Exception e) {
//			log.error("* 아이고!! ㅜ.ㅜ 데이터베이스 업데이트 에러 발생");
//			log.error(e.getMessage());
//			log.debug(ExceptionUtil.getExceptionInfo(e.getStackTrace()));
//			log.error(ExceptionUtil.getExceptionInfo(e));
//			conn.txRollBack();
//			throw e;
//		} finally {
//			conn.close();
//		}
//		
//        return updatedSeq;
//	}

	/**
	 * 저장소에 저장한 음식 종류를 반환한다
	 * 
	 * @param seqRoom
	 * @return
	 * @throws Exception
	 */
	public List<String> getStoreItemNames(String seqStore) throws Exception {

		List<String> names = null;
		LocalDBConnection conn = null;
		try {
			conn = new LocalDBConnection();
			StringBuffer querySb = new StringBuffer("SELECT i.name iname");
			querySb.append(" FROM item i, box b");
			querySb.append(" WHERE b.seq_store =" + seqStore);
			querySb.append(" AND i.seq_box = b.seq");
			querySb.append(" GROUP BY iname");
			querySb.append(" ORDER BY iname");
			log.debug("LOCALDB-QUERY------------------------------------------------------------------------------");
			log.debug(querySb);
			ResultSet rset = conn.executeQuery(querySb.toString());

			if (rset != null) {
				names = new ArrayList<String>();
				while (rset.next()) {
					names.add(rset.getString("iname"));
				}
				return names;
			} else {
			    return null;
			}
		} catch (Exception e) {
			log.error("* 프로그램 수행중 에러 발생");
			log.error(ExceptionUtil.getExceptionInfo(e));
			throw e;
		} finally {
			conn.close();
		}
	}

	/**
	 * 저장소에 저장한 아이템 정보를 반환한다
	 * 
	 * @param seqStore
	 * @param boxName  (null:모든 보관함)
	 * @param itemName (null:모든 아이템)
	 * @return
	 * @throws Exception
	 */
	public List<JSONObject> getStoreItems(String seqStore, String boxName, String itemName) throws Exception {

		List<JSONObject> items = null;
		LocalDBConnection conn = null;
		try {
			conn = new LocalDBConnection();
			StringBuffer querySb = new StringBuffer(
					"SELECT i.seq iseq, i.name iname, i.qty iqty, i.insert_date iinsert_date, i.expiry_date iexpiry_date, i.insert_time iinsert_time, i.hidden ihidden");
			querySb.append(", b.seq bseq, b.name bname, b.details bdetails");
			querySb.append(" FROM item i, box b");
			querySb.append(" WHERE b.seq_store =" + seqStore);
			querySb.append(" AND i.seq_box = b.seq");
			if (this.isNotNull(boxName))
				querySb.append(" AND b.name = '" + boxName + "'");
			if (this.isNotNull(itemName))
				querySb.append(" AND i.name = '" + itemName + "'");
			querySb.append(" AND i.hidden != 1");
			querySb.append(" ORDER BY bname");
			log.debug("LOCALDB-QUERY------------------------------------------------------------------------------");
			log.debug(querySb);
			ResultSet rset = conn.executeQuery(querySb.toString());

			if (rset != null) {
				items = new ArrayList<JSONObject>();
				JSONObject jsonObj = null;
				while (rset.next()) {
					jsonObj = new JSONObject();
					jsonObj.put("seq", rset.getInt("iseq"));
					jsonObj.put("name", rset.getString("iname"));
					jsonObj.put("qty", rset.getInt("iqty"));
					jsonObj.put("insdate", rset.getInt("iinsert_date"));
					jsonObj.put("expdate", rset.getInt("iexpiry_date"));
					jsonObj.put("bseq", rset.getString("bseq"));
					jsonObj.put("bname", rset.getString("bname"));
					jsonObj.put("hidden", rset.getString("ihidden"));
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
			conn.close();
		}
	}

	/**
	 * seq로 아이템 정보를 반환한다
	 * 
	 * @param seq
	 * @return
	 * @throws Exception
	 */
	public JSONObject getItem(String seq) throws Exception {

	    LocalDBConnection conn = null;
		try {
			conn = new LocalDBConnection();
			StringBuffer querySb = new StringBuffer("SELECT seq, name, qty, insert_date, expiry_date, seq_box, hidden from item");
			querySb.append(" WHERE seq =" + seq);
			log.debug("LOCALDB-QUERY------------------------------------------------------------------------------");
			log.debug(querySb);
			ResultSet rset = conn.executeQuery(querySb.toString());

			JSONObject jsonObj = null;
			if (rset != null) {
				while (rset.next()) {
					jsonObj = new JSONObject();
					jsonObj.put("seq", rset.getInt("seq"));
					jsonObj.put("name", rset.getString("name"));
					jsonObj.put("qty", rset.getInt("qty"));
					jsonObj.put("insert_date", rset.getInt("insert_date"));
					jsonObj.put("expiry_date", rset.getInt("expiry_date"));
					jsonObj.put("seq_box", rset.getInt("seq_box"));
					jsonObj.put("hidden", rset.getInt("hidden"));
				}
				return jsonObj;
			} else {
			    return null;
			}
		} catch (Exception e) {
			log.error("* 프로그램 수행중 에러 발생");
			log.error(ExceptionUtil.getExceptionInfo(e));
			throw e;
		} finally {
			conn.close();
		}
	}
	
	/**
	 * 사용자 시퀀스가 같고 이름에 검색어를 포함한 아이템을 조회하여 정보를 반환한다
	 * @param seqUser
	 * @param itemName
	 * @return
	 * @throws Exception
	 */
	public List<JSONObject> getUserItems(String seqUser, String itemName) throws Exception {
	    List<JSONObject> items = null;
        LocalDBConnection conn = null;
        try {
            conn = new LocalDBConnection();
            StringBuffer querySb = new StringBuffer(
                    "SELECT seq, name, qty, insert_date, expiry_date, seq_box, hidden from item");
            querySb.append(" WHERE seq_user = " + seqUser);
//          querySb.append(" ORDER BY insert_date DESC");
            if (this.isNotNull(itemName))
                querySb.append(" AND name like '%" + itemName + "%'");
            querySb.append(" AND hidden != 1");
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
                    jsonObj.put("qty", rset.getInt("qty"));
                    jsonObj.put("insert_date", rset.getInt("insert_date"));
                    jsonObj.put("expiry_date", rset.getInt("expiry_date"));
                    jsonObj.put("seq_box", rset.getInt("seq_box"));
                    jsonObj.put("hidden", rset.getInt("hidden"));
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
            conn.close();
        }
	}

	/**
	 * 보관함 시퀀스와 아이템 이름으로 조회하여 정보를 반환한다
	 * 
	 * @param seqBox
	 * @param itemName (null:모든 아이템)
	 * @return List<JSONObject>
	 * @throws Exception
	 */
	public List<JSONObject> getBoxItems(String seqBox, String itemName) throws Exception {

		List<JSONObject> items = null;
		LocalDBConnection conn = null;
		try {
			conn = new LocalDBConnection();
			StringBuffer querySb = new StringBuffer(
					"SELECT seq, name, qty, insert_date, expiry_date, seq_box, hidden from item");
			querySb.append(" WHERE seq_box = " + seqBox);
			querySb.append(" AND hidden != 1");
//			querySb.append(" ORDER BY insert_date DESC");
			if (this.isNotNull(itemName))
				querySb.append(" AND name = '" + itemName + "'");
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
					jsonObj.put("qty", rset.getInt("qty"));
					jsonObj.put("insert_date", rset.getInt("insert_date"));
					jsonObj.put("expiry_date", rset.getInt("expiry_date"));
					jsonObj.put("seq_box", rset.getInt("seq_box"));
					jsonObj.put("hidden", rset.getInt("hidden"));
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
			conn.close();
		}
	}

    /**
     * 기준일자에 따라 만료일자(expiry_date)가 가깝거나 지난 아이템들의 갯수를 사용자 별로 반환 
     * @param baseDate 기준일자
     * @return
     * @throws Exception
     */
    public List<JSONObject> getExpiryInfo(String baseDate) throws Exception {

        List<JSONObject> items = null;
        LocalDBConnection conn = null;
        try {
            conn = new LocalDBConnection();
            StringBuffer querySb = new StringBuffer(
                    "SELECT count(i.seq) cnt, u.seq useq");
            querySb.append(" FROM item i, box b, store s, user u");
            querySb.append(" WHERE i.expiry_date < " + baseDate);
            querySb.append(" AND i.seq_box = b.seq");
            querySb.append(" AND b.seq_store = s.seq");
            querySb.append(" AND s.seq_owner = u.seq");
            querySb.append(" AND i.hidden != 1");
            querySb.append(" GROUP BY u.seq");
            log.debug("LOCALDB-QUERY------------------------------------------------------------------------------");
            log.debug(querySb);
            ResultSet rset = conn.executeQuery(querySb.toString());

            if (rset != null) {
                items = new ArrayList<JSONObject>();
                JSONObject jsonObj = null;
                while (rset.next()) {
                    jsonObj = new JSONObject();
                    jsonObj.put("cnt", rset.getString("cnt"));
                    jsonObj.put("useq", rset.getString("useq"));
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
            conn.close();
        }
    }
	
    /**
     * 기준일자에 따라 만료일자(expiry_date)가 가깝거나 지난 아이템들의 목록을 반환 
     * @param baseDate 기준일자
     * @return
     * @throws Exception
     */
    public List<JSONObject> getCloseToExpiry(String baseDate) throws Exception {

        List<JSONObject> items = null;
        LocalDBConnection conn = null;
        try {
            conn = new LocalDBConnection();
            StringBuffer querySb = new StringBuffer(
                    "SELECT i.seq iseq, i.name iname, i.qty iqty, i.insert_date iinsert_date, i.expiry_date iexpiry_date, i.insert_time iinsert_time");
            querySb.append(", b.seq bseq, b.name bname");
            querySb.append(", u.seq useq, u.juid ujuid");
            querySb.append(" FROM item i, box b, store s, user u");
            querySb.append(" WHERE i.expiry_date < " + baseDate);
            querySb.append(" AND i.seq_box = b.seq");
            querySb.append(" AND b.seq_store = s.seq");
            querySb.append(" AND s.seq_owner = u.seq");
            querySb.append(" ORDER BY ujuid");
            log.debug("LOCALDB-QUERY------------------------------------------------------------------------------");
            log.debug(querySb);
            ResultSet rset = conn.executeQuery(querySb.toString());

            if (rset != null) {
                items = new ArrayList<JSONObject>();
                JSONObject jsonObj = null;
                while (rset.next()) {
                    jsonObj = new JSONObject();
                    jsonObj.put("seq", rset.getInt("iseq"));
                    jsonObj.put("name", rset.getString("iname"));
                    jsonObj.put("qty", rset.getInt("iqty"));
                    jsonObj.put("insdate", rset.getInt("iinsert_date"));
                    jsonObj.put("expdate", rset.getInt("iexpiry_date"));
                    jsonObj.put("bseq", rset.getString("bseq"));
                    jsonObj.put("bname", rset.getString("bname"));
                    jsonObj.put("useq", rset.getString("useq"));
                    jsonObj.put("ujuid", rset.getString("ujuid"));
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
            conn.close();
        }
    }
    
	/**
	 * 아이템 시퀀스로 삭제
	 * @param seqItem
	 * @return
	 * @throws Exception
	 */
	public boolean deleteItem(String seqItem) throws Exception {
	    LocalDBConnection conn = null;
		try {
			conn = new LocalDBConnection();
			conn.txOpen();

			StringBuffer querySb = new StringBuffer();
			querySb.append("DELETE from item");
			querySb.append(" WHERE seq=" + seqItem);

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
	 * 해당 박스에서 아이템 이름으로 삭제
	 * @param seqBox
	 * @param name
	 * @return
	 * @throws Exception
	 */
	public boolean deleteItem(String seqBox, String name) throws Exception {
	    LocalDBConnection conn = null;
		try {
			conn = new LocalDBConnection();
			conn.txOpen();

			StringBuffer querySb = new StringBuffer();
			querySb.append("DELETE from item");
			querySb.append(" WHERE seq_box =" + seqBox + " AND name = '" + name + "'");

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

//    
//    /**
//     * 전달받은 box seq 목록내에서 item이 존재하는 box을 찾는다
//     * @param seqRooms
//     * @param itemName
//     * @return
//     * @throws Exception
//     */
//    public int[] searchRoom(int[] seqRooms, String itemName) throws Exception {
//        try {
//            conn = new LocalDBConnection();
//            StringBuffer querySb = new StringBuffer("SELECT seq_box from item");
//            querySb.append(" WHERE name ='" + itemName + "'");
//            querySb.append(" AND qty > 0");
//            querySb.append(" AND seq_box in (");
//            for (int i = 0; i < seqRooms.length; i++) {
//                querySb.append(seqRooms[i]);
//                if (i < seqRooms.length - 1) {
//                    querySb.append(",");
//                }
//            }
//            querySb.append(")");
//            querySb.append(" GROUP BY seq_box");
//            log.debug("LOCALDB-QUERY------------------------------------------------------------------------------");
//            log.debug(querySb);
//            ResultSet rset = conn.executeQuery(querySb.toString());
//
//            if (rset != null) {
//                int rsetSize = this.getResultSetSize(rset);
//                if (rsetSize > 0) {
//                    int[] rtnVal = new int[rsetSize];
//                    int i = 0;
//                    do {
//                        rtnVal[i] = rset.getInt("seq_box");
//                        i++;
//                    } while (rset.next());
//                    return rtnVal;
//                }
//            }
//            
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
//    
//    /**
//     * 전달받은 box seq 에서 item을 찾아 목록을 반환한다
//     * 
//     * @param seqStore
//     * @param itemName
//     * @return
//     * @throws Exception
//     */
//    public List<JSONObject> getItems(String seqStore, String itemName) throws Exception {
//        List<JSONObject> items = null;
//        try {
//            conn = new LocalDBConnection();
//            StringBuffer querySb = new StringBuffer(
//                    "SELECT seq, name, qty, insert_date, insert_time, seq_box from item");
//            querySb.append(" WHERE name ='" + itemName + "'");
//            querySb.append(" AND seq_box =" + seqBox);
//            querySb.append(" ORDER BY insert_date DESC");
//            log.debug("LOCALDB-QUERY------------------------------------------------------------------------------");
//            log.debug(querySb);
//            ResultSet rset = conn.executeQuery(querySb.toString());
//
//            JSONObject jsonObj = null;
//            if (rset != null) {
//                items = new ArrayList<JSONObject>();
//                while (rset.next()) {
//                    jsonObj = new JSONObject();
//                    jsonObj.put("seq", rset.getInt("seq"));
//                    jsonObj.put("name", rset.getString("name"));
//                    jsonObj.put("qty", rset.getInt("qty"));
//                    jsonObj.put("insert_date", rset.getInt("insert_date"));
//                    jsonObj.put("insert_time", rset.getLong("insert_time"));
//                    jsonObj.put("seq_box", rset.getInt("seq_box"));
//                    items.add(jsonObj);
//                }
//                return items;
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
//    
//    /**
//     * 전달받은 box seq 목록내에서 item을 찾아 목록을 반환한다
//     * @param seqRooms
//     * @param itemName
//     * @return
//     * @throws Exception
//     */
//    public List searchItems(int[] seqRooms, String itemName) throws Exception {
//        List<JSONObject> items = null;
//        try {
//            conn = new LocalDBConnection();
//            StringBuffer querySb = new StringBuffer("SELECT seq, name, qty, insert_date, insert_time, seq_box from item");
//            querySb.append(" WHERE name ='" + itemName + "'");
//            querySb.append(" AND seq_box in (");
//            for (int i = 0; i < seqRooms.length; i++) {
//                querySb.append(seqRooms[i]);
//                if (i < seqRooms.length - 1) {
//                    querySb.append(",");
//                }
//            }
//            querySb.append(")");
//            querySb.append(" ORDER BY insert_date DESC");
//            log.debug("LOCALDB-QUERY------------------------------------------------------------------------------");
//            log.debug(querySb);
//            ResultSet rset = conn.executeQuery(querySb.toString());
//
//            JSONObject jsonObj = null;
//            if (rset != null) {
//                items = new ArrayList<JSONObject>();
//                while (rset.next()) {
//                    jsonObj = new JSONObject();
//                    jsonObj.put("seq", rset.getInt("seq"));
//                    jsonObj.put("name", rset.getString("name"));
//                    jsonObj.put("qty", rset.getInt("qty"));
//                    jsonObj.put("insert_date", rset.getInt("insert_date"));
//                    jsonObj.put("insert_time", rset.getLong("insert_time"));
//                    jsonObj.put("seq_box", rset.getInt("seq_box"));
//                    items.add(jsonObj);
//                }
//                return items;
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
	 * 특정일에 저장한 아이템 정보를 반환한다 (갯수를 증감시키기 위함)
	 * 
	 * @param insertDate
	 * @param itemName
	 * @return 단일 아이템으로써 목록이 아님
	 * @throws Exception
	 */
	public JSONObject getSomedayItem(int seqBox, String itemName, String insertDate, String expiryDate) throws Exception {

	    LocalDBConnection conn = null;
		try {
			conn = new LocalDBConnection();
			StringBuffer querySb = new StringBuffer("SELECT seq, name, qty, insert_time, seq_box from item");
			querySb.append(" WHERE insert_date =" + insertDate);
            if (this.isNotNull(itemName))
                querySb.append(" AND name ='" + itemName + "'");
            if (this.isNotNull(expiryDate))
                querySb.append(" AND expiry_date =" + expiryDate);
            if (seqBox != -1)
                querySb.append(" AND seq_box =" + seqBox);
            log.debug("LOCALDB-QUERY------------------------------------------------------------------------------");
			log.debug(querySb);
			ResultSet rset = conn.executeQuery(querySb.toString());

			JSONObject jsonObj = null;
			if (rset != null && rset.next()) {
				jsonObj = new JSONObject();
				jsonObj.put("seq", rset.getInt("seq"));
				jsonObj.put("name", rset.getString("name"));
				jsonObj.put("qty", rset.getInt("qty"));
				jsonObj.put("insert_time", rset.getLong("insert_time"));
				jsonObj.put("seq_box", rset.getInt("seq_box"));
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
     * 등록대기 보관함의 아이템 목록을 반환
     * 
     * @param seqStore
     * @return
     * @throws Exception
     */
    public List<JSONObject> getStandbyItems(String seqStore) throws Exception {

        List<JSONObject> items = null;
        LocalDBConnection conn = null;
        try {
            conn = new LocalDBConnection();
            StringBuffer querySb = new StringBuffer("SELECT i.seq, i.name, i.qty, i.insert_date, i.seq_jbgorder");
            querySb.append(" FROM item i, box b");
            querySb.append(" WHERE b.seq_store = " + seqStore);
            querySb.append(" AND b.type = -1");
            querySb.append(" AND i.seq_box = b.seq");
            log.debug("LOCALDB-QUERY------------------------------------------------------------------------------");
            log.debug(querySb);
            ResultSet rset = conn.executeQuery(querySb.toString());

            JSONObject jsonObj = null;
            if (rset != null) {
                items = new ArrayList<JSONObject>();
                while (rset.next()) {
                    jsonObj = new JSONObject();
                    jsonObj.put("seq", rset.getInt("seq"));
                    jsonObj.put("name", rset.getString("name"));
                    jsonObj.put("qty", rset.getInt("qty"));
                    jsonObj.put("insert_date", rset.getInt("insert_date"));
                    jsonObj.put("seq_order", rset.getInt("seq_jbgorder"));
                    items.add(jsonObj);
                }
                return items;
            }
        } catch (Exception e) {
            log.error("* 프로그램 수행중 에러 발생");
            log.error(ExceptionUtil.getExceptionInfo(e));
            throw e;
        } finally {
            conn.close();
        }

        return null;
    }

}
