package com.applitools.quickstarts;

import static com.google.common.base.Strings.isNullOrEmpty;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import javax.imageio.ImageIO;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.experimental.runners.Enclosed;
import org.openqa.selenium.By;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.Point;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.applitools.eyes.BatchInfo;
import com.applitools.eyes.EyesRunner;
import com.applitools.eyes.selenium.ClassicRunner;
import com.applitools.eyes.selenium.Eyes;
import com.applitools.eyes.selenium.fluent.Target;

@RunWith(Enclosed.class)
public class VisualAITests {

	@RunWith(Parameterized.class)
	public static class TheParameterizedPart {

		private EyesRunner runner;
		private Eyes eyes;
		private static BatchInfo batch;
		private WebDriver driver;
		private String userName;
		private String password;
		private String testName;

		public TheParameterizedPart(String userName, String password, String testName) {
			this.userName = userName;
			this.password = password;
			this.testName = testName;
		}
		
		@Test
		public void loginTest() throws Exception {
			eyes.open(driver, "Hackathon App", testName);
			// Navigate the browser to the "ACME" demo app.
			driver.get("https://demo.applitools.com/hackathonV2.html");
			System.out.println("UserName: " + userName + " , Password: " + password + " , TestName: " + testName);
			boolean userNameExist = !StringUtils.isBlank(userName);
			boolean passwordExist = !StringUtils.isBlank(password);
			if (userNameExist) {
				WebElement userNameWE = driver.findElement(By.id("username"));
				userNameWE.sendKeys(userName);
			}
			if (passwordExist) {
				WebElement passwordWE = driver.findElement(By.id("password"));
				passwordWE.sendKeys(password);
			}
			driver.findElement(By.id("log-in")).click();
			if (userNameExist && passwordExist) {
				// should see the application
				WebDriverWait wait = new WebDriverWait(driver, 10); // you can reuse this one

				WebElement table = driver.findElement(By.id("transactionsTable"));
				wait.until(ExpectedConditions.visibilityOf(table));
			} else if (userNameExist && !passwordExist) {
				// Visual checkpoint
				eyes.checkWindow("UserName provided Window");
			} else if (!userNameExist && passwordExist) {
				// Visual checkpoint
				eyes.checkWindow("Password provided Window");
			} else {
				// Visual checkpoint
				eyes.checkWindow("No UserName Password provided Window");
			}			
		}

		@Parameterized.Parameters
		public static Object[][] excelDataProvider() throws Exception {
			DataDrivenHelper ddh = new DataDrivenHelper("LoginCredentials.xlsx");
			Object[][] testdataset = ddh.getTestcaseDataset("Sheet1", "credentials");
			return testdataset;
		}

		@BeforeClass
		public static void setBatch() {
			// Must be before ALL tests (at Class-level)
			batch = new BatchInfo("Login Batch");
		}

		@Before
		public void beforeEach() {
			// Initialize the Runner for your test.
			runner = new ClassicRunner();

			// Initialize the eyes SDK
			eyes = new Eyes(runner);

			// Raise an error if no API Key has been found.
			if (isNullOrEmpty(System.getenv("APPLITOOLS_API_KEY"))) {
				throw new RuntimeException("No API Key found; Please set environment variable 'APPLITOOLS_API_KEY'.");
			}

			// Set your personal Applitols API Key from your environment variables.
			eyes.setApiKey(System.getenv("APPLITOOLS_API_KEY"));

			System.out.println(System.getenv("APPLITOOLS_API_KEY"));
			// set batch name
			eyes.setBatch(batch);

			// Use Chrome browser
			driver = new ChromeDriver();
			driver.manage().window().maximize();
		}

		@After
		public void afterEach() {
			// End the test.
			eyes.closeAsync();
			// Close the browser.
			driver.quit();
			// If the test was aborted before eyes.close was called, ends the test as
			// aborted.
			eyes.abortIfNotClosed();

		}
	}

