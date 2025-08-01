package tests;

import base.BaseTest;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.Assert;
import org.testng.annotations.Test;
import utils.ExceptionHandler;

/**
 * Tests for Amazon search functionality with smart retries and error handling
 */
public class SearchTest extends BaseTest {
    private static final Logger logger = LogManager.getLogger(SearchTest.class);

    @Test(description = "Test search with valid product name")
    public void testValidSearch() throws Exception {
        logger.info("Starting valid search test");
        
        try {
            // Perform search with a specific product name
            String searchTerm = "iPhone 15 Pro Max";
            logger.debug("Searching for: {}", searchTerm);
            searchResultsPage = homePage.searchProduct(searchTerm);
            
            Assert.assertNotNull(searchResultsPage, "Search results page should not be null");
            
            // Verify search results using ExceptionHandler for retries
            ExceptionHandler.handleException(driver, "verify search results", d -> {
                Assert.assertTrue(searchResultsPage.isResultsDisplayed(), 
                    "Search results should be displayed");
                Assert.assertTrue(searchResultsPage.getProductCount() > 0, 
                    "Should return at least one product");
                return null;
            });
            
            // Verify first product name
            String firstProductName = searchResultsPage.getFirstProductName().toLowerCase();
            Assert.assertTrue(
                firstProductName.contains("iphone") || firstProductName.contains("apple"), 
                "First product name should contain search term. Product name: " + firstProductName
            );
            
            logger.info("Valid search test passed successfully");
        } catch (Exception e) {
            logger.error("Valid search test failed: {}", e.getMessage(), e);
            throw e;
        }
    }

    @Test(description = "Test search with empty string")
    public void testEmptySearch() throws Exception {
        logger.info("Starting empty search test");
        
        try {
            try {
                homePage.searchProduct("");
                Assert.fail("Empty search should throw IllegalArgumentException");
            } catch (IllegalArgumentException e) {
                logger.debug("Caught expected IllegalArgumentException");
            }

            Assert.assertTrue(homePage.isLogoDisplayed(), 
                "Should remain on homepage after failed search");
            logger.info("Empty search test passed");
        } catch (Exception e) {
            logger.error("Empty search test failed: {}", e.getMessage());
            throw e;
        }
    }

    @Test(description = "Test search with invalid product name")
    public void testInvalidSearch() throws Exception {
        logger.info("Starting invalid product search test");
        String invalidProduct = "nonexistentproduct" + System.currentTimeMillis();

        try {
            searchResultsPage = homePage.searchProduct(invalidProduct);
            Assert.assertNotNull(searchResultsPage, "Should load results page even for invalid search");

            ExceptionHandler.handleException(driver, "verify no results", d -> {
                boolean hasNoResults = searchResultsPage.hasNoResultsMessage() || 
                                    searchResultsPage.getProductCount() == 0;
                Assert.assertTrue(hasNoResults, 
                    "Should show no results message or zero products");
                return null;
            });
            logger.info("Invalid search test passed");
        } catch (Exception e) {
            logger.error("Invalid search test failed: {}", e.getMessage());
            throw e;
        }
    }
}
