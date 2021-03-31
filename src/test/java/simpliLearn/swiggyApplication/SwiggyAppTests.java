package simpliLearn.swiggyApplication;

import java.net.URL;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.openqa.selenium.By;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableMap;

import appium.AppiumSetup;
import appium.AppiumSetup.DeviceTypes;
import appium.TestProperties;
import io.appium.java_client.MobileElement;
import io.appium.java_client.TouchAction;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.touch.offset.PointOption;

public class SwiggyAppTests extends TestCase {

	private AndroidDriver driver;
	private DeviceTypes testDeviceType;
	private WebDriverWait wait;

	@BeforeTest
	public void setup() throws Throwable {
		testDeviceType = initiateDeviceType();
		DesiredCapabilities desiredCapabilities = AppiumSetup.getCapabilitiesOfDevice(testDeviceType);
		URL remoteUrl = new URL(TestProperties.getAppiumServerURL());
		driver = new AndroidDriver(remoteUrl, desiredCapabilities);
		driver.manage().timeouts().implicitlyWait(Integer.parseInt(TestProperties.getImplicitWaitTimeoutConfig()),
				TimeUnit.SECONDS);
		wait = new WebDriverWait(driver, Integer.parseInt(TestProperties.getExplicitWaitTimeoutConfig()));
	}

	@Test(priority = 1)
	@Parameters({ "searchType", "searchTypeValue" })
	public void runSearchTest(String searchType, String searchTypeValue) throws Throwable {

		if (!searchType.equalsIgnoreCase("resto") && !searchType.equalsIgnoreCase("restaurant")
				&& !searchType.equalsIgnoreCase("dish") && !searchType.equalsIgnoreCase("food")) {
			throw new RuntimeException(
					"Parameter passed for Search Type is incorrect and should be either Resto or Restaurant or Dish or Food (ignore case)");
		} else {

			Thread.sleep(5000);
			takeScreenShot("01_Splash_Screen", driver);
			MobileElement initialLocation = (MobileElement) driver
					.findElementById("in.swiggy.android:id/set_location_text");
			initialLocation.click();
			Thread.sleep(2000);
			takeScreenShot("02_Location_Permission_Popup", driver);

			switch (testDeviceType) {
			case GalaxyA30s_swiggy:
				MobileElement initLocationPermission_g = (MobileElement) driver
						.findElementById("com.android.permissioncontroller:id/permission_allow_foreground_only_button");
				initLocationPermission_g.click();
				break;
			case Oneplus8t_A11_swiggy:
				MobileElement initLocationPermission_op = (MobileElement) driver
						.findElementById("com.android.permissioncontroller:id/permission_allow_foreground_only_button");
				initLocationPermission_op.click();
				break;
			case Android7:
				MobileElement initLocationPermission_emu7 = (MobileElement) driver
						.findElementById("com.android.packageinstaller:id/permission_allow_button");
				initLocationPermission_emu7.click();
				MobileElement manualSearch_emu7 = (MobileElement) driver
						.findElementById("in.swiggy.android:id/dialog_negative_layout_text");
				manualSearch_emu7.click();
				MobileElement loc_emu7 = (MobileElement) driver
						.findElementById("in.swiggy.android:id/location_description");
				loc_emu7.click();
				loc_emu7.sendKeys("Thane");
				takeScreenShot("03_Location_emu_entered", driver);
				MobileElement selectLoc = (MobileElement) driver.findElementByXPath("//*[@text=\"Thane West\"]");
				selectLoc.click();
				break;
			default:
				break;
			}

			MobileElement confLocation = (MobileElement) driver
					.findElementById("in.swiggy.android:id/google_place_search_title_text1");
			takeScreenShot("04_Location_set", driver);
			confLocation.click();
			MobileElement initiateSearch = (MobileElement) driver
					.findElementById("in.swiggy.android:id/bottom_bar_explore");
			initiateSearch.click();
			MobileElement searchBar = (MobileElement) driver
					.findElementByXPath("//*[@text=\"Search for restaurants and food\"]");
			searchBar.click();
			takeScreenShot("05_Search_screen", driver);

			if (searchType.equalsIgnoreCase("Restaurant") || searchType.equalsIgnoreCase("Resto")) {
				searchBar.sendKeys(searchTypeValue);
				driver.executeScript("mobile:performEditorAction", ImmutableMap.of("action", "Search"));
				List<MobileElement> searchResults = driver
						.findElementsByXPath("/hierarchy/android.widget.FrameLayout/android.widget.LinearLayout"
								+ "/android.widget.FrameLayout/android.widget.LinearLayout/"
								+ "android.widget.FrameLayout/android.widget.FrameLayout/android.widget.FrameLayout/"
								+ "android.widget.RelativeLayout/android.widget.LinearLayout/android.widget.FrameLayout"
								+ "/android.widget.FrameLayout/android.widget.FrameLayout/android.widget.FrameLayout/android.widget.LinearLayout"
								+ "/androidx.recyclerview.widget.RecyclerView[2]/android.view.ViewGroup");
				System.out.println("Number of elements found: " + searchResults.size());
				takeScreenShot("06_Search_by_restaurant_results", driver);
				MobileElement restaurantSearch;

				if (searchResults.size() >= 1) {
					restaurantSearch = (MobileElement) searchResults.get(0);
					restaurantSearch.click();
					if (checkRatingDialogPopup()) {
						takeScreenShot("07_Rating_dialog_popup", driver);
						driver.navigate().back();
					}
					String restName = driver.findElementById("in.swiggy.android:id/restaurant_name").getText();
					if (restName.contains(searchTypeValue)) {
						takeScreenShot("08_Restaurant_search_completed", driver);
						driver.navigate().back();
						Assert.assertTrue(true);
					} else
						Assert.assertTrue(false);
				} else if (searchResults.size() < 1) {
					throw new RuntimeException("Search was incomplete or found no results");
				} else {
					throw new RuntimeException("Search was incomplete or found no results");
				}

			} else if (searchType.equalsIgnoreCase("Dish") || searchType.equalsIgnoreCase("Food")) {
				searchBar.sendKeys(searchTypeValue);
				driver.executeScript("mobile:performEditorAction", ImmutableMap.of("action", "Search"));
				List<MobileElement> searchResults = driver
						.findElementsByXPath("/hierarchy/android.widget.FrameLayout/android.widget.LinearLayout"
								+ "/android.widget.FrameLayout/android.widget.LinearLayout/"
								+ "android.widget.FrameLayout/android.widget.FrameLayout/android.widget.FrameLayout/"
								+ "android.widget.RelativeLayout/android.widget.LinearLayout/android.widget.FrameLayout"
								+ "/android.widget.FrameLayout/android.widget.FrameLayout/android.widget.FrameLayout/android.widget.LinearLayout"
								+ "/androidx.recyclerview.widget.RecyclerView[2]/android.view.ViewGroup");
				System.out.println("Number of elements found: " + searchResults.size());
				takeScreenShot("06_Search_by_dish_name_results", driver);

				if (searchResults.size() >= 1) {
					takeScreenShot("07_Dishes_found_successfully", driver);
					driver.navigate().back();
					Assert.assertTrue(true);
				} else if (searchResults.size() < 1) {
					throw new RuntimeException(
							"A problem occurred due to which no search results were found or search value has a problem");
				}
			}
		}
	}

