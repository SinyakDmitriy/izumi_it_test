package com.izumi_it_test;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.NavigableSet;
import java.util.TreeSet;
import java.util.concurrent.CopyOnWriteArrayList;

public class RangeFilterTest {

    int MAX_RANGE_FILTER_BUFFER_LENGTH = 512;
    private RangeFilter rangeFilter;
    private CarSateBuffer carStateBuffer;
    List<CarState> list = new CopyOnWriteArrayList<>();
    List<CarState> listCarTempFiltered = new CopyOnWriteArrayList<>();

    @Before
    public void before() throws InterruptedException {
        carStateBuffer = new CarSateBuffer();
        rangeFilter = new RangeFilter();
        for (int i = MAX_RANGE_FILTER_BUFFER_LENGTH * 2; i > 0; i--) {
            CarState state = new CarState();
            state.setCarTimestamp(System.currentTimeMillis());
            state.setCarTemp((short) (i + (Math.random() * 50)));
            state.setCarId(1L);
            Thread.sleep(2);
            list.add(state);
            state = new CarState();
            state.setCarTimestamp(System.currentTimeMillis());
            state.setCarTemp((short) i);
            state.setCarId(1L);
            listCarTempFiltered.add(state);
        }
    }

    @Test
    public void checkOnInnerTest() {
        NavigableSet<CarState> set = new TreeSet<>();
        set.addAll(listCarTempFiltered);
        CarState fromList = set.last();
        CarState state = new CarState();
        state.setCarTemp(fromList.getCarTemp());
        state.setCarTimestamp(fromList.getCarTimestamp());
        state.setCarId(fromList.getCarId());
        Assert.assertEquals(true, rangeFilter.checkOnInner(listCarTempFiltered, state));
        state.setCarTimestamp((short) (fromList.getCarTimestamp()));
        Assert.assertEquals(false, rangeFilter.checkOnInner(listCarTempFiltered, state));
    }

    @Test
    public void checkOnSerialTest() {
        NavigableSet<CarState> set = new TreeSet<>();
        set.addAll(listCarTempFiltered);
        CarState fromList = set.last();
        CarState state = new CarState();
        state.setCarTemp(fromList.getCarTemp());
        state.setCarTimestamp(fromList.getCarTimestamp() + 1000L);
        state.setCarId(fromList.getCarId());
        Assert.assertEquals(true, rangeFilter.checkOnSerial(listCarTempFiltered, state));
        state.setCarTemp((short) (fromList.getCarTemp() + 1));
        Assert.assertEquals(false, rangeFilter.checkOnSerial(listCarTempFiltered, state));
    }

    @Test
    public void addNewAndRemoveOldTest() {
        NavigableSet<CarState> set = new TreeSet<>();
        List<CarState> copy = list.subList(MAX_RANGE_FILTER_BUFFER_LENGTH - 2, MAX_RANGE_FILTER_BUFFER_LENGTH * 2 - 1);
        set.addAll(copy);
        CarState oldest = set.first();
        CarState newest = set.last();
        set.remove(newest);
        List<CarState> result = new ArrayList<>(set);
        rangeFilter.addNewAndRemoveOld(result, newest);

        Assert.assertEquals(false, result.contains(oldest));
        Assert.assertEquals(true, result.contains(newest));
    }
}
