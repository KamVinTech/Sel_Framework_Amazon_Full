package listeners;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebDriver;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;
import utils.ExtentReportManager;
import utils.ScreenshotUtils;

import java.util.Arrays;

public class TestListener implements ITestListener {
    private static final Logger logger = LogManager.getLogger(TestListener.class);

    @Override
    public void onStart(ITestContext context) {
        logger.info("Test Suite started: " + context.getName());
        ExtentReportManager.initReports();
    }

    @Override
    public void onFinish(ITestContext context) {
        logger.info("Test Suite finished: " + context.getName());
        ExtentReportManager.flushReports();
    }

    @Override
    public void onTestStart(ITestResult result) {
        logger.info("Test started: " + result.getName());
        logger.info("Test parameters: " + Arrays.toString(result.getParameters()));
    }

    @Override
    public void onTestSuccess(ITestResult result) {
        logger.info("Test passed: " + result.getName());
    }

    @Override
    public void onTestFailure(ITestResult result) {
        logger.error("Test failed: " + result.getName());
        logger.error("Failure reason: " + result.getThrowable());

        try {
            Object currentClass = result.getInstance();
            WebDriver driver = (WebDriver) currentClass.getClass().getSuperclass()
                .getDeclaredField("driver").get(currentClass);

            if (driver != null) {
                String screenshotPath = ScreenshotUtils.captureScreenshot(driver, result.getName());
                ExtentReportManager.getTest()
                    .fail("Test Failed")
                    .addScreenCaptureFromPath(screenshotPath)
                    .fail(result.getThrowable());
            }
        } catch (Exception e) {
            logger.error("Error capturing failure screenshot: " + e.getMessage(), e);
        }
    }

    @Override
    public void onTestSkipped(ITestResult result) {
        logger.info("Test skipped: " + result.getName());
        if (result.getThrowable() != null) {
            logger.info("Skip reason: " + result.getThrowable());
        }
    }

    @Override
    public void onTestFailedButWithinSuccessPercentage(ITestResult result) {
        logger.info("Test failed within success percentage: " + result.getName());
    }
}
