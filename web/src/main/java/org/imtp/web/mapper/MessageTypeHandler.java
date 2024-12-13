package org.imtp.web.mapper;

import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.imtp.common.enums.MessageType;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @Description
 * @Author ys
 * @Date 2024/12/13 14:49
 */
public class MessageTypeHandler extends BaseTypeHandler<MessageType> {

    public MessageTypeHandler(){}

    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, MessageType parameter, JdbcType jdbcType) throws SQLException {
        ps.setInt(i,parameter.getValue());
    }

    @Override
    public MessageType getNullableResult(ResultSet rs, String columnName) throws SQLException {
        int messageType = rs.getInt(columnName);
        return messageType == 0 && rs.wasNull() ? null : MessageType.findMessageTypeByValue(messageType);
    }

    @Override
    public MessageType getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        int messageType = rs.getInt(columnIndex);
        return messageType == 0 && rs.wasNull() ? null : MessageType.findMessageTypeByValue(messageType);
    }

    @Override
    public MessageType getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        int messageType = cs.getInt(columnIndex);
        return messageType == 0 && cs.wasNull() ? null : MessageType.findMessageTypeByValue(messageType);
    }
}
