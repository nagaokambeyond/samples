package com.example.csvexport.dao;

import com.example.csvexport.generated.entity.Orders;
import com.example.csvexport.model.OrderFilter;
import java.time.LocalDateTime;
import java.util.List;
import org.seasar.doma.Dao;
import org.seasar.doma.Select;
import org.seasar.doma.boot.ConfigAutowireable;

@Dao
@ConfigAutowireable
public interface OrderDao {
    @Select List<Orders> findPreview(OrderFilter filter);
    @Select List<Orders> findNextForExport(OrderFilter filter, LocalDateTime snapshotAt,
                                          LocalDateTime afterOrderedAt, Long afterId, int limit);
}
