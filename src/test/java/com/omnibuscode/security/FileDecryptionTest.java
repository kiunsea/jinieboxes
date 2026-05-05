package com.omnibuscode.security;

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

import com.omnibuscode.dao.JbgInfoDataAccessObject;

/**
 * 로컬에 저장된 암호화 파일을 Private Key로 복호화하는 테스트 클래스
 * 
 * 테스트 시나리오:
 * 1. DB에서 사용자의 Private Key 조회
 * 2. 로컬 파일 읽기
 * 3. Private Key로 파일 복호화
 * 4. 복호화된 JSON 내용 출력
 * 
 * @author KIUNSEA
 */
public class FileDecryptionTest {
    
    /**
     * 테스트 설정
     */
    private static final String TEST_USER_SEQ = "3"; // 실제 사용자 seq로 변경 필요
    private static final String ENCRYPTED_FILE_PATH = "D:/DEV/ftp/apache-ftpserver-1.1.4/res/home/jiniebox/jangbogo_orders_20251109_055854.json.encrypted"; // 암호화된 파일 경로
    
    /**
     * 메인 테스트 메서드
     */
    public static void main(String[] args) {
        FileDecryptionTest test = new FileDecryptionTest();
        
        try {
            System.out.println("========================================");
            System.out.println("암호화 파일 복호화 테스트");
            System.out.println("========================================\n");
            
            // 암호화된 파일 경로 (커맨드라인 인수 또는 기본값)
            String filePath = args.length > 0 ? args[0] : ENCRYPTED_FILE_PATH;
            String userSeq = args.length > 1 ? args[1] : TEST_USER_SEQ;
            
            test.decryptLocalFile(filePath, userSeq);
            
        } catch (Exception e) {
            System.err.println("❌ 테스트 중 오류 발생:");
            e.printStackTrace();
        }
    }
    
    /**
     * 로컬 파일 복호화 테스트
     * 
     * @param encryptedFilePath 암호화된 파일 경로
     * @param seqUser 사용자 seq
     */
    public void decryptLocalFile(String encryptedFilePath, String seqUser) {
        try {
            System.out.println("암호화된 파일: " + encryptedFilePath);
            System.out.println("사용자 seq: " + seqUser);
            System.out.println();
            
            // 1. DB에서 Private Key 조회
            System.out.println("1. DB에서 Private Key 조회 중...");
            String privateKeyBase64 = getPrivateKeyFromDB(seqUser);
            
            if (privateKeyBase64 == null || privateKeyBase64.isEmpty()) {
                System.err.println("❌ Private Key가 DB에 없습니다.");
                System.err.println("   먼저 jiniebox의 store_jbg.jsp에서 키를 생성해주세요.");
                return;
            }
            
            System.out.println("✓ Private Key 조회 완료");
            System.out.println("  Key 미리보기: " + privateKeyBase64.substring(0, 50) + "...\n");
            
            // 2. 파일 읽기
            System.out.println("2. 암호화된 파일 읽기 중...");
            File file = new File(encryptedFilePath);
            
            if (!file.exists()) {
                System.err.println("❌ 파일이 존재하지 않습니다: " + encryptedFilePath);
                return;
            }
            
            byte[] encryptedData = Files.readAllBytes(file.toPath());
            System.out.println("✓ 파일 읽기 완료 (크기: " + encryptedData.length + " bytes)\n");
            
            // 3. 파일 복호화
            System.out.println("3. 파일 복호화 중...");
            String decryptedContent = decryptFile(encryptedData, privateKeyBase64);
            
            if (decryptedContent == null) {
                System.err.println("❌ 파일 복호화 실패");
                return;
            }
            
            System.out.println("✓ 파일 복호화 완료\n");
            
            // 4. 복호화된 내용 출력
            System.out.println("========================================");
            System.out.println("복호화된 JSON 내용:");
            System.out.println("========================================");
            System.out.println(decryptedContent);
            System.out.println("========================================\n");
            
            System.out.println("✅ 테스트 완료!");
            
        } catch (Exception e) {
            System.err.println("❌ 테스트 실패:");
            e.printStackTrace();
        }
    }
    
