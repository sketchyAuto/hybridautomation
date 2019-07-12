package com.base;

import org.apache.commons.io.*;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.openqa.selenium.*;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Set;
import static org.junit.jupiter.api.Assertions.assertTrue;


public abstract class BasePage {

    protected static final Logger logger = LogManager.getLogger("GLOBAL");

    protected WebDriver driver;
    WebElement element = null;

    public abstract String getSiteUrl();
    public abstract String getPageUrl();

    public BasePage(WebDriver driver){
        this.driver = driver;
    }

    public BasePage(){}

    public String getUrl(){
        return getSiteUrl()+getPageUrl();
    }

    public void get(){
        driver.get(getUrl());
    }

    //Locate and Click on an element using explicit wait of 30 seconds
    public void clickElement(By by){
        waitForElementToBeVisible(by);
        WebElement element = driver.findElement(by);
        element.click();
    }

    //Locate and click element no wait time
    public void clickElement(WebElement element){
        element.click();
    }

    public void fillTextField(By by, String value){
        waitForElement(by);
        WebElement element = driver.findElement(by);
        if(value.equals("")){
            element.clear();
        }else{
            element.sendKeys(value);
        }
    }

    //Clear the text field only
    public void clearTextField(By by){
        waitForElement(by);
        WebElement element = driver.findElement(by);
        element.clear();
    }

    //Check if an element is present by this By, searches for the given number of secondsToWait
    public boolean isElementPresent(By by, int secondToWait) {
        try {
            WebDriverWait wait = new WebDriverWait(driver, secondToWait);
            wait.until(ExpectedConditions.presenceOfElementLocated(by));
        } catch (NoSuchElementException e) {
            //if we exceptiones while trying to find the element, it wasn't present
            return false;
        } catch (TimeoutException e1) {
            return false;
        }
        //If we made it through without an exception, the element was present
        return true;
    }

    //Check if an element is present located by this By. Searches for 5 seconds
    public boolean isElementPresent(By by) {
        return isElementPresent(by, 5);
    }

    //Wait for element to exist located by this By. Will block and throw exception if element doesnot become visible
    public void waitForElement(By by) {
        WebDriverWait wait = new WebDriverWait(driver, 30);
        wait.until(ExpectedConditions.presenceOfElementLocated(by));
    }

    //Wait for element to be visible located By th by. Will block and throw exception if element does not become visible
    public void waitForElementToBeVisible(By by) {
        WebDriverWait wait = new WebDriverWait(driver, 30);
        wait.until(ExpectedConditions.presenceOfElementLocated(by));
        wait.until(ExpectedConditions.visibilityOfElementLocated(by));
    }

    //Form a By.xpath locator to select an element by HTML tag(e.g. dib) and the partial text contained in it
    public By formXpathBy(String htmlTag, String text) {
        return formXpathBy(htmlTag, text, true);
    }

    public By formXpathBy(String htmlTag, String text, boolean global) {
        String startLocation = "/";
        if (global) {
            startLocation = "//";
        }
        return By.xpath(startLocation + htmlTag + "[contains(.,'" + text + "')]");
    }

    //Find the first visible element by a given By
    public WebElement getVisibleElement(By by) {
        logger.info("Fing elements using: " + by.toString());
        ArrayList<WebElement> elements = (ArrayList<WebElement>) driver.findElements(by);
        logger.info("Found " + elements.size() + "matching elements");

        for (WebElement element : elements) {
            if (element.isDisplayed()) {
                return element;
            }
        }
        logger.error("No element was visible using locator: " + by.toString());
        return null;
    }

    public ArrayList<WebElement> getVisibleElements(By by) {
        logger.info("Finding elements using: " + by.toString());
        ArrayList<WebElement> elements = (ArrayList<WebElement>) driver.findElement(by);
        ArrayList<WebElement> visibleElements = new ArrayList<>(0);
        for (WebElement element : elements) {
            if (element.isDisplayed()) {
                visibleElements.add(element);
            }
        }
        logger.info("Found " + visibleElements.size() + " matching elements");
        return visibleElements;
    }

    //Check that an element is present over a period o time. Useful for situations where the text change dynamically,
    //If the method gets to the end without throwing an exception then the element was present each time it was located
    public boolean isElementPresentOverTime(By by, int seconds) {
        //Initial wait to find the element
        WebDriverWait wait = new WebDriverWait(driver, 30);
        wait.until(ExpectedConditions.presenceOfElementLocated(by));

        //loop X times and maker sure its still there
        int attempt = 0;

        while (attempt++ <= seconds) {
            try {
                logger.info("Searching for element with text");
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                break;
            }
            wait.until(ExpectedConditions.presenceOfElementLocated(by));
        }
        return true;
    }

