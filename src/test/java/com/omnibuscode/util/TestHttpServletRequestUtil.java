package com.omnibuscode.util;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.Part;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.tomcat.util.http.fileupload.impl.InvalidContentTypeException;
import org.json.simple.JSONObject;

import com.omnibuscode.utils.ExceptionUtil;
import com.omnibuscode.utils.FileUtil;

/**
 * parseJSON(), containsFiles(HttpServletRequest) 함수는 사용할 수 없다.
 * containsFiles() 를 호출하고 나서는 request 에서 파일 스트림을 취할 수가 없는 버그가 있다. (request.getParts(); 때문임)
 * 해당 오류에 대한 해결 방안이 마련되기 전까지는 이 클래스를 사용하지 못한다.
 * @author KIUNSEA
 *
 */
public class TestHttpServletRequestUtil {
    
    /**
     * 서버 환경 변경시 log4j.properties 설정 정보도 함께 변경 필요
     */
    private static Logger log = LogManager.getLogger(TestHttpServletRequestUtil.class);
    
    public static int TEMP_SIZE_LIMIT = 100 * 1024; // 업로드시 사용할 임시 메모리 제한. 100K
    public static long UPLOAD_FILESIZE_LIMIT = 100000 * 1024 * 1024L; // 업로드 사이즈 제한. 10000M
    public static String SAVED_USERFILES = "HSR_FILES";

    /**
     * HttpServletRequest 내의 파라미터들을 파싱하여 JSON 으로 반환 (파일 정보 포함)<br/>
     * 파일 파라미터가 존재하는 경우 web application context root 에 upfolder 를 자동 생성하여 저장한다.<br/>
     * HttpServletRequestUtil.TEMP_SIZE_LIMIT : 업로드시 사용할 임시 메모리 제한. (default 100K)<br/>
     * HttpServletRequestUtil.UPLOAD_FILESIZE_LIMIT : 업로드 사이즈 제한. (default 10000M)
     * 
     * @param request
     * @return
     * @throws Exception 
     */
    public static JSONObject parseJSON(HttpServletRequest request) throws Exception {
        JSONObject jsonObj = new JSONObject();

        if (TestHttpServletRequestUtil.containsFiles(request)) {
            /** Initialize **/
            String contextRealPath = request.getSession().getServletContext().getRealPath("/");
            String savePath = contextRealPath + "upfolder";
            DiskFileItemFactory factory = new DiskFileItemFactory();

            // 바로 디스크에 저장되는 것이 아니라 메모리에 먼저 저장을 해둔다.
            factory.setSizeThreshold(TEMP_SIZE_LIMIT); // 임시 업로드할 사이즈를 제한한다.
            FileUtil.makeDirs(savePath + "/temp");
            factory.setRepository(new File(savePath + "/temp")); // 임시 디렉토리를 지정한다.
            ServletFileUpload upload = new ServletFileUpload(factory); // 업로드 객체를 얻는다.
            upload.setSizeMax(UPLOAD_FILESIZE_LIMIT); // 최대 업로드 사이즈를 지정한다.
            upload.setHeaderEncoding("UTF-8"); // 파일명을 인코딩해준다.

            //2025.01.02 javax.servet에서 jakarta.servlet 패키지로 변경하면서 오류가 발생하는 코드들이다.
            //javax.servlet 패키지로 원복할 경우엔 다시 참고 코드로 복귀시키도록 한다.
//            List<FileItem> items = upload.parseRequest(request); // 아이템을 얻는다.
//            Iterator<FileItem> iter = items.iterator(); // iterator로 변경한다.
//            List<File> savedFiles = new ArrayList<File>();
//            while (iter.hasNext()) {
//                FileItem item = iter.next(); // 아이템 얻기
//                if (item.isFormField()) { 
//                    // 파라미터 처리
//                    String fieldName = item.getFieldName(); // 필드명을 얻는다.
//                    String value = item.getString("UTF-8");
//                    jsonObj.put(fieldName, value);
//                } else { 
//                    // 파일 처리
//                    if (item.getSize() > 0) {
//                        String name = item.getName(); // 파일명 얻기
//                        String fileName = name.substring(name.lastIndexOf("\\") + 1);// 파일명을 얻는다.
////                        long fileSize = item.getSize(); // 파일 사이즈를 얻는다.
//
//                        File file = new File(savePath + "/" + fileName); // 기본경로+파일명으로 생성한다.
//                        item.write(file); // 파일 저장.
//                        savedFiles.add(file);
//                    }
//                }
//            }
//            jsonObj.put(SAVED_USERFILES, savedFiles);
        } else {
            String pName = null;
            Enumeration<String> enums = request.getParameterNames();
            while (enums.hasMoreElements()) {
                pName = enums.nextElement().toString();
                jsonObj.put(pName, request.getParameter(pName));
            }
        }

        return jsonObj;
    }
    
