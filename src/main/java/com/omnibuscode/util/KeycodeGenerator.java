package com.omnibuscode.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.omnibuscode.utils.ExceptionUtil;
import com.omnibuscode.utils.FileUtil;
import com.omnibuscode.utils.StringUtil;

public class KeycodeGenerator {

    /**
     * 서버 환경 변경시 log4j.properties 설정 정보도 함께 변경 필요
     */
    private static Logger log = LogManager.getLogger(KeycodeGenerator.class);
    public static final int KEYCODE_COUNT = 100; //미만값 (입력된 수치는 범위에 포함되지 않음)

    public static final String FILE_KEYCODE_JINIE = "keycode_jinie.dat";
    public static final String FILE_KEYCODE_BIXBY = "keycode_bixby.dat";

    /**
     * 부팅 시 호출. {@code resPath}/keycode_jinie.dat 가 없으면 새 키코드를 자동 생성한다.
     * keycode_bixby.dat 도 함께 생성한다(빅스비 개발자 콘솔 등록용 보조 파일).
     *
     * <p>이미 파일이 존재하면 아무 것도 하지 않는다. 한 번 생성된 키코드는
     * 사용자 인증값과 직접적으로 연관되므로 임의로 재생성되지 않아야 한다.</p>
     */
    public static void generateIfMissing(String resPath) {
        if (resPath == null) {
            log.warn("keycode 자동 생성 스킵: resPath 가 null 입니다");
            return;
        }
        File jinieFile = new File(resPath, FILE_KEYCODE_JINIE);
        if (jinieFile.exists()) {
            return;
        }
        File parent = jinieFile.getParentFile();
        if (parent != null && !parent.exists()) {
            parent.mkdirs();
        }

        log.info("keycode 파일 부재 - 자동 생성합니다: " + jinieFile.getAbsolutePath());
        Map<String, String> keycode = new KeycodeGenerator().createKeycode();
        try {
            FileUtil.writeFile(jinieFile.getAbsolutePath(), formatJinieKeycode(keycode), "UTF-8");
            File bixbyFile = new File(resPath, FILE_KEYCODE_BIXBY);
            FileUtil.writeFile(bixbyFile.getAbsolutePath(), formatBixbyKeycode(keycode), "UTF-8");
            log.info("keycode 파일 자동 생성 완료");
        } catch (UnsupportedEncodingException | FileNotFoundException e) {
            log.error(ExceptionUtil.getExceptionInfo(e));
        } catch (IOException e) {
            log.error(ExceptionUtil.getExceptionInfo(e));
        }
    }

    /** jinie 형식("01:1234" 라인) 으로 직렬화 */
    public static String formatJinieKeycode(Map<String, String> keycode) {
        StringBuilder sb = new StringBuilder();
        Iterator<String> keys = keycode.keySet().iterator();
        while (keys.hasNext()) {
            String key = keys.next();
            String code = keycode.get(key);
            sb.append(key).append(':').append(code);
            if (keys.hasNext()) {
                sb.append(System.lineSeparator());
            }
        }
        return sb.toString();
    }

    /** bixby 개발자 콘솔용 JS 함수 형식으로 직렬화 */
    public static String formatBixbyKeycode(Map<String, String> keycode) {
        StringBuilder sb = new StringBuilder();
        sb.append("  getKeycode:function () {").append(System.lineSeparator());
        sb.append("    return {").append(System.lineSeparator());
        Iterator<String> keys = keycode.keySet().iterator();
        while (keys.hasNext()) {
            String key = keys.next();
            String code = keycode.get(key);
            sb.append("      \"").append(key).append("\":\"").append(code).append('"');
            sb.append(keys.hasNext() ? "," : "").append(System.lineSeparator());
        }
        sb.append("    }").append(System.lineSeparator());
        sb.append("  }").append(System.lineSeparator());
        return sb.toString();
    }

    public static void main(String args[]) {
        Map<String, String> keycode = new KeycodeGenerator().createKeycode();
        String userDir = StringUtil.replaceAll(System.getProperty("user.dir"), "\\\\", "/", false);
        String kcbFileUri = userDir + "/src/main/java/res/" + FILE_KEYCODE_BIXBY;
        String kcjFileUri = userDir + "/src/main/java/res/" + FILE_KEYCODE_JINIE;

        try {
            FileUtil.writeFile(kcbFileUri, formatBixbyKeycode(keycode), "UTF-8");
            FileUtil.writeFile(kcjFileUri, formatJinieKeycode(keycode), "UTF-8");
        } catch (UnsupportedEncodingException | FileNotFoundException e) {
            log.error(ExceptionUtil.getExceptionInfo(e));
        } catch (IOException e) {
            log.error(ExceptionUtil.getExceptionInfo(e));
        }

        System.out.println("KEYCODE 파일 저장 완료 (" + kcjFileUri + ", " + kcbFileUri + ")");
    }

    public Map<String, String> createKeycode() {
        Set<String> chkCodeSet = new HashSet<String>();
        Map<String, String> keycode = new HashMap<String, String>();
        Random rg = new Random();
        int cnt = 1;
        int intFn, intBn;
        String strFn, strBn;
        while (cnt != this.KEYCODE_COUNT) {
            intFn = rg.nextInt(100);
            intBn = rg.nextInt(100);
            if (intFn > 0 && intBn > 0) {
                strFn = (intFn < 10) ? "0" + intFn : "" + intFn;
                strBn = (intBn < 10) ? "0" + intBn : "" + intBn;
                if (chkCodeSet.add(strFn + strBn)) {
                    keycode.put(((cnt < 10) ? "0" + cnt : "" + cnt), strFn + strBn);
                    cnt++;
                }
            }
        }
        
        return keycode;
    }
}
