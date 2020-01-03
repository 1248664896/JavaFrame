package io.jpress.model.base;

import com.jfinal.plugin.activerecord.IBean;
import io.jpress.base.BaseOptionsModel;

/**
 * Generated by JPress, do not modify this file.
 */
@SuppressWarnings("serial")
public abstract class BaseUserFavorite<M extends BaseUserFavorite<M>> extends BaseOptionsModel<M> implements IBean {

    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
	public void setId(java.lang.Long id) {
		set("id", id);
	}

    /**
     * 主键
     */
	public java.lang.Long getId() {
		return getLong("id");
	}

    /**
     * 收藏用户
     */
	public void setUserId(java.lang.Long userId) {
		set("user_id", userId);
	}

    /**
     * 收藏用户
     */
	public java.lang.Long getUserId() {
		return getLong("user_id");
	}

    /**
     * 收藏数据的类型
     */
	public void setType(java.lang.String type) {
		set("type", type);
	}

    /**
     * 收藏数据的类型
     */
	public java.lang.String getType() {
		return getStr("type");
	}

	public void setTypeText(java.lang.String typeText) {
		set("type_text", typeText);
	}

	public java.lang.String getTypeText() {
		return getStr("type_text");
	}

    /**
     * 标题
     */
	public void setTitle(java.lang.String title) {
		set("title", title);
	}

    /**
     * 标题
     */
	public java.lang.String getTitle() {
		return getStr("title");
	}

    /**
     * 摘要
     */
	public void setSummary(java.lang.String summary) {
		set("summary", summary);
	}

    /**
     * 摘要
     */
	public java.lang.String getSummary() {
		return getStr("summary");
	}

    /**
     * 缩略图
     */
	public void setThumbnail(java.lang.String thumbnail) {
		set("thumbnail", thumbnail);
	}

    /**
     * 缩略图
     */
	public java.lang.String getThumbnail() {
		return getStr("thumbnail");
	}

    /**
     * 详情页
     */
	public void setDetailPage(java.lang.String detailPage) {
		set("detail_page", detailPage);
	}

    /**
     * 详情页
     */
	public java.lang.String getDetailPage() {
		return getStr("detail_page");
	}

    /**
     * 相关表
     */
	public void setRelativeTable(java.lang.String relativeTable) {
		set("relative_table", relativeTable);
	}

    /**
     * 相关表
     */
	public java.lang.String getRelativeTable() {
		return getStr("relative_table");
	}

    /**
     * 相关的id
     */
	public void setRelativeId(java.lang.String relativeId) {
		set("relative_id", relativeId);
	}

    /**
     * 相关的id
     */
	public java.lang.String getRelativeId() {
		return getStr("relative_id");
	}

	public void setOptions(java.lang.String options) {
		set("options", options);
	}

	public java.lang.String getOptions() {
		return getStr("options");
	}

    /**
     * 创建日期
     */
	public void setCreated(java.util.Date created) {
		set("created", created);
	}

    /**
     * 创建日期
     */
	public java.util.Date getCreated() {
		return get("created");
	}

}
