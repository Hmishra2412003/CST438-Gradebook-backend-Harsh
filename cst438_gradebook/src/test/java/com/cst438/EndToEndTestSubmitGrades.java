package com.cst438;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.Test;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.springframework.boot.test.context.SpringBootTest;

/*
 * This example shows how to use selenium testing using the web driver
 * with Chrome browser.
 *
 *  - Buttons, input, and anchor elements are located using XPATH expression.
 *  - onClick( ) method is used with buttons and anchor tags.
 *  - Input fields are located and sendKeys( ) method is used to enter test data.
 *  - Spring Boot JPA is used to initialize, verify and reset the database before
 *      and after testing.
 *
 *  In SpringBootTest environment, the test program may use Spring repositories to
 *  setup the database for the test and to verify the result.
 */

@SpringBootTest
public class EndToEndTestSubmitGrades {


	public static final String CHROME_DRIVER_FILE_LOCATION = "C://Users//18315//Desktop//chromedriver.exe/";

	public static final String URL = "http://localhost:3000";
	public static final int SLEEP_DURATION = 1000; // 1 second.
	public static final String TEST_ASSIGNMENT_NAME = "db design";
	public static final String NEW_GRADE = "99";
	public static final String UPDATED_ASSIGNMENT_NAME = "React Assignment";

	public static final String UPDATED_NAME = "db designReact Assignment";

	public static final String ADD_ASSIGNMENT_NAME = "Database Assignment";

	public static final String ADD_ASSIGNMENT_ID = "31045";

	public static final String ADD_ASSIGNMENT_DATE = "2023-01-01";


	@Test
	public void addCourseTest() throws Exception {


		// set the driver location and start driver
		//@formatter:off
		// browser	property name 				Java Driver Class
		// edge 	webdriver.edge.driver 		EdgeDriver
		// FireFox 	webdriver.firefox.driver 	FirefoxDriver
		// IE 		webdriver.ie.driver 		InternetExplorerDriver
		//@formatter:on

		/*
		 * initialize the WebDriver and get the home page.
		 */

		System.setProperty("webdriver.chrome.driver", CHROME_DRIVER_FILE_LOCATION);
		ChromeOptions ops = new ChromeOptions();
		ops.addArguments("--remote-allow-origins=*");
		WebDriver driver = new ChromeDriver(ops);
		// Puts an Implicit wait for 10 seconds before throwing exception
		driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);

		driver.get(URL);
		Thread.sleep(SLEEP_DURATION);

		WebElement w;


