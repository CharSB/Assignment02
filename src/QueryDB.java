package src;

import java.sql.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;

public class QueryDB {

    private String dbPath = "src/movies.db";
    public String getDBPath(){ return dbPath; };
    public static void main(String[] args) {
        QueryDB queryDB = new QueryDB();

        queryDB.tryToAccessDB(queryDB.getDBPath());

        if(args.length >= 1 ){
            try {
                int query = Integer.parseInt(args[0]);
                String dbUrl = "jdbc:sqlite:" + queryDB.getDBPath();

                try ( Connection connection = DriverManager.getConnection(dbUrl); ) {
                    String actor;
                    String director;
                    String year;
                    switch (query) {
                        case 1:
                            queryDB.movieNames(connection);
                            break;
                        case 2:
                            // movie_title
                            if(args.length != 2){
                                String errMessage = "USAGE: QueryDB 2 \"movie_title\" ";
                                queryDB.inputError(errMessage);
                            }
                            String title = args[1];
                            queryDB.actorsInMovie(connection, title);
                            break;
                        case 3: 
                            // actor, director
                            if(args.length != 3){
                                String errMessage = "USAGE: QueryDB 3 \"actor's name\" \"director's name\" ";
                                queryDB.inputError(errMessage);
                            }
                            actor = args[1];
                            director = args[2];
                            queryDB.descriptionFromActorDirector(connection, actor, director); 
                            break;
                        case 4: 
                            // actor
                            if(args.length != 2){
                                String errMessage = "USAGE: QueryDB 4 \"actor's name\"";
                                queryDB.inputError(errMessage);
                            }
                            actor = args[1];
                            queryDB.directorFromActor(connection, actor);
                            break;
                        case 5: 
                            // actor
                            if(args.length != 2){
                                String errMessage = "USAGE: QueryDB 5 \"actor's name\"";
                                queryDB.inputError(errMessage);
                            }
                            actor = args[1];
                            queryDB.actorsAwards(connection, actor);
                            break;
                        case 6:
                            // year
                            if(args.length != 2){
                                String errMessage = "USAGE: QueryDB 6 \"year\"";
                                queryDB.inputError(errMessage);
                            }
                            year = args[1];
                            queryDB.genreFromAwardYear(connection, year);
                            break;
                    
                        default:
                            queryDB.inputError();
                            break;
                    } 
                } catch (SQLException e) {
                    System.out.println(e.getMessage());
                }
            } catch (Exception e) {
                queryDB.inputError();
            }

            
        }
        else{
            queryDB.inputError();
        }


    }

    private void inputError(){
        System.err.println("USAGE: QueryQB # ");
        System.err.println("# Options are: ");
        System.err.println("1 - List the names of all the movies in the database.");
        System.err.println("2 - List the names of the actors who perform in some specified movie.");
        System.err.println("3 - List the synopses of a movie with a specified actor in it and directed by some particular director.");
        System.err.println("4 - List the directors of the movies that have a particular actor in them.");
        System.err.println("5 - ");
        System.err.println("6 - ");
        System.exit(0);
    }
    private void inputError(String errMessage){
        System.err.println(errMessage);
        System.err.println("# Options are: ");
        System.err.println("1 - List the names of all the movies in the database.");
        System.err.println("2 - List the names of the actors who perform in some specified movie.");
        System.err.println("3 - List the synopses of a movie with a specified actor in it and directed by some particular director.");
        System.err.println("4 - List the directors of the movies that have a particular actor in them.");
        System.err.println("5 - ");
        System.err.println("6 - ");
        System.exit(0);
    }

