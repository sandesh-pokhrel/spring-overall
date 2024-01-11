package com.sandesh.overall.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpFilter;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;

@Configuration
@ServletComponentScan
public class LegacyServletConfig {

    // Using registration approach
    // If not wanted then annotate configuration class with @ServletComponentScan
    @Bean
    public ServletRegistrationBean<HttpServlet> servletServletRegistrationBean() {
        var legacyServlet = new LegacyServlet();
        var servletRegBean = new ServletRegistrationBean<HttpServlet>();
        servletRegBean.setServlet(legacyServlet);
        servletRegBean.addUrlMappings("/legacy");
        return servletRegBean;
    }
}

@WebFilter(urlPatterns = "/legacy")
class LegacyFilterScanned extends HttpFilter {
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        System.out.println("Before processing");
        super.doFilter(request, response, chain);
        System.out.println("After processing");
    }
}

@WebServlet(urlPatterns = "/legacy/scanned")
class LegacyServletComponentScanned extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.getWriter().write("Message from legacy servlet scanned");
    }
}

class LegacyServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.getWriter().write("Message from legacy servlet");
    }
}
