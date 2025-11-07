# PostgreSQL 兼容性问题修复总结

## 已修复的问题

### 1. deleted字段布尔类型兼容性问题
**错误信息**: `operator does not exist: boolean = integer`

**原因**: MyBatis Plus的逻辑删除功能使用整数值（0/1），但PostgreSQL表中的deleted字段是BOOLEAN类型。

**解决方案**: 
- 将deleted字段从BOOLEAN改为SMALLINT类型
- 修改文件: `product_performance_pgsql.sql`
- 修复脚本: `fix_deleted_field.sql`

### 2. JSONB类型转换问题
**错误信息**: `column "small_cate_rank" is of type jsonb but expression is of type character varying`

**原因**: PostgreSQL需要显式的类型转换将字符串转为JSONB。

**解决方案**:
- 在Mapper XML中为所有JSONB字段添加`::jsonb`类型转换
- 修改文件: `ProductPerformanceMapper.xml`
- 示例: `#{item.smallCateRank, typeHandler=com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler, jdbcType=OTHER}::jsonb`

### 3. deleted字段非空约束违反
**错误信息**: `null value in column "deleted" of relation "amazon_product_performance" violates not-null constraint`

**原因**: 创建ProductPerformanceDO对象时没有设置基础字段的值。

**解决方案**:
- 在convertDtoToDo方法中显式设置所有非空字段的默认值
- 修改文件: `ProductPerformanceServiceImpl.java`
- 设置的字段:
  - deleted = 0 (通过反射设置为Integer类型)
  - creator = "system"
  - updater = "system"
  - createTime = LocalDateTime.now()
  - updateTime = LocalDateTime.now()
  - tenantId = 1L

### 4. deleted字段类型不匹配
**错误信息**: `column "deleted" is of type smallint but expression is of type boolean`

**原因**: BaseDO中deleted字段是Boolean类型，但PostgreSQL表中是SMALLINT类型，MyBatis Plus的逻辑删除配置使用整数值。

**解决方案**:
- 保持PostgreSQL表中deleted字段为SMALLINT类型
- 使用反射在运行时将deleted字段设置为Integer类型的0
- 这样既兼容了框架的逻辑删除配置，又解决了类型不匹配问题

## 执行步骤

1. 如果已有表结构，执行修复脚本:
   ```bash
   psql -U your_username -d your_database -f fix_deleted_field.sql
   ```

2. 如果是新建表，直接使用更新后的建表语句:
   ```bash
   psql -U your_username -d your_database -f product_performance_pgsql.sql
   ```

3. 重新编译并部署应用

### 5. 逻辑删除的Boolean到SMALLINT类型转换问题
**错误信息**: `column "deleted" is of type smallint but expression is of type boolean`

**原因**: MyBatis Plus在执行逻辑删除时，会尝试将deleted字段更新为Boolean值，但PostgreSQL表中是SMALLINT类型。

**解决方案**:
1. 在Mapper XML的insert语句中，使用`<choose>`标签将Boolean值转换为整数
2. 创建物理删除方法`physicalDelete`，绕过MyBatis Plus的逻辑删除机制
3. 修改文件: 
   - `ProductPerformanceMapper.xml` - 添加deleted字段的类型转换
   - `ProductPerformanceMapper.java` - 添加physicalDelete方法
   - `ProductPerformanceServiceImpl.java` - 使用物理删除替代逻辑删除

## 注意事项

1. PostgreSQL和MySQL在某些数据类型上有差异，需要特别注意:
   - 布尔类型: MySQL使用BIT(1)或TINYINT(1)，PostgreSQL使用BOOLEAN
   - JSON类型: MySQL使用JSON，PostgreSQL推荐使用JSONB
   - 自增主键: MySQL使用AUTO_INCREMENT，PostgreSQL使用SERIAL或BIGSERIAL

2. MyBatis Plus的逻辑删除功能默认使用整数值，如果要使用布尔值需要额外配置

3. 插入数据时确保所有非空字段都有值，特别是继承自BaseDO的基础字段

4. 当框架的BaseDO使用Boolean类型的deleted字段，而数据库使用SMALLINT时，需要在Mapper XML中进行类型转换