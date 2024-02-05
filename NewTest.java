package com.example;



import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.concurrent.TimeUnit;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class NewTest {

    private WebDriver driver;
    private SearchWebSite google;
    private SearchWebSite bing;
    private SearchWebSite yahoo;
    private long startTime;

    @BeforeMethod
    public void setUp() {
        // Set up the WebDriver
//        System.setProperty("webdriver.chrome.driver", "path/to/chromedriver");
        driver = new ChromeDriver();

        google = new SearchWebSite("Google", "q", "//h3", "https://www.google.com");
        bing = new SearchWebSite("Bing", "q", "//*[@id=\"b_results\"]/li[2]/h2/a", "https://www.bing.com");
        yahoo = new SearchWebSite("Yahoo", "p", "//*[@id=\"web\"]/ol/li[2]/div/div[1]/h3/a", "https://www.yahoo.com");

        // Set up logging to a file
        Logger logger = Logger.getLogger("");
        FileHandler fh;

        try {
            fh = new FileHandler("test_log.txt");
            logger.addHandler(fh);
            SimpleFormatter formatter = new SimpleFormatter();
            fh.setFormatter(formatter);
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Start measuring test duration
        startTime = System.currentTimeMillis();
    }

    @AfterMethod
    public void tearDown() {
        // End measuring test duration
        long endTime = System.currentTimeMillis();
        long durationSeconds = (endTime - startTime) / 1000;

        // Log the test name and duration
        String testName = this.getClass().getSimpleName();
        Logger.getLogger("").log(Level.INFO, "Test '" + testName + "' duration: " + durationSeconds + " seconds");

        // Close the browser window
        driver.quit();
    }

    private WebElement searchAndFind(SearchWebSite searchEngine, String searchTerm) {
        // Navigate to Browser
        driver.get(searchEngine.getUrl());

        WebElement searchInput = null;
        if (searchEngine.getQuery() != null) {
            searchInput = driver.findElement(By.name(searchEngine.getQuery()));
        } else {
            System.out.println("Input name undefined");
        }

        assert searchInput != null;
        searchInput.sendKeys(searchTerm, Keys.RETURN);

        // Wait for the results page to load
        driver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);

        return driver.findElement(By.xpath(searchEngine.getXpath()));
    }

    @Test
    public void testGoogleSearch() {
        WebElement results = searchAndFind(google, "Fiserv");
        String expectedResult = "Fiserv: Financial Services Technology, Mobile Banking ...";
        Assert.assertEquals(results.getText(), expectedResult);
    }

    @Test
    public void testYahooSearch() {
        WebElement results = searchAndFind(yahoo, "Fiserv");
        String expectedResult = "Financial Services Technology, Mobile Banking, Payments | Fiserv";
        Assert.assertEquals(results.getAttribute("accessible_name"), expectedResult);
    }

    @Test
    public void testBingSearch() {
        WebElement results = searchAndFind(bing, "Fiserv");
        String expectedResult = "Financial Services Technology, Mobile Banking, Payments | Fiserv";
        Assert.assertEquals(results.getText(), expectedResult);
    }

    @Test
    public void testPageTitle() {
        WebElement results = searchAndFind(google, "Fiserv");
        String expectedTitle = "Fiserv - Google Search";
        Assert.assertEquals(driver.getTitle(), expectedTitle);
    }

    @Test
    public void testSearchNumbersSpecialCharacters() {
        String searchTerm = "!@#$%^&*1234567890";
        String expectedGoogleResult = "`1234567890-=qwertyuiop[]\\asdfghjkl ... - Urban Dictionary";
        String expectedYahooResult = "Number 1234567890 English version Toy Soda - YouTube";

        // Test for Google
        WebElement resultsGoogle = searchAndFind(google, searchTerm);
        Assert.assertEquals(resultsGoogle.getText(), expectedGoogleResult);

        // Test for Yahoo
        WebElement resultsYahoo = searchAndFind(yahoo, searchTerm);
        Assert.assertEquals(resultsYahoo.getAttribute("accessible_name"), expectedYahooResult);
    }
}
