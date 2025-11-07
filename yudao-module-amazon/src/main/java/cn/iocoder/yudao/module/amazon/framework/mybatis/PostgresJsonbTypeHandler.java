package cn.iocoder.yudao.module.amazon.framework.mybatis;

import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedJdbcTypes;
import org.apache.ibatis.type.MappedTypes;
import org.postgresql.util.PGobject;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * PostgreSQL JSONB 类型处理器
 * 用于处理 PostgreSQL 的 jsonb 类型与 Java String 类型的转换
 *
 * @author Demons
 */
@MappedTypes(String.class)
@MappedJdbcTypes(JdbcType.OTHER)
public class PostgresJsonbTypeHandler extends BaseTypeHandler<String> {

    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, String parameter, JdbcType jdbcType) throws SQLException {
        PGobject jsonObject = new PGobject();
        jsonObject.setType("jsonb");
        // 如果参数为空或空字符串，设置为空JSON数组
        if (parameter == null || parameter.trim().isEmpty()) {
            jsonObject.setValue("[]");
        } else {
            jsonObject.setValue(parameter);
        }
        ps.setObject(i, jsonObject);
    }

    @Override
    public String getNullableResult(ResultSet rs, String columnName) throws SQLException {
        Object obj = rs.getObject(columnName);
        if (obj == null) {
            return null;
        }
        if (obj instanceof PGobject) {
            return ((PGobject) obj).getValue();
        }
        return obj.toString();
    }

    @Override
    public String getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        Object obj = rs.getObject(columnIndex);
        if (obj == null) {
            return null;
        }
        if (obj instanceof PGobject) {
            return ((PGobject) obj).getValue();
        }
        return obj.toString();
    }

    @Override
    public String getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        Object obj = cs.getObject(columnIndex);
        if (obj == null) {
            return null;
        }
        if (obj instanceof PGobject) {
            return ((PGobject) obj).getValue();
        }
        return obj.toString();
    }
}