package com.example.pages;

import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import pages.SearchResultsPage;


//import com.example.pages.SearchResultsPage;

import java.time.Duration;

public class HomePage {
    private final WebDriver driver;
    private final WebDriverWait wait;

    @FindBy(id = "nav-logo-sprites")
    private WebElement amazonLogo;

    @FindBy(id = "twotabsearchtextbox")
    private WebElement searchBox;

    @FindBy(id = "nav-search-submit-button")
    private WebElement searchButton;

    public HomePage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        PageFactory.initElements(driver, this);
    }

    public boolean isLogoDisplayed() {
        return wait.until(ExpectedConditions.visibilityOf(amazonLogo)).isDisplayed();
    }

    public SearchResultsPage searchProduct(String productName) {
        try {
            WebElement search = wait.until(ExpectedConditions.elementToBeClickable(searchBox));
            search.clear();
            search.sendKeys(productName);
            
            // Wait a brief moment for the search box to update
            Thread.sleep(500);
            
            // Click using both regular click and JavaScript fallback
            try {
                wait.until(ExpectedConditions.elementToBeClickable(searchButton)).click();
            } catch (Exception e) {
                ((JavascriptExecutor) driver).executeScript("arguments[0].click();", searchButton);
            }
            
            // Wait for URL to change, indicating search is initiated
            wait.until(driver -> driver.getCurrentUrl().contains("s?k="));
            
            return new SearchResultsPage(driver);
        } catch (Exception e) {
            System.out.println("Error during search: " + e.getMessage());
            throw new RuntimeException("Failed to perform search: " + e.getMessage());
        }
    }

    public String getPageTitle() {
        return driver.getTitle();
    }

    public boolean isSearchBoxEnabled() {
        return wait.until(ExpectedConditions.elementToBeClickable(searchBox)).isEnabled();
    }
}
