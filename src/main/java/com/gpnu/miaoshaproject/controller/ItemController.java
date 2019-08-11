package com.gpnu.miaoshaproject.controller;
import	java.util.stream.Collectors;

import com.gpnu.miaoshaproject.controller.UserVO.ItemVO;
import com.gpnu.miaoshaproject.error.BusinessException;
import com.gpnu.miaoshaproject.model.ItemModel;
import com.gpnu.miaoshaproject.response.CommonReturnType;
import com.gpnu.miaoshaproject.service.ItemService;
import org.joda.time.format.DateTimeFormat;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@Controller("item")
@RequestMapping("/item")
@CrossOrigin(allowCredentials = "true", allowedHeaders = "*")
public class ItemController extends BaseController {

    @Autowired
    private ItemService itemService;

    @RequestMapping(value = "/list", method = RequestMethod.GET)
    @ResponseBody
    public CommonReturnType listItem(){
        List<ItemModel> itemModels = itemService.listItems();
        List<ItemVO> itemVOs = itemModels.stream().map(itemModel -> {
            ItemVO itemVO = convertFromModel(itemModel);
            return itemVO;
        }).collect(Collectors.toList());
        return CommonReturnType.create(itemVOs);
    }


    @RequestMapping(value = "/create", method = {RequestMethod.POST}, consumes = {CONTENT_TYPE_FORMED})
    @ResponseBody
    public CommonReturnType createItem(ItemModel item) throws BusinessException {

        ItemModel itemModel = itemService.createItem(item);
        ItemVO itemVO = convertFromModel(itemModel);
        return CommonReturnType.create(itemVO);
    }

    @RequestMapping(value = "/get", method = {RequestMethod.GET})
    @ResponseBody
    public CommonReturnType getItem(@RequestParam(name="id") Integer id){
        System.out.println("11111111111111111111111111111");
        ItemModel itemModel = itemService.getItemById(id);
        System.out.println("2222222222222222222222222222");
        ItemVO itemVO = convertFromModel(itemModel);
        return CommonReturnType.create(itemVO);
    }

    private ItemVO convertFromModel(ItemModel itemModel){
        if(itemModel == null){
            return null;
        }
        ItemVO itemVO = new ItemVO();
        BeanUtils.copyProperties(itemModel, itemVO);
        if (itemModel.getPromoModel() != null) {
            // 有正在进行或即将进行的秒杀活动
            itemVO.setPromoStatus(itemModel.getPromoModel().getStatus());
            itemVO.setPromoId(itemModel.getPromoModel().getId());
            itemVO.setPromoStartDate(itemModel.getPromoModel().getStartDate().toString(DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss")));
            itemVO.setPromoPrice(itemModel.getPromoModel().getPromoItemPrice());
        } else {
            itemVO.setPromoStatus(0);
        }
        return itemVO;
    }
}