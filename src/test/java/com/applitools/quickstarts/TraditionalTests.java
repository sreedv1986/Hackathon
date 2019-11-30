package com.applitools.quickstarts;

import java.awt.image.BufferedImage;
import java.awt.image.DataBuffer;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import javax.imageio.ImageIO;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
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

@RunWith(Enclosed.class)
public class TraditionalTests {

	@RunWith(Parameterized.class)
	public static class TheParameterizedPart {

		private WebDriver driver;
		private String userName;
		private String password;

		public TheParameterizedPart(String userName, String password) {
			this.userName = userName;
			this.password = password;
		}

		
		@Test
		public void loginTest() {
			// Navigate the browser to the "ACME" demo app.
			driver.get("https://demo.applitools.com/hackathon.html");
			System.out.println("UserName: " + userName + " , Password: " + password);
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
				// password must be present
				String errorTxt = driver.findElement(By.xpath("//div[@role='alert' and @class='alert alert-warning']"))
						.getText();
				Assert.assertTrue("Login error text not matching", errorTxt.equals("Password must be present"));
			} else if (!userNameExist && passwordExist) {
				// username must be present
				String errorTxt = driver.findElement(By.xpath("//div[@role='alert' and @class='alert alert-warning']"))
						.getText();
				Assert.assertTrue("Login error text not matching", errorTxt.equals("Username must be present"));
			} else {
				// both username and password must be present
				String errorTxt = driver.findElement(By.xpath("//div[@role='alert' and @class='alert alert-warning']"))
						.getText();
				Assert.assertTrue("Login error text not matching",
						errorTxt.equals("Both Username and Password must be present"));
			}
		}

		@Parameterized.Parameters
		public static Object[][] excelDataProvider() throws Exception {
			DataDrivenHelper ddh = new DataDrivenHelper("LoginCredentials.xlsx");
			Object[][] testdataset = ddh.getTestcaseDataset("Sheet1", "credentials");
			return testdataset;
		}

		@Before
		public void beforeEach() {

			// Use Chrome browser
			driver = new ChromeDriver();
			driver.manage().window().maximize();
		}

		@After
		public void afterEach() {
			// Close the browser.
			driver.quit();
		}
	}

	public static class NotParameterizedPart {
		private WebDriver driver;

		@Test
		public void DynamicContentTest() {
			driver.get("https://demo.applitools.com/hackathonApp.html?showAd=true");

			List<WebElement> ad1 = driver.findElement(By.id("flashSale")).findElements(By.tagName("img"));
			Assert.assertTrue("Dynamic ad1 is missing", ad1.size() == 1);
			List<WebElement> ad2 = driver.findElement(By.id("flashSale2")).findElements(By.tagName("img"));
			Assert.assertTrue("Dynamic ad2 is missing", ad2.size() == 1);
		}

		
		@Test
		public void CanvasChartTest() throws IOException, InterruptedException {
			driver.get("https://demo.applitools.com/hackathon.html");

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

			Thread.sleep(1000);

			File screenshotLocation = new File(".\\HackathonCanvasScreenshot.png");
			File screenshotLocationTemp = new File(".\\HackathonCanvasScreenshotTemp.png");
			WebElement canvas = driver.findElement(By.id("canvas"));
			// Get entire page screenshot
			File screenshot = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
			BufferedImage fullImg = ImageIO.read(screenshot);

			// Get the location of element on the page
			Point point = canvas.getLocation();

			// Get width and height of the element
			int canvasWidth = canvas.getSize().getWidth();
			int canvasHeight = canvas.getSize().getHeight();

			// Crop the entire page screenshot to get only element screenshot
			BufferedImage canvasScreenshot = fullImg.getSubimage(point.getX(), point.getY(), canvasWidth, canvasHeight);
			ImageIO.write(canvasScreenshot, "png", screenshot);

			if (!screenshotLocation.exists()) {
				// Copy the element screenshot to disk
				FileUtils.copyFile(screenshot, screenshotLocation);
			} else {
				FileUtils.copyFile(screenshot, screenshotLocationTemp);
				Assert.assertTrue("There is a change in canvas chart",
						compareImage(screenshotLocation, screenshotLocationTemp));
			}

			// click show data for next year
			driver.findElement(By.id("addDataset")).click();
			Thread.sleep(1000);
			File screenshotLocation2019 = new File(".\\HackathonCanvasScreenshot2019.png");
			canvas = driver.findElement(By.id("canvas"));
			// Get entire page screenshot
			screenshot = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
			fullImg = ImageIO.read(screenshot);

			// Get the location of element on the page
			point = canvas.getLocation();

			// Get width and height of the element
			canvasWidth = canvas.getSize().getWidth();
			canvasHeight = canvas.getSize().getHeight();
			canvasScreenshot = fullImg.getSubimage(point.getX(), point.getY(), canvasWidth, canvasHeight);
			ImageIO.write(canvasScreenshot, "png", screenshot);
			FileUtils.copyFile(screenshot, screenshotLocation2019);
			Assert.assertFalse("There is no change in canvas chart after adding next year",
					compareImage(screenshotLocationTemp, screenshotLocation2019));
		}

		
		@Test
		public void TableSortTest() {

			// Navigate the browser to the "ACME" demo app.
			driver.get("https://demo.applitools.com/hackathon.html");

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
		public void loginUITest() {

			// Navigate the browser to the "ACME" demo app.
			driver.get("https://demo.applitools.com/hackathon.html");

			// verify the elements on the login form
			String headerText = driver.findElement(By.xpath("//h4")).getText();
			Assert.assertTrue("Header text did not match", headerText.equals("Login Form"));

			String usernameLabel = driver.findElement(By.xpath("//input[@id = 'username']/preceding-sibling::label"))
					.getText();
			Assert.assertTrue("Username did not match", usernameLabel.equals("Username"));

			String passwordLabel = driver.findElement(By.xpath("//input[@id = 'password']/preceding-sibling::label"))
					.getText();
			Assert.assertTrue("Password did not match", passwordLabel.equals("Password"));

			String signinLabel = driver.findElement(By.xpath("//button[@id='log-in']")).getText();
			Assert.assertTrue("Log In button text did not match", signinLabel.equals("Log In"));

			String remembermeLabel = driver.findElement(By.xpath("//label[@class = 'form-check-label']")).getText();
			Assert.assertTrue("Remember Me did not match", remembermeLabel.equals("Remember Me"));

			WebElement userIcon = driver
					.findElement(By.xpath("//div[@class = 'pre-icon os-icon os-icon-user-male-circle']"));
			userIcon.isDisplayed();

			WebElement passwordIcon = driver
					.findElement(By.xpath("//div[@class = 'pre-icon os-icon os-icon-fingerprint']"));
			passwordIcon.isDisplayed();

			WebElement twitterIcon = driver.findElement(By.xpath("//img[@src='img/social-icons/twitter.png']"));
			twitterIcon.isDisplayed();

			WebElement facebookIcon = driver.findElement(By.xpath("//img[@src='img/social-icons/facebook.png']"));
			facebookIcon.isDisplayed();

			WebElement linkedinIcon = driver.findElement(By.xpath("//img[@src='img/social-icons/linkedin.png']"));
			linkedinIcon.isDisplayed();

			WebElement logoIcon = driver.findElement(By.xpath("//img[@src='img/logo-big.png']"));
			logoIcon.isDisplayed();

		}

		@After
		public void afterEach() {
			// Close the browser.
			driver.quit();
		}

		@Before
		public void beforeEach() {

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
