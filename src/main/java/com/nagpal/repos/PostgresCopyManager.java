package com.nagpal.repos;

import com.nagpal.config.Configuration;
import com.nagpal.entity.Person;
import lombok.extern.log4j.Log4j2;
import org.postgresql.copy.CopyManager;
import org.postgresql.jdbc.PgConnection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

@Log4j2
@Component
public final class PostgresCopyManager {

    private static final String SQL = "COPY person (first_name,last_name,full_name,address) " +
            "FROM STDIN WITH" +
            " " +
            "                               (FORMAT TEXT, ENCODING 'UTF-8', DELIMITER '\t', HEADER false)";

    @Autowired
    private Configuration config;

    public void prepareAndSaveToDB(final List<Person> personList) {
        try (Connection connection = getConnection()) {
            PgConnection unwrapped = connection.unwrap(PgConnection.class);
            CopyManager copyManager = unwrapped.getCopyAPI();

            String data = prepareDBSaveString(personList);
            pushTODB(copyManager, data);
        } catch (SQLException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    private Connection getConnection() {
        PostgresConnection postgresConnection = new PostgresConnection(config.getDriver(), config.getUrl(), config.getUsername(),
                config.getPassword());
        return postgresConnection.get();
    }

    private String prepareDBSaveString(List<Person> personList) {
        StringBuilder sb = new StringBuilder();

        personList.forEach(person -> {
                sb.append(person.getFirstName())
                        .append("\t")
                        .append(person.getLastName())
                        .append("\t")
                        .append(person.getFullName())
                        .append("\t")
                        .append(person.getAddress())
                        .append("\n");
        });
        return sb.toString();
    }

    private void pushTODB(final CopyManager copyManager, final String data) throws SQLException, IOException {

        InputStream inputStream = new ByteArrayInputStream(data.getBytes());
        copyManager.copyIn(SQL, inputStream);
    }
}
