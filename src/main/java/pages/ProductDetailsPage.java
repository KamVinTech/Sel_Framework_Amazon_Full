package pages;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import java.time.Duration;

public class ProductDetailsPage {
    private final WebDriver driver;
    private final WebDriverWait wait;

    @FindBy(id = "productTitle")
    private WebElement productTitle;

    @FindBy(css = ".a-price .a-offscreen")
    private WebElement productPrice;

    @FindBy(id = "add-to-cart-button")
    private WebElement addToCartButton;

    public ProductDetailsPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        PageFactory.initElements(driver, this);
    }

    public String getProductTitle() {
        return wait.until(ExpectedConditions.visibilityOf(productTitle)).getText();
    }

    public String getProductPrice() {
        return wait.until(ExpectedConditions.visibilityOf(productPrice)).getText();
    }

    public boolean isAddToCartButtonVisible() {
        return wait.until(ExpectedConditions.visibilityOf(addToCartButton)).isDisplayed();
    }
}
