package io.jpress.web.front;

import com.jfinal.aop.Inject;
import com.jfinal.plugin.activerecord.Page;
import io.jboot.utils.StrUtil;
import io.jboot.web.controller.annotation.RequestMapping;
import io.jpress.model.CouponCode;
import io.jpress.model.UserOrder;
import io.jpress.model.UserOrderItem;
import io.jpress.service.CouponCodeService;
import io.jpress.service.UserOrderItemService;
import io.jpress.service.UserOrderService;
import io.jpress.service.UserService;
import io.jpress.web.base.UcenterControllerBase;

import java.util.Date;


@RequestMapping(value = "/ucenter/order", viewPath = "/WEB-INF/views/ucenter/order")
public class OrderController extends UcenterControllerBase {

    @Inject
    private UserOrderService orderService;

    @Inject
    private UserOrderItemService orderItemService;

    @Inject
    private UserService userService;

    @Inject
    private CouponCodeService couponCodeService;

    /**
     * 用户订单列表
     */
    public void index() {
        Page<UserOrder> userOrderPage = orderService.paginateByUserId(getPagePara(), 10, getLoginedUser().getId(), getPara("title"), getPara("ns"));
        setAttr("userOrderPage", userOrderPage);
        render("order_list.html");
    }


    /**
     * 订单详情
     */
    public void detail() {
        UserOrder order = orderService.findById(getIdPara());
        render404If(notLoginedUserModel(order,"buyer_id"));

        setAttr("order", order);
        setAttr("orderItems", orderItemService.findListByOrderId(order.getId()));
        setAttr("orderUser", userService.findById(order.getBuyerId()));

        if (StrUtil.isNotBlank(order.getCouponCode())) {
            CouponCode couponCode = couponCodeService.findByCode(order.getCouponCode());
            setAttr("orderCoupon", couponCode);
            setAttr("orderCouponUser", userService.findById(couponCode.getUserId()));
        }

        setAttr("order", order);
        render("order_detail.html");
    }

    public void comment() {
        UserOrderItem item = orderItemService.findById(getPara());
        render404If(notLoginedUserModel(item,"buyer_id"));

        redirect(item.getCommentPath() + "?id=" + item.getProductId() + "&itemId=" + item.getId());
    }


    public void addMessage() {
        UserOrder userOrder = orderService.findById(getPara());
        render404If(notLoginedUserModel(userOrder,"buyer_id"));

        setAttr("order", userOrder);
        render("order_layer_addmessage.html");
    }


    public void doAddMessage() {
        UserOrder userOrder = orderService.findById(getPara("orderId"));
        render404If(notLoginedUserModel(userOrder,"buyer_id"));

        userOrder.setBuyerMsg(getPara("message"));
        orderService.saveOrUpdate(userOrder);
        renderOkJson();
    }


    public void doFlagDelivery() {
        UserOrder userOrder = orderService.findById(getPara());
        render404If(notLoginedUserModel(userOrder,"buyer_id"));

        userOrder.setDeliveryStatus(UserOrder.DELIVERY_STATUS_FINISHED);
        userOrder.setDeliveryFinishTime(new Date());
        orderService.update(userOrder);
    }


}
