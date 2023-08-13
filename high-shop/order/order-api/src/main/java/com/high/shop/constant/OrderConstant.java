package com.high.shop.constant;

/**
 * @author high
 * @version 1.0
 * @since 1.0
 */
public interface OrderConstant {
    // 1:待付款 2:待发货 3:待收货 4:待评价 5:成功 6:失败
    int ORDER_STATUS_PAY = 1;
    int ORDER_STATUS_DELIVER = 2;
    int ORDER_STATUS_RECEIVE = 3;
    int ORDER_STATUS_EVALUATE = 4;
    int ORDER_STATUS_FINISH = 5;
    int ORDER_STATUS_FAIL = 6;
}
