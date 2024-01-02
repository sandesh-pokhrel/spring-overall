package com.sandesh.overall;

import com.sandesh.overall.service.CaptureOutputService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.system.CapturedOutput;
import org.springframework.boot.test.system.OutputCaptureExtension;
import org.springframework.boot.test.util.TestPropertyValues;

import static org.junit.jupiter.api.Assertions.*;
import static org.assertj.core.api.Assertions.*;

@ExtendWith(OutputCaptureExtension.class)
public class CaptureOutputServiceTests {

    @Test
    void checkOutput_doesNotContains(CapturedOutput out) {
        CaptureOutputService.checkAuth();
        assertThat(out).doesNotContain("AUTH: ");
    }

    @Test
    void checkOutput_doesContains(CapturedOutput out) {
        TestPropertyValues.of("auth.required:true").applyToSystemProperties(CaptureOutputService::checkAuth);
        assertThat(out).contains("AUTH: ");
    }

    @Test
    void checkOutput_doesNotContainsAndPresent(CapturedOutput out) {
        TestPropertyValues.of("auth.required:false").applyToSystemProperties(CaptureOutputService::checkAuth);
        assertThat(out).doesNotContain("AUTH: ");
    }
}
