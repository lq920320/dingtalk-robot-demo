package com.dingtalk.clients;

import com.dingtalk.model.client.ApiResult;
import com.dingtalk.model.client.HuangliResult;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * 黄历接口
 *
 * @author liuqian
 * created of 2024/7/26 10:00 for com.dingtalk.clients
 */
@FeignClient(name = "huangli", url = "${huangli.host.url}")
public interface HuangliClient {


    /**
     * 获取黄历
     *
     * @param sun 是阳历时间
     * @return 黄历信息
     */
    @GetMapping("/Commonweal/almanac")
    ApiResult<HuangliResult> getHuangli(@RequestParam("sun") String sun);

}
