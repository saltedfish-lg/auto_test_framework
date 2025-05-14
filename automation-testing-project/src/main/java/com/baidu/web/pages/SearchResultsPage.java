package com.baidu.web.pages;

import com.baidu.utils.ElementActions;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class SearchResultsPage {

    private WebDriver driver;
    private By resultsLocator = By.id("content_left");

    public SearchResultsPage(WebDriver driver) {
        this.driver = driver;
    }

    public String getSearchResultsText() {
        return ElementActions.getText(driver, resultsLocator);
    }

    public By getResultsLocator() {
        return resultsLocator;
    }
}
