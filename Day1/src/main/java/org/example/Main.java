package org.example;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class Main {

    private static final String REGEX = "\\s+";

    private static List<Integer> leftColumn = new LinkedList<>();
    private static List<Integer> rightColumn = new LinkedList<>();

    public static void main(String[] args) {

        try {
            ClassLoader classloader = Thread.currentThread().getContextClassLoader();
            InputStream is = classloader.getResourceAsStream("input.txt");
            InputStreamReader streamReader = new InputStreamReader(is, StandardCharsets.UTF_8);
            BufferedReader reader = new BufferedReader(streamReader);
            for (String line; (line = reader.readLine()) != null; ) {
                String[] foo = line.split(REGEX);
                leftColumn.add(Integer.valueOf(foo[0]));
                rightColumn.add(Integer.valueOf(foo[1]));
            }
        } catch (IOException ex) {
            System.err.println("Upsie Dupsie");
            System.exit(42);
        }
        processPartOne();
        processPartTwo();
    }

    private static void processPartOne() {
        Collections.sort(leftColumn);
        Collections.sort(rightColumn);

        int result = 0;

        for (int i = 0; i < leftColumn.size(); ++i) {

            result += leftColumn.get(i) > rightColumn.get(i) ? leftColumn.get(i) - rightColumn.get(i) : rightColumn.get(i) - leftColumn.get(i);
        }

        System.out.println(result);
    }

    private static void processPartTwo() {
        Collections.sort(leftColumn);
        Collections.sort(rightColumn);

        int result = 0;

        for (int i = 0; i < leftColumn.size(); ++i) {

            int appearings = Collections.frequency(rightColumn, leftColumn.get(i));
            result += leftColumn.get(i) * appearings;

        }

        System.out.println(result);
    }
}