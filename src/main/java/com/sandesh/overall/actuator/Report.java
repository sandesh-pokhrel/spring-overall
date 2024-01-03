package com.sandesh.overall.actuator;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
@Builder
public class Report {

    private static List<Report> reports;

    static {
        reports = List.of(
                Report.builder().id(101).name("Employee Report").status("Pending").samples(1_000L).build(),
                Report.builder().id(102).name("Customer Report").status("Completed").samples(2_000L).build(),
                Report.builder().id(103).name("Address Report").status("Error").samples(10_000L).build(),
                Report.builder().id(104).name("General Report").status("Completed").samples(16_000L).build(),
                Report.builder().id(105).name("Profit Report").status("Progress").samples(7_000L).build()
        );
    }

    private int id;
    private String name;
    private String status;
    private Long samples;

    @Getter
    @Setter
    @AllArgsConstructor
    public static class ReportDTO implements Serializable {
        private int id;
    }

    public static List<ReportDTO> getTestReportIds() {
        return reports.stream().map(report -> new ReportDTO(report.getId())).collect(Collectors.toList());
    }

    public static Report getReportById(int id) {
        return reports.stream().filter(report -> report.getId() == id).findFirst().orElse(null);
    }

    public static void removeReportById(int id) {
        reports = reports.stream().filter(report -> report.getId() != id).collect(Collectors.toList());
    }

    public static void changeReportNameBy(int id, String name) {
        reports.stream().filter(report -> report.getId() == id).forEach(report -> report.setName(name));
    }
}
