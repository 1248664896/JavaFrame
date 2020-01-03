package io.jpress.service.provider;

import com.jfinal.aop.Inject;
import com.jfinal.plugin.activerecord.Page;
import io.jboot.aop.annotation.Bean;
import io.jboot.db.model.Column;
import io.jboot.db.model.Columns;
import io.jboot.service.JbootServiceBase;
import io.jpress.model.UserCart;
import io.jpress.service.MemberPriceService;
import io.jpress.service.UserCartService;

import java.util.List;

@Bean
public class UserCartServiceProvider extends JbootServiceBase<UserCart> implements UserCartService {

    @Inject
    private MemberPriceService memberPriceService;

    @Override
    public UserCart findById(Object id) {
        UserCart userCart = super.findById(id);
        return joinMemberPrice(userCart);
    }

    @Override
    public Object save(UserCart model) {
        UserCart userCart = findByProductTablendProductId(model.getProductTable(), model.getProductId());
        if (userCart == null) {
            return super.save(model);
        } else {
            userCart.setProductCount(userCart.getProductCount() + 1);
        }
        update(userCart);
        return userCart.getId();
    }

    @Override
    public List<UserCart> findListByUserId(Object userId, int count) {
        List<UserCart> userCarts = DAO.findListByColumns(Columns.create("user_id", userId), "id desc", count);
        return joinMemberPrice(userCarts);
    }

    @Override
    public long findCountByUserId(Object userId) {
        return DAO.findCountByColumn(Column.create("user_id", userId));
    }

    @Override
    public List<UserCart> findSelectedListByUserId(Long userId) {
        List<UserCart> userCarts = DAO.findListByColumns(Columns.create("user_id", userId).eq("selected", true), "id desc");
        return joinMemberPrice(userCarts);
    }

    @Override
    public UserCart findByProductTablendProductId(String productTable, long productId) {
        UserCart userCart = DAO.findFirstByColumns(Columns.create("product_table", productTable).eq("product_id", productId));
        return joinMemberPrice(userCart);
    }

    @Override
    public Page<UserCart> paginateByUser(int page, int pageSize, Long userId) {
        Page<UserCart> userCartPage = paginateByColumns(page, pageSize, Columns.create("user_id", userId), "id desc");
        joinMemberPrice(userCartPage.getList());
        return userCartPage;
    }

    @Override
    public long querySelectedCount(Long userId) {
        return DAO.findCountByColumns(Columns.create("user_id", userId).eq("selected", true));
    }


    private List<UserCart>  joinMemberPrice(List<UserCart> userCarts) {
        if (userCarts != null){
            userCarts.forEach(this::joinMemberPrice);
        }
        return userCarts;
    }

    private UserCart joinMemberPrice(UserCart userCart) {
        if (userCart != null) {
            userCart.put("memberPrice", memberPriceService.queryPrice(userCart.getProductTable(), userCart.getProductId(), userCart.getUserId()));
        }
        return userCart;
    }
}