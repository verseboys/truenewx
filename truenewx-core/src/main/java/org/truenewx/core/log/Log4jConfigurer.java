package org.truenewx.core.log;

import java.io.IOException;
import java.net.URL;

import org.apache.log4j.PropertyConfigurator;
import org.apache.log4j.xml.DOMConfigurator;
import org.springframework.core.io.Resource;

/**
 * Log4j配置器
 *
 * @author jianglei
 * @since JDK 1.8
 */
public class Log4jConfigurer {

    public static void configure(final URL url) {
        final String filename = url.getFile();
        if (filename.endsWith(".xml")) {
            DOMConfigurator.configure(url);
        } else if (filename.endsWith(".properties")) {
            PropertyConfigurator.configure(url);
        }
    }

    public Log4jConfigurer(final Resource location) {
        try {
            configure(location.getURL());
        } catch (final IOException e) {
            e.printStackTrace();
        }
    }
}
