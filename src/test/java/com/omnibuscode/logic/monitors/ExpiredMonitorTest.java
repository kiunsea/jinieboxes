package com.omnibuscode.logic.monitors;

import org.junit.jupiter.api.Test;

import com.omnibuscode.base.EnvSYS;
import com.omnibuscode.logic.mon.ExpiredItemMonitor;
import com.omnibuscode.utils.PropertiesUtil;

public class ExpiredMonitorTest {
    
    public ExpiredMonitorTest() {
        EnvSYS.SYS_RES_PATH = "D:/SVN/BOX_MANAGER/JINIEBOX/output/server/java/jiniebox/src/main/res/";
        PropertiesUtil.setPropertiesFilePath("src/main/res/JINIEBOX.PROPERTIES");
    }
    
    @Test
    public void test() throws Exception {
//        fail("Not yet implemented");
        ExpiredItemMonitor em = new ExpiredItemMonitor();
        em.sendExpiredInfoMessage("1", "테스트 메세지");
    }

}
