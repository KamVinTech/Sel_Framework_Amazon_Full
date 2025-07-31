package utils;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import com.aventstack.extentreports.reporter.configuration.Theme;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.nio.file.Paths;

public class ExtentReportManager {
    private static final Logger logger = LogManager.getLogger(ExtentReportManager.class);
    private static ExtentReports extent;
    private static final String REPORT_PATH = ConfigReader.getValue("extent.report.path", "reports/ExtentReport.html");
    private static final ThreadLocal<ExtentTest> extentTest = new ThreadLocal<>();

    public static void initReports() {
        try {
            if (extent == null) {
                createReportDirectory();
                extent = new ExtentReports();
                ExtentSparkReporter spark = new ExtentSparkReporter(REPORT_PATH);
                configureReporter(spark);
                extent.attachReporter(spark);
                addSystemInfo();
                logger.info("Extent Reports initialized successfully");
            }
        } catch (Exception e) {
            logger.error("Failed to initialize Extent Reports: " + e.getMessage(), e);
        }
    }

    private static void createReportDirectory() {
        try {
            java.nio.file.Path path = Paths.get(REPORT_PATH).getParent();
            if (path != null && !java.nio.file.Files.exists(path)) {
                java.nio.file.Files.createDirectories(path);
                logger.info("Created report directory: " + path);
            }
        } catch (Exception e) {
            logger.error("Failed to create report directory: " + e.getMessage(), e);
        }
    }

    private static void configureReporter(ExtentSparkReporter spark) {
        spark.config().setTheme(Theme.STANDARD);
        spark.config().setDocumentTitle("Automation Test Report");
        spark.config().setReportName("Selenium Test Results");
        spark.config().setTimeStampFormat("MMM dd, yyyy HH:mm:ss");
    }

    private static void addSystemInfo() {
        extent.setSystemInfo("Browser", ConfigReader.getValue("browser", "chrome"));
        extent.setSystemInfo("Environment", ConfigReader.getValue("environment", "QA"));
        extent.setSystemInfo("URL", ConfigReader.getValue("app.url"));
        extent.setSystemInfo("OS", System.getProperty("os.name"));
        extent.setSystemInfo("Java Version", System.getProperty("java.version"));
    }

    public static ExtentTest createTest(String testName, String description) {
        ExtentTest test = extent.createTest(testName, description);
        extentTest.set(test);
        return test;
    }

    public static ExtentTest getTest() {
        return extentTest.get();
    }

    public static void flushReports() {
        if (extent != null) {
            extent.flush();
            logger.info("Extent Reports flushed successfully");
        }
    }
}
