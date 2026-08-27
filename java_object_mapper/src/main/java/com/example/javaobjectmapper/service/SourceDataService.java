package com.example.javaobjectmapper.service;

import com.example.javaobjectmapper.config.BenchmarkProperties;
import com.example.javaobjectmapper.doma.PersonDao;
import com.example.javaobjectmapper.doma.SourcePeople;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SourceDataService {

    private final PersonDao personDao;

    private final BenchmarkProperties properties;

    public SourceDataService(PersonDao personDao, BenchmarkProperties properties) {
        this.personDao = personDao;
        this.properties = properties;
    }

    @Transactional
    public void prepare(int recordCount) {
        personDao.deleteLombokMapStructTargets();
        personDao.deleteLombokModelMapperTargets();
        personDao.deleteMapStructTargets();
        personDao.deleteModelMapperTargets();
        personDao.deleteSources();

        int chunkSize = properties.getChunkSize();
        for (int start = 0; start < recordCount; start += chunkSize) {
            int end = Math.min(start + chunkSize, recordCount);
            personDao.insertSources(createPeople(start, end));
        }
    }

    private List<SourcePeople> createPeople(int startInclusive, int endExclusive) {
        List<SourcePeople> people = new ArrayList<>(endExclusive - startInclusive);
        LocalDateTime baseTime = LocalDateTime.of(2026, 1, 1, 0, 0);
        for (int index = startInclusive; index < endExclusive; index++) {
            long id = index + 1L;
            SourcePeople person = new SourcePeople();
            person.setId(id);
            person.setFirstName("First" + (index % 1_000));
            person.setLastName("Last" + (index % 2_000));
            person.setAge(18 + (index % 58));
            person.setEmail("person" + id + "@example.com");
            person.setCity("City" + (index % 47));
            person.setStreet((100 + index % 900) + " Benchmark Street");
            person.setPostalCode(String.format("%03d-%04d", index % 1000, index % 10000));
            person.setLoyaltyPoints(index % 50_000);
            person.setCreatedAt(baseTime.plusSeconds(index));
            people.add(person);
        }
        return people;
    }
}
