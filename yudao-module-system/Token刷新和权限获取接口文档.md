# Token刷新和权限获取接口文档

## 1. 刷新Token接口

### 接口概述
用于在访问令牌即将过期或已过期时，使用刷新令牌获取新的访问令牌和刷新令牌对。

### 接口信息
- **接口名称**：刷新访问令牌
- **接口路径**：`/admin-api/system/auth/refresh-token`
- **请求方式**：POST
- **Content-Type**：application/x-www-form-urlencoded
- **权限要求**：无需认证（@PermitAll）

### 请求参数

#### Query参数
| 参数名 | 类型 | 必填 | 说明 | 示例值 |
|--------|------|------|------|---------|
| refreshToken | String | 是 | 刷新令牌（登录时获得） | "6c1f7e85-86f2-4b91-bce2-1e0f3c3a5b8c" |

### 请求示例

#### cURL
```bash
curl -X POST "https://api.example.com/admin-api/system/auth/refresh-token?refreshToken=6c1f7e85-86f2-4b91-bce2-1e0f3c3a5b8c"
```

#### Form表单方式
```http
POST /admin-api/system/auth/refresh-token HTTP/1.1
Host: api.example.com
Content-Type: application/x-www-form-urlencoded

refreshToken=6c1f7e85-86f2-4b91-bce2-1e0f3c3a5b8c
```

### 响应参数

| 参数名 | 类型 | 说明 | 示例值 |
|--------|------|------|---------|
| code | Integer | 响应码（0=成功） | 0 |
| msg | String | 响应消息 | "成功" |
| data | Object | 响应数据 | - |
| data.userId | Long | 用户编号 | 1024 |
| data.accessToken | String | 新的访问令牌 | "a1b2c3d4-e5f6-7890-abcd-ef1234567890" |
| data.refreshToken | String | 新的刷新令牌 | "98765432-dcba-0987-6543-210fedcba987" |
| data.expiresTime | String | 访问令牌过期时间 | "2024-01-20T12:30:00" |

### 响应示例

#### 成功响应
```json
{
    "code": 0,
    "msg": "成功",
    "data": {
        "userId": 1024,
        "accessToken": "a1b2c3d4-e5f6-7890-abcd-ef1234567890",
        "refreshToken": "98765432-dcba-0987-6543-210fedcba987",
        "expiresTime": "2024-01-20T12:30:00"
    }
}
```

#### 失败响应
```json
{
    "code": 400,
    "msg": "无效的刷新令牌"
}
```

```json
{
    "code": 401,
    "msg": "刷新令牌已过期"
}
```

### 错误码说明
| 错误码 | 说明 | 处理建议 |
|--------|------|----------|
| 0 | 成功 | - |
| 400 | 无效的刷新令牌 | 令牌不存在或格式错误，需要重新登录 |
| 401 | 刷新令牌已过期 | 刷新令牌已超过30天有效期，需要重新登录 |

### 使用建议
1. **主动刷新策略**：建议在访问令牌过期前5分钟主动刷新
2. **被动刷新策略**：当API返回401错误时，自动尝试刷新Token
3. **刷新失败处理**：刷新失败时应引导用户重新登录
4. **Token存储更新**：刷新成功后需要更新本地存储的Token对

### Java示例
```java
public class TokenRefreshService {
    
    public TokenResponse refreshToken(String refreshToken) {
        RestTemplate restTemplate = new RestTemplate();
        
        // 构建请求参数
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("refreshToken", refreshToken);
        
        // 发送请求
        ResponseEntity<Map> response = restTemplate.postForEntity(
            "https://api.example.com/admin-api/system/auth/refresh-token?refreshToken=" + refreshToken,
            null,
            Map.class
        );
        
        // 处理响应
        if (response.getBody().get("code").equals(0)) {
            Map data = (Map) response.getBody().get("data");
            // 更新本地存储的Token
            updateStoredTokens(data);
            return new TokenResponse(data);
        } else {
            // 刷新失败，需要重新登录
            redirectToLogin();
        }
    }
}
```

---

## 2. 获取权限信息接口

### 接口概述
获取当前登录用户的完整权限信息，包括用户基本信息、角色列表、权限标识和菜单树。

### 接口信息
- **接口名称**：获取登录用户权限信息
- **接口路径**：`/admin-api/system/auth/get-permission-info`
- **请求方式**：GET
- **权限要求**：需要登录认证

### 请求参数

