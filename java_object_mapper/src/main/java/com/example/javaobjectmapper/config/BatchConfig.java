package com.example.javaobjectmapper.config;

import com.example.javaobjectmapper.batch.MapperBenchmarkTasklet;
import com.example.javaobjectmapper.doma.PersonDao;
import com.example.javaobjectmapper.mapper.MapStructPersonMapper;
import org.modelmapper.ModelMapper;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.job.Job;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.Step;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
public class BatchConfig {

    @Bean
    public Job modelMapperJob(JobRepository jobRepository, Step modelMapperStep) {
        return new JobBuilder("modelMapperJob", jobRepository)
                .start(modelMapperStep)
                .build();
    }

    @Bean
    public Step modelMapperStep(
            JobRepository jobRepository,
            PlatformTransactionManager transactionManager,
            PersonDao personDao,
            ModelMapper modelMapper,
            BenchmarkProperties properties) {
        return new StepBuilder("modelMapperStep", jobRepository)
                .tasklet(MapperBenchmarkTasklet.modelMapper(personDao, modelMapper, properties), transactionManager)
                .build();
    }

    @Bean
    public Job mapStructJob(JobRepository jobRepository, Step mapStructStep) {
        return new JobBuilder("mapStructJob", jobRepository)
                .start(mapStructStep)
                .build();
    }

    @Bean
    public Job lombokModelMapperJob(JobRepository jobRepository, Step lombokModelMapperStep) {
        return new JobBuilder("lombokModelMapperJob", jobRepository)
                .start(lombokModelMapperStep)
                .build();
    }

    @Bean
    public Step lombokModelMapperStep(
            JobRepository jobRepository,
            PlatformTransactionManager transactionManager,
            PersonDao personDao,
            ModelMapper modelMapper,
            BenchmarkProperties properties) {
        return new StepBuilder("lombokModelMapperStep", jobRepository)
                .tasklet(MapperBenchmarkTasklet.lombokModelMapper(personDao, modelMapper, properties), transactionManager)
                .build();
    }

    @Bean
    public Step mapStructStep(
            JobRepository jobRepository,
            PlatformTransactionManager transactionManager,
            PersonDao personDao,
            MapStructPersonMapper mapper,
            BenchmarkProperties properties) {
        return new StepBuilder("mapStructStep", jobRepository)
                .tasklet(MapperBenchmarkTasklet.mapStruct(personDao, mapper, properties), transactionManager)
                .build();
    }

    @Bean
    public Job lombokMapStructJob(JobRepository jobRepository, Step lombokMapStructStep) {
        return new JobBuilder("lombokMapStructJob", jobRepository)
                .start(lombokMapStructStep)
                .build();
    }

    @Bean
    public Step lombokMapStructStep(
            JobRepository jobRepository,
            PlatformTransactionManager transactionManager,
            PersonDao personDao,
            MapStructPersonMapper mapper,
            BenchmarkProperties properties) {
        return new StepBuilder("lombokMapStructStep", jobRepository)
                .tasklet(MapperBenchmarkTasklet.lombokMapStruct(personDao, mapper, properties), transactionManager)
                .build();
    }
}
