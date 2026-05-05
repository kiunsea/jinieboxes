package com.omnibuscode.logic.jbg.ftp;

import java.io.File;
import java.nio.file.Files;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.omnibuscode.dao.JbgInfoDataAccessObject;

/**
 * FTP 업로드 파일 복호화
 * 
 * jangbogo에서 Public Key로 암호화한 파일을
 * jiniebox의 Private Key로 복호화합니다.
 * 
 * 지원 형식:
 * 1. 작은 파일: RSA 직접 복호화
 * 2. 큰 파일: Hybrid 복호화 (AES + RSA)
 * 
 * @author KIUNSEA
 */
public class FtpFileDecryptor {
    
    private Logger log = LogManager.getLogger(FtpFileDecryptor.class);
    
    /**
     * 암호화된 파일을 복호화
     * 
     * @param encryptedFile 암호화된 파일
     * @param ftpUserId FTP 사용자 ID
     * @return 복호화된 JSON 문자열
     * @throws Exception
     */
    public String decryptFile(File encryptedFile, String ftpUserId) throws Exception {
        
        log.info("파일 복호화 시작: " + encryptedFile.getName() + " (FTP ID: " + ftpUserId + ")");
        
        // 1. ftpUserId로 seq_user 조회
        JbgInfoDataAccessObject jbgInfoDao = new JbgInfoDataAccessObject();
        String seqUser = jbgInfoDao.getUserSeqByFtpId(ftpUserId);
        
        if (seqUser == null) {
            throw new IllegalArgumentException("FTP ID에 매핑된 사용자가 없습니다: " + ftpUserId);
        }
        
        log.debug("seq_user 조회 완료: " + seqUser);
        
        // 2. Private Key 조회
        String privateKeyBase64 = jbgInfoDao.getPrivateKey(seqUser);
        
        if (privateKeyBase64 == null || privateKeyBase64.isEmpty()) {
            throw new IllegalStateException("사용자 " + seqUser + "의 Private Key가 없습니다.");
        }
        
        log.debug("Private Key 조회 완료 (길이: " + privateKeyBase64.length() + ")");
        
        // 3. 파일 읽기
        byte[] encryptedData = Files.readAllBytes(encryptedFile.toPath());
        
        log.debug("암호화된 파일 읽기 완료 (크기: " + encryptedData.length + " bytes)");
        
        // 4. Private Key 복원
        PrivateKey privateKey = getPrivateKeyFromBase64(privateKeyBase64);
        
        // 5. 복호화 (파일 크기에 따라 방식 선택)
        // 암호화 측(jangbogo)과 동일한 기준 사용: 245바이트 이하는 직접 RSA, 이상은 Hybrid
        // RSA 2048비트 키로 암호화된 데이터는 256바이트이므로, 256바이트를 기준으로 판단
        String decryptedContent;
        if (encryptedData.length > 256) {
            // 암호화된 크기가 256바이트보다 크면 Hybrid 방식
            log.debug("Hybrid 복호화 모드 (AES + RSA)");
            decryptedContent = decryptFileLarge(encryptedData, privateKey);
        } else {
            // 암호화된 크기가 256바이트 이하면 직접 RSA 복호화
            log.debug("직접 RSA 복호화 모드");
            decryptedContent = decryptFileSmall(encryptedData, privateKey);
        }
        
        log.info("파일 복호화 완료 (복호화 후 크기: " + decryptedContent.length() + " bytes)");
        
        return decryptedContent;
    }
    
    /**
     * 작은 파일 복호화 (RSA 직접 복호화)
     * 
     * @param encryptedData 암호화된 데이터
     * @param privateKey RSA Private Key
     * @return 복호화된 문자열
     * @throws Exception
     */
    private String decryptFileSmall(byte[] encryptedData, PrivateKey privateKey) throws Exception {
        try {
            Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
            cipher.init(Cipher.DECRYPT_MODE, privateKey);
            byte[] decryptedData = cipher.doFinal(encryptedData);
            log.debug("  RSA 복호화 성공 (복호화 후 크기: " + decryptedData.length + " bytes)");
            return new String(decryptedData, "UTF-8");
        } catch (Exception e) {
            log.error("RSA 복호화 실패 - 암호화 데이터 크기: " + encryptedData.length + " bytes");
            log.error("  Private Key 알고리즘: " + privateKey.getAlgorithm());
            log.error("  Private Key 포맷: " + privateKey.getFormat());
            log.error("  오류 메시지: " + e.getMessage());
            throw new Exception("RSA 복호화 실패: Public Key와 Private Key가 쌍이 아니거나 파일이 손상되었을 수 있습니다.", e);
        }
    }
    
