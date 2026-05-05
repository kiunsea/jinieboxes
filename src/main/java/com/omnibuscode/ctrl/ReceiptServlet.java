package com.omnibuscode.ctrl;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.json.simple.JSONObject;

import com.google.cloud.vision.v1.AnnotateImageRequest;
import com.google.cloud.vision.v1.AnnotateImageResponse;
import com.google.cloud.vision.v1.BatchAnnotateImagesResponse;
import com.google.cloud.vision.v1.Feature;
import com.google.cloud.vision.v1.Image;
import com.google.cloud.vision.v1.ImageAnnotatorClient;
import com.google.protobuf.ByteString;
import com.omnibuscode.auth.AuthManager;
import com.omnibuscode.base.EnvSYS;
import com.omnibuscode.utils.ExceptionUtil;
import com.omnibuscode.utils.FileUtil;
import com.omnibuscode.utils.NumberUtil;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Part;

/**
 * @author KIUNSEA
 *
 */
@WebServlet("/receipt")
@MultipartConfig(
		fileSizeThreshold = 1024 * 1024 * 10, // 10MB
		maxFileSize = 1024 * 1024 * 10 * 10, // 100MB
		maxRequestSize = 1024 * 1024 * 50 * 10 // 500MB
)
public class ReceiptServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
	/**
	 * 서버 환경 변경시 log4j.properties 설정 정보도 함께 변경 필요
	 */
	private Logger log = LogManager.getLogger(ReceiptServlet.class);
	
	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		JSONObject resJson = null;
		
		try {
			if (AuthManager.getInstance().hasUserAuthed(request)) {
				
				String contentType = request.getContentType();
				if (contentType.toLowerCase().indexOf("application/octet-stream") > -1) {
					resJson = this.processFile(request, response);
				} else if (contentType.toLowerCase().indexOf("multipart/form-data") > -1) {
					resJson = this.processForm(request, response);
				} else {
					resJson = new JSONObject();
					resJson.put("code", EnvSYS.RESCODE_FAIL);
                    resJson.put("msg", "[Error] invaid content type : " + contentType);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			log.error(ExceptionUtil.getExceptionInfo(e));
		}
		
		if (resJson == null) resJson = new JSONObject();
		response.setCharacterEncoding("UTF-8");
		response.setContentType("application/json;charset=UTF-8");
		response.setHeader("Cache-Control", "no-cache");
        PrintWriter out = response.getWriter();
        out.println(resJson);
        out.flush();
        out.close();
	}
	
	/**
	 * request 가 form data 인 경우 
	 * @param request
	 * @param response
	 * @throws IOException
	 */
	private JSONObject processForm(HttpServletRequest request, HttpServletResponse response) throws IOException {
		
		JSONObject resObj = new JSONObject();
		
		// Check if request is multipart
		String contentType = request.getContentType();
		if (contentType != null && contentType.toLowerCase().indexOf("multipart/form-data") > -1) {
            try {
            	String seqUser = AuthManager.getInstance().getUserSession(request).getSeq();
        		String uploadPath = getServletContext().getRealPath("/") + "upload_receipts" + File.separator + seqUser;
        		
                // Set up factory and upload handler
                DiskFileItemFactory factory = new DiskFileItemFactory();
                ServletFileUpload upload = new ServletFileUpload(factory);

				boolean numbering = true;
				String filePath = null;
				
                // Process each form item
				for (Part part : request.getParts()) {
					if (part.getContentType() == null) { 
	                    // 일반 파라미터 처리
	                    String fieldName = part.getName(); // 파라미터 이름
	                    String fieldValue = new String(part.getInputStream().readAllBytes(), "UTF-8"); // 파라미터 값
	                    if (fieldName.equals("numbering")) {
							numbering = Boolean.parseBoolean(fieldValue.toString());
						}
	                } else {
	                    // 파일 처리
	                    String fileName = part.getSubmittedFileName();
                        File uploadDir = new File(uploadPath);
                        
						if (uploadDir.exists()) {
							try {
								if (uploadDir.isDirectory()) {
									for (File subFile : uploadDir.listFiles()) {
										System.gc(); // Garbage Collector 호출
										Thread.sleep(100); // 약간의 지연 후
										subFile.delete(); // 내부 파일 삭제
									}
									System.gc(); // Garbage Collector 호출
								}
								Thread.sleep(100); // 약간의 지연 후
								uploadDir.delete(); // 디렉토리 삭제
							} catch (Exception e) {
								log.error(e);
							}
						}
						FileUtil.makeDirs(uploadDir.getAbsolutePath());
//						uploadDir.mkdir();
						
						filePath = uploadPath + java.io.File.separator + fileName;
						File outputFile = new File(filePath);
						if (outputFile.exists()) {
							log.error("기존 파일 삭제에 실패하였습니다. : " + filePath);
						} else {
							part.write(filePath); // 파일 저장
							outputFile.setWritable(true);
						}
						filePath = outputFile.getAbsolutePath();
	                }
	            }
                
				
				log.debug("FilePath: " + filePath);
				log.debug("Numbering: " + numbering);
                
                resObj.put("itemNames", detectText(filePath, numbering));
                log.debug("Upload successful!");
                
                if (new File(filePath).exists()) {
                	log.info("Cache files were not deleted >> "+filePath);
                }
            } catch (Exception ex) {
                ex.printStackTrace();
                log.error("Error: " + ex.getMessage());
            }
        } else {
			log.error("Error: Form must have enctype=multipart/form-data. - this is '" + request.getContentType() + "'");
        }
        
        resObj.put("code", EnvSYS.RESCODE_SUCC);
        resObj.put("msg", "");
        
        return resObj;
	}
	
	/**
	 * request 가 blob 데이터를 그대로 적재하여 전송한 경우 
	 * @param request
	 * @param response
	 * @throws Exception 
	 */
	private JSONObject processFile(HttpServletRequest request, HttpServletResponse response) throws Exception {
		
		JSONObject resObj = new JSONObject();
		
		// 파일 저장
		String seqUser = AuthManager.getInstance().getUserSession(request).getSeq();
		String uploadPath = getServletContext().getRealPath("/") + "upload_receipts" + File.separator + seqUser;
		if (FileUtil.exists(uploadPath) | FileUtil.makeDirs(uploadPath)) {
			File outputFile = new File(uploadPath, "captured_image.png");
			try (InputStream inputStream = request.getInputStream();
					FileOutputStream outputStream = new FileOutputStream(outputFile)) {
				byte[] buffer = new byte[1024];
				int bytesRead;
				while ((bytesRead = inputStream.read(buffer)) != -1) {
					outputStream.write(buffer, 0, bytesRead);
				}
			}
			
			resObj.put("itemNames", detectText(outputFile.getAbsolutePath(), true));

			// 응답
			response.setContentType("text/plain");
			response.getWriter().write("Image uploaded to: " + outputFile.getAbsolutePath());
		}
		
		resObj.put("code", EnvSYS.RESCODE_SUCC);
        resObj.put("msg", "");
        
        return resObj;
	}
	
	
    /**
     * 이미지에서 텍스트 추출
     * 
     * @param filePath
     * @param numbering
     * @return
     * @throws Exception
     */
    private List<String> detectText(String filePath, boolean numbering) throws Exception {
        List<AnnotateImageRequest> requests = new ArrayList<>();

        ByteString imgBytes = ByteString.readFrom(new FileInputStream(filePath));

        Image img = Image.newBuilder().setContent(imgBytes).build();
        Feature feat = Feature.newBuilder().setType(Feature.Type.TEXT_DETECTION).build();
        AnnotateImageRequest request = AnnotateImageRequest.newBuilder().addFeatures(feat).setImage(img).build();
        requests.add(request);

        // Initialize client that will be used to send requests. This client only needs
        // to be created
        // once, and can be reused for multiple requests. After completing all of your
        // requests, call
        // the "close" method on the client to safely clean up any remaining background
        // resources.
        try (ImageAnnotatorClient client = ImageAnnotatorClient.create()) {
            BatchAnnotateImagesResponse response = client.batchAnnotateImages(requests);
            List<AnnotateImageResponse> responses = response.getResponsesList();

			log.info("google-cloud-vision responses!! : size >> " + responses.size());
            List<String> itemNames = new ArrayList<String>();
            for (AnnotateImageResponse res : responses) {
                if (res.hasError()) {
//                    System.out.format("* Error: %s%n", res.getError().getMessage());
                	log.error(res.getError().getMessage());
                    throw new Exception(res.getError().getMessage());
                }
                
                //see http://g.co/cloud/vision/docs
				String sentence = res.getTextAnnotations(0).getDescription(); // 첫번째 인덱스만 추출하여 처리
				log.debug("response message : " + sentence);
				if (!sentence.isEmpty() && (sentence.contains("\n") || sentence.contains("\r"))) {

					String[] lines = sentence.split("\\r?\\n");

					// 결과를 출력
					for (String line : lines) {
						if (numbering) {
							String[] words = line.split("\\s");
							if (words.length > 1) {
								String firstWord = words[0].trim();
								boolean isNum = false; //number check 결과
								if (NumberUtil.isNumber(firstWord.replace(",", ""))) {
									// 넘버에 콤마 삽입형 - ex> '001,00'
									isNum = true;
								}
								if (firstWord.length() == 4
										&& (NumberUtil.isNumber(firstWord.substring(0, firstWord.length() - 1)))) {
									// 넘버끝에 문자 삽입형 - ex> '001)'
									isNum = true;
								}
								if (isNum) {
									if (words[1] != null && !NumberUtil.isNumber(words[1].replace(",", ""))) {
										log.debug(") " + line);
										itemNames.add(this.joinStringsFromIndex(words, 1));
									}
								}
							}
						} else {
							log.debug("> " + line);
							itemNames.add(line);
						}
					}
				}
            }
            
			File fObj = new File(filePath);
			try {
				if (fObj.exists()) {
					System.gc(); // Garbage Collector 호출
					Thread.sleep(100); // 약간의 지연 후
					if (fObj.delete()) {
						log.info("Receipt Analysis Complete!! - " + filePath);
					} else {
						log.info("The file was not deleted!! - " + filePath);
					}
				} else {
					log.error("There is no file!! - " + filePath);
				}
			} catch (Exception e) {
				log.error(e);
			}
            
            return itemNames;
        }
    }
    
    private String joinStringsFromIndex(String[] array, int startIndex) {
        if (array == null || startIndex >= array.length) {
            return ""; // 유효하지 않은 경우 빈 문자열 반환
        }

        // 문자열을 조합하여 반환
        StringBuilder result = new StringBuilder();
        for (int i = startIndex; i < array.length; i++) {
            if (i > startIndex) {
                result.append(" "); // 단어 사이에 공백 추가
            }
            result.append(array[i]);
        }
        return result.toString();
    }
}