#### 请求头
| 参数名 | 类型 | 必填 | 说明 | 示例值 |
|--------|------|------|------|---------|
| Authorization | String | 是 | 访问令牌 | "Bearer a1b2c3d4-e5f6-7890-abcd-ef1234567890" |

### 请求示例
```bash
curl -X GET "https://api.example.com/admin-api/system/auth/get-permission-info" \
  -H "Authorization: Bearer a1b2c3d4-e5f6-7890-abcd-ef1234567890"
```

### 响应参数

#### 主体结构
| 参数名 | 类型 | 说明 |
|--------|------|------|
| code | Integer | 响应码（0=成功） |
| msg | String | 响应消息 |
| data | Object | 权限信息对象 |
| data.user | Object | 用户信息 |
| data.roles | Array[String] | 角色标识数组 |
| data.permissions | Array[String] | 权限标识数组 |
| data.menus | Array[Object] | 菜单树结构 |

#### 用户信息（data.user）
| 参数名 | 类型 | 说明 | 示例值 |
|--------|------|------|---------|
| id | Long | 用户编号 | 1024 |
| username | String | 用户账号 | "admin" |
| nickname | String | 用户昵称 | "管理员" |
| avatar | String | 用户头像URL | "https://www.example.com/avatar.jpg" |
| deptId | Long | 部门编号 | 2048 |
| email | String | 用户邮箱 | "admin@example.com" |

#### 菜单信息（data.menus[]）
| 参数名 | 类型 | 说明 | 示例值 |
|--------|------|------|---------|
| id | Long | 菜单ID | 1 |
| parentId | Long | 父菜单ID（0为顶级） | 0 |
| name | String | 菜单名称 | "系统管理" |
| path | String | 路由地址 | "/system" |
| component | String | 组件路径 | "Layout" |
| componentName | String | 组件名称 | "SystemLayout" |
| icon | String | 菜单图标 | "system" |
| visible | Boolean | 是否可见 | true |
| keepAlive | Boolean | 是否缓存 | true |
| alwaysShow | Boolean | 是否总是显示 | false |
| children | Array[Object] | 子菜单列表 | [] |

### 响应示例

```json
{
    "code": 0,
    "msg": "成功",
    "data": {
        "user": {
            "id": 1024,
            "username": "admin",
            "nickname": "管理员",
            "avatar": "https://www.example.com/avatar.jpg",
            "deptId": 2048,
            "email": "admin@example.com"
        },
        "roles": [
            "admin",
            "super_admin"
        ],
        "permissions": [
            "system:user:create",
            "system:user:update",
            "system:user:delete",
            "system:user:query",
            "system:role:create",
            "system:role:update",
            "system:role:delete",
            "system:role:query"
        ],
        "menus": [
            {
                "id": 1,
                "parentId": 0,
                "name": "系统管理",
                "path": "/system",
                "component": "Layout",
                "componentName": "SystemLayout",
                "icon": "system",
                "visible": true,
                "keepAlive": true,
                "alwaysShow": true,
                "children": [
                    {
                        "id": 100,
                        "parentId": 1,
                        "name": "用户管理",
                        "path": "user",
                        "component": "system/user/index",
                        "componentName": "SystemUser",
                        "icon": "user",
                        "visible": true,
                        "keepAlive": true,
                        "alwaysShow": false,
                        "children": []
                    },
                    {
                        "id": 101,
                        "parentId": 1,
                        "name": "角色管理",
                        "path": "role",
                        "component": "system/role/index",
                        "componentName": "SystemRole",
                        "icon": "peoples",
                        "visible": true,
                        "keepAlive": true,
                        "alwaysShow": false,
                        "children": []
                    }
                ]
            },
            {
                "id": 2,
                "parentId": 0,
                "name": "基础设施",
                "path": "/infra",
                "component": "Layout",
                "componentName": "InfraLayout",
                "icon": "monitor",
                "visible": true,
                "keepAlive": true,
                "alwaysShow": true,
                "children": []
            }
        ]
    }
}
```

### 错误码说明
| 错误码 | 说明 | 处理建议 |
|--------|------|----------|
| 0 | 成功 | - |
| 401 | 未认证或Token无效 | 需要重新登录或刷新Token |
| 403 | 无权限 | 用户无权访问该接口 |

### 使用场景

#### 1. 前端初始化
登录成功后，前端应立即调用此接口获取权限信息，用于：
- 渲染用户信息（头像、昵称等）
- 生成动态菜单
- 初始化权限控制

