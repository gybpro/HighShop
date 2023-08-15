package com.high.shop.controller;

import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayResponse;
import com.alipay.api.domain.TradeFundBill;
import com.alipay.api.internal.util.AlipaySignature;
import com.alipay.api.response.AlipayTradePrecreateResponse;
import com.alipay.api.response.AlipayTradeQueryResponse;
import com.alipay.demo.trade.model.ExtendParams;
import com.alipay.demo.trade.model.GoodsDetail;
import com.alipay.demo.trade.model.builder.AlipayTradePrecreateRequestBuilder;
import com.alipay.demo.trade.model.builder.AlipayTradeQueryRequestBuilder;
import com.alipay.demo.trade.model.result.AlipayF2FPrecreateResult;
import com.alipay.demo.trade.model.result.AlipayF2FQueryResult;
import com.alipay.demo.trade.service.AlipayTradeService;
import com.alipay.demo.trade.utils.Utils;
import com.high.shop.base.BasePayController;
import com.high.shop.domain.Order;
import com.high.shop.domain.OrderItem;
import com.high.shop.feign.PayOrderFeign;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

/**
 * @author high
 * @version 1.0
 * @since 1.0
 */
@RestController
@RequestMapping("/p/order")
@Slf4j
public class PayController extends BasePayController {

    private final AlipayTradeService tradeService;

    private final PayOrderFeign payOrderFeign;

    public PayController(AlipayTradeService tradeService, PayOrderFeign payOrderFeign) {
        this.tradeService = tradeService;
        this.payOrderFeign = payOrderFeign;
    }

