package com.omnibuscode.logic.jbg.ftp;

import static org.junit.jupiter.api.Assertions.*;

import java.io.File;

import org.junit.jupiter.api.Test;

class JangbogoDataParserTest {

	@Test
	void test() {
		// TODO: 테스트 구현 필요
		// fail("Not yet implemented");
		
		// FtpFileDecryptor 인스턴스 생성
		FtpFileDecryptor decryptor = new FtpFileDecryptor();
		
		// 테스트용 파일 및 FTP ID 설정 (실제 값으로 변경 필요)
		File file = new File("test_file.json.encrypted");
		String ftpUserId = "test_ftp_user";
		
		JangbogoDataParser jbdp = new JangbogoDataParser();
		
		try {
			String jsonContent = decryptor.decryptFile(file, ftpUserId);
			jbdp.parse(jsonContent);
		} catch (Exception e) {
			fail("테스트 실패: " + e.getMessage());
		}
	}

}
