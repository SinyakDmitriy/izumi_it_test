package com.izumi_it_test;

import org.apache.commons.math3.complex.Complex;
import org.apache.commons.math3.transform.DftNormalization;
import org.apache.commons.math3.transform.FastFourierTransformer;
import org.apache.commons.math3.transform.TransformType;

import java.util.List;
import java.util.Map;

import static org.apache.commons.math3.util.Precision.round;

public class CarDataFilter {
    private static final int WINDOW_SIZE = 50;
    private static final int ITERATION = 30;
    private static final int MAX_LEVEL = 8;
    private static final int TO_FREQUENT = 2;

    public double[] do_(int windowSize, int iteration, int maxLevel, int toFrequent, double[] data) {
        RamerDuglasPekkerFilter pekker = new RamerDuglasPekkerFilter();

        MedianFilter medianFilter = new MedianFilter(windowSize);
        double[] filtered = new double[data.length];
        double[] filtered2 = new double[data.length];

        for (int j = 0; j < iteration; j++) {

            if (j == 0) {
                filtered = data;
            } else if (j == 1) {
                filtered = new double[data.length];
            }

            if (j % 2 == 0) {
                for (int i = 0; i < data.length; i++) {
                    filtered2[i] = medianFilter.filtered(filtered[i]);
                }
            } else {
                for (int i = data.length - 1; i >= 0; i--) {
                    filtered[i] = medianFilter.filtered(filtered2[i]);
                }
            }
        }

        List<Map<Integer, Double>> list7 = pekker.trimmingData(filtered, maxLevel);

        double[] innerData = new double[data.length];

        for (int i = 0; i < list7.size() - 1; i++) {
            Map<Integer, Double> first = list7.get(i);
            Map<Integer, Double> second = list7.get(i + 1);

            double y2 = second.get(0);
            double y1 = first.get(0);
            double x2 = second.get(1);
            double x1 = first.get(1);
            double k = (y1 - y2) / (x1 - x2);
            double b = y2 - x2 * ((y1 - y2) / (x1 - x2));

            for (int j = first.get(1).intValue(); j <= second.get(1).intValue(); j++) {
                innerData[j] = k * j + b;
            }
        }

        double[] minus = new double[data.length];
        for (int i = 0; i < data.length; i++) {
            minus[i] = data[i] - innerData[i];
        }

        FastFourierTransformer fft = new FastFourierTransformer(DftNormalization.STANDARD);

        Complex[] forward = fft.transform(minus, TransformType.FORWARD);

        for (int i = 0; i < data.length; i++) {
            if (i > toFrequent && i < data.length - toFrequent) {
                forward[i] = new Complex(0);
            }
        }

        Complex[] inverse = fft.transform(forward, TransformType.INVERSE);
        for (int i = 0; i < innerData.length; i++) {
            innerData[i] = innerData[i] + round(inverse[i].getReal(), 2);
        }

        return innerData;
    }
}
