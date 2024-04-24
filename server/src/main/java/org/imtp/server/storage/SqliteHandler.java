package org.imtp.server.storage;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.sqlite.SQLiteConfig;
import org.sqlite.SQLiteDataSource;

import java.sql.Connection;
import java.sql.SQLException;

public class SqliteHandler {

    private static final String BASE_PATH = "jdbc:sqlite:server/src/main/resources/storage/";

    public static void main(String[] args) throws SQLException {
        SqliteHandler sqliteHandler = new SqliteHandler();

        HikariConfig hikariConfig = new HikariConfig();
        hikariConfig.setDriverClassName("org.sqlite.JDBC");
        hikariConfig.setJdbcUrl("jdbc:sqlite:server/src/main/resources/storage/imtp_server.db");
        hikariConfig.setMaximumPoolSize(10);
    }

    public Connection openConnection() throws SQLException {
        SQLiteConfig sqLiteConfig = new SQLiteConfig();
        SQLiteDataSource sqLiteDataSource = new SQLiteDataSource(sqLiteConfig);
        String url = BASE_PATH;
        sqLiteDataSource.setUrl(url);
        return sqLiteDataSource.getConnection();
    }

}
