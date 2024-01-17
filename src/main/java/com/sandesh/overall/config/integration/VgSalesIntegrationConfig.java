package com.sandesh.overall.config.integration;

import com.sandesh.overall.config.batch.BatchConfig;
import com.sandesh.overall.model.VideoGame;
import jakarta.persistence.EntityManagerFactory;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.core.GenericHandler;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.file.dsl.Files;
import org.springframework.integration.file.support.FileExistsMode;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.transaction.PlatformTransactionManager;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.UUID;

@RequiredArgsConstructor
public class VgSalesIntegrationConfig {

    private static final String LOG_CHANNEL = "logChannel";
    private static final String JOB_NAME = "fileProcessingJob";

    private final JobLauncher jobLauncher;
    private final JobRepository jobRepository;
    private final PlatformTransactionManager batchTransactionManager;
    private final EntityManagerFactory entityManagerFactory;

    public Job fileProcessingJob(Resource resource) throws IOException {
        final BatchConfig batchConfig = new BatchConfig(jobRepository, batchTransactionManager, jobLauncher);
        FlatFileItemReader<VideoGame> flatFileItemReader = batchConfig.flatFileItemReader(resource);
        return new JobBuilder(JOB_NAME, jobRepository)
                .start(batchConfig.fileProcessingStep(flatFileItemReader, entityManagerFactory))
                .build();
    }

    @Bean
    public IntegrationFlow fileInputFlow(@Value("${inbound.file.location}") String inboundPath,
                                         @Value("${outbound.file.location}") String outboundPath) {
        var inbound = Files.inboundAdapter(new File(inboundPath))
                .autoCreateDirectory(true)
                .preventDuplicates(true)
                .filterFunction(File::isFile)
                .ignoreHidden(true)
                .recursive(true)
                .getObject();

        var outbound = Files.outboundAdapter(new File(outboundPath))
                .fileExistsMode(FileExistsMode.REPLACE)
                .autoCreateDirectory(true)
                .deleteSourceFiles(true)
                .preserveTimestamp(true)
                .getObject();

        return IntegrationFlow
                .from(inbound)
                .wireTap(LOG_CHANNEL)
                .handle(new GenericHandler<File>() {
                    @SneakyThrows
                    @Override
                    public Object handle(File payload, MessageHeaders headers) {
                        Resource resource = new FileSystemResource(payload);
                        jobLauncher.run(fileProcessingJob(resource), new JobParametersBuilder()
                                .addString("uuid", UUID.randomUUID().toString())
                                .addDate("today", new Date())
                                .toJobParameters());

                        return payload;
                    }
                })
                .handle(outbound)
                .get();
    }

    @ServiceActivator(inputChannel = LOG_CHANNEL)
    public void log(@Payload File file) {
        System.out.println("Received file -----------");
        System.out.println(file.getAbsolutePath());
    }
}