    //Find all elements using given By. Will throw exception if no elements are found
    public ArrayList<WebElement> getElements(By by) {
        waitForElementToBeVisible(by);
        ArrayList<WebElement> elements = (ArrayList<WebElement>) driver.findElement(by);
        return elements;
    }

    //Get a text value of the element located by a given by
    public String getTextValue(By by) {
        int attempt = 0;
        int maxAttempts = 30;

        try {
            while (attempt++ < maxAttempts)
                waitForElementToBeVisible(by);
            WebElement element = driver.findElement(by);
            return element.getText();
        } catch (StaleElementReferenceException e) {
            logger.info("Element was stale, retrying");
            try {
                Thread.sleep(500);
            } catch (InterruptedException e1) {
                e1.printStackTrace();
            }
        }
        return "Was statle after retries";
    }

    //Select a value from a drop down box using the text displayed
    public void selectDropDownByVisibleText(By by, String visibleText) {
        waitForElementToBeVisible(by);
        Select dropDown = new Select(driver.findElement(by));
        By xppath = formXpathBy("option", visibleText);
        waitForElementToBeVisible(xppath);
        if (dropDown.getOptions().size() > 0) {
            dropDown.selectByVisibleText(visibleText);
        }
    }

    //Wait for element to be clickable. Will be block and thro an exception if element does not become clickable
    public void waitForElementToBeClickable(By by) {
        WebDriverWait wait = new WebDriverWait(driver, 30);
        wait.until(ExpectedConditions.presenceOfElementLocated(by));
        wait.until(ExpectedConditions.visibilityOfElementLocated(by));
        wait.until(ExpectedConditions.elementToBeClickable(by));
    }

    public void waitForElementToDisappear(By by) {
        WebDriverWait wait = new WebDriverWait(driver, 10);
        wait.until(ExpectedConditions.invisibilityOfElementLocated(by));
    }

    public void clickLink(By link) {
        isElementPresent(link, 15);
        try {
            setFocus(link);
        } catch (Exception e) {
            System.out.println("Failed to set Focus");
            WebDriverWait wait = new WebDriverWait(driver, 10);
            try {
                WebElement element = wait.until(ExpectedConditions.elementToBeClickable(link));
                element.click();
            } catch (Exception e1) {
                System.out.println("Failed to click using element, re-finding wit By");
                driver.findElement(link).click();
            }
        }
    }

    public void clickLink(WebElement link) {
        WebDriverWait wait = new WebDriverWait(driver, 7);
        WebElement element = wait.until(ExpectedConditions.elementToBeClickable(link));
        element.click();
    }

    public void setFocus(By element) {
        new Actions(driver).moveToElement(driver.findElement(element)).perform();
    }

    public String getText(By element) {
        isElementPresent(element, 25);
        return getText(driver.findElement(element));
    }

    public String getText(WebElement element) {
        String text = null;
        WebElement a = element;
        try {
            text = a.getText();
        } catch (Exception e) {
            System.out.println(e);
        }
        if (text == null || text.isEmpty()) {
            try {
                text = a.getAttribute("value");
            } catch (Exception e) {
                System.out.println(e);
            }
        }
        if (text == null || text.isEmpty()) {
            try {
                text = a.getAttribute("innerText");
            } catch (Exception e) {
                System.out.println(e);
            }
        }
        return text;
    }

    public void enterText(By field, String text) {
        isElementPresent(field);
        WebElement a = driver.findElement(field);

        try {
            a.click();
        } catch (Exception e) {
            System.out.println(e);
        }
        try {
            a.clear();
        } catch (Exception e) {
            System.out.println("Failed to clear");
        }
        if (getText(field).isEmpty()) {
            if (text.length() < 500) {
                a.sendKeys(text);
            } else {
                ((JavascriptExecutor) driver).executeScript("arguments[0].value= arguments[1];", a, text);
            }
        } else {
            a.sendKeys("");
            a.sendKeys(Keys.CONTROL, "a", Keys.DELETE);
            if (text.length() < 500) {
                a.sendKeys(text);
            } else {
                ((JavascriptExecutor) driver).executeScript("arguments[0].value = arguments[1];", a, text);
            }
        }
    }

