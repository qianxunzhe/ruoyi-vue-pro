package cn.iocoder.yudao.module.amazon.openapi.okhttp;

import cn.iocoder.yudao.module.amazon.openapi.core.Config;
import cn.iocoder.yudao.module.amazon.openapi.entity.Result;

import java.util.Map;

public class AKRestClientBuild {

    public static AKRestClientBuild.AKRestClientBuilder builder() {
        return new AKRestClientBuild.AKRestClientBuilder();
    }

    public static class AKRestClientBuilder {

        private Config config;

        private String endpoint;

        public AKRestClientBuild.AKRestClientBuilder endpoint(String endpoint) {
            this.endpoint = endpoint;
            return this;
        }

        public AKRestClientBuild.AKRestClientBuilder endpoint(Config config) {
            this.config = config;
            return this;
        }

        public Result getAccessToken(String appId, String appSecret) throws Exception {
            AKRestClient akRestClient = new AKRestClient(endpoint, config);
            return akRestClient.getAccessToken(appId, appSecret);
        }

        public Object refreshToken(String appId, String refreshToken) throws Exception {
            AKRestClient akRestClient = new AKRestClient(endpoint, config);
            return akRestClient.refreshToken(appId, refreshToken);
        }

        public Object sign(Map<String, Object> params) throws Exception {
            AKRestClient akRestClient = new AKRestClient(endpoint, config);
            return akRestClient.sign(params);
        }
    }

}
