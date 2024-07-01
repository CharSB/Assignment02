package tests;
import org.junit.Test;

import src.InitialiseDB;
import src.PopulateDB;
import src.QueryDB;

import org.junit.After;
import org.junit.Before;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.PrintStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
public class PopDBTests {
    InitialiseDB init;
    PopulateDB pop;
    Connection connection;

    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private final ByteArrayOutputStream errContent = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;
    private final PrintStream originalErr = System.err;

    @Before
    public void setup(){
        pop = new PopulateDB();
        init = new InitialiseDB();

        System.setOut(new PrintStream(outContent));
        System.setErr(new PrintStream(errContent));
      
        File db = new File("test.db");
        if(db.exists()){
            db.delete();
        }

        init.tryToAccessDB("test.db","src/createDB.sql");

        String dbUrl = "jdbc:sqlite:" + "test.db";
        try {
            connection = DriverManager.getConnection(dbUrl);
        } catch (Exception e) {
            // TODO: handle exception
        }
    }

    @Test
    public void insertBradPittTest() throws SQLException{
        pop.InsertActor(connection, "Brad", "Pitt", "1963-12-18");

        PreparedStatement statement;
        statement = connection.prepareStatement("SELECT * FROM actor");
        ResultSet rs = statement.executeQuery();
        rs.next();
        String last_name = rs.getString("last_name");

        assertEquals("Pitt", last_name);
    }

    @Test
    public void insertBradPittTest2() throws SQLException{
        pop.InsertActor(connection, "Brad", "Pitt", "1963-12-18");

        PreparedStatement statement;
        statement = connection.prepareStatement("SELECT * FROM actor");
        ResultSet rs = statement.executeQuery();
        rs.next();
        String dob = rs.getString("dob");

        assertEquals("1963-12-18", dob);
    }

    @Test
    public void insertTitanticTest() throws SQLException{
        pop.InsertMovie(connection, "The Titanic", 1998, 194, "James Cameron", "Boat sinks", 7.9f);

        PreparedStatement statement;
        statement = connection.prepareStatement("SELECT * FROM movie");
        ResultSet rs = statement.executeQuery();
        rs.next();
        int runtime = rs.getInt("runtime");

        assertEquals(194, runtime);
    }

    @Test
    public void insertTitanticTest2() throws SQLException{
        pop.InsertMovie(connection, "The Titanic", 1998, 194, "James Cameron", "Boat sinks", 7.9f);

        PreparedStatement statement;
        statement = connection.prepareStatement("SELECT * FROM movie");
        ResultSet rs = statement.executeQuery();
        rs.next();
        String director = rs.getString("director");

        assertEquals("James Cameron", director);
    }
    
    @Test
    public void insertRomanceTest() throws SQLException{
        pop.InsertGenre(connection, "Romance");

        PreparedStatement statement;
        statement = connection.prepareStatement("SELECT * FROM genre");
        ResultSet rs = statement.executeQuery();
        rs.next();
        String genre = rs.getString("genre");

        assertEquals("Romance", genre);
    }

    @Test
    public void insertDramaTest() throws SQLException{
        pop.InsertGenre(connection, "Drama");
        pop.InsertGenre(connection, "Romance");
        pop.InsertGenre(connection, "Comedy");

        PreparedStatement statement;
        statement = connection.prepareStatement("SELECT * FROM genre");
        ResultSet rs = statement.executeQuery();
        rs.next();
        String genre = rs.getString("genre");

        assertEquals("Drama", genre);
    }

    @Test
    public void insertMovieAwardTest() throws SQLException{
        pop.InsertMovie(connection, "The Titanic", 1998, 194, "James Cameron", "Boat sinks", 7.9f);
        pop.InsertMovieAward(connection, "BEST MOVIE", "The Titanic", 1999, false);
        
        PreparedStatement statement;
        statement = connection.prepareStatement("SELECT * FROM movie_award");
        ResultSet rs = statement.executeQuery();
        rs.next();
        boolean winner = rs.getBoolean("winner");

        assertEquals(false, winner);
    }

