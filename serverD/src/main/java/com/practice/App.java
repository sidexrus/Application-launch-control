package com.practice;

import org.neo4j.driver.v1.Record;
import org.neo4j.driver.v1.StatementResult;
import org.neo4j.driver.v1.Value;
import org.neo4j.driver.v1.util.Pair;
import java.util.ArrayList;
import java.util.List;

public class App {
    public static void main( String[] args ) {
        System.out.println( "Hello World!" );
        DBConnection conn = new DBConnection("bolt://localhost:7687",
                "neo4j", "antropik2");
        String app_json = "{\"applications\":[\"1\", \"App8\", \"App10\", \"App6\"]}";
        ArrayList<String> apps = AppListWorker.ToArrayList(app_json);
        ArrayList res = SequenceFinder.FindSequenceWithCycles(apps, conn);
        String sequence = AppListWorker.ToJson(res);
    }
}
