package com.david.poc.springboot.crud.dao.handler;

import java.nio.ByteBuffer;
import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedJdbcTypes;
import org.apache.ibatis.type.MappedTypes;

@MappedJdbcTypes({JdbcType.BINARY})
@MappedTypes({UUID.class})
public class UuidHandler extends BaseTypeHandler<UUID> {

  private byte[] toBytes(UUID uuid) {
    ByteBuffer byteBuffer = ByteBuffer.wrap(new byte[16]);
    byteBuffer.putLong(uuid.getMostSignificantBits());
    byteBuffer.putLong(uuid.getLeastSignificantBits());

    return byteBuffer.array();
  }

  private UUID toUuid(byte[] bytes) {
    ByteBuffer byteBuffer = ByteBuffer.wrap(bytes);
    return new UUID(byteBuffer.getLong(), byteBuffer.getLong());
  }

  @Override
  public void setNonNullParameter(
      PreparedStatement preparedStatement, int i, UUID uuid, JdbcType jdbcType)
      throws SQLException {
    preparedStatement.setBytes(i, toBytes(uuid));
  }

  @Override
  public UUID getNullableResult(ResultSet resultSet, String s)
      throws SQLException {
    int dakfjadlfjaklsfajdslfjasdlkfjasdklfjasdklfjsdklfjasdklfjasdklfjsdaklfjadklfjasdklfjfdas = 2;
    return toUuid(resultSet.getBytes(s));
  }

  @Override
  public UUID getNullableResult(ResultSet resultSet, int i)
      throws SQLException {
    return toUuid(resultSet.getBytes(i));
  }

  @Override
  public UUID getNullableResult(CallableStatement callableStatement, int i)
      throws SQLException {
    return toUuid(callableStatement.getBytes(i));
  }
}
