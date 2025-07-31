package tests;

import base.BaseTest;
import org.testng.Assert;
import org.testng.annotations.Test;

public class ProductTest extends BaseTest {
    
    @Test(description = "Verify product details page elements")
    public void testProductDetails() throws Exception {
        try {
            // Navigate to a product
            searchResultsPage = homePage.searchProduct("iPhone 15");
            Assert.assertTrue(searchResultsPage.isResultsDisplayed(), "Search results should be displayed");
            
            String expectedProductName = searchResultsPage.getFirstProductName();
            productDetailsPage = searchResultsPage.clickFirstProduct();
            
            // Verify product details
            String actualProductTitle = productDetailsPage.getProductTitle();
            Assert.assertTrue(actualProductTitle.contains(expectedProductName), 
                "Product title should match selected product");
            
            // Verify price is displayed
            String price = productDetailsPage.getProductPrice();
            Assert.assertFalse(price.isEmpty(), "Product price should be displayed");
            
            // Verify add to cart button
            Assert.assertTrue(productDetailsPage.isAddToCartButtonVisible(), 
                "Add to Cart button should be visible");
            
            System.out.println("Product Details Test Passed");
        } catch (Exception e) {
            System.out.println("Test failed: " + e.getMessage());
            throw e;
        }
    }
    
    @Test(description = "Verify multiple products from search results")
    public void testMultipleProducts() throws Exception {
        try {
            // Search for products
            searchResultsPage = homePage.searchProduct("iPhone 15");
            Assert.assertTrue(searchResultsPage.isResultsDisplayed(), "Search results should be displayed");
            
            // Get total product count
            int productCount = searchResultsPage.getProductCount();
            Assert.assertTrue(productCount > 1, "Should have multiple products");
            
            // Click first product and verify
            String firstProductName = searchResultsPage.getFirstProductName();
            productDetailsPage = searchResultsPage.clickFirstProduct();
            Assert.assertTrue(productDetailsPage.getProductTitle().contains(firstProductName), 
                "First product title should match");
            
            // Navigate back and verify search results are still present
            driver.navigate().back();
            Assert.assertTrue(searchResultsPage.isResultsDisplayed(), 
                "Search results should still be displayed after navigation");
            
            System.out.println("Multiple Products Test Passed");
        } catch (Exception e) {
            System.out.println("Test failed: " + e.getMessage());
            throw e;
        }
    }
}