    /**
     * 큰 파일 복호화 (Hybrid: AES + RSA)
     * 
     * 파일 형식: [AES키크기(4byte)][암호화된AES키][IV크기(4byte)][IV][암호화된파일데이터]
     * 
     * @param encryptedData 암호화된 데이터
     * @param privateKey RSA Private Key
     * @return 복호화된 문자열
     * @throws Exception
     */
    private String decryptFileLarge(byte[] encryptedData, PrivateKey privateKey) throws Exception {
        try {
            int offset = 0;
            
            // 1. AES 키 크기 읽기 (4 bytes)
            int aesKeySize = bytesToInt(encryptedData, offset);
            offset += 4;
            log.debug("  AES 키 크기: " + aesKeySize + " bytes");
            
            // 2. 암호화된 AES 키 읽기
            byte[] encryptedAesKey = new byte[aesKeySize];
            System.arraycopy(encryptedData, offset, encryptedAesKey, 0, aesKeySize);
            offset += aesKeySize;
            
            // 3. AES 키를 RSA로 복호화
            Cipher rsaCipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
            rsaCipher.init(Cipher.DECRYPT_MODE, privateKey);
            byte[] aesKeyBytes = rsaCipher.doFinal(encryptedAesKey);
            SecretKey aesKey = new SecretKeySpec(aesKeyBytes, "AES");
            log.debug("  AES 키 복호화 완료");
        
        // 4. IV 크기 읽기 (4 bytes)
        int ivSize = bytesToInt(encryptedData, offset);
        offset += 4;
        log.debug("  IV 크기: " + ivSize + " bytes");
        
        // 5. IV 읽기
        byte[] iv = new byte[ivSize];
        System.arraycopy(encryptedData, offset, iv, 0, ivSize);
        offset += ivSize;
        
        // 6. 암호화된 파일 데이터 읽기
        int encryptedFileDataSize = encryptedData.length - offset;
        byte[] encryptedFileData = new byte[encryptedFileDataSize];
        System.arraycopy(encryptedData, offset, encryptedFileData, 0, encryptedFileDataSize);
        log.debug("  암호화된 파일 데이터 크기: " + encryptedFileDataSize + " bytes");
        
            // 7. AES로 파일 데이터 복호화
            Cipher aesCipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            IvParameterSpec ivSpec = new IvParameterSpec(iv);
            aesCipher.init(Cipher.DECRYPT_MODE, aesKey, ivSpec);
            byte[] decryptedData = aesCipher.doFinal(encryptedFileData);
            log.debug("  파일 데이터 복호화 완료 (크기: " + decryptedData.length + " bytes)");
            
            return new String(decryptedData, "UTF-8");
            
        } catch (Exception e) {
            log.error("Hybrid 복호화 실패 - 전체 암호화 데이터 크기: " + encryptedData.length + " bytes");
            log.error("  오류 타입: " + e.getClass().getSimpleName());
            log.error("  오류 메시지: " + e.getMessage());
            throw new Exception("Hybrid 복호화 실패: Public Key와 Private Key가 쌍이 아니거나 파일 형식이 올바르지 않습니다.", e);
        }
    }
    
    /**
     * Base64 인코딩된 Private Key를 PrivateKey 객체로 변환
     * 
     * @param base64PrivateKey Base64 인코딩된 Private Key
     * @return PrivateKey 객체
     * @throws Exception
     */
    private PrivateKey getPrivateKeyFromBase64(String base64PrivateKey) throws Exception {
        byte[] keyBytes = Base64.getDecoder().decode(base64PrivateKey);
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        return keyFactory.generatePrivate(keySpec);
    }
    
    /**
     * 4바이트 배열을 int로 변환
     * 
     * @param bytes 바이트 배열
     * @param offset 시작 위치
     * @return 정수 값
     */
    private int bytesToInt(byte[] bytes, int offset) {
        return ((bytes[offset] & 0xFF) << 24) |
               ((bytes[offset + 1] & 0xFF) << 16) |
               ((bytes[offset + 2] & 0xFF) << 8) |
               (bytes[offset + 3] & 0xFF);
    }
}

