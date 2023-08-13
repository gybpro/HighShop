package com.high.shop.service;

import com.high.shop.domain.Prod;
import com.baomidou.mybatisplus.extension.service.IService;
import com.high.shop.entity.ChangeStock;

/**
* @author high
* @description 针对表【prod(商品)】的数据库操作Service
* @createDate 2023-07-02 16:12:42
*/
public interface ProdService extends IService<Prod> {

    /**
     * 更新商品库存
     * @param changeStock
     */
    void updateProdAndSkuStock(ChangeStock changeStock);

}