    /**
     * DB에서 Private Key 조회
     * 
     * @param seqUser 사용자 seq
     * @return Base64 인코딩된 Private Key
     */
    private String getPrivateKeyFromDB(String seqUser) {
        try {
            JbgInfoDataAccessObject jbgInfoDao = new JbgInfoDataAccessObject();
            return jbgInfoDao.getPrivateKey(seqUser);
        } catch (Exception e) {
            System.err.println("❌ DB 조회 실패: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }
    
    /**
     * 암호화된 파일 데이터를 Private Key로 복호화
     * 
     * 지원 형식:
     * 1. 작은 파일: RSA 직접 복호화
     * 2. 큰 파일: Hybrid 복호화 (AES + RSA)
     *    형식: [AES키크기(4byte)][암호화된AES키][IV크기(4byte)][IV][암호화된파일데이터]
     * 
     * @param encryptedData 암호화된 데이터
     * @param privateKeyBase64 Base64 인코딩된 Private Key
     * @return 복호화된 문자열 (JSON 내용)
     */
    private String decryptFile(byte[] encryptedData, String privateKeyBase64) {
        try {
            // Private Key 복원
            PrivateKey privateKey = getPrivateKeyFromBase64(privateKeyBase64);
            
            // Hybrid 방식인지 확인 (파일 크기가 256보다 크면 Hybrid)
            if (encryptedData.length > 256) {
                System.out.println("  → Hybrid 복호화 모드 (AES + RSA)");
                return decryptFileLarge(encryptedData, privateKey);
            } else {
                System.out.println("  → 직접 RSA 복호화 모드");
                return decryptFileSmall(encryptedData, privateKey);
            }
            
        } catch (Exception e) {
            System.err.println("❌ 복호화 실패: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }
    
    /**
     * 작은 파일 복호화 (RSA 직접 복호화)
     */
    private String decryptFileSmall(byte[] encryptedData, PrivateKey privateKey) throws Exception {
        Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        cipher.init(Cipher.DECRYPT_MODE, privateKey);
        byte[] decryptedData = cipher.doFinal(encryptedData);
        return new String(decryptedData, "UTF-8");
    }
    
    /**
     * 큰 파일 복호화 (Hybrid: AES + RSA)
     * 
     * 파일 형식: [AES키크기(4byte)][암호화된AES키][IV크기(4byte)][IV][암호화된파일데이터]
     */
    private String decryptFileLarge(byte[] encryptedData, PrivateKey privateKey) throws Exception {
        int offset = 0;
        
        // 1. AES 키 크기 읽기 (4 bytes)
        int aesKeySize = bytesToInt(encryptedData, offset);
        offset += 4;
        System.out.println("  AES 키 크기: " + aesKeySize + " bytes");
        
        // 2. 암호화된 AES 키 읽기
        byte[] encryptedAesKey = new byte[aesKeySize];
        System.arraycopy(encryptedData, offset, encryptedAesKey, 0, aesKeySize);
        offset += aesKeySize;
        
        // 3. AES 키를 RSA로 복호화
        Cipher rsaCipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        rsaCipher.init(Cipher.DECRYPT_MODE, privateKey);
        byte[] aesKeyBytes = rsaCipher.doFinal(encryptedAesKey);
        SecretKey aesKey = new SecretKeySpec(aesKeyBytes, "AES");
        System.out.println("  ✓ AES 키 복호화 완료");
        
        // 4. IV 크기 읽기 (4 bytes)
        int ivSize = bytesToInt(encryptedData, offset);
        offset += 4;
        System.out.println("  IV 크기: " + ivSize + " bytes");
        
        // 5. IV 읽기
        byte[] iv = new byte[ivSize];
        System.arraycopy(encryptedData, offset, iv, 0, ivSize);
        offset += ivSize;
        
        // 6. 암호화된 파일 데이터 읽기
        int encryptedFileDataSize = encryptedData.length - offset;
        byte[] encryptedFileData = new byte[encryptedFileDataSize];
        System.arraycopy(encryptedData, offset, encryptedFileData, 0, encryptedFileDataSize);
        System.out.println("  암호화된 파일 데이터 크기: " + encryptedFileDataSize + " bytes");
        
        // 7. AES로 파일 데이터 복호화
        Cipher aesCipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        IvParameterSpec ivSpec = new IvParameterSpec(iv);
        aesCipher.init(Cipher.DECRYPT_MODE, aesKey, ivSpec);
        byte[] decryptedData = aesCipher.doFinal(encryptedFileData);
        System.out.println("  ✓ 파일 데이터 복호화 완료 (크기: " + decryptedData.length + " bytes)");
        
        return new String(decryptedData, "UTF-8");
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
     */
    private int bytesToInt(byte[] bytes, int offset) {
        return ((bytes[offset] & 0xFF) << 24) |
               ((bytes[offset + 1] & 0xFF) << 16) |
               ((bytes[offset + 2] & 0xFF) << 8) |
               (bytes[offset + 3] & 0xFF);
    }
}