    /**
     * 生成二维码
     *
     * @return
     */
    @PostMapping("/pay")
    public ResponseEntity<String> pay(@RequestBody Map<String, Object> params) {
        // (必填) 商户网站订单系统中唯一订单号，64个字符以内，只能包含字母、数字、下划线，
        // 需保证商户系统端不能重复，建议通过数据库sequence生成，
        // String outTradeNo = "tradeprecreate" + System.currentTimeMillis()
        //         + (long) (Math.random() * 10000000L);
        String outTradeNo = (String) params.get("orderNumber");

        if (StringUtils.isEmpty(outTradeNo)) {
            throw new RuntimeException("订单号异常");
        }

        // 根据订单号查询订单信息
        Order order = payOrderFeign.getOrderByOrderNumber(outTradeNo);

        if (ObjectUtils.isEmpty(order)) {
            throw new RuntimeException("订单查询异常");
        }

        // (必填) 订单标题，粗略描述用户的支付目的。如“xxx品牌xxx门店当面付扫码消费”
        // String subject = "xxx品牌xxx门店当面付扫码消费";
        String subject = "商城购买订单";

        // (必填) 订单总金额，单位为元，不能超过1亿元
        // 如果同时传入了【打折金额】,【不可打折金额】,【订单总金额】三者,则必须满足如下条件:【订单总金额】=【打折金额】+【不可打折金额】
        // String totalAmount = "0.01";
        // 使用toPlainString，防止出现科学计数法
        String totalAmount = order.getActualTotal().toPlainString();

        // (可选) 订单不可打折金额，可以配合商家平台配置折扣活动，如果酒水不参与打折，则将对应金额填写至此字段
        // 如果该值未传入,但传入了【订单总金额】,【打折金额】,则该值默认为【订单总金额】-【打折金额】
        String undiscountableAmount = "0";

        // 卖家支付宝账号ID，用于支持一个签约账号下支持打款到不同的收款账号，(打款到sellerId对应的支付宝账号)
        // 如果该字段为空，则默认为与支付宝签约的商户的PID，也就是appid对应的PID
        String sellerId = "";

        // 订单描述，可以对交易或商品进行一个详细地描述，比如填写"购买商品2件共15.00元"
        // String body = "购买商品3件共20.00元";
        String body = "购买商品" + order.getProductNums() + "件共" + order.getActualTotal().toPlainString() + "元";

        // 商户操作员编号，添加此参数可以为商户操作员做销售统计
        String operatorId = "test_operator_id";

        // (必填) 商户门店编号，通过门店号和商家后台可以配置精准到门店的折扣信息，详询支付宝技术支持
        String storeId = "test_store_id";

        // 业务扩展参数，目前可添加由支付宝分配的系统商编号(通过setSysServiceProviderId方法)，详情请咨询支付宝技术支持
        // ExtendParams extendParams = new ExtendParams();
        // extendParams.setSysServiceProviderId("2088100200300400500");

        // 支付超时，定义为120分钟
        String timeoutExpress = "120m";

        // // 商品明细列表，需填写购买商品详细信息，
        // List<GoodsDetail> goodsDetailList = new ArrayList<GoodsDetail>();
        // // 创建一个商品信息，参数含义分别为商品id（使用国标）、名称、单价（单位为分）、数量，如果需要添加商品类别，详见GoodsDetail
        // GoodsDetail goods1 = GoodsDetail.newInstance("goods_id001", "xxx小面包", 1000, 1);
        // // 创建好一个商品后添加至商品明细列表
        // goodsDetailList.add(goods1);

        // 构建商品明细列表，需填写购买商品详细信息
        List<OrderItem> orderItemList = order.getOrderItemList();

        List<GoodsDetail> goodsDetailList = orderItemList.stream().map(orderItem -> GoodsDetail.newInstance(
                orderItem.getOrderItemId().toString(),
                orderItem.getProdName() + "," + orderItem.getSkuName(),
                orderItem.getPrice().multiply(new BigDecimal("100")).longValue(),
                orderItem.getProdCount()
        )).collect(Collectors.toList());

        // 继续创建并添加第一条商品信息，用户购买的产品为“黑人牙刷”，单价为5.00元，购买了两件
        GoodsDetail goods2 = GoodsDetail.newInstance("goods_id002", "xxx牙刷", 500, 2);
        goodsDetailList.add(goods2);

        // 创建扫码支付请求builder，设置请求参数
        AlipayTradePrecreateRequestBuilder builder = new AlipayTradePrecreateRequestBuilder()
                .setSubject(subject).setTotalAmount(totalAmount).setOutTradeNo(outTradeNo)
                .setUndiscountableAmount(undiscountableAmount).setSellerId(sellerId).setBody(body)
                .setOperatorId(operatorId).setStoreId(storeId)
                // .setExtendParams(extendParams)
                .setTimeoutExpress(timeoutExpress)
                // .setNotifyUrl("http://www.test-notify-url.com")//支付宝服务器主动通知商户服务器里指定的页面http路径,根据需要设置
                .setNotifyUrl("http://5j53s3.natappfree.cc/p/order/alipayNotify")
                .setGoodsDetailList(goodsDetailList);

        AlipayF2FPrecreateResult result = tradeService.tradePrecreate(builder);
        switch (result.getTradeStatus()) {
            case SUCCESS:
                log.info("支付宝预下单成功: )");

                AlipayTradePrecreateResponse response = result.getResponse();
                dumpResponse(response);

                // 需要修改为运行机器上的路径
                String filePath = String.format("/Users/sudo/Desktop/qr-%s.png",
                        response.getOutTradeNo());
                log.info("filePath:" + filePath);
                // ZxingUtils.getQRCodeImge(response.getQrCode(), 256, filePath);
                return ok(response.getQrCode());

            case FAILED:
                log.error("支付宝预下单失败!!!");
                break;

            case UNKNOWN:
                log.error("系统异常，预下单状态未知!!!");
                break;

            default:
                log.error("不支持的交易状态，交易返回异常!!!");
                break;
        }
        return ok("支付失败");
    }

