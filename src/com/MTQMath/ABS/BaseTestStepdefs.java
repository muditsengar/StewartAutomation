package com.MTQMath.ABS;


import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.support.ui.Select;

import com.MTQMath.pages.base.BaseWebComponent;

import java.util.concurrent.TimeUnit;

import cucumber.annotation.Before;

public class BaseTestStepdefs extends BaseWebComponent{
	
	public static String courseName = "AutomatedCourse "+ System.currentTimeMillis();

	private static String logincssID = "#loginFormId>input[type='submit']"; 
	private static String newCourseCssID = ".stepContent>[target='_blank']";
	private static String createCoursecssID = ".courseManage>a:nth-child(1)";
	private static String newCourseID = "createNewCourse";
	private static String courseDropDown= "productISBN";
	private static String continuebtnclass = "button";
	private static String courseNameID = "courseName";
	private static String enddateID = "endDate";
	private static String startdateID = "beginDate";
	private static String creatCourseclass = "button";
	private static String enterID = "nb_enter";
	private static String regURLcssID = ".distributionOptions> [target='_blank']";
	private static String accesscodetxtboxID = "registerAccessCode";
	private static String registerkeycssID = ".viewDetailsBtn.register_button";
	private static String continueregistercssID = ".clearfix.buttons> #apliaContinueForm>a";
	private static String usermenucssID = ".user-menu-link.tb_item";
	private static String dashboardCheckboxcssID = "[name='dashboardCheckbox']";
	private static String dashboardCollapseicon = "#dashboard-settings>form>.subhead>.ng-isolate-scope>span:nth-child(1)";

	public static String env;
	public static PropertiesConfiguration envProperties;

	public static String mtEnv = "qaf";//qad/qaf/mtprod
	// QAF/QAD
//	private static String uName="AutotestQA_inst2014@qai.net";
//	private static String stud_uName = "AutotestQA_stud2014@qai.net";
	// For SA on Anderson
//	private static String uName="clhmqa_instr1@qai.com";
//	private static String stud_uName = "clhm_stur1@qai.com";
	//private static String stud_uName = "AutotestQA_stud2014@qai.net";
	//Production
	private static String uName="clhmqa_inst01@qai.com";
	private static String stud_uName = "Mindapp_stu1501@qai.com";
	//Cloud
//	private static String uName="test_inst_01@qai.com";
//	private static String stud_uName = "test_stu_n1@qai.com";
	 
	private static String password="A123456";
	public static String courseKey;
	public static String mtURL;
	public static String inst_mtURL;
	public static int jcount;
	public static Select se;
	
	public static void setUp() {
		setChrome();
		ssoLogin();
		creatCourse();
		selectCourse();
		navigateToLPN();
	}

	public static void setChrome(){
		env = System.getProperty("env", "prod");//stg/prod/cloud
		driver = new ChromeDriver();
		driver.manage().timeouts().implicitlyWait(120, TimeUnit.SECONDS);
		driver.get(url());
		driver.manage().window().maximize();
		System.setProperty("webdriver.chrome.driver","C://Users//muditsingh//Desktop//eclipse workspace//chromedriver.exe");
	}
	
	public static void launch_sso(){
		driver.get(url());
	}
	
	public static void creatCourse(){
		se = new Select(driver.findElement(By.cssSelector("#productISBN")));
		se.selectByValue("9781305876651"); // Stewart 
		System.out.println("Successfully selected title!!!!!");
		driver.findElement(By.cssSelector(createCoursecssID)).click();
		driver.findElement(By.id(newCourseID)).click();
		driver.findElement(By.className(continuebtnclass)).click();
		driver.findElement(By.id(courseNameID)).sendKeys(courseName);
		
		DateFormat dt = new SimpleDateFormat("M/d/yyyy");
		Date date = new Date();
		Calendar calendar = Calendar.getInstance();

		driver.findElement(By.id(startdateID)).sendKeys(dt.format(calendar.getTime()));

		calendar.add(Calendar.DATE,7);
		date = calendar.getTime();

		driver.findElement(By.id(enddateID)).sendKeys(dt.format(date));
		se = new Select(driver.findElement(By.cssSelector("#timeZone")));
		se.selectByValue("Asia/Calcutta");
		driver.findElement(By.className(creatCourseclass)).click();
	}
	
	public static void selectCourse(){
		String regURL = driver.findElement(By.cssSelector(regURLcssID)).getText();
		String[] splittedLink = regURL.split("/");
		courseKey = splittedLink[4];
		System.out.println("Course Name(Key): "+courseName+"("+courseKey+")");
		mtURL = driver.findElement(By.cssSelector(newCourseCssID)).getAttribute("href");
		System.out.println(mtURL);
	 	driver.manage().timeouts().implicitlyWait(120, TimeUnit.SECONDS);
	 	if (mtEnv.equals("mtprod") || env.equals("cloud")){
	 		inst_mtURL = mtURL;
	 		//inst_mtURL = mtURL.replaceAll("cloud-qap-ng", mtEnv);
			driver.get(inst_mtURL);
			System.out.println("mtprod url is = " + inst_mtURL);
		}
	 	
	 	if (mtEnv.equals("qaf") || env.equals("prod")){
	 		inst_mtURL = mtURL;
			driver.get(mtURL);
		}
		
		if (mtEnv.equals("qad")){
			inst_mtURL = mtURL.replaceAll("qaf.", mtEnv+"-");
			driver.get(mtURL.replaceAll("qaf.", mtEnv+"-"));		
		}
	}
	
	public static void navigateToLPN(){
		try{
			if(driver.findElement(By.id("nb_eula")).isDisplayed()){
				if(waitForElementToAppear(driver.findElement(By.cssSelector("#nb_eula> .buttons> .adminButton.accept")))){
					((JavascriptExecutor)driver).executeScript("document.getElementsByClassName('adminButton accept')[0].click();");
				}
			}
		}
			catch (Exception e){
				waitTillTime(1000);
			}

		if(driver.findElements(By.id(enterID)).size() !=0){
			driver.findElement(By.id(enterID)).click();
			((JavascriptExecutor)driver).executeScript("document.getElementsByClassName('close mxui_layout_positionable')[0].click();");
		}
	}


	public static void ssoLogin(){
		driver.findElement(By.id("emailId")).sendKeys(uName);
		driver.findElement(By.id("password")).sendKeys(password);
		driver.findElement(By.cssSelector(logincssID)).click();
	}
	
	public static void stud_ssoLogin(){
		driver.findElement(By.id("emailId")).sendKeys(stud_uName);
		driver.findElement(By.id("password")).sendKeys(password);
		driver.findElement(By.cssSelector(logincssID)).click();		
	}
	
	public static void join_course(){
		driver.findElement(By.id(accesscodetxtboxID)).sendKeys(courseKey);
		driver.findElement(By.cssSelector(registerkeycssID)).click();
		waitForElementToAppear(driver.findElement(By.cssSelector(continueregistercssID)));
		driver.findElement(By.cssSelector(continueregistercssID)).click();
		jcount = 1;
	}
	
	private static String url() {
		try {
			envProperties = new PropertiesConfiguration("environment.properties");
		} catch (ConfigurationException e) {
			e.printStackTrace();
		}
		String URL=envProperties.getString("url.sso."+env, envProperties.getString("url.sso."+ env));
		System.out.println(URL);
		return URL;
	}

	public static void tearDown() {
		driver.close();
	}

	@Before
	public void printClassName(){
		String className = this.getClass().getCanonicalName();
		System.out.println("Running Test:" + className);
	}
}
