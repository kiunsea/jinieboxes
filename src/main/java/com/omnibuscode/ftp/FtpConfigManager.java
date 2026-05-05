package com.omnibuscode.ftp;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Apache FTP Server의 ftpd-typical.xml 설정 파일을 관리하는 클래스
 */
public class FtpConfigManager {
    
    private String configFilePath;
    private Document document;
    private XPath xpath;
    
    /**
     * FtpConfigManager 생성자
     * @param ftpServerPath FTP 서버 설치 경로
     */
    public FtpConfigManager(String ftpServerPath) {
        this.configFilePath = ftpServerPath + "/res/conf/ftpd-typical.xml";
        this.xpath = XPathFactory.newInstance().newXPath();
    }
    
    /**
     * 설정 파일을 로드합니다
     * @throws Exception
     */
    public void loadConfig() throws Exception {
        File configFile = new File(configFilePath);
        if (!configFile.exists()) {
            throw new Exception("설정 파일을 찾을 수 없습니다: " + configFilePath);
        }
        
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        
        try (InputStream is = new FileInputStream(configFile)) {
            document = builder.parse(is);
            document.getDocumentElement().normalize();
        }
        
        System.out.println("설정 파일 로드 완료: " + configFilePath);
    }
    
    /**
     * 설정 파일을 저장합니다
     * @throws Exception
     */
    public void saveConfig() throws Exception {
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();
        
        // 들여쓰기 및 포맷팅 설정
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
        transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
        
        DOMSource source = new DOMSource(document);
        
        try (OutputStream os = new FileOutputStream(configFilePath)) {
            StreamResult result = new StreamResult(os);
            transformer.transform(source, result);
        }
        
        System.out.println("설정 파일 저장 완료: " + configFilePath);
    }
    
    /**
     * FTP 서버 포트 번호를 설정합니다
     * @param port 포트 번호
     * @throws Exception
     */
    public void setServerPort(int port) throws Exception {
        String expression = "//listener[@name='default']/port";
        Node portNode = (Node) xpath.evaluate(expression, document, XPathConstants.NODE);
        
        if (portNode != null) {
            portNode.setTextContent(String.valueOf(port));
            System.out.println("FTP 서버 포트 변경: " + port);
        } else {
            throw new Exception("포트 설정 노드를 찾을 수 없습니다");
        }
    }
    
    /**
     * 최대 로그인 수를 설정합니다
     * @param maxLogins 최대 로그인 수
     * @throws Exception
     */
    public void setMaxLogins(int maxLogins) throws Exception {
        String expression = "//max-logins";
        Node maxLoginsNode = (Node) xpath.evaluate(expression, document, XPathConstants.NODE);
        
        if (maxLoginsNode != null) {
            maxLoginsNode.setTextContent(String.valueOf(maxLogins));
            System.out.println("최대 로그인 수 변경: " + maxLogins);
        } else {
            throw new Exception("max-logins 노드를 찾을 수 없습니다");
        }
    }
    
    /**
     * 익명 로그인 허용 여부를 설정합니다
     * @param allow true: 허용, false: 비허용
     * @throws Exception
     */
    public void setAnonymousLoginEnabled(boolean allow) throws Exception {
        String expression = "//anonymous-login-enabled";
        Node anonymousNode = (Node) xpath.evaluate(expression, document, XPathConstants.NODE);
        
        if (anonymousNode != null) {
            anonymousNode.setTextContent(String.valueOf(allow));
            System.out.println("익명 로그인 설정: " + (allow ? "허용" : "비허용"));
        } else {
            throw new Exception("anonymous-login-enabled 노드를 찾을 수 없습니다");
        }
    }
    
    /**
     * 최대 업로드 속도를 설정합니다 (bytes/sec)
     * @param bytesPerSec 초당 바이트 수 (0 = 무제한)
     * @throws Exception
     */
    public void setMaxUploadRate(int bytesPerSec) throws Exception {
        String expression = "//max-upload-rate";
        Node uploadRateNode = (Node) xpath.evaluate(expression, document, XPathConstants.NODE);
        
        if (uploadRateNode != null) {
            uploadRateNode.setTextContent(String.valueOf(bytesPerSec));
            System.out.println("최대 업로드 속도 설정: " + (bytesPerSec == 0 ? "무제한" : bytesPerSec + " bytes/sec"));
        } else {
            throw new Exception("max-upload-rate 노드를 찾을 수 없습니다");
        }
    }
    
