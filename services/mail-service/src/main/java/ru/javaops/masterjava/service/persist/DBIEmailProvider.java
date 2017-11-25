package ru.javaops.masterjava.service.persist;

import com.typesafe.config.Config;
import ru.javaops.masterjava.persist.config.Configs;
import ru.javaops.masterjava.persist.model.DBIProvider;

import java.sql.DriverManager;

public class DBIEmailProvider {
    public static void initDBI() {
        Config db = Configs.getConfig("persist.conf","db");
        initDBI(db.getString("url"), db.getString("user"), db.getString("password"));
    }

    public static void initDBI(String dbUrl, String dbUser, String dbPassword) {
        DBIProvider.init(() -> {
            try {
                Class.forName("org.postgresql.Driver");
            } catch (ClassNotFoundException e) {
                throw new IllegalStateException("PostgreSQL driver not found", e);
            }
            return DriverManager.getConnection(dbUrl, dbUser, dbPassword);
        });
    }
}