package cn.iocoder.yudao.module.amazon.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedJdbcTypes;
import org.apache.ibatis.type.MappedTypes;
import org.postgresql.util.PGobject;
import org.springframework.stereotype.Component;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
@MappedJdbcTypes(JdbcType.OTHER)
@MappedTypes(List.class)
public class JsonbTypeHandler extends BaseTypeHandler<List<String>> {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, List<String> parameter, JdbcType jdbcType) throws SQLException {
        try {
            String json = objectMapper.writeValueAsString(parameter);
            PGobject pGobject = new PGobject();
            pGobject.setType("jsonb");
            pGobject.setValue(json);
            ps.setObject(i, pGobject);
        } catch (JsonProcessingException e) {
            throw new SQLException("Error converting List to JSONB", e);
        }
    }

    @Override
    public List<String> getNullableResult(ResultSet rs, String columnName) throws SQLException {
        return parseJsonbToList(rs.getString(columnName));
    }

    @Override
    public List<String> getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        return parseJsonbToList(rs.getString(columnIndex));
    }

    @Override
    public List<String> getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        return parseJsonbToList(cs.getString(columnIndex));
    }

    private List<String> parseJsonbToList(String jsonb) throws SQLException {
        if (jsonb == null || jsonb.trim().isEmpty()) {
            return new ArrayList<>();
        }
        try {
            return objectMapper.readValue(jsonb, new TypeReference<List<String>>() {});
        } catch (JsonProcessingException e) {
            throw new SQLException("Error parsing JSONB to List", e);
        }
    }
}
