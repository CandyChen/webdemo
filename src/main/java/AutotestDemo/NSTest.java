package AutotestDemo;


import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;
import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.testng.Assert;
import org.testng.TestListenerAdapter;
import org.testng.TestNG;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;


public class NSTest {

	private static WebDriver driver;
	private static String baseURL="http://www.jumei.com/i/account/login";
	private static String email="autotestno@163.com";
	private static String password="123qwe";
	private static String goodName="MG";
	
	private static Logger logger = Logger.getLogger(NSTest.class);
	
	@BeforeTest
	public void start(){
		//System.setProperty("webdriver.chrome.driver", "driver/chromedriver.exe");
		//driver=new ChromeDriver();
		driver = new FirefoxDriver();
		driver.manage().window().maximize();
	}
	
	public static void main(String[] args) {
		TestListenerAdapter tla = new TestListenerAdapter();
		TestNG testng = new TestNG();
		testng.setTestClasses(new Class[] { NSTest.class });
		testng.addListener(tla);
		testng.run(); 
	}
	
	@Test
	public void login(){
		driver.get(baseURL);
		logger.info(driver.getTitle());
		
		driver.findElement(By.name("email")).clear();
		driver.findElement(By.name("email")).sendKeys(email);
		
		driver.findElement(By.name("password")).clear();
		driver.findElement(By.name("password")).sendKeys(password);
		
		driver.findElement(By.className("loginbtn")).click();
		driver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);
		//check points 
		Assert.assertTrue(isElementExsit(driver,By.className("new_user_login")));
		logger.info("登录成功，用户信息已显示");
	}
	
	
	@Test(dependsOnMethods={"login"})
	public void bugGoods(){
		
		logger.info(driver.getCurrentUrl());
		
		//driver.findElement(By.linkText("美妆商城"));
		driver.findElement(By.cssSelector("div.new_user_menu > div:nth-child(2) > a")).click();;
		
		
		//检查搜索框是否存在
		Assert.assertTrue(isElementExsit(driver, By.className("top_search_wrap")));
		
		driver.findElement(By.className("top_search_input")).clear();
		driver.findElement(By.className("top_search_input")).sendKeys(goodName);
		
		driver.findElement(By.id("btn_global_search")).click();
		
		//搜索结果产品列表
		List<WebElement> elements=driver.findElements(By.className("item"));
		
		logger.info(elements.size());
		
		if(isElementExsit(driver, By.className("search_footer.not_found"))){
			logger.info("当前搜索没有结果显示");
			System.exit(-1);
		}else{
			//选择第一个
			elements.get(0).click();
			driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
			
			//打开产品详细页面
			logger.info(driver.getCurrentUrl());
			
			String currentHandle = driver.getWindowHandle();  
	        Set<String> handles = driver.getWindowHandles();
	        
	        for(String handle:handles){
	        	if(handle.equals(currentHandle)) continue;
	        	driver.switchTo().window(handle);
	        }
			//check points 产品title是否存在
	        Assert.assertTrue(isElementExsit(driver, By.className("share_title")));
	        
	        //先清空购物车
	        if(isElementExsit(driver, By.className("jmb-cart-bag"))){
	        	String goodUrl=driver.getCurrentUrl();
	        	driver.findElement(By.id("cart")).click();
	        	driver.findElement(By.className("clean_cart")).click();
	        	Alert alert=driver.switchTo().alert();
	        	alert.accept();
	        	driver.get(goodUrl);
	        }
	        
	        driver.findElement(By.className("newdeal_deal_buyit"));
			driver.findElement(By.id("go_to_cart")).click();
			
			//点击购物车，进行结算
			driver.findElement(By.id("cart")).click();
		}
		
	}
	
	@Test(dependsOnMethods={"login","bugGoods"})
	public void settle(){
		
		List<WebElement> goods=driver.findElements(By.className("cart_item"));
		goods.get(0).findElement(By.className("increase_num")).click();
		
		driver.findElement(By.id("go_to_order")).click();
		
		//check point
		Assert.assertTrue(isElementExsit(driver, By.id("cart")));
		if(!isElementExsit(driver, By.className("address_more"))){
			driver.findElement(By.className("address_more")).click();
			//输入收货人地址
			driver.findElement(By.id("receiver_name_new")).sendKeys("autoTest");
			
			Select province=(Select)driver.findElement(By.id("province_new"));
			province.selectByValue("北京市");
			
			Select city=(Select)driver.findElement(By.id("city"));
			province.selectByValue("市辖区");
			
			Select county=(Select)driver.findElement(By.id("county"));
			province.selectByValue("东城区");
			
			driver.findElement(By.id("address_new")).sendKeys("东直门南大街11号中汇广场B座23层");
			
			driver.findElement(By.id("hp_new")).sendKeys("13012345678");
			
			driver.findElement(By.id("new_address_submit_new")).click();
		}
		
		//确认订单
		driver.findElement(By.className("btn_pink_big")).click();
		
		Assert.assertTrue(driver.getTitle().contains("请尽快付款"));
		
	}
	
	public boolean isElementExsit(WebDriver driver, By locator) {  
        boolean flag = false;  
        try {  
            WebElement element=driver.findElement(locator);  
            flag=null!=element;  
        } catch (NoSuchElementException e) {  
            logger.info("Element:" + locator.toString()  
                    + " is not exsit!");  
        }  
        return flag;  
    } 
	
	@AfterTest
	public void end(){
		Set<String> handles=driver.getWindowHandles();
		for(String handle:handles){
			driver.switchTo().window(handle);
			driver.close();
		}
	}
}
