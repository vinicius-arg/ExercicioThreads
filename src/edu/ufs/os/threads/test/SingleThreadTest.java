package edu.ufs.os.threads.test;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

import edu.ufs.os.threads.ThreadSafeArrayList;
import edu.ufs.os.util.FileHandler;
import edu.ufs.os.util.Measure;

public class SingleThreadTest {
    private ArrayList<Integer> arrayList = new ArrayList<>();
    private ThreadSafeArrayList<Integer> threadSafe = new ThreadSafeArrayList<>();

    String path = System.getProperty("user.dir") + "/output/";

    private ArrayList<Measure> threadSafeAdd = new ArrayList<>();
    private ArrayList<Measure> threadSafeContains = new ArrayList<>();
    private ArrayList<Measure> threadSafeRemove = new ArrayList<>();
    private ArrayList<Measure> arrayListAdd = new ArrayList<>();
    private ArrayList<Measure> arrayListContains = new ArrayList<>();
    private ArrayList<Measure> arrayListRemove = new ArrayList<>();

    private void fillList(List<Integer> list, int n) {
        list.clear();
        for (int i = 0; i < n; i++) list.add(0);
    }

    public static void main(String[] args) {
        final int maxExp = 5; // Define caso de teste final como 1e5
        SingleThreadTest s = new SingleThreadTest();

        List<Integer> testCases = IntStream.rangeClosed(0, maxExp)
            .map(x -> (int)Math.pow(10, x))
            .boxed()
            .toList();

        // Testes de ArrayList
        for (int i : testCases) {
            s.fillList(s.arrayList, i);
            s.arrayListAdd.add(Test.testAdd(s.arrayList));
        }

        for (int i : testCases) {
            s.fillList(s.arrayList, i);
            s.arrayListContains.add(Test.testContains(s.arrayList));
        }

        for (int i : testCases) {
            s.fillList(s.arrayList, i+1);
            s.arrayListRemove.add(Test.testRemove(s.arrayList));
        }

        // Testes de ThreadSafeArrayList
        for (int i : testCases) {
            s.fillList(s.threadSafe, i);
            s.threadSafeAdd.add(Test.testAdd(s.threadSafe));
        }

        for (int i : testCases) {
            s.fillList(s.threadSafe, i);
            s.threadSafeContains.add(Test.testContains(s.threadSafe));
        }

        for (int i : testCases) {
            s.fillList(s.threadSafe, i);
            s.threadSafeRemove.add(Test.testRemove(s.threadSafe));
        }

        // Impressão em CSV
        try (PrintWriter writer = new PrintWriter(new FileWriter(s.path + "SingleThread_Benchmark.csv"))) {
            int size = testCases.size();
            // Imprimir cabeçalho da tabela
            writer.println("n,ArrayList_add(0 e),ThreadSafe_add(0 e),ArrayList_remove(0),ThreadSafe_remove(0),ArrayList_contains(rand),ThreadSafe_contains(rand)");
            // Impressão de dados
            List<List<Measure>> variables = List.of(s.arrayListAdd, s.threadSafeAdd, s.arrayListRemove, s.threadSafeRemove, s.arrayListContains, s.threadSafeContains);
            FileHandler.writeLinesToCSV(writer, variables, size, testCases);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}