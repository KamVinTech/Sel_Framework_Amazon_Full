package tests;

import base.BaseTest;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.Assert;
import org.testng.annotations.Test;
import utils.ExceptionHandler;

/**
 * Tests for Amazon navigation flows with smart retries and error handling
 */
public class NavigationTest extends BaseTest {
    private static final Logger logger = LogManager.getLogger(NavigationTest.class);
    
    @Test(description = "Verify all homepage elements are present")
    public void testHomePageElements() throws Exception {
        logger.info("Starting homepage elements verification test");
        
        try {
            // Verify essential homepage elements using ExceptionHandler
            ExceptionHandler.handleException(driver, "verify homepage elements", d -> {
                // Verify logo
                try {
                    Assert.assertTrue(homePage.isLogoDisplayed(), 
                        "Amazon logo should be visible");
                } catch (Exception e) {
                    e.printStackTrace();
                }
                
                // Verify search functionality
                try {
                    Assert.assertTrue(homePage.isSearchBarDisplayed(), 
                        "Search box should be visible");
                } catch (Exception e) {
                    e.printStackTrace();
                }
                try {
                    Assert.assertTrue(homePage.isSearchBarEnabled(), 
                        "Search box should be enabled");
                } catch (Exception e) {
                    e.printStackTrace();
                }
                
                // Verify page title
                String pageTitle = driver.getTitle();
                Assert.assertTrue(pageTitle.contains("Amazon"), 
                    "Page title should contain Amazon but was: " + pageTitle);
                
                return null;
            });
            
            logger.info("Homepage elements test passed successfully");
        } catch (Exception e) {
            logger.error("Homepage elements test failed: {}", e.getMessage(), e);
            throw e;
        }
    }
    
    @Test(description = "Verify navigation flow from home to product details")
    public void testSearchNavigation() throws Exception {
        logger.info("Starting navigation flow test");
        String searchTerm = "iPhone 15";
        
        try {
            // Navigate to search results with retry mechanism
            logger.debug("Searching for: {}", searchTerm);
            searchResultsPage = homePage.searchProduct(searchTerm);
            
            ExceptionHandler.handleException(driver, "verify search results page", d -> {
                Assert.assertTrue(searchResultsPage.isResultsDisplayed(), 
                    "Search results should be displayed");
                return null;
            });
            
            // Navigate to product details with retry mechanism
            String productName = searchResultsPage.getFirstProductName();
            logger.debug("Clicking on product: {}", productName);
            productDetailsPage = searchResultsPage.clickFirstProduct();
            
            // Verify product details page with retry mechanism
            ExceptionHandler.handleException(driver, "verify product details page", d -> {
                String actualTitle = productDetailsPage.getProductTitle();
                Assert.assertTrue(actualTitle.contains(productName), 
                    "Product title should match selected product. Expected to contain: " + 
                    productName + ", but was: " + actualTitle);
                return null;
            });
            
            logger.info("Navigation flow test passed successfully");
        } catch (Exception e) {
            logger.error("Navigation flow test failed: {}", e.getMessage(), e);
            throw e;
        }
    }
    
    @Test(description = "Verify page titles during navigation")
    public void testPageTitles() throws Exception {
        logger.info("Starting page titles verification test");
        String searchTerm = "iPhone 15";
        
        try {
            // Verify homepage title with retry mechanism
            ExceptionHandler.handleException(driver, "verify homepage title", d -> {
                String homeTitle = driver.getTitle();
                Assert.assertTrue(homeTitle.contains("Amazon"), 
                    "Homepage title should contain Amazon but was: " + homeTitle);
                return null;
            });
            
            // Navigate to search results and verify title
            logger.debug("Searching for: {}", searchTerm);
            searchResultsPage = homePage.searchProduct(searchTerm);
            
            ExceptionHandler.handleException(driver, "verify search results title", d -> {
                String searchTitle = driver.getTitle();
                Assert.assertTrue(searchTitle.contains(searchTerm), 
                    "Search results page title should contain search term but was: " + searchTitle);
                return null;
            });
            
            // Navigate to product details and verify title
            String productName = searchResultsPage.getFirstProductName();
            logger.debug("Clicking on product: {}", productName);
            productDetailsPage = searchResultsPage.clickFirstProduct();
            
            ExceptionHandler.handleException(driver, "verify product page title", d -> {
                String productTitle = driver.getTitle();
                Assert.assertTrue(productTitle.contains("Amazon.in"), 
                    "Product page title should contain Amazon.in but was: " + productTitle);
                return null;
            });
            
            logger.info("Page titles test passed successfully");
        } catch (Exception e) {
            logger.error("Page titles test failed: {}", e.getMessage(), e);
            throw e;
        }
    }
}