	public static class NotParameterizedPart {
		private EyesRunner runner;
		private Eyes eyes;
		private static BatchInfo batch;
		private WebDriver driver;

		@BeforeClass
		public static void setBatch() {
			// Must be before ALL tests (at Class-level)
			batch = new BatchInfo("Hackathon Batch");
		}

		@Test
		public void DynamicContentTest() {
			eyes.open(driver, "Hackathon App", "DynamicContentTest");
			driver.get("https://demo.applitools.com/hackathonAppV2.html?showAd=true");
			// Visual checkpoint - make sure nothing changes other than ads
			eyes.checkWindow("DynamicContentTest Window");
			List<WebElement> ad1 = driver.findElement(By.id("flashSale")).findElements(By.tagName("img"));
			Assert.assertTrue("Dynamic ad1 is missing", ad1.size() == 1);
			List<WebElement> ad2 = driver.findElement(By.id("flashSale2")).findElements(By.tagName("img"));
			Assert.assertTrue("Dynamic ad2 is missing", ad2.size() == 1);
		}

		@Test
		public void CanvasChartTest() throws IOException, InterruptedException {
			eyes.open(driver, "Hackathon App", "CanvasChartTest");
			driver.get("https://demo.applitools.com/hackathonV2.html");

			// login to application
			WebElement userNameWE = driver.findElement(By.id("username"));
			userNameWE.sendKeys("user");
			WebElement passwordWE = driver.findElement(By.id("password"));
			passwordWE.sendKeys("password");
			driver.findElement(By.id("log-in")).click();

			// should see the application
			WebDriverWait wait = new WebDriverWait(driver, 10); // you can reuse this one
			WebElement table = driver.findElement(By.id("transactionsTable"));
			wait.until(ExpectedConditions.visibilityOf(table));

			// Click showExpensesChart
			driver.findElement(By.id("showExpensesChart")).click();
			WebElement canvasWE = driver.findElement(By.id("canvas"));
			wait.until(ExpectedConditions.visibilityOf(canvasWE));

			// Visual checkpoint - taken screenshot of chart with 2017 and 2018
			eyes.checkWindow("Canvas Chart Window");

			// click show data for next year
			driver.findElement(By.id("addDataset")).click();
			// click show data for next year
			eyes.checkWindow("Canvas Chart 2019 Window");
		}

