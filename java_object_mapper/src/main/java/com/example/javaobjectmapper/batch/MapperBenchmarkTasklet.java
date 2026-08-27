package com.example.javaobjectmapper.batch;

import com.example.javaobjectmapper.config.BenchmarkProperties;
import com.example.javaobjectmapper.doma.MappedPeopleLombokMapstruct;
import com.example.javaobjectmapper.doma.MappedPeopleLombokModelmapper;
import com.example.javaobjectmapper.doma.MappedPeopleMapstruct;
import com.example.javaobjectmapper.doma.MappedPeopleModelmapper;
import com.example.javaobjectmapper.doma.PersonDao;
import com.example.javaobjectmapper.doma.SourcePeople;
import com.example.javaobjectmapper.mapper.LombokMappedPerson;
import com.example.javaobjectmapper.mapper.MapStructPersonMapper;
import java.util.List;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.StepContribution;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.infrastructure.repeat.RepeatStatus;

public class MapperBenchmarkTasklet implements Tasklet {

    private static final Logger log = LoggerFactory.getLogger(MapperBenchmarkTasklet.class);

    private final String mapperName;

    private final Variant variant;

    private final PersonDao personDao;

    private final ModelMapper modelMapper;

    private final MapStructPersonMapper mapStructPersonMapper;

    private final BenchmarkProperties properties;

    private MapperBenchmarkTasklet(
            String mapperName,
            PersonDao personDao,
            ModelMapper modelMapper,
            MapStructPersonMapper mapStructPersonMapper,
            BenchmarkProperties properties) {
        this.mapperName = mapperName;
        this.variant = Variant.from(mapperName);
        this.personDao = personDao;
        this.modelMapper = modelMapper;
        this.mapStructPersonMapper = mapStructPersonMapper;
        this.properties = properties;
    }

    public static MapperBenchmarkTasklet modelMapper(
            PersonDao personDao, ModelMapper modelMapper, BenchmarkProperties properties) {
        return new MapperBenchmarkTasklet("ModelMapper", personDao, modelMapper, null, properties);
    }

    public static MapperBenchmarkTasklet mapStruct(
            PersonDao personDao, MapStructPersonMapper mapper, BenchmarkProperties properties) {
        return new MapperBenchmarkTasklet("MapStruct", personDao, null, mapper, properties);
    }

    public static MapperBenchmarkTasklet lombokModelMapper(
            PersonDao personDao, ModelMapper modelMapper, BenchmarkProperties properties) {
        return new MapperBenchmarkTasklet("ModelMapper + Lombok DTO", personDao, modelMapper, null, properties);
    }

    public static MapperBenchmarkTasklet lombokMapStruct(
            PersonDao personDao, MapStructPersonMapper mapper, BenchmarkProperties properties) {
        return new MapperBenchmarkTasklet("MapStruct + Lombok DTO", personDao, null, mapper, properties);
    }

    @Override
    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) {
        int recordCount = chunkContext.getStepContext()
                .getJobParameters()
                .get("recordCount") instanceof Long value ? value.intValue() : properties.getDefaultRecordCount();
        int chunkSize = properties.getChunkSize();
        long startNanos = System.nanoTime();
        int processed = 0;

        for (int offset = 0; offset < recordCount; offset += chunkSize) {
            List<SourcePeople> sourcePeople = personDao.selectSources(chunkSize, offset);
            if (sourcePeople.isEmpty()) {
                break;
            }
            mapAndInsert(sourcePeople);
            processed += sourcePeople.size();
        }

        long elapsedNanos = System.nanoTime() - startNanos;
        double elapsedMillis = elapsedNanos / 1_000_000.0;
        double throughput = processed / (elapsedNanos / 1_000_000_000.0);
        log.info("{} result: processed={} elapsedMillis={} recordsPerSecond={}",
                mapperName,
                processed,
                String.format("%.3f", elapsedMillis),
                String.format("%.2f", throughput));
        return RepeatStatus.FINISHED;
    }

    private void mapAndInsert(List<SourcePeople> sourcePeople) {
        switch (variant) {
            case MODEL_MAPPER -> {
                List<MappedPeopleModelmapper> mappedPeople = sourcePeople.stream()
                        .map(source -> modelMapper.map(source, MappedPeopleModelmapper.class))
                        .toList();
                personDao.insertModelMapperTargets(mappedPeople);
            }
            case MAPSTRUCT -> {
                List<MappedPeopleMapstruct> mappedPeople = sourcePeople.stream()
                        .map(mapStructPersonMapper::toEntity)
                        .toList();
                personDao.insertMapStructTargets(mappedPeople);
            }
            case LOMBOK_MODEL_MAPPER -> {
                List<MappedPeopleLombokModelmapper> mappedPeople = sourcePeople.stream()
                        .map(source -> modelMapper.map(source, LombokMappedPerson.class))
                        .map(MapperBenchmarkTasklet::toLombokModelMapperEntity)
                        .toList();
                personDao.insertLombokModelMapperTargets(mappedPeople);
            }
            case LOMBOK_MAPSTRUCT -> {
                List<MappedPeopleLombokMapstruct> mappedPeople = sourcePeople.stream()
                        .map(mapStructPersonMapper::toLombokDto)
                        .map(MapperBenchmarkTasklet::toLombokMapStructEntity)
                        .toList();
                personDao.insertLombokMapStructTargets(mappedPeople);
            }
        }
    }

    private static MappedPeopleLombokModelmapper toLombokModelMapperEntity(LombokMappedPerson source) {
        MappedPeopleLombokModelmapper target = new MappedPeopleLombokModelmapper();
        copy(source, target);
        return target;
    }

    private static MappedPeopleLombokMapstruct toLombokMapStructEntity(LombokMappedPerson source) {
        MappedPeopleLombokMapstruct target = new MappedPeopleLombokMapstruct();
        copy(source, target);
        return target;
    }

    private static void copy(LombokMappedPerson source, MappedPeopleLombokModelmapper target) {
        target.setId(source.getId());
        target.setSourceId(source.getSourceId());
        target.setFullName(source.getFullName());
        target.setAge(source.getAge());
        target.setAgeGroup(source.getAgeGroup());
        target.setEmail(source.getEmail());
        target.setAddressLine(source.getAddressLine());
        target.setLoyaltyPoints(source.getLoyaltyPoints());
        target.setCreatedAt(source.getCreatedAt());
    }

    private static void copy(LombokMappedPerson source, MappedPeopleLombokMapstruct target) {
        target.setId(source.getId());
        target.setSourceId(source.getSourceId());
        target.setFullName(source.getFullName());
        target.setAge(source.getAge());
        target.setAgeGroup(source.getAgeGroup());
        target.setEmail(source.getEmail());
        target.setAddressLine(source.getAddressLine());
        target.setLoyaltyPoints(source.getLoyaltyPoints());
        target.setCreatedAt(source.getCreatedAt());
    }

    private enum Variant {
        MODEL_MAPPER,
        MAPSTRUCT,
        LOMBOK_MODEL_MAPPER,
        LOMBOK_MAPSTRUCT;

        private static Variant from(String mapperName) {
            return switch (mapperName) {
                case "ModelMapper" -> MODEL_MAPPER;
                case "MapStruct" -> MAPSTRUCT;
                case "ModelMapper + Lombok DTO" -> LOMBOK_MODEL_MAPPER;
                case "MapStruct + Lombok DTO" -> LOMBOK_MAPSTRUCT;
                default -> throw new IllegalArgumentException("Unknown mapper: " + mapperName);
            };
        }
    }
}
