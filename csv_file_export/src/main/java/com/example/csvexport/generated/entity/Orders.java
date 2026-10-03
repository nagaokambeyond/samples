package com.example.csvexport.generated.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import org.seasar.doma.Column;
import org.seasar.doma.Entity;
import org.seasar.doma.Id;
import org.seasar.doma.Metamodel;
import org.seasar.doma.Table;

/**
 * 
 */
@Entity(metamodel = @Metamodel)
@Table(name = "orders")
public class Orders extends AbstractOrders {

    /** */
    @Id
    @Column(name = "id")
    public Long id;

    /** */
    @Column(name = "order_number")
    public String orderNumber;

    /** */
    @Column(name = "customer_name")
    public String customerName;

    /** */
    @Column(name = "product_name")
    public String productName;

    /** */
    @Column(name = "amount")
    public BigDecimal amount;

    /** */
    @Column(name = "status")
    public String status;

    /** */
    @Column(name = "ordered_at")
    public LocalDateTime orderedAt;

    /** */
    @Column(name = "created_at")
    public LocalDateTime createdAt;
}
