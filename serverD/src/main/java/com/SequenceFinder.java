package com;

import org.neo4j.driver.v1.Record;
import org.neo4j.driver.v1.StatementResult;
import org.neo4j.driver.v1.Value;

import java.util.ArrayList;
import java.util.Collections;

public class SequenceFinder {

    private static ArrayList<String> Find(ArrayList<String> apps, DBConnection conn){
        StatementResult query_result;
        ArrayList<String> result = new ArrayList<String>();
        ArrayList<String> childs;
        for(String app: apps){
            query_result = conn.session.run("MATCH (a{name: \"" + app + "\"})-[r:Depend]->(d:App) RETURN d");
            childs = new ArrayList<String>();
            while( query_result.hasNext()) {
                Record res = query_result.next();
                String child = res.get(0).get("name").asString();
                childs.add(child);
            }
            result.add(app);
            result.addAll(Find(childs, conn));
        }
        return result;
    }

    private static <T> ArrayList<T> removeDuplicates(ArrayList<T> list)
    {
        ArrayList<T> newList = new ArrayList<T>();
        for (T element : list) {
            if (!newList.contains(element)) {

                newList.add(element);
            }
        }
        return newList;
    }

    public static ArrayList<String> FindSequence(ArrayList<String> apps, DBConnection conn) {
        ArrayList<String> result = Find(apps, conn);
        Collections.reverse(result);
        result = removeDuplicates(result);
        return result;
    }
}

