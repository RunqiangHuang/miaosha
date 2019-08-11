package com.gpnu.miaoshaproject.service;

import com.gpnu.miaoshaproject.error.BusinessException;
import com.gpnu.miaoshaproject.model.ItemModel;

import java.util.List;

public interface ItemService {
    //创建商品
    ItemModel createItem(ItemModel itemModel) throws BusinessException;

    //商品列表浏览
    List<ItemModel> listItems();

    //商品详情 根据Id查找。
    ItemModel getItemById(Integer id);

    // 减少库存
    boolean decreaseStock(Integer itemId, Integer amount);

    // 增加销量
    void increaseSales(Integer itemId, Integer amount) throws BusinessException;
}
