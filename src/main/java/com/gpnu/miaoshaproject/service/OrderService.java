package com.gpnu.miaoshaproject.service;

import com.gpnu.miaoshaproject.error.BusinessException;
import com.gpnu.miaoshaproject.model.OrderModel;

public interface OrderService {
    // 创建订单
    OrderModel createOrder(Integer userId, Integer itemId, Integer promoId, Integer amount) throws BusinessException;
}
