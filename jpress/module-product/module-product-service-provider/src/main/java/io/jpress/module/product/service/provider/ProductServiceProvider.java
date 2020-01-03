package io.jpress.module.product.service.provider;

import com.jfinal.aop.Inject;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import io.jboot.aop.annotation.Bean;
import io.jboot.components.cache.annotation.Cacheable;
import io.jboot.db.model.Column;
import io.jboot.db.model.Columns;
import io.jboot.service.JbootServiceBase;
import io.jboot.utils.StrUtil;
import io.jpress.commons.utils.SqlUtils;
import io.jpress.module.product.model.Product;
import io.jpress.module.product.service.ProductCommentService;
import io.jpress.module.product.service.ProductService;
import io.jpress.service.UserService;

import java.util.ArrayList;
import java.util.List;

@Bean
public class ProductServiceProvider extends JbootServiceBase<Product> implements ProductService {

    private static final String DEFAULT_ORDER_BY = "order_number desc,id desc";

    @Inject
    private UserService userService;

    @Inject
    private ProductCommentService commentService;

    @Override
    public void doUpdateCategorys(long productId, Long[] categoryIds) {
        Db.tx(() -> {
            Db.update("delete from product_category_mapping where product_id = ?", productId);

            if (categoryIds != null && categoryIds.length > 0) {
                List<Record> records = new ArrayList<>();
                for (long categoryId : categoryIds) {
                    Record record = new Record();
                    record.set("product_id", productId);
                    record.set("category_id", categoryId);
                    records.add(record);
                }
                Db.batchSave("product_category_mapping", records, records.size());
            }

            return true;
        });
    }

    @Override
    public void doUpdateCommentCount(long productId) {
        Product product = findById(productId);
        if (product == null) {
            return;
        }

        long count = commentService.findCountByProductId(productId);
        product.setCommentCount(count);
        product.update();
    }

    @Override
    public boolean doChangeStatus(long id, int status) {
        Product product = findById(id);
        product.setStatus(status);
        return update(product);
    }

    @Override
    public Page<Product> _paginateByStatus(int page, int pagesize, String title, Long categoryId, int status) {

        return _paginateByBaseColumns(page
                , pagesize
                , Columns.create("p.status", status).likeAppendPercent("p.title", title)
                , categoryId
                , null);
    }

    @Override
    public Page<Product> _paginateWithoutTrash(int page, int pagesize, String title, Long categoryId) {

        return _paginateByBaseColumns(page
                , pagesize
                , Columns.create().ne("p.status", Product.STATUS_TRASH).likeAppendPercent("p.title", title)
                , categoryId
                , null);
    }


    @Override
    @Cacheable(name = "product")
    public Page<Product> paginateInNormal(int page, int pagesize) {
        return paginateInNormal(page, pagesize, null);
    }


    @Override
    @Cacheable(name = "product")
    public Page<Product> paginateInNormal(int page, int pagesize, String orderBy) {
        orderBy = StrUtil.obtainDefaultIfBlank(orderBy, DEFAULT_ORDER_BY);
        Columns columns = new Columns();
        columns.add("status", Product.STATUS_NORMAL);
        Page<Product> dataPage = DAO.paginateByColumns(page, pagesize, columns, orderBy);
        return joinUserInfo(dataPage);
    }


    @Override
    @Cacheable(name = "product")
    public Page<Product> paginateByCategoryIdInNormal(int page, int pagesize, long categoryId, String orderBy) {

        Columns columns = new Columns();
        columns.add("m.category_id", categoryId);
        columns.add("p.status", Product.STATUS_NORMAL);

        return _paginateByBaseColumns(page, pagesize, columns, categoryId, orderBy);
    }


    public Page<Product> _paginateByBaseColumns(int page, int pagesize, Columns baseColumns, Long categoryId, String orderBy) {

        StringBuilder sqlBuilder = new StringBuilder("from product p ");
        if (categoryId != null) {
            sqlBuilder.append(" left join product_category_mapping m on p.id = m.`product_id` ");
        }


        Columns columns = baseColumns;
        columns.add("m.category_id", categoryId);

        sqlBuilder.append(SqlUtils.toWhereSql(columns));

        // 前台走默认排序，但是后台必须走 id 排序，
        // 否当有默认排序的文章很多的时候,发布的新文章可能在后几页
        sqlBuilder.append(" ORDER BY " + StrUtil.obtainDefaultIfBlank(orderBy, DEFAULT_ORDER_BY));

        Page<Product> dataPage = DAO.paginate(page, pagesize, "select * ", sqlBuilder.toString(), columns.getValueArray());
        return joinUserInfo(dataPage);
    }



    @Override
    @Cacheable(name = "product", key = "#(columns.cacheKey)-#(orderBy)-#(count)", liveSeconds = 60 * 60)
    public List<Product> findListByColumns(Columns columns, String orderBy, Integer count) {
        return joinUserInfo(super.findListByColumns(columns, orderBy, count));
    }

    @Override
    public Product findFirstBySlug(String slug) {
        return joinUserInfo(DAO.findFirstByColumn(Column.create("slug", slug)));
    }

    @Override
    public long findCountByStatus(int status) {
        return DAO.findCountByColumn(Column.create("status",status));
    }

    @Override
    public boolean deleteByIds(Object... ids) {
        for (Object id : ids) {
            deleteById(id);
        }
        return true;
    }

    @Override
    public void doIncProductViewCount(long productId) {

    }

    @Override
    public void doIncProductCommentCount(long productId) {

    }


    private Page<Product> joinUserInfo(Page<Product> page) {
        userService.join(page, "user_id");
        return page;
    }

    private List<Product> joinUserInfo(List<Product> list) {
        userService.join(list, "user_id");
        return list;
    }

    private Product joinUserInfo(Product article) {
        userService.join(article, "user_id");
        return article;
    }
}