-- --------------------------------------------------------
-- 호스트:                          127.0.0.1
-- 서버 버전:                        11.0.2-MariaDB - mariadb.org binary distribution
-- 서버 OS:                        Win64
-- HeidiSQL 버전:                  12.3.0.6589
-- --------------------------------------------------------

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET NAMES utf8 */;
/*!50503 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;


-- jiniebox 데이터베이스 구조 내보내기
CREATE DATABASE IF NOT EXISTS `jiniebox` /*!40100 DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci */;
USE `jiniebox`;

-- 테이블 jiniebox.account_clova 구조 내보내기
CREATE TABLE IF NOT EXISTS `account_clova` (
  `seq` int(255) unsigned NOT NULL AUTO_INCREMENT,
  `user_id` char(50) NOT NULL COMMENT 'clova 의 user id',
  `seq_user` int(255) unsigned NOT NULL,
  PRIMARY KEY (`seq`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- 내보낼 데이터가 선택되어 있지 않습니다.

-- 테이블 jiniebox.account_naver 구조 내보내기
CREATE TABLE IF NOT EXISTS `account_naver` (
  `seq` int(255) unsigned NOT NULL AUTO_INCREMENT,
  `user_id` char(50) NOT NULL COMMENT 'naver 의 user id',
  `seq_user` int(255) unsigned NOT NULL,
  PRIMARY KEY (`seq`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- 내보낼 데이터가 선택되어 있지 않습니다.

-- 테이블 jiniebox.auto_barcode 구조 내보내기
CREATE TABLE IF NOT EXISTS `auto_barcode` (
  `seq` int(255) unsigned NOT NULL AUTO_INCREMENT,
  `barcode` char(50) NOT NULL,
  `seq_box` int(255) unsigned NOT NULL COMMENT '이동할 보관함',
  `seq_store` int(255) unsigned NOT NULL,
  PRIMARY KEY (`seq`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='분류 자동화 규칙 - 바코드';

-- 내보낼 데이터가 선택되어 있지 않습니다.

-- 테이블 jiniebox.auto_keyword 구조 내보내기
CREATE TABLE IF NOT EXISTS `auto_keyword` (
  `seq` int(255) unsigned NOT NULL AUTO_INCREMENT,
  `keywords` varchar(2000) NOT NULL COMMENT '키워드 목록 json(url encoded)',
  `keyoper` tinyint(1) unsigned NOT NULL DEFAULT 0 COMMENT '키 연산 (0 : OR, 1 : AND)',
  `matchcnt` tinyint(2) unsigned NOT NULL DEFAULT 0 COMMENT '적용 키워드 갯수 (0인 경우 모든 키워드)',
  `seq_box` int(255) unsigned NOT NULL COMMENT '이동할 보관함',
  `status` tinyint(1) unsigned NOT NULL DEFAULT 1 COMMENT '활성화 여부 (1:활성화, 0:비활성화)',
  `seq_store` int(255) unsigned NOT NULL,
  PRIMARY KEY (`seq`)
) ENGINE=InnoDB AUTO_INCREMENT=50 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='분류 자동화 규칙 - 키워드';

-- 내보낼 데이터가 선택되어 있지 않습니다.

-- 테이블 jiniebox.box 구조 내보내기
CREATE TABLE IF NOT EXISTS `box` (
  `seq` int(255) unsigned NOT NULL AUTO_INCREMENT,
  `name` char(50) DEFAULT '0' COMMENT '이름',
  `details` varchar(200) DEFAULT NULL COMMENT '상세',
  `type` tinyint(1) DEFAULT 0 COMMENT '등록대기:-1, 일반:0',
  `hide_after` int(3) unsigned NOT NULL DEFAULT 0 COMMENT '보관함의 아이템 보관 일수(day)',
  `seq_store` int(255) unsigned NOT NULL DEFAULT 0 COMMENT '어느 저장소의 박스인지',
  PRIMARY KEY (`seq`)
) ENGINE=InnoDB AUTO_INCREMENT=120 DEFAULT CHARSET=utf8mb3 COLLATE=utf8mb3_general_ci COMMENT='저장소내의 보관함';

-- 내보낼 데이터가 선택되어 있지 않습니다.

-- 테이블 jiniebox.fcm_token 구조 내보내기
CREATE TABLE IF NOT EXISTS `fcm_token` (
  `seq` int(255) unsigned NOT NULL AUTO_INCREMENT,
  `token` char(255) DEFAULT NULL COMMENT 'fcm token',
  `seq_user` int(255) unsigned NOT NULL,
  PRIMARY KEY (`seq`)
) ENGINE=InnoDB AUTO_INCREMENT=17 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='Firebase Cloud Messaging 용 user token 관리테이블';

-- 내보낼 데이터가 선택되어 있지 않습니다.

-- 테이블 jiniebox.gcp_token 구조 내보내기
CREATE TABLE IF NOT EXISTS `gcp_token` (
  `seq` int(255) unsigned NOT NULL AUTO_INCREMENT,
  `refresh_token` char(128) DEFAULT NULL COMMENT 'google refresh token',
  `refresh_expiry_date` int(8) NOT NULL DEFAULT 0 COMMENT 'refresh token 만료일 (YYYYMMDD)',
  `access_token` char(255) DEFAULT NULL COMMENT 'google access token',
  `access_expiry_time` bigint(20) NOT NULL DEFAULT 0 COMMENT 'access token 만료시간 (long type millisecond)',
  `seq_user` int(255) unsigned NOT NULL,
  PRIMARY KEY (`seq`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='Google Cloud Platform 용 사용자 인증 정보 (token 저장소)\r\n> user테이블과 1:1 (gcp 관련 설정값만 분리)';

-- 내보낼 데이터가 선택되어 있지 않습니다.

-- 테이블 jiniebox.gd_file_info 구조 내보내기
CREATE TABLE IF NOT EXISTS `gd_file_info` (
  `seq` int(255) unsigned NOT NULL AUTO_INCREMENT,
  `name` varchar(255) NOT NULL DEFAULT 'noname' COMMENT '파일명',
  `gd_file_id` varchar(255) DEFAULT NULL COMMENT '구글 드라이브 파일 아이디',
  `gd_file_type` tinyint(1) DEFAULT 0 COMMENT '0:file, 1:folder (folder 는 file 집합의 저장소를 의미)',
  `type_class` char(1) DEFAULT 'I' COMMENT 'I:item, B:box',
  `seq_class` int(255) DEFAULT -1 COMMENT '파일이 소속된 box 또는 item 의 seq',
  `seq_store` int(255) unsigned NOT NULL DEFAULT 0,
  PRIMARY KEY (`seq`)
) ENGINE=InnoDB AUTO_INCREMENT=35 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='Google Drive 에 생성한 File, Folder 정보';

-- 내보낼 데이터가 선택되어 있지 않습니다.

-- 테이블 jiniebox.invite 구조 내보내기
CREATE TABLE IF NOT EXISTS `invite` (
  `seq` int(255) unsigned NOT NULL AUTO_INCREMENT,
  `seq_owner` int(255) unsigned NOT NULL DEFAULT 0 COMMENT '초대장 생성자 (저장소 소유자)',
  `seq_user` int(255) unsigned DEFAULT 0 COMMENT '공유 받을 사람 SEQ',
  `juid` char(50) DEFAULT NULL COMMENT '공유 받을 사람 ID',
  `authority` char(1) NOT NULL DEFAULT 'R' COMMENT '부여할 자격 (Read, Modify)',
  `type_object` char(1) DEFAULT NULL COMMENT '공유하는 객체의 형태 : S(store), B(box), N(nanum)',
  `seq_object` int(255) unsigned NOT NULL DEFAULT 0 COMMENT '공유하는 객체의 시퀀스',
  `invite_code` char(10) NOT NULL DEFAULT '0' COMMENT '초대 CODE',
  `invite_url` varchar(255) DEFAULT NULL COMMENT '초대 URL',
  `expiry_date` int(8) DEFAULT NULL COMMENT '유효일자(YYMMDD)',
  `insert_time` bigint(20) DEFAULT NULL COMMENT '아이템 생성시간(millisecond)',
  PRIMARY KEY (`seq`),
  UNIQUE KEY `invite_code` (`invite_code`)
) ENGINE=InnoDB AUTO_INCREMENT=21 DEFAULT CHARSET=utf8mb3 COLLATE=utf8mb3_general_ci;

-- 내보낼 데이터가 선택되어 있지 않습니다.

-- 테이블 jiniebox.item 구조 내보내기
CREATE TABLE IF NOT EXISTS `item` (
  `seq` int(255) unsigned NOT NULL AUTO_INCREMENT,
  `name` char(50) NOT NULL COMMENT '아이템 이름',
  `barcode` varchar(30) DEFAULT NULL COMMENT '상품 바코드',
  `qty` int(255) DEFAULT 1 COMMENT '등록수량',
  `insert_date` int(8) DEFAULT NULL COMMENT '등록일자(YYYYMMDD)',
  `expiry_date` int(8) DEFAULT NULL COMMENT '만료일자(유통기한, YYYYMMDD)',
  `detail` varchar(200) DEFAULT NULL COMMENT '상세',
  `qty_deplete` int(255) DEFAULT 0 COMMENT '소진수량(누적이 가능하며 qty 보다 작거나 같음)',
  `seq_jbgorder` int(255) DEFAULT -1 COMMENT '구매정보',
  `seq_box` int(255) DEFAULT -1 COMMENT '저장 위치(동일한 박스에 여러 기간에 나누어 저장 가능)',
  `seq_user` int(255) NOT NULL COMMENT '소유자',
  `insert_time` bigint(20) NOT NULL COMMENT '아이템 생성시간(millisecond)',
  `hidden` tinyint(1) NOT NULL DEFAULT -1 COMMENT '숨김여부 (-1:보이기, 0:기록없음, 1:숨기기)',
  PRIMARY KEY (`seq`)
) ENGINE=InnoDB AUTO_INCREMENT=1970 DEFAULT CHARSET=utf8mb3 COLLATE=utf8mb3_general_ci COMMENT='* 아이템을 습득한 시점을 기준으로 저장 관리하는 테이블\r\n* 동일한 종류의 아이템이라도 습득한 시점에 따라 구분(insert_date)\r\n* 다음과 같은 경로로 획득한 모든 아이템 모음\r\n  - 사용자가 직접 등록하는 아이템\r\n  - 온라인/오프라인으로 쇼핑몰(마트)에서 구매한 아이템\r\n  - 지인으로부터 나눔받은 아이템\r\n  - 관리 이력이 확인되지 않는 아이템\r\n';

-- 내보낼 데이터가 선택되어 있지 않습니다.

-- 테이블 jiniebox.jbg_access 구조 내보내기
CREATE TABLE IF NOT EXISTS `jbg_access` (
  `seq_user` int(255) unsigned NOT NULL,
  `seq_jbgmall` int(255) unsigned NOT NULL,
  `account_status` tinyint(1) NOT NULL DEFAULT 0 COMMENT '서비스 이용 가능 여부(0:이용 불가, 1:이용 가능)',
  `encrypt_key` varchar(50) DEFAULT NULL COMMENT 'Encrypt SecretKey',
  `encrypt_iv` varchar(50) DEFAULT NULL COMMENT 'Encrypt IvParameterSpec',
  `last_signin_time` bigint(20) DEFAULT NULL COMMENT '마지막 접속 시간 (millisecond)',
  UNIQUE KEY `unique_mall_user` (`seq_jbgmall`,`seq_user`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='사용자가 쇼핑몰 이용자인지 여부와 접속 빈도를 관리';

-- 내보낼 데이터가 선택되어 있지 않습니다.

-- 테이블 jiniebox.jbg_info 구조 내보내기
CREATE TABLE IF NOT EXISTS `jbg_info` (
  `seq` int(255) unsigned NOT NULL AUTO_INCREMENT,
  `seq_user` int(255) unsigned NOT NULL COMMENT 'user.seq 와 1:1 매핑',
  `ftp_id` char(50) DEFAULT '' COMMENT 'FTP 계정 ID',
  `sec_priv_key` varchar(4096) DEFAULT NULL COMMENT 'RSA Private Key (지니박스 내부)',
  `sec_pub_key` varchar(1024) DEFAULT NULL COMMENT 'RSA Public Key (지니박스 → 장보고)',
  `auto_collect_enabled` int(10) unsigned NOT NULL DEFAULT 0 COMMENT '자동 수집 0: 비활성화, 1: 활성화',
  `auto_collect_interval` int(10) unsigned NOT NULL DEFAULT 5 COMMENT '자동 수집 주기 (분)',
  `insert_time` bigint(20) unsigned NOT NULL DEFAULT 0,
  `update_time` bigint(20) unsigned NOT NULL DEFAULT 0,
  PRIMARY KEY (`seq`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- 내보낼 데이터가 선택되어 있지 않습니다.

-- 테이블 jiniebox.jbg_mall 구조 내보내기
CREATE TABLE IF NOT EXISTS `jbg_mall` (
  `seq` int(255) unsigned NOT NULL AUTO_INCREMENT,
  `id` varchar(50) NOT NULL COMMENT '쇼핑몰 아이디',
  `name` varchar(200) NOT NULL DEFAULT '0' COMMENT '쇼핑몰 이름',
  `details` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`seq`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='장보고 온라인/오프라인 쇼핑몰(마트) 정보';

-- 내보낼 데이터가 선택되어 있지 않습니다.

-- 테이블 jiniebox.jbg_order 구조 내보내기
CREATE TABLE IF NOT EXISTS `jbg_order` (
  `seq` int(255) unsigned NOT NULL AUTO_INCREMENT,
  `serial_num` varchar(50) NOT NULL DEFAULT '0' COMMENT '시리얼 번호 (영수증 바코드 또는 주문번호)',
  `date_time` int(8) NOT NULL DEFAULT 0 COMMENT '구매일자(YYYYMMDD)',
  `mall_name` varchar(50) DEFAULT NULL COMMENT '매장명',
  `seq_jbgmall` int(255) NOT NULL,
  `seq_user` int(255) NOT NULL,
  PRIMARY KEY (`seq`)
) ENGINE=InnoDB AUTO_INCREMENT=205 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='장보고 쇼핑몰(마트) 구매정보';

-- 내보낼 데이터가 선택되어 있지 않습니다.

-- 테이블 jiniebox.nanum 구조 내보내기
CREATE TABLE IF NOT EXISTS `nanum` (
  `seq` int(255) unsigned NOT NULL AUTO_INCREMENT,
  `name` char(50) DEFAULT '0' COMMENT '이름',
  `details` varchar(200) DEFAULT NULL COMMENT '상세',
  `seq_store` int(255) unsigned NOT NULL DEFAULT 0,
  `access_level` char(1) NOT NULL DEFAULT 'F' COMMENT '접근 레벨 (Friends, Members, Open)',
  `share_code` varchar(50) DEFAULT NULL COMMENT '공유 URL작성용 코드',
  `access_code` char(10) DEFAULT NULL COMMENT '공유 정보 확인용 접속코드',
  PRIMARY KEY (`seq`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb3 COLLATE=utf8mb3_general_ci ROW_FORMAT=DYNAMIC COMMENT='저장소내의 나눔함';

-- 내보낼 데이터가 선택되어 있지 않습니다.

-- 테이블 jiniebox.nanum_favorite 구조 내보내기
CREATE TABLE IF NOT EXISTS `nanum_favorite` (
  `seq_nanum` int(255) unsigned NOT NULL DEFAULT 0 COMMENT '나눔 보관함',
  `seq_user` int(255) unsigned NOT NULL DEFAULT 0 COMMENT '즐겨찾기 등록자'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='나눔 즐겨찾기';

-- 내보낼 데이터가 선택되어 있지 않습니다.

-- 테이블 jiniebox.nanum_item 구조 내보내기
CREATE TABLE IF NOT EXISTS `nanum_item` (
  `seq` int(255) unsigned NOT NULL AUTO_INCREMENT,
  `seq_nanum` int(255) unsigned NOT NULL DEFAULT 0 COMMENT '나눔 보관함',
  `seq_item` int(255) unsigned NOT NULL DEFAULT 0 COMMENT '나눔 아이템',
  `detail` varchar(200) DEFAULT NULL COMMENT '상세',
  PRIMARY KEY (`seq`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=63 DEFAULT CHARSET=utf8mb3 COLLATE=utf8mb3_general_ci ROW_FORMAT=DYNAMIC COMMENT='나눔으로 공유하는 아이템의 링크를 저장';

-- 내보낼 데이터가 선택되어 있지 않습니다.

-- 테이블 jiniebox.share 구조 내보내기
CREATE TABLE IF NOT EXISTS `share` (
  `seq` int(255) unsigned NOT NULL AUTO_INCREMENT,
  `seq_user` int(255) unsigned NOT NULL DEFAULT 0 COMMENT '공유 받는 사람',
  `authority` char(1) NOT NULL DEFAULT 'R' COMMENT '공유 권한 : R(read), M(modify)',
  `type_object` char(1) DEFAULT NULL COMMENT '공유하는 객체의 형태 : S(store), B(box), N(nanum)',
  `seq_object` int(255) unsigned NOT NULL DEFAULT 0 COMMENT '공유하는 객체의 시퀀스',
  PRIMARY KEY (`seq`)
) ENGINE=InnoDB AUTO_INCREMENT=30 DEFAULT CHARSET=utf8mb3 COLLATE=utf8mb3_general_ci COMMENT='공유 정보';

-- 내보낼 데이터가 선택되어 있지 않습니다.

-- 테이블 jiniebox.store 구조 내보내기
CREATE TABLE IF NOT EXISTS `store` (
  `seq` int(255) unsigned NOT NULL AUTO_INCREMENT,
  `name` char(50) NOT NULL COMMENT '[옵션]저장소 이름',
  `details` varchar(200) DEFAULT NULL COMMENT '상세',
  `seq_owner` int(255) NOT NULL DEFAULT 0 COMMENT '소유자의 시퀀스',
  `insert_time` bigint(20) DEFAULT NULL COMMENT '등록시간',
  `img_use` tinyint(1) NOT NULL DEFAULT 0 COMMENT '박스와 아이템 이미지 사용 여부 -> 0 : false, 1 : true',
  `storage_type` tinyint(1) NOT NULL DEFAULT 1 COMMENT '사용자 파일 저장 위치 -> 0 : service local, 1 : google drive',
  `standby_days` int(3) NOT NULL DEFAULT 7 COMMENT '등록대기 보관함에 아이템 저장 일수(day)',
  PRIMARY KEY (`seq`)
) ENGINE=InnoDB AUTO_INCREMENT=28 DEFAULT CHARSET=utf8mb3 COLLATE=utf8mb3_general_ci;

-- 내보낼 데이터가 선택되어 있지 않습니다.

-- 테이블 jiniebox.user 구조 내보내기
CREATE TABLE IF NOT EXISTS `user` (
  `seq` int(255) unsigned NOT NULL AUTO_INCREMENT,
  `juid` char(50) NOT NULL COMMENT 'jiniebox user email',
  `jupw` char(50) NOT NULL COMMENT 'jiniebox user pw',
  `buid` char(50) DEFAULT NULL COMMENT 'bixby user id',
  `kakaoid` char(50) DEFAULT NULL COMMENT 'kakao user id',
  `googleid` char(50) DEFAULT NULL COMMENT 'google user id',
  `juname` char(50) DEFAULT NULL COMMENT '[옵션] 사용자 이름',
  `seq_defstore` int(255) unsigned DEFAULT NULL COMMENT '기본 저장소',
  `insert_time` bigint(20) unsigned DEFAULT NULL COMMENT '등록시간',
  `last_signin` bigint(12) DEFAULT NULL COMMENT '마지막 로그인(yyyyMMddHHmm)',
  `verified` tinyint(1) unsigned NOT NULL DEFAULT 0 COMMENT '인증여부 (0:미인증, 1:인증됨)',
  `verifycd` char(50) DEFAULT NULL COMMENT '인증코드 (code)',
  `verifyed` int(8) unsigned DEFAULT NULL COMMENT '인증코드 유효일자 (expire date, yyyyMMdd)',
  `is_partner` tinyint(1) unsigned NOT NULL DEFAULT 0 COMMENT '사용자 구분 (0:일반 사용자, 1:파트너)',
  `first_visit` tinyint(1) unsigned NOT NULL DEFAULT 1 COMMENT '회원가입후 첫방문 여부 (1:true, 0:false)',
  PRIMARY KEY (`seq`),
  UNIQUE KEY `juid` (`juid`)
) ENGINE=InnoDB AUTO_INCREMENT=31 DEFAULT CHARSET=utf8mb3 COLLATE=utf8mb3_general_ci;

-- 내보낼 데이터가 선택되어 있지 않습니다.

-- 테이블 jiniebox.zzim 구조 내보내기
CREATE TABLE IF NOT EXISTS `zzim` (
  `seq` int(255) unsigned NOT NULL AUTO_INCREMENT,
  `seq_nitem` int(255) unsigned NOT NULL COMMENT '나눔한 아이템(nanum_item.seq)',
  `seq_user` int(255) unsigned NOT NULL COMMENT '아이템을 찜한 사용자',
  `zzim_qty` tinyint(255) unsigned NOT NULL DEFAULT 1 COMMENT '필요갯수',
  `insert_time` bigint(20) unsigned DEFAULT NULL COMMENT '등록시간',
  `shared` tinyint(1) unsigned zerofill NOT NULL DEFAULT 0 COMMENT '0:신규추가, -1:나눔취소, 1:나눔받음',
  PRIMARY KEY (`seq`)
) ENGINE=InnoDB AUTO_INCREMENT=15 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='나눔을 찜한 사용자 목록';

-- 내보낼 데이터가 선택되어 있지 않습니다.

/*!40103 SET TIME_ZONE=IFNULL(@OLD_TIME_ZONE, 'system') */;
/*!40101 SET SQL_MODE=IFNULL(@OLD_SQL_MODE, '') */;
/*!40014 SET FOREIGN_KEY_CHECKS=IFNULL(@OLD_FOREIGN_KEY_CHECKS, 1) */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40111 SET SQL_NOTES=IFNULL(@OLD_SQL_NOTES, 1) */;
