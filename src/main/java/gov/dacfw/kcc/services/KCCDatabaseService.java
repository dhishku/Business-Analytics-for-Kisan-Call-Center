package gov.dacfw.kcc.services;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

import gov.dacfw.kcc.model.Alert;
import gov.dacfw.kcc.model.CallData;
import gov.dacfw.kcc.model.CallSummaryData;

public class KCCDatabaseService {
	private static final String DB_URL = "jdbc:mysql://localhost/testDB";
	private static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";
	private static final String USER_NAME = "guest";
	private static final String PASSWORD = "Guest123";
	private static final String CALL_ID = "call_id";
	private static final String LOCATION = "location";
	private static final String CROP = "crop";
	private static final String ANNOTATION = "annotation";
	private static final String DATE = "date";

	public KCCDatabaseService() {

	}

	private Connection getConnection() throws ClassNotFoundException, SQLException {
		Class.forName(JDBC_DRIVER);
		return DriverManager.getConnection(DB_URL, USER_NAME, PASSWORD);
	}

	/*
	 * This method queries the database and returns all the matching records for
	 * a particular crop, location, annotation and between the beginDate and the
	 * endDate.
	 * 
	 * If crop is null, it returns for all crops. If annotation is null, it
	 * returns for all annotations. If location is null, it returns for all
	 * locations. If both beginDate and endDate is null, it returns for all
	 * dates. If only endDate is null, it returns all data on and after the
	 * beginDate. If only beginDate is null, it returns all data on or before
	 * the endDate.
	 */
	public List<CallData> getData(String location, String crop, String annotation, String beginDate, String endDate) {
		// TODO Auto-generated method stub
		try {
			Connection conn = getConnection();
			List<CallData> list = new ArrayList<CallData>();
			// After connection has been established, form the sql query
			String sql = "SELECT * FROM KCCDATA";
			if ((location != null) || (crop != null) || (annotation != null) || (beginDate != null)
					|| (endDate != null)) {
				sql = sql + " WHERE ";
				if (location != null)
					sql = sql + "location LIKE '" + location + "%' AND ";
				if (crop != null)
					sql = sql + "crop LIKE '" + crop + "%' AND ";
				if (annotation != null)
					sql = sql + "annotation LIKE'" + annotation + "%' AND ";
				if (beginDate != null) {
					if (endDate == null)
						sql = sql + "date >= '" + beginDate + "'";
					else
						sql = sql + "date BETWEEN '" + beginDate + "' AND '" + endDate + "'";
				} else {
					if (endDate != null)
						sql = sql + "date <= '" + endDate + "'";
					else
						/*
						 * Both beginDate and endDate are null and there is no
						 * need to include date in the sql query. But we have
						 * reached here because one of the prior parameters was
						 * not null. This means we have added a " AND " to our
						 * sql query which we need to remove.
						 */
						sql = sql.substring(0, sql.length() - 5);
				}
			}

			sql = sql + ";";
			// Now execute the sql query
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery(sql);
			CallData cd;
			while (rs.next()) {
				cd = new CallData();
				cd.setCall_id(rs.getInt(CALL_ID));
				cd.setAnnotation(rs.getString(ANNOTATION));
				cd.setCrop(rs.getString(CROP));
				cd.setLocation(rs.getString(LOCATION));
				cd.setDate(rs.getString(DATE));
				list.add(cd);
			}
			stmt.close();
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

/*	
	 * This method will return a list of the calls made related to the
	 * particular crop or particular location or particular annotation 
	 * or a combination of any of these in the
	 * particular season and for a particular time window. For example,
	 * if you want all cotton calls in Gujarat for the previous season and alert
	 * window width is set to 15 days, then pass crop = cotton, location =
	 * Gujarat numOfPreviousSeasons = 1, alertWindowWidth = 15.
	 * 
	 * For the current season pass numOfPreviousSeasons = 0
	 
	private List<CallData> getCallsOfSeason(Connection conn, 
			String location, 
			String crop, 
			String annotation,
			int numOfPreviousSeasons, 
			int windowWidth) {

		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.DATE, -numOfPreviousSeasons*365);
		SimpleDateFormat dForm = new SimpleDateFormat("yyyy-MM-dd");
		String endDate = dForm.format(cal.getTime());
		cal.add(Calendar.DATE, - windowWidth);
		String beginDate = dForm.format(cal.getTime());

		return getData(location, crop, annotation, beginDate, endDate);
	}

	
	 * This method will return all calls in the database of a particular
	 * location or a particular crop or a particular annotation or a 
	 * combination of any of these for all the previous seasons in the form
	 * of an array. Each season's call will be stored as one element of the
	 * returned array. 
	 * Returns null if no call found in the whole database.
	 
	public List<List<CallData>> getCallsOfAllSeasons(String location, 
			String crop, 
			String annotation,
			int windowWidth) {
		try{
			Connection conn = getConnection();
			int numOfPreviousSeasons = 0;
			List<List<CallData>> results = new ArrayList<List<CallData>>();
			while (true) {
				List<CallData> oneSeasonResult = getCallsOfSeason(conn, location, crop, annotation, numOfPreviousSeasons,
						windowWidth);
				if (oneSeasonResult.size() == 0) {
					conn.close();
					return results;
				} else {
					numOfPreviousSeasons++;
					results.add(oneSeasonResult);
				}
			}
		} catch (SQLException e){
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	
	 * This method queries the database and returns all the matching records for
	 * a particular crop, location, annotation and between the beginDate and the
	 * endDate.
	 * 
	 * If crop is null, it returns for all crops. If annotation is null, it
	 * returns for all annotations. If location is null, it returns for all
	 * locations. If both beginDate and endDate is null, it returns for all
	 * dates. If only endDate is null, it returns all data on and after the
	 * beginDate. If only beginDate is null, it returns all data on or before
	 * the endDate.
	 
	public int getCallsCount(String location, 
			String crop, 
			String annotation, 
			String beginDate, 
			String endDate) {
		// TODO Auto-generated method stub
		try {
			Connection conn = getConnection();
			// After connection has been established, form the sql query
			String sql = "SELECT COUNT(call_id) FROM KCCDATA";
			if ((location != null) || (crop != null) || (annotation != null) || (beginDate != null)
					|| (endDate != null)) {
				sql = sql + " WHERE ";
				if (location != null)
					sql = sql + "location = '" + location + "' AND ";
				if (crop != null)
					sql = sql + "crop LIKE '" + crop + "%' AND ";
				if (annotation != null)
					sql = sql + "annotation LIKE'" + annotation + "%' AND ";
				if (beginDate != null) {
					if (endDate == null)
						sql = sql + "date >= '" + beginDate + "'";
					else
						sql = sql + "date BETWEEN '" + beginDate + "' AND '" + endDate + "'";
				} else {
					if (endDate != null)
						sql = sql + "date <= '" + endDate + "'";
					else
						
						 * Both beginDate and endDate are null and there is no
						 * need to include date in the sql query. But we have
						 * reached here because one of the prior parameters was
						 * not null. This means we have added a " AND " to our
						 * sql query which we need to remove.
						 
						sql = sql.substring(0, sql.length() - 5);
				}
			}

			sql = sql + ";";
			// Now execute the sql query
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery(sql);
			rs.next();
			int result = rs.getInt(1);
			stmt.close();
			rs.close();
			conn.close();
			return result;
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return -1;
	}
*/	
	/* This method runs a DISTINCT query on the database and returns all the 
	 * crops present in the table. If none are present, empty arraylist is returned.
	 * */
	public List<String> getAllCrops(){
		List<String> list = new ArrayList<String>();
		try {
			Connection conn = getConnection();
			String sql = "SELECT DISTINCT(crop), COUNT(call_id) FROM KCCDATA WHERE CROP NOT LIKE '%other%'GROUP BY 1 ORDER BY 2 DESC;";
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery(sql);
			while (rs.next()) {
				if (rs.getInt(2)>= Alert.MINIMUM_CALLS_FOR_CROP)
					list.add(rs.getString(1));
			}
			stmt.close();
			rs.close();
			conn.close();
		} catch (ClassNotFoundException | SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return list;
	}
	
	/* This method runs a DISTINCT query on the database and returns all the 
	 * locations present in the table. If none are present, empty arraylist is returned.
	 * */
	public List<String> getAllLocations(){
		List<String> list = new ArrayList<String>();
		try {
			Connection conn = getConnection();
			String sql = "SELECT DISTINCT(location), COUNT(call_id) FROM KCCDATA GROUP BY 1 ORDER BY 2 DESC;";
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery(sql);
			while (rs.next()) {
				if (rs.getInt(2)>= Alert.MINIMUM_CALLS_FOR_LOCATION)
					list.add(rs.getString(1));
			}
			stmt.close();
			rs.close();
			conn.close();
			return list;
		} catch (ClassNotFoundException | SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return list;
	}
	/* This method runs a DISTINCT query on the database and returns all the 
	 * annotations present in the table. If none are present, empty arraylist is returned.
	 * */

	public List<String> getAllAnnotations() {
		List<String> list = new ArrayList<String>();
		try {
			Connection conn = getConnection();
			String sql = "SELECT DISTINCT(annotation), COUNT(call_id) FROM KCCDATA GROUP BY 1 ORDER BY 2 DESC;";
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery(sql);
			while (rs.next()) {
				if (rs.getInt(2)>= Alert.MINIMUM_CALLS_FOR_ANNOTATION)
					list.add(rs.getString(1));
			}
			stmt.close();
			rs.close();
			conn.close();
			return list;
		} catch (ClassNotFoundException | SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return list;
	}

	/* This method takes in a combination of location, crop, annotations, date range and window length.
	 * It returns location, crop, annotation and year wise counts of calls made. 
	 * It runs a sql query of kind "select location, crop, annotation, year(date), count(call_id)
	 * from KCCDATA where location = ?, crop = ?, annotation = ?, dayofyear between begindate
	 * and enddate and group by location, crop, annotation and the year".
	 * 
	 * If any of the parameters passed is null, it is treated as *. 
	 * If year is wrapped, then the call falling in the alertwindow but of previous year is 
	 * counted in the present year.
	 * */
	public List<CallSummaryData> getCallsSummary(String location, String crop, String annotation,
			int beginDayOfYear, int endDayOfYear, int alertWindowLength){
		// Initializing variables
		String sql = "";
		List<CallSummaryData> results = new ArrayList<CallSummaryData>();

		if ((beginDayOfYear == 0) || (endDayOfYear == 0)){
			return results;
		}
		
		
		/* If we are in January and our beginning of the window extends to December, then
		 * actually the calls in previous year and recorded in December belong to this season.
		 * So we will have different query logics based on whether our window extends into
		 * previous year or not.
		 */		
		if  (beginDayOfYear < endDayOfYear){
			// There is no wrapping around the calendar year.
			sql = "SELECT location, crop, annotation, year(date), count(call_id) FROM KCCDATA WHERE ";
			if (location != null)
				sql = sql + "location LIKE '" + location + "%' AND ";
			if (crop != null)
				sql = sql + "crop LIKE '" + crop + "%' AND ";
			if (annotation != null)
				sql = sql + "annotation LIKE'" + annotation + "%' AND ";
			
			sql = sql + "dayofyear(date) BETWEEN "+beginDayOfYear+" AND "+endDayOfYear+" GROUP BY 1,2,3,4 order by 1 ASC, 2 ASC, 3 ASC, 4 DESC;";
		} else {
			// There is wrapping around the calendar year.
			sql = "SELECT location, crop, annotation, IF(YEAR(DATE_ADD(date,INTERVAL "+ alertWindowLength +" DAY))>YEAR(date),YEAR(date)+1,year(date)), count(call_id) from KCCDATA WHERE ";
			if (location != null)
				sql = sql + "location LIKE '" + location + "%' AND ";
			if (crop != null)
				sql = sql + "crop LIKE '" + crop + "%' AND ";
			if (annotation != null)
				sql = sql + "annotation LIKE'" + annotation + "%' AND ";
			
			sql = sql + "dayofyear(date) <= "+beginDayOfYear+" OR dayofyear(date) >= "+ endDayOfYear+" GROUP BY 1, 2, 3, 4 order by 1 ASC, 2 ASC, 3 ASC, 4 DESC;";
		}
		System.out.println(sql);
		Connection conn;
		try {
			conn = getConnection();
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery(sql);
			
			// Now iterate through the resulting query
			while (rs.next()){
				if (rs.getInt(5) >= Alert.MINIMUM_CALLS_FOR_ALERT){
					CallSummaryData callSummary = new CallSummaryData();
					callSummary.setCount(rs.getInt(5));
					callSummary.setLocation(rs.getString(1));
					callSummary.setAnnotation(rs.getString(3));
					callSummary.setCrop(rs.getString(2));
					callSummary.setYear(rs.getInt(4));
					results.add(callSummary);
				}
			}
			rs.close();
			stmt.close();
			conn.close();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return results;
	}
	
}