    // Taken from Exercise06 - from JDBC Example
    private void tryToAccessDB(String dbFileName) {
        String dbUrl = "jdbc:sqlite:" + dbFileName;

		try ( Connection connection = DriverManager.getConnection(dbUrl); ) {
			checkDriverLoaded();
            
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

    private void movieNames(Connection connection){
        try {
            PreparedStatement statement;
            statement = connection.prepareStatement("SELECT title FROM movie");
            ResultSet resultSet = statement.executeQuery();

            System.out.println("Movie Titles: ");
            while(resultSet.next()){
                System.out.println(resultSet.getString("title"));
            }
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }

    private void actorsInMovie(Connection connection, String movie_title){
        try {
            PreparedStatement statement;
                statement = connection.prepareStatement("SELECT actor.first_name, actor.last_name FROM movie_cast " 
                + "INNER JOIN movie ON movie_cast.movie_id = movie.id "
                + "INNER JOIN actor ON movie_cast.actor_id = actor.id "
                + "WHERE movie.title = ?");
            statement.setString(1, movie_title);
            ResultSet resultSet = statement.executeQuery();

            // Ensure movie exists in DB
            if(!resultSet.next()){
                System.err.println("Movie does not exist in DB");
                System.exit(1);
            }

            System.out.println("Cast: ");
            System.out.println(resultSet.getString("first_name") + " " + resultSet.getString("last_name"));
            
            while(resultSet.next()){
                System.out.println(resultSet.getString("first_name") + " " + resultSet.getString("last_name"));

            }
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }

    private void descriptionFromActorDirector(Connection connection, String actor, String director){
        try {
            PreparedStatement statement;
            statement = connection.prepareStatement("SELECT movie.title, movie.plot FROM movie_cast " 
            + "INNER JOIN movie ON movie_cast.movie_id = movie.id "
            + "INNER JOIN actor ON movie_cast.actor_id = actor.id "
            + "WHERE actor.first_name = ? AND actor.last_name = ? "
            + "AND movie.director = ?");
            String[] name = actor.split(" ", 2);
            statement.setString(1, name[0]);
            statement.setString(2, name[1]);
            statement.setString(3, director);
            ResultSet resultSet = statement.executeQuery();

            // Ensure movie exists in DB
            if(!resultSet.next()){
                System.err.println("Cannot find Movie with " + actor + " directed by " + director);
                System.exit(1);
            } else{
                System.out.println("Descriptions: ");
                System.out.println(resultSet.getString("title") + ": " + resultSet.getString("plot"));

                while(resultSet.next()){
                    System.out.println(resultSet.getString("title") + ": " + resultSet.getString("plot"));
                }
            }

            
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }

    }

    private void directorFromActor(Connection connection, String actor){
        try {
            PreparedStatement statement;
            statement = connection.prepareStatement("SELECT movie.title, movie.director FROM movie_cast " 
            + "INNER JOIN movie ON movie_cast.movie_id = movie.id "
            + "INNER JOIN actor ON movie_cast.actor_id = actor.id "
            + "WHERE actor.first_name = ? AND actor.last_name = ? ");
            String[] name = actor.split(" ", 2);
            statement.setString(1, name[0]);
            statement.setString(2, name[1]);
            ResultSet resultSet = statement.executeQuery();

            if(!resultSet.next()){
                System.err.println("Cannot find a movie with " + actor + " in it");
                System.exit(1);
            } else{
                System.out.println("Movie: Director");
                System.out.println(resultSet.getString("title") + ": " + resultSet.getString("director"));

                while(resultSet.next()){
                    System.out.println(resultSet.getString("title") + ": " + resultSet.getString("director"));
                }
            }
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
        
    }

    private void actorsAwards(Connection connection, String actor){
        try {
            PreparedStatement statement;
            statement = connection.prepareStatement("SELECT * FROM actor_award "
            + "INNER JOIN actor ON actor_award.actor_id = actor.id "
            + "WHERE actor.first_name = ? AND actor.last_name = ?");
            String[] name = actor.split(" ", 2);
            statement.setString(1, name[0]);
            statement.setString(2, name[1]);
            ResultSet resultSet = statement.executeQuery();

            if(!resultSet.next()){
                System.out.println(name + " has not won an oscar between 1927-2024");
            } else{
                System.out.println("Awards: ");
                if( resultSet.getBoolean("winner")){
                    System.out.printf("Won ");
                } else {
                    System.out.printf("Nominated ");
                }
                System.out.println("for " + resultSet.getString("name") + " in \""  + resultSet.getString("movie") + "\" during the "+ resultSet.getString("year_awarded") + " Oscar's");

                while(resultSet.next()){
                    if( resultSet.getBoolean("winner")){
                        System.out.printf("Winner ");
                    } else {
                        System.out.printf("Nominated ");
                    }
                    System.out.println("for " + resultSet.getString("name") + " in \""  + resultSet.getString("movie") + "\" during the "+ resultSet.getString("year_awarded") + " Oscar's");
                }
            }
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }

    private void genreFromAwardYear(Connection connection, String year){
        try {
            PreparedStatement statement;
            statement = connection.prepareStatement("SELECT * FROM movie_award " 
            + "INNER JOIN movie_genre ON movie_award.movie_id = movie_genre.movie_id "
            + "INNER JOIN genre ON movie_genre.genre_id = genre.id "
            + "WHERE movie_award.year_awarded = ? AND movie_award.winner = TRUE ");
            statement.setString(1, year);
            ResultSet resultSet = statement.executeQuery();

            if(!resultSet.next()){
                System.err.println("No movies won in " + year);
                System.exit(1);
            } else{
                // Only 1 of each genre should be printed out
                Set<String> genres = new HashSet<String> ();
                genres.add(resultSet.getString("genre"));
                while(resultSet.next()){
                    genres.add(resultSet.getString("genre"));
                }
                System.out.println("Winning Genres: ");
                for(String s : genres){
                    System.out.println(s);
                }

            }
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }

}
