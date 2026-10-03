package com.example.csvexport.dao;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.csvexport.CsvExportApplication;
import com.example.csvexport.generated.entity.Orders;
import com.example.csvexport.model.OrderFilter;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(
        classes = CsvExportApplication.class,
        webEnvironment = SpringBootTest.WebEnvironment.MOCK,
        properties = {
                "app.export.seed-size=0",
                "app.export.storage-dir=./build/test-exports",
                "spring.datasource.url=jdbc:h2:mem:order_dao_test;MODE=MySQL;DATABASE_TO_LOWER=TRUE;DB_CLOSE_DELAY=-1"
        })
class OrderDaoH2IntegrationTest {
    @Autowired OrderDao orderDao;
    @Autowired SeedOrderDao seedOrderDao;

    @Test
    void findsOrdersWithinAnInclusiveEndDate() {
        var order = new Orders();
        order.id = 1L;
        order.orderNumber = "ORD-00000001";
        order.customerName = "顧客1";
        order.productName = "商品1";
        order.amount = BigDecimal.TEN;
        order.status = "PAID";
        order.orderedAt = LocalDateTime.of(2026, 10, 31, 23, 59, 59);
        order.createdAt = order.orderedAt;
        seedOrderDao.insert(List.of(order));

        var filter = new OrderFilter(null, LocalDate.of(2026, 10, 1), LocalDate.of(2026, 10, 31), null, null, null);

        assertThat(orderDao.findPreview(filter)).extracting(result -> result.orderNumber).contains("ORD-00000001");
    }
}