	@Test(priority = 2)
	@Parameters({ "phoneNumber" })
	public void runLoginTest(String phoneNumber) throws Throwable {
		if (!checkPhoneNumber(phoneNumber) || phoneNumber.length() != 10) {
			throw new RuntimeException("Phone number entered is either invalid or is not 10 digits");
		} else {

			MobileElement account = (MobileElement) driver.findElementById("in.swiggy.android:id/bottom_bar_account");
			account.click();
			MobileElement loginButton = (MobileElement) driver.findElementByXPath("//*[@text=\"LOGIN\"]");
			loginButton.click();
			takeScreenShot("01_Login_screen", driver);
			driver.navigate().back();
			MobileElement number = (MobileElement) driver
					.findElementById("in.swiggy.android:id/loginCheckPhoneNumberEditText");
			number.click();
			number.sendKeys(phoneNumber);
			takeScreenShot("02_Phone_number_entered", driver);
			MobileElement continueLogin = (MobileElement) driver
					.findElementById("in.swiggy.android:id/loginCheckButton");
			continueLogin.click();
			Thread.sleep(2000);
			takeScreenShot("03_Waiting_OTP", driver);

			if (TestProperties.getDefaultDeviceType().equals("Oneplus8T")) {
				wait.until(ExpectedConditions
						.visibilityOfElementLocated(By.id("com.google.android.gms:id/positive_button")));
				takeScreenShot("03_OTP_Recevied_Android_System_automation", driver);
				MobileElement otpConfirmSubmit = (MobileElement) driver
						.findElementById("com.google.android.gms:id/positive_button");
				otpConfirmSubmit.click();
			} else {
				wait.until(
						ExpectedConditions.elementToBeClickable(By.id("in.swiggy.android:id/forgotPasswordSubmitBtn")));
				MobileElement otpSubmit = (MobileElement) driver
						.findElementById("in.swiggy.android:id/forgotPasswordSubmitBtn");
				takeScreenShot("04_OTP_entered_manually", driver);
				otpSubmit.click();
			}
			
			TouchAction action = new TouchAction(driver);
			action.longPress(PointOption.point(310, 1160)).moveTo(PointOption.point(310, 800)).release()
					.perform();
			action.longPress(PointOption.point(310, 800)).moveTo(PointOption.point(310, 1160)).release()
			.perform();
			wait.until(ExpectedConditions
					.elementToBeClickable(By.xpath("//android.view.ViewGroup[@content-desc=\"EDIT\"]")));
			MobileElement editDetails = (MobileElement) driver
					.findElementByXPath("//android.view.ViewGroup[@content-desc=\"EDIT\"]");
			takeScreenShot("05_Account_logged_in", driver);
			editDetails.click();
			MobileElement accountPhoneNumber = (MobileElement) driver
					.findElementById("in.swiggy.android:id/edit_account__phone_et");
			String loggedInUser = accountPhoneNumber.getText();
			System.out.println(loggedInUser);
			driver.navigate().back();
			Assert.assertEquals(loggedInUser, phoneNumber);
		}
	}

