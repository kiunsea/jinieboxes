package com.omnibuscode.logic.file;

import java.io.File;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.json.simple.JSONObject;

import com.omnibuscode.dao.GDFileInfoDataAccessObject;
import com.omnibuscode.dao.StoreDataAccessObject;
import com.omnibuscode.dao.UserDataAccessObject;
import com.omnibuscode.logic.GcpService;
import com.omnibuscode.util.JSONObjectExt;
import com.omnibuscode.utils.FileUtil;
import com.omnibuscode.utils.PropertiesUtil;

/**
 * 사용자 파일을 처리하기 위한 관리자
 */
public class FileManager {

    public int STORAGE_TYPE_LOCAL = 0;
    public int STORAGE_TYPE_GOOGLE = 1;
    
    private Logger log = LogManager.getLogger(FileManager.class);
    
    private String servletContextPath;
    private String repositoryPath;
    private String googleAccessToken;

    public FileManager() {;};
    
    /**
     * 유효한 파일경로 형태로 변환해준다(back slash -> slash)<br/>
     * 파라미터가 파일 경로가 아닐수도 있으니 주의해야 한다
     * @param path
     * @return replaced path
     */
    private String convertValidPath(String path) {
        return path.contains("\\") ? path.replace("\\", "/") : path;
    }
    
    public FileManager(String servletContextPath, String repositoryPath, String googleAccessToken) {
        this.servletContextPath = servletContextPath;
        this.repositoryPath = repositoryPath;
        this.googleAccessToken = googleAccessToken;
    }

    public void setServletContextPath(String servletContextPath) {
        this.servletContextPath = servletContextPath;
    }

    public void setRepositoryPath(String repositoryPath) {
        this.repositoryPath = repositoryPath;
    }

//    /**
//     * 사용자의 구글 드라이브에 생성한 시스템 폴더의 아이디를 반환한다.<br/>
//     * 현재 예약된 키워드는 "jiniebox, user_files, item_images, box_images" 가 있다.<br/>
//     * 위 목록중 하나를 파라미터로 전달하면 id 값을 취할 수 있다.<br/>
//     * 만일 해당 폴더가 생성되지 않았다면 폴더를 생성한 후 아이디를 취한다.
//     * @param seqStore
//     * @param seqUser
//     * @param fName
//     * @return 구글 드라이브에 생성한 folder id
//     * @throws Exception
//     */
//    private String getGdFolderId(String seqStore, String seqUser, String fName) throws Exception {
//
//        GDFileInfoDataAccessObject gdfDao = new GDFileInfoDataAccessObject();
//        JSONObject jbFolderJo = gdfDao.getSysFolder(seqStore, fName);
//
//        String folderId = null;
//        if (jbFolderJo == null) {
//            //TODO jbFolder 생성후 db에 id 저장
//            GcpService gcpSvc = new GcpService();
//            String accessToken = gcpSvc.getAccessToken(seqUser);
//        } else {
//            folderId = jbFolderJo.get("gd_file_id").toString();
//        }
//
//        return folderId;
//    }
    
    /**
     * 사용자 파일 저장
     * 
     * @param seqUser
     * @param seqClass (Box or Item)
     * @param typeClass (Box or Item)
     * @param files
     * @throws Exception
     */
    public void restoreFile(String seqUser, String seqClass, String typeClass, List<File> files) throws Exception {

        if (files != null) {
            
            StoreDataAccessObject sdao = new StoreDataAccessObject();
            JSONObject storeJo = sdao.getOwnStore(seqUser);
            
            int storageType = Integer.parseInt(storeJo.get("storage_type").toString());
            
            Iterator<File> imgIter = files.iterator();
            File f = null;
            while (imgIter.hasNext()) {
                
                f = (File) imgIter.next();
                
                if (storageType == this.STORAGE_TYPE_LOCAL) {
                    
                    String pathDest = this.convertValidPath(this.servletContextPath + this.repositoryPath + seqClass);
                    FileUtil.makeDirs(pathDest);
                    FileUtil.moveFile(f, new File(pathDest + "/" + f.getName()));
                    
                } else if (storageType == this.STORAGE_TYPE_GOOGLE) {
                    
                    GcpService gcpSvc = new GcpService();
                    GoogleDrive gd = new GoogleDrive(gcpSvc.getAccessToken(seqUser));
                    String parentFolderId = gd.getFolderId(PropertiesUtil.get("APP_NAME") + "/" + this.repositoryPath + seqClass);
                    String fileId = gd.uploadBasic(parentFolderId, f);
                    
                    String fileName = FileUtil.getFullName(f.getName());
                    GDFileInfoDataAccessObject gdfDao = new GDFileInfoDataAccessObject();
                    gdfDao.addFile(fileName, fileId, gdfDao.GD_FILE_TYPE_FILE, typeClass, seqClass);
                    
                }
            }
        }
    }

    /**
     * 지정한 경로안에 있는 이미지 목록을 조회하여 이미지 정보를 반환
     * 
     * @param seqUser
     * @param seqClass (Box or Item)
     * @param typeClass (Box or Item)
     * @return
     * @throws Exception
     */
    public JSONObject getImageInfo(String seqUser, int storageType, String seqClass, String typeClass) throws Exception {
        
        JSONObject resJson = new JSONObject();
        
        StoreDataAccessObject sdao = new StoreDataAccessObject();
//        int storageType = sdao.getStorageType(seqUser);
        resJson.put("storage_type", storageType);
        
        if (storageType == this.STORAGE_TYPE_LOCAL) {

            ServiceLocal sl = new ServiceLocal();
            resJson.put("imgs", sl.getClassImgs(this.servletContextPath, this.repositoryPath, seqClass));

        } else if (storageType == this.STORAGE_TYPE_GOOGLE) {

            GcpService gcpSvc = new GcpService();
            String accessToken = this.googleAccessToken != null ? this.googleAccessToken : gcpSvc.getAccessToken(seqUser);
//            log.debug("["+seqUser+ "] 사용자의 access token : " + accessToken);
            resJson.put("access_token", accessToken);

            GoogleDrive gd = new GoogleDrive(accessToken);
            resJson.put("files", gd.getClassImgs(seqUser, seqClass, typeClass));

        }
        
        return resJson;
    }
    
    public JSONObjectExt deleteImage(String seqUser, String seqClass, String typeClass, String imageId) throws Exception {
        JSONObjectExt resJson = new JSONObjectExt();

        StoreDataAccessObject sdao = new StoreDataAccessObject();
        int storageType = sdao.getStorageType(seqUser);
        resJson.put("storage_type", storageType);

        if (storageType == this.STORAGE_TYPE_LOCAL) {

            ServiceLocal sl = new ServiceLocal();
            boolean result = sl.deleteClassImg(this.servletContextPath, this.repositoryPath, seqClass, imageId);
            resJson.put("result", result);

        } else if (storageType == this.STORAGE_TYPE_GOOGLE) {
            
            GcpService gcpSvc = new GcpService();
            String accessToken = gcpSvc.getAccessToken(seqUser);
            log.debug("[" + seqUser + "] 사용자의 access token : " + accessToken);
            log.debug(" file id : " + imageId);
            GoogleDrive gd = new GoogleDrive(accessToken);
            gd.deleteFile(imageId);
            
            UserDataAccessObject userDao = new UserDataAccessObject();
            String seqStore = userDao.getDefaultStoreSeq(seqUser);
            GDFileInfoDataAccessObject gdfDao = new GDFileInfoDataAccessObject();
            gdfDao.deleteFile(imageId, seqStore);
            resJson.put("result", true);
            
        }

        return resJson;
    }
}
