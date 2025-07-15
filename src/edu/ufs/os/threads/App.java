package edu.ufs.os.threads;

import java.util.Random;

public class App {
    public ThreadSafeArrayList<Integer> numbers = new ThreadSafeArrayList<>();
    public final int threadCount = 3;

    private Random random = new Random();
    private int operations = 100000;

    public class Writer extends Thread {
        private ThreadSafeArrayList<Integer> numbers;
 
        public Writer(ThreadSafeArrayList<Integer> list) {
            this.numbers = list;
        }

        @Override
        public void run() {
            for (int i = 0; i < operations; i++) {
                numbers.add(0, random.nextInt(100));
            }
        }
    }

    public class Reader extends Thread {
        private ThreadSafeArrayList<Integer> numbers;

        public Reader(ThreadSafeArrayList<Integer> list) {
            this.numbers = list;
        }

        @Override
        @SuppressWarnings("unused")
        public void run() {
            boolean hasNumber = false;
            for (int i = 0; i < operations; i++) {
                hasNumber = numbers.contains(random.nextInt(100));
            }
        }
    }

    public class Remover extends Thread {
        private ThreadSafeArrayList<Integer> numbers;

        public Remover(ThreadSafeArrayList<Integer> list) {
            this.numbers = list;
        }

        @Override
        public void run() {
            for (int i = 0; i < operations; i++) {
                if (i % 2 == 0) numbers.remove(0);
            }
        }
    }

    public static void main(String[] args) throws InterruptedException {
        App app = new App();

        Thread[] thread = new Thread[3];

        thread[0] = app.new Writer(app.numbers);
        thread[1] = app.new Reader(app.numbers);
        thread[2] = app.new Remover(app.numbers);

        for (int i = 0; i < app.threadCount; i++) thread[i].start();

        for (int i = 0; i < app.threadCount; i++) thread[i].join();

        System.out.println("***********************");
        System.out.println("Operacoes realizadas: " + app.numbers.getQuantityOfOperations() + "/" + 250000);
    }
}