    /**
     * 최대 다운로드 속도를 설정합니다 (bytes/sec)
     * @param bytesPerSec 초당 바이트 수 (0 = 무제한)
     * @throws Exception
     */
    public void setMaxDownloadRate(int bytesPerSec) throws Exception {
        String expression = "//max-download-rate";
        Node downloadRateNode = (Node) xpath.evaluate(expression, document, XPathConstants.NODE);
        
        if (downloadRateNode != null) {
            downloadRateNode.setTextContent(String.valueOf(bytesPerSec));
            System.out.println("최대 다운로드 속도 설정: " + (bytesPerSec == 0 ? "무제한" : bytesPerSec + " bytes/sec"));
        } else {
            throw new Exception("max-download-rate 노드를 찾을 수 없습니다");
        }
    }
    
    /**
     * 유휴 시간 제한을 설정합니다 (초)
     * @param seconds 유휴 시간 (초)
     * @throws Exception
     */
    public void setIdleTime(int seconds) throws Exception {
        String expression = "//idle-time";
        Node idleTimeNode = (Node) xpath.evaluate(expression, document, XPathConstants.NODE);
        
        if (idleTimeNode != null) {
            idleTimeNode.setTextContent(String.valueOf(seconds));
            System.out.println("유휴 시간 제한 설정: " + seconds + "초");
        } else {
            throw new Exception("idle-time 노드를 찾을 수 없습니다");
        }
    }
    
    /**
     * 현재 설정 정보를 출력합니다
     * @throws Exception
     */
    public void printCurrentConfig() throws Exception {
        System.out.println("\n" + "=".repeat(100));
        System.out.println("FTP 서버 설정 정보");
        System.out.println("=".repeat(100));
        
        // 포트
        String portExpr = "//listener[@name='default']/port";
        Node portNode = (Node) xpath.evaluate(portExpr, document, XPathConstants.NODE);
        if (portNode != null) {
            System.out.println("포트 번호: " + portNode.getTextContent());
        }
        
        // 최대 로그인 수
        String maxLoginsExpr = "//max-logins";
        Node maxLoginsNode = (Node) xpath.evaluate(maxLoginsExpr, document, XPathConstants.NODE);
        if (maxLoginsNode != null) {
            System.out.println("최대 로그인 수: " + maxLoginsNode.getTextContent());
        }
        
        // 익명 로그인
        String anonymousExpr = "//anonymous-login-enabled";
        Node anonymousNode = (Node) xpath.evaluate(anonymousExpr, document, XPathConstants.NODE);
        if (anonymousNode != null) {
            System.out.println("익명 로그인: " + anonymousNode.getTextContent());
        }
        
        // 유휴 시간
        String idleTimeExpr = "//idle-time";
        Node idleTimeNode = (Node) xpath.evaluate(idleTimeExpr, document, XPathConstants.NODE);
        if (idleTimeNode != null) {
            System.out.println("유휴 시간 제한: " + idleTimeNode.getTextContent() + "초");
        }
        
        // 최대 업로드 속도
        String uploadRateExpr = "//max-upload-rate";
        Node uploadRateNode = (Node) xpath.evaluate(uploadRateExpr, document, XPathConstants.NODE);
        if (uploadRateNode != null) {
            String rate = uploadRateNode.getTextContent();
            System.out.println("최대 업로드 속도: " + (rate.equals("0") ? "무제한" : rate + " bytes/sec"));
        }
        
        // 최대 다운로드 속도
        String downloadRateExpr = "//max-download-rate";
        Node downloadRateNode = (Node) xpath.evaluate(downloadRateExpr, document, XPathConstants.NODE);
        if (downloadRateNode != null) {
            String rate = downloadRateNode.getTextContent();
            System.out.println("최대 다운로드 속도: " + (rate.equals("0") ? "무제한" : rate + " bytes/sec"));
        }
        
        System.out.println("=".repeat(100));
    }
    
    /**
     * 테스트용 메인 메서드
     */
    public static void main(String[] args) {
        try {
            String ftpServerPath = "D:/DEV/ftp/apache-ftpserver-1.1.4";
            
            FtpConfigManager configManager = new FtpConfigManager(ftpServerPath);
            
            // 설정 파일 로드
            configManager.loadConfig();
            
            // 현재 설정 출력
            configManager.printCurrentConfig();
            
            // 설정 변경 예시
            System.out.println("\n설정을 변경합니다...\n");
            configManager.setServerPort(2121);
            configManager.setMaxLogins(20);
            configManager.setAnonymousLoginEnabled(false);
            configManager.setIdleTime(600);
            configManager.setMaxUploadRate(0);
            configManager.setMaxDownloadRate(0);
            
            // 변경된 설정 저장
            configManager.saveConfig();
            
            // 변경된 설정 확인
            configManager.loadConfig();
            configManager.printCurrentConfig();
            
        } catch (Exception e) {
            System.err.println("오류 발생: " + e.getMessage());
            e.printStackTrace();
        }
    }
}

