package edu.ufs.os.threads.test;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import edu.ufs.os.util.Measure;

public class Test {
    private static Random random = new Random();

    private Test() {}
    
    public static Measure testAdd(List<Integer> list) {
        ArrayList<Long> times = new ArrayList<>();

        for (int i = 0; i < Measure.SAMPLES; i++) {
            int value = random.nextInt(1000);
            long begin = System.nanoTime();
            // Adição de número randômico na cabeça
            list.add(0, value); 
            long end = System.nanoTime();

            times.add(end - begin);
        }

        return (new Measure(times));
    }

    public static Measure testRemove(List<Integer> list) {
        ArrayList<Long> times = new ArrayList<>();

        for (int i = 0; i < Measure.SAMPLES; i++) {
            // Remoção na cabeça
            if (list.isEmpty()) list.add(0);
            
            long begin = System.nanoTime();
            list.remove(0); 
            long end = System.nanoTime();
            
            times.add(end - begin);
        }

        return (new Measure(times));
    }

    public static Measure testContains(List<Integer> list) {
        ArrayList<Long> times = new ArrayList<>();

        for (int i = 0; i < Measure.SAMPLES; i++) {
            int value = random.nextInt(1000);
            long begin = System.nanoTime();
            // Busca de número randômico
            list.contains(value); 
            long end = System.nanoTime();

            times.add(end - begin);
        }

        return (new Measure(times));
    }

    public static String operationsPerSecond(Measure m, int n) {
        double a = m.getData() - m.getUncertainty(); // Tempo mínimo
        double b = m.getData() + m.getUncertainty(); // Tempo máximo

        final double second = 1e9; // 1s = 1e9ns

        double minOp = Math.round(second / b);
        double maxOp = Math.round(second / a);

        return("n=" + n + ": [" + String.format("%,.0f", minOp).replace(",", ".") + ", " + String.format("%,.0f", maxOp).replace(",", ".") + "]");
    }
}
