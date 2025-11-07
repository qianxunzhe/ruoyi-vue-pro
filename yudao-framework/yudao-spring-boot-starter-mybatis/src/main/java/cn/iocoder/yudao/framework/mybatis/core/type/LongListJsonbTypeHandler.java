package cn.iocoder.yudao.framework.mybatis.core.type;

import com.fasterxml.jackson.core.type.TypeReference;

import java.util.List;

/**
 * List<Long> 类型的 JSONB 处理器
 *
 * @author Demons
 */
public class LongListJsonbTypeHandler extends JsonbTypeHandler {

    public LongListJsonbTypeHandler() {
        super(new TypeReference<List<Long>>() {});
    }
}
