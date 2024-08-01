package com.dingtalk;

import com.alibaba.fastjson2.JSON;
import com.dingtalk.clients.HuangliClient;
import com.dingtalk.clients.HuangliOkHttpClient;
import com.dingtalk.model.client.ApiResult;
import com.dingtalk.model.client.HuangliResult;
import jakarta.annotation.Resource;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.Objects;


/**
 * 黄历测试
 *
 * @author liuqian
 * created of 2024/7/26 10:33 for PACKAGE_NAME
 */
@SpringBootTest(classes = RobotApplication.class)
class HuangliTests {

    @Resource
    private HuangliClient huangliClient;
    @Resource
    private HuangliOkHttpClient huangliOkHttpClient;

    private static final String DATE_PATTERN = "yyyy-MM-dd";
//
//    @Test
//    void test() {
//        ApiResult<HuangliResult> huangli = huangliClient.getHuangli("2024-07-26");
//        System.out.println(JSON.toJSONString(huangli));
//        Assertions.assertTrue(true);
//    }


    @Test
    void okhttpTest() {
        String huangli = huangliContent("2024-10-01 黄历");
        System.out.println(huangli);
        Assertions.assertTrue(true);
    }

    @Test
    void weekDayTest() {
        String weekDay = fetchWeekDay("2024-08-01");
        System.out.println(weekDay);
        Assertions.assertTrue(true);
    }

    private String huangliContent(String content) {
        if (StringUtils.isBlank(content)) {
            return "";
        }
        Date today = new Date();
        String date = DateFormatUtils.format(today, DATE_PATTERN);
        if (content.contains("明天")) {
            date = DateFormatUtils.format(DateUtils.addDays(today, 1), DATE_PATTERN);
        } else if (content.contains("-")) {
            date = content.replace("黄历", "").trim();
        }
        // 获取星期几
        String weekDay = fetchWeekDay(date);
        HuangliResult huangli = huangliOkHttpClient.getHuangli(date);
        // 构建黄历返回内容
        StringBuilder sendContent = new StringBuilder("\n");
        sendContent.append("日期：").append(huangli.getGregorianDateTime()).append("\n");
        sendContent.append("农历：").append(huangli.getTianGanDiZhiYear())
                .append(huangli.getLYear()).append("年")
                .append(huangli.getLMonth()).append(huangli.getLDay()).append("\n");
        sendContent.append("星期：").append(weekDay).append("\n");
        sendContent.append("节气：")
                .append(StringUtils.isBlank(huangli.getSolarTermName()) ? "-" : huangli.getSolarTermName())
                .append("\n");
        sendContent.append("农历节日：")
                .append(StringUtils.isBlank(huangli.getLJie()) ? "-" : huangli.getLJie())
                .append("\n");
        sendContent.append("公历节日：")
                .append(StringUtils.isBlank(huangli.getGJie()) ? "-" : huangli.getGJie())
                .append("\n");
        sendContent.append("宜：").append(huangli.getYi()).append("\n");
        sendContent.append("忌：").append(huangli.getJi()).append("\n");
        sendContent.append("神位：").append(huangli.getShenWei()).append("\n");
        sendContent.append("岁煞：").append(huangli.getSuiSha()).append("\n");
        sendContent.append("冲煞：").append(huangli.getChong()).append("\n");
        sendContent.append("天干地支月：").append(huangli.getTianGanDiZhiMonth()).append("\n");
        sendContent.append("天干地支日：").append(huangli.getTianGanDiZhiDay()).append("\n");
        sendContent.append("彭祖百忌：").append(huangli.getPengZu()).append("\n");
        sendContent.append("五行甲子：").append(huangli.getWuxingJiazi()).append("\n");
        return sendContent.toString();
    }

    private String fetchWeekDay(String date) {
        Date targetDate = null;
        try {
            targetDate = DateUtils.parseDate(date, "yyyy-MM-dd");
        } catch (ParseException ignored) {
        }
        if (Objects.isNull(targetDate)) {
            return "";
        }
        Calendar calendar = DateUtils.toCalendar(targetDate);
        int weekday = calendar.get(Calendar.DAY_OF_WEEK);
        return switch (weekday) {
            case 1 -> "星期日";
            case 2 -> "星期一";
            case 3 -> "星期二";
            case 4 -> "星期三";
            case 5 -> "星期四";
            case 6 -> "星期五";
            case 7 -> "星期六";
            default -> "";
        };
    }
}
