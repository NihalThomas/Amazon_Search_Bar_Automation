package utils;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import java.util.Properties;
import java.io.FileInputStream;
import java.io.IOException;

public class BrowserUtils {
    private static WebDriver driver;
    private static Properties properties;

    static {
        properties = new Properties();
        try {
            FileInputStream fis = new FileInputStream("src/main/resources/config/config.properties");
            properties.load(fis);
            fis.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static WebDriver getDriver(String browser) {
        if (driver == null) {
            if (browser.equalsIgnoreCase("chrome")) {
                driver = new ChromeDriver();
            } else if (browser.equalsIgnoreCase("firefox")) {
                driver = new FirefoxDriver();
            } else {
                throw new IllegalArgumentException("Unsupported browser: " + browser);
            }
            driver.manage().window().maximize();
        }
        return driver;
    }

    public static String getProperty(String key) {
        return properties.getProperty(key);
    }

    public static void closeBrowser() {
        if (driver != null) {
            driver.quit();
            driver = null;
        }
    }
}