		@Test
		public void TableSortTest() {

			eyes.open(driver, "Hackathon App", "TableSortTest");
			// Navigate the browser to the "ACME" demo app.
			driver.get("https://demo.applitools.com/hackathonV2.html");

			// login to application
			WebElement userNameWE = driver.findElement(By.id("username"));
			userNameWE.sendKeys("user");
			WebElement passwordWE = driver.findElement(By.id("password"));
			passwordWE.sendKeys("password");
			driver.findElement(By.id("log-in")).click();

			// should see the application
			WebDriverWait wait = new WebDriverWait(driver, 10); // you can reuse this one
			WebElement table = driver.findElement(By.id("transactionsTable"));
			wait.until(ExpectedConditions.visibilityOf(table));

			// Visual checkpoint - before sorting transaction data table
			eyes.checkWindow("Transaction Data Window");

			// save data from each row before sorting
			HashMap<String, List<String>> transactionDataMap = new HashMap<String, List<String>>();
			List<WebElement> allRows = table.findElements(By.xpath("tbody/tr"));
			for (WebElement row : allRows) {
				List<WebElement> cells = row.findElements(By.xpath("./*"));
				String key = cells.get(cells.size() - 1).getText();
				List<String> values = new ArrayList<String>();
				transactionDataMap.put(key, values);
				for (WebElement cell : cells) {
					values.add(cell.getText());
				}
			}
			System.out.println("transactionDataMap: " + transactionDataMap);

			// sort amount column
			driver.findElement(By.id("amount")).click();

			// Visual checkpoint - before sorting transaction data table after sort
			eyes.checkWindow("Transaction Data Window Sorted");

			// save data from each row after sorting
			HashMap<String, List<String>> sortedTransactionDataMap = new HashMap<String, List<String>>();
			allRows = table.findElements(By.xpath("tbody/tr"));
			List<Float> formattedSortedAmountsUI = new ArrayList<Float>();
			for (WebElement row : allRows) {
				List<WebElement> cells = row.findElements(By.xpath("./*"));
				String key = cells.get(cells.size() - 1).getText();
				formattedSortedAmountsUI
						.add(Float.parseFloat(key.replace("USD", "").replace(" ", "").replace(",", "")));
				List<String> values = new ArrayList<String>();
				sortedTransactionDataMap.put(key, values);
				for (WebElement cell : cells) {
					values.add(cell.getText());
				}
			}
			System.out.println("sortedTransactionDataMap: " + sortedTransactionDataMap);

			// verifying if each row data is in tact after sorting
			for (String key : transactionDataMap.keySet()) {
				List<String> rowData = transactionDataMap.get(key);
				List<String> rowDataSorted = sortedTransactionDataMap.get(key);
				Arrays.equals(rowData.toArray(), rowDataSorted.toArray());
			}

			// verify amount is in ascending order
			System.out.println("formattedSortedAmountsUI : " + formattedSortedAmountsUI);
			for (int i = 1; i < formattedSortedAmountsUI.size(); i++) {
				Assert.assertTrue("Amounts are not in Ascending order",
						formattedSortedAmountsUI.get(i - 1).compareTo(formattedSortedAmountsUI.get(i)) < 0);
			}
		}

		@Test
		public void loginUITest() throws Exception {

			eyes.open(driver, "Hackathon App", "loginUITest");

			// Navigate the browser to the "ACME" demo app.
			driver.get("https://demo.applitools.com/hackathonV2.html");

			// Visual checkpoint #1 - Check the login page.
			eyes.checkWindow("Login Window");
		}

		@After
		public void afterEach() {
			// End the test.
			eyes.closeAsync();
			// Close the browser.
			driver.quit();
			// If the test was aborted before eyes.close was called, ends the test as
			// aborted.
			eyes.abortIfNotClosed();

		}

		@Before
		public void beforeEach() {
			// Initialize the Runner for your test.
			runner = new ClassicRunner();

			// Initialize the eyes SDK
			eyes = new Eyes(runner);

			// Raise an error if no API Key has been found.
			if (isNullOrEmpty(System.getenv("APPLITOOLS_API_KEY"))) {
				throw new RuntimeException("No API Key found; Please set environment variable 'APPLITOOLS_API_KEY'.");
			}

			// Set your personal Applitols API Key from your environment variables.
			eyes.setApiKey(System.getenv("APPLITOOLS_API_KEY"));

			System.out.println(System.getenv("APPLITOOLS_API_KEY"));
			// set batch name
			eyes.setBatch(batch);

			// Use Chrome browser
			driver = new ChromeDriver();
			driver.manage().window().maximize();
		}

		public static boolean compareImage(File fileA, File fileB) throws IOException {

			// take buffer data from both image files //
			BufferedImage imgA = ImageIO.read(fileA);
			BufferedImage imgB = ImageIO.read(fileB);
			// The images must be the same size.
			if (imgA.getWidth() != imgB.getWidth() || imgA.getHeight() != imgB.getHeight()) {
				return false;
			}

			int width = imgA.getWidth();
			int height = imgA.getHeight();

			// Loop over every pixel.
			for (int y = 0; y < height; y++) {
				for (int x = 0; x < width; x++) {
					// Compare the pixels for equality.
					if (imgA.getRGB(x, y) != imgB.getRGB(x, y)) {
						return false;
					}
				}
			}
			return true;
		}
	}
}
