package org.imtp.server.storage;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import lombok.extern.slf4j.Slf4j;

import javax.sql.DataSource;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Slf4j
public class SqlHandler {
    private static final String USER_TABLE_SQL = "create table if not exists users (id bigint auto_increment primary key,account bigint UNIQUE not null,password varchar(48),name varchar(24))";
    private static final String HISTORY_MESSAGE_TABLE_SQL = "create table if not exists h_message (id bigint auto_increment primary key,sender bigint not null,receiver bigint not null,timestamp bigint not null,type int not null,status int not null,msg varchar(2048))";
    private static final String DRIVER_CLASS_NAME = "org.h2.Driver";
    private static final String JDBC_URL = "jdbc:h2:~/imtp_server;AUTO_SERVER=TRUE;MODE=MySQL";
    private static final int MAXIMUM_POOL_SIZE = 10;
    private static final HikariConfig hikariConfig;
    private static final DataSource dataSource;

    static {
        hikariConfig =new HikariConfig();
        hikariConfig.setDriverClassName(DRIVER_CLASS_NAME);
        hikariConfig.setJdbcUrl(JDBC_URL);
        hikariConfig.setMaximumPoolSize(MAXIMUM_POOL_SIZE);
        dataSource = new HikariDataSource(hikariConfig);

        Connection connection = null;
        Statement statement = null;
        try {
            connection = dataSource.getConnection();
            statement = connection.createStatement();
            //初始化用户表
            statement.execute(USER_TABLE_SQL);
            //初始化历史消息表
            statement.execute(HISTORY_MESSAGE_TABLE_SQL);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }finally {
            if(connection != null){
                try {
                    connection.close();
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            }
            if(statement != null){
                try {
                    statement.close();
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    public void init() throws SQLException {
        this.execute("insert into users(account,password,name) values(147,123456,'大鱼')");
        this.execute("insert into users(account,password,name) values(258,123456,'！')");
        this.execute("insert into users(account,password,name) values(369,123456,'。')");
    }

    public boolean execute(String sql) throws SQLException {
        try (Statement statement = getStatement()){
            return statement.execute(sql);
        }
    }

    public <T> T queryOne(String sql, Class<T> c){
        List<T> ts = queryList(sql, c);
        if (ts.isEmpty()){
            return null;
        }
        if (ts.size() > 1){
            throw new RuntimeException("查询1条，但是却返回了"+ ts.size() + "条");
        }
        return ts.get(0);
    }

    public <T> List<T> queryList(String sql, Class<T> c){
        List<T> r= new ArrayList<>();
        try (Statement statement = getStatement()){
            ResultSet resultSet = statement.executeQuery(sql);
            ResultSetMetaData metaData = resultSet.getMetaData();
            int columnCount = metaData.getColumnCount();
            while (resultSet.next()){
                T t = c.getDeclaredConstructor().newInstance();
                for (int i = 1; i <= columnCount; i++) {
                    String columnName = metaData.getColumnName(i);
                    Object value = resultSet.getObject(i);
                    Field field = c.getDeclaredField(columnName.toLowerCase());
                    if(value != null){
                        field.setAccessible(true);
                        if(field.getType() == Long.class || field.getType() == long.class){
                            field.set(t, resultSet.getLong(i));
                        }else if(field.getType() == Integer.class || field.getType() == int.class){
                            field.set(t, resultSet.getInt(i));
                        }else if(field.getType() == Short.class || field.getType() == short.class){
                            field.set(t, resultSet.getShort(i));
                        }else if(field.getType() == Byte.class || field.getType() == byte.class){
                            field.set(t, resultSet.getByte(i));
                        }else if(field.getType() == Float.class || field.getType() == float.class){
                            field.set(t, resultSet.getFloat(i));
                        }else if(field.getType() == Double.class || field.getType() == double.class){
                            field.set(t, resultSet.getDouble(i));
                        }else if(field.getType() == Boolean.class || field.getType() == boolean.class){
                            field.set(t, resultSet.getBoolean(i));
                        } else if(field.getType() == BigDecimal.class){
                            field.set(t, resultSet.getBigDecimal(i));
                        }else if(field.getType() == String.class){
                            field.set(t, resultSet.getString(i));
                        }else if(field.getType() == Date.class){
                            field.set(t, resultSet.getDate(i));
                        }
                    }
                }
                r.add(t);
            }
        } catch (NoSuchFieldException | IllegalAccessException | InvocationTargetException | InstantiationException |
                 NoSuchMethodException | SQLException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
        return r;
    }

    public Connection connection() throws SQLException {
        return dataSource.getConnection();
    }

    public Statement getStatement() throws SQLException {

        return connection().createStatement();
    }

    public Statement getStatement(Connection connection) throws SQLException {

        return connection.createStatement();
    }

    public void close(Statement statement) throws SQLException {
        if(statement == null || statement.isClosed()){
            return;
        }
        statement.close();
        Connection connection = statement.getConnection();
        close(connection);
    }

    public void close(Connection connection) throws SQLException {
        if(connection.isClosed()){
            return;
        }
        connection.close();
    }



}
