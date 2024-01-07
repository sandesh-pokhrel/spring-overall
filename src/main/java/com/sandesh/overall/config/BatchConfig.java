package com.sandesh.overall.config;

import com.sandesh.overall.model.VideoGame;
import jakarta.persistence.EntityManagerFactory;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.database.JpaItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.transaction.PlatformTransactionManager;

import java.io.IOException;
import java.util.Date;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Function;

@Slf4j
@Configuration
@AllArgsConstructor
public class BatchConfig {

    private static final String TEST_JOB_NAME = "test_job";

    private final JobRepository jobRepository;
    private final PlatformTransactionManager batchTransactionManager;
    private final JobLauncher jobLauncher;

    // @Bean // Uncomment this line to run job at the beginning
    public ApplicationRunner runner(Job firstJob) {
        return args -> {
            JobParameters jobParameters = new JobParametersBuilder()
                    .addString("uuid", UUID.randomUUID().toString())
                    .addDate("today", new Date())
                    .toJobParameters();
            jobLauncher.run(firstJob, jobParameters);
        };
    }

    @Bean
    public Job firstJob(Step fileProcessingStep) throws IOException {
        return new JobBuilder(TEST_JOB_NAME, jobRepository)
                // .start(infoStep())
                .start(fileProcessingStep)
                .build();
    }

    @Bean
    public Step fileProcessingStep(FlatFileItemReader<VideoGame> flatFileItemReader, EntityManagerFactory factory) throws IOException {
        //  File vgSalesFile = ResourceUtils.getFile("classpath:data/vgsales.csv");
        // String vgSalesContent = FileUtils.readFileToString(vgSalesFile, StandardCharsets.UTF_8);
        JpaItemWriter<VideoGame> jpaItemWriter = new JpaItemWriter<>();
        jpaItemWriter.setEntityManagerFactory(factory);
        return new StepBuilder("processing_step", jobRepository)
                .<VideoGame, VideoGame>chunk(1000, batchTransactionManager)
                // .reader(new ListItemReader<>(Arrays.asList(vgSalesContent.split(","))))
                .reader(flatFileItemReader)
                // .writer(chunk -> log.info(chunk.toString()))
                .writer(jpaItemWriter)
                .taskExecutor(new SimpleAsyncTaskExecutor())
                .build();
    }

    @Bean
    public Step infoStep() {
        return new StepBuilder("first_step", jobRepository)
                .tasklet((stepContribution, chunkContext) -> {
                    log.info("This is first task executing");
                    return RepeatStatus.FINISHED;
                }, batchTransactionManager).build();
    }

    @Bean
    public FlatFileItemReader<VideoGame> flatFileItemReader(@Value("classpath:data/vgsales.csv") Resource resource) {
        Function<String, Integer> numberInvalidReturnRandom = (val) ->
                NumberUtils.isCreatable(val) ? Integer.parseInt(val) : ThreadLocalRandom.current().nextInt(1_00_000, 2_00_000);
        return new FlatFileItemReaderBuilder<VideoGame>()
                .name("video_game_flat_file_reader")
                .addComment("Item reader for video game sales")
                .resource(resource)
                .delimited()
                .delimiter(",")
                .names("rank", "name", "platform", "year", "genre", "publisher", "na_sales", "eu_sales", "jp_sales", "other_sales", "global_sales")
                .linesToSkip(1)
                .fieldSetMapper(fieldSet -> VideoGame.builder()
                        .id(fieldSet.readLong("rank"))
                        .name(fieldSet.readString("name"))
                        .platform(fieldSet.readString("platform"))
                        .year(numberInvalidReturnRandom.apply(fieldSet.readString("year")))
                        .genre(fieldSet.readString("genre"))
                        .publisher(fieldSet.readString("publisher"))
                        .naSales(fieldSet.readFloat("na_sales"))
                        .euSales(fieldSet.readFloat("eu_sales"))
                        .jpSales(fieldSet.readFloat("jp_sales"))
                        .otherSales(fieldSet.readFloat("other_sales"))
                        .globalSales(fieldSet.readFloat("global_sales"))
                        .build())
                .build();
    }
}
