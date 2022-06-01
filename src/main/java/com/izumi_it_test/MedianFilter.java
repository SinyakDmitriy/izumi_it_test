package com.izumi_it_test;

import java.util.LinkedList;

public class MedianFilter {
    private LinkedList<Double> linkedList = new LinkedList<>();
    private int filterSize;

    public MedianFilter(int filterSize) {
        this.filterSize = filterSize;
    }

    public Double filtered(double number) {
        if (filterSize >= linkedList.size()) {
            linkedList.add(number);
        } else {
            linkedList.removeFirst();
            linkedList.add(number);
        }

        return getLastValue();
    }

    public Double getLastValue() {
        Object[] item = linkedList.stream().sorted().skip(Math.round((linkedList.size() - 1) / 2d)).limit(1).toArray();

        return item.length != 0 ? (Double) item[0] : 0d;
    }
}
