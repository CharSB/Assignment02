package src;

import java.io.BufferedReader;
import java.io.FileReader;
import java.sql.*;

public class PopulateDB {
    
    private String dbPath = "src/movies.db";
    public String getDBPath(){ return dbPath; };

    public static void main(String[] args) {
        PopulateDB populateDB = new PopulateDB();
        populateDB.tryToAccessDB(populateDB.getDBPath());
    }

    // Taken from Exercise06 - from JDBC Example
    public void tryToAccessDB(String dbFileName) {
        String dbUrl = "jdbc:sqlite:" + dbFileName;

		try ( Connection connection = DriverManager.getConnection(dbUrl); ) {
			checkDriverLoaded();
            populateMovies(connection);
            populateActors(connection);
            populateCast(connection);
            populateAwards(connection);
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

    public void populateActors(Connection connection){
        String csvPath = "data/HollywoodActors.csv";

        try {
            BufferedReader br = new BufferedReader(new FileReader(csvPath)); 
            String line = br.readLine();

            // In order to ignore header
            line = br.readLine();

			while (line != null) {
				String first_name;
                String last_name;
                String dob;

                // split on ,
                String[] actor = line.split(",");

                // first name is everything before the first space
                // last name is everything after
                String[] name = actor[5].split(" ", 2);
                first_name = name[0];
                last_name = name[1];
                dob = actor[7];

                // PUT DATA IN DB
                InsertActor(connection, first_name, last_name, dob);

				// read the next line
				line = br.readLine();
			}

			br.close();
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }

    public void populateMovies(Connection connection){
        String csvPath = "data/BestMovies.tsv";

        try {
            BufferedReader br = new BufferedReader(new FileReader(csvPath)); 
            String line = br.readLine();

            // In order to ignore header
            line = br.readLine();

			while (line != null) {
                String title; 
                int release_year; 
                int runtime;
                String director;
                String plot;
                float rating;

                // split on ,
                String[] movie = line.split("\t");

                // all atomci data for the movie
                title = movie[5];
                release_year = Integer.parseInt(movie[10]);
                runtime = Integer.parseInt(movie[9]);
                director = movie[14];
                plot = movie[4].replace("\"", "");
                rating = Float.parseFloat(movie[8]);

                // PUT DATA IN DB
                InsertMovie(connection, title, release_year, runtime, director, plot, rating);

                // Retrieve the ID of the movie just put in
                PreparedStatement statement3;
                statement3 = connection.prepareStatement("SELECT * FROM movie WHERE plot = ?");
                statement3.setString(1, plot);
                ResultSet resultSet3 = statement3.executeQuery();
                int movie_id = resultSet3.getInt("id");

                // GENRE HANDLING
                // remove quotes - these occur when there is more than 1 genre
                // then split the set based on the , 
                String[] genres = movie[11].replace("\"", "").replace(" ", "").split(",");

                for(String genre : genres){
                    
                    // Check if the genre already exists int he DB
                    PreparedStatement statement;
                    statement = connection.prepareStatement("SELECT * FROM genre WHERE genre = ?");
                    statement.setString(1, genre);
                    ResultSet resultSet = statement.executeQuery();

                    if(!resultSet.next()){
                        InsertGenre(connection, genre);
                    } 
                    statement.close();

                    // return ID of the genre
                    PreparedStatement statement2;
                    statement2 = connection.prepareStatement("SELECT * FROM genre WHERE genre = ?");
                    statement2.setString(1, genre);
                    ResultSet resultSet2 = statement2.executeQuery();
                    int genre_id = resultSet2.getInt("id");
                    statement2.close();
                    
                    InsertMovieGenre(connection, movie_id, genre_id);
                }

				// read the next line
				line = br.readLine();
			}

			br.close();
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }

    public void populateCast(Connection connection){
        String csvPath = "data/IMDB_Movies&Actors.tsv";

        try {
            BufferedReader br = new BufferedReader(new FileReader(csvPath)); 
            String line = br.readLine();

            // In order to ignore header
            line = br.readLine();

			while (line != null) {
                String[] info = line.replace("\"", "").split("\t");
                String movie_title = info[0];

                String[] actors = info[1].split(", ");

                for (String actor : actors) {

                    String[] name = actor.split(" ", 2);
                    String first_name = name[0];
                    String last_name = "";
                    if(name.length == 2){
                        last_name = name[1];
                    }

                    linkMoviesActors(connection, first_name, last_name, movie_title);
                }

				// read the next line
				line = br.readLine();
			}

			br.close();
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }

    public void populateAwards(Connection connection){
        String csvPath = "data/Oscars1927-2024.tsv";

        try {
            BufferedReader br = new BufferedReader(new FileReader(csvPath)); 
            String line = br.readLine();

            // In order to ignore header
            line = br.readLine();

			while (line != null) {
                String[] info = line.replace("\"", "").split("\t");
                String award_title = info[3];
                String movie_title = info[5];
                int year = Integer.parseInt(info[1]);
                boolean winner;


                if(info[6].equals("TRUE")){
                    winner = true;
                } else {
                    winner = false;
                }

                if(!award_title.contains("ACTOR") && !award_title.contains("ACTRESS"))
                {
                    InsertMovieAward(connection, award_title, movie_title, year, winner);
                } 
                else{
                    String[] name = info[4].split(" ", 2);
                    String first_name = name[0];
                    String last_name = "";
                    if(name.length == 2)
                    {
                        last_name = name[1];
                    }

                    InsertActorAward(connection, award_title, first_name, last_name, year, movie_title ,winner);
                }

				// read the next line
				line = br.readLine();
			}

			br.close();
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }

    public void linkMoviesActors(Connection connection, String first_name, String last_name, String movie_title){

        try {
            PreparedStatement statement;
            statement = connection.prepareStatement("SELECT id FROM actor WHERE first_name = ? AND last_name = ?");
            statement.setString(1, first_name);
            statement.setString(2, last_name);
            ResultSet resultSet = statement.executeQuery();

            PreparedStatement statement2;
            statement2 = connection.prepareStatement("SELECT id FROM movie WHERE title = ?");
            statement2.setString(1, movie_title);
            ResultSet resultSet2 = statement2.executeQuery();

            if(resultSet.next() && resultSet2.next()){
                int actor_id = resultSet.getInt("id");
                int movie_id = resultSet2.getInt("id");

                InsertCastMember(connection, movie_id, actor_id);
            }

        } catch (Exception e) {
            // TODO: handle exception
        }
    }

    public void InsertMovieAward(Connection connection, String award_title, String movie_title, int year, boolean winner){
        try {
            PreparedStatement statement;
            statement = connection.prepareStatement("SELECT * FROM movie WHERE title = ?");
            statement.setString(1, movie_title);
            ResultSet resultSet = statement.executeQuery();

            if(resultSet.next()){
                int movie_id = resultSet.getInt("id");
                PreparedStatement statement2;
                statement2 = connection.prepareStatement("INSERT INTO movie_award (name, movie_id, year_awarded, winner) VALUES ("
                + " ?,"
                + " ?,"
                + " ?,"
                + " ?" + ")");

                statement2.setString(1, award_title);
                statement2.setInt(2, movie_id);
                statement2.setString(3, Integer.toString(year));
                statement2.setBoolean(4, winner);

                statement2.executeUpdate();
                statement2.close();
            }
            

        } catch (Exception e) {
            // TODO: handle exception
        }
    }

    public void InsertActorAward(Connection connection, String award_title, String first_name, String last_name, int year, String movie_title,boolean winner){
        try {
            PreparedStatement statement;
            statement = connection.prepareStatement("SELECT id FROM actor WHERE first_name = ? AND last_name = ?");
            statement.setString(1, first_name);
            statement.setString(2, last_name);
            ResultSet resultSet = statement.executeQuery();

            if(resultSet.next()){
                int actor_id = resultSet.getInt("id");
                PreparedStatement statement2;
                statement2 = connection.prepareStatement("INSERT INTO actor_award (name, actor_id, year_awarded, movie, winner) VALUES ("
                + " ?,"
                + " ?,"
                + " ?,"
                + " ?,"
                + " ?" + ")");
                statement2.setString(1, award_title);
                statement2.setInt(2, actor_id);
                statement2.setString(3, Integer.toString(year));
                statement2.setString(4, movie_title);
                statement2.setBoolean(5, winner);

                statement2.executeUpdate();
                statement2.close();
            }
            statement.close();

        } catch (Exception e) {
            // TODO: handle exception
        }
    }

    public void InsertMovie(Connection connection, String title, int release_year, int runtime, String director, String plot, float rating) throws SQLException{
        PreparedStatement statement;
		statement = connection.prepareStatement("INSERT INTO movie (title, release_year, runtime, director, plot, rating) VALUES ("
        + " ?,"
        + " ?,"
        + " ?,"
        + " ?,"
        + " ?,"
        + " ?" + ")");
        statement.setString(1, title);
        statement.setInt(2, release_year);
        statement.setInt(3, runtime);
        statement.setString(4, director);
        statement.setString(5, plot);
        statement.setFloat(6, rating);

        statement.executeUpdate();
		statement.close();
    }

    public void InsertActor(Connection connection, String first_name, String last_name, String dob) throws SQLException{
        PreparedStatement statement;
		statement = connection.prepareStatement("INSERT INTO actor (first_name, last_name, dob) VALUES ("
        + " ?,"
        + " ?,"
        + " ?" + ")");
        statement.setString(1, first_name);
        statement.setString(2, last_name);
        statement.setString(3, dob);


        statement.executeUpdate();
		statement.close();
    }

    public void InsertGenre(Connection connection, String genre) throws SQLException{
        PreparedStatement statement;
        statement = connection.prepareStatement("INSERT INTO genre (genre) VALUES (?)");
        statement.setString(1, genre);
        statement.executeUpdate();
        statement.close();
    }

    public void InsertMovieGenre(Connection connection, int movie_id, int genre_id) throws SQLException{
        // Add values into movie_genre table to link the two tables
        PreparedStatement statement;
        statement = connection.prepareStatement("INSERT INTO movie_genre VALUES (?, ?)");
        statement.setInt(1, genre_id);
        statement.setInt(2, movie_id);
        statement.executeUpdate();
        statement.close();
    }

    public void InsertCastMember(Connection connection, int movie_id, int actor_id) throws SQLException{
        // Add values into movie_genre table to link the two tables
        PreparedStatement statement;
        statement = connection.prepareStatement("INSERT INTO movie_cast VALUES (?, ?)");
        statement.setInt(1, actor_id);
        statement.setInt(2, movie_id);
        statement.executeUpdate();
        statement.close();
    }
}
