package pages;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import java.time.Duration;

public class AmazonSearchResultsPage {
    private WebDriver driver;
    private WebDriverWait wait;

    @FindBy(css = "div.s-main-slot div.s-result-item h2 span")
    private WebElement firstResult;

    @FindBy(css = "div.a-section.a-spacing-top-large span")
    private WebElement errorMessage;

    public AmazonSearchResultsPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(15)); // Increased wait time
        PageFactory.initElements(driver, this);
    }

    public boolean verifySearchResults(String productName) {
        try {
            wait.until(ExpectedConditions.visibilityOf(firstResult));
            String resultText = firstResult.getText().toLowerCase();
            return resultText.contains(productName.toLowerCase());
        } catch (Exception e) {
            return false;
        }
    }

    public boolean verifyErrorMessage() {
        try {
            wait.until(ExpectedConditions.visibilityOf(errorMessage));
            return errorMessage.isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    public boolean isPageLoaded() {
        try {
            wait.until(ExpectedConditions.urlContains("s?k="));
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}