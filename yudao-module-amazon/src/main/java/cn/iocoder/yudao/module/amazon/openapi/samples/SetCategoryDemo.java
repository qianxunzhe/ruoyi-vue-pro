package cn.iocoder.yudao.module.amazon.openapi.samples;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import cn.iocoder.yudao.module.amazon.openapi.core.HttpMethod;
import cn.iocoder.yudao.module.amazon.openapi.core.HttpRequest;
import cn.iocoder.yudao.module.amazon.openapi.core.HttpResponse;
import cn.iocoder.yudao.module.amazon.openapi.okhttp.HttpExecutor;
import cn.iocoder.yudao.module.amazon.openapi.sign.ApiSign;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
public class SetCategoryDemo {
    public static void main(String[] args) throws Exception {
        String appId = "xxx";
        Map<String, Object> queryParam = new HashMap<>();
        queryParam.put("timestamp", System.currentTimeMillis() / 1000 + "");
        queryParam.put("app_key", appId);
        queryParam.put("access_token", "0a482d2d-8280-48da-b69b-8f7c7d5c3c21");
        Map<String, Object> body = new HashMap<>();
        List<Map<String, String>> bodyParam = new ArrayList<>();
        Map<String, String> body_content = new HashMap<>();
        body_content.put("title","Weaving Loom 织机产品线");
        bodyParam.add(body_content);
        body.put("data", JSONObject.toJSONString(bodyParam));
        Map<String, Object> signMap = new HashMap<>();
        signMap.putAll(queryParam);
        signMap.putAll(body);
        body.put("data", bodyParam);
        String sign = ApiSign.sign(signMap, appId);
        queryParam.put("sign", sign);
        HttpRequest<Object> build = HttpRequest.builder(Object.class)
                .method(HttpMethod.POST)
                .endpoint("xxx")
                .path("erp/sc/routing/storage/category/set")
                .queryParams(queryParam)
                .json(JSON.toJSONString(body))
                .build();
        HttpResponse execute = HttpExecutor.create().execute(build);
    }
}
