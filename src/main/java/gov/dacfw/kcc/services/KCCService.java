package gov.dacfw.kcc.services;

import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import gov.dacfw.kcc.model.CallData;

public class KCCService {
	private static final String DB_URL = "jdbc:mysql://localhost/testDB";
	private static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";
	private static final String USER_NAME = "guest";
	private static final String PASSWORD = "Guest123";
	private static final String CALL_ID = "call_id";
	private static final String LOCATION = "location";
	private static final String CROP = "crop";
	private static final String ANNOTATION = "annotation";
	private static final String DATE = "date";
	
	public KCCService(){
		
	}
	
	private Connection getConnection() throws ClassNotFoundException, SQLException{
		Class.forName(JDBC_DRIVER);
		return DriverManager.getConnection(DB_URL, USER_NAME, PASSWORD);
	}
	
	public List<CallData> getData(String location, String crop, String beginDate, String endDate) {
		// TODO Auto-generated method stub
		try {
			Connection conn = getConnection();
			List<CallData> list = new ArrayList<CallData>();
			// After connection has been established, form the sql query
			String sql = "SELECT * FROM KCCDATA";
			if ((location != null) || (crop != null) || (beginDate != null) || (endDate != null))
				sql = sql + " WHERE ";
			if (location != null)
				sql = sql + "location = '" + location + "' AND ";
			if (crop != null)
				sql = sql + "crop LIKE '" + crop +"%' AND ";
			if (beginDate != null){
				if (endDate == null)
					sql = sql + "date >= '" + beginDate +"'"; 
				else
					sql = sql+ "date BETWEEN '" + beginDate + "' AND '" + endDate + "'";
			} 
			sql = sql + ";";  
			// Now execute the sql query
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery(sql);
			CallData cd;
			while (rs.next()){
				cd = new CallData();
				cd.setCall_id(rs.getInt(CALL_ID));
				cd.setAnnotation(rs.getString(ANNOTATION));
				cd.setCrop(rs.getString(CROP));
				cd.setLocation(rs.getString(LOCATION));
				cd.setDate(rs.getString(DATE));
				list.add(cd);
			}
			rs.close();
			conn.close();
			return list;
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return null;
	}
	/*private int getCountOfRows(Connection conn, int year, int month) throws SQLException{
		String sql = "SELECT count(call_id) FROM KCCDATA";
		if (year == 0)
			sql = sql + ";";
		else
			sql = sql + " WHERE date BETWEEN '"+year+"-"+month+"-01' AND '"+year+"-"+month+"-01';";
		Statement stmt = conn.createStatement();
		ResultSet rs = stmt.executeQuery(sql);
		return rs.getInt(2);
	}
	
	public void getAlert(String crop, String[] location){
		try {
			Connection conn = getConnection();
			// get total number of rows in this year
			
			// After a connection has been established, make an sql query
			String sql = "SELECT ";
		} catch (ClassNotFoundException | SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
*/
}
