<%-- <%@ page import="jakarta.servlet.ServletContext" %> --%>
<%@ page import="com.omnibuscode.auth.AuthManager" %>
<%@ page import="com.omnibuscode.base.UserSession" %>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
    boolean is_partner = false;	
    UserSession us = AuthManager.getInstance().getUserSession(request);
    if (us != null) {
		is_partner = us.isPartner();
    }

    String quickBtnList = "btn_list_top,btn_onboarding,bd-theme";
%>
<!DOCTYPE html>
<html lang="ko" data-bs-theme="auto">

<head>
    <jsp:include page="./include/head.jsp">
        <jsp:param name="page_name" value="store_jbg" />
        <jsp:param name="title" value="지니박스 장보고" />
        <jsp:param name="quick_btn_list" value="<%= quickBtnList %>" />
    </jsp:include>
</head>

<body>
    <jsp:include page="./include/main_menu.jsp">
        <jsp:param name="is_partner" value="<%= is_partner %>" />
    </jsp:include>
    <div class="container-fluid">
        <div class="row">
            <main class="col-md-12 ms-sm-auto col-lg-12 px-md-4">

                <jsp:include page="./include/quick_buttons.jsp">
                    <jsp:param name="quick_btn_list" value="<%= quickBtnList %>" />
                </jsp:include>

                <hr class="mb-3">
                <div class="container">
                    <h2>장보고(Jangbogo)</h2>
                    <div class="row" id="card-row">
                        <div class="col-sm-12 mb-3 mb-sm-3">
                            <div id="card" class="card">
                                <img src="/jbs/res/web/images/shopping_items.png" class="card-img-top" alt="shopping_items">
                                <div class="card-body">
                                    <h5 id="card-title" class="card-title">쇼핑 데이터 수집(자동화)</h5>
                                    <p class="card-text">온라인/오프라인 쇼핑몰에서 구매한 상품들을 자동으로 수집하여 저장합니다.</p>
                                    <p class="card-text">쇼핑몰 계정 정보는 지니박스 서비스에 저장하지 않습니다.</p>
                                    <p class="card-text">개인 브라우저에 암호화되어 저장되므로 브라우저 변경시 다시 연결하셔야 합니다.</p>
                                </div>
                                <hr class="mb-3" style="border-top: 1px dotted gray;">
                                
                                <div id="jangbogo_config_div" class="card-body">
                                    <h5 class="card-title">장보고 설정</h5>
									<div class="mb-3">
                                        <div class="row mb-2">
                                            <div class="col">
                                                <h6 class="mb-3">
                                                    <i class="bi bi-person-badge"></i> FTP 사용자 계정 설정
                                                </h6>
                                                <div class="ps-4">
                                                    <div class="mb-3">
                                                        <label for="ftp_account_id" class="form-label">
                                                            <i class="bi bi-person-circle"></i> 계정 ID
                                                        </label>
                                                    <div class="input-group">
                                                        <input type="text" 
                                                               class="form-control" 
                                                               id="ftp_account_id" 
                                                               placeholder="FTP 계정 ID를 입력하세요"
                                                               autocomplete="off"
                                                               oninput="clearFtpAccountCheckMsg()"
                                                               onblur="checkFtpAccountId()"
                                                               onkeypress="handleFtpIdKeyPress(event)"
                                                               readonly>
                                                        <button class="btn btn-outline-info" 
                                                                type="button" 
                                                                id="btn_enable_ftp_id_input"
                                                                onclick="enableFtpIdInput(event)"
                                                                title="아이디 갱신">
                                                            <i class="bi bi-pencil-square"></i>
                                                        </button>
                                            </div>
                                                    <div id="ftp_account_check_msg" class="form-text" style="display: none;"></div>
										</div>
                                                <div class="mb-3">
                                                    <label for="ftp_account_password" class="form-label">
                                                        <i class="bi bi-lock-fill"></i> 계정 비밀번호
                                                    </label>
                                                    <input type="password" 
                                                           class="form-control" 
                                                           id="ftp_account_password" 
                                                           placeholder="FTP 계정 비밀번호를 입력하세요"
                                                           autocomplete="new-password">
										</div>
										
                                                <div class="mb-3">
                                                    <label for="jangbogo_public_key" class="form-label">
                                                        <i class="bi bi-key-fill"></i> Public Key (암호화용)
                                                    </label>
                                                    <div class="input-group">
                                                <input type="text" 
                                                       class="form-control" 
                                                       id="jangbogo_public_key" 
                                                       readonly
                                                       placeholder="생성된 Public Key가 여기에 표시됩니다." 
                                                       data-default-placeholder="생성된 Public Key가 여기에 표시됩니다."
                                                       data-null-placeholder="'키 생성' 버튼을 눌러 키를 생성하세요."
                                                       style="font-family: monospace; font-size: 0.85rem; background-color: #f8f9fa;">
                                                        <button class="btn btn-outline-secondary" 
                                                                type="button" 
                                                                onclick="generateAndSetPublicKey(event)"
                                                                title="키 생성">
                                                            <i class="bi bi-arrow-repeat"></i>
                                                        </button>
                                                        <button class="btn btn-outline-primary" 
                                                                type="button" 
                                                                onclick="copyJangbogoPublicKey(event)"
                                                                title="Public Key 복사">
                                                            <i class="bi bi-clipboard"></i>
                                                        </button>
                                                    </div>
                                                    <div class="form-text">
                                                        <i class="bi bi-info-circle"></i> 
                                                        '키 생성' 버튼을 누르면 새로운 키 쌍이 즉시 생성되어 저장됩니다.
                                                    </div>
									</div>
										
                                                <div class="d-grid">
                                                    <button type="button" 
                                                            class="btn btn-primary" 
                                                            id="btn_create_ftp_account"
                                                            onclick="createOrUpdateFtpAccount(event)">
                                                        <i class="bi bi-person-plus-fill"></i> 계정 생성/갱신
                                                        </button>
                                            </div>
                                                    <div class="alert alert-info mt-3 py-2 px-3 mb-0" style="font-size: 0.875rem;">
                                                        <i class="bi bi-info-circle-fill"></i> 
                                                        <strong>동작 방식:</strong><br>
                                                        • 새 ID 입력 → ID, 비밀번호 필수 (필요 시 '키 생성'으로 Public Key 발급)<br>
                                                        • 기존 ID 유지 + 비밀번호만 입력 → 비밀번호만 갱신<br>
                                                        • 기존 ID 유지 + '키 생성' 버튼 클릭 → Public Key가 즉시 저장됨<br>
                                                        <small class="text-muted">* '키 생성' 버튼으로 생성된 Public Key는 곧바로 DB에 저장됩니다.</small>
										</div>
											</div>
									</div>
                                            </div>
                                            
                                            <hr class="my-3" style="border-top: 1px dotted #dee2e6;">
                                            
                                            <div class="row mb-2">
                                                <div class="col">
                                                    <h6 class="mb-3">
                                                        <i class="bi bi-robot"></i> 자동 수집 설정
                                                    </h6>
                                                    <div class="ps-4">
                                                        <div class="form-check form-switch mb-3">
                                                            <input class="form-check-input" 
                                                                   type="checkbox" 
                                                                   id="auto_collect_enabled"
                                                                   onchange="toggleAutoCollect()">
                                                            <label class="form-check-label" for="auto_collect_enabled">
                                                                <strong>장보고 FTP 파일 자동 수집 활성화</strong>
                                                            </label>
                                                        </div>
                                                        <div class="mb-3">
                                                            <label for="auto_collect_interval" class="form-label mb-1" style="font-size: 0.875rem;">
                                                                <i class="bi bi-clock-fill"></i> 수집 주기
                                                            </label>
                                                            <select class="form-select form-select-sm" 
                                                                    id="auto_collect_interval" 
                                                                    onchange="changeCollectInterval()"
                                                                    style="width: 200px;">
                                                                <option value="1">1분</option>
                                                                <option value="5" selected>5분 (권장)</option>
                                                                <option value="10">10분</option>
                                                                <option value="30">30분</option>
                                                                <option value="60">1시간</option>
                                                            </select>
                                                            <div class="form-text" style="font-size: 0.75rem;">
                                                                <i class="bi bi-info-circle"></i> 선택한 주기마다 FTP 서버의 파일을 자동으로 처리합니다.
                                                            </div>
                                                        </div>
                                                        <div class="alert alert-warning py-2 px-3 mb-0" style="font-size: 0.875rem;">
                                                            <i class="bi bi-exclamation-triangle-fill"></i> 
                                                            자동 수집을 활성화하면 설정한 주기마다 FTP 서버의 업로드 파일을 확인하여 자동으로 처리합니다.
                                                        </div>
                                                    </div>
                                                </div>
                                            </div>
                                            
                                    <script type="text/javascript">
                                        /**
                                         * 장보고 RSA 키 쌍 새로 생성하여 입력 필드에 설정
                                         * (실제 저장은 '계정 생성/갱신' 버튼 클릭 시 수행)
                                         * @param {Event} e - 클릭 이벤트 객체
                                         */
                                        function generateAndSetPublicKey(e) {
                                            if (!confirm('새로운 Public Key를 생성하시겠습니까?\n\n생성 후 "계정 생성/갱신" 버튼을 눌러 저장해야 합니다.')) {
                                                return;
                                            }
                                            
                                            const btn = e.target.closest('button');
                                            const icon = btn.querySelector('i');
                                            const originalClass = icon.className;
                                            const originalText = btn.innerHTML;
                                            
                                            // 로딩 표시
                                            icon.className = 'spinner-border spinner-border-sm';
                                            btn.disabled = true;
                                            
                                            const path = "/jbs/jbg";
                                            const params = "cmd=generatePublicKeyOnly";
                                            
                                            const xhr = new XMLHttpRequest();
                                            xhr.onreadystatechange = function() {
                                                if (checkAjaxSuc(xhr)) {
                                                    const resMsg = xhr.responseText;
                                                    console.log('Public Key 생성 응답:', resMsg);
                                                    
                                                    if (resMsg.length > 0) {
                                                        try {
                                                            const resJson = JSON.parse(resMsg);
                                                            console.log('파싱된 응답:', resJson);
                                                            
                                                            if (resJson.code === '000') {
                                                                // Public Key 필드에 설정 (저장은 안 함)
                                                                if (resJson.publicKey) {
                                                                    setPublicKeyFieldValue(resJson.publicKey, false);
                                                                    console.log('Public Key 생성 완료:', resJson.publicKey.substring(0, 50) + '...');
                                                                    alert('✓ 새로운 Public Key가 생성되어 저장되었습니다.\n\n장보고 관리화면에서 이 값을 바로 사용할 수 있습니다.');
                                                                    loadJangbogoConfig();
                                                                } else {
                                                                    console.warn('Public Key가 응답에 없습니다.');
                                                                    alert('Public Key 생성에 실패했습니다.');
                                                                }
                                                            } else {
                                                                console.error('키 생성 실패:', resJson);
                                                                alert('키 생성 실패: ' + (resJson.msg || '알 수 없는 오류'));
                                                            }
                                                        } catch (e) {
                                                            console.error('응답 파싱 오류:', e, '원본 응답:', resMsg);
                                                            alert('응답 처리 중 오류가 발생했습니다.\n콘솔을 확인해주세요.');
                                                        }
                                                    } else {
                                                        console.warn('서버 응답이 비어있습니다.');
                                                    }
                                                    
                                                    // 버튼 복원
                                                    btn.innerHTML = originalText;
                                                    btn.disabled = false;
                                                }
                                            };
                                            sendPost(xhr, path, params);
                                        }
                                        
                                        /**
                                         * Public Key 입력 필드 업데이트
                                         * @param {string} publicKey - 표시할 Public Key (없으면 빈 문자열)
                                         * @param {boolean} [isMissing] - 키가 없는 상태인지 여부
                                         */
                                        function setPublicKeyFieldValue(publicKey, isMissing) {
                                            const input = document.querySelector('#jangbogo_public_key');
                                            if (!input) {
                                                return;
                                            }
                                            
                                            const defaultPlaceholder = input.getAttribute('data-default-placeholder') || '';
                                            const nullPlaceholder = input.getAttribute('data-null-placeholder') || defaultPlaceholder;
                                            const missing = (typeof isMissing === 'boolean') ? isMissing : !publicKey;
                                            
                                            if (publicKey && !missing) {
                                                input.value = publicKey;
                                                input.placeholder = defaultPlaceholder;
                                                input.classList.remove('text-muted');
                                            } else {
                                                input.value = '';
                                                input.placeholder = nullPlaceholder;
                                                input.classList.add('text-muted');
                                            }
                                        }
                                        
                                        /**
                                         * 장보고 Public Key를 클립보드에 복사
                                         * @param {Event} e - 클릭 이벤트 객체
                                         */
                                        function copyJangbogoPublicKey(e) {
                                            const publicKeyInput = document.querySelector('#jangbogo_public_key');
                                            const publicKey = publicKeyInput.value;
                                            
                                            if (!publicKey || publicKey.trim() === '') {
                                                alert('복사할 Public Key가 없습니다.');
                                                return;
                                            }
                                            
                                            // 클립보드에 복사
                                            navigator.clipboard.writeText(publicKey).then(function() {
                                                // 성공 시 버튼 아이콘 변경 (피드백)
                                                const btn = e.target.closest('button');
                                                const icon = btn.querySelector('i');
                                                const originalClass = icon.className;
                                                
                                                icon.className = 'bi bi-check2';
                                                btn.classList.add('btn-success');
                                                btn.classList.remove('btn-outline-primary');
                                                
                                                setTimeout(function() {
                                                    icon.className = originalClass;
                                                    btn.classList.remove('btn-success');
                                                    btn.classList.add('btn-outline-primary');
                                                }, 2000);
                                                
                                                // 안내 메시지 표시
                                                alert('Public Key가 클립보드에 복사되었습니다.\n\n장보고 관리화면의 "Public Key (암호화용)" 입력란에 붙여넣기 해주세요.');
                                                console.log('Public Key가 클립보드에 복사되었습니다.');
                                            }).catch(function(err) {
                                                alert('클립보드 복사 실패: ' + err);
                                                console.error('클립보드 복사 오류:', err);
                                            });
                                        }
                                        
                                        /**
                                         * FTP 계정 확인 메시지 초기화
                                         */
                                        function clearFtpAccountCheckMsg() {
                                            // readonly 상태에서는 메시지 초기화 안 함
                                            const ftpIdInput = document.querySelector('#ftp_account_id');
                                            if (ftpIdInput.hasAttribute('readonly')) {
                                                return;
                                            }
                                            
                                            const msgDiv = document.querySelector('#ftp_account_check_msg');
                                            msgDiv.style.display = 'none';
                                            msgDiv.removeAttribute('data-exists');
                                        }
                                        
                                        /**
                                         * 아이디 갱신 버튼 클릭 - readonly 해제
                                         */
                                        function enableFtpIdInput(e) {
                                            const inputField = document.querySelector('#ftp_account_id');
                                            const btn = e.target.closest('button');
                                            
                                            if (inputField.hasAttribute('readonly')) {
                                                // readonly 해제
                                                inputField.removeAttribute('readonly');
                                                inputField.value = '';
                                                inputField.focus();
                                                inputField.placeholder = 'FTP 계정 ID를 입력하세요';
                                                
                                                // 버튼 아이콘 변경 (취소 모드)
                                                btn.querySelector('i').className = 'bi bi-x-circle';
                                                btn.className = 'btn btn-outline-danger';
                                                btn.title = '취소';
                                                btn.onclick = function(e) { cancelFtpIdInput(e); };
                                                
                                                clearFtpAccountCheckMsg();
                                                
                                                // 안내 메시지
                                                const msgDiv = document.querySelector('#ftp_account_check_msg');
                                                msgDiv.style.display = 'block';
                                                msgDiv.className = 'form-text text-info';
                                                msgDiv.innerHTML = '<i class="bi bi-info-circle-fill"></i> 새로운 FTP 계정 ID를 입력하세요.';
                                                
                                                console.log('✅ 아이디 갱신 모드 활성화');
                                            }
                                        }
                                        
                                        /**
                                         * 아이디 입력 취소 - readonly 복원
                                         */
                                        function cancelFtpIdInput(e) {
                                            const inputField = document.querySelector('#ftp_account_id');
                                            const btn = e.target.closest('button');
                                            
                                            console.log('취소 - FTP ID 입력 모드 해제');
                                            
                                            // 원래 ftpId 값 복원
                                            loadJangbogoConfig();
                                            
                                            clearFtpAccountCheckMsg();
                                        }
                                        
                                        /**
                                         * 엔터키 처리
                                         */
                                        function handleFtpIdKeyPress(event) {
                                            if (event.key === 'Enter' || event.keyCode === 13) {
                                                event.preventDefault();
                                                
                                                // readonly 상태이면 엔터키 무시
                                                const ftpIdInput = document.querySelector('#ftp_account_id');
                                                if (ftpIdInput.hasAttribute('readonly')) {
                                                    console.log('readonly 상태 - 엔터키 무시');
                                                    return false;
                                                }
                                                
                                                checkFtpAccountId();
                                                return false;
                                            }
                                        }
                                        
                                        /**
                                         * FTP 계정 ID 중복 확인
                                         */
                                        function checkFtpAccountId() {
                                            const ftpIdInput = document.querySelector('#ftp_account_id');
                                            const accountId = ftpIdInput.value.trim();
                                            const msgDiv = document.querySelector('#ftp_account_check_msg');
                                            
                                            // readonly 상태이면 중복 확인 수행하지 않음
                                            if (ftpIdInput.hasAttribute('readonly')) {
                                                console.log('readonly 상태 - 계정 확인 스킵');
                                                return;
                                            }
                                            
                                            // 입력값이 없으면 메시지 숨김
                                            if (!accountId) {
                                                msgDiv.style.display = 'none';
                                                return;
                                            }
                                            
                                            // ID 유효성 검사 (영문, 숫자, 언더스코어만 허용)
                                            const idPattern = /^[a-zA-Z0-9_]+$/;
                                            if (!idPattern.test(accountId)) {
                                                msgDiv.style.display = 'block';
                                                msgDiv.className = 'form-text text-danger';
                                                msgDiv.innerHTML = '<i class="bi bi-x-circle-fill"></i> 계정 ID는 영문, 숫자, 언더스코어(_)만 사용할 수 있습니다.';
                                                return;
                                            }
                                            
                                            // 서버에 중복 확인 요청
                                            const path = "/jbs/jbg";
                                            const params = "cmd=checkFtpAccountExists&accountId=" + encodeURIComponent(accountId);
                                            
                                            const xhr = new XMLHttpRequest();
                                            xhr.onreadystatechange = function() {
                                                if (xhr.readyState === 4) {
                                                    if (xhr.status === 200) {
                                                        const resMsg = xhr.responseText;
                                                        
                                                        if (resMsg.length > 0) {
                                                            try {
                                                                const resJson = JSON.parse(resMsg);
                                                                
                                                                if (resJson.code === '000') {
                                                                    msgDiv.style.display = 'block';
                                                                    
                                                                    if (resJson.exists) {
                                                                        // 계정이 이미 존재 - 에러 처리
                                                                        msgDiv.className = 'form-text text-danger';
                                                                        msgDiv.innerHTML = '<i class="bi bi-x-circle-fill"></i> ' +
                                                                                          '이미 존재하는 계정입니다. 다른 ID를 사용해주세요.';
                                                                        msgDiv.setAttribute('data-exists', 'true');
                                                                        console.warn('❌ 계정 중복:', accountId);
                                            } else {
                                                                        // 사용 가능한 계정 ID
                                                                        msgDiv.className = 'form-text text-success';
                                                                        msgDiv.innerHTML = '<i class="bi bi-check-circle-fill"></i> ' +
                                                                                          '사용 가능한 계정 ID입니다.';
                                                                        msgDiv.setAttribute('data-exists', 'false');
                                                                        console.log('✅ 사용 가능한 ID:', accountId);
                                                                    }
                                                                } else {
                                                                    // 서버 오류
                                                                    msgDiv.style.display = 'block';
                                                                    msgDiv.className = 'form-text text-danger';
                                                                    msgDiv.innerHTML = '<i class="bi bi-x-circle-fill"></i> ' +
                                                                                      '계정 확인 실패: ' + (resJson.msg || '알 수 없는 오류');
                                                                    msgDiv.setAttribute('data-exists', 'unknown');
                                                                    console.error('계정 확인 실패:', resJson);
                                                                    
                                                                    // 오류 alert 표시
                                                                    alert('❌ 계정 확인 실패\n\n' + 
                                                                          (resJson.msg || 'FTP 서버 연결을 확인해주세요.'));
                                                                }
                                                            } catch (e) {
                                                                console.error('계정 확인 응답 파싱 오류:', e);
                                                                msgDiv.style.display = 'block';
                                                                msgDiv.className = 'form-text text-danger';
                                                                msgDiv.innerHTML = '<i class="bi bi-x-circle-fill"></i> 응답 처리 오류';
                                                                msgDiv.setAttribute('data-exists', 'unknown');
                                                                alert('❌ 응답 처리 오류\n\n서버 응답을 처리할 수 없습니다.');
                                                            }
                                                        } else {
                                                            console.warn('서버 응답이 비어있습니다.');
                                                            alert('⚠️ 서버 응답 없음');
                                                        }
                                                    } else {
                                                        // HTTP 오류
                                                        console.error('HTTP 오류:', xhr.status);
                                                        msgDiv.style.display = 'block';
                                                        msgDiv.className = 'form-text text-danger';
                                                        msgDiv.innerHTML = '<i class="bi bi-x-circle-fill"></i> 서버 연결 실패';
                                                        msgDiv.setAttribute('data-exists', 'unknown');
                                                        alert('❌ 서버 연결 실패\n\nHTTP ' + xhr.status);
                                                    }
                                                }
                                            };
                                            sendPost(xhr, path, params);
                                        }
                                        
                                        /**
                                         * FTP 사용자 계정 생성/갱신
                                         * @param {Event} e - 클릭 이벤트 객체
                                         */
                                        function createOrUpdateFtpAccount(e) {
                                            const ftpIdInput = document.querySelector('#ftp_account_id');
                                            const accountId = ftpIdInput.value.trim();
                                            const accountPassword = document.querySelector('#ftp_account_password').value.trim();
                                            const publicKey = document.querySelector('#jangbogo_public_key').value.trim();
                                            const msgDiv = document.querySelector('#ftp_account_check_msg');
                                            const isReadonly = ftpIdInput.hasAttribute('readonly');
                                            
                                            // 케이스 1: 새로운 FTP ID 입력 (readonly 해제된 상태)
                                            if (!isReadonly) {
                                                // ID, Pass 필수 (Public Key는 서버에서 자동 생성)
                                                if (!accountId) {
                                                    alert('계정 ID를 입력해주세요.');
                                                    ftpIdInput.focus();
                                                    return;
                                                }
                                                
                                                if (!accountPassword) {
                                                    alert('계정 비밀번호를 입력해주세요.');
                                                    document.querySelector('#ftp_account_password').focus();
                                                    return;
                                                }
                                                
                                                if (accountPassword.length < 4) {
                                                    alert('비밀번호는 최소 4자 이상이어야 합니다.');
                                                    document.querySelector('#ftp_account_password').focus();
                                                    return;
                                                }
                                                
                                                // 계정 중복 확인
                                                const accountExists = msgDiv.getAttribute('data-exists');
                                                if (accountExists === 'true') {
                                                    alert('❌ 이미 존재하는 계정 ID입니다.\n\n다른 ID를 사용해주세요.');
                                                    ftpIdInput.focus();
                                                    return;
                                                }
                                                
                                                if (accountExists !== 'false') {
                                                    alert('먼저 계정 ID 중복 확인을 해주세요.');
                                                    checkFtpAccountId();
                                                    return;
                                                }
                                                
                                                // 최종 확인
                                                const pubKeyMsg = publicKey ? 'Public Key: 설정됨' : 'Public Key: 자동 생성됨';
                                                if (!confirm('새 FTP 계정을 생성하시겠습니까?\n\n' +
                                                            '계정 ID: ' + accountId + '\n' +
                                                            pubKeyMsg)) {
                                                    return;
                                                }
                                            }
                                            // 케이스 2 & 3: 기존 계정 갱신 (readonly 상태)
                                            else if (isReadonly && accountId) {
                                                const hasPassword = accountPassword !== '';
                                                const hasPublicKey = publicKey !== '';
                                                
                                                // 아무것도 입력하지 않은 경우
                                                if (!hasPassword && !hasPublicKey) {
                                                    alert('비밀번호 또는 Public Key 중 하나 이상을 입력해주세요.');
                                                    return;
                                                }
                                                
                                                // 비밀번호만 입력한 경우
                                                if (hasPassword && !hasPublicKey) {
                                                    if (accountPassword.length < 4) {
                                                        alert('비밀번호는 최소 4자 이상이어야 합니다.');
                                                        document.querySelector('#ftp_account_password').focus();
                                                        return;
                                                    }
                                                    
                                                    if (!confirm('FTP 계정 "' + accountId + '"의 비밀번호를 갱신하시겠습니까?')) {
                                                        return;
                                                    }
                                                }
                                                // Public Key만 입력한 경우
                                                else if (!hasPassword && hasPublicKey) {
                                                    if (!confirm('FTP 계정 "' + accountId + '"의 Public Key를 갱신하시겠습니까?')) {
                                                        return;
                                                    }
                                                }
                                                // 둘 다 입력한 경우
                                                else {
                                                    if (accountPassword.length < 4) {
                                                        alert('비밀번호는 최소 4자 이상이어야 합니다.');
                                                        document.querySelector('#ftp_account_password').focus();
                                                        return;
                                                    }
                                                    
                                                    if (!confirm('FTP 계정 "' + accountId + '"의 비밀번호와 Public Key를 모두 갱신하시겠습니까?')) {
                                                        return;
                                                    }
                                                }
                                            } else {
                                                alert('계정 ID를 확인할 수 없습니다.');
                                                return;
                                            }
                                            
                                            const btn = e.target.closest('button');
                                            const icon = btn.querySelector('i');
                                            const originalClass = icon.className;
                                            const originalText = btn.innerHTML;
                                            
                                            // 로딩 표시
                                            icon.className = 'spinner-border spinner-border-sm';
                                            btn.disabled = true;
                                            
                                            const path = "/jbs/jbg";
                                            let params = "cmd=createFtpAccount&accountId=" + encodeURIComponent(accountId);
                                            
                                            // 비밀번호가 입력된 경우에만 전송
                                            if (accountPassword) {
                                                params += "&accountPassword=" + encodeURIComponent(accountPassword);
                                            }
                                            
                                            // Public Key가 입력된 경우에만 전송
                                            if (publicKey) {
                                                params += "&publicKey=" + encodeURIComponent(publicKey);
                                            }
                                            
                                            const xhr = new XMLHttpRequest();
                                            xhr.onreadystatechange = function() {
                                                if (xhr.readyState === 4) {
                                                    // 버튼 복원
                                                    btn.innerHTML = originalText;
                                                    btn.disabled = false;
                                                    
                                                    if (xhr.status === 200) {
                                                        const resMsg = xhr.responseText;
                                                        console.log('FTP 계정 생성 응답:', resMsg);
                                                        
                                                    if (resMsg.length > 0) {
                                                            try {
                                                                const resJson = JSON.parse(resMsg);
                                                                console.log('파싱된 응답:', resJson);
                                                                
                                                                if (resJson.code === '000') {
                                                                    // 성공
                                                                    let alertMsg = '✅ ' + (resJson.msg || 'FTP 계정 작업이 완료되었습니다.') + '\n\n' +
                                                                                   '━━━━━━━━━━━━━━━━━━━━━━\n' +
                                                                                   '계정 ID: ' + (resJson.accountId || accountId) + '\n' +
                                                                                   '홈 디렉토리: ' + (resJson.homeDirectory || 'N/A') + '\n' +
                                                                                   '━━━━━━━━━━━━━━━━━━━━━━\n\n' +
                                                                                   '장보고 관리화면에서 이 계정으로\n파일을 업로드할 수 있습니다.';
                                                                    
                                                                    // 이전 계정 정보가 있으면 추가 안내
                                                                    if (resJson.previousAccountId) {
                                                                        alertMsg += '\n\n⚠️ 참고: 이전 계정 "' + resJson.previousAccountId + '"은(는)\n더 이상 사용되지 않습니다.';
                                                                    }
                                                                    
                                                                    alert(alertMsg);
                                                                    
                                                                    // 비밀번호 필드 초기화
                                                                    document.querySelector('#ftp_account_password').value = '';
                                                                    
                                                                    // Public Key가 응답에 포함되어 있으면 업데이트 (자동 생성되었거나 갱신된 경우)
                                                                    setPublicKeyFieldValue(resJson.publicKey || '', !(resJson.publicKey && resJson.publicKey.trim().length));
                                                                    if (resJson.publicKey) {
                                                                        console.log('Public Key 업데이트:', resJson.publicKey.substring(0, 50) + '...');
                                                                    } else {
                                                                        console.log('Public Key 값이 전달되지 않아 비워둡니다.');
                                                                    }
                                                                    
                                                                    // 메시지 초기화
                                                                    const msgDiv = document.querySelector('#ftp_account_check_msg');
                                                                    msgDiv.style.display = 'none';
                                                                    msgDiv.removeAttribute('data-exists');
                                                                    
                                                                    // 설정 다시 로드 (ftpId를 readonly로 표시하기 위해)
                                                                    loadJangbogoConfig();
                                                                    
                                                                    console.log('✅ FTP 계정 작업 완료:', resJson);
                                                                    
                                                                } else if (resJson.code === '400') {
                                                                    // 잘못된 요청
                                                                    alert('❌ 입력 오류\n\n' + (resJson.msg || '입력값을 확인해주세요.'));
                                                                    console.error('입력 오류:', resJson);
                                                                    
                                                                } else if (resJson.code === '401') {
                                                                    // 인증 오류
                                                                    alert('❌ 인증 오류\n\n로그인이 필요합니다.');
                                                                    console.error('인증 오류:', resJson);
                                                                    
                                                                } else if (resJson.code === '500') {
                                                                    // 서버 오류
                                                                    alert('❌ FTP 계정 생성 실패\n\n' +
                                                                          'FTP 서버와의 통신에 실패했습니다.\n' +
                                                                          'FTP 서버가 실행 중인지 확인해주세요.\n\n' +
                                                                          '상세: ' + (resJson.msg || '알 수 없는 오류'));
                                                                    console.error('서버 오류:', resJson);
                                                                    
                                                            } else {
                                                                    // 기타 오류
                                                                    alert('❌ 계정 생성 실패\n\n' + (resJson.msg || '알 수 없는 오류'));
                                                                    console.error('계정 생성 실패:', resJson);
                                                                }
                                                            } catch (e) {
                                                                // JSON 파싱 오류
                                                                console.error('응답 파싱 오류:', e, '원본 응답:', resMsg);
                                                                alert('❌ 서버 응답 처리 오류\n\n' +
                                                                      '서버 응답을 처리할 수 없습니다.\n' +
                                                                      '콘솔을 확인해주세요.\n\n' +
                                                                      '원본 응답: ' + resMsg.substring(0, 100));
                                                            }
                                                        } else {
                                                            // 빈 응답
                                                            console.warn('서버 응답이 비어있습니다.');
                                                            alert('⚠️ 서버 응답 없음\n\n서버가 응답하지 않았습니다.');
                                                        }
                                                    } else {
                                                        // HTTP 오류
                                                        console.error('HTTP 오류:', xhr.status, xhr.statusText);
                                                        alert('❌ 서버 연결 실패\n\n' +
                                                              'HTTP 상태: ' + xhr.status + ' ' + xhr.statusText + '\n\n' +
                                                              '네트워크 연결을 확인해주세요.');
                                                    }
                                                }
                                            };
                                            
                                            // 타임아웃 설정 (30초)
                                            xhr.timeout = 30000;
                                            xhr.ontimeout = function() {
                                                btn.innerHTML = originalText;
                                                btn.disabled = false;
                                                alert('❌ 요청 시간 초과\n\n서버 응답 시간이 초과되었습니다.\n다시 시도해주세요.');
                                                console.error('요청 시간 초과');
                                            };
                                            
                                            sendPost(xhr, path, params);
                                        }
                                        
                                        /**
                                         * 장보고 설정 로드 (RSA 키 쌍 및 FTP ID 조회)
                                         */
                                        function loadJangbogoConfig() {
                                            console.log('장보고 설정 로드 시작...');
                                            const path = "/jbs/jbg";
                                            const params = "cmd=getJangbogoPublicKey";
                                            
                                            const xhr = new XMLHttpRequest();
                                            xhr.onreadystatechange = function() {
                                                if (checkAjaxSuc(xhr)) {
                                                    const resMsg = xhr.responseText;
                                                    console.log('Public Key 조회 응답:', resMsg);
                                                    
                                                    if (resMsg.length > 0) {
                                                        try {
                                                            const resJson = JSON.parse(resMsg);
                                                            console.log('파싱된 응답:', resJson);
                                                            
                                                            if (resJson.code === '000') {
                                                                // Public Key 표시
                                                                setPublicKeyFieldValue(resJson.publicKey || '', resJson.publicKeyMissing === true);
                                                                if (resJson.publicKey) {
                                                                    console.log('Public Key 로드 완료:', resJson.publicKey.substring(0, 50) + '...');
                                                                } else {
                                                                    console.warn('Public Key가 비어있습니다. 새로 생성해야 합니다.');
                                                                }
                                                                
                                                                // FTP ID 표시
                                                                const ftpIdInput = document.querySelector('#ftp_account_id');
                                                                const ftpIdBtn = document.querySelector('#btn_enable_ftp_id_input');
                                                                
                                                                if (resJson.ftpId && resJson.ftpId !== '') {
                                                                    // FTP ID가 있으면 표시하고 readonly 설정
                                                                    ftpIdInput.value = resJson.ftpId;
                                                                    ftpIdInput.setAttribute('readonly', 'readonly');
                                                                    ftpIdInput.placeholder = '등록된 FTP 계정 ID';
                                                                    
                                                                    // 버튼을 '아이디 갱신' 모드로 설정
                                                                    ftpIdBtn.querySelector('i').className = 'bi bi-pencil-square';
                                                                    ftpIdBtn.className = 'btn btn-outline-info';
                                                                    ftpIdBtn.title = '아이디 갱신';
                                                                    ftpIdBtn.onclick = function(e) { enableFtpIdInput(e); };
                                                                    
                                                                    console.log('FTP ID 로드 완료:', resJson.ftpId);
                                                                } else {
                                                                    // FTP ID가 없으면 입력 가능 상태로 (readonly 해제)
                                                                    ftpIdInput.removeAttribute('readonly');
                                                                    ftpIdInput.value = '';
                                                                    ftpIdInput.placeholder = 'FTP 계정 ID를 입력하세요';
                                                                    
                                                                    // 버튼을 '아이디 갱신' 모드로 설정
                                                                    ftpIdBtn.querySelector('i').className = 'bi bi-pencil-square';
                                                                    ftpIdBtn.className = 'btn btn-outline-info';
                                                                    ftpIdBtn.title = '아이디 갱신';
                                                                    ftpIdBtn.onclick = function(e) { enableFtpIdInput(e); };
                                                                    
                                                                    console.log('FTP ID 없음 - 새로 입력 가능');
                                                                }
                                                                
                                                                // 자동 수집 설정 로드
                                                                if (typeof resJson.autoCollectEnabled !== 'undefined') {
                                                                    const autoCollectCheckbox = document.querySelector('#auto_collect_enabled');
                                                                    autoCollectCheckbox.checked = (resJson.autoCollectEnabled === 1);
                                                                    console.log('자동 수집 활성화 상태 로드:', resJson.autoCollectEnabled);
                                                                }
                                                                
                                                                if (typeof resJson.autoCollectInterval !== 'undefined') {
                                                                    const intervalSelect = document.querySelector('#auto_collect_interval');
                                                                    intervalSelect.value = resJson.autoCollectInterval;
                                                                    console.log('자동 수집 주기 로드:', resJson.autoCollectInterval + '분');
                                                                }
                                                            } else {
                                                                console.error('Public Key 조회 실패:', resJson);
                                                            }
                                                        } catch (e) {
                                                            console.error('장보고 설정 로드 파싱 오류:', e, '원본 응답:', resMsg);
                                                        }
                                                    } else {
                                                        console.warn('서버 응답이 비어있습니다.');
                                                    }
                                                }
                                            };
                                            sendPost(xhr, path, params);
                                        }
                                        
                                        // 페이지 로드 시 장보고 설정 로드
                                        document.addEventListener('DOMContentLoaded', function() {
                                            loadJangbogoConfig();
                                        });
                                    </script>
                                </div>

                                <div class="mb-3"></div>

                                <hr class="hr mb-3">
                                <div class="card-body">
                                    <h5 id="card-title" class="card-title">쇼핑 데이터 분류(자동화)</h5>
                                    <p id="card-text" class="card-text">수집한 아이템을 아래의 자동화 규칙에 따라 지정한 보관함(BOX)으로 자동 분류합니다.</p>
                                </div>
                                <hr class="mb-3" style="border-top: 1px dotted gray;">
                                <div id="autoclass_div" class="card-body">
                                    <h5 class="card-title">자동화 규칙 목록</h5>
                                    <div id="rules" class="row mb-1">

                                        <%-- <div class="card mb-2">
                                            <div class="input-group input-group-sm d-flex mt-1 mb-1">
                                                <div class="input-group-text">
                                                    <input class="form-check-input mt-0" type="checkbox" value="" checked>
                                                </div>
                                                <span class="input-group-text flex-grow-1">
                                                    <select id="tobox">
                                                        <option value="1">냉장고</option>
                                                        <option value="2">라면함</option>
                                                        <option value="3">장난감함</option>
                                                    </select>
                                                    &nbsp;로 이동
                                                </span>
                                                <button id="" type="button" class="btn btn-success" style="--bs-btn-padding-y: .25rem; --bs-btn-padding-x: .5rem; --bs-btn-font-size: .75rem;">
                                                    저장
                                                </button>
                                            </div>
                                            <div class="input-group input-group-sm mb-1">
                                                <input id="txtnum_1" class="form-control" type="text" aria-label="Text input with checkbox" value="AAA">
                                                <div class="input-group-text">
                                                    <button id="" type="button" class="btn btn-outline-primary" style="--bs-btn-padding-y: .25rem; --bs-btn-padding-x: .5rem; --bs-btn-font-size: .75rem;">
                                                        삭제
                                                    </button>
                                                </div>
                                            </div>
                                            <div class="input-group input-group-sm mb-1">
                                                <input id="txtnum_2" class="form-control" type="text" aria-label="Text input with checkbox" value="BBB">
                                                <div class="input-group-text">
                                                    <button id="" type="button" class="btn btn-outline-primary" style="--bs-btn-padding-y: .25rem; --bs-btn-padding-x: .5rem; --bs-btn-font-size: .75rem;">
                                                        삭제
                                                    </button>
                                                </div>
                                            </div>
                                            <div class="input-group input-group-sm mb-1">
                                                <button id="" type="button" class="btn btn-outline-primary" style="--bs-btn-padding-y: .25rem; --bs-btn-padding-x: .5rem; --bs-btn-font-size: .75rem;">
                                                    키워드 추가
                                                </button>
                                            </div>
                                            <div class="input-group input-group-sm mb-1 ps-3">
                                                <div class="input-group-text">
                                                    <input name="keyoper" value="0" class="form-check-input mt-0" type="radio">
                                                </div>
                                                <span class="input-group-text">
                                                    키워드가&nbsp;
                                                    <select id="keycnt">
                                                        <option value="1">1</option>
                                                        <option value="2">2</option>
                                                        <option value="3">3</option>
                                                    </select>
                                                    &nbsp;개 이상 포함된 경우
                                                </span>
                                            </div>
                                            <div class="input-group input-group-sm mb-1 ps-3">
                                                <div class="input-group-text">
                                                    <input name="keyoper" value="1" class="form-check-input mt-0" type="radio">
                                                </div>
                                                <span class="input-group-text">
                                                    모든 키워드가 포함된 경우
                                                </span>
                                            </div>                                            
                                        </div> --%>

                                    </div>
                                    <div class="input-group mb-1">
                                        <button id="" type="button" onclick="javascript:createEmptyCard()" class="btn btn-primary ms-auto" style="--bs-btn-padding-y: .25rem; --bs-btn-padding-x: .5rem; --bs-btn-font-size: .75rem;">
                                            규칙 추가
                                        </button>
                                        &nbsp;
                                        <button id="" type="button" onclick="javascript:saveAll()" class="btn btn-outline-primary" style="--bs-btn-padding-y: .25rem; --bs-btn-padding-x: .5rem; --bs-btn-font-size: .75rem;">
                                            전체 저장
                                        </button>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>

            </main>
        </div>
    </div>
    
    <footer id="foot" class="my-3 text-center text-small"><jsp:include page="./include/foot.jsp"/></footer>

    <script>
        window.addEventListener('load', () => {
            const _doc = window.document;

            //초기화
            _doc.malls = {};
            _doc.rules = {};

            //장보고 정보(쇼핑몰 목록, 자동화 목록, 사용자 정보)로 초기화
            var path = "/jbs/jbg";
            var params = "cmd=getInfo"
            var _ajax = new XMLHttpRequest();
            _ajax.onreadystatechange = function () {
                if (checkAjaxSuc(_ajax)) {
                    var resMsg = _ajax.responseText;
                    if (resMsg.length > 0) {
                        let resJson = JSON.parse(resMsg);
                        var alertMsg = null;
                        if (resJson.code == "000") {
							if (resJson.malls) {
								let failedAuthMalls = resJson.failedAuthMalls;
								let malls = _doc.malls = resJson.malls;
								Object.keys(malls).forEach(key => {
									const mall = malls[key];
                                    if (_doc.querySelector("#btn_signin_"+mall.id)) { //사용 가능 쇼핑몰
                                        let cipidval = localStorage.getItem(mall.cipidkey);
                                        if ((!failedAuthMalls.includes(mall.seq+"")) && mall.status == 1 && cipidval) {
                                            if (mall.usrid) {
                                                _doc.querySelector("#usrid_mall").value = mall.usrid;
                                                _doc.querySelector("#usrpass_mall").value = "**********";
                                            }
                                            _doc.querySelector("#usrid_mall").disabled = true;;
                                            _doc.querySelector("#usrpass_mall").disabled = true;                                        
                                            _doc.querySelector("#btn_signin_"+mall.id).textContent = '연결해제';
                                            _doc.querySelector("#btn_signin_"+mall.id).className = 'btn btn-outline-success';
                                            hideElement(_doc.querySelector("#mall_account_"+mall.id));
                                            mall.status = 1;
                                        } else {
                                            _doc.querySelector("#usrid_mall").disabled = false;;
                                            _doc.querySelector("#usrpass_mall").disabled = false;;
                                            _doc.querySelector("#btn_signin_"+mall.id).textContent = '계정연결';
                                            _doc.querySelector("#btn_signin_"+mall.id).className = 'btn btn-primary';
                                            showElement(_doc.querySelector("#mall_account_"+mall.id));
                                            mall.status = 0;
                                        }
                                    }
								});
							}
                            if (resJson.boxes) {
                                _doc.boxes = resJson.boxes;
                            }
                            if (resJson.rules) {
                                const div_rules = _doc.querySelector("#rules");

                                let rules = _doc.rules = resJson.rules;
                                Object.keys(rules).forEach(key => {
									const rule = rules[key];
                                    createRuleCard(div_rules, rule);
								});
                            }
                        } else {
                            authFailCheck(resJson.code, resJson.msg);
                        }
                    }
                }
            };
            sendPost(_ajax,
                path,
                params);
        }, false);

        /**
         * 새로운 규칙 카드 생성
         */
        function createEmptyCard() {
            const _doc = window.document;
            const div_rules = _doc.querySelector("#rules");

            var seq_newrule = _doc.seq_newrule ? _doc.seq_newrule+1 : 1;
            _doc.seq_newrule = seq_newrule;

            var newrule = {};
            newrule.seq = "n"+seq_newrule;
            newrule.status = 0;
            _doc.rules[newrule.seq] = newrule;
            createRuleCard(div_rules, newrule);
        }

        /**
         * 자동화 규칙 카드 생성
         * parent_elem : 카드를 attach할 부모 elem
         * rule : 적용할 규칙
         */
        function createRuleCard(parent_elem, rule) {
            const _doc = window.document;
            const boxes = _doc.boxes;

            let card_rule = _doc.createElement("div");
            card_rule.className = "card mb-2";
            card_rule.id = "div_card_rule_"+rule.seq;
            card_rule.style.borderTop = '3px solid green';
            parent_elem.appendChild(card_rule);
            
        //[ROW] RULE CONTROL
            let div_ctrl = _doc.createElement("div");
            div_ctrl.className = "input-group input-group-sm d-flex mt-1";
            card_rule.appendChild(div_ctrl);

            let div_ctrl_divchk = _doc.createElement("div");
            div_ctrl_divchk.className = "input-group-text";
            div_ctrl.appendChild(div_ctrl_divchk);

            let chk_ctrl = _doc.createElement("input");
            chk_ctrl.className = "form-check-input mt-0";
            chk_ctrl.id = "chk_ctrl_"+rule.seq;
            chk_ctrl.type = "checkbox";
            chk_ctrl.title = "활성화 상태";
            chk_ctrl.checked = (rule.status == 1) ? true : false;
            chk_ctrl.addEventListener('changed', function() {
                //2024.08.13 수정여부를 체크하려 하였으나 일단은 보류
            });
            div_ctrl_divchk.appendChild(chk_ctrl);

            let spn_ctrl = _doc.createElement("span");
            spn_ctrl.className = "input-group-text flex-grow-1";
            spn_ctrl.id = "";
            spn_ctrl.style.whiteSpace = "pre";
            spn_ctrl.appendChild(_doc.createTextNode(" 체크하여 활성화"));
            div_ctrl.appendChild(spn_ctrl);

            let btn_rule_save = _doc.createElement("button");
            btn_rule_save.className = "btn btn-success";
            btn_rule_save.type = "button";
            btn_rule_save.style = "--bs-btn-padding-y: .25rem; --bs-btn-padding-x: .5rem; --bs-btn-font-size: .75rem;";
            btn_rule_save.id = "btn_rule_save_"+rule.seq;
            btn_rule_save.innerText = "저장";
            btn_rule_save.setAttribute("onclick", "javascript:saveRuleCard('"+rule.seq+"', true)");
            div_ctrl.appendChild(btn_rule_save);

            let btn_rule_del = _doc.createElement("button");
            btn_rule_del.className = "btn btn-outline-success ms-auto";
            btn_rule_del.type = "button";
            btn_rule_del.style = "--bs-btn-padding-y: .25rem; --bs-btn-padding-x: .5rem; --bs-btn-font-size: .75rem;";
            btn_rule_del.id = "btn_rule_del_"+rule.seq;
            btn_rule_del.innerText = "삭제";
            btn_rule_del.setAttribute("onclick", "javascript:deleteRuleCard('"+rule.seq+"')");
            div_ctrl.appendChild(btn_rule_del);

        //[ROW] TOBOX
            let div_tobox = _doc.createElement("div");
            div_tobox.className = "input-group input-group-sm d-flex mt-1 mb-1";
            card_rule.appendChild(div_tobox);

            // let div_tobox_divchk = _doc.createElement("div");
            // div_tobox_divchk.className = "input-group-text";
            // div_tobox.appendChild(div_tobox_divchk);

            // let chk_tobox = _doc.createElement("input");
            // chk_tobox.className = "form-check-input mt-0";
            // chk_tobox.id = "chk_tobox_"+rule.seq;
            // chk_tobox.type = "checkbox";
            // chk_tobox.title = "활성화 상태";
            // chk_tobox.checked = (rule.status == 1) ? true : false;
            // chk_tobox.addEventListener('changed', function() {
            //     //2024.08.13 수정여부를 체크하려 하였으나 일단은 보류
            // });
            // div_tobox_divchk.appendChild(chk_tobox);

            //box select 생성
            let elem_sel_box = _doc.createElement("select");
            elem_sel_box.className = "form-select form-select-sm";
            elem_sel_box.id = "sel_tobox_"+rule.seq;
            elem_sel_box.style = "width:auto;";
            Object.keys(boxes).forEach(seq => {
                let box = boxes[seq];
                if (box.type != -1) {
                    let elem_opt = _doc.createElement("option");
                    elem_opt.value = box.seq;
                    elem_opt.text = box.name;
                    elem_sel_box.appendChild(elem_opt);
                }
            });
            elem_sel_box.value = rule.seq_box;
            let spn_tobox = _doc.createElement("span");
            spn_tobox.className = "input-group-text flex-grow-1";
            spn_tobox.id = "";
            spn_tobox.appendChild(elem_sel_box);
            spn_tobox.style.whiteSpace = "pre";
            spn_tobox.appendChild(_doc.createTextNode(" 로 이동"));
            div_tobox.appendChild(spn_tobox);

            // let btn_rule_save = _doc.createElement("button");
            // btn_rule_save.className = "btn btn-success";
            // btn_rule_save.type = "button";
            // btn_rule_save.style = "--bs-btn-padding-y: .25rem; --bs-btn-padding-x: .5rem; --bs-btn-font-size: .75rem;";
            // btn_rule_save.id = "btn_rule_save_"+rule.seq;
            // btn_rule_save.innerText = "저장";
            // btn_rule_save.setAttribute("onclick", "javascript:saveRuleCard('"+rule.seq+"', true)");
            // div_tobox.appendChild(btn_rule_save);

            // let btn_rule_del = _doc.createElement("button");
            // btn_rule_del.className = "btn btn-outline-success ms-auto";
            // btn_rule_del.type = "button";
            // btn_rule_del.style = "--bs-btn-padding-y: .25rem; --bs-btn-padding-x: .5rem; --bs-btn-font-size: .75rem;";
            // btn_rule_del.id = "btn_rule_del_"+rule.seq;
            // btn_rule_del.innerText = "삭제";
            // btn_rule_del.setAttribute("onclick", "javascript:deleteRuleCard('"+rule.seq+"')");
            // div_tobox.appendChild(btn_rule_del);

            //[ROW] KEYWORDS
            let div_keywords = _doc.createElement("div");
            div_keywords.id = "div_keywords_"+rule.seq;
            card_rule.appendChild(div_keywords);

            let keywordArr = rule.keywords ? JSON.parse(rule.keywords) : [];
            keywordArr.forEach(keyword => {
                addKeyword(rule.seq, keyword);
            });

        //[ROW] ADD KEYWORD
            let div_add_keyword = _doc.createElement("div");
            div_add_keyword.className = "input-group input-group-sm mb-1";
            card_rule.appendChild(div_add_keyword);

            let btn_add_keyword = _doc.createElement("button");
            btn_add_keyword.className = "btn btn-outline-primary";
            btn_add_keyword.type = "button";
            btn_add_keyword.id = "btn_add_keyword_"+rule.seq;
            btn_add_keyword.style = "--bs-btn-padding-y: .25rem; --bs-btn-padding-x: .5rem; --bs-btn-font-size: .75rem;";
            btn_add_keyword.innerText = "키워드 추가";
            btn_add_keyword.setAttribute("onclick", "javascript:addKeyword('"+rule.seq+"')");
            div_add_keyword.appendChild(btn_add_keyword);

        //[ROW] CONDITION OR
            let div_or_cond = _doc.createElement("div");
            div_or_cond.className = "input-group input-group-sm mb-1 ps-3";
            card_rule.appendChild(div_or_cond);

            let div_or_keyoper = _doc.createElement("div");
            div_or_keyoper.className = "input-group-text";
            div_or_cond.appendChild(div_or_keyoper);

            let input_or_keyoper = _doc.createElement("input");
            input_or_keyoper.className = "form-check-input mt-0";
            input_or_keyoper.type = "radio";
            input_or_keyoper.name = "keyoper_"+rule.seq;
            input_or_keyoper.value = "0";
            input_or_keyoper.checked = (rule.keyoper == 0) ? true : false;
            div_or_keyoper.appendChild(input_or_keyoper);
            var elem_sel_mc = _doc.createElement("select");
            elem_sel_mc.className = "form-select form-select-sm";
            elem_sel_mc.id = "sel_matchcnt_"+rule.seq;
            createSelOptions(elem_sel_mc, keywordArr.length, rule.matchcnt);
            let spn_or_cond = _doc.createElement("span");
            spn_or_cond.className = "input-group-text";
            spn_or_cond.style = "whiteSpace:pre;font-size:.75rem;";            
            spn_or_cond.appendChild(_doc.createTextNode("키워드가"));
            spn_or_cond.appendChild(elem_sel_mc);
            spn_or_cond.appendChild(_doc.createTextNode(" 개 이상 포함된 경우"));
            div_or_cond.appendChild(spn_or_cond);

        //[ROW] CONDITION AND
            let div_and_cond = _doc.createElement("div");
            div_and_cond.className = "input-group input-group-sm mb-1 ps-3";
            card_rule.appendChild(div_and_cond);

            let div_and_keyoper = _doc.createElement("div");
            div_and_keyoper.className = "input-group-text";
            div_and_cond.appendChild(div_and_keyoper);

            let input_and_keyoper = _doc.createElement("input");
            input_and_keyoper.className = "form-check-input mt-0";
            input_and_keyoper.type = "radio";
            input_and_keyoper.name = "keyoper_"+rule.seq;
            input_and_keyoper.value = "1";
            input_and_keyoper.checked = (rule.keyoper == 1) ? true : false;
            div_and_keyoper.appendChild(input_and_keyoper);

            let spn_and_cond = _doc.createElement("span");
            spn_and_cond.className = "input-group-text";
            spn_and_cond.style = "whiteSpace:pre;font-size:.75rem;";
            spn_and_cond.innerText = "모든 키워드가 포함된 경우";
            div_and_cond.appendChild(spn_and_cond);
        }

        /**
         * 규칙 카드 저장
         */
        function saveRuleCard(seq_rule, alert_msg) {
            const _doc = window.document;

            var seq_box = _doc.querySelector("#sel_tobox_"+seq_rule).value;
            var keywords = [];
            var keyoper;
            var matchcnt = _doc.querySelector("#sel_matchcnt_"+seq_rule).value;
            var status = _doc.querySelector("#chk_ctrl_"+seq_rule).checked ? 1 : 0;

            const div_keywords = _doc.querySelector("#div_keywords_"+seq_rule);
            const childDivs = div_keywords.querySelectorAll('div');
            childDivs.forEach(div_keyword => {
                let elem_input = div_keyword.querySelector('input');
                if (elem_input && elem_input.value) {
                    keywords.push(elem_input.value);
                }
            });
            
            const radios = document.querySelectorAll('input[name="keyoper_'+seq_rule+'"]');
            for (const radio of radios) {
                if (radio.checked) {
                    keyoper = radio.value;
                }
            }

            if (!keyoper) {
                alert("키워드 매칭 조건을 선택해야 합니다");
                return -1;
            }

            if (!seq_box) {
                alert("이동할 보관함(Box)를 선택해야 합니다");
                return -1;
            }
            
            var path = "/jbs/auto";
            var params = "cmd=updateRule";
            if (isNumber(seq_rule)) params += "&seq_rule=" + seq_rule;
            if (keyoper) params += "&keyoper=" + keyoper;
            if (keywords) params += "&keywords=" + JSON.stringify(keywords);
            if (matchcnt) params += "&matchcnt=" + matchcnt;
            if (status) params += "&status=" + status;
            params += "&seq_box=" + seq_box;

            var _ajax = new XMLHttpRequest();
            _ajax.onreadystatechange = function () {
                if (checkAjaxSuc(_ajax)) {
                    var resMsg = _ajax.responseText;
                    if (resMsg.length > 0) {
                        //alert(resMsg);
                        let resJo = JSON.parse(resMsg);
                        if (resJo.rule) {
                            let res_rule = resJo.rule;
                            _doc.rules[res_rule.seq] = res_rule;
                            delete _doc.rules[seq_rule];
                            
                            /**
                             * dom 의 event handler function 수정
                             */
                            let btn_rule_save = _doc.querySelector("#btn_rule_save_"+seq_rule);                            
                            btn_rule_save.setAttribute("onclick", "javascript:saveRuleCard('"+res_rule.seq+"')");
                            btn_rule_save.id = "#btn_rule_save_"+res_rule.seq;

                            let btn_rule_del = _doc.querySelector("#btn_rule_del_"+seq_rule);
                            btn_rule_del.setAttribute("onclick", "javascript:deleteRuleCard('"+res_rule.seq+"')");
                            btn_rule_del.id = "#btn_rule_del_"+res_rule.seq;

                            const div_keywords = _doc.querySelector("#div_keywords_"+seq_rule);
                            div_keywords.id = "#div_keywords_"+res_rule.seq;
                        }
                        if (resJo.msg && alert_msg) {
                            alert(resJo.msg);
                        }
                    }
                }
            };
            sendPost(_ajax, path, params);
        }

        /**
         * 전체 규칙 카드 저장
         */
        function saveAll() {
            const _doc = window.document;
            Object.keys(_doc.rules).forEach(seq_rule => {
                saveRuleCard(seq_rule);                
            });
            alert("전체 규칙을 저장하였습니다.");
        }
        
        /**
         * 규칙 카드 삭제
         */
        function deleteRuleCard(seq_rule) {
            if (confirm("규칙을 삭제할까요?")) {
                const _doc = window.document;
                const card_rule = _doc.querySelector("#div_card_rule_"+seq_rule);

                if (isNumber(seq_rule)) {
                    var path = "/jbs/auto";
                    var params = "cmd=deleteRule";
                    params += "&seq_rule=" + seq_rule;

                    var _ajax = new XMLHttpRequest();
                    _ajax.onreadystatechange = function () {
                        if (checkAjaxSuc(_ajax)) {
                            var resMsg = _ajax.responseText;
                            if (resMsg.length > 0) {
                                //alert(resMsg);
                                let resJo = JSON.parse(resMsg);
                                if (resJo.code == "000") {
                                    card_rule.remove();
                                    delete _doc.rules[seq_rule];
                                }
                                if (resJo.msg) {
                                    alert(resJo.msg);
                                }
                            }
                        }
                    };
                    sendPost(_ajax, path, params);
                } else {
                    card_rule.remove();
                    delete _doc.rules[seq_rule];
                }
            }
        }

        /**
         * 키워드 추가
         */
        function addKeyword(seq_rule, keyword) {
            const _doc = window.document;
            const div_keywords = _doc.querySelector("#div_keywords_"+seq_rule);
            var seq_keyword = div_keywords.seq_keyword ? (div_keywords.seq_keyword+1) : 1;

            let div_keyword = _doc.createElement("div");
            div_keyword.className = "input-group input-group-sm mb-1";
            div_keyword.id = "div_keyword_"+seq_rule+"_"+seq_keyword;
            div_keywords.appendChild(div_keyword);
            div_keywords.seq_keyword = seq_keyword;

            let input_txt = _doc.createElement("input");
            input_txt.className = "form-control";
            input_txt.type = "text";
            input_txt.value = keyword ? keyword : "";
            div_keyword.appendChild(input_txt);

            let div_keyword_del = _doc.createElement("div");
            div_keyword_del.className = "input-group-text";
            div_keyword.appendChild(div_keyword_del);
            
            let btn_keyword_del = _doc.createElement("button");
            btn_keyword_del.className = "btn btn-outline-primary";
            btn_keyword_del.tpye = "button";
            btn_keyword_del.style = "--bs-btn-padding-y: .25rem; --bs-btn-padding-x: .5rem; --bs-btn-font-size: .75rem;";
            btn_keyword_del.id = "btn_del_keyword_"+seq_rule+"_"+seq_keyword;
            btn_keyword_del.innerText = "삭제";
            btn_keyword_del.setAttribute("onclick", "javascript:delKeyword("+seq_rule+", 'div_keyword_"+seq_rule+"_"+seq_keyword+"')");
            div_keyword_del.appendChild(btn_keyword_del);

            const elem_sel_mc = _doc.querySelector("#sel_matchcnt_"+seq_rule);
            if (elem_sel_mc) {
                var optsize = elem_sel_mc.options ? elem_sel_mc.options.length : 0;
                var selval = elem_sel_mc.value;
                createSelOptions(elem_sel_mc, optsize+1, selval);
            }
        }

        /**
         * 키워드 삭제
         */
        function delKeyword(seq_rule, id) {
            const _doc = window.document;
            const div_keyword = _doc.querySelector("#"+id);
            div_keyword.remove();

            const elem_sel_mc = _doc.querySelector("#sel_matchcnt_"+seq_rule);
            if (elem_sel_mc) {
                var optsize = elem_sel_mc.options.length;
                var selval = elem_sel_mc.value;
                createSelOptions(elem_sel_mc, optsize-1, selval);
            }
        }

        /**
         * 옵션 목록 생성
         */
        function createSelOptions(elem_sel, size, matchcnt) {
            const _doc = window.document;
            elem_sel.innerHTML = "";
            for (let i = 1; i <= size; i++) {
                let elem_opt = _doc.createElement("option");
                elem_opt.value = i;
                elem_opt.text = i;
                elem_sel.appendChild(elem_opt);
            }
            elem_sel.value = matchcnt;
        }
        
        /**
         * 자동 수집 활성화/비활성화
         */
        function toggleAutoCollect() {
            const checkbox = document.querySelector('#auto_collect_enabled');
            const enabled = checkbox.checked;
            
            // 수집 주기 가져오기
            const intervalSelect = document.querySelector('#auto_collect_interval');
            const intervalMinutes = parseInt(intervalSelect.value) || 5;
            
            console.log('자동 수집 상태 변경:', enabled ? '활성화' : '비활성화', ', 주기:', intervalMinutes + '분');
            
            const path = "/jbs/jbg";
            const params = "cmd=toggleAutoCollect&enabled=" + (enabled ? '1' : '0') + 
                          "&interval=" + intervalMinutes;
            
            const xhr = new XMLHttpRequest();
            xhr.onreadystatechange = function() {
                if (checkAjaxSuc(xhr)) {
                    const resMsg = xhr.responseText;
                    
                    if (resMsg.length > 0) {
                        try {
                            const resJson = JSON.parse(resMsg);
                            
                            if (resJson.code === '000') {
                                console.log('✅ 자동 수집 상태 저장 완료:', resJson.enabled ? '활성화' : '비활성화');
                                
                                // 활성화 시 즉시 실행
                                if (resJson.enabled) {
                                    const intervalSelect = document.querySelector('#auto_collect_interval');
                                    const intervalMinutes = parseInt(intervalSelect.value) || 5;
                                    
                                    alert('✅ 자동 수집이 활성화되었습니다.\n\n' +
                                          '- 즉시 1회 실행됩니다.\n' +
                                          '- 이후 ' + intervalMinutes + '분마다 자동 처리됩니다.');
                                    
                                    // 즉시 실행
                                    executeProcessorNow();
                                } else {
                                    alert('✅ 자동 수집이 비활성화되었습니다.\n\n자동 처리가 중단됩니다.');
                                }
                            } else {
                                console.error('자동 수집 상태 저장 실패:', resJson);
                                alert('❌ 설정 저장 실패\n\n' + (resJson.msg || '알 수 없는 오류'));
                                
                                // 실패 시 체크박스 복원
                                checkbox.checked = !enabled;
                            }
                        } catch (e) {
                            console.error('응답 파싱 오류:', e);
                            alert('❌ 응답 처리 오류');
                            
                            // 오류 시 체크박스 복원
                            checkbox.checked = !enabled;
                        }
                    }
                }
            };
            sendPost(xhr, path, params);
        }
        
        /**
         * 수집 주기 변경
         */
        function changeCollectInterval() {
            const intervalSelect = document.querySelector('#auto_collect_interval');
            const intervalMinutes = parseInt(intervalSelect.value) || 5;
            
            console.log('수집 주기 변경:', intervalMinutes + '분');
            
            const path = "/jbs/jbg";
            const params = "cmd=changeCollectInterval&interval=" + intervalMinutes;
            
            const xhr = new XMLHttpRequest();
            xhr.onreadystatechange = function() {
                if (checkAjaxSuc(xhr)) {
                    const resMsg = xhr.responseText;
                    
                    if (resMsg.length > 0) {
                        try {
                            const resJson = JSON.parse(resMsg);
                            
                            if (resJson.code === '000') {
                                console.log('✅ 수집 주기 변경 완료:', intervalMinutes + '분');
                                
                                const intervalText = intervalMinutes >= 60 ? 
                                                    (intervalMinutes / 60) + '시간' : 
                                                    intervalMinutes + '분';
                                
                                alert('✅ 수집 주기가 변경되었습니다.\n\n새 주기: ' + intervalText);
                            } else {
                                console.error('수집 주기 변경 실패:', resJson);
                                alert('❌ 주기 변경 실패\n\n' + (resJson.msg || '알 수 없는 오류'));
                            }
                        } catch (e) {
                            console.error('응답 파싱 오류:', e);
                        }
                    }
                }
            };
            sendPost(xhr, path, params);
        }
        
        /**
         * FTP 파일 처리 즉시 실행
         */
        function executeProcessorNow() {
            console.log('FTP 파일 처리 즉시 실행 요청...');
            
            const path = "/jbs/jbg";
            const params = "cmd=executeProcessorNow";
            
            const xhr = new XMLHttpRequest();
            xhr.onreadystatechange = function() {
                if (checkAjaxSuc(xhr)) {
                    const resMsg = xhr.responseText;
                    
                    if (resMsg.length > 0) {
                        try {
                            const resJson = JSON.parse(resMsg);
                            
                            if (resJson.code === '000') {
                                console.log('✅ 즉시 실행 완료:', resJson);
                                
                                if (resJson.result) {
                                    console.log('  처리 결과:', resJson.result);
                                }
                            } else {
                                console.error('즉시 실행 실패:', resJson);
                            }
                        } catch (e) {
                            console.error('응답 파싱 오류:', e);
                        }
                    }
                }
            };
            sendPost(xhr, path, params);
        }
    </script>

    <div id="__endic_crx__">
        <div class="css-diqpy0"></div>
    </div>

</body>

</html>