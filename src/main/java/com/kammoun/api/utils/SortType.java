package com.kammoun.api.utils;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public interface SortType<T> {

    Comparator<T> getComparator();
    String getDisplayName();

    @SuppressWarnings("unchecked")
    default <E extends Enum<E> & SortType<T>> E next() {
        E[] values = (E[]) getClass().getEnumConstants();
        return values[(((Enum<?>) this).ordinal() + 1) % values.length];
    }

    @SuppressWarnings("unchecked")
    default <E extends Enum<E> & SortType<T>> E prev() {
        E[] values = (E[]) getClass().getEnumConstants();
        int i = ((Enum<?>) this).ordinal();
        return values[(i - 1 + values.length) % values.length];
    }

    default List<T> sort(List<T> list) {
        List<T> sorted = new ArrayList<>(list);
        sorted.sort(getComparator());
        return sorted;
    }
}
