package com.base;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public class JavaScriptBase {
    protected WebDriver driver;
    WebElement element = null;
    private JavascriptExecutor js;


    //Not really needed
    public void jsSetup(){
        js = (JavascriptExecutor) driver;
        js.executeScript("window.location = 'http://automationpractice.com';");
    }

    public void jsFindElemSendkey(){
        WebElement element = (WebElement) js.executeScript("return document.getElementById('elementwewant');");
        element.sendKeys("");
    }

    public void jsClickElement(By by){
        WebElement element = driver.findElement(by);
        js.executeScript("arguments[0].click();", element);
    }

    public void jsSetFocus(By by){
        WebElement element = driver.findElement(by);
        ((JavascriptExecutor)driver).executeScript("arguments[0].focus();",element);
    }

    public void jsScrolltoView(By by){
            //scrollin down
            //js.executeScript("window.scrollBy(0,1900);");

            //Scrolling Up
            // js.executeScript("window.scrollBy(0,-1900);");

        //Scroll element to view
        WebElement element = driver.findElement(by);
        js.executeScript("arguments[0].scrollIntoView(true", element);
        js.executeScript("window.ScrollBy(0,-190");
        }

}
