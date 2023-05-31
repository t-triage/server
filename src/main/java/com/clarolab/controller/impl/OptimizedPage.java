package com.clarolab.controller.impl;

import org.springframework.data.domain.*;

import java.util.Iterator;
import java.util.List;
import java.util.function.Function;

public class OptimizedPage<T> implements Page<T>  {
    Page page;
    int totalPages = 0;
    long totalElements = 0;
    List<T> content;


    public OptimizedPage(Page pageimp) {
        this.page = pageimp;
    }

    public static <T> OptimizedPage<T> getPageable(Pageable pageable, List<T> list) {
        int start = (int) pageable.getOffset();
        if (start > list.size()) {
            // Workaround: There isn't that page, the page will be reset. Likely the UI filter has changed but the Pagable was not reset.
            start = 0;
            pageable = PageRequest.of(0, pageable.getPageSize());
        }
        int end = (start + pageable.getPageSize()) > list.size() ? list.size() : (start + pageable.getPageSize());

        PageImpl page = new PageImpl<>(list.subList(start, end), pageable, list.size());
        return new OptimizedPage(page);
    }

    public static <T> OptimizedPage<T> newContent(OptimizedPage page, List<T> list) {
        OptimizedPage clonePage = new OptimizedPage(page);
        clonePage.page = page.page;
        clonePage.totalPages = page.getTotalPages();
        clonePage.totalElements = page.getTotalElements();

        clonePage.content = list;

        return clonePage;
    }

    @Override
    public List<T> getContent() {
        if (content != null) {
            return content;
        }
        return page.getContent();
    }

    @Override
    public int getTotalPages() {
        if (totalPages > 0) {
            return totalPages;
        }
        return page.getTotalPages();
    }

    @Override
    public long getTotalElements() {
        if (totalElements > 0) {
            return totalElements;
        }
        return page.getTotalElements();
    }

    @Override
    public int getNumber() {
        return page.getNumber();
    }

    @Override
    public int getSize() {
        return page.getSize();
    }

    @Override
    public int getNumberOfElements() {
        return page.getNumberOfElements();
    }

    @Override
    public boolean hasContent() {
        return page.hasContent();
    }

    @Override
    public Sort getSort() {
        return page.getSort();
    }

    @Override
    public boolean isFirst() {
        return page.isFirst();
    }

    @Override
    public boolean isLast() {
        return page.isLast();
    }

    @Override
    public boolean hasNext() {
        return page.hasNext();
    }

    @Override
    public boolean hasPrevious() {
        return page.hasPrevious();
    }

    @Override
    public Pageable nextPageable() {
        return page.nextPageable();
    }

    @Override
    public Pageable previousPageable() {
        return page.previousPageable();
    }

    @Override
    public <U> org.springframework.data.domain.Page<U> map(Function<? super T, ? extends U> function) {
        return page.map(function);
    }

    @Override
    public Iterator<T> iterator() {
        return page.iterator();
    }
}
