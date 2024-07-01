package src;
import java.io.File;
import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;

/**
 * InitialiseDB
 */
public class InitialiseDB {

    // using the VSCode run tool it works fine
    public static void main(String[] args) {
        InitialiseDB movies = new InitialiseDB();
        // checks whether a file for the database already exists in the database, and if so, deletes it
        String dbPath = "src/movies.db";
        List<String> tables = new ArrayList<String>(Arrays.asList("actor", "actor_award", "movie", "movie_award", "movie_cast", "movie_genre", "genre"));
        String schemaPath = "src/createDB.sql";

        File db = new File(dbPath);
        if(db.exists() & db.canRead()){
            db.delete();
        }

        // creates the table (by reading and running the DDL file)
        movies.tryToAccessDB(dbPath, schemaPath);

        // Test that the action was successful and print 
        if(movies.validateDB(dbPath, tables)){
            System.out.println("OK");
        } else {
            System.out.println("INVALID DB");
        }
    }

    // Taken from Exercise06 - from JDBC Example
    public void tryToAccessDB(String dbFileName, String schemaFile) {
        String dbUrl = "jdbc:sqlite:" + dbFileName;

		try ( Connection connection = DriverManager.getConnection(dbUrl); ) {
			checkDriverLoaded();
			createSchema(connection, schemaFile);

		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}
    }

    // Taken from Exercise06 - from JDBC Example
    private static void checkDriverLoaded() {
		try {
			Class jdbc = Class.forName("org.sqlite.JDBC");
			// System.out.println( "Loaded Driver for JDBC:" + jdbc.getCanonicalName() );
		} catch (ClassNotFoundException e) {
			System.out.println("Driver for JDBC failed to load");
			System.exit(1);
        }
    }

    // Edited from Exercise06 - from JDBC Example
    private static void createSchema(Connection connection, String schemaPath) throws SQLException{
        Statement statement = connection.createStatement();

        // take schema from schema DDL File
        String schema = "";
        File schemaFile = new File(schemaPath);
        try {
            Scanner scanner = new Scanner(schemaFile);
            while (scanner.hasNextLine()) {
                schema += scanner.nextLine() + " ";
            }
            scanner.close();
        } catch (Exception e) {
            // TODO: handle exception
        }
        statement.executeUpdate(schema);
        statement.close();
    }

    public boolean validateDB(String db, List<String> tables){
        boolean isValid = true;

        String dbUrl = "jdbc:sqlite:" + db;
        try ( Connection connection = DriverManager.getConnection(dbUrl); ) {
            List<String> tableNames = new ArrayList<String>();
            // get meta data about the tables
            DatabaseMetaData dbMeta = connection.getMetaData();
            ResultSet resultSet = dbMeta.getTables(null, null, null, new String[] {"TABLE"});

            // check if the tables we expect have been created
            while (resultSet.next()) {
                // return the name of the current table
                String name = resultSet.getString("TABLE_NAME");
                tableNames.add(name);
            }

            Collections.sort(tableNames);
            Collections.sort(tables);
            if(!tableNames.equals(tables)){
                isValid = false;
            }

            

		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}

        return isValid;
    }
}