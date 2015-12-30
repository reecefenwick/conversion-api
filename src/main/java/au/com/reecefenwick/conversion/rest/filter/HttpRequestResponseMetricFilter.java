package au.com.reecefenwick.conversion.rest.filter;


import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * This filter is used to log request/response level information
 * Such as timings, correlation ID's
 */
@Component
public class HttpRequestResponseMetricFilter implements Filter {

    private static final Logger log = LoggerFactory.getLogger(HttpRequestResponseMetricFilter.class);

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        // Nothing to initialize
    }

    @Override
    public void destroy() {
        // Nothing to destroy
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;

        // Capture information about the request
        Long startTime = new DateTime().getMillis();
        String requestURI = httpRequest.getRequestURI();
        String requestMethod = httpRequest.getMethod();
        Integer requestContentLength = httpRequest.getContentLength();
        String remoteIP = request.getRemoteAddr();
        String remoteHost = request.getRemoteHost();

        // Pass request down the chain
        chain.doFilter(request, response);

        HttpServletResponse httpResponse = (HttpServletResponse) response;
        Long finishTime = new DateTime().getMillis();
        Long elapsedTime = finishTime - startTime;
        String correlationID = httpResponse.getHeader("X-CorrelationID");
        Integer responseCode = httpResponse.getStatus();
        Integer responseSize = httpResponse.getBufferSize();

        logRequestMetrics(requestURI, requestMethod, requestContentLength,
                finishTime, elapsedTime, correlationID, responseCode, remoteIP, remoteHost, responseSize);
    }

    public void logRequestMetrics(String requestURI,
                                  String requestMethod,
                                  Integer requestContentLength,
                                  Long finishTime,
                                  Long elapsedTime,
                                  String correlationID,
                                  Integer responseCode,
                                  String remoteIP,
                                  String remoteHost,
                                  Integer responseSize) {
        log.info("Request made to server uri={} method={} reqContentLength={} finishTime={} " +
                        "elapsedTime={} correlationID={} responseCode={} remoteIP={} remoteHost={}",
                requestURI, requestMethod, requestContentLength, finishTime, elapsedTime,
                correlationID, responseCode, remoteIP, remoteHost, responseSize);
    }
}
