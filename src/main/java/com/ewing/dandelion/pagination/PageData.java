package com.ewing.dandelion.pagination;

import java.util.List;

/**
 * 分页数据。
 *
 * @author Ewing
 * @since 2017-04-22
 **/
public class PageData<T> {
    private long total;

    private List<T> content;

    public PageData() {
    }

    public PageData(List<T> content) {
        if (content == null) return;
        this.content = content;
        this.total = content.size();
    }

    public long getTotal() {
        return total;
    }

    public void setTotal(long total) {
        this.total = total;
    }

    public List<T> getContent() {
        return content;
    }

    public void setContent(List<T> content) {
        this.content = content;
    }
}