	@Test(priority = 3)
	@Parameters({ "orderFoodBy", "orderFood" })
	public void runBuyFoodTest(String orderFoodBy, String orderFood) throws Throwable {
		if (!orderFoodBy.equalsIgnoreCase("resto") && !orderFoodBy.equalsIgnoreCase("restaurant")
				&& !orderFoodBy.equalsIgnoreCase("dish") && !orderFoodBy.equalsIgnoreCase("food")) {
			throw new RuntimeException(
					"Parameter passed for Search Type is incorrect and should be either Resto or Restaurant or Dish or Food (ignore case)");
		} else {
			MobileElement initiateSearch = (MobileElement) driver
					.findElementById("in.swiggy.android:id/bottom_bar_explore");
			initiateSearch.click();
			MobileElement searchBar = (MobileElement) driver
					.findElementByXPath("//*[@text=\"Search for restaurants and food\"]");
			searchBar.click();
			takeScreenShot("06_SearchToBuy_clicked", driver);
			if (orderFoodBy.equalsIgnoreCase("Restaurant") || orderFoodBy.equalsIgnoreCase("Resto")) {
				searchBar.sendKeys(orderFood);
				driver.executeScript("mobile:performEditorAction", ImmutableMap.of("action", "Search"));
				List<MobileElement> searchResults = driver
						.findElementsByXPath("/hierarchy/android.widget.FrameLayout/android.widget.LinearLayout"
								+ "/android.widget.FrameLayout/android.widget.LinearLayout/"
								+ "android.widget.FrameLayout/android.widget.FrameLayout/android.widget.FrameLayout/"
								+ "android.widget.RelativeLayout/android.widget.LinearLayout/android.widget.FrameLayout"
								+ "/android.widget.FrameLayout/android.widget.FrameLayout/android.widget.FrameLayout/android.widget.LinearLayout"
								+ "/androidx.recyclerview.widget.RecyclerView[2]/android.view.ViewGroup");
				System.out.println("Number of elements found: " + searchResults.size());
				takeScreenShot("07_SearchToBuy_Restaurant_results", driver);
				MobileElement restaurantSearch;

				if (searchResults.size() >= 1) {
					restaurantSearch = (MobileElement) searchResults.get(0);
					restaurantSearch.click();
					if (checkRatingDialogPopup())
						driver.navigate().back();
					takeScreenShot("07_SearchToBuy_Restaurant_opened", driver);
					TouchAction action = new TouchAction(driver);
					action.longPress(PointOption.point(310, 1160)).moveTo(PointOption.point(310, 800)).release()
							.perform();
					takeScreenShot("08_SearchToBuy_Restaurant_Scrolled_down", driver);
					MobileElement addItem = (MobileElement) driver
							.findElementByXPath("//*[@content-desc=\"Add Item\"]");
					addItem.click();
					takeScreenShot("09_SearchToBuy_Restaurant_Add-item_tapped", driver);
					while (checkElementDisplayedById("in.swiggy.android:id/progressive_variants_continue_button")) {
						int ctr = 1;
						MobileElement continueButton = (MobileElement) driver
								.findElementById("in.swiggy.android:id/progressive_variants_continue_button");
						takeScreenShot(ctr + "_SearchToBuy_Restaurant_Item-Variant_continue", driver);
						continueButton.click();
						ctr = ctr + 1;
					}
					if (checkElementDisplayedByXpath("//*[@text=\"ADD ITEM\"]")) {
						MobileElement addToCart = (MobileElement) driver.findElementByXPath("//*[@text=\"ADD ITEM\"]");
						takeScreenShot("10_SearchToBuy_Restaurant_Item-variant_add", driver);
						addToCart.click();
						Thread.sleep(2000);
					}
					driver.navigate().back();
				}
			} else if (orderFoodBy.equalsIgnoreCase("Dish") || orderFoodBy.equalsIgnoreCase("Food")) {
				searchBar.sendKeys(orderFood);
				driver.executeScript("mobile:performEditorAction", ImmutableMap.of("action", "Search"));
				List<MobileElement> searchResults = driver
						.findElementsByXPath("/hierarchy/android.widget.FrameLayout/android.widget.LinearLayout"
								+ "/android.widget.FrameLayout/android.widget.LinearLayout/"
								+ "android.widget.FrameLayout/android.widget.FrameLayout/android.widget.FrameLayout/"
								+ "android.widget.RelativeLayout/android.widget.LinearLayout/android.widget.FrameLayout"
								+ "/android.widget.FrameLayout/android.widget.FrameLayout/android.widget.FrameLayout/android.widget.LinearLayout"
								+ "/androidx.recyclerview.widget.RecyclerView[2]/android.view.ViewGroup");
				System.out.println("Number of elements found: " + searchResults.size());
				takeScreenShot("07_SearchToBuy_Dish_results", driver);
				MobileElement addItem = (MobileElement) driver.findElementByXPath("//*[@content-desc=\"Add Item\"]");
				addItem.click();
				takeScreenShot("08_SearchToBuy_Dish_Add-Item_tapped", driver);
				while (checkElementDisplayedById("in.swiggy.android:id/progressive_variants_continue_button")) {
					int ctr = 1;
					MobileElement continueButton = (MobileElement) driver
							.findElementById("in.swiggy.android:id/progressive_variants_continue_button");
					takeScreenShot(ctr + "_SearchToBuy_Dish_Item-Variant_continue", driver);
					continueButton.click();
					ctr = ctr + 1;
				}
				if (checkElementDisplayedByXpath("//*[@text=\"ADD ITEM\"]")) {
					MobileElement addToCart = (MobileElement) driver.findElementByXPath("//*[@text=\"ADD ITEM\"]");
					takeScreenShot("09_SearchToBuy_Dish_Item-variant_add", driver);
					addToCart.click();
				}
			}

			MobileElement viewCart = (MobileElement) driver
					.findElementById("in.swiggy.android:id/bottom_bar_cart_title");
			viewCart.click();
			if (checkElementDisplayedByXpath("//*[contains(@text,\"SELECT ADDRESS\")]")) {
				MobileElement selectAddress = (MobileElement) driver
						.findElementByXPath("//*[contains(@text,\"SELECT ADDRESS\")]");
				takeScreenShot("12_Select-Address_Displayed", driver);
				selectAddress.click();
				MobileElement addressLine = (MobileElement) driver
						.findElementById("in.swiggy.android:id/cartAddressLineOne");
				addressLine.click();
			}

			MobileElement proceedToPay = (MobileElement) driver.findElementByXPath("//*[contains(@text, 'PAY')]");
			takeScreenShot("11_View-Cart", driver);
			proceedToPay.click();
			if (!presenceWaitByXPath("//*[contains(@text, 'BILL TOTAL')]")) {
				takeScreenShot("12_Cancellation_Policy_Window", driver);
				(new TouchAction(driver)).tap(PointOption.point(517, 2207)).perform();
			}
			MobileElement billTotal = (MobileElement) driver.findElementByXPath("//*[contains(@text, 'BILL TOTAL')]");
			System.out.println("Total Bill to be paid is: " + billTotal.getText());
			takeScreenShot("13_Payment_Method_Section", driver);
			if (checkElementDisplayedByXpath("//*[contains(@text,\"PREFERRED PAYMENT\")]")) {
				Assert.assertTrue(true);
			} else {
				throw new RuntimeException("Failed to reach the payment details and confirmation page");
			}
		}
	}

	public boolean checkRatingDialogPopup() {
		try {
			driver.findElementById("in.swiggy.android:id/dialog_content");
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	private boolean checkElementDisplayedById(String id) {
		try {
			driver.findElementById(id);
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	private boolean presenceWaitByXPath(String xpath) {
		try {
			WebDriverWait cancelPolicyWait = new WebDriverWait(driver, 4);
			cancelPolicyWait.until(ExpectedConditions.presenceOfElementLocated(By.xpath(xpath)));
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	private boolean checkElementDisplayedByXpath(String xpath) {
		try {
			driver.findElementByXPath(xpath);
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	private boolean checkPhoneNumber(String phoneNumber) {
		try {
			Long.parseLong(phoneNumber);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	@AfterTest
	public void tearDown() throws Throwable {
		if (!driver.equals(null))
			driver.quit();
	}
}
