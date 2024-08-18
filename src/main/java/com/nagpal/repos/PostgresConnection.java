package com.nagpal.repos;

import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import java.lang.reflect.InvocationTargetException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

@Log4j2
@Component
public final class PostgresConnection {

    private final String driver;
    private final String url;
    private final String username;
    private final String password;

    public PostgresConnection() {
        this.driver = null;
        this.url = null;
        this.username = null;
        this.password = null;
    }
    public PostgresConnection(String driver, String url, String username, String password) {
        this.driver = driver;
        this.url = url;
        this.username = username;
        this.password = password;
    }

    public Connection get() {
        log.debug("Driver : " + this.driver);
        log.debug("URL : " + this.url);
        log.debug("Username : " + this.username);
        log.debug("Password : " + this.password);

        try {
            try {
                Class.forName(driver).getDeclaredConstructor().newInstance();
            } catch (InvocationTargetException | NoSuchMethodException e) {
                throw new RuntimeException(e);
            }
            return DriverManager.getConnection(url, username, password);

        } catch (ClassNotFoundException | IllegalAccessException | InstantiationException ex) {
            throw new RuntimeException("Can not instantiate driver " + driver, ex);
        } catch (SQLException ex) {
            throw new RuntimeException("Can not connect to database", ex);
        }
    }
}

