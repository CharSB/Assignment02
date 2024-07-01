package tests;
import org.junit.Test;

import src.InitialiseDB;
import src.Movies;
import src.PopulateDB;
import src.QueryDB;

import org.junit.After;
import org.junit.Before;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class InitDBTests {
    
    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private final ByteArrayOutputStream errContent = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;
    private final PrintStream originalErr = System.err;
    InitialiseDB obj;

    @Before
    public void setup(){
        obj = new InitialiseDB();
        System.setOut(new PrintStream(outContent));
        System.setErr(new PrintStream(errContent));
    }

    @Test
    public void InitDBOKTest(){
        InitialiseDB.main(null);
        
        assertEquals("OK\n", outContent.toString());
    }

    @Test
    public void validDBTest(){
        obj.tryToAccessDB("test.db","src/createDB.sql");
        List<String> tables = new ArrayList<String>(Arrays.asList("actor", "actor_award", "movie", "movie_award", "movie_cast", "movie_genre", "genre"));
        boolean isValid = obj.validateDB("test.db",tables);
        assertTrue(isValid);
    }

    @Test
    public void badSchemaTest(){
        obj.tryToAccessDB("test.db","tests/badSchema.sql");
        List<String> tables = new ArrayList<String>(Arrays.asList("actor", "actor_award", "movie", "movie_award", "movie_cast", "movie_genre", "genre"));
        boolean isValid = obj.validateDB("test.db",tables);
        assertFalse(isValid);
    }

    @After
    public void cleanUp(){
        Movies.main(null);
    }
}
