package com.sandesh.overall.util;

import lombok.SneakyThrows;
import org.springframework.hateoas.Link;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

public class GenericUtil {

    @SneakyThrows
    public static void sleep(long millis) {
        Thread.sleep(millis);
    }

    public static Link buildLinkFromContextPath(String relation, String path, Object... uriVars) {
        URI uri = UriComponentsBuilder
                .fromUri(ServletUriComponentsBuilder.fromCurrentContextPath().build().toUri())
                .path(path)
                .buildAndExpand(uriVars).toUri();
        return Link.of(uri.toString(), relation);
    }
}
