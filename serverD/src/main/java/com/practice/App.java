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
        ArrayList<String> apps = new ArrayList<String>();
        apps.add("HelloWorld");
        apps.add("lel");
        ArrayList res = SequenceFinder.FindSequence(apps, conn);
        int a=0;
        a++;
        /*
        conn.session.run("CREATE (baeldung:Company {name:\"Baeldung\"}) " +
                "RETURN baeldung");*/
        /*
        StatementResult result;
        result = conn.session.run("MATCH (a:App) RETURN a");
        while ( result.hasNext() )
        {
            Record res = result.next();
            Value v = res.get(0);
            String t = v.get("name").asString();
            String a = v.get("id").asString();
            int id = (int)v.asNode().id();

            List<Pair<String,Value>> values = res.fields();
            for (Pair<String,Value> nameValue: values) {
                if ("a".equals(nameValue.key())) {  // you named your node "p"
                    Value value = nameValue.value();
                    // print Book title and author
                    String title = value.get("name").asString();
                    String author = value.get("id").asString();
                }
            }
        }*/
    }
}
