package org.imtp.server.service;

import lombok.extern.slf4j.Slf4j;
import org.imtp.server.entity.User;
import org.imtp.server.storage.SqlHandler;

import java.sql.SQLException;
import java.util.List;

/**
 * @Description
 * @Author ys
 * @Date 2024/4/25 12:37
 */
@Slf4j
public class LocalUserService implements UserService{

    private SqlHandler sqlHandler;

    public LocalUserService(SqlHandler sqlHandler){
        this.sqlHandler = sqlHandler;
    }

    @Override
    public boolean save(User user) {
        StringBuilder sql = new StringBuilder();
        sql.append("insert into users(account,name) values(")
                .append(user.getAccount())
                .append(user.getName())
                .append(")");
        try {
            return sqlHandler.execute(sql.toString());
        } catch (SQLException e) {
            log.error("database exception ",e);
        }
        return false;
    }

    @Override
    public User findByUserId(Long userId) {
        StringBuilder sql = new StringBuilder();
        sql.append("select * from users where account = '").append(userId).append("'");
        return sqlHandler.queryOne(sql.toString(), User.class);
    }

    public static void main(String[] args) {
        LocalUserService localUserService = new LocalUserService(new SqlHandler());
        User user = localUserService.findByUserId(147L);
        System.out.println(user);
    }

    @Override
    public List<User> findFriendByUserId(Long userId) {
        return null;
    }

    @Override
    public List<User> findUserByGroupId(Long userId) {
        return null;
    }
}
