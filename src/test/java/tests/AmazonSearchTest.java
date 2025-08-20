package tests;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import pages.AmazonHomePage;
import pages.AmazonSearchResultsPage;
import utils.BrowserUtils;
import utils.ExcelReader;
import utils.ExtentReportManager;
import utils.ScreenshotUtil;
import java.util.ArrayList;
import java.util.List;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

public class AmazonSearchTest {
    private WebDriver driver;
    private AmazonHomePage homePage;
    private AmazonSearchResultsPage resultsPage;

    @BeforeClass
    @Parameters("browser")
    public void setup(String browser) {
        ExtentReportManager.initExtentReports();
        driver = BrowserUtils.getDriver(browser);
        homePage = new AmazonHomePage(driver);
        resultsPage = new AmazonSearchResultsPage(driver);
        homePage.navigateToHome();
    }

    @Test(dataProvider = "searchData")
    public void testSearchFunctionality(String testCase, String input, String expected) {
        ExtentReportManager.startTest(testCase + " - " + input.replaceAll("\\s+", "_"));
        ExtentReportManager.logInfo("Running test case: " + testCase + " with input: " + input);
        String screenshotPath = null;

        try {
            homePage.navigateToHome(); // Reset to homepage before each test
            switch (testCase) {
                case "ValidProductSearch":
                    homePage.enterSearchTerm(input);
                    homePage.clickSearchButton();
                    screenshotPath = ScreenshotUtil.captureScreenshot(driver, testCase + "_" + input.replaceAll("\\s+", "_"));
                    assertTrue(resultsPage.verifySearchResults(expected), "Results should contain: " + expected);
                    ExtentReportManager.logPass("Search results displayed for: " + input, screenshotPath);
                    break;

                case "EmptySearch":
                    String initialUrl = homePage.getCurrentUrl();
                    homePage.clickSearchButton();
                    screenshotPath = ScreenshotUtil.captureScreenshot(driver, testCase);
                    assertEquals(homePage.getCurrentUrl(), initialUrl, "Page should not refresh on empty search");
                    ExtentReportManager.logPass("Empty search kept the same page", screenshotPath);
                    break;

                case "SingleCharSuggestions":
                    homePage.enterSearchTerm(input);
                    screenshotPath = ScreenshotUtil.captureScreenshot(driver, testCase + "_" + input);
                    assertTrue(homePage.isSuggestionContainerDisplayed(), "Suggestion container should be displayed");
                    ExtentReportManager.logPass("Suggestion container displayed for: " + input, screenshotPath);
                    break;

                case "SingleCharTenSuggestions":
                    homePage.enterSearchTerm(input);
                    screenshotPath = ScreenshotUtil.captureScreenshot(driver, testCase + "_" + input);
                    assertTrue(homePage.isTenthSuggestionDisplayed(), "10th suggestion should exist");
                    ExtentReportManager.logPass("10 suggestions displayed for: " + input, screenshotPath);
                    break;

                case "SelectSuggestion":
                    homePage.enterSearchTerm(input);
                    homePage.selectFirstSuggestion();
                    screenshotPath = ScreenshotUtil.captureScreenshot(driver, testCase + "_" + input);
                    assertTrue(resultsPage.isPageLoaded(), "Result page should load after selecting suggestion");
                    ExtentReportManager.logPass("Selected suggestion loaded result page", screenshotPath);
                    break;

                case "TwoCharSuggestions":
                    homePage.enterSearchTerm(input);
                    screenshotPath = ScreenshotUtil.captureScreenshot(driver, testCase + "_" + input);
                    List<WebElement> suggestions = new ArrayList<>();
                    // Check suggestions from sac-suggestion-row-1 to sac-suggestion-row-10
                    for (int i = 1; i <= 10; i++) {
                        try {
                            WebElement suggestion = driver.findElement(By.xpath("//div[@id='sac-suggestion-row-" + i + "']"));
                            suggestions.add(suggestion);
                        } catch (Exception e) {
                            // Break if suggestion not found
                            break;
                        }
                    }
                    assertTrue(suggestions.size() >= 10, "At least 10 suggestions should be displayed for input: " + input);
                    boolean allStartWith = true;
                    for (WebElement suggestion : suggestions) {
                        // Try to get aria-label from child div
                        try {
                            WebElement suggestionTextElement = suggestion.findElement(By.xpath(".//div[@aria-label]"));
                            String ariaLabel = suggestionTextElement.getAttribute("aria-label");
                            if (ariaLabel == null || !ariaLabel.toLowerCase().startsWith(input.toLowerCase())) {
                                // Fallback to text in left-pane-results-container if aria-label is missing
                                try {
                                    String suggestionText = suggestion.findElement(By.xpath(".//div[@class='left-pane-results-container']")).getText();
                                    if (!suggestionText.toLowerCase().startsWith(input.toLowerCase())) {
                                        allStartWith = false;
                                        break;
                                    }
                                } catch (Exception e) {
                                    allStartWith = false;
                                    break;
                                }
                            }
                        } catch (Exception e) {
                            // Fallback to suggestion row text if child div is not found
                            String suggestionText = suggestion.getText();
                            if (!suggestionText.toLowerCase().startsWith(input.toLowerCase())) {
                                allStartWith = false;
                                break;
                            }
                        }
                    }
                    assertTrue(allStartWith, "All suggestions should start with: " + input);
                    ExtentReportManager.logPass("All suggestions start with: " + input, screenshotPath);
                    break;

                case "RepeatedWord":
                    homePage.enterSearchTermWithEnter(input);
                    screenshotPath = ScreenshotUtil.captureScreenshot(driver, testCase + "_" + input.replaceAll("\\s+", "_"));
                    assertTrue(resultsPage.verifySearchResults(expected), "Results should contain: " + expected);
                    ExtentReportManager.logPass("Results loaded for repeated word: " + input, screenshotPath);
                    break;

                case "RandomChar":
                    homePage.enterSearchTermWithEnter(input);
                    screenshotPath = ScreenshotUtil.captureScreenshot(driver, testCase + "_" + input);
                    // Check for absence of results count or 0 results
                    try {
                        WebElement resultsCountElement = driver.findElement(By.xpath("//span[contains(normalize-space(), '1-16 of over') and contains(normalize-space(), 'results for')]"));
                        String resultsText = resultsCountElement.getText();
                        if (resultsText.contains("0 results")) {
                            ExtentReportManager.logPass("No results displayed for random character: " + input, screenshotPath);
                        } else {
                            ExtentReportManager.logFail("Results count found when none expected for random character: " + input, screenshotPath, new AssertionError("Results count found: " + resultsText));
                            throw new AssertionError("Results count found when none expected for random character: " + input);
                        }
                    } catch (Exception e) {
                        // Element not found means no results, which is expected
                        ExtentReportManager.logPass("No results count found for random character: " + input, screenshotPath);
                    }
                    break;

                case "LongString":
                    homePage.enterSearchTermWithEnter(input);
                    screenshotPath = ScreenshotUtil.captureScreenshot(driver, testCase);
                    assertTrue(resultsPage.isPageLoaded(), "Result page should load for long string");
                    ExtentReportManager.logPass("Result page loaded for long string", screenshotPath);
                    break;

                case "CharAndNumber":
                    homePage.enterSearchTermWithEnter(input);
                    screenshotPath = ScreenshotUtil.captureScreenshot(driver, testCase + "_" + input.replaceAll("\\s+", "_"));
                    assertTrue(resultsPage.verifySearchResults(expected), "Results should contain: " + expected);
                    ExtentReportManager.logPass("Results loaded for character and number: " + input, screenshotPath);
                    break;

                default:
                    screenshotPath = ScreenshotUtil.captureScreenshot(driver, testCase);
                    ExtentReportManager.logFail("Unknown test case: " + testCase, screenshotPath, new IllegalArgumentException("Unknown test case"));
                    throw new IllegalArgumentException("Unknown test case: " + testCase);
            }
        } catch (Exception e) {
            screenshotPath = ScreenshotUtil.captureScreenshot(driver, testCase + "_error");
            ExtentReportManager.logFail("Test failed: " + testCase, screenshotPath, e);
            throw e; // Re-throw to ensure TestNG marks the test as failed
        }
    }

    @DataProvider(name = "searchData")
    public Object[][] getSearchData() {
        return ExcelReader.readExcel("src/test/resources/testdata/search_data.xlsx");
    }

    @AfterClass
    public void tearDown() {
        ExtentReportManager.endTest();
        BrowserUtils.closeBrowser();
    }
}