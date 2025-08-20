package utils;

import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ScreenshotUtil {
    public static String captureScreenshot(WebDriver driver, String testName) {
        // Create screenshots directory if it doesn't exist
        String screenshotDir = "reports/screenshots/";
        try {
            Files.createDirectories(Paths.get(screenshotDir));
        } catch (IOException e) {
            e.printStackTrace();
        }

        String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String screenshotPath = screenshotDir + testName + "_" + timestamp + ".png";
        try {
            File screenshot = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
            Files.copy(screenshot.toPath(), Paths.get(screenshotPath));
            return screenshotPath;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}