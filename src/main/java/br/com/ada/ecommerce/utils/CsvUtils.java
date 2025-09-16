package br.com.ada.ecommerce.utils;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class CsvUtils {
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public static List<String[]> readCsv(String fileName) throws IOException {
        List<String[]> records = new ArrayList<>();
        
        if (!Files.exists(Paths.get(fileName))) {
            return records;
        }

        try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] values = parseCsvLine(line);
                records.add(values);
            }
        }
        
        return records;
    }

    private static String[] parseCsvLine(String line) {
        List<String> fields = new ArrayList<>();
        StringBuilder currentField = new StringBuilder();
        boolean inQuotes = false;
        
        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);
            
            if (c == '"') {
                inQuotes = !inQuotes;
            } else if (c == ',' && !inQuotes) {
                fields.add(currentField.toString().trim());
                currentField = new StringBuilder();
            } else {
                currentField.append(c);
            }
        }
        
        // Adicionar o último campo
        fields.add(currentField.toString().trim());
        
        return fields.toArray(new String[0]);
    }

    public static void writeCsv(String fileName, List<String[]> data) throws IOException {
        try (PrintWriter pw = new PrintWriter(new FileWriter(fileName))) {
            for (String[] row : data) {
                StringBuilder line = new StringBuilder();
                for (int i = 0; i < row.length; i++) {
                    if (i > 0) {
                        line.append(",");
                    }
                    line.append(row[i]);
                }
                pw.println(line.toString());
            }
        }
    }

    public static void appendToCsv(String fileName, String[] data) throws IOException {
        try (PrintWriter pw = new PrintWriter(new FileWriter(fileName, true))) {
            StringBuilder line = new StringBuilder();
            for (int i = 0; i < data.length; i++) {
                if (i > 0) {
                    line.append(",");
                }
                line.append(data[i]);
            }
            pw.println(line.toString());
        }
    }

    public static String formatDateTime(LocalDateTime dateTime) {
        return dateTime != null ? dateTime.format(DATE_TIME_FORMATTER) : "";
    }

    public static LocalDateTime parseDateTime(String dateTimeString) {
        if (dateTimeString == null || dateTimeString.isEmpty()) {
            return null;
        }
        try {
            return LocalDateTime.parse(dateTimeString, DATE_TIME_FORMATTER);
        } catch (Exception e) {
            return null;
        }
    }

    public static String escapeCsv(String value) {
        if (value == null) {
            return "";
        }
        if (value.contains(",") || value.contains("\"") || value.contains("\n")) {
            return "\"" + value.replace("\"", "\"\"") + "\"";
        }
        return value;
    }
}