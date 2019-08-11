package com.gpnu.miaoshaproject.service.impl;

import com.gpnu.miaoshaproject.dao.PromoDOMapper;
import com.gpnu.miaoshaproject.dataobject.PromoDO;
import com.gpnu.miaoshaproject.model.PromoModel;
import com.gpnu.miaoshaproject.service.PromoService;
import org.joda.time.DateTime;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PromoServiceImpl implements PromoService {

    @Autowired
    private PromoDOMapper promoDOMapper;

    @Override
    public PromoModel getPromoByItemId(Integer itemId) {
        // 获取对应的商品对应的秒杀多动信息
        PromoDO promoDO = promoDOMapper.selectByItemId(itemId);

        // dataObject -> model
        PromoModel promoModel = convertFromDataObejct(promoDO);
        if (promoModel == null) {
            return null;
        }
        // 判断当前时间是否秒杀活动即将开始或正在进行
        DateTime now = new DateTime();
        System.out.println(promoModel);
        if(promoModel.getStartDate().isAfterNow()){
            //未开始
            promoModel.setStatus(1);
        }else if(promoModel.getStartDate().isBeforeNow()){
            //已结束
            promoModel.setStatus(3);
        }else{
            //进行中
            promoModel.setStatus(2);
        }
        return promoModel;
    }

    private PromoModel convertFromDataObejct(PromoDO promoDO){
        if(promoDO == null)
            return null;
        PromoModel promoModel = new PromoModel();
        BeanUtils.copyProperties(promoDO,promoModel);
        promoModel.setStartDate(new DateTime(promoDO.getStartDate()));
        promoModel.setEndDate(new DateTime(promoDO.getEndDate()));
        return promoModel;
    }
}
