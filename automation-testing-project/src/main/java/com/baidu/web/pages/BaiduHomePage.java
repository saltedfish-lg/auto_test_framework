package com.baidu.web.pages;

import com.baidu.utils.ElementActions;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class BaiduHomePage {

    private WebDriver driver;
    private By searchBoxLocator = By.id("kw");
    private By searchButtonLocator = By.id("su");

    public BaiduHomePage(WebDriver driver) {
        this.driver = driver;
    }

    public void open() {
        driver.get("https://www.baidu.com");
    }

    public void enterSearch(String keyword) {
        ElementActions.type(driver, searchBoxLocator, keyword);
    }

    public void submitSearch() {
        ElementActions.click(driver, searchButtonLocator);
    }
}
