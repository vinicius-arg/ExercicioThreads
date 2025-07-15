package edu.ufs.os.threads.test;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Vector;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.List;
import java.util.stream.IntStream;

import edu.ufs.os.threads.ThreadSafeArrayList;
import edu.ufs.os.util.FileHandler;
import edu.ufs.os.util.Measure;

class Writer extends Thread {
    List<Integer> list;
    Vector<Measure> samples;
    ReentrantReadWriteLock mutex;

    public Writer(List<Integer> l, Vector<Measure> v, ReentrantReadWriteLock mutex) {
        this.list = l;
        this.samples = v;
        this.mutex = mutex;
    }

    @Override
    public void run() {
        samples.add(Test.testAdd(list));
    }
}

class Reader extends Thread {
    List<Integer> list;
    Vector<Measure> samples;
    ReentrantReadWriteLock mutex;

    public Reader(List<Integer> l, Vector<Measure> v, ReentrantReadWriteLock mutex) {
        this.list = l;
        this.samples = v;
        this.mutex = mutex;
    }

    @Override
    public void run() {
        samples.add(Test.testContains(list));
    }
}

class Remover extends Thread {
    List<Integer> list;
    Vector<Measure> samples;
    ReentrantReadWriteLock mutex;

    public Remover(List<Integer> l, Vector<Measure> v, ReentrantReadWriteLock mutex) {
        this.list = l;
        this.samples = v;
        this.mutex = mutex;
    }

    @Override
    public void run() {
        mutex.writeLock().lock();
        samples.add(Test.testRemove(list));
        mutex.writeLock().unlock();
    }
}

public class MultiThreadTest {
    private Vector<Integer> vector = new Vector<>();
    private ThreadSafeArrayList<Integer> threadSafe = new ThreadSafeArrayList<>();
    private ReentrantReadWriteLock mutex = new ReentrantReadWriteLock();

    String path = System.getProperty("user.dir") + "/output/";

    private Vector<Measure> threadSafeAdd = new Vector<>();
    private Vector<Measure> threadSafeContains = new Vector<>();
    private Vector<Measure> threadSafeRemove = new Vector<>();
    private Vector<Measure> vectorAdd = new Vector<>();
    private Vector<Measure> vectorContains = new Vector<>();
    private Vector<Measure> vectorRemove = new Vector<>();

    public static void fillList(List<Integer> list, int n) {
        list.clear();
        for (int i = 0; i < n; i++) list.add(0);
    }

    public static void main(String[] args) throws InterruptedException {
        final int maxExp = 5; // Define caso de teste final como 1e5
        final int threadCount = 16;
        MultiThreadTest m = new MultiThreadTest();

        List<Integer> testCases = IntStream.rangeClosed(0, maxExp)
            .map(x -> (int)Math.pow(10, x))
            .boxed()
            .toList();

        // Declaração de threads
        Thread[] thread = new Thread[threadCount];

        // Testes de Vector
        for (int i : testCases) {
            MultiThreadTest.fillList(m.vector, i);

            // Instanciação de threads com papéis definidos
            for (int j = 0; j < threadCount; j++) {
                if (j < Math.floor((double)threadCount / 3)) {
                    thread[j] = new Writer(m.vector, m.vectorAdd, m.mutex);
                } else if (j < Math.floor(2 * (double)threadCount / 3)) {
                    thread[j] = new Remover(m.vector, m.vectorRemove, m.mutex);
                } else {
                    thread[j] = new Reader(m.vector, m.vectorContains, m.mutex);
                }

                thread[j].start();
            }
        }

        for (int i = 0; i < 16; i++) {
            thread[i].join();
        }

        // Testes de ThreadSafeArrayList
        for (int i : testCases) {
            MultiThreadTest.fillList(m.vector, i);

            // Instanciação de threads com papéis definidos
            for (int j = 0; j < threadCount; j++) {
                if (j < Math.floor((double)threadCount / 3)) {
                    thread[j] = new Writer(m.threadSafe, m.threadSafeAdd, m.mutex);
                } else if (j < Math.floor(2 * (double)threadCount / 3)) {
                    thread[j] = new Remover(m.threadSafe, m.threadSafeRemove, m.mutex);
                } else {
                    thread[j] = new Reader(m.threadSafe, m.threadSafeContains, m.mutex);
                }

                thread[j].start();
            }
        }

        for (int i = 0; i < 16; i++) {
            thread[i].join();
        }

        // Impressão em CSV
        try (PrintWriter writer = new PrintWriter(new FileWriter(m.path + "MultiThread_Benchmark.csv"))) {
            int size = testCases.size();
            // Imprimir cabeçalho da tabela
            writer.println("n,Vector_add(0 e),ThreadSafe_add(0 e),Vector_remove(0),ThreadSafe_remove(0),Vector_contains(rand),ThreadSafe_contains(rand)");
            // Impressão de dados
            List<List<Measure>> variables = List.of(m.vectorAdd, m.threadSafeAdd, m.vectorRemove, m.threadSafeRemove, m.vectorContains, m.threadSafeContains);
            FileHandler.writeLinesToCSV(writer, variables, size, testCases);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
