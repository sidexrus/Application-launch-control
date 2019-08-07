package com;

import org.neo4j.driver.v1.*;

public class DBConnection {
    Driver driver;
    Session session;

    public DBConnection(String server, String user, String password){
        driver = GraphDatabase.driver(server, AuthTokens.basic(user, password));
        session = driver.session();
    }
}
