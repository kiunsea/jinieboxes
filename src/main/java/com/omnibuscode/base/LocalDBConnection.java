package com.omnibuscode.base;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * @author KIUNSEA
 *
 */
public class LocalDBConnection {

	// MySQL Connector/J 8.x 의 Driver 클래스 (구 com.mysql.jdbc.Driver 는 deprecated)
	private final String className = "com.mysql.cj.jdbc.Driver";

	private Connection conn = null;
	private Statement stmt = null;
	private ResultSet rset = null;

    private String ldbPass, ldbUrl, ldbUser;

	public LocalDBConnection() throws ClassNotFoundException, IllegalAccessException, InstantiationException, SQLException {

		// DB CONNECT
        try {
            Class.forName(className);

            this.ldbUrl = SafeProps.getString("LOCALDB_URL");
            this.ldbUser = SafeProps.getString("LOCALDB_USER");
            this.ldbPass = SafeProps.getString("LOCALDB_PASS");

            if (this.ldbUrl == null || this.ldbUser == null || this.ldbPass == null) {
                throw new SQLException(
                    "DB 설정이 누락되었습니다. JINIEBOX.PROPERTIES 의 LOCALDB_URL/LOCALDB_USER/LOCALDB_PASS 를 확인하세요. "
                    + "(현재: URL=" + (this.ldbUrl != null ? "set" : "MISSING")
                    + ", USER=" + (this.ldbUser != null ? "set" : "MISSING")
                    + ", PASS=" + (this.ldbPass != null ? "set" : "MISSING") + ")");
            }

            conn = DriverManager.getConnection(
                    this.ldbUrl,
                    this.ldbUser,
                    this.ldbPass);
        } catch (SQLException sqle) {
            if (conn != null) {
                try {
                    conn.close();
                } finally {
                    conn = null;
                }
            }
            throw sqle;
        }

		if (conn != null) {
			stmt = conn.createStatement();
		}
	}

	public ResultSet executeQuery(String query) throws SQLException {
		if (stmt != null) {
			rset = stmt.executeQuery(query);
		}
		return rset;
	}

	public void txOpen() throws SQLException {
		conn.setAutoCommit(false);
	}

	public void txClose() throws SQLException {
		conn.setAutoCommit(true);
	}

	public void txCommit() throws SQLException {
		conn.commit();
	}

	public void txRollBack() throws SQLException {
		conn.rollback();
	}

	public void txExecuteUpdate(String query) throws SQLException {
		if (stmt != null) {
			stmt.executeUpdate(query);
		}
	}

    public void close() throws SQLException {

        try {
            if (rset != null) {
                rset.close();
                rset = null;
            }
            if (stmt != null) {
                stmt.close();
                stmt = null;
            }
            if (conn != null) {
                conn.close();
                conn = null;
            }
        } catch (SQLException e) {
            throw e;
        } finally {// finally 를 이용해서 무조건 모든 리소스를 반환토록 한다.
            if (rset != null) {
                try { // close 시에도 exception 이 발생할 수 있기 때문에 각각도 예외처리를 한다.
                    rset.close();
                } catch (SQLException e) {
                    throw e;
                }
            }
            if (stmt != null) {
                try {
                    stmt.close();
                } catch (SQLException e) {
                    throw e;
                }
            }
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    throw e;
                }
            }
        }

    }
}
