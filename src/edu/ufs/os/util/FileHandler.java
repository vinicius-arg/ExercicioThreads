package edu.ufs.os.util;

import java.io.PrintWriter;
import java.util.List;
import java.util.StringJoiner;

public class FileHandler {
    private FileHandler() {}

    public static void writeToCSV(PrintWriter writer, String name, List<Measure> list) {
        int size = list.size();

        // Cabeçalho da medição
        writer.print(name + ","); 

        for (int i = 0; i <= size; i++) {
            // Impressão de itens
            writer.print(list.get(i).getMeasurement() + (i == size ? "\n" : ","));
        }
    }

    public static void writeLinesToCSV(PrintWriter writer, List<List<Measure>> variablesList, int size, List<Integer> testCases) {        
        for (int i = 0; i < size; i++) {
            StringJoiner line = new StringJoiner(",");

            line.add(testCases.get(i).toString());

            for (int j = 0; j < variablesList.size(); j++) {
                line.add(variablesList.get(j).get(i).getMeasurement());
            }

            writer.println(line);
        }
    }
}