    /* {
      gmt_create=2023-08-1320: 14: 48,
      charset=utf-8,
      seller_email=aimmlr7395@sandbox.com,
      subject=商城购买订单,
      sign=HCOy59V1fkYMCscqUvQL5B4OOMOCpkMu/Tz/c7rEvxDFl1QTib86vIoH9Fk1P8NqrYFTNxlw0yxwsgo9mlFsBiAa1795CLCXyjpLv0Bs4wJb1jcg+F9PSLlSjP+lzTWSJ1+7W5Wqkuj+eEv/wMqsF4u73G3dkI4wensdFqoW+U4a7h8zRE8vGmUP0wPzr0fYCgQYq02rW2SN17wSvXnAB5FVWISl/BPO075QZs2Adb9wvVDgRLevFO6KBV8TB7pA2Gy/lDS4uhvFE1OPVqaRYIX85EjE51bE0xGMIFHfwpI0DgeS25KxZe9qPUYFdIjanO7jYkPToib31U45HFAGlQ==,
      buyer_id=2088722008221215,
      body=购买商品1件共20.00元,
      invoice_amount=20.00,
      notify_id=2023081301222201453021210500665651,
      fund_bill_list=[
        {
          "amount": "20.00",
          "fundChannel": "ALIPAYACCOUNT"
        }
      ],
      notify_type=trade_status_sync,
      trade_status=TRADE_SUCCESS,
      receipt_amount=20.00,
      buyer_pay_amount=20.00,
      app_id=9021000125624440,
      sign_type=RSA2,
      seller_id=2088721008270634,
      gmt_payment=2023-08-1320: 14: 52,
      notify_time=2023-08-1320: 14: 54,
      version=1.0,
      out_trade_no=1690698381573226496,
      total_amount=20.00,
      trade_no=2023081322001421210500641656,
      auth_app_id=9021000125624440,
      buyer_logon_id=ltvotw2767@sandbox.com,
      point_amount=0.00
    } */
    @PostMapping("/alipayNotify")
    public void alipayNotify(@RequestParam Map<String, String> params) throws AlipayApiException {
        if (ObjectUtils.isEmpty(params)) {
            return;
        }

        // 获取公钥
        ResourceBundle bundle = ResourceBundle.getBundle("alipay/zfbinfo");
        String alipayPublicKey = bundle.getString("alipay_public_key");

        System.out.println("支付宝支付异步回调通知：" + params);

        // 校验签名
        boolean flag = AlipaySignature.rsaCheckV1(params, alipayPublicKey, "UTF-8", "RSA2");
        System.out.println("验签结果：" + flag);
        if (flag) {
            // 验签通过，修改订单状态
            flag = payOrderFeign.changeOrderStatus(params.get("out_trade_no"));
            if (!flag) {
                throw new RuntimeException("修改订单状态失败");
            }
        } else {
            throw new RuntimeException("签名验证失败");
        }
    }

    // pay-service/p/order/query?orderSn=
    @GetMapping("/query")
    public ResponseEntity<Boolean> queryTradeStatus(@RequestParam("orderSn") String orderSn) {
        if (org.springframework.util.StringUtils.isEmpty(orderSn)) {
            throw new RuntimeException("订单号异常");
        }

        // 根据订单号查询支付结果
        // 创建查询请求builder，设置请求参数
        AlipayTradeQueryRequestBuilder builder = new AlipayTradeQueryRequestBuilder()
                .setOutTradeNo(orderSn);

        AlipayF2FQueryResult result = tradeService.queryTradeResult(builder);
        switch (result.getTradeStatus()) {
            case SUCCESS:
                log.info("查询返回该订单支付成功: )");

                AlipayTradeQueryResponse response = result.getResponse();
                dumpResponse(response);

                log.info(response.getTradeStatus());
                if (Utils.isListNotEmpty(response.getFundBillList())) {
                    for (TradeFundBill bill : response.getFundBillList()) {
                        log.info(bill.getFundChannel() + ":" + bill.getAmount());
                    }
                }
                // 发送远程调用，修改订单状态
                return ok(payOrderFeign.changeOrderStatus(orderSn));

            case FAILED:
                log.error("查询返回该订单支付失败或被关闭!!!");
                break;

            case UNKNOWN:
                log.error("系统异常，订单支付状态未知!!!");
                break;

            default:
                log.error("不支持的交易状态，交易返回异常!!!");
                break;
        }

        return ok(false);
    }

    // 简单打印应答
    private void dumpResponse(AlipayResponse response) {
        if (response != null) {
            log.info(String.format("code:%s, msg:%s", response.getCode(), response.getMsg()));
            if (StringUtils.isNotEmpty(response.getSubCode())) {
                log.info(String.format("subCode:%s, subMsg:%s", response.getSubCode(),
                        response.getSubMsg()));
            }
            log.info("body:" + response.getBody());
        }
    }

}
