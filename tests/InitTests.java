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
import java.io.PrintStream;

import static org.junit.Assert.assertFalse;
public class InitTests {
    InitialiseDB init;
    PopulateDB pop;
    QueryDB query;

    @Before
    public void setup(){
        init = new InitialiseDB();
        pop = new PopulateDB();
        query = new QueryDB();
    }

    @Test
    public void initInitialiseDBTest(){
        assertTrue(init != null);
    }

    @Test
    public void initPopulateDBTest(){
        assertTrue(pop != null);
    }

    @Test
    public void initQueryDBTest(){
        assertTrue(query != null);
    }
}
