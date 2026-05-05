package com.omnibuscode.util;

/**
 * 2025.01.02 jakarta.servlet 패키지 대응을 위해 기존 클래스를 백업하였다
 * 추후 시스템에서 파일 처리에 대해 별다른 오류가 없다면 이 클래스는 삭제한다.
 */
public class JinieboxUtil_Bak {

//    public static int TEMP_SIZE_LIMIT = 100 * 1024; // 업로드시 사용할 임시 메모리 제한. 100K
//    public static long UPLOAD_FILESIZE_LIMIT = 100000 * 1024 * 1024L; // 업로드 사이즈 제한. 10000M
//    
//    /**
//     * 입력값 검사 (null 이거나 공백이라면 true)
//     * 
//     * @param param
//     * @return
//     */
//    public static boolean isEmpty(String param) {
//        if (param == null || param.trim().length() < 1)
//            return true;
//        return false;
//    }
//	
//	/**
//	 * 오늘 일자를 스트링으로 반환하는 유틸함수
//	 * @return yyyyMMdd
//	 */
//	public static String getTodayString() {
//		LocalDate today = LocalDate.now();
//		int yearInt = today.getYear();
//		int monthInt = today.getMonthValue();
//		int dayInt = today.getDayOfMonth();
//
//		String monthStr = (monthInt < 10) ? "0" + monthInt : Integer.toString(monthInt);
//		String dayStr = (dayInt < 10) ? "0" + dayInt : Integer.toString(dayInt);
//
//		return yearInt + monthStr + dayStr;
//	}
//	
//	/**
//	 * 현재 날짜와 시간을 반환
//	 * @return yyyyMMddHHmm
//	 */
//	public static String getNowString() {
//		// 현재 날짜와 시간을 가져옵니다.
//        LocalDateTime now = LocalDateTime.now();
//
//        // 원하는 포맷을 정의합니다.
//        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmm");
//
//        // 포맷에 맞게 문자열로 변환하여 반환합니다.
//        return now.format(formatter);
//	}
//
//	/**
//	 * 지정한 일자(yyyyMMdd) 에서 지정한 일수만큼 이후의 일자(yyyyMMdd)
//	 * 
//	 * @param udate 지정 일자
//	 * @param term  초과할 일수
//	 * @return
//	 * @throws ParseException
//	 */
//	public static String getNextdayString(String udate, int term) throws ParseException {
//
//		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
//
//		Calendar cal = Calendar.getInstance();
//		cal.setTime(sdf.parse(udate));
//		cal.add(Calendar.DAY_OF_MONTH, term);
//
//		int yyyy = cal.get(Calendar.YEAR);
//		int mm = cal.get(Calendar.MONTH) + 1;
//		int dd = cal.get(Calendar.DAY_OF_MONTH);
//
//		return "" + yyyy + (mm > 9 ? mm : "0" + mm) + (dd > 9 ? dd : "0" + dd);
//	}
//    
//	/**
//	 * YYYYMMDD to YYYY.MM.DD
//	 * 
//	 * @param d
//	 * @return
//	 */
//	public static String addDatedot(String d) {
//		String edate = d + "";
//		String edate_str = "";
//		edate_str += edate.substring(0, 4) + ".";
//		edate_str += edate.substring(4, 6) + ".";
//		edate_str += edate.substring(6);
//		return edate_str;
//	}
//	
//	/**
//	 * YYYY.MM.DD to YYYYMMDD
//	 * 
//	 * @param d
//	 * @return
//	 */
//	public static String delDatedot(String d) {
//	    if (d == null) {
//            return null;  // 입력이 null인 경우 null 반환
//        }
//        return d.replace(".", "");  // 모든 점을 빈 문자열로 대체
//	};
//	
//    /**
//     * multipart request 로부터 파일과 파라미터를 취하여 JSON 데이터로 반환<br/>
//     * contentType 이 multipart 인 경우 현재의 함수를 실행한 이후엔 request.getParameter()로 값을 취할 수 없게 된다.
//     * @param request
//     * @return
//     * @throws Exception
//     */
//    public static JSONObjectExt parseRequest(HttpServletRequest request) throws Exception {
//        
//        JSONObjectExt jsonObj = new JSONObjectExt();
//        
//        /**
//         * 헤더 파라미터 추출 및 저장
//         */
//        Enumeration hNames = request.getHeaderNames();
//        while (hNames.hasMoreElements()) {
//            String headerName = hNames.nextElement().toString();
//            String headerValue = request.getHeader(headerName);
//            jsonObj.put("header_"+headerName, headerValue);
//        }
//        
//        /**
//         * 요청 본문을 처리
//         */
//        boolean isProcessed = false;
//        String contentType = request.getContentType();
//        if (contentType != null) {
//        	if (contentType.toLowerCase().indexOf("multipart/form-data") > -1
//                && ServletFileUpload.isMultipartContent(request)) {
//        		/**
//                 * 요청 본문이 multipart data 인 경우 처리
//                 */
//                String contextRealPath = request.getSession().getServletContext().getRealPath("/");
//                String savePath = contextRealPath + "upfolder";
//                DiskFileItemFactory factory = new DiskFileItemFactory();
//
//                // 바로 디스크에 저장되는 것이 아니라 메모리에 먼저 저장을 해둔다.
//                factory.setSizeThreshold(TEMP_SIZE_LIMIT); // 임시 업로드할 사이즈를 제한한다.
//                FileUtil.makeDirs(savePath + "/temp");
//                factory.setRepository(new File(savePath + "/temp")); // 임시 디렉토리를 지정한다.
//                ServletFileUpload upload = new ServletFileUpload(factory); // 업로드 객체를 얻는다.
//                upload.setSizeMax(UPLOAD_FILESIZE_LIMIT); // 최대 업로드 사이즈를 지정한다.
//                upload.setHeaderEncoding("UTF-8"); // 파일명을 인코딩해준다.
//
//                List<FileItem> items = upload.parseRequest(request);
//                Iterator<FileItem> itemIter = items.iterator();
//                List<File> savedFiles = new ArrayList<File>();
//                while (itemIter.hasNext()) {
//                    FileItem item = itemIter.next(); // 아이템 얻기
//                    if (item.isFormField()) { 
//                        // 파라미터 처리
//                        String fieldName = item.getFieldName(); // 필드명을 얻는다.
//                        jsonObj.put(fieldName, item.getString("UTF-8"));
//                    } else { 
//                        // 파일 처리
//                        if (item.getSize() > 0) {
//                            String name = item.getName(); // 파일명 얻기
//                            String fileName = name.substring(name.lastIndexOf("\\") + 1);// 파일명을 얻는다.
////                            long fileSize = item.getSize(); // 파일 사이즈를 얻는다.
//
//                            File file = new File(savePath + "/" + fileName); // 기본경로+파일명으로 생성한다.
//                            item.write(file); // 파일 저장.
//                            savedFiles.add(file);
//                        }
//                    }
//                }
//                jsonObj.put(JSONObjectExt.SAVED_USERFILES, savedFiles);
//                isProcessed = true;
//			} else if (contentType.toLowerCase().indexOf("application/json") > -1) {
//        		/**
//                 * 요청 본문이 json data 인 경우 처리
//                 */
//                // 요청 본문을 InputStream으로 읽기
//                InputStream inputStream = request.getInputStream();
//                ByteArrayOutputStream buffer = new ByteArrayOutputStream();
//
//                // 버퍼 크기 설정
//                byte[] data = new byte[1024];
//                int bytesRead;
//
//                // InputStream에서 데이터를 읽고 ByteArrayOutputStream에 저장
//                while ((bytesRead = inputStream.read(data, 0, data.length)) != -1) {
//                    buffer.write(data, 0, bytesRead);
//                }
//                
//                byte[] bodyContent = buffer.toByteArray();
//                jsonObj.put("bodyByteArray", bodyContent); //body 의 내용을 byte array object 그대로 저장
//                
//                String strJson = new String(bodyContent, "UTF-8");
//                jsonObj.putAll(JSONUtil.parseJSONObject(strJson));
//                
//                isProcessed = true;
//        	}
//        }
//        
//        if (!isProcessed) {
//        	/**
//             * Content-Type이 multipart 또는 json이 아니거나 아무것도 지정하지 않은 요청은 
//             * 모두 Content-Type 을 application/x-www-form-urlencoded 로 간주하여 처리
//             */
//            
//            String pName = null;
//            Enumeration<String> enums = request.getParameterNames();
//            while (enums.hasMoreElements()) {
//                pName = enums.nextElement().toString();
//                jsonObj.put(pName, request.getParameter(pName));
//            }
//        }
//        
//        return jsonObj;
//    }
//    
//    /**
//     * json list 를 key:object 형태의 json map 으로 변형시킨다 (key is "seq" string)
//     * 
//     * @param jsonlist
//     * @return
//     */
//    public static JSONObject listToMap(List<JSONObject> jsonlist) {
//        JSONObject rtnJson = new JSONObject();
//        Iterator<JSONObject> jsonIter = jsonlist.iterator();
//        JSONObject jsonObj = null;
//        while (jsonIter.hasNext()) {
//            jsonObj = (JSONObject) jsonIter.next();
//            rtnJson.put(jsonObj.get("seq").toString(), jsonObj);
//        }
//        return rtnJson;
//    }
//    
//    /**
//     * 영문 대소문자 랜덤 문자 반환
//     * @param length 문자열 길이
//     * @return
//     */
//    public static String generateRandomAlphabet(int length) {
//
//        Random random = new Random();
//        StringBuilder sb = new StringBuilder();
//
//        for (int i = 0; i < length; i++) {
//            char c = (char) (random.nextInt(26) + 'a');
//            if (random.nextBoolean()) {
//                c = Character.toUpperCase(c);
//            }
//            sb.append(c);
//        }
//
//        return sb.toString();
//    }
//    
//    /**
//     * 지정한 자리수의 숫자를 랜덤하게 생성
//     * @param length 문자열 길이
//     * @return
//     */
//    public static String generateRandomNumber(int length) {
//
//        Random random = new Random();
//        StringBuilder sb = new StringBuilder();
//
//        for (int i = 0; i < length; i++) {
//            sb.append(random.nextInt(10));
//        }
//
//        return sb.toString();
//    }
}
