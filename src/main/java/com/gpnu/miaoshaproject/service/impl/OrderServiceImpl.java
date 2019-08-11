package com.gpnu.miaoshaproject.service.impl;

import java.math.BigDecimal;

import com.gpnu.miaoshaproject.dao.OrderDOMapper;
import com.gpnu.miaoshaproject.dao.SequenceDOMapper;
import com.gpnu.miaoshaproject.dataobject.OrderDO;
import com.gpnu.miaoshaproject.dataobject.SequenceDO;
import com.gpnu.miaoshaproject.error.BusinessException;
import com.gpnu.miaoshaproject.error.EmBusinessError;
import com.gpnu.miaoshaproject.model.ItemModel;
import com.gpnu.miaoshaproject.model.OrderModel;
import com.gpnu.miaoshaproject.model.UserModel;
import com.gpnu.miaoshaproject.service.ItemService;
import com.gpnu.miaoshaproject.service.OrderService;
import com.gpnu.miaoshaproject.service.UserService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    private ItemService itemService;

    @Autowired
    private UserService userService;

    @Autowired
    private OrderDOMapper orderDOMapper;

    @Autowired
    private SequenceDOMapper sequenceDOMapper;

    @Override
    @Transactional
    public OrderModel createOrder(Integer userId, Integer itemId, Integer promoId, Integer amount) throws BusinessException {
        // 1. 校验下单状态，下单的商品是否存在，用户是否合法，购买数量是否正确
        //商品是否存在
        ItemModel itemModel = this.itemService.getItemById(itemId);
        if (itemModel == null) {
            throw new BusinessException(EmBusinessError.PARAMETER_VALIDATION_ERROR, "商品信息不存在");
        }
        //用户是否存在
        UserModel userModel = this.userService.getUserById(userId);
        if (itemModel == null) {
            throw new BusinessException(EmBusinessError.PARAMETER_VALIDATION_ERROR, "用户信息不存在");
        }
        //购买的量是否符合规则
        if (amount <= 0 || amount > 99) {
            throw new BusinessException(EmBusinessError.PARAMETER_VALIDATION_ERROR, "购买数量不正确");
        }
        // 校验活动信息
        if (promoId != null) {
            // 1. 校验对应活动是否存在这个适用商品
            if (promoId.intValue() != itemModel.getPromoModel().getId()) {
                throw new BusinessException(EmBusinessError.PARAMETER_VALIDATION_ERROR, "活动信息不正确");
            } else if (itemModel.getPromoModel().getStatus().intValue() != 2) {
                throw new BusinessException(EmBusinessError.PARAMETER_VALIDATION_ERROR, "活动还未开始");
            }
        }
        // 2. 落单减库存
        boolean result = this.itemService.decreaseStock(itemId, amount);
        if (!result) {
            throw new BusinessException(EmBusinessError.STOCK_NOT_ENOUGH);
        }

        // 3. 订单入库
        OrderModel orderModel = new OrderModel();
        orderModel.setUserId(userId);
        orderModel.setItemId(itemId);
        orderModel.setAmount(amount);
        if(promoId != null){
            orderModel.setItemPrice(itemModel.getPromoModel().getPromoItemPrice());
        }else{
            orderModel.setItemPrice(itemModel.getPrice());
        }
        orderModel.setPromoId(promoId);
        orderModel.setOrderPrice(orderModel.getItemPrice().multiply(new BigDecimal(amount)));
        // 生成交易流水号，订单号
        orderModel.setId(this.generateOrderNo());
        OrderDO orderDO = this.convertFromOrderModal(orderModel);
        this.orderDOMapper.insertSelective(orderDO);
        // 加上商品的销量
        this.itemService.increaseSales(itemId, amount);
        // 4. 返回前端
        return orderModel;
    }

    //生成流水号。
    //因为又可能会交易失败，如果事务回滚，该流水号会再次被使用
    //最好要求此流水号作废。
    //所以开启新的事务
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public String generateOrderNo() {
        StringBuilder sb = new StringBuilder();
        // 前8位为时间信息，年月日
        LocalDateTime now = LocalDateTime.now();
        String nowDate = now.format(DateTimeFormatter.ISO_DATE).replace("-", "");
        sb.append(nowDate);
        // 中间6位为自增序列
        // 获取当前sequence
        int sequence = 0;
        SequenceDO sequenceDO = sequenceDOMapper.getSequenceByName("order_info");
        sequence = sequenceDO.getCurrentValue();
        sequenceDO.setCurrentValue(sequenceDO.getCurrentValue() + sequenceDO.getStep());
        this.sequenceDOMapper.updateByPrimaryKeySelective(sequenceDO);
        String sequenceStr = String.valueOf(sequence);
        for (int i = 0; i < 6 - sb.length(); i++) {
            sb.append(0);
        }
        sb.append(sequenceStr);
        // 最后2位为分库分表位，暂时写死
        sb.append("00");
        return sb.toString();
    }

    private OrderDO convertFromOrderModal(OrderModel orderModel) {
        if (orderModel == null)
            return null;
        OrderDO orderDO = new OrderDO();
        BeanUtils.copyProperties(orderModel, orderDO);
        // 处理 decimal类
        orderDO.setItemPrice(orderModel.getItemPrice().doubleValue());
        orderDO.setOrderPrice(orderModel.getOrderPrice().doubleValue());
        return orderDO;
    }
}
