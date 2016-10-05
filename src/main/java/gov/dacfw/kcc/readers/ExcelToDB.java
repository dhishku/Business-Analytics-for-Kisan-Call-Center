package gov.dacfw.kcc.readers;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import org.apache.poi.ss.usermodel.Row;

import com.monitorjbl.xlsx.StreamingReader;

public class ExcelToDB {
	private static final String BASE_URI = "/home/dhishku/Projects/KCC2/res/";
	private static final String[] files = {"August_2015.xlsx","August_2014.xlsx",
			"July_2016.xlsx","July_2015.xlsx","July_2014.xlsx",
			"June_2016.xlsx","June_2015.xlsx","June_2014.xlsx"};
	
	private static final String DB_URL = "jdbc:mysql://localhost/testDB";
	private static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";
	private static final String USER_NAME = "guest";
	private static final String PASSWORD = "Guest123";
	private static final String BASE_SQL = "INSERT INTO KCCDATA (location, crop, annotation, date) VALUES (?, ?, ?, ?)";

	private static Connection getConnection() throws ClassNotFoundException, SQLException {
		Class.forName(JDBC_DRIVER);
		return DriverManager.getConnection(DB_URL, USER_NAME, PASSWORD);
	}

	
	public static void main (String[] args) throws SQLException, ClassNotFoundException, IOException{
		// open a database connection
		Connection conn = getConnection();
		
		// Load all excel files
		for (String file: files){
			System.out.println(file);
			InputStream is = new FileInputStream(new File(BASE_URI+file));
			StreamingReader reader = StreamingReader.builder()
			        .rowCacheSize(100)    // number of rows to keep in memory (defaults to 10)
			        .bufferSize(4096)     // buffer size to use when reading InputStream to file (defaults to 1024)
			        .sheetIndex(0)        // index of sheet to use (defaults to 0)
			        .read(is);            // InputStream or File for XLSX file (required)
			System.out.println("Starting file: "+file);
			
			PreparedStatement ps = conn.prepareStatement(BASE_SQL);
			conn.setAutoCommit(false);
			int rowCounter = 1;
			for (Row r : reader) {
				if (r.getRowNum() == 0)
					continue;
				String location, crop, annotation, date;
				int indexOfParanthesis;
				location = r.getCell(2).getStringCellValue();
				
				crop = r.getCell(7).getStringCellValue();
				// In crop we convert likes of Paddy (Dhan) into just Paddy
				indexOfParanthesis = crop.indexOf("(");
				if (indexOfParanthesis > 1)
					crop = crop.substring(0, indexOfParanthesis);

				annotation = r.getCell(5).getStringCellValue().trim();
				if (r.getCell(11) == null)
					date = r.getCell(10).getStringCellValue().substring(0,10);
				else
					date = r.getCell(11).getStringCellValue().substring(0,10);

				// Now that all values are set, simply put in the database
				// Put only non weather values in the database as of now
				if (!annotation.contains("Weather")){
					ps.setString(1, location);
					ps.setString(2, crop);
					ps.setString(3, annotation);
					ps.setString(4, date);
					ps.addBatch();
					rowCounter++;
				}
				if ((rowCounter%10000) == 0){
					ps.executeBatch();
					conn.commit();
					rowCounter = 1;
					ps = conn.prepareStatement(BASE_SQL);
				}
				
			}
			ps.executeBatch();
			conn.commit();
			// all the rows have been read, so now close the sql query
			ps.close();
			reader.close();
			is.close();
		}
		conn.close();
	}

}
