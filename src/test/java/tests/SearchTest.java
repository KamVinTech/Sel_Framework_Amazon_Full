package tests;

import base.BaseTest;
import org.testng.Assert;
import org.testng.annotations.Test;

public class SearchTest extends BaseTest {
    
    @Test(description = "Verify search functionality with valid product")
    public void testValidSearch() throws Exception {
        try {
            // Perform search with a more specific product name
            searchResultsPage = homePage.searchProduct("iPhone 15 Pro Max");
            
            Assert.assertNotNull(searchResultsPage, "Search results page should not be null");
            
            // Verify search results with retry
            int retryCount = 0;
            boolean resultsFound = false;
            while (retryCount < 3 && !resultsFound) {
                try {
                    Assert.assertTrue(searchResultsPage.isResultsDisplayed(), "Search results should be displayed");
                    Assert.assertTrue(searchResultsPage.getProductCount() > 0, "Should return at least one product");
                    resultsFound = true;
                } catch (AssertionError e) {
                    retryCount++;
                    if (retryCount == 3) throw e;
                    Thread.sleep(1000);
                }
            }
            
            // Get and verify first product name
            String firstProductName = searchResultsPage.getFirstProductName().toLowerCase();
            Assert.assertTrue(firstProductName.contains("iphone") || firstProductName.contains("apple"), 
                "First product name should contain search term. Product name: " + firstProductName);
            
            System.out.println("Valid Search Test Passed");
        } catch (Exception e) {
            System.out.println("Test failed: " + e.getMessage());
            throw e;
        }
    }
    
    @Test(description = "Verify search behavior with empty string")
    public void testEmptySearch() throws Exception {
        try {
            // Try to search with empty string
            searchResultsPage = homePage.searchProduct("");
            
            // Should return null for empty search
            Assert.assertNull(searchResultsPage, "Search with empty string should return null");
            
            // Verify we're still on home page
            Assert.assertTrue(homePage.isLogoDisplayed(), "Should remain on home page");
            Assert.assertTrue(homePage.isSearchBoxEnabled(), "Search box should remain enabled");
            
            System.out.println("Empty Search Test Passed");
        } catch (Exception e) {
            System.out.println("Test failed: " + e.getMessage());
            throw e;
        }
    }
    
    @Test(description = "Verify search with invalid product name")
    public void testInvalidSearch() throws Exception {
        try {
            // Search for a truly invalid product (long random string)
            String invalidProduct = "xyzabc123invalidproductnotfound" + System.currentTimeMillis();
            searchResultsPage = homePage.searchProduct(invalidProduct);
            
            Assert.assertNotNull(searchResultsPage, "Search results page should not be null");
            
            // Wait for the page to load completely
            Thread.sleep(2000);
            
            // Check for no results message or zero products
            Assert.assertTrue(
                searchResultsPage.hasNoResultsMessage() || searchResultsPage.getProductCount() == 0,
                "Should either show no results message or have zero products"
            );
            
            System.out.println("Invalid Search Test Passed");
        } catch (Exception e) {
            System.out.println("Test failed: " + e.getMessage());
            throw e;
        }
    }
}
