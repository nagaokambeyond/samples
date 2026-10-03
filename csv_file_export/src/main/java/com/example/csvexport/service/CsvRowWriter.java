package com.example.csvexport.service;

import com.example.csvexport.generated.entity.Orders;
import java.io.Writer;
import com.opencsv.CSVWriter;

final class CsvRowWriter {
    private CsvRowWriter() { }
    static CSVWriter create(Writer writer) {
        return new CSVWriter(writer, CSVWriter.DEFAULT_SEPARATOR, CSVWriter.DEFAULT_QUOTE_CHARACTER,
                CSVWriter.DEFAULT_ESCAPE_CHARACTER, "\r\n");
    }
    static void writeHeader(CSVWriter csv) {
        csv.writeNext(new String[] {"注文番号", "顧客名", "商品名", "金額", "状態", "注文日時"}, false);
    }
    static void writeOrder(CSVWriter csv, Orders order) {
        csv.writeNext(new String[] {
                escapeFormula(order.orderNumber), escapeFormula(order.customerName), escapeFormula(order.productName),
                escapeFormula(order.amount.toPlainString()), escapeFormula(order.status), escapeFormula(order.orderedAt.toString())
        }, true);
    }
    private static String escapeFormula(String raw) {
        String value = raw == null ? "" : raw;
        if (!value.isEmpty() && "=+-@".indexOf(value.charAt(0)) >= 0) value = "'" + value;
        return value;
    }
}