    /**
     * HttpServletRequest 에 File 이 포함되어 있는지 여부 확인<br/>
     * request 에서 파일 스트림을 취할 수가 없는 버그가 있다. (request.getParts(); 때문임) - 대안을 찾기전까지는 사용금지
     * WAS 의 Context 에 다음의 설정이 필요함<br/>
     * &lt;Context allowCasualMultipartParsing="true" path="/"&gt;<br/>
     * &nbsp;&nbsp;&nbsp;&nbsp;&lt;Resources cachingAllowed="true" cacheMaxSize="100000" /&gt;<br/>
     * &lt;/Context&gt;
     * @param request
     * @return
     * @throws IOException
     * @throws ServletException
     */
    public static boolean containsFiles(HttpServletRequest request) throws IOException, ServletException {
        // Check if the request contains a file.
        String contentType = request.getContentType();
        if (contentType != null) {
            if (contentType.toLowerCase().indexOf("multipart/form-data") > -1) {
                return true;
            } else if (contentType.toLowerCase().indexOf("multipart/mixed") > -1) {
                // 실제 스트림을 검사하여 파일 존재 여부를 확인
                // Get all the parts from the request.
                Collection<Part> parts = null;
                try {
                    parts = request.getParts();
                } catch (InvalidContentTypeException e) {
                    log.error(ExceptionUtil.getExceptionInfo(e));
                }

                // Check if any of the parts are files.
                for (Part part : parts) {
                    if (part.getName().toLowerCase().equals("file") 
                            || part.getContentType() != null
                            || part.getSubmittedFileName() != null) {
                        return true;
                    }
                }
            }
        } else {
            return true;
        }

        return false;
    }
    
    public static String getBrowser(HttpServletRequest request) {
        String header = request.getHeader("User-Agent");
        if (header == null) {
            return "";
        } else if (header.indexOf("MSIE") > -1) {
            return "MSIE";
        } else if (header.indexOf("Chrome") > -1) {
            return "Chrome";
        } else if (header.indexOf("Opera") > -1) {
            return "Opera";
        } else if (header.indexOf("Firefox") > -1) {
            return "Firefox";
        } else if (header.indexOf("Safari") > -1) {
            return "Safari";
        } else {
            return header;
        }
    }

    public static String getRemoteAddr(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_CLIENT_IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_X_FORWARDED_FOR");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }

    public static String getRequestBaseUrl(HttpServletRequest request) {
        String path = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort()
                + request.getContextPath();
        return path;
    }

    public static String getHeaderString(HttpServletRequest request) {
        Enumeration<?> headerNames = request.getHeaderNames();
        if (headerNames == null) {
            return null;
        }
        Map<String, String> headerMap = new LinkedHashMap<>();
        try {
            Object headerName = null;
            while ((headerName = headerNames.nextElement()) != null) {
                headerMap.put(String.valueOf(headerName), request.getHeader(String.valueOf(headerName)));
            }
        } catch (NoSuchElementException e) {
            return headerMap.toString();
        }

        return headerMap.toString();
    }

    public static String getRequestURI(HttpServletRequest request) {
        Object forwardRequestUri = request.getAttribute("jakarta.servlet.forward.request_uri");
        if (forwardRequestUri != null) {
            return forwardRequestUri + "(" + request.getRequestURI() + ")";
        }
        return request.getRequestURI();
    }

    public static String getUserAgent(HttpServletRequest request) {
        return request.getHeader("User-Agent");
    }

    public static String getHeader(HttpServletRequest request, String name) {
        return request.getHeader(name);
    }
}
