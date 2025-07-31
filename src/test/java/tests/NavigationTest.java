package tests;

import base.BaseTest;
import org.testng.Assert;
import org.testng.annotations.Test;

public class NavigationTest extends BaseTest {
    
    @Test(description = "Verify all homepage elements are present")
    public void testHomePageElements() {
        try {
            // Verify logo
            Assert.assertTrue(homePage.isLogoDisplayed(), "Amazon logo should be visible");
            
            // Verify search functionality
            Assert.assertTrue(homePage.isSearchBoxEnabled(), "Search box should be enabled");
            
            // Verify page title
            Assert.assertTrue(homePage.getPageTitle().contains("Amazon"), 
                "Page title should contain Amazon");
            
            System.out.println("Homepage Elements Test Passed");
        } catch (Exception e) {
            System.out.println("Test failed: " + e.getMessage());
            throw e;
        }
    }
    
    @Test(description = "Verify navigation flow from home to product details")
    public void testSearchNavigation() throws Exception {
        try {
            // Navigate to search results
            searchResultsPage = homePage.searchProduct("iPhone 15");
            Assert.assertTrue(searchResultsPage.isResultsDisplayed(), 
                "Search results should be displayed");
            
            // Navigate to product details
            String productName = searchResultsPage.getFirstProductName();
            productDetailsPage = searchResultsPage.clickFirstProduct();
            
            // Verify product details page
            Assert.assertTrue(productDetailsPage.getProductTitle().contains(productName), 
                "Product title should match selected product");
            
            System.out.println("Navigation Flow Test Passed");
        } catch (Exception e) {
            System.out.println("Test failed: " + e.getMessage());
            throw e;
        }
    }
    
    @Test(description = "Verify page titles during navigation")
    public void testPageTitles() throws Exception {
        try {
            // Verify homepage title
            Assert.assertTrue(homePage.getPageTitle().contains("Amazon"), 
                "Homepage title should contain Amazon");
            
            // Navigate to search results and verify title
            searchResultsPage = homePage.searchProduct("iPhone 15");
            Assert.assertTrue(driver.getTitle().contains("iPhone 15"), 
                "Search results page title should contain search term");
            
            // Navigate to product details and verify title
            productDetailsPage = searchResultsPage.clickFirstProduct();
            Assert.assertTrue(driver.getTitle().contains("Amazon.in"), 
                "Product page title should contain Amazon.in");
            
            System.out.println("Page Titles Test Passed");
        } catch (Exception e) {
            System.out.println("Test failed: " + e.getMessage());
            throw e;
        }
    }
}