#### 2. 权限判断
```javascript
// 判断是否有某个权限
function hasPermission(permission) {
    const permissions = store.getState().auth.permissions;
    return permissions.includes(permission);
}

// 使用示例
if (hasPermission('system:user:create')) {
    // 显示创建用户按钮
}
```

#### 3. 动态路由生成
```javascript
// 根据菜单数据生成Vue Router路由
function generateRoutes(menus) {
    return menus.map(menu => {
        const route = {
            path: menu.path,
            name: menu.componentName,
            component: () => import(`@/views/${menu.component}`),
            meta: {
                title: menu.name,
                icon: menu.icon,
                keepAlive: menu.keepAlive
            }
        };
        
        if (menu.children && menu.children.length > 0) {
            route.children = generateRoutes(menu.children);
        }
        
        return route;
    });
}
```

### 前端集成示例

#### Vue 3 + Pinia
```javascript
// stores/auth.js
import { defineStore } from 'pinia';
import { getPermissionInfo } from '@/api/auth';

export const useAuthStore = defineStore('auth', {
    state: () => ({
        user: null,
        roles: [],
        permissions: [],
        menus: []
    }),
    
    actions: {
        async fetchPermissionInfo() {
            try {
                const { data } = await getPermissionInfo();
                this.user = data.user;
                this.roles = data.roles;
                this.permissions = data.permissions;
                this.menus = data.menus;
                return data;
            } catch (error) {
                console.error('获取权限信息失败:', error);
                throw error;
            }
        }
    }
});
```

#### React + Redux
```javascript
// actions/authActions.js
export const fetchPermissionInfo = () => async (dispatch) => {
    try {
        const response = await api.get('/system/auth/get-permission-info');
        const { data } = response.data;
        
        dispatch({
            type: 'SET_PERMISSION_INFO',
            payload: data
        });
        
        return data;
    } catch (error) {
        dispatch({
            type: 'PERMISSION_INFO_ERROR',
            payload: error.message
        });
    }
};
```

### 注意事项

1. **缓存策略**：权限信息可以在前端缓存，但需要在以下情况清除：
   - 用户登出
   - Token刷新
   - 用户权限变更通知

2. **性能优化**：
   - 权限信息较大时可考虑压缩传输
   - 可以增加版本号机制，减少重复获取

3. **安全考虑**：
   - 前端权限控制仅用于UI展示，真正的权限验证必须在后端进行
   - 敏感操作需要二次确认

4. **菜单过滤**：
   - 后端已过滤禁用的角色和菜单
   - 前端可根据visible属性进一步控制显示

## 接口调用流程图

```mermaid
sequenceDiagram
    participant Client as 客户端
    participant API as API服务器
    participant Redis as Redis缓存
    participant DB as 数据库

    Note over Client,DB: 登录流程
    Client->>API: 登录请求
    API->>DB: 验证用户
    API->>Redis: 存储Token
    API-->>Client: 返回Token对

    Note over Client,DB: 获取权限信息
    Client->>API: 请求权限信息(带Token)
    API->>Redis: 验证Token
    API->>DB: 查询用户权限
    API-->>Client: 返回权限信息

    Note over Client,DB: Token刷新流程
    Client->>API: 刷新Token请求
    API->>DB: 验证RefreshToken
    API->>Redis: 更新Token
    API-->>Client: 返回新Token对
```

## 最佳实践

1. **Token自动刷新机制**
```javascript
// axios拦截器示例
axios.interceptors.response.use(
    response => response,
    async error => {
        if (error.response?.status === 401) {
            const refreshToken = localStorage.getItem('refreshToken');
            if (refreshToken) {
                try {
                    const { data } = await refreshTokenAPI(refreshToken);
                    localStorage.setItem('accessToken', data.accessToken);
                    localStorage.setItem('refreshToken', data.refreshToken);
                    // 重试原请求
                    return axios(error.config);
                } catch {
                    // 刷新失败，跳转登录
                    router.push('/login');
                }
            }
        }
        return Promise.reject(error);
    }
);
```

2. **权限指令封装**
```vue
<!-- Vue自定义指令 -->
<template>
    <button v-permission="'system:user:create'">创建用户</button>
</template>

<script>
// 指令定义
app.directive('permission', {
    mounted(el, binding) {
        const permissions = store.state.auth.permissions;
        if (!permissions.includes(binding.value)) {
            el.style.display = 'none';
        }
    }
});
</script>
```