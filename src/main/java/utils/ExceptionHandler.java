package utils;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.function.Function;

public class ExceptionHandler {
    private static final Logger logger = LogManager.getLogger(ExceptionHandler.class);
    private static final int MAX_RETRY_COUNT = ConfigReader.getIntValue("auto.retry.count", 3);
    private static final int RETRY_DELAY = ConfigReader.getIntValue("retry.delay", 1000);

    public static <T> T handleException(WebDriver driver, String action, Function<WebDriver, T> function) throws Exception {
        int retryCount = 0;
        Exception lastException = null;

        while (retryCount < MAX_RETRY_COUNT) {
            try {
                if (retryCount > 0) {
                    Thread.sleep(RETRY_DELAY);
                    logger.info("Retrying {} attempt {}/{}", action, retryCount + 1, MAX_RETRY_COUNT);
                }
                return handleSpecificExceptions(driver, function);
            } catch (StaleElementReferenceException e) {
                logger.warn("Stale element encountered during {}: {}", action, e.getMessage());
                lastException = e;
            } catch (ElementClickInterceptedException e) {
                logger.warn("Element click intercepted during {}: {}", action, e.getMessage());
                attemptToFixInterception(driver, e);
                lastException = e;
            } catch (TimeoutException e) {
                logger.warn("Timeout occurred during {}: {}", action, e.getMessage());
                lastException = e;
            } catch (WebDriverException e) {
                logger.warn("WebDriver exception during {}: {}", action, e.getMessage());
                lastException = e;
            } catch (Exception e) {
                logger.error("Unexpected error during {}: {}", action, e.getMessage());
                throw e; // Unexpected exceptions are thrown immediately
            }
            retryCount++;
        }

        // If all retries failed, throw the last exception
        throw new RuntimeException("Failed to execute " + action + " after " + MAX_RETRY_COUNT + " attempts", lastException);
    }

    private static <T> T handleSpecificExceptions(WebDriver driver, Function<WebDriver, T> function) {
        try {
            return function.apply(driver);
        } catch (StaleElementReferenceException e) {
            // Wait for the page to stabilize
            new WebDriverWait(driver, Duration.ofSeconds(5))
                .until(webDriver -> {
                    try {
                        return function.apply(webDriver) != null;
                    } catch (StaleElementReferenceException ex) {
                        return false;
                    }
                });
            return function.apply(driver);
        }
    }

    private static void attemptToFixInterception(WebDriver driver, ElementClickInterceptedException e) {
        try {
            // Try to scroll element into view
            WebElement element = driver.findElement(By.cssSelector(extractCssFromException(e)));
            ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", element);
            
            // Wait for any overlays to disappear
            waitForOverlaysToDisappear(driver);
            
            // Try to click using JavaScript if needed
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", element);
        } catch (Exception ex) {
            logger.warn("Failed to fix intercepted click: {}", ex.getMessage());
        }
    }

    private static String extractCssFromException(ElementClickInterceptedException e) {
        // Extract element selector from exception message
        String message = e.getMessage();
        int start = message.indexOf("selector: \"") + 11;
        int end = message.indexOf("\"", start);
        return message.substring(start, end);
    }

    private static void waitForOverlaysToDisappear(WebDriver driver) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));
        By overlayLocators = By.cssSelector(".overlay, .modal, .dialog, .loading");
        
        try {
            wait.until(ExpectedConditions.invisibilityOfElementLocated(overlayLocators));
        } catch (TimeoutException e) {
            logger.warn("Timeout waiting for overlays to disappear");
        }
    }

    // Utility method for retrying actions
    public static void retryAction(Runnable action, String actionName) {
        int retryCount = 0;
        Exception lastException = null;

        while (retryCount < MAX_RETRY_COUNT) {
            try {
                if (retryCount > 0) {
                    Thread.sleep(RETRY_DELAY);
                    logger.info("Retrying {} attempt {}/{}", actionName, retryCount + 1, MAX_RETRY_COUNT);
                }
                action.run();
                return;
            } catch (Exception e) {
                logger.warn("Attempt {} failed for {}: {}", retryCount + 1, actionName, e.getMessage());
                lastException = e;
                retryCount++;
            }
        }

        throw new RuntimeException("Failed to execute " + actionName + " after " + MAX_RETRY_COUNT + " attempts", lastException);
    }
}
