package com.sandesh.overall.config.integration;

import com.sandesh.overall.model.Address;
import com.sandesh.overall.model.Employee;
import com.sandesh.overall.service.EmployeeService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.integration.core.GenericHandler;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.MessageChannels;
import org.springframework.integration.kafka.dsl.Kafka;
import org.springframework.integration.splitter.AbstractMessageSplitter;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.support.MessageBuilder;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class EmployeeIntegrationConfig {

    private final EmployeeService employeeService;
    private final KafkaTemplate<Long, Employee> employeeKafkaTemplate;
    private static final String EMPLOYEE_CHANNEL = "employeeChannel";
    private static final String ADDRESS_CHANNEL = "addressChannel";

    public EmployeeIntegrationConfig(EmployeeService employeeService,
                                     @Qualifier("employeeKafkaTemplate") KafkaTemplate<Long, Employee> employeeKafkaTemplate) {
        this.employeeService = employeeService;
        this.employeeKafkaTemplate = employeeKafkaTemplate;
    }

    @Bean
    public ApplicationRunner employeeSender(MessageChannel employeeChannel) {
        return args -> {
            List<Employee> employees = employeeService.getAll();
            employeeChannel.send(MessageBuilder.withPayload(employees).build());
        };
    }

    @Bean(name = EMPLOYEE_CHANNEL)
    public MessageChannel employeeChannel() {
        return MessageChannels.direct().getObject();
    }

    @Bean(name = ADDRESS_CHANNEL)
    public MessageChannel addressChannel() {
        return MessageChannels.direct().getObject();
    }

    @Bean
    public IntegrationFlow employeeFlow() {
        return IntegrationFlow
                .from(EMPLOYEE_CHANNEL)
                .handle((GenericHandler<List<Employee>>) (payload, headers) -> {
                    System.out.println("Employee message ::: ");
                    payload.forEach(e -> System.out.println(e.getName()));
                    return payload;
                })
                // .handle(Kafka.outboundChannelAdapter(employeeKafkaTemplate)).get();
                .split(new AbstractMessageSplitter() {
                    @Override
                    protected Object splitMessage(Message<?> message) {
                        List<Employee> employees = (List<Employee>) message.getPayload();
                        return employees.stream().map(Employee::getAddresses)
                                .filter(Objects::nonNull)
                                .collect(Collectors.toList());
                    }
                })
                .channel(ADDRESS_CHANNEL).get();
    }

    @Bean
    public IntegrationFlow addressFlow() {
        return IntegrationFlow
                .from(ADDRESS_CHANNEL)
                .handle((GenericHandler<List<Address>>) (payload, headers) -> {
                    System.out.println("Address message ::: ");
                    payload.forEach(a -> System.out.println(a.getCity()));
                    return null;
                })
                .get();
    }
}
