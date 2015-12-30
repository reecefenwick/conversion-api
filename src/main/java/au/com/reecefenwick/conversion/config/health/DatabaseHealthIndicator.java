package au.com.reecefenwick.conversion.config.health;

import org.springframework.boot.actuate.health.DataSourceHealthIndicator;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

/**
 * SpringBoot Actuator HealthIndicator check for the Database.
 */

@Component
public class DatabaseHealthIndicator implements HealthIndicator {

    @Override
    public Health health() {
        DataSourceHealthIndicator ds = new DataSourceHealthIndicator();
        String status = ds.health().getStatus().toString();

        if (status.contains("UP")) {
            return Health.up().build();
        }

        return Health.down().build();
    }
}
