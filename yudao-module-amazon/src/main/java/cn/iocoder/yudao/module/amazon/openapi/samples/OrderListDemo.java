package cn.iocoder.yudao.module.amazon.openapi.samples;

import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson.JSON;
import cn.iocoder.yudao.module.amazon.openapi.core.Config;
import cn.iocoder.yudao.module.amazon.openapi.core.HttpMethod;
import cn.iocoder.yudao.module.amazon.openapi.core.HttpRequest;
import cn.iocoder.yudao.module.amazon.openapi.core.HttpResponse;
import cn.iocoder.yudao.module.amazon.openapi.okhttp.HttpExecutor;
import cn.iocoder.yudao.module.amazon.openapi.sign.ApiSign;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class OrderListDemo {

    public static void main(String[] args) throws Exception {
        String appId = "xx";

        Map<String, Object> queryParam = new HashMap<>();
        queryParam.put("timestamp", System.currentTimeMillis() / 1000 + "");
        queryParam.put("access_token", "805181e7-4365-468c-bf5c-8c0762a1d358");
        queryParam.put("app_key", appId);

        Map<String, Object> body = new HashMap<>();
        // 如果入参中存在集合，出现了 api sign not correct	报文；建议如下传参方式：
        // 1. 将集合json后传入
        List<Integer> sids = Arrays.asList(90123, 45091);
        body.put("sids", JSONUtil.toJsonStr(sids));


        Map<String, Object> signMap = new HashMap<>();
        signMap.putAll(queryParam);
        signMap.putAll(body);

        String sign = ApiSign.sign(signMap, appId);
        // 2. 在计算sign后，重新将集合字段覆盖为集合；步骤1将集合json是为了计算sign避免出现sign校验不通过。
        body.put("sids", sids);
        queryParam.put("sign", sign);

        HttpRequest<Object> build = HttpRequest.builder(Object.class)
                .method(HttpMethod.POST)
                .endpoint("xxx")
                .path("erp/sc/data/mws_report/allOrders")
                .queryParams(queryParam)
                .json(JSON.toJSONString(body))
                .config(Config.DEFAULT.withConnectionTimeout(30000).withReadTimeout(30000))
                .build();
        HttpResponse execute = HttpExecutor.create().execute(build);
    }

}
