package com.dingtalk.model.client;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * 黄历返回结果
 *
 * @author liuqian
 * created of 2024/7/26 10:20 for com.dingtalk.model.client
 */
@Data
public class HuangliResult {
    /**
     * 公历日期时间
     */
    @JsonProperty("GregorianDateTime")
    private String gregorianDateTime;

    /**
     * 农历日期时间
     */
    @JsonProperty("LunarDateTime")
    private String lunarDateTime;

    /**
     * 是否是节假日
     */
    @JsonProperty("IsJieJia")
    private Boolean isJieJia;

    /**
     * 农历节日
     */
    @JsonProperty("LJie")
    private String lJie;

    /**
     * 公历节日
     */
    @JsonProperty("GJie")
    private String gJie;

    /**
     * 宜
     */
    @JsonProperty("Yi")
    private String yi;

    /**
     * 忌
     */
    @JsonProperty("Ji")
    private String ji;

    /**
     * 神位
     */
    @JsonProperty("ShenWei")
    private String shenWei;

    /**
     * 胎神
     */
    @JsonProperty("Taishen")
    private String taishen;

    /**
     * 冲煞
     */
    @JsonProperty("Chong")
    private String chong;

    /**
     * 岁煞
     */
    @JsonProperty("SuiSha")
    private String suiSha;

    /**
     * 五行甲子
     */
    @JsonProperty("WuxingJiazi")
    private String wuxingJiazi;

    /**
     * 纳音五行年
     */
    @JsonProperty("WuxingNaYear")
    private String wuxingNaYear;

    /**
     * 纳音五行月
     */
    @JsonProperty("WuxingNaMonth")
    private String wuxingNaMonth;

    /**
     * 纳音五行日
     */
    @JsonProperty("WuxingNaDay")
    private String wuxingNaDay;

    /**
     * 农历月名称
     */
    @JsonProperty("MoonName")
    private String moonName;

    /**
     * 星宿吉凶（东方星座）
     */
    @JsonProperty("XingEast")
    private String xingEast;

    /**
     * 四方（星座）
     */
    @JsonProperty("XingWest")
    private String xingWest;

    /**
     * 彭祖百忌
     */
    @JsonProperty("PengZu")
    private String pengZu;

    /**
     * 黄历12值神建
     */
    @JsonProperty("JianShen")
    private String jianShen;

    /**
     * 天干地支年
     */
    @JsonProperty("TianGanDiZhiYear")
    private String tianGanDiZhiYear;

    /**
     * 天干地支月
     */
    @JsonProperty("TianGanDiZhiMonth")
    private String tianGanDiZhiMonth;

    /**
     * 天干地支日
     */
    @JsonProperty("TianGanDiZhiDay")
    private String tianGanDiZhiDay;

    /**
     * 农历月名称
     */
    @JsonProperty("LMonthName")
    private String lMonthName;

    /**
     * 生肖
     */
    @JsonProperty("LYear")
    private String lYear;

    /**
     * 农历月
     */
    @JsonProperty("LMonth")
    private String lMonth;

    /**
     * 农历日
     */
    @JsonProperty("LDay")
    private String lDay;

    /**
     * 农历节气的名称
     */
    @JsonProperty("SolarTermName")
    private String solarTermName;
}
