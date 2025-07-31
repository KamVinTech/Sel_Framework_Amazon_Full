package utils;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;

import java.time.Duration;

public class WebDriverFactory {
    private static final Logger logger = LogManager.getLogger(WebDriverFactory.class);
    private static final ThreadLocal<WebDriver> driverThreadLocal = new ThreadLocal<>();

    public static WebDriver getDriver() {
        if (driverThreadLocal.get() == null) {
            initializeDriver();
        }
        return driverThreadLocal.get();
    }

    public static void initializeDriver() {
        try {
            WebDriver driver = createDriver();
            configureDriver(driver);
            driverThreadLocal.set(driver);
            logger.info("WebDriver initialized successfully");
        } catch (Exception e) {
            logger.error("Failed to initialize WebDriver: " + e.getMessage(), e);
            throw new RuntimeException("Failed to initialize WebDriver", e);
        }
    }

    private static void configureDriver(WebDriver driver) {
        int implicitWait = ConfigReader.getIntValue("implicit.wait", 10);
        int pageLoadTimeout = ConfigReader.getIntValue("page.load.timeout", 30);
        int scriptTimeout = ConfigReader.getIntValue("script.timeout", 30);

        driver.manage().window().maximize();
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(implicitWait));
        driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(pageLoadTimeout));
        driver.manage().timeouts().scriptTimeout(Duration.ofSeconds(scriptTimeout));

        logger.info(String.format("WebDriver configured with implicitWait=%ds, pageLoadTimeout=%ds, scriptTimeout=%ds",
                implicitWait, pageLoadTimeout, scriptTimeout));
    }

    private static WebDriver createDriver() {
        String browser = ConfigReader.getValue("browser", "chrome").toLowerCase();
        boolean isHeadless = ConfigReader.getBooleanValue("headless", false);
        
        logger.info("Creating WebDriver instance for browser: " + browser);
        
        try {
            switch (browser) {
                case "chrome":
                    return createChromeDriver(isHeadless);
                case "firefox":
                    return createFirefoxDriver(isHeadless);
                case "edge":
                    return createEdgeDriver(isHeadless);
                default:
                    logger.warn("Unsupported browser: " + browser + ". Defaulting to Chrome");
                    return createChromeDriver(isHeadless);
            }
        } catch (Exception e) {
            logger.error("Failed to create WebDriver for browser " + browser + ": " + e.getMessage(), e);
            throw new RuntimeException("Failed to create WebDriver for browser " + browser, e);
        }
    }

    private static WebDriver createChromeDriver(boolean isHeadless) {
        WebDriverManager.chromedriver().setup();
        ChromeOptions options = new ChromeOptions();
        if (isHeadless) {
            options.addArguments("--headless=new");
        }
        options.addArguments("--start-maximized");
        options.addArguments("--disable-notifications");
        options.addArguments("--disable-popup-blocking");
        options.addArguments("--remote-allow-origins=*");
        return new ChromeDriver(options);
    }

    private static WebDriver createFirefoxDriver(boolean isHeadless) {
        WebDriverManager.firefoxdriver().setup();
        FirefoxOptions options = new FirefoxOptions();
        if (isHeadless) {
            options.addArguments("-headless");
        }
        options.addArguments("-start-maximized");
        return new FirefoxDriver(options);
    }

    private static WebDriver createEdgeDriver(boolean isHeadless) {
        WebDriverManager.edgedriver().setup();
        EdgeOptions options = new EdgeOptions();
        if (isHeadless) {
            options.addArguments("--headless=new");
        }
        options.addArguments("--start-maximized");
        options.addArguments("--remote-allow-origins=*");
        return new EdgeDriver(options);
    }

    public static void quitDriver() {
        WebDriver driver = driverThreadLocal.get();
        if (driver != null) {
            logger.info("Quitting WebDriver instance");
            driver.quit();
            driverThreadLocal.remove();
        }
    }
}
