package com.gpnu.miaoshaproject.service;

import com.gpnu.miaoshaproject.model.PromoModel;

public interface PromoService {
    /**
     * 根据商品id获取秒杀活动
     * @param itemId 商品id
     * @return
     */
    PromoModel getPromoByItemId(Integer itemId);
}
