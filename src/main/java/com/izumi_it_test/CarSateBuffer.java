package com.izumi_it_test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.NavigableSet;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

import static java.util.Objects.isNull;

@Component
public class CarSateBuffer {
    int MAX_RANGE_FILTER_BUFFER_LENGTH = 512;
    @Autowired private RangeFilter rangeFilter;

    private Map<Long, List<CarState>> buffer = new ConcurrentHashMap<>();

    public void put(CarState carState) {
        List<CarState> list = buffer.get(carState.getCarId());
        if (isNull(list)) {
            list = new CopyOnWriteArrayList<>();
            buffer.put(carState.getCarId(), list);
        }
        rangeFilter = new RangeFilter();
        if (rangeFilter.checkOnSerial(list, carState)) {
            return;
        }

        if (list.size() >= MAX_RANGE_FILTER_BUFFER_LENGTH
                && !rangeFilter.checkOnInner(list, carState)) {
            return;
        }

        rangeFilter.addNewAndRemoveOld(list, carState);
    }

    /**
     * Реализация фильтрации массива для данного трекера trackerId
     *
     * @param trackerId tracker's id
     * @return последнее значение
     */
    public Short getLastFiltered(long trackerId) {
        List<CarState> innerList = buffer.get(trackerId);
        if (isNull(innerList) || innerList.size() < MAX_RANGE_FILTER_BUFFER_LENGTH) {
            return 0;
        }
        NavigableSet<CarState> set = new TreeSet<>();
        set.addAll(innerList);
        SerialFilter serialFilter = new SerialFilter();
        innerList = set.stream().filter(t -> serialFilter.filtered(t.getCarTemp())).collect(Collectors.toList());
        double[] data = innerList.stream().mapToDouble(t -> t.getCarTemp()).toArray();
        CarDataFilter carDataFilter = new CarDataFilter();
        data = carDataFilter.do_(50, 30, 8, 2, data);
        return (short) data[data.length - 1];
    }
}
