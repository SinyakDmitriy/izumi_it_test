package com.izumi_it_test;

import org.springframework.stereotype.Component;

import java.util.List;
import java.util.NavigableSet;
import java.util.TreeSet;
import java.util.stream.Collectors;

@Component
public class RangeFilter {
    int MAX_RANGE_FILTER_BUFFER_LENGTH = 512; /*должно быть кратным степени двойки*/

    public void addNewAndRemoveOld(List<CarState> list, CarState carState) {
        list.add(carState);
        if (list.size() > MAX_RANGE_FILTER_BUFFER_LENGTH) {
            NavigableSet<CarState> set = new TreeSet<>();
            set.addAll(list);
            CarState oldest = set.first();
            list.remove(oldest);
        }
    }

    public boolean checkOnInner(List<CarState> list, CarState carState) {
        NavigableSet<CarState> set = new TreeSet<>();
        set.addAll(list);
        CarState oldest = set.first();
        return oldest.getCarTimestamp() < carState.getCarTimestamp();
    }

    public boolean checkOnSerial(List<CarState> list, CarState carState) {
        NavigableSet<CarState> set = new TreeSet<>();
        set.addAll(list);
        set.add(carState);
        SerialFilter serialFilter = new SerialFilter();
        int sizeBefore = list.size();
        list = set.stream().filter(t -> serialFilter.filtered(t.getCarTemp())).collect(Collectors.toList());
        int sizeAfter = list.size();
        return sizeBefore == sizeAfter;
    }
}
