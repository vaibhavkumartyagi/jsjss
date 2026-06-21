package com.assignments.corejava;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class FileReadExample {

    public static List<String> readFile(String filePath) throws IOException {
        List<String> lines = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                lines.add(line);
            }
        }

        return lines;
    }
    public static void main (String [] args) throws IOException {
       System.out.println(readFile("/Users/vaibhav/DOCKER/tt"));
    }
}
