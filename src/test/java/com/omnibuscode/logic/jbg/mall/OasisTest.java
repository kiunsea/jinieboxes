package com.omnibuscode.logic.jbg.mall;

import org.json.simple.JSONArray;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.WebDriver;

import com.omnibuscode.logic.jbg.util.WebDriverManager;
import com.omnibuscode.utils.PropertiesUtil;

public class OasisTest {

    public OasisTest() {
        PropertiesUtil.setPropertiesFilePath("src/main/java/res/JINIEBOX.PROPERTIES");
    }
    
    @Test
    public void navigatePurchasedTest() {
        Oasis oasis = new Oasis("kiunsea", "Wtbtsaas7684@");
        WebDriverManager wdm = new WebDriverManager();
        WebDriver driver = wdm.getWebDriver(WebDriverManager.BROWSER_NAME_EDGE);
        if (oasis.signin(driver)) {
            JSONArray resJoArr = oasis.navigatePurchased(driver);
            System.out.println(resJoArr.toJSONString());
        }
        
        oasis.signout(driver);
        // 드라이버 종료
        driver.quit();
    }
    
//    @Test
//    public void signinTest() {
//        OasisBak oasis = new OasisBak("kiunsea", "Wtbtsaas7684@");
//        WebDriverManager wdm = new WebDriverManager();
//        WebDriver driver = wdm.getWebDriver(WebDriverManager.BROWSER_NAME_CHROME);
//        assertEquals(oasis.signin(driver), true);
//        
//        oasis.signout(driver);
//        // 드라이버 종료
//        driver.quit();
//    }

}
