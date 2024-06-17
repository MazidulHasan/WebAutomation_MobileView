package com.mobileresponsivetesting;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.relevantcodes.extentreports.ExtentReports;
import com.relevantcodes.extentreports.ExtentTest;
import com.relevantcodes.extentreports.LogStatus;

import io.github.bonigarcia.wdm.WebDriverManager;

public class ResponsivenessCheck {
    static ExtentTest graphicalTest;
    static ExtentReports graphicalReport;

    @BeforeSuite
    public void beforeSuiteStart(){
        graphicalReport = new ExtentReports("D:/Automation/mobileresponsivetesting/ExtentReport/GraphicalResults.html", true);
    }

    @BeforeMethod
    public void BeforeMethod(Method tesMethod){
        graphicalTest = graphicalReport.startTest(tesMethod.getName());
    }

    @DataProvider
    public Object[][] emulationSize(){
        return new Object[][]{
            {"Pixel 7",412,915},
            {"iPhone SE",375,667},
            {"iPad Air",768,1024}, //Portrait Mode
            {"iPad Air",1024,768} //Landscape Mode
        };
    }

    @Test(dataProvider = "emulationSize")
    public void checkResponsiveness(String emulation,int width, int height) throws IOException{
        WebDriverManager.chromedriver().setup();
        Map<String, String> deviceEmu = new HashMap<>();
        deviceEmu.put("deviceName", emulation);
        ChromeOptions chromeOptions = new ChromeOptions();
        chromeOptions.setExperimentalOption("mobileEmulation", deviceEmu);
        WebDriver driver = new ChromeDriver(chromeOptions);
        Dimension dim = new Dimension(width, height);
        driver.manage().window().setSize(dim);

        driver.get("https://derivfe.github.io/qa-test/settings");

        graphicalTest.log(LogStatus.PASS, graphicalTest.addScreenCapture(screenCapture(driver))+"  Test begin with  "+emulation);
        
        List<WebElement> inputFields = new ArrayList<>();
        inputFields.add(driver.findElement(By.id("fname")));
        inputFields.add(driver.findElement(By.id("lname")));
        inputFields.add(driver.findElement(By.id("dob")));
        inputFields.add(driver.findElement(By.id("address")));
        inputFields.add(driver.findElement(By.id("city")));
        inputFields.add(driver.findElement(By.id("state")));
        inputFields.add(driver.findElement(By.id("zip")));
        inputFields.add(driver.findElement(By.id("tel")));
        inputFields.add(driver.findElement(By.id("email")));
        inputFields.add(driver.findElement(By.id("email")));
        inputFields.add(driver.findElement(By.id("website")));
        

        for (WebElement field : inputFields) {
            validateField(field);
        }

        
        graphicalTest.log(LogStatus.PASS, graphicalTest.addScreenCapture(screenCapture(driver))+"  Test done with  "+emulation);
        driver.close();
    }

    private static void validateField(WebElement field) {
        boolean isVisible = field.isDisplayed();
        boolean isClickable = field.isEnabled();
        field.sendKeys("Demo");

        String label = field.findElement(By.xpath("preceding-sibling::label")).getText();

        if (isVisible && isClickable) {
            graphicalTest.log(LogStatus.PASS, "Input Field: " + label + " - Visible and Clickable");
        } else {
            graphicalTest.log(LogStatus.FAIL, "Input Field: " + label + " - Not Visible or Not Clickable");
        }
    }

    @AfterMethod
    public void afterEachTest(){
        graphicalReport.endTest(graphicalTest);
        graphicalReport.flush();
    }

    public static String screenCapture(WebDriver driver) throws IOException{
        TakesScreenshot takeSS = (TakesScreenshot) driver;

        File sourceOutputFile = takeSS.getScreenshotAs(OutputType.FILE);
        String time = String.valueOf(System.currentTimeMillis());
        File Dest = new File("./Screenshot/fullPage"+time+".png");
        FileUtils.copyFile(sourceOutputFile, new File("./Screenshot/fullPage"+time+".png"));
        String errFilePath = Dest.getAbsolutePath();
        return errFilePath;
    }
}
