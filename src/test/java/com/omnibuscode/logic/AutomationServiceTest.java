package com.omnibuscode.logic;

import org.junit.jupiter.api.Test;

public class AutomationServiceTest {

    @Test
    public void test() throws Exception {
        AutomationService as = new AutomationService();
        as.checkRules("", "1");
    }
    
    @Test
    public void testGetKeywordArray() {
        AutomationService as = new AutomationService();
        String[] elements = as.getKeywordArray("[\"피코크\",\"조선호텔\",\"김치\"]");
    
        // 결과 출력
        for (String element : elements) {
            System.out.println(element);
        }
    }

}
