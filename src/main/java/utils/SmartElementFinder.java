package utils;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.*;
import java.util.NoSuchElementException;

public class SmartElementFinder {
    private static final Logger logger = LogManager.getLogger(SmartElementFinder.class);
    private static final Map<String, List<By>> alternativeLocators = new HashMap<>();
    private static final Map<String, By> healedLocators = new HashMap<>();
    
    // Similarity threshold for attribute matching
    private static final double SIMILARITY_THRESHOLD = 0.7;

    public static WebElement findElement(WebDriver driver, By originalLocator, String elementName) {
        try {
            // First try with original locator
            return findWithExplicitWait(driver, originalLocator);
        } catch (TimeoutException | NoSuchElementException e) {
            logger.warn("Original locator failed for {}: {}", elementName, e.getMessage());
            
            // Try with healed locator if available
            String key = createLocatorKey(originalLocator, elementName);
            if (healedLocators.containsKey(key)) {
                try {
                    WebElement element = findWithExplicitWait(driver, healedLocators.get(key));
                    logger.info("Element found using healed locator for: {}", elementName);
                    return element;
                } catch (Exception ex) {
                    logger.warn("Healed locator also failed for: {}", elementName);
                }
            }

            // Try self-healing
            WebElement healedElement = attemptSelfHealing(driver, originalLocator, elementName);
            if (healedElement != null) {
                return healedElement;
            }

            // If all attempts fail, throw custom exception
            throw new ElementNotFoundException("Element not found: " + elementName, e);
        }
    }

    private static WebElement findWithExplicitWait(WebDriver driver, By locator) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(
            ConfigReader.getIntValue("explicit.wait", 10)));
        return wait.until(ExpectedConditions.presenceOfElementLocated(locator));
    }

    private static WebElement attemptSelfHealing(WebDriver driver, By originalLocator, String elementName) {
        try {
            // Get alternative locators
            List<By> alternatives = generateAlternativeLocators(driver, originalLocator);
            
            // Try each alternative
            for (By alternative : alternatives) {
                try {
                    WebElement element = findWithExplicitWait(driver, alternative);
                    if (element != null && element.isDisplayed()) {
                        // Store successful locator for future use
                        healedLocators.put(createLocatorKey(originalLocator, elementName), alternative);
                        logger.info("Self-healing successful for {} using: {}", elementName, alternative);
                        return element;
                    }
                } catch (Exception e) {
                    logger.debug("Alternative locator failed: {}", alternative);
                }
            }
        } catch (Exception e) {
            logger.error("Self-healing attempt failed for: {}", elementName, e);
        }
        return null;
    }

    private static List<By> generateAlternativeLocators(WebDriver driver, By originalLocator) {
        List<By> alternatives = new ArrayList<>();
        String locatorString = originalLocator.toString();
        
        if (locatorString.contains("id=")) {
            // Try partial ID match
            String id = locatorString.split("id=")[1];
            alternatives.add(By.cssSelector(String.format("[id*='%s']", id)));
        }
        
        if (locatorString.contains("className=")) {
            // Try partial class match
            String className = locatorString.split("className=")[1];
            alternatives.add(By.cssSelector(String.format("[class*='%s']", className)));
        }

        if (locatorString.contains("xpath=")) {
            // Generate dynamic XPath alternatives
            alternatives.addAll(generateDynamicXPathAlternatives(locatorString));
        }

        return alternatives;
    }

    private static List<By> generateDynamicXPathAlternatives(String originalXPath) {
        List<By> alternatives = new ArrayList<>();
        
        // Remove position indices if present
        if (originalXPath.contains("[")) {
            alternatives.add(By.xpath(originalXPath.replaceAll("\\[\\d+\\]", "")));
        }
        
        // Try with different attributes
        String[] attributes = {"id", "name", "class", "title", "role", "aria-label"};
        for (String attribute : attributes) {
            if (originalXPath.contains("@" + attribute)) {
                // Try contains() instead of exact match
                String relaxedXPath = originalXPath.replace(
                    "@" + attribute + "='", 
                    "contains(@" + attribute + ",'");
                alternatives.add(By.xpath(relaxedXPath));
            }
        }
        
        return alternatives;
    }

    private static String createLocatorKey(By locator, String elementName) {
        return elementName + ":" + locator.toString();
    }

    // Custom exception for better error handling
    public static class ElementNotFoundException extends RuntimeException {
        public ElementNotFoundException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}
