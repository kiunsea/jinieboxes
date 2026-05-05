package com.omnibuscode.base;

/**
 * 단일 객체 저장용 싱글톤 클래스
 * @author KIUNSEA
 *
 */
public class EnvSYS {
	
    public static String SYS_RES_PATH = null; // Servlet Context Root + "WEB-INF/classes/res/" 의 경로
    
    /**
     * FTP 서버 관리자 인스턴스 (전역 접근용)
     */
    public static com.omnibuscode.ftp.FtpServerManager ftpServerManager = null;
    
    /**
     * FTP 서버 실행 경로 (전역 공유)
     */
    public static String FTP_SERVER_PATH = null;
    
    /**
     * 객체 구분을 위한 이니셜 정의 (TYPE_OBJECT)
     */
    public static final String CLASS_TYPE_STORE = "S";
    public static final String CLASS_TYPE_BOX = "B";
    public static final String CLASS_TYPE_NANUM = "N";
    public static final String CLASS_TYPE_ITEM = "I";
    
	public static final String KEY_CLIENT_TYPE_BIXBY = "CLIENT_TYPE_BIXBY";
	public static final String KEY_CLIENT_TYPE_KAKAO = "CLIENT_TYPE_KAKAO";
    public static final String KEY_CLIENT_TYPE_NAVER = "CLIENT_TYPE_NAVER";
	public static final String KEY_CLIENT_TYPE_WEB = "CLIENT_TYPE_WEB";

    /**
     * 인증이 성공한 사용자 정보로써 반환값은 UserSession instance
     */
    public static final String KEY_USER_SESSION = "JINIEBOX_USER_SS";
    /**
     * 사용자 검증 결과로써 반환값은 AuthInfo instance
     */
    public static final String KEY_USER_AUTH_INFO = "JINIEBOX_USER_VI";
    /**
     * 세션에 임시 저장하는 아이디 참조 변수명
     */
    public static final String KEY_KAKAO_USER_ID = "KAKAO_USER_ID";
    public static final String KEY_NAVER_USER_ID = "NAVER_USER_ID";
    public static final String KEY_CLOVA_USER_ID = "CLOVA_USER_ID";
    /**
     * 시스템 예약어
     */
    public static final String RESERVED_WAITINGBOX = "등록대기";
//    public static final String RESERVED_DEFAULTBOX = "기본박스";
    public static final String RESERVED_COSMETICBOX = "화장품";
    public static final String RESERVED_LIFEGOODSBOX = "생활용품";
    public static final String RESERVED_TOYSBOX = "장난감함";
    public static final String RESERVED_FRIDGE__COMP1 = "실온실";
    public static final String RESERVED_FRIDGE__COMP2 = "냉장실";
    public static final String RESERVED_FREEZER_COMP = "냉동실";
    public static final String RESERVED_UTILITY_COMP = "다용도실";
    /**
     * 시스템 메세지
     */
    public static final String RESMSG_CREATEBOXSYS = "시스템에서 자동 생성한 박스입니다";
    
    /**
     * 000 : 수행 성공
     */
    public static final String RESCODE_SUCC = "000";
    /**
     * 001 : 수행 실패
     */
    public static final String RESCODE_FAIL = "001";
    public static final String RESMSG_FAIL = "* 시스템 에러입니다";
    public static final String RESMSG_INVREQ= "* 잘못된 요청입니다 (명령어가 없습니다)";
    public static final String RESMSG_USERSESSION_FAIL = "사용자 로그인 정보가 없습니다\n계속하시려면 로그인 해주세요";
    /**
     * 002 : 선택
     */
    public static final String RESCODE_SELECT = "002";
    public static final String RESMSG_SELECT = "* 선택이 필요합니다";
    /**
     * 003 : 박스 없음
     */
    public static final String RESCODE_NOBOX = "003";
    public static final String RESMSG_NOBOX  = "* 보관함이 없습니다";
    /**
     * 010 : 인증 성공
     */
    public static final String RESAUTH_SUCC = "010";
    /**
     * 011 : 인증 실패 (부정한 입력값)
     */
    public static final String RESAUTH_FAIL = "011";
    /**
     * 012 : 인증 실패 (사용자 정보 없음)
     */
    public static final String RESGUEST_USER = "012";
    /**
     * 013 : 인증 실패 (이메일 인증 필요)
     */
    public static final String RESNOTYET_VERIFY = "013";
    
    /**
     * 100 : 페이지 닫음 (close page)
     */
    public static final String RESCODE_CLOSE = "100";
    
    private static EnvSYS instance;
    private EnvSYS() {
        ;
    }
    public static synchronized EnvSYS getInstance() {
        if (instance == null) {
            instance = new EnvSYS();
        }
        return instance;
    }
    
}