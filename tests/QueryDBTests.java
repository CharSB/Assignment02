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
public class QueryDBTests {
    InitialiseDB init;
    PopulateDB pop;
    QueryDB q;
    Connection connection;

    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private final ByteArrayOutputStream errContent = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;
    private final PrintStream originalErr = System.err;

    @Before
    public void setup(){
        pop = new PopulateDB();
        init = new InitialiseDB();
        q = new QueryDB();

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
            pop.tryToAccessDB("test.db");
        } catch (Exception e) {
            // TODO: handle exception
        }
    }

    @Test
    public void case1Test(){
        String[] args = {"1"};
        QueryDB.main(args);

        assertTrue(outContent != null);
    }

    @Test
    public void movieNamesTest(){
        String[] args = {"1"};
        QueryDB.main(args);

        assertTrue(outContent.toString().contains("Fight Club"));
        assertTrue(outContent.toString().contains("Glory Road"));
    }

    @Test
    public void case2Test(){
        String[] args = {"2"};
        QueryDB.main(args);

        assertTrue(outContent != null);
    }

    @Test 
    public void fightClubCastTest(){
        String[] args = {"2", "Fight Club"};
        QueryDB.main(args);

        assertTrue(outContent.toString().contains("Edward Norton"));
    }

    @Test 
    public void ryanCastTest(){
        String[] args = {"2", "Saving Private Ryan"};
        QueryDB.main(args);

        assertTrue(outContent.toString().contains("Tom Hanks"));
    }

    @Test
    public void case3Test(){
        String[] args = {"3"};
        QueryDB.main(args);

        assertTrue(outContent != null);
    }

    @Test 
    public void forrestGumpTest(){
        String[] args = {"3", "Tom Hanks", "Robert Zemeckis"};
        QueryDB.main(args);

        assertTrue(outContent.toString().contains("Forrest Gump"));
    }

    @Test 
    public void theShiningTest(){
        String[] args = {"3", "Jack Nicholson", "Stanley Kubrick"};
        QueryDB.main(args);

        assertTrue(outContent.toString().contains("family"));
    }

    @Test
    public void case4Test(){
        String[] args = {"4"};
        QueryDB.main(args);

        assertTrue(outContent != null);
    }

    @Test 
    public void christianBaleTest(){
        String[] args = {"4", "Christian Bale"};
        QueryDB.main(args);

        assertTrue(outContent.toString().contains("Christopher Nolan"));
    }

    @Test 
    public void mattDamonTest(){
        String[] args = {"4", "Matt Damon"};
        QueryDB.main(args);

        assertTrue(outContent.toString().contains("Martin Scorsese"));
    }

    @Test
    public void case5Test(){
        String[] args = {"5"};
        QueryDB.main(args);

        assertTrue(outContent != null);
    }

    @Test 
    public void bradPittAwardsTest(){
        String[] args = {"5", "Brad Pitt"};
        QueryDB.main(args);

        assertTrue(outContent.toString().contains("2020"));
    }

    @Test 
    public void bradPittAwardsTest2(){
        String[] args = {"5", "Brad Pitt"};
        QueryDB.main(args);

        assertTrue(outContent.toString().contains("2009"));
    }

    @Test 
    public void mattDamonAwardsTest(){
        String[] args = {"5", "Matt Damon"};
        QueryDB.main(args);

        assertTrue(outContent.toString().contains("2016"));
    }

    @Test
    public void case6Test(){
        String[] args = {"6"};
        QueryDB.main(args);

        assertTrue(outContent != null);
    }

    @Test 
    public void Genres2009Test(){
        String[] args = {"6", "2009"};
        QueryDB.main(args);

        assertTrue(outContent.toString().contains("Action"));
    }

    @Test 
    public void Genres2009Test2(){
        String[] args = {"6", "2009"};
        QueryDB.main(args);

        assertTrue(outContent.toString().contains("Crime"));
    }
}
