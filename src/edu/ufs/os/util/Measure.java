package edu.ufs.os.util;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

public class Measure extends Object {
    private double data;
    private double uncertainty;
    // Número de vezes que a medição é coletada
    public static final int SAMPLES = 1000;

    public Measure(List<Long> times) {
        this.uncertainty = Measure.calculateUncertainty(times);
        this.data = BigDecimal.valueOf(Measure.mean(times))
            .setScale(Measure.getSignificantNumberPosition(this.uncertainty), RoundingMode.DOWN)
            .doubleValue();
    }

    public double getData() {
        return this.data;
    }

    public double getUncertainty() {
        return this.uncertainty;
    }

    public String getMeasurement() {
        return (this.data + "+/-" + String.format("%.4f", this.uncertainty));
    }

    /* Calcula a média */
    public static double mean (List<Long> list) {
        return (list.stream().mapToLong(Long::longValue).average().orElse(0));
    }

    /* Calcula a incerteza de medição */
    public static double calculateUncertainty(List<Long> list) {
        if (list.isEmpty()) throw new IllegalArgumentException();

        double mean = Measure.mean(list);
        double terms = list.stream().mapToDouble(x -> Math.pow(x - mean, 2)).sum();

        double dp = Math.sqrt(terms / (list.size()-1));

        return (dp / Math.sqrt(list.size()));
    }

    public static int getSignificantNumberPosition(double sigma) {
        int count = 0;
        double precision = 10e-6;

        BigDecimal decimalSigma = BigDecimal.valueOf(sigma);

        for (double i = 1; i >= precision; i/=10) { 
            sigma = decimalSigma.setScale(count, RoundingMode.DOWN).doubleValue();
            if (sigma * i != 0) return count;
            count++;
        }
        // Se a incerteza for menor que 'precision', retorna o expoente
        return count;
    }

    /*
    public static void main(String[] args) {
        System.out.println(getSignificantNumberPosition(0.01));
    } 
     */
}
