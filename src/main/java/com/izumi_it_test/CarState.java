package com.izumi_it_test;

import lombok.Data;

@Data
public class CarState implements Comparable<CarState> {
    private long carId;
    private long carTimestamp;
    private short carTemp;

    @Override
    public int compareTo(CarState o) {
        if (this.carTimestamp > o.getCarTimestamp()) {
            return 1;
        }
        if (this.carTimestamp < o.getCarTimestamp()) {
            return -1;
        }
        return 0;
    }
}
