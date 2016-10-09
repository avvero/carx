//
// Built on Wed Oct 07 09:44:43 UTC 2015 by logback1-translator
// For more information on configuration files in Groovy
// please see http://logback.qos.ch/manual/groovy.html

// For assistance related to this tool or configuration files
// in general, please contact the logback1 user mailing list at
//    http://qos.ch/mailman/listinfo/logback-user

// For professional support please see
//   http://www.qos.ch/shop/products/professionalSupport
import ch.qos.logback.classic.encoder.PatternLayoutEncoder
import ch.qos.logback.core.rolling.RollingFileAppender
import ch.qos.logback.core.rolling.TimeBasedRollingPolicy
import ch.qos.logback.core.ConsoleAppender

import static ch.qos.logback.classic.Level.*

appender("file", RollingFileAppender) {
    file = "/var/log/service/carx/service.log"
    encoder(PatternLayoutEncoder) {
        charset = java.nio.charset.StandardCharsets.UTF_8
        pattern = "%d{yyyy-MM-dd HH:mm:ss.SSS} %5p %t %c{0}:%M:%L - %m%n"
    }
    rollingPolicy(TimeBasedRollingPolicy) {
        fileNamePattern = "/var/log/service/carx/service.log.%d{yyyy-MM-dd}"
    }
}

logger("org", ERROR, ["file"], false)
logger("org.springframework", ERROR, ["file"], false)
logger("org.hibernate", INFO, ["file"], false)
logger("org.hibernate.SQL", DEBUG, ["file"], false)
logger("org.apache.camel", DEBUG, ["file"], false)
logger("com.avvero", TRACE, ["file"], false)
root(ERROR, ["file"])