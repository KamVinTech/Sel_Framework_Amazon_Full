package pages;

import org.openqa.selenium.*;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import java.time.Duration;
import java.util.List;

public class SearchResultsPage {
    private final WebDriver driver;
    private final WebDriverWait wait;

    // Multiple locator strategies for search results
    @FindBy(css = ".s-result-item")
    private List<WebElement> productList;

    @FindBy(css = ".s-search-results .sg-col-inner")
    private List<WebElement> altProductList;

    // Multiple locator strategies for product links
    @FindBy(css = "h2 .a-link-normal")
    private List<WebElement> productLinks;

    @FindBy(css = ".s-result-item .a-text-normal")
    private List<WebElement> altProductLinks;

    @FindBy(css = ".s-result-item h2 a")
    private List<WebElement> titleLinks;

    @FindBy(css = ".s-result-item .a-link-normal[href*='/dp/']")
    private List<WebElement> productDetailLinks;

    public SearchResultsPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(30));
        PageFactory.initElements(driver, this);
        waitForPageLoad();
    }

    private void waitForPageLoad() {
        try {
            // Wait for page load
            wait.until(webDriver -> ((JavascriptExecutor) webDriver)
                .executeScript("return document.readyState").equals("complete"));
                
            // Wait for URL to contain search parameters
            wait.until(driver -> driver.getCurrentUrl().contains("s?k="));

            // Try multiple selectors to find search results or no results message
            By[] selectors = {
                By.cssSelector(".s-result-item"),
                By.cssSelector(".s-search-results"),
                By.cssSelector("[data-component-type='s-search-result']"),
                By.cssSelector(".sg-col-inner"),
                By.cssSelector(".s-no-results-result") // For no results message
            };

            boolean resultsFound = false;
            for (By selector : selectors) {
                try {
                    wait.until(ExpectedConditions.presenceOfElementLocated(selector));
                    resultsFound = true;
                    break;
                } catch (TimeoutException ignored) {}
            }

            if (!resultsFound) {
                throw new TimeoutException("Search results not found with any selector");
            }

            // Additional wait for dynamic content
            Thread.sleep(2000);
        } catch (Exception e) {
            System.out.println("Error during page load wait: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }

    public int getProductCount() {
        try {
            if (!productList.isEmpty()) {
                return productList.size();
            } else if (!altProductList.isEmpty()) {
                return altProductList.size();
            }
            return 0;
        } catch (Exception e) {
            System.out.println("Error getting product count: " + e.getMessage());
            return 0;
        }
    }

    public ProductDetailsPage clickFirstProduct() throws Exception {
        try {
            // Try different strategies to find clickable product
            WebElement productLink = findClickableProduct();
            if (productLink != null) {
                // Scroll into view and wait for it to be stable
                ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block: 'center'});", productLink);
                Thread.sleep(1000);

                String productName = productLink.getText();
                System.out.println("Attempting to click product: " + productName);

                try {
                    productLink.click();
                } catch (Exception e) {
                    // Fallback to JavaScript click
                    ((JavascriptExecutor) driver).executeScript("arguments[0].click();", productLink);
                }

                return new ProductDetailsPage(driver);
            }
            throw new NoSuchElementException("No clickable product found");
        } catch (Exception e) {
            System.out.println("Error while trying to click first product: " + e.getMessage());
            throw e;
        }
    }

    private WebElement findClickableProduct() {
        // Refresh elements to ensure we have the latest DOM
        PageFactory.initElements(driver, this);
        
        // Try different link locators
        @SuppressWarnings("unchecked")
        List<WebElement>[] linkLists = new List[]{productLinks, altProductLinks, titleLinks, productDetailLinks};
        
        // Additional direct selectors as backup
        By[] additionalSelectors = {
            By.cssSelector("h2 .a-link-normal"),
            By.cssSelector(".s-result-item .a-text-normal"),
            By.cssSelector(".s-result-item h2 a"),
            By.cssSelector(".s-result-item .a-link-normal[href*='/dp/']")
        };
        
        // Try PageFactory elements first
        for (List<WebElement> links : linkLists) {
            try {
                if (links != null && !links.isEmpty()) {
                    for (WebElement link : links) {
                        try {
                            if (link.isDisplayed()) {
                                // Scroll element into view
                                ((JavascriptExecutor) driver)
                                    .executeScript("arguments[0].scrollIntoView({block: 'center'});", link);
                                Thread.sleep(500);
                                
                                if (link.isEnabled() && wait.until(ExpectedConditions.elementToBeClickable(link)) != null) {
                                    return link;
                                }
                            }
                        } catch (Exception ignored) {
                            // Continue to next element if one fails
                        }
                    }
                }
            } catch (Exception ignored) {}
        }
        
        // Try additional selectors as backup
        for (By selector : additionalSelectors) {
            try {
                List<WebElement> elements = driver.findElements(selector);
                for (WebElement element : elements) {
                    if (element.isDisplayed() && element.isEnabled()) {
                        wait.until(ExpectedConditions.elementToBeClickable(element));
                        return element;
                    }
                }
            } catch (Exception ignored) {}
        }
        
        return null;
    }

    public boolean isResultsDisplayed() {
        try {
            // First check for no results message
            try {
                List<WebElement> noResultsElements = driver.findElements(
                    By.cssSelector(".s-no-result-search-message, .s-no-results-result"));
                if (!noResultsElements.isEmpty() && noResultsElements.get(0).isDisplayed()) {
                    return false;
                }
            } catch (Exception ignored) {}

            // Then check for actual results
            return !productList.isEmpty() || !altProductList.isEmpty();
        } catch (Exception e) {
            System.out.println("Error checking results display: " + e.getMessage());
            return false;
        }
    }
    
    public boolean hasNoResultsMessage() {
        try {
            // Check for various no results indicators
            String pageSource = driver.getPageSource().toLowerCase();
            
            // Common no-results messages and elements
            String[] noResultsIndicators = {
                "no results for",
                "no search results",
                "try checking your spelling",
                "did not match any products",
                "0 results for",
                "we could not find any results"
            };
            
            // Check for the presence of any no-results message
            for (String indicator : noResultsIndicators) {
                if (pageSource.contains(indicator)) {
                    return true;
                }
            }
            
            // Check for specific no-results elements
            By[] noResultsSelectors = {
                By.cssSelector(".s-no-result-search-message"),
                By.cssSelector(".s-no-results-result"),
                By.cssSelector("[data-component-type='no-results-search-message']")
            };
            
            for (By selector : noResultsSelectors) {
                List<WebElement> elements = driver.findElements(selector);
                if (!elements.isEmpty() && elements.get(0).isDisplayed()) {
                    return true;
                }
            }
            
            return false;
        } catch (Exception e) {
            System.out.println("Error checking for no results message: " + e.getMessage());
            return false;
        }
    }

    public String getFirstProductName() throws InterruptedException {
        try {
            // Wait for search results to be present with any of our selectors
            By[] productSelectors = {
                By.cssSelector("h2 .a-link-normal"),
                By.cssSelector(".s-result-item .a-text-normal"),
                By.cssSelector(".s-result-item h2 a"),
                By.cssSelector(".s-result-item .a-link-normal[href*='/dp/']")
            };

            WebElement productElement = null;
            String productName = null;

            // Try each selector until we find a valid product name
            for (By selector : productSelectors) {
                try {
                    // Wait for elements to be present
                    wait.until(ExpectedConditions.presenceOfElementLocated(selector));
                    
                    // Find all elements with this selector
                    List<WebElement> elements = driver.findElements(selector);
                    
                    // Try each element until we find one with valid text
                    for (WebElement element : elements) {
                        try {
                            if (element.isDisplayed()) {
                                // Try different text extraction methods
                                String text = element.getText().trim();
                                if (text.isEmpty()) {
                                    text = element.getAttribute("textContent");
                                }
                                if (text.isEmpty()) {
                                    text = (String) ((JavascriptExecutor) driver)
                                        .executeScript("return arguments[0].textContent.trim()", element);
                                }
                                
                                // Validate the text content
                                if (text != null && !text.isEmpty() && !text.matches("^[\\d,.$]+$")) {
                                    productElement = element;
                                    productName = text;
                                    break;
                                }
                            }
                        } catch (StaleElementReferenceException e) {
                            // Element became stale, continue to next element
                            continue;
                        }
                    }
                    
                    if (productName != null) {
                        break;
                    }
                } catch (Exception e) {
                    System.out.println("Error with selector " + selector + ": " + e.getMessage());
                }
            }

            if (productElement == null || productName == null) {
                throw new NoSuchElementException("Could not find any product with valid text");
            }

            // Make sure the element is actually interactable
            ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block: 'center'});", productElement);
            Thread.sleep(500);
            wait.until(ExpectedConditions.elementToBeClickable(productElement));

            return productName.trim();
        } catch (Exception e) {
            System.out.println("Error while getting first product name: " + e.getMessage());
            throw e;
        }
    }
}
