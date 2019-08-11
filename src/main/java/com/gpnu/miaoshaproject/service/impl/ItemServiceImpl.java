package com.gpnu.miaoshaproject.service.impl;
import	java.util.stream.Collectors;

import com.gpnu.miaoshaproject.dao.ItemDOMapper;
import com.gpnu.miaoshaproject.dao.ItemStockDOMapper;
import com.gpnu.miaoshaproject.dataobject.ItemDO;
import com.gpnu.miaoshaproject.dataobject.ItemStockDO;
import com.gpnu.miaoshaproject.error.BusinessException;
import com.gpnu.miaoshaproject.error.EmBusinessError;
import com.gpnu.miaoshaproject.model.ItemModel;
import com.gpnu.miaoshaproject.model.PromoModel;
import com.gpnu.miaoshaproject.service.ItemService;
import com.gpnu.miaoshaproject.service.PromoService;
import com.gpnu.miaoshaproject.validator.ValidationResult;
import com.gpnu.miaoshaproject.validator.ValidatorImpl;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
public class ItemServiceImpl implements ItemService {

    @Autowired
    private ValidatorImpl validator;

    @Autowired
    private ItemDOMapper itemDOMapper;

    @Autowired
    private ItemStockDOMapper itemStockDOMapper;

    @Autowired
    private PromoService promoService;

    @Transactional
    @Override
    public ItemModel createItem(ItemModel itemModel) throws BusinessException {
        //检验入参
        ValidationResult result = validator.validate(itemModel);
        if (result.isHasErrors()) {
            throw new BusinessException(EmBusinessError.PARAMETER_VALIDATION_ERROR, result.getErrMsg());
        }
        //转化itemModel -> dataObject
        ItemDO itemDO = this.convertItemDOFromItemModel(itemModel);
        //写入数据库
        itemDOMapper.insertSelective(itemDO);
        itemModel.setId(itemDO.getId());
        ItemStockDO itemStockDO = this.covertItemStockDOFromItemModel(itemModel);
        itemStockDOMapper.insertSelective(itemStockDO);
        //返回创建完成的对象
        return this.getItemById(itemModel.getId());
    }

    @Override
    public List<ItemModel> listItems() {
        List<ItemDO> itemDOS = itemDOMapper.listItem();
        List<ItemModel> itemModels = new ArrayList<>();
        for(int i = 0;i < itemDOS.size(); i++){
            ItemStockDO itemStockDO = itemStockDOMapper.selectByItemId(itemDOS.get(i).getId());
            itemModels.add(convertModelFromDataObject(itemDOS.get(i),itemStockDO));
        }
        return itemModels;


        // java 8的Stream方法
//        List<ItemModel> itemModelList = itemDOS.stream().map(itemDO -> {
//            ItemStockDO itemStockDO = itemStockDOMapper.selectByItemId(itemDO.getId());
//            ItemModel itemModel = convertModelFromDataObject(itemDO,itemStockDO);
//            return itemModel;
//        }).collect(Collectors.toList());
//        return itemModelList;
    }

    @Override
    public ItemModel getItemById(Integer id) {
        ItemDO itemDO = itemDOMapper.selectByPrimaryKey(id);
        if (itemDO == null)
            return null;
        //获取库存
        ItemStockDO itemStockDO = itemStockDOMapper.selectByItemId(itemDO.getId());
        ItemModel itemModel = convertModelFromDataObject(itemDO, itemStockDO);
        //获取商品信息，观察是否在秒杀
        PromoModel promoModel = promoService.getPromoByItemId(itemModel.getId());
        if(promoModel != null && promoModel.getStatus().intValue() != 3){
            //秒杀。
            itemModel.setPromoModel(promoModel);
        }
        return itemModel;
    }

    @Override
    public boolean decreaseStock(Integer itemId, Integer amount) {
        //根据受影响行数。看是否减少库存成功
        int affectedRow = this.itemStockDOMapper.decreaseStock(itemId, amount);
        if (affectedRow > 0) {
            // 更新库存成功
            return true;
        } else {
            // 更新库存失败
            return false;
        }
    }

    @Override
    public void increaseSales(Integer itemId, Integer amount) throws BusinessException {
        this.itemDOMapper.increaseSales(itemId, amount);
    }

    private ItemDO convertItemDOFromItemModel(ItemModel itemModel) {
        if (itemModel == null)
            return null;
        ItemDO itemDO = new ItemDO();
        BeanUtils.copyProperties(itemModel, itemDO);
        //因为一个是double类型。 一个是bigDecimal类型。
        //所以这里需要手动转换
        itemDO.setPrice(itemModel.getPrice().doubleValue());
        return itemDO;
    }

    private ItemStockDO covertItemStockDOFromItemModel(ItemModel itemModel) {
        if (itemModel == null)
            return null;
        ItemStockDO itemStockDO = new ItemStockDO();
        itemStockDO.setItemId(itemModel.getId());
        itemStockDO.setStock(itemModel.getStock());
        return itemStockDO;
    }

    private ItemModel convertModelFromDataObject(ItemDO itemDO, ItemStockDO itemStockDO) {
        ItemModel itemModel = new ItemModel();
        BeanUtils.copyProperties(itemDO, itemModel);
        itemModel.setPrice(new BigDecimal(itemDO.getPrice()));
        itemModel.setStock(itemStockDO.getStock());
        return itemModel;
    }
}
