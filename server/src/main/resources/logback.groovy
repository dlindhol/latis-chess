import ch.qos.logback.classic.PatternLayout
import com.splunk.logging.HttpEventCollectorLogbackAppender
import latis.util.BuildInfo

def src = "${BuildInfo.name()}-${BuildInfo.version()}"
def hostname = System.getenv("DOCKER_HOST") ?: "${hostname}"
def splunkUrl = System.getenv("LOG_SPLUNK_URL")
def splunkToken = System.getenv("LOG_SPLUNK_TOKEN")

def appenders = []

if (splunkUrl && splunkToken) {
  appender("SPLUNK", HttpEventCollectorLogbackAppender) {
    url = splunkUrl
    token = splunkToken
    source = src
    host = hostname
    disableCertificateValidation = true
    layout(PatternLayout) {
      pattern = "[%d{yyyy-MM-dd'T'HH:mm:ss.SSS, GMT} %-5level %logger{36} (%thread\\)] %msg%n"
    }
  }

  appenders << "SPLUNK"
}

appender("CONSOLE", ConsoleAppender) {
  encoder(PatternLayoutEncoder) {
    pattern = "[%d{yyyy-MM-dd'T'HH:mm:ss.SSS, GMT} %-5level %logger{36} (%thread\\) (%X\\)] %msg%n"
  }
}

appenders << "CONSOLE"

root(INFO, appenders)
