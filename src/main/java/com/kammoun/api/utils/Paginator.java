package com.kammoun.api.utils;

import java.util.Collections;
import java.util.List;

public class Paginator<T> {

    private final List<T> items;
    private final int pageSize;

    public Paginator(List<T> items, int pageSize) {
        this.items = items;
        this.pageSize = pageSize;
    }

    public List<T> getPage(int page) {
        if (items.isEmpty()) return Collections.emptyList();
        int from = (page - 1) * pageSize;
        if (from >= items.size()) return Collections.emptyList();
        int to = Math.min(from + pageSize, items.size());
        return items.subList(from, to);
    }

    public int getTotalPages() {
        if (items.isEmpty()) return 1;
        return (int) Math.ceil((double) items.size() / pageSize);
    }

    public boolean hasNext(int page) {
        return page < getTotalPages();
    }

    public boolean hasPrev(int page) {
        return page > 1;
    }

    public int size() {
        return items.size();
    }

    public boolean isEmpty() {
        return items.isEmpty();
    }
}
