package com.high.shop.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.high.shop.base.BaseCartController;
import com.high.shop.domain.Basket;
import com.high.shop.domain.Sku;
import com.high.shop.feign.CartProductFeign;
import com.high.shop.service.BasketService;
import com.high.shop.vo.CartItemVO;
import com.high.shop.vo.CartMoneyVO;
import com.high.shop.vo.CartVO;
import com.high.shop.vo.ShopVO;
import org.springframework.http.ResponseEntity;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author high
 * @version 1.0
 * @since 1.0
 */
@RestController
@RequestMapping("/p/shopCart")
public class BasketController extends BaseCartController {

    private final BasketService basketService;

    private final CartProductFeign cartProductFeign;

    public BasketController(BasketService basketService, CartProductFeign cartProductFeign) {
        this.basketService = basketService;
        this.cartProductFeign = cartProductFeign;
    }

    /**
     * 查询购物车记录数
     *
     * @return
     */
    @GetMapping("/prodCount")
    public ResponseEntity<Integer> prodCount() {
        /* 查询购物车的总记录数
        return ok(
                basketService.count(
                        new LambdaQueryWrapper<Basket>()
                                .eq(Basket::getUserId, getAuthenticationUserId())
                )
        ); */

        // 查询购物车的总商品数
        return ok(
                basketService.list(
                                new LambdaQueryWrapper<Basket>()
                                        .eq(Basket::getUserId, getAuthenticationUserId())
                        )
                        .stream()
                        .map(Basket::getBasketCount)
                        .collect(Collectors.toList())
                        .stream()
                        .reduce(Integer::sum)
                        .orElse(0)
        );
    }

    @PostMapping("/changeItem")
    public ResponseEntity<Boolean> changeItem(@RequestBody Basket changeBasket) {
        // 根据用户id和库存单位(具体产品)id查询购物车信息
        Basket basket = basketService.getOne(
                new LambdaQueryWrapper<Basket>()
                        .eq(Basket::getUserId, getAuthenticationUserId())
                        .eq(Basket::getSkuId, changeBasket.getSkuId())
        );

        boolean flag;
        if (ObjectUtils.isEmpty(basket)) {
            // 如果购物车没有该具体产品，则新增信息
            if (changeBasket.getBasketCount() > 0) {
                flag = basketService.save(
                        changeBasket.setUserId(getAuthenticationUserId())
                                .setBasketDate(LocalDateTime.now())
                );
            } else {
                throw new RuntimeException("商品数量不能小于0");
            }
        } else {
            // 如果有该具体产品，则更新信息
            Integer basketCount = changeBasket.getBasketCount();

            int finalCount = basket.getBasketCount() + basketCount;

            if (finalCount <= 0) {
                throw new RuntimeException("商品数量不能小于0");
            }

            Integer stocks = cartProductFeign.getSkuStocks(basket.getSkuId());
            if (finalCount > stocks) {
                throw new RuntimeException("商品数量不能大于" + stocks);
            }

            basket.setBasketCount(finalCount);

            flag = basketService.updateById(basket);
        }

        return ok(flag);
    }

    @GetMapping("/info")
    public ResponseEntity<CartVO> info() {
        // 查询当前用户的购物车列表数据
        List<Basket> basketList = basketService.list(
                new LambdaQueryWrapper<Basket>()
                        .eq(Basket::getUserId, getAuthenticationUserId())
        );

        // 根据skuId查询详细信息，补充VO所需信息
        List<Sku> skuList = cartProductFeign.getSkuListByIds(
                basketList.stream().map(Basket::getSkuId).collect(Collectors.toList())
        );

        // 构建ShopVO对象，并按ShopId分组添加到shopList中
        Map<Long, List<Basket>> shopCartMap = basketList.stream().collect(
                Collectors.groupingBy(Basket::getShopId)
        );

        List<ShopVO> shopList = new ArrayList<>();
        List<BigDecimal> shopMoneyList = new ArrayList<>();
        List<BigDecimal> shopFreightList = new ArrayList<>();

        shopCartMap.forEach((shopId, shopBasketList) -> {
            // 单品总价的集合
            List<BigDecimal> itemMoneyList = new ArrayList<>();

            // 遍历购物车列表数据，构建CartItemVO对象，并添加到cartItemList当中
            List<CartItemVO> cartItemList = new ArrayList<>();
            shopBasketList.forEach(basket -> {
                Sku sku = getSku(skuList, basket);

                CartItemVO cartItem = CartItemVO.builder()
                        .basketId(basket.getBasketId())
                        .checked(true)
                        .skuId(basket.getSkuId())
                        .prodId(basket.getProdId())
                        .skuName(sku.getSkuName())
                        .prodName(sku.getProdName())
                        .pic(sku.getPic())
                        .price(sku.getPrice())
                        .basketCount(basket.getBasketCount())
                        .build();

                cartItemList.add(cartItem);

                // 计算单品总价，并添加到itemMoneyList
                itemMoneyList.add(
                        cartItem.getPrice().multiply(new BigDecimal(cartItem.getBasketCount()))
                );
            });

            // 计算店铺总金额，运费金额，并存入shopMoneyList和shopFreightList
            calcMoneyList(shopMoneyList, shopFreightList, itemMoneyList);

            BigDecimal freight = shopFreightList.get(shopFreightList.size() - 1);

            ShopVO shopVO = ShopVO.builder()
                    .shopCartItems(cartItemList)
                    .freight(freight)
                    .build();

            shopList.add(shopVO);
        });

        CartMoneyVO cartMoneyVO = getCartMoneyVO(shopMoneyList, shopFreightList);

        // 构建CartVO对象
        CartVO cartVO = CartVO.builder()
                .shopList(shopList)
                .cartMoneyVO(cartMoneyVO)
                .build();

        return ok(cartVO);
    }

