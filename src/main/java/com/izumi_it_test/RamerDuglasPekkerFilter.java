package com.izumi_it_test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.DoubleStream;

import static java.lang.Math.abs;


public class RamerDuglasPekkerFilter {

    private static final byte EPSILON_DUGLAS_PEKKER_ALGO = 8;

    public List<Map<Integer, Double>> trimmingData(double[] data, double dltMax) {
        List<Map<Integer, Double>> tss = new ArrayList<>();
        List<Double> ts = DoubleStream.of(data).boxed().collect(Collectors.toList());

        if (data.length < 2) {
            return new ArrayList<>();
        }

        List<Integer> stack = new ArrayList<>();
        stack.add(0);
        stack.add(ts.size() - 1);

        boolean[] keepPoint = new boolean[ts.size()];
        for (int i = 0; i < keepPoint.length; i++) {
            keepPoint[i] = true;
        }
        while (!stack.isEmpty()) {

            int startIndex = stack.get(0);
            int endIndex = stack.get(1);

            double x1 = startIndex;
            double y1 = ts.get(startIndex);
            double x2 = endIndex;
            double y2 = ts.get(endIndex);

            stack.remove(0);
            stack.remove(0);

            double dmax = 0;
            int index = startIndex;

            double k = (y1 - y2) / (x1 - x2);
            double b = y2 - x2 * ((y1 - y2) / (x1 - x2));

            for (int i = startIndex + 1; i < endIndex - 1; i++) {
                if (keepPoint[i]) {
                    double x3 = i;
                    double y3 = ts.get(i);

                    double d = abs((k * x3 + b) - y3);
                    if (d > dmax) {
                        index = i;
                        dmax = d;
                    }
                }
            }
            if (dmax >= dltMax) {
                stack.add(startIndex);
                stack.add(index);
                stack.add(index);
                stack.add(endIndex);
            } else {
                for (int j = startIndex + 1; j < endIndex - 1; j++) {
                    keepPoint[j] = false;
                }
            }
        }

        for (int i = 0; i < ts.size(); i++) {
            if (keepPoint[i]) {
                Map<Integer, Double> map = new HashMap<>();
                map.put(0, ts.get(i));
                map.put(1, Double.valueOf(i));
                tss.add(map);
            }
        }

        return tss;
    }
}
