// $codepro.audit.disable unnecessaryImport
import static ch.qos.logback.classic.Level.DEBUG
import static ch.qos.logback.classic.Level.ERROR
import static ch.qos.logback.classic.Level.INFO
import static ch.qos.logback.classic.Level.OFF
import static ch.qos.logback.classic.Level.TRACE
import static ch.qos.logback.classic.Level.WARN
import ch.qos.logback.classic.encoder.PatternLayoutEncoder
import ch.qos.logback.core.ConsoleAppender
import ch.qos.logback.core.FileAppender

//statusListener(OnConsoleStatusListener)

logger("pl.edu.mimuw", TRACE)

logger("org.springframework", INFO)

appender("STDOUT", ConsoleAppender) {
	encoder(PatternLayoutEncoder) { pattern = "%date %contextName:[%thread] %-5level %logger{35} - %msg %n" }
}

def bySecond = timestamp("yyyy-MM-dd'T'HH-mm-ss")
appender("FILE", FileAppender) {
	file = "/tmp/logFile${bySecond}.log"
	encoder(PatternLayoutEncoder) { pattern = "%date %contextName:[%thread] %-5level %logger{35} - %msg %n" }
}

root(TRACE, ["STDOUT", "FILE"])