    @Test
    public void insertBadMovieAwardTest() throws SQLException{
        String badTitle = "The Big Boat";
        pop.InsertMovie(connection, "The Titanic", 1998, 194, "James Cameron", "Boat sinks", 7.9f);
        pop.InsertMovieAward(connection, "BEST MOVIE", badTitle, 1999, false);
        
        PreparedStatement statement;
        statement = connection.prepareStatement("SELECT * FROM movie_award");
        ResultSet rs = statement.executeQuery();
        rs.next();

        assertEquals(badTitle + " was not found in the DB\n", errContent);
    }

    @Test
    public void insertActorAwardTest() throws SQLException{
        String first_name = "Brad";
        String last_name = "Pitt";
        pop.InsertActor(connection, first_name, last_name, "1963-12-18");        
        pop.InsertActorAward(connection, "BEST ACTOR", first_name, last_name, 2000, "FIGHT CLUB", true);

        PreparedStatement statement;
        statement = connection.prepareStatement("SELECT * FROM actor_award");
        ResultSet rs = statement.executeQuery();
        rs.next();
        boolean winner = rs.getBoolean("winner");

        assertEquals(true, winner);
    }

    @Test
    public void insertBadActorAwardTest() throws SQLException{
        String first_name = "Brad";
        String last_name = "Pitt";
        pop.InsertActor(connection, first_name, last_name, "1963-12-18");        
        pop.InsertActorAward(connection, "BEST ACTOR", "Bad", last_name, 2000, "FIGHT CLUB", true);

        PreparedStatement statement;
        statement = connection.prepareStatement("SELECT * FROM actor_award");
        ResultSet rs = statement.executeQuery();
        rs.next();
        String error = "Bad Pitt was not found in DB\n";

        assertEquals(error, errContent);
    }


    @Test
    public void insertMovieGenreTest() throws SQLException{
        pop.InsertMovie(connection, "The Titanic", 1998, 194, "James Cameron", "Boat sinks", 7.9f);
        pop.InsertGenre(connection, "Drama");
        pop.InsertGenre(connection, "Romance");
        pop.InsertGenre(connection, "Comedy");

        pop.InsertMovieGenre(connection, 1, 2);

        PreparedStatement statement;
        statement = connection.prepareStatement("SELECT * FROM movie_genre");
        ResultSet rs = statement.executeQuery();
        rs.next();
        int id = rs.getInt("movie_id");

        assertEquals(1, id);
    }

    @Test
    public void insertBadMovieGenreTest() throws SQLException{
        pop.InsertMovie(connection, "The Titanic", 1998, 194, "James Cameron", "Boat sinks", 7.9f);
        pop.InsertGenre(connection, "Drama");
        pop.InsertGenre(connection, "Romance");
        pop.InsertGenre(connection, "Comedy");

        pop.InsertMovieGenre(connection, 2, 2);

        PreparedStatement statement;
        statement = connection.prepareStatement("SELECT * FROM movie_genre");
        ResultSet rs = statement.executeQuery();
        rs.next();
        int id = rs.getInt("movie_id");

        assertEquals(1, id);
    }

    @Test
    public void insertCastMemberTest() throws SQLException{
        pop.InsertActor(connection, "Brad", "Pitt", "1963-12-18");
        pop.InsertActor(connection, "Bad", "Pitt", "1963-12-19");
        pop.InsertActor(connection, "Brad", "Pie", "1963-12-20");
        pop.InsertMovie(connection, "Fight Club", 1999, 139, "David Fincher", "Crazy Soap", 9.8f);

        pop.linkMoviesActors(connection, "Brad", "Pitt", "Fight Club");

        PreparedStatement statement;
        statement = connection.prepareStatement("SELECT * FROM movie_cast");
        ResultSet rs = statement.executeQuery();
        rs.next();
        int id = rs.getInt("actor_id");

        assertEquals(1, id);
    }

    @Test
    public void insertBadCastMemberTest() throws SQLException{
        pop.InsertActor(connection, "Brad", "Pitt", "1963-12-18");
        pop.InsertActor(connection, "Bad", "Pitt", "1963-12-19");
        pop.InsertActor(connection, "Brad", "Pie", "1963-12-20");
        pop.InsertMovie(connection, "Fight Club", 1999, 139, "David Fincher", "Crazy Soap", 9.8f);

        pop.linkMoviesActors(connection, "Bad", "Pitt", "Fight Sword");

        PreparedStatement statement;
        statement = connection.prepareStatement("SELECT * FROM movie_cast");
        ResultSet rs = statement.executeQuery();
        rs.next();
        int id = rs.getInt("actor_id");

        // id of 0 means no entries
        assertEquals(0, id);
    }

    @Test
    public void populateMoviesTest() throws SQLException{
        pop.populateMovies(connection);
        PreparedStatement statement;
        statement = connection.prepareStatement("SELECT * FROM movie");
        ResultSet rs = statement.executeQuery();
        int recordCount = 0;
        while (rs.next()) {
            recordCount++;
        }

        assertEquals(50, recordCount);
    }

    @Test
    public void populateMoviesTest2() throws SQLException{
        pop.populateMovies(connection);
        PreparedStatement statement;
        statement = connection.prepareStatement("SELECT * FROM movie");
        ResultSet rs = statement.executeQuery();
        rs.next();
        String first_title = rs.getString("title");

        assertEquals("Forrest Gump", first_title);
    }

    @Test
    public void populateActorsTest() throws SQLException{
        pop.populateActors(connection);
        PreparedStatement statement;
        statement = connection.prepareStatement("SELECT * FROM actor");
        ResultSet rs = statement.executeQuery();
        int recordCount = 0;
        while (rs.next()) {
            recordCount++;
        }

        assertEquals(50, recordCount);
    }

    @Test
    public void populateActorsTest2() throws SQLException{
        pop.populateActors(connection);
        PreparedStatement statement;
        statement = connection.prepareStatement("SELECT * FROM actor");
        ResultSet rs = statement.executeQuery();
        rs.next();
        String first_name = rs.getString("first_name");

        assertEquals("Johnny", first_name);
    }

    @Test
    public void populateCastTest() throws SQLException{
        pop.populateMovies(connection);
        pop.populateActors(connection);
        pop.populateCast(connection);

        PreparedStatement statement;
        statement = connection.prepareStatement("SELECT * FROM movie_cast");
        ResultSet rs = statement.executeQuery();
        
        boolean exists = false;
        while (rs.next()) {
            int movie = rs.getInt("movie_id");
            int actor = rs.getInt("actor_id");

            if(movie == 1 && actor == 22){
                exists = true;
                break;
            }
        }

        assertTrue(exists);
    }

    @Test
    public void populateAwardsMovieTest() throws SQLException{
        pop.populateMovies(connection);
        pop.populateActors(connection);
        pop.populateAwards(connection);

        PreparedStatement statement;
        statement = connection.prepareStatement("SELECT * FROM movie_award");
        ResultSet rs = statement.executeQuery();
        
        boolean exists = false;
        while (rs.next()) {
            int movie = rs.getInt("movie_id");

            if(movie == 8){
                exists = true;
                break;
            }
        }

        assertTrue(exists);
    }

    @Test
    public void populateAwardsActorTest() throws SQLException{
        pop.populateMovies(connection);
        pop.populateActors(connection);
        pop.populateAwards(connection);

        PreparedStatement statement;
        statement = connection.prepareStatement("SELECT * FROM actor_award");
        ResultSet rs = statement.executeQuery();
        
        boolean exists = false;
        while (rs.next()) {
            int actor = rs.getInt("actor_id");

            if(actor == 7){
                exists = true;
                break;
            }
        }

        assertTrue(exists);
    }


    @After
    public void cleanup(){
    }
}
