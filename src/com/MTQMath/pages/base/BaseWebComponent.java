package com.MTQMath.pages.base;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Set;

import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.io.FileUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.WebDriverWait;

public class BaseWebComponent {

	public static WebDriver driver;
	public PropertiesConfiguration searchProperties;
	public PropertiesConfiguration envProperties;
	public static int waitTime = 120000;
	private String mainWindowHandle=driver.getWindowHandle();

	protected void switchDriverToPopUp() {
		  String mainWindowHandle = driver.getWindowHandle();
		  String popupWindowHandle = null;
		  Set<String> windowHandles = driver.getWindowHandles();
		  for (String windowHandle : windowHandles) {
		   if (!windowHandle.equals(mainWindowHandle)) {
		    popupWindowHandle = windowHandle;
		   }
		  }
		  if (null == popupWindowHandle) {
		   throw new IllegalStateException("Could not find the popup window handle for ");
		  }
		  driver.switchTo().window(popupWindowHandle);
		 }

	public String takeActualScreenShot(String pageName){
		Date date=new Date();
  		String currentDate=Integer.toString(date.getDate());
  		String currentTime=Integer.toString(date.getHours())+Integer.toString(date.getSeconds());
  		String currentDateTime=currentDate+currentTime;
		File scrFile = ((TakesScreenshot)driver).getScreenshotAs(OutputType.FILE);
		try {
			FileUtils.copyFile(scrFile, new File("images\\actual\\"+pageName+currentDateTime+"screenshot.jpg"));
			System.out.println("images\\actual\\"+pageName+currentDateTime+"screenshot.jpg");
			return "images\\actual\\"+pageName+currentDateTime+"screenshot.jpg";
		} catch (IOException e) {
			System.out.println("File not found");
		}
		return "";
	}

	public String takeExpectedScreenShot(String pageName){
		Date date=new Date();
  		String currentDate=Integer.toString(date.getDate());
  		String currentTime=Integer.toString(date.getHours())+Integer.toString(date.getSeconds());
  		String currentDateTime=currentDate+currentTime;
		File scrFile = ((TakesScreenshot)driver).getScreenshotAs(OutputType.FILE);
		try {
			FileUtils.copyFile(scrFile, new File("images\\expected\\"+pageName+currentDateTime+"screenshot.jpg"));
		return "images\\expected\\"+pageName+currentDateTime+"screenshot.jpg";
		} catch (IOException e) {
			System.out.println("File not found");
		}
		return "";
	}
	public void switchToMainWindow(){
		if(!driver.getWindowHandle().equals(mainWindowHandle)){
			driver.close();
			driver.switchTo().window(mainWindowHandle);
		}
	}

	public void switchToPopUpWindow(){
		Set<String>allWindowHandle = driver.getWindowHandles();
		for(String window : allWindowHandle){
			if(!window.equals(mainWindowHandle)){
				driver.switchTo().window(window);
				break;
			}
		}
	}

	public void switchToOtherWindow(){
		if(driver.getWindowHandle().equals(mainWindowHandle)){
			driver.switchTo().window(mainWindowHandle);
		}
	}

	public String replaceCssWithIndex(String cssPath,String xIndex){
		String locString = cssPath;
		return locString.replace("%", xIndex);
	}
	public String replaceCssWithIndex(String cssPath,String xIndex,String secIndex){
		String locString = cssPath;
		locString =locString.replace("%", xIndex);
		return locString.replace("$", secIndex);
	}
	public static WebElement findElementByXpath(String elementUid){
		WebElement searchIndex = (WebElement) driver.findElement(By.xpath(elementUid));
		return searchIndex;
	}
	public WebElement findElementById(String elementUid){
		WebElement searchIndex = (WebElement) driver.findElement(By.id(elementUid));
		return searchIndex;
	}
	public WebElement findElementById(String elementUid,String index){
		elementUid = elementUid.replace("%", index);
		WebElement searchIndex = (WebElement) driver.findElement(By.id(elementUid));
		return searchIndex;
	}
	public WebElement findElementByName(String elementUid){
		WebElement searchIndex = (WebElement) driver.findElement(By.name(elementUid));
		return searchIndex;
	}
	public WebElement findElementByClass(String elementUid){
		WebElement searchIndex = (WebElement) driver.findElement(By.className(elementUid));
		return searchIndex;
	}
	public WebElement findElementByCssPath(String elementUid){
		WebElement searchIndex = (WebElement) driver.findElement(By.cssSelector(elementUid));
		return searchIndex;
	}
	public WebElement findElementByCssPath(String elementUid, String cssIndex){
		WebElement searchIndex = (WebElement) driver.findElement(By.cssSelector(replaceCssWithIndex(elementUid, cssIndex)));
		return searchIndex;
	}
	public WebElement findElementByCssPath(String elementUid, String cssIndex,String secIndex){
		WebElement searchIndex = (WebElement) driver.findElement(By.cssSelector(replaceCssWithIndex(elementUid, cssIndex,secIndex)));
		return searchIndex;
	}
	
