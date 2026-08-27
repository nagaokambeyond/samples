package com.example.javaobjectmapper.doma;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.javaobjectmapper.service.SourceDataService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(properties = "benchmark.auto-run=false")
class PersonDaoTest {

    @Autowired
    private PersonDao personDao;

    @Autowired
    private SourceDataService sourceDataService;

    @Test
    void insertsAndReadsSourcePeopleWithDoma() {
        sourceDataService.prepare(10);

        assertThat(personDao.countSources()).isEqualTo(10);
        assertThat(personDao.countModelMapperTargets()).isZero();
        assertThat(personDao.countMapStructTargets()).isZero();
        assertThat(personDao.countLombokModelMapperTargets()).isZero();
        assertThat(personDao.countLombokMapStructTargets()).isZero();
        assertThat(personDao.selectSources(3, 0))
                .hasSize(3)
                .first()
                .extracting(SourcePeople::getFirstName)
                .isEqualTo("First0");
    }
}
