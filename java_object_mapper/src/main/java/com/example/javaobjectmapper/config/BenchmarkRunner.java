package com.example.javaobjectmapper.config;

import com.example.javaobjectmapper.service.SourceDataService;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.job.Job;
import org.springframework.batch.core.job.JobExecution;
import org.springframework.batch.core.job.parameters.JobParameters;
import org.springframework.batch.core.job.parameters.JobParametersBuilder;
import org.springframework.batch.core.launch.JobOperator;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(prefix = "benchmark", name = "auto-run", havingValue = "true", matchIfMissing = true)
public class BenchmarkRunner implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(BenchmarkRunner.class);

    private final JobOperator jobOperator;

    private final Job modelMapperJob;

    private final Job mapStructJob;

    private final Job lombokModelMapperJob;

    private final Job lombokMapStructJob;

    private final SourceDataService sourceDataService;

    private final BenchmarkProperties properties;

    public BenchmarkRunner(
            JobOperator jobOperator,
            Job modelMapperJob,
            Job mapStructJob,
            Job lombokModelMapperJob,
            Job lombokMapStructJob,
            SourceDataService sourceDataService,
            BenchmarkProperties properties) {
        this.jobOperator = jobOperator;
        this.modelMapperJob = modelMapperJob;
        this.mapStructJob = mapStructJob;
        this.lombokModelMapperJob = lombokModelMapperJob;
        this.lombokMapStructJob = lombokMapStructJob;
        this.sourceDataService = sourceDataService;
        this.properties = properties;
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        int recordCount = resolveRecordCount(args);
        log.info("Preparing {} source rows with Doma3", recordCount);
        sourceDataService.prepare(recordCount);

        JobParameters parameters = new JobParametersBuilder()
                .addLong("recordCount", (long) recordCount)
                .addLong("run.id", System.currentTimeMillis())
                .toJobParameters();

        runAndAssertCompleted(modelMapperJob, parameters);
        runAndAssertCompleted(mapStructJob, parameters);
        runAndAssertCompleted(lombokModelMapperJob, parameters);
        runAndAssertCompleted(lombokMapStructJob, parameters);
    }

    private void runAndAssertCompleted(Job job, JobParameters parameters) throws Exception {
        JobExecution execution = jobOperator.start(job, parameters);
        if (execution.getStatus() != BatchStatus.COMPLETED) {
            throw new IllegalStateException(
                    "Job " + job.getName() + " finished with status " + execution.getStatus());
        }
    }

    private int resolveRecordCount(ApplicationArguments args) {
        Optional<String> optionValue = Optional.ofNullable(args.getOptionValues("recordCount"))
                .flatMap(values -> values.stream().findFirst());
        Optional<String> nonOptionValue = args.getNonOptionArgs().stream()
                .filter(argument -> argument.startsWith("recordCount="))
                .map(argument -> argument.substring("recordCount=".length()))
                .findFirst();
        return optionValue.or(() -> nonOptionValue)
                .map(Integer::parseInt)
                .filter(value -> value > 0)
                .orElse(properties.getDefaultRecordCount());
    }
}
