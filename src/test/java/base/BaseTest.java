package base;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebDriver;
import org.testng.ITestContext;
import org.testng.ITestResult;
import org.testng.annotations.*;
import pages.HomePage;
import pages.SearchResultsPage;
import pages.ProductDetailsPage;
import utils.ConfigReader;
import utils.WebDriverFactory;
import utils.ScreenshotUtils;
import utils.ExtentReportManager;
import com.aventstack.extentreports.ExtentTest;

public class BaseTest {
    private static final Logger logger = LogManager.getLogger(BaseTest.class);
    protected WebDriver driver;
    protected HomePage homePage;
    protected SearchResultsPage searchResultsPage;
    protected ProductDetailsPage productDetailsPage;
    protected ExtentTest extentTest;

    @BeforeSuite(alwaysRun = true)
    public void beforeSuite(ITestContext context) {
        logger.info("Starting test suite execution");
        ExtentReportManager.initReports();
    }

    @BeforeClass(alwaysRun = true)
    public void beforeClass() {
        logger.info("Starting test class: " + getClass().getSimpleName());
    }

    @BeforeMethod(alwaysRun = true)
    public void setUp(ITestResult result) {
        try {
            extentTest = ExtentReportManager.createTest(result.getMethod().getMethodName(), 
                                                      result.getMethod().getDescription());
            logger.info("Setting up test: " + result.getMethod().getMethodName());
            
            // Initialize WebDriver using factory
            driver = WebDriverFactory.getDriver();
            
            // Initialize page objects
            homePage = new HomePage(driver);
            
            // Navigate to application URL
            String baseUrl = ConfigReader.getValue("app.url", "https://www.amazon.in");
            driver.get(baseUrl);
            logger.info("Navigated to: " + baseUrl);
            
            extentTest.info("Test setup completed successfully");
        } catch (Exception e) {
            logger.error("Error in test setup: " + e.getMessage(), e);
            if (extentTest != null) {
                extentTest.fail("Test setup failed: " + e.getMessage());
            }
            throw new RuntimeException("Test setup failed", e);
        }
    }

    @AfterMethod(alwaysRun = true)
    public void tearDown(ITestResult result) {
        try {
            if (result.getStatus() == ITestResult.FAILURE) {
                logger.error("Test case failed: " + result.getName());
                String screenshotPath = ScreenshotUtils.captureScreenshot(driver, result.getName());
                extentTest.fail("Test Failed")
                         .addScreenCaptureFromPath(screenshotPath);
            } else if (result.getStatus() == ITestResult.SUCCESS) {
                logger.info("Test case passed: " + result.getName());
                extentTest.pass("Test Passed");
            } else {
                logger.info("Test case skipped: " + result.getName());
                extentTest.skip("Test Skipped");
            }
            
            if (driver != null) {
                WebDriverFactory.quitDriver();
                logger.info("WebDriver closed successfully");
            }
        } catch (Exception e) {
            logger.error("Error in test cleanup: " + e.getMessage(), e);
        }
    }

    @AfterClass(alwaysRun = true)
    public void afterClass() {
        logger.info("Completed test class: " + getClass().getSimpleName());
    }

    @AfterSuite(alwaysRun = true)
    public void afterSuite() {
        logger.info("Test suite execution completed");
        ExtentReportManager.flushReports();
    }
}
