package com.gpnu.miaoshaproject.controller.UserVO;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class ItemVO {
    private Integer id;

    private String title;

    private BigDecimal price;

    //库存
    private Integer stock;

    private String description;

    //销量
    private Integer sales;

    private String imgUrl;
    /**
     * 记录商品是否在秒杀活动中，以及对应的状态
     * 0：表示没有秒杀活动
     * 1：表示秒杀活动待开始
     * 2：表示秒杀活动进行中
     */
    private Integer promoStatus;

    /**
     * 秒杀活动价格
     */
    private BigDecimal promoPrice;

    /**
     * 秒杀Id
     */
    private Integer promoId;

    /**
     * 秒杀活动开始时间
     */
    private String promoStartDate;
}
