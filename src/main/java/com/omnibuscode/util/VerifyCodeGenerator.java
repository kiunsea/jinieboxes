package com.omnibuscode.util;

import java.util.Random;

/**
 * 인증 코드 생성기
 * @author KIUNSEA
 *
 */
public class VerifyCodeGenerator {

    public static final int INVITECODE_LENGTH = 25;

//    public static void main(String args[]) {
//        InvitecodeGenerator ig = new InvitecodeGenerator();
//        ig.createInvitecode();
//    }
    
    /**
     * 숫자와 영어 대문자 조합으로 지정한 길이로 생성
     * @param length 1보다 작으면 기본값(25)으로 설정됨
     * @return
     */
    public static String generateCode(int length) {

    	if (length < 1) {
    		length = VerifyCodeGenerator.INVITECODE_LENGTH;
    	}
        String codes = "123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ";

        Random rg = new Random();
        StringBuffer inviteCodeSb = new StringBuffer();
        for (int i = 0; i < length; i++) {
            inviteCodeSb.append(codes.charAt(rg.nextInt(codes.length())));
        }

        return inviteCodeSb.toString();
    }
}
