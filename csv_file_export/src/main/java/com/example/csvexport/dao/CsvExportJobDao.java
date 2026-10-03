package com.example.csvexport.dao;

import com.example.csvexport.generated.entity.CsvExportJobs;
import java.time.LocalDateTime;
import java.util.List;
import org.seasar.doma.Dao;
import org.seasar.doma.Delete;
import org.seasar.doma.Insert;
import org.seasar.doma.Select;
import org.seasar.doma.Update;
import org.seasar.doma.boot.ConfigAutowireable;

@Dao
@ConfigAutowireable
public interface CsvExportJobDao {
    @Insert int insert(CsvExportJobs job);
    @Update int update(CsvExportJobs job);
    @Delete int delete(CsvExportJobs job);
    @Select CsvExportJobs selectNextQueuedForUpdate();
    @Select CsvExportJobs selectByIdAndOwner(Long id, String ownerUsername);
    @Select List<CsvExportJobs> selectByOwner(String ownerUsername);
    @Select List<CsvExportJobs> selectExpired(LocalDateTime now);
}
