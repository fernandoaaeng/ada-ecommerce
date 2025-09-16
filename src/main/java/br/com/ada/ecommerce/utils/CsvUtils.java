package br.com.ada.ecommerce.utils;

import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import com.opencsv.exceptions.CsvException;

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

        try (FileReader fileReader = new FileReader(fileName);
             CSVReader csvReader = new CSVReader(fileReader)) {
            records = csvReader.readAll();
        } catch (CsvException e) {
            throw new IOException("Erro ao ler arquivo CSV: " + e.getMessage(), e);
        }
        
        return records;
    }

    public static void writeCsv(String fileName, List<String[]> data) throws IOException {
        try (FileWriter fileWriter = new FileWriter(fileName);
             CSVWriter csvWriter = new CSVWriter(fileWriter)) {
            csvWriter.writeAll(data);
        }
    }

    public static void appendToCsv(String fileName, String[] data) throws IOException {
        try (FileWriter fileWriter = new FileWriter(fileName, true);
             CSVWriter csvWriter = new CSVWriter(fileWriter)) {
            csvWriter.writeNext(data);
        }
    }

    public static String escapeCsvData(String data) {
        if (data == null) {
            return "";
        }
        
        // Se contém vírgula, aspas duplas ou quebra de linha, precisa ser escapado
        if (data.contains(",") || data.contains("\"") || data.contains("\n") || data.contains("\r")) {
            // Escapa aspas duplas duplicando-as
            data = data.replace("\"", "\"\"");
            // Envolve em aspas duplas
            return "\"" + data + "\"";
        }
        
        return data;
    }

    public static String formatDateTime(LocalDateTime dateTime) {
        return dateTime != null ? dateTime.format(DATE_TIME_FORMATTER) : "";
    }

    public static LocalDateTime parseDateTime(String dateTimeStr) {
        if (dateTimeStr == null || dateTimeStr.trim().isEmpty()) {
            return null;
        }
        try {
            return LocalDateTime.parse(dateTimeStr.trim(), DATE_TIME_FORMATTER);
        } catch (Exception e) {
            return null;
        }
    }
}