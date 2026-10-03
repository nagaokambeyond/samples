package com.example.csvexport.dao;

import com.example.csvexport.generated.entity.Orders;
import java.util.List;
import org.seasar.doma.BatchInsert;
import org.seasar.doma.Dao;
import org.seasar.doma.Select;
import org.seasar.doma.boot.ConfigAutowireable;

@Dao
@ConfigAutowireable
public interface SeedOrderDao {
    @Select long count();
    @BatchInsert int[] insert(List<Orders> orders);
}
