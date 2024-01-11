package com.sandesh.overall.config.batch;

import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.batch.core.*;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

import java.util.Date;
import java.util.UUID;

@Configuration
@RequiredArgsConstructor
public class OverallBatchConfig {

    private static final String FAILED_STATUS = "FAILED_STATUS";
    private static final String PASSED_STATUS = "PASSED_STATUS";

    private final JobRepository jobRepository;
    private final PlatformTransactionManager batchTransactionManager;
    private final JobLauncher jobLauncher;

    // @Bean // Uncomment this line to run job at the beginning OR enable in property file
    public ApplicationRunner runner(Job testJob) {
        return args -> {
            JobParameters jobParameters = new JobParametersBuilder()
                    .addString("uuid", UUID.randomUUID().toString())
                    .addDate("today", new Date())
                    .toJobParameters();
            jobLauncher.run(testJob, jobParameters);
        };
    }

    @Bean
    public Job testJob(Step firstStep) {
        return new JobBuilder("check_job", jobRepository)
                .start(firstStep).on(FAILED_STATUS).to(errorStep())
                .from(firstStep).on("*").to(secondStep())
                .next(thirdStep())
                .build().build();
    }

    @Bean
    public Step firstStep() {
        return new StepBuilder("first_step", jobRepository)
                .tasklet((contribution, chunkContext) -> {
                    System.out.println("First step echoing message");
                    return RepeatStatus.FINISHED;
                }, batchTransactionManager)
                .listener(new StepExecutionListener() {
                    @Override
                    public ExitStatus afterStep(@NotNull StepExecution stepExecution) {
                        System.out.println("First Step listener message");
                        return new ExitStatus(FAILED_STATUS);
                    }
                })
                .build();
    }

    @Bean
    public Step secondStep() {
        return new StepBuilder("second_step", jobRepository)
                .tasklet((contribution, chunkContext) -> {
                    System.out.println("Second step echoing message");
                    return RepeatStatus.FINISHED;
                }, batchTransactionManager)
                .build();
    }

    @Bean
    public Step thirdStep() {
        return new StepBuilder("third_step", jobRepository)
                .tasklet((contribution, chunkContext) -> {
                    System.out.println("Third step echoing message");
                    return RepeatStatus.FINISHED;
                }, batchTransactionManager)
                .build();
    }

    @Bean
    public Step errorStep() {
        return new StepBuilder("error_step", jobRepository)
                .tasklet((contribution, chunkContext) -> {
                    System.out.println("Error step echoing message");
                    return RepeatStatus.FINISHED;
                }, batchTransactionManager)
                .build();
    }
}
