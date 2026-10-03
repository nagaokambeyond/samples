package com.example.csvexport.service;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.csvexport.generated.entity.Orders;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.OutputStreamWriter;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;
import com.opencsv.CSVWriter;
import org.junit.jupiter.api.Test;

class CsvRowWriterTest {
    @Test
    void createsSingleBomCsvEntryAndEscapesFormulaAndQuotes() throws Exception {
        var order = new Orders(); order.orderNumber = "=1+1"; order.customerName = "A\"lice"; order.productName = "商品";
        order.amount = new BigDecimal("123.45"); order.status = "PAID"; order.orderedAt = LocalDateTime.of(2026, 1, 2, 3, 4);
        var bytes = new ByteArrayOutputStream();
        try (var zip = new ZipOutputStream(bytes, StandardCharsets.UTF_8)) {
            zip.putNextEntry(new ZipEntry("orders-20260102-030400.csv"));
            var writer = new OutputStreamWriter(zip, StandardCharsets.UTF_8);
            writer.write('\uFEFF');
            CSVWriter csv = CsvRowWriter.create(writer);
            CsvRowWriter.writeHeader(csv); CsvRowWriter.writeOrder(csv, order); csv.flush(); zip.closeEntry();
        }
        try (var input = new ZipInputStream(new ByteArrayInputStream(bytes.toByteArray()), StandardCharsets.UTF_8)) {
            assertThat(input.getNextEntry().getName()).isEqualTo("orders-20260102-030400.csv");
            String csv = new String(input.readAllBytes(), StandardCharsets.UTF_8);
            assertThat(input.getNextEntry()).isNull();
            assertThat(csv).startsWith("\uFEFF注文番号").contains("\"'=1+1\",\"A\"\"lice\"");
        }
    }
}
