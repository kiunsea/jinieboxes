package com.omnibuscode.logic.file;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.json.simple.JSONObject;

import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.http.FileContent;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.FileList;
import com.google.auth.http.HttpCredentialsAdapter;
import com.google.auth.oauth2.AccessToken;
import com.google.auth.oauth2.GoogleCredentials;
import com.omnibuscode.dao.GDFileInfoDataAccessObject;
import com.omnibuscode.logic.GcpService;
import com.omnibuscode.utils.FileUtil;
import com.omnibuscode.utils.StringUtil;

public class GoogleDrive {
    
    private Logger log = LogManager.getLogger(GoogleDrive.class);
    
    private String accessToken = null;
    
    public GoogleDrive(String accessToken) {
        this.accessToken = accessToken;
    }
    
    /**
     * 유효한 파일경로 형태로 변환해준다(back slash -> slash)<br/>
     * 파라미터가 파일 경로가 아닐수도 있으니 주의해야 한다
     * @param path
     * @return replaced path
     */
    private String convertValidPath(String path) {
        return path.replace("\\", "/");
    }
    
    /**
     * 구글 드라이브 접근시 필요한 Credential 을 반환
     * @return
     * @throws Exception
     */
    private Drive getDriveService() throws Exception {

        if (this.accessToken != null) {
            GoogleCredentials gc = GoogleCredentials.newBuilder().setAccessToken(new AccessToken(accessToken, null)).build();

            return new com.google.api.services.drive.Drive.Builder(new NetHttpTransport(),
                    GsonFactory.getDefaultInstance(), 
                    new HttpCredentialsAdapter(gc)).setApplicationName("appname").build();
        } else {
            log.debug("AccessToken is NULL!!");
            return null;
        }
    }
    
    /**
     * child 목록에 폴더가 있는지 검사하고 id 를 반환
     * @param parentId
     * @param name
     * @return 검색 실패시 null
     * @throws IOException
     * @throws Exception
     */
    private String getChildFolderId(String parentId, String name) throws IOException, Exception {
        // 폴더 내의 파일과 폴더 목록을 가져옵니다.
        Drive drive = this.getDriveService();
        
        if (drive != null) {
            FileList result = this.getDriveService().files().list()
                    .setQ("'" + parentId + "' in parents")
                    .setSpaces("drive")
                    .setFields("nextPageToken, files(id, name)")
                    .execute();
            
            String fName = null;
            List<File> files = result.getFiles();
            if (files == null || files.isEmpty()) {
                log.error("No files found.");
            } else {
                for (File file : files) {
                    fName = file.getName();
                    if (fName.trim().equals(name.trim())) {
                        return file.getId();
                    }
                }
            }
        }

        return null;
    }
    
    /**
     * parent 를 지정하여 folder 를 생성
     * 
     * @param service
     * @param folderName
     * @param parentFolderId
     * @return 생성한 folder 의 id
     * @throws Exception
     */
    private String createFolder(Drive service, String folderName, String parentFolderId) throws Exception {
        File fileMetadata = new File();
        fileMetadata.setName(folderName);
        fileMetadata.setMimeType("application/vnd.google-apps.folder");
        if (parentFolderId != null) {
            fileMetadata.setParents(Collections.singletonList(parentFolderId));
        }

        File folder = service.files().create(fileMetadata).setFields("id").execute();

        return folder.getId();
    }
    
    /**
     * 폴더의 경로를 입력받아 경로 검색후 계층의 마지막 폴더의 아이디를 반환<br/>
     * 폴더가 생성되어 있지 않은 경우 자동 생성
     * @param folderPath
     * @return file_id
     * @throws Exception 
     * @throws IOException 
     */
    public String getFolderId(String folderPath) throws IOException, Exception {

        String fPath = this.convertValidPath(folderPath);
        String[] arr_folder = fPath.split("/");

        String folderName = arr_folder[0];
        String folderId = this.getChildFolderId("root", folderName);
        if (folderId == null) {
            folderId = this.createFolder(this.getDriveService(), folderName, "root");
        }
        for (int i = 1; i < arr_folder.length; i++) {
            String cFolderId = this.getChildFolderId(folderId, arr_folder[i]);
            if (cFolderId != null) {
                folderId = cFolderId;
            } else {
                folderId = this.createFolder(this.getDriveService(), arr_folder[i], folderId);
            }
        }

        return folderId;
    }
    
    /**
     * jiniebox Class(Box, Item)의 이미지 목록을 조회하여 이미지의 fileid 목록을 반환
     * 
     * @param seqUser
     * @param seqClass
     * @param typeClass
     * @return
     * @throws Exception
     */
    public List<JSONObject> getClassImgs(String seqUser, String seqClass, String typeClass) throws Exception {

        GDFileInfoDataAccessObject gdfileInfoDao = new GDFileInfoDataAccessObject();
        List<JSONObject> list = gdfileInfoDao.getFiles(seqClass, typeClass);

        List<JSONObject> imgs = new ArrayList<JSONObject>();
        JSONObject imgJson = null;
        Iterator imgIter = list.iterator();
        while (imgIter.hasNext()) {
            imgJson = (JSONObject) imgIter.next();
            if ("0".equals(imgJson.get("gd_file_type").toString())) {
                imgs.add(imgJson);
            }
        }

        return imgs;
    }
    
    /**
     *  구글 드라이브에 파일을 업로드한다.
     * 
     * @param parentFolderId
     * @param fObj
     * @return file id
     * @throws IOException
     * @throws Exception
     */
    public String uploadBasic(String parentFolderId, java.io.File fObj) throws IOException, Exception {
        
        String faPath = fObj.getAbsolutePath();
        java.nio.file.Path fPath = java.nio.file.Paths.get(faPath);
        String mt = java.nio.file.Files.probeContentType(fPath);
        
        File fileMetadata = new File()
                .setName(FileUtil.getFullName(faPath))
                .setMimeType(mt)
                .setParents(Collections.singletonList(parentFolderId));
        
        // File's content.
        java.io.File filePath = new java.io.File(faPath);
        log.debug(">>> "+filePath.getAbsolutePath());
        // Specify media type and file-path for file.
        FileContent mediaContent = new FileContent(mt, filePath);
        
        try {
            File file = this.getDriveService().files().create(fileMetadata, mediaContent).setFields("id").execute();
            log.debug("File ID: " + file.getId());
            return file.getId();
        } catch (GoogleJsonResponseException e) {
            log.error("Unable to upload file: " + e.getDetails());
            throw e;
        }
    }
    
    /**
     * 지정한 파일을 삭제한다.
     * 
     * @param fileId
     * @throws IOException
     * @throws Exception
     */
    public void deleteFile(String fileId) throws IOException, Exception {
        this.getDriveService().files().delete(fileId).execute();
    }
}
