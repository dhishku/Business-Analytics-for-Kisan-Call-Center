package gov.dacfw.kcc.readers;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import com.monitorjbl.xlsx.StreamingReader;

import gov.dacfw.kcc.model.Map;

public class ExcelToDB {
	private static final String BASE_URI = "/home/dhishku/Projects/KCC2/res/";
	private static final String[] files = {"August_2016.xlsx", "August_2015.xlsx", "August_2014.xlsx", "July_2016.xlsx", "July_2015.xlsx", "July_2014.xlsx",
			"June_2016.xlsx", "June_2015.xlsx", "June_2014.xlsx" };
	//private static final String[] files = {"August_2016.xlsx"};

	private static final String DB_URL = "jdbc:mysql://localhost/testDB";
	private static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";
	private static final String USER_NAME = "guest";
	private static final String PASSWORD = "Guest123";
	private static final String BASE_SQL = "INSERT INTO KCCDATA (location, crop, annotation, date, query) VALUES (?, ?, ?, ?,?)";

	private static List<Map> mapping;
	private static final String MAP_FILE_NAME = BASE_URI + "mapping.csv";
	// private static final String FILE_OUT_NAME = BASE_URI+"output.txt";

	private static Connection getConnection() throws ClassNotFoundException, SQLException {
		Class.forName(JDBC_DRIVER);
		return DriverManager.getConnection(DB_URL, USER_NAME, PASSWORD);
	}

	public static void main(String[] args) throws SQLException, ClassNotFoundException, IOException {
		// initialize the map.
		initializeMapping();

		// open a database connection
		Connection conn = getConnection();

		// open writing file
		/*
		 * File of = new File(FILE_OUT_NAME); if (!of.exists())
		 * of.createNewFile();
		 * 
		 * BufferedWriter bw = new BufferedWriter(new
		 * FileWriter(of.getAbsolutePath()));
		 */

		// Load all excel files
		for (String file : files) {
			System.out.println(file);
			InputStream is = new FileInputStream(new File(BASE_URI + file));
			Workbook workbook = StreamingReader.builder().rowCacheSize(100) // number
																			// of
																			// rows
																			// to
																			// keep
																			// in
																			// memory
																			// (defaults
																			// to
																			// 10)
					.bufferSize(4096) // buffer size to use when reading
										// InputStream to file (defaults to
										// 1024)
					.open(is); // InputStream or File for XLSX file (required)
			System.out.println("Starting file: " + file);

			PreparedStatement ps = conn.prepareStatement(BASE_SQL);
			conn.setAutoCommit(false);
			for (Sheet sheet : workbook) {
				int rowCounter = 1;
				for (Row r : sheet) {
					if (r.getRowNum() == 0)
						continue;
					if (r.getCell(5).getStringCellValue().contains("Weather"))
						continue;
					String location, crop, annotation, date, query;
					int indexOfParanthesis;
					location = r.getCell(2).getStringCellValue();

					crop = r.getCell(7).getStringCellValue();
					// In crop we convert likes of Paddy (Dhan) into just Paddy
					indexOfParanthesis = crop.indexOf("(");
					if (indexOfParanthesis > 1)
						crop = crop.substring(0, indexOfParanthesis);
					if (r.getCell(8) == null)
						continue;
					query = r.getCell(8).getStringCellValue().trim();
					
					annotation = getAnnotation(query,
							r.getCell(5).getStringCellValue().trim());
					if (r.getCell(11) == null)
						date = r.getCell(10).getStringCellValue().substring(0, 10);
					else
						date = r.getCell(11).getStringCellValue().substring(0, 10);

					if (query.length()>=250){
						query = query.substring(0, 249);
					}
					
					// Now that all values are set, simply put in the database
					// Put only non weather and matched values in the database as of now
					if (!annotation.contains("Weather") && !annotation.contains("Unmatched")) {
						ps.setString(1, location);
						ps.setString(2, crop);
						ps.setString(3, annotation);
						ps.setString(4, date);
						ps.setString(5, query);
						ps.addBatch();
						rowCounter++;
						// String line =
						// r.getCell(5).getStringCellValue().trim()+"
						// "+annotation+","+r.getCell(8).getStringCellValue().trim()+"\n";
						// bw.write(line);
					}
					if ((rowCounter % 10000) == 0) {
						ps.executeBatch();
						conn.commit();
						rowCounter = 1;
						ps = conn.prepareStatement(BASE_SQL);
					}

				}
				ps.executeBatch();
				conn.commit();
				// all the rows have been read, so now close the sql query
			}
			ps.close();
			workbook.close();
			is.close();
			System.out.println("ended file: " + file);

		}
		// bw.close();
		conn.close();
	}

	private static String getAnnotation(String query, String query_type) {

		if (mapping.size() == 0)
			return query_type;
		query = query.replaceAll(" ", ""); // removing all spaces
		query = query.toLowerCase();
		for (int i = 0; i < mapping.size(); i++) {
			if (query.contains(mapping.get(i).getKey())) {
				// annotation found. return the value
				return mapping.get(i).getValue();
			}
		}
		// reaching here means unmatched. Now specific rules
		if (query_type.contains("Cultural"))
			return "GovtScheme_AgronometricPractices";
		if (query_type.contains("Fertilizer"))
			return "GovtScheme_NutrientManagement";
		if (query_type.contains("Nutrient"))
			return "GovtScheme_NutrientManagement";
		if (query_type.contains("Government"))
			return "GovtScheme";
		if (query_type.contains("Protection"))
			return "PlantProtection";
		if (query_type.contains("Weed"))
			return "PlantProtection_Weed";
		if (query_type.contains("Seed"))
			return "GovtScheme_Seeds";
		if (query_type.contains("Field"))
			return "GovtScheme_AgronometricPractices";
		if (query_type.contains("Varieties"))
			return "GovtScheme_Seeds";
		if (query_type.contains("Organic"))
			return "GovtScheme_Organic";
		if (query_type.contains("Market"))
			return "GovtScheme_Price";

		return "Unmatched";
	}

	/*
	 * This method reads the mapping file and populates the static mapping array
	 * list.
	 */
	private static void initializeMapping() {
		try {
			mapping = new ArrayList<Map>();
			File f = new File(MAP_FILE_NAME);
			BufferedReader br = new BufferedReader(new FileReader(f));
			String line;
			while ((line = br.readLine()) != null) {
				StringTokenizer st = new StringTokenizer(line, ",");
				if (st.countTokens() >= 2) {
					Map m = new Map();
					m.setKey(st.nextToken());
					String value = "";
					while (st.hasMoreTokens()) {
						value += st.nextToken() + "_";
					}
					m.setValue(value.substring(0, value.length() - 1));
					mapping.add(m);
				}
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
