package org.imtp.server.service;

import lombok.extern.slf4j.Slf4j;
import org.imtp.server.entity.HistoryMessage;
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
public class H2DBChatService implements ChatService {

    private SqlHandler sqlHandler;

    public H2DBChatService(SqlHandler sqlHandler){
        this.sqlHandler = sqlHandler;
    }

    @Override
    public User findByUserId(Long userId) {
        return sqlHandler.queryOne("select * from users where account = '" + userId + "'", User.class);
    }

    @Override
    public List<User> findFriendByUserId(Long userId) {
        return null;
    }

    @Override
    public List<User> findUserByGroupId(Long userId) {
        return null;
    }


    @Override
    public boolean saveHistoryMessage(HistoryMessage historyMessage) {
        StringBuilder  sql = new StringBuilder();
        sql.append("insert into h_message(sender,receiver,timestamp,type,status,msg) values(")
                .append(historyMessage.getSender()).append(",")
                .append(historyMessage.getReceiver()).append(",")
                .append(historyMessage.getTimestamp()).append(",")
                .append(historyMessage.getType()).append(",")
                .append(historyMessage.getStatus()).append(",")
                .append("'").append(historyMessage.getMsg()).append("'")
                .append(")");
        try {
            return sqlHandler.execute(sql.toString());
        } catch (SQLException e) {
            log.error("database exception ",e);
        }
        return false;
    }

    @Override
    public List<HistoryMessage> findHistoryMessageByUserId(Long userId) {
        return List.of();
    }
}
