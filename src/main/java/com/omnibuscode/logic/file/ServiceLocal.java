package com.omnibuscode.logic.file;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.json.simple.JSONObject;

import com.omnibuscode.utils.FileUtil;
import com.omnibuscode.utils.PropertiesUtil;

public class ServiceLocal {

//    /**
//     * 지정한 경로안에 있는 이미지 목록을 조회하여 이미지의 경로 목록을 반환
//     * 
//     * @param folderName
//     * @return
//     */
//    public List<JSONObject> getImgUriList(String servletContextRoot, String repositoryPath, String folderName) {
//
//        File fileRepo = new File(servletContextRoot + repositoryPath + folderName);
//        List<JSONObject> imgs = null;
//        File[] childsF = fileRepo.listFiles();
//        if (childsF != null) {
//            imgs = new ArrayList<JSONObject>();
//            JSONObject jo = null;
//            for (int i = 0; i < childsF.length; i++) {
//                jo = new JSONObject();
//                jo.put("src", repositoryPath + folderName + "/" + FileUtil.getFullName(childsF[i].getAbsolutePath()));
//                imgs.add(jo);
//            }
//            return imgs;
//        }
//        return null;
//    }
    
    /**
     * 지정한 경로안에 있는 이미지 목록을 조회하여 이미지의 경로 목록을 반환
     * 
     * @param servletContextRoot
     * @param repositoryPath
     * @param seqClass
     * @return
     * @throws Exception
     */
    public List<JSONObject> getClassImgs(String servletContextRoot, String repositoryPath, String seqClass) throws Exception {
        File fileRepo = new File(servletContextRoot + repositoryPath + seqClass);
        List<JSONObject> imgs = null;
        File[] childsF = fileRepo.listFiles();
        if (childsF != null) {
            imgs = new ArrayList<JSONObject>();
            JSONObject jo = null;
            String fName = null;
            for (int i = 0; i < childsF.length; i++) {
                fName = FileUtil.getFullName(childsF[i].getAbsolutePath());
                jo = new JSONObject();
                jo.put("id", fName);
                jo.put("src", repositoryPath + seqClass + "/" + fName);
                imgs.add(jo);
            }
            return imgs;
        }
        return null;
    }
    
    /**
     * 지정한 경로의 이미지를 삭제한다
     * 
     * @param servletContextRoot
     * @param repositoryPath
     * @param seqClass
     * @param imageId
     * @return
     */
    public boolean deleteClassImg(String servletContextRoot, String repositoryPath, String seqClass, String imageId) {
        String pathImage = servletContextRoot + repositoryPath + seqClass + "/" + imageId;
        return FileUtil.deleteFile(new File(pathImage));
    }
}
