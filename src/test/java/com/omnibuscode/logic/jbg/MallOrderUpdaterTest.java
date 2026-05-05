package com.omnibuscode.logic.jbg;

import java.io.File;
import java.util.List;

import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.omnibuscode.base.EnvSYS;
import com.omnibuscode.dao.FcmTokenDataAccessObject;
import com.omnibuscode.util.FcmNotificationUtil;
import com.omnibuscode.utils.FileUtil;
import com.omnibuscode.utils.JSONUtil;
import com.omnibuscode.utils.PropertiesUtil;

public class MallOrderUpdaterTest {

    public MallOrderUpdaterTest() {
        EnvSYS.SYS_RES_PATH = "D:/SVN/BOX_MANAGER/JINIEBOX/output/server/java/jiniebox/src/main/res/";
        PropertiesUtil.setPropertiesFilePath("src/main/res/JINIEBOX.PROPERTIES");
    }
    
    /**
     *  텍스트 파일로 저장한 아이템 수집 내용을 읽어서 DB에 저장하고 메세지를 전송
     * @throws Exception
     */
    @Test
    public void testUpdateItems() throws Exception {
//        fail("Not yet implemented");

        String seqStore = "1";
        String seqUser = "1";
        String seqBox = "1";
        String seqMall = "1";

        String eJson = FileUtil.readFile(new File("d:\\TEMP\\20240722.txt"));
        System.out.println("===========================================================================");
        System.out.println(JSONUtil.JsonEnterConvert(eJson));
        System.out.println("===========================================================================");
        ObjectMapper mapper = new ObjectMapper();
        JsonNode root = mapper.readTree(eJson);

        MallOrderUpdater mou = new MallOrderUpdater();
        JsonNode fcmMsges = mou.updateItems(root, seqStore, seqUser, seqMall, seqBox);
        
        JsonNode itemCnt = fcmMsges.get("item_count");
        JsonNode msgArr = fcmMsges.get("messages");
        
        String titleStr = "쇼핑몰에서 수집한 아이템이 총 '" + itemCnt + "'개 있습니다.";
        StringBuffer bodySb = new StringBuffer();
        
        if (msgArr.isArray()) {
            for (JsonNode msg : msgArr) {
                bodySb.append(msg.asText() + System.lineSeparator());
            }
        }
        
        FcmTokenDataAccessObject ftDao = new FcmTokenDataAccessObject();
        List<String> usrTokens = ftDao.getTokens(seqUser);
        
        if (usrTokens != null && usrTokens.size() > 0) {
            FcmNotificationUtil fcmNoti = FcmNotificationUtil.getInstance();
            fcmNoti.sendNotifications(usrTokens, titleStr, bodySb.toString());
        }
    }
}