		try {

			List<WebElement> elements = driver.findElements(By.xpath("//td"));
			boolean found = false;
			for (WebElement we : elements) {
				if (we.getText().equals(TEST_ASSIGNMENT_NAME)) {
					found = true;
					we.findElement(By.xpath("..//a")).click();
					break;
				}
			}
			assertThat(found).withFailMessage("The test assignment was not found.").isTrue();
			Thread.sleep(SLEEP_DURATION);

			ArrayList<String> originalGrades = new ArrayList<>();
			elements = driver.findElements(By.xpath("//input"));
			for (WebElement element : elements) {
				originalGrades.add(element.getAttribute("value"));
				element.clear();
				element.sendKeys(NEW_GRADE);
				Thread.sleep(SLEEP_DURATION);
			}

			for (String s : originalGrades) {
				System.out.println("'" + s + "'");
			}
			driver.findElement(By.id("savegrades")).click();
			Thread.sleep(SLEEP_DURATION);

			w = driver.findElement(By.id("gmessage"));
			assertThat(w.getText()).withFailMessage("After saving grades, message should be \"Grades saved.\"").startsWith("Grades saved");

			driver.navigate().back();  // back button to last page
			Thread.sleep(SLEEP_DURATION);

			// find the assignment 'db design' again.
			elements = driver.findElements(By.xpath("//td"));
			found = false;
			for (WebElement we : elements) {
				if (we.getText().equals(TEST_ASSIGNMENT_NAME)) {
					found = true;
					we.findElement(By.xpath("..//a")).click();
					break;
				}
			}
			Thread.sleep(SLEEP_DURATION);
			assertThat(found).withFailMessage("The test assignment was not found.").isTrue();

			// verify the grades. Change grades back to original values

			elements = driver.findElements(By.xpath("//input"));
			for (int idx = 0; idx < elements.size(); idx++) {
				WebElement element = elements.get(idx);
				assertThat(element.getAttribute("value")).withFailMessage("Incorrect grade value.").isEqualTo(NEW_GRADE);

				// clear the input value by backspacing over the value
				while (!element.getAttribute("value").equals("")) {
					element.sendKeys(Keys.BACK_SPACE);
				}
				if (!originalGrades.get(idx).equals("")) element.sendKeys(originalGrades.get(idx));
				Thread.sleep(SLEEP_DURATION);
			}
			driver.findElement(By.id("savegrades")).click();
			Thread.sleep(SLEEP_DURATION);

			w = driver.findElement(By.id("gmessage"));
			assertThat(w.getText()).withFailMessage("After saving grades, message should be \"Grades saved.\"").startsWith("Grades saved");


		} catch (Exception ex) {
			throw ex;
		} finally {

			driver.quit();
		}

	}

	@Test
	public void addAssignmentTest() throws Exception {
		System.setProperty("webdriver.chrome.driver", CHROME_DRIVER_FILE_LOCATION);
		ChromeOptions ops = new ChromeOptions();
		ops.addArguments("--remote-allow-origins=*");
		WebDriver driver = new ChromeDriver(ops);
		driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);


		try {
			driver.get(URL);
			Thread.sleep(SLEEP_DURATION);

			// Locate and click the "Add Assignment" button
			driver.findElement(By.id("addbutton1")).click();
			Thread.sleep(SLEEP_DURATION);

			// Locate and click the "Add Assignment" button
			driver.findElement(By.id("addbutton2")).click();
			Thread.sleep(SLEEP_DURATION);

			// Find the assignment name input field and enter a new assignment name
			WebElement assignmentNameElement = driver.findElement(By.id("addassignmentname"));
			assignmentNameElement.sendKeys(ADD_ASSIGNMENT_NAME);
			Thread.sleep(SLEEP_DURATION);

			WebElement assignmentIdElement = driver.findElement(By.id("addassignmentid"));
			assignmentIdElement.sendKeys(ADD_ASSIGNMENT_ID);

			WebElement assignmentDateElement = driver.findElement(By.id("addassignmentdate"));
			assignmentDateElement.sendKeys(ADD_ASSIGNMENT_DATE);


			// Locate and click the "Save" button to add the assignment
			driver.findElement(By.id("addbutton3")).click();
			Thread.sleep(SLEEP_DURATION);

			assertEquals(ADD_ASSIGNMENT_NAME, driver.findElement(By.xpath("//table/tbody/tr[5]/td[1]")).getText());

		} catch (Exception ex) {
			throw ex;
		} finally {
			driver.quit();
			Thread.sleep(5000);
		}
	}

	@Test
	public void updateAssignmentTest() throws Exception {
		System.setProperty("webdriver.chrome.driver", CHROME_DRIVER_FILE_LOCATION);
		ChromeOptions ops = new ChromeOptions();
		ops.addArguments("--remote-allow-origins=*");
		WebDriver driver = new ChromeDriver(ops);
		driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);

		try {
			driver.get(URL);
			Thread.sleep(SLEEP_DURATION);

			// Locate the assignment to be updated (you may need to locate it based on a unique identifier)
			WebElement assignmentElement = driver.findElement(By.id("editassignmentbutton")); // Change this selector accordingly
			assignmentElement.click();
			Thread.sleep(SLEEP_DURATION);

			WebElement assignmentbuttonElement = driver.findElement(By.id("editassignmentbutton2")); // Change this selector accordingly
			assignmentbuttonElement.click();
			Thread.sleep(SLEEP_DURATION);

			// Find the assignment name input field and update the assignment name
			WebElement assignmentNameElement = driver.findElement(By.id("newassignmentname"));
			assignmentNameElement.clear();
			assignmentNameElement.sendKeys(UPDATED_ASSIGNMENT_NAME);
			Thread.sleep(SLEEP_DURATION);

			// Locate and click the "Save" button to update the assignment
			driver.findElement(By.id("submitedit")).click();
			Thread.sleep(SLEEP_DURATION);

			// Assert that the updated assignment name is displayed on the page
			WebElement updatedAssignmentElement = driver.findElement(By.xpath("//table/tbody/tr[1]/td[1]"));
			assertEquals(UPDATED_NAME, updatedAssignmentElement.getText());

		} catch (Exception ex) {
			throw ex;
		} finally {
			driver.quit();
			Thread.sleep(5000);
		}
	}


	@Test
	public void deleteAssignmentTest() throws Exception {
		System.setProperty("webdriver.chrome.driver", CHROME_DRIVER_FILE_LOCATION);
		ChromeOptions ops = new ChromeOptions();
		ops.addArguments("--remote-allow-origins=*");
		WebDriver driver = new ChromeDriver(ops);
		driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);



		try {
			driver.get(URL);
			Thread.sleep(SLEEP_DURATION);
// Locate and click the "Add Assignment" button
			driver.findElement(By.id("addbutton1")).click();
			Thread.sleep(SLEEP_DURATION);

			// Locate and click the "Add Assignment" button
			driver.findElement(By.id("addbutton2")).click();
			Thread.sleep(SLEEP_DURATION);

			// Find the assignment name input field and enter a new assignment name
			WebElement assignmentNameElement = driver.findElement(By.id("addassignmentname"));
			assignmentNameElement.sendKeys(ADD_ASSIGNMENT_NAME);
			Thread.sleep(SLEEP_DURATION);

			WebElement assignmentIdElement = driver.findElement(By.id("addassignmentid"));
			assignmentIdElement.sendKeys(ADD_ASSIGNMENT_ID);

			WebElement assignmentDateElement = driver.findElement(By.id("addassignmentdate"));
			assignmentDateElement.sendKeys(ADD_ASSIGNMENT_DATE);


			// Locate and click the "Save" button to add the assignment
			driver.findElement(By.id("addbutton3")).click();
			Thread.sleep(SLEEP_DURATION);

			// Locate and click the "Delete" button
			WebElement deleteButtonElement = driver.findElement(By.xpath("//table/tbody/tr[5]/td[6]/button"));
			deleteButtonElement.click();
			Thread.sleep(SLEEP_DURATION);
			WebElement deletedAssignmentElement = null;
			try {
				 deletedAssignmentElement = driver.findElement(By.xpath("//table/tbody/tr[6]/td[1]"));
//				if (deletedAssignmentElement != null) {

//				}
			} catch (NoSuchElementException e) {

					// Handle the case where assignment is not found in row 6, column 1
					System.out.println("Assignment deleted");
//
			}
			assertNull(deletedAssignmentElement);



		} catch (Exception ex) {
			throw ex;
		} finally {
			driver.quit();
			Thread.sleep(5000);
		}
	}


}