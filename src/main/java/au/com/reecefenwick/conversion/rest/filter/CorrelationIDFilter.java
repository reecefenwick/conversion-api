package au.com.reecefenwick.conversion.rest.filter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.UUID;

/**
 * This filter adds a UUID as the X-CorrelationID header
 * This is useful for API consumers and maintainers to track requests and debug
 * This filter checks if the client has set the X-CorrelationID header
 * If not it will create one and set it on the response header
 */
@Component
@Order(1)
public class CorrelationIDFilter implements Filter{

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

        String correlationID = httpRequest.getHeader("X-CorrelationID");

        if (correlationID == null) {
            correlationID = UUID.randomUUID().toString();
        }

        ((HttpServletResponse) response).addHeader("X-CorrelationID", correlationID);
        chain.doFilter(request, response);
    }

}
