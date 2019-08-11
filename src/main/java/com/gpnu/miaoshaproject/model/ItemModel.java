package com.gpnu.miaoshaproject.model;

import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@Data
public class ItemModel {

    private Integer id;

    @NotBlank(message = "商品名称不能为空")
    private String title;

    @NotNull(message = "商品价格不能为空")
    @Min(value = 0, message = "商品价格必须大于0")
    private BigDecimal price;

    @NotNull(message = "库存不能为空")
    @Min(value = 0, message = "库存必须大于0")
    private Integer stock;

    @NotNull(message = "商品表述信息不能为空")
    private String description;

    //销量
    private Integer sales;

    @NotNull(message = "商品图片不能为空")
    private String imgUrl;

    //聚合模型
    //如果promoModel不为空，则表示其拥有还未结束的秒杀活动
    private PromoModel promoModel;

}