	public String getStandardWaitTime() {
		return envProperties.getString("STANDARD_PAGE_LOAD_WAIT_TIME");
	}
	public void sleep(int millis) {
		try {
			Thread.sleep(millis);
		} catch (InterruptedException e) {
		}
	}
	public void doHoverOverElementByID(String elementID) {
		WebElement element = driver.findElement(By.id(elementID));
		doHoverOverElement(element);
	}
	public void doHoverOverElement(WebElement element) {
		Actions builder = new Actions(driver);
		builder.moveToElement(element).build().perform();
	}
	public void waitForElementToLoad(By seleniumFindExpression, long i) {

		long now = new Date().getTime();
		long endTime = now + i;

		while (now < endTime) {

			try {
				driver.findElement(seleniumFindExpression);
			} catch (NoSuchElementException e) {
				now = new Date().getTime();

				try {
					Thread.sleep(500);
				} catch (InterruptedException ignored) {
				}

				continue;
			}
			break;
		}
		if (now > endTime) {
			throw new IllegalStateException("could not find element " + seleniumFindExpression.toString() + " within " + i + "ms.");
		}
	}
	protected void waitForAjaxLoadAndVerifyByElementId(final String expectedElement) {
		try {
			(new WebDriverWait(driver, 5)).until(new ExpectedCondition<Boolean>() {
				public Boolean apply(WebDriver arg0) {
					return driver.findElement(By.id(expectedElement)) != null;
				}
			});
		} catch (TimeoutException ex) {
			throw new TimeoutException("Expected element " + expectedElement + " did not load.", ex);
		}
	}
	protected void waitForAjaxLoadAndVerifyByCss(final String expectedElement) {
		try {
			(new WebDriverWait(driver, 5)).until(new ExpectedCondition<Boolean>() {
				public Boolean apply(WebDriver arg0) {
					return driver.findElement(By.cssSelector(expectedElement)) != null;
				}
			});
		} catch (TimeoutException ex) {
			throw new TimeoutException("Expected element " + expectedElement + " did not load.", ex);
		}
	}
	public void switchBackToMainWindow() {
		driver.switchTo().window(mainWindowHandle);
	}
	@SuppressWarnings("unchecked")
	public WebElement findElementBySizzleCss(String using) {
        injectSizzleIfNeeded();
        String javascriptExpression = createSizzleSelectorExpression(using);
        List<WebElement> elements = (List<WebElement>) ((JavascriptExecutor) driver).executeScript(javascriptExpression);
        if (elements.size() > 0)
            return elements.get(0);
        return null;
    }
	@SuppressWarnings("unchecked")
	public WebElement findElementBySizzleCss(String using,String Index) {
		using = using.replace("%", Index);
        injectSizzleIfNeeded();
        String javascriptExpression = createSizzleSelectorExpression(using);
        List<WebElement> elements = (List<WebElement>) ((JavascriptExecutor) driver).executeScript(javascriptExpression);
        if (elements.size() > 0)
            return elements.get(0);
        return null;
    }
    @SuppressWarnings("unchecked")
	public List<WebElement> findElementsBySizzleCss(String using) {
        injectSizzleIfNeeded();
        String javascriptExpression = createSizzleSelectorExpression(using);
        return (List<WebElement>) ((JavascriptExecutor) driver).executeScript(javascriptExpression);
    }

    private String createSizzleSelectorExpression(String using) {
        return "return Sizzle(\"" + using + "\")";
    }

    private void injectSizzleIfNeeded() {
        if (!sizzleLoaded())
            injectSizzle();
    }

    public Boolean sizzleLoaded() {
        Boolean loaded;
        try {
            loaded = (Boolean) ((JavascriptExecutor) driver).executeScript("return Sizzle()!=null");
        } catch (WebDriverException e) {
            loaded = false;
        }
        return loaded;
    }

    public void injectSizzle() {
        ((JavascriptExecutor) driver).executeScript(" var headID = document.getElementsByTagName(\"head\")[0];"
                + "var newScript = document.createElement('script');"
                + "newScript.type = 'text/javascript';"
                + "newScript.src = 'https://raw.github.com/jquery/sizzle/master/sizzle.js';"
                + "headID.appendChild(newScript);");
    }
    

public static boolean waitForElementToAppear(WebElement element) {
		long endTime = System.currentTimeMillis() + waitTime;
		
		while (System.currentTimeMillis() < endTime) {
			
			try {
				if (element.isDisplayed()) {
					return true;
				}
			} catch (Exception e) {
				waitTillTime(1000);
			}
		}
		return false;
	}
	
	public static void waitTillTime(int wTime) {
		try {
			Thread.sleep(wTime);
		} catch (Exception ex) {
		}
	}
}