    public boolean waitUntilElementHasAttribute(By element, String attribute, String expected, int timeout) {
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
        }
        isElementPresent(element, 20);
        WebDriverWait wait = new WebDriverWait(driver, timeout);
        if (attribute.trim().toLowerCase().equals("text".trim())) {
            wait.until(ExpectedConditions.textToBe(element, expected.trim()));
        } else {
            wait.until(ExpectedConditions.attributeContains(element, attribute, expected));
        }
        return true;
    }

    public boolean waitUntilElementHasAttribute(By element, String attribute, String expected) {
        return waitUntilElementHasAttribute(element, attribute, expected, 180);
    }

    public String getElementValue(By element, String attribute) {
        isElementPresent(element, 5);
        return driver.findElement(element).getAttribute(attribute);
    }

    public void hoverOnElement(By element) {
        WebElement web_Element_To_Be_Hovered = driver.findElement(element);
        Actions builder = new Actions(driver);
        builder.moveToElement(web_Element_To_Be_Hovered).perform();
    }

    //Selecting an element by visible text
    public void selectByVisibleText(By element,String text) {
        WebElement select1 = driver.findElement(element);
        Select select = new Select(select1);
        select.selectByVisibleText(text);
        System.out.println("Element selected: "+select);
    }

    public void acceptDialogue() {
        Alert alert = driver.switchTo().alert();
        String AlertText = alert.getText();
        System.out.println(AlertText);
        alert.accept();
    }

    //Ticking to accept a dialog box
    public void acceptDialogue(By element) {
        boolean a = false;
        try {
            acceptDialogue();
            a = true;
        } catch (Exception e) {
            a = false;
        } finally {
            if (!a) {
                clickLink(element);
            }
        }
    }

    //Drag and drop element on a page
    public void dragAndDrop(By drag, By drop) {
        dragAndDrop(drag, drop, 2000);
    }

    public void dragAndDrop(By drag, By drop, int pause) {
        Actions builder = new Actions(driver);
        builder.clickAndHold(driver.findElement(drag)).moveToElement(driver.findElement(drop)).perform();
        try {
            Thread.sleep(pause);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        builder.release(driver.findElement(drop)).build().perform();
    }

    //Finds the index of Strings in a list
    public int findIndextOfStringInList(java.util.List<String> itemList, String stringItem) {
        int index = 99;
        for (int i = 0; i < itemList.size(); i++) {
            if (itemList.get(i).equals(stringItem)) {
                index = i;
                break;
            }
        }
        System.out.println("Found string " + stringItem + " at index: " + index);
        return index;
    }

    //Gets the text from a list of elements
    public java.util.List<String> getTextFromListOfElements(java.util.List<WebElement> elements) {
        java.util.List<String> strings = new ArrayList<>();
        for (WebElement e : elements) {
            strings.add(getText(e));
        }
        return strings;
    }

    public void isTextPresent() {

    }

    //Refreshes the web page
    public void refreshPage() {
        driver.navigate().refresh();
    }

    //Explicit wait for Element to be ready to click
    public void isElementReady(By by, int timeout) {
        try {
            element = null;
            System.out.println("waiting for max " + timeout + " seconds for element to be clickable");
            WebDriverWait wait = new WebDriverWait(driver, 3);
            element = wait.until(ExpectedConditions.elementToBeClickable(by));
            System.out.println("Element clicked");
            element.click();
        } catch (Exception e) {
            System.out.println("Element not appearing on web page.");
        }
    }

    //Explicit wait for an element to be visible
    public void isElementVisible(By by) {
        try {
            element = null;
            System.out.println("Wait for max " + 10 + " second for element to be visible");
            WebDriverWait wait = new WebDriverWait(driver, 10);
            element = wait.until(ExpectedConditions.visibilityOfElementLocated(by));
            System.out.println("Element is visible");
            element.click();
        } catch (Exception e) {
            System.out.println("Element is not appearing");
        }
    }


    //Navigate to another page
    public void urlToNavigateTo(String urlText) {
        driver.navigate().to(urlText);
        String currentUrl = driver.getCurrentUrl();
        System.out.println("Navigated to: " + currentUrl);
    }

    //Checks the maximum length a text field holds
    public boolean checkMaxLengthCounter(By input, By counter, int maxLength) {
        enterText(input, "");
        assertTrue(Integer.valueOf(getText(counter)) == maxLength);
        WebElement a = driver.findElement(input);

        for (int i = 1; i <= maxLength; i++) {
            a.sendKeys("a");
            assertTrue(Integer.valueOf(getText(counter)) == (maxLength - i));
        }
        String b = RandomStringUtils.randomAlphanumeric(maxLength + 1);
        enterText(input, b);

        //System.out.println(getText(input));
        assertTrue(getText(input).length() == maxLength && getText(input).contains(b.substring(0, maxLength - 1)));
        return true;
    }

    //Checking Password Validation
    public boolean passwordValidation(By by, String password) {
        waitForElement(by);
        WebElement element = driver.findElement(by);
        element.sendKeys(password);

        if (password.length() > 8) {
            if (checkPassword(password)) {
                return true;
            } else {
                return false;
            }
        } else {
            System.out.println("Password not valid");
            return false;
        }
    }

    public boolean checkPassword(String password) {
        boolean hasNumber = false;
        boolean hasUpperCase = false;
        boolean hasLowerCase = false;
        char c;

        for (int i = 0; i < password.length(); i++) {
            c = password.charAt(i);
            if (Character.isDigit(c)) {
                hasNumber = true;
            } else if (Character.isUpperCase(c)) {
                hasUpperCase = true;
            } else if (Character.isLowerCase(c)) {
                hasLowerCase = true;
            }
            if (hasNumber && hasUpperCase && hasLowerCase) {
                return true;
            }
        }
        return false;
    }

    //Creating random strings
    public static String getRandomString(int length){
        String characters = "Intouch";
        char[] alphanumeric = (characters + characters.toUpperCase()+"0123456789").toCharArray();
        StringBuilder screenshots = new StringBuilder(length);
        for (int i=0; i<length; i++){
            screenshots.append(characters+alphanumeric[new Random().nextInt(alphanumeric.length)]);
        }
        return screenshots.toString();
    }

    //Taking Screenshot with random filename
    public void screenShot() throws IOException {
        String fileName = getRandomString(5)+".png";
        String directory = "C:\\Users\\pc\\Documents\\GenericFrameWork\\Screenshot\\";
        File sourceFile = ((TakesScreenshot)driver).getScreenshotAs(OutputType.FILE);
        FileUtils.copyFile(sourceFile, new File(directory + fileName));
    }

    //Switch to New Window, when the link opens a new window
    public void switchToThisPage(By by) {
        String parentHandle = driver.getWindowHandle();
        System.out.println("Parent Page: "+parentHandle);
        WebElement element = driver.findElement(by);
        element.click();

        Set<String > handles = driver.getWindowHandles();

        for(String handle: handles){
            System.out.println("Printing all Handles"+handle);
            if(!handle.equals(parentHandle)){
                driver.switchTo().window(handle);
            }
        }
    }

    public void iFrameElements(){
        driver.switchTo().frame("");
    }

    //Use after testing the Iframe and you want to go back to the default page
    public void backToDefault()
    {
        driver.switchTo().defaultContent();
    }

    //Used for sliders on the page
    public void slider(By by, int xOffset, int yOffset){
        WebElement element = driver.findElement(by);
        Actions action = new Actions(driver);
        action.dragAndDropBy(element,xOffset,yOffset).perform();
    }

    //Use for elements that have attributes when hovered on
    public void hoverOnElementAndClick(By by, By element){
        WebElement mainElement = driver.findElement(by);
        Actions action = new Actions(driver);
        action.moveToElement(mainElement).perform();
        waitForElement(by);
        WebElement subElement = driver.findElement(element);
        action.moveToElement(subElement).click().perform();
    }

    //Select buttons and RadioButtons
    public void selectAllbuttons(By by) throws InterruptedException {
        boolean isChecked = false;
        List<WebElement> buttons = driver.findElements(by);
        int size = buttons.size();
        System.out.println("Size of the list is: "+size);
        for(int i=0; i<size; i++){
            isChecked = buttons.get(i).isSelected();
            if(!isChecked){
                buttons.get(i).click();
                Thread.sleep(200);
            }
        }
    }

    //Select all values in a drop-down list
    public void selectElementsInDropDown(By by){
        WebElement element = driver.findElement(by);
        Select select = new Select(element);

        List<WebElement>options = select.getOptions();
        int size = options.size();
        for(int i=0; i<size; i++) {
            String optionName = options.get(i).getText();
            System.out.println("Option Name: "+optionName);
        }
    }


}
