package pages;

import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import java.time.Duration;
import java.util.List;

public class AmazonHomePage {
    private WebDriver driver;
    private WebDriverWait wait;

    @FindBy(id = "twotabsearchtextbox")
    private WebElement searchBox;

    @FindBy(id = "nav-search-submit-button")
    private WebElement searchButton;

    @FindBy(xpath = "//div[@id='sac-autocomplete-results-container']")
    private WebElement suggestionContainer;

    @FindBy(xpath = "//div[@id='sac-autocomplete-results-container']//div[contains(@class, 's-suggestion')]")
    private List<WebElement> suggestionList;

    @FindBy(xpath = "//div[@id='sac-suggestion-row-10']")
    private WebElement tenthSuggestion;

    @FindBy(xpath = "//div[@id='sac-suggestion-row-1']")
    private WebElement firstSuggestion;

    public AmazonHomePage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(15)); // Increased wait time
        PageFactory.initElements(driver, this);
    }

    public void navigateToHome() {
        driver.get("https://www.amazon.in");
        wait.until(ExpectedConditions.visibilityOf(searchBox));
    }

    public void enterSearchTerm(String term) {
        wait.until(ExpectedConditions.visibilityOf(searchBox));
        searchBox.clear();
        searchBox.sendKeys(term);
        wait.until(ExpectedConditions.visibilityOf(suggestionContainer)); // Ensure suggestions load
    }

    public void clickSearchButton() {
        wait.until(ExpectedConditions.elementToBeClickable(searchButton));
        searchButton.click();
    }

    public boolean isSuggestionContainerDisplayed() {
        try {
            wait.until(ExpectedConditions.visibilityOf(suggestionContainer));
            return suggestionContainer.isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    public boolean isTenthSuggestionDisplayed() {
        try {
            wait.until(ExpectedConditions.visibilityOf(tenthSuggestion));
            return tenthSuggestion.isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    public void selectFirstSuggestion() {
        wait.until(ExpectedConditions.elementToBeClickable(firstSuggestion));
        firstSuggestion.click();
    }

    public List<WebElement> getSuggestionList() {
        wait.until(ExpectedConditions.visibilityOfAllElements(suggestionList));
        return suggestionList;
    }

    public String getCurrentUrl() {
        return driver.getCurrentUrl();
    }

    public void enterSearchTermWithEnter(String term) {
        wait.until(ExpectedConditions.visibilityOf(searchBox));
        searchBox.clear();
        searchBox.sendKeys(term, Keys.ENTER);
    }
}