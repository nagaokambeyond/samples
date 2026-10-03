package com.example.csvexport.service;

import com.example.csvexport.dao.SeedOrderDao;
import com.example.csvexport.generated.entity.Orders;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
public class OrderSeeder implements ApplicationRunner {
    private final SeedOrderDao dao; private final int seedSize;
    public OrderSeeder(SeedOrderDao dao, @Value("${app.export.seed-size}") int seedSize) { this.dao = dao; this.seedSize = seedSize; }
    @Override
    public void run(ApplicationArguments args) {
        if (dao.count() > 0) return;
        LocalDateTime base = LocalDateTime.of(2026, 1, 1, 0, 0);
        for (int start = 1; start <= seedSize; start += 1000) {
            List<Orders> batch = new ArrayList<>(1000);
            for (int id = start; id < Math.min(start + 1000, seedSize + 1); id++) {
                var o = new Orders(); o.id = (long) id; o.orderNumber = String.format("ORD-%08d", id);
                o.customerName = "顧客" + (id % 10000); o.productName = "商品" + (id % 100);
                o.amount = BigDecimal.valueOf(1000L + id % 100000); o.status = switch (id % 4) { case 0 -> "NEW"; case 1 -> "PAID"; case 2 -> "SHIPPED"; default -> "CANCELLED"; };
                o.orderedAt = base.plusMinutes(id); o.createdAt = o.orderedAt; batch.add(o);
            }
            dao.insert(batch);
        }
    }
}