    @PostMapping("/totalPay")
    public ResponseEntity<CartMoneyVO> totalPay(@RequestBody List<Long> basketIds) {
        // 判断是否有选中商品
        if (CollectionUtils.isEmpty(basketIds)) {
            return ok(new CartMoneyVO());
        }

        // 根据选中商品id查询购物车信息
        List<Basket> basketList = basketService.list(
                new LambdaQueryWrapper<Basket>()
                        .eq(Basket::getUserId, getAuthenticationUserId())
                        .in(Basket::getBasketId, basketIds)
        );

        // 根据skuId查询详细信息，补充VO所需信息
        List<Sku> skuList = cartProductFeign.getSkuListByIds(
                basketList.stream().map(Basket::getSkuId).collect(Collectors.toList())
        );

        // 按店铺分组
        Map<Long, List<Basket>> shopCartMap = basketList.stream().collect(
                Collectors.groupingBy(Basket::getShopId)
        );

        List<BigDecimal> shopMoneyList = new ArrayList<>();
        List<BigDecimal> shopFreightList = new ArrayList<>();

        shopCartMap.forEach((shopId, shopBasketList) -> {
            // 单品总价的集合
            List<BigDecimal> itemMoneyList = new ArrayList<>();

            shopBasketList.forEach(basket -> {
                Sku sku = getSku(skuList, basket);

                itemMoneyList.add(
                        sku.getPrice().multiply(new BigDecimal(basket.getBasketCount()))
                );
            });

            calcMoneyList(shopMoneyList, shopFreightList, itemMoneyList);
        });

        // 计算购物车商品总金额、最终金额、优惠金额(0)
        CartMoneyVO cartMoneyVO = getCartMoneyVO(shopMoneyList, shopFreightList);

        return ok(cartMoneyVO);
    }

    @DeleteMapping("/deleteItem")
    public ResponseEntity<String> deleteItem(@RequestBody List<Long> basketIds) {
        // 判断是否有选中商品
        if (CollectionUtils.isEmpty(basketIds)) {
            return ok("请选择要删除的商品");
        }

        // 删除购物车商品
        basketService.removeByIds(basketIds);

        return ok("删除成功");
    }

    private static Sku getSku(List<Sku> skuList, Basket basket) {
        return skuList.stream().filter(
                sku -> sku.getSkuId().equals(basket.getSkuId())
        ).collect(Collectors.toList()).get(0);
    }

    private static void calcMoneyList(List<BigDecimal> shopMoneyList, List<BigDecimal> shopFreightList, List<BigDecimal> itemMoneyList) {
        // 计算店铺总金额，并添加到shopMoneyList
        BigDecimal shopTotalMoney = itemMoneyList.stream().reduce(BigDecimal::add).orElse(BigDecimal.ZERO);
        shopMoneyList.add(shopTotalMoney);

        // 计算应付运费，如果小于99，则需要运费，否则包邮
        BigDecimal freight = shopTotalMoney.compareTo(
                new BigDecimal(99)
        ) < 0 ? new BigDecimal(10) : BigDecimal.ZERO;
        // 将运费添加到shopFreightList
        shopFreightList.add(freight);
    }

    private static CartMoneyVO getCartMoneyVO(List<BigDecimal> shopMoneyList, List<BigDecimal> shopFreightList) {
        // 计算购物车商品总金额、最终金额、优惠金额(0)
        BigDecimal totalMoney = shopMoneyList.stream().reduce(BigDecimal::add).orElse(BigDecimal.ZERO);
        BigDecimal subtractMoney = BigDecimal.ZERO;
        BigDecimal finalMoney = totalMoney.add(
                shopFreightList.stream().reduce(BigDecimal::add).orElse(BigDecimal.ZERO)
        ).subtract(subtractMoney);

        // 构建CartMoneyVO对象
        return CartMoneyVO.builder()
                .totalMoney(totalMoney)
                .subtractMoney(subtractMoney)
                .finalMoney(finalMoney)
                .build();
    }

    // ============== 远程调用 ==============
    @GetMapping("/getBasketListByIds")
    public List<Basket> getBasketListByIds(@RequestParam("ids") List<Long> ids) {
        return basketService.listByIds(ids);
    }

    @PostMapping("/p/shopCart/deleteUserBasket")
    public Boolean deleteUserBasket(@RequestParam("userId") String userId, @RequestParam("skuIds") List<Long> skuIds) {
        return basketService.remove(
                new LambdaQueryWrapper<Basket>()
                        .eq(Basket::getUserId, userId)
                        .in(Basket::getSkuId, skuIds)
        );
    }

}
