package com.example.javaobjectmapper.doma;

import java.util.List;
import org.seasar.doma.BatchInsert;
import org.seasar.doma.Dao;
import org.seasar.doma.Delete;
import org.seasar.doma.Select;
import org.seasar.doma.boot.ConfigAutowireable;

@Dao
@ConfigAutowireable
public interface PersonDao {

    @BatchInsert
    int[] insertSources(List<SourcePeople> sourcePeople);

    @BatchInsert
    int[] insertModelMapperTargets(List<MappedPeopleModelmapper> mappedPeople);

    @BatchInsert
    int[] insertMapStructTargets(List<MappedPeopleMapstruct> mappedPeople);

    @BatchInsert
    int[] insertLombokModelMapperTargets(List<MappedPeopleLombokModelmapper> mappedPeople);

    @BatchInsert
    int[] insertLombokMapStructTargets(List<MappedPeopleLombokMapstruct> mappedPeople);

    @Select
    long countSources();

    @Select
    long countModelMapperTargets();

    @Select
    long countMapStructTargets();

    @Select
    long countLombokModelMapperTargets();

    @Select
    long countLombokMapStructTargets();

    @Select
    List<SourcePeople> selectSources(int limit, int offset);

    @Delete(sqlFile = true)
    int deleteSources();

    @Delete(sqlFile = true)
    int deleteModelMapperTargets();

    @Delete(sqlFile = true)
    int deleteMapStructTargets();

    @Delete(sqlFile = true)
    int deleteLombokModelMapperTargets();

    @Delete(sqlFile = true)
    int deleteLombokMapStructTargets();
}
