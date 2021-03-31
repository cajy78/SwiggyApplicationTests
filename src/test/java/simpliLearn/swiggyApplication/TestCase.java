package simpliLearn.swiggyApplication;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.io.FileUtils;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;

import appium.AppiumSetup.DeviceTypes;
import appium.TestProperties;
import io.appium.java_client.android.AndroidDriver;

public class TestCase {

	private DeviceTypes deviceType;
	public AndroidDriver driver;

	public DeviceTypes initiateDeviceType() throws Throwable {
		String defaultTestDevice = TestProperties.getDefaultDeviceType();
		switch (defaultTestDevice) {
		case "Oneplus8T":
			deviceType = DeviceTypes.Oneplus8t_A11_swiggy;
			break;
		case "GalaxyA30s":
			deviceType = DeviceTypes.GalaxyA30s_swiggy;
			break;
		case "Android7emu":
			deviceType = DeviceTypes.Android7;
			break;
		default:
			throw new RuntimeException("DeviceType entered in configuration does not exist for this test environment.");
		}
		return deviceType;
	}

	public void takeScreenShot(String fileName, AndroidDriver driver) {
		File scrFile = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
		DateFormat dateFormat = new SimpleDateFormat("dd-MMM-yyyy__hh_mm_ssaa");
		String destFile = fileName + "-" + dateFormat.format(new Date()) + ".png";

		try {
			FileUtils.copyFile(scrFile, new File(TestProperties.getScreenshotDir() + "\\" + destFile));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
