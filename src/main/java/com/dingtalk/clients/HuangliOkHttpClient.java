package com.dingtalk.clients;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.TypeReference;
import com.dingtalk.model.client.ApiResult;
import com.dingtalk.model.client.HuangliResult;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * 黄历接口okHttpClient
 *
 * @author liuqian
 * created of 2024/7/26 15:34 for com.dingtalk.clients
 */
@Component
@Slf4j
public class HuangliOkHttpClient {

    @Value("${huangli.host.url}")
    private String BASE_URL;

    private static final OkHttpClient client = new OkHttpClient();

    /**
     * 获取黄历
     * 文档地址：https://api.aa1.cn/doc/almanac.html
     *
     * @param date 日期
     * @return {@link HuangliResult}
     */
    public HuangliResult getHuangli(String date) {
        Request request = new Request.Builder()
                .url(BASE_URL + "/Commonweal/almanac" + "?sun=" + date)
                .get().build();
        try (Response response = client.newCall(request).execute()) {
            assert response.body() != null;
            ApiResult<HuangliResult> result = JSON.parseObject(response.body().string(),
                    new TypeReference<>() {
                    });
            return result.getData();
        } catch (Exception e) {
            log.error("获取黄历失败，date:{}", date, e);
            return null;
        }
    }

}
