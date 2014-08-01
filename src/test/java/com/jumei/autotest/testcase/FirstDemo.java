package com.jumei.autotest.testcase;

import java.io.File;
import java.io.IOException;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

public class FirstDemo {
	WebDriver driver;
	
	@BeforeClass
	public void start(){
		File file=new File("source/driver/firebug-2.0.2-fx.xpi");
		FirefoxProfile profile=new FirefoxProfile();
		try {
			profile.addExtension(file);
		} catch (IOException e) {
			e.printStackTrace();
		}
		profile.setPreference("extensions.firebug.currentVersion", "2.0.2");
		
		driver=new FirefoxDriver(profile);
	}
	
	@Test
	public void openUrl(){
		driver.get("http://www.baidu.com");
		System.out.println(driver.getTitle());
	}
	
	@AfterClass
	public void tearDown(){
		driver.quit();
	}

}
