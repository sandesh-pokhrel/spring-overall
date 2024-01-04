package com.sandesh.overall.actuator;

import org.springframework.boot.actuate.endpoint.annotation.*;
import org.springframework.stereotype.Component;

import javax.annotation.Nullable;
import java.util.List;

@Component
@Endpoint(id = "report-details")
// @EndpointWebExtension(endpoint = HealthEndpoint.class) // Can be extended existing health endpoint to enhance functionality further
public class ReportEndpoint {

    @ReadOperation
    public List<Report.ReportDTO> testReports() {
        return Report.getTestReportIds();
    }

    /**
     * here if @selector annotation is not present then query param of id is created eg: http://localhost:8080/actuator/report-details?id=105
     * currently path var is created eg: http://localhost:8080/actuator/report-details/105
     * @param id selector
     * @return report
     */
    @ReadOperation
    public Report testReport(@Selector int id) {
        return Report.getReportById(id);
    }

    @DeleteOperation
    public void testReportDelete(@Selector int id) {
        Report.removeReportById(id);
    }

    /**
     * eg request: curl -X POST -d '{"name":"New Employee Report"}' -H "Content-Type: application/json" http://localhost:8080/actuator/report-details/101
     * port-details/101
     * @param id selector
     * @param name new name to set
     */
    @WriteOperation
    public void testReportWrite(@Selector int id, @Nullable String name) {
        Report.changeReportNameBy(id, name);
    }
}
