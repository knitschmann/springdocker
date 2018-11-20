package net.beyondrealism.publictransport;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
@EnableConfigurationProperties
public class PublicTransportApplication {

    private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(PublicTransportApplication.class);

    public static void main(String[] args) {
        ConfigurableApplicationContext run = SpringApplication.run(PublicTransportApplication.class, args);
        String[] profiles = run.getEnvironment().getActiveProfiles();

        if (profiles.length > 0) {
            System.out.println(profiles[0]);
            LOG.info("Using spring profile: " + profiles[0]);
        }
    }

}

