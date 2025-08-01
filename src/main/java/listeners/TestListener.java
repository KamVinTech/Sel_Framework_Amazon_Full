package listeners;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;
import utils.ExtentReportManager;
import utils.ScreenshotUtils;
import utils.SmartElementFinder;
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
        Throwable throwable = result.getThrowable();
        
        // Enhanced error analysis
        analyzeFailure(throwable);

        try {
            Object currentClass = result.getInstance();
            WebDriver driver = (WebDriver) currentClass.getClass().getSuperclass()
                .getDeclaredField("driver").get(currentClass);

            if (driver != null) {
                // Capture full page screenshot
                String screenshotPath = ScreenshotUtils.captureScreenshot(driver, result.getName());
                
                // Get additional failure context
                String failureContext = getFailureContext(driver, throwable);
                
                // Update extent report with detailed failure information
                ExtentReportManager.getTest()
                    .fail("Test Failed: " + failureContext)
                    .addScreenCaptureFromPath(screenshotPath)
                    .fail(throwable);
            }
        } catch (Exception e) {
            logger.error("Error capturing failure details: " + e.getMessage(), e);
        }
    }

    private void analyzeFailure(Throwable throwable) {
        if (throwable instanceof SmartElementFinder.ElementNotFoundException) {
            logger.error("Element location failure. Self-healing was attempted but unsuccessful.");
        } else if (throwable instanceof org.openqa.selenium.TimeoutException) {
            logger.error("Timeout occurred. This might indicate performance issues or invalid waits.");
        } else if (throwable instanceof org.openqa.selenium.ElementClickInterceptedException) {
            logger.error("Click was intercepted. This might indicate UI overlay issues or timing problems.");
        } else if (throwable instanceof org.openqa.selenium.StaleElementReferenceException) {
            logger.error("Stale element encountered. This might indicate dynamic content changes.");
        }
    }

    private String getFailureContext(WebDriver driver, Throwable throwable) {
        StringBuilder context = new StringBuilder();
        try {
            // Add current URL
            context.append("URL: ").append(driver.getCurrentUrl()).append("\n");
            
            // Add specific failure information
            if (throwable instanceof org.openqa.selenium.WebDriverException) {
                org.openqa.selenium.WebDriverException webDriverException = (org.openqa.selenium.WebDriverException) throwable;
                context.append("Error Type: WebDriver Exception\n");
                context.append("Error Message: ").append(webDriverException.getMessage()).append("\n");
                
                // Add any JavaScript console errors if available
                if (driver instanceof JavascriptExecutor) {
                    try {
                        Object jsErrors = ((JavascriptExecutor) driver).executeScript(
                            "return window.jsErrors || []");
                        if (jsErrors != null) {
                            context.append("JavaScript Errors: ").append(jsErrors).append("\n");
                        }
                    } catch (Exception e) {
                        logger.debug("Could not retrieve JavaScript errors", e);
                    }
                }
            }
        } catch (Exception e) {
            logger.warn("Error getting failure context", e);
        }
        return context.toString();
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
