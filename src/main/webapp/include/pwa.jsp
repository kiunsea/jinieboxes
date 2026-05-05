<%-- <%@ page import="jakarta.servlet.ServletContext" %> --%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<script type="module">
    // Import the functions you need from the SDKs you need 
    import { initializeApp } from "https://www.gstatic.com/firebasejs/10.6.0/firebase-app.js";
    import { getMessaging, getToken, onMessage } from "https://www.gstatic.com/firebasejs/10.6.0/firebase-messaging.js";

    // https://firebase.google.com/docs/web/setup#available-libraries
    // Your web app's Firebase configuration
    // ⚠️ 자신의 Firebase 프로젝트(https://console.firebase.google.com) 설정값으로 교체하세요.
    //    Firebase Web SDK 의 apiKey 는 client-side 에 노출되도록 설계된 식별자이지만
    //    공개용 OSS 클론에서는 placeholder 로 두고 사용자가 직접 채우게 합니다.
    const firebaseConfig = {
        apiKey: "YOUR_FIREBASE_WEB_API_KEY",
        authDomain: "YOUR_PROJECT.firebaseapp.com",
        projectId: "YOUR_PROJECT_ID",
        storageBucket: "YOUR_PROJECT.appspot.com",
        messagingSenderId: "YOUR_SENDER_ID",
        appId: "YOUR_APP_ID",
        measurementId: "YOUR_MEASUREMENT_ID"
    };

    // Initialize Firebase
    const firebaseApp = initializeApp(firebaseConfig);
    const messaging = getMessaging(firebaseApp);

    var isServiceWorkerSupported = 'serviceWorker' in navigator;
    if (isServiceWorkerSupported)
    {
        navigator.serviceWorker.register('service-worker.js', { scope: './'})
            .then(reg => {
                console.log('[ServiceWorker] 등록 성공: ', reg.scope);
                Notification.requestPermission().then((permission) => {
                    if (permission === 'granted') {
                        console.log('Permission granted');

                        getToken(messaging, {
                            vapidKey: 'YOUR_FIREBASE_VAPID_KEY',
                            serviceWorkerRegistration: reg
                        }).then((token) => {
                            var jbs_fcm = localStorage.getItem("jbs_fcm");
                            if (!jbs_fcm && token) { //현재 브라우저로 처음 서비스에 접속 했을때만 물어본다.
                                onMessage(messaging, (payload) => {
                                    console.log('Messaging ', payload);
                                });
                                console.log('[FCM Token] token is : ', token);
                                if (confirm("이 기기에서 아이템의 기간 만료에 대한 알림을 받으시겠습니까?")) {
                                    sendTokenToServer(token);
                                } else {
                                    localStorage.setItem("jbs_fcm", 0);
                                }
                            } else {
                                console.log('[FCM Token] is first OR token is NULL', token);
                            }
                        }).catch((err) => {
                            console.log(err);
                        });

                    } else {
                        console.log('Permission denied');
                    }
                });
            })
            .catch(function(err)
            {
                console.log('[ServiceWorker] 등록 실패: ', err);
            });
    }

    //발급된 Instance ID Token을 서버에 전송
    const sendTokenToServer = function (token)
    {
        var data = {
            'cmd' : 'saveToken',
            'token' : token
        };
        return fetch('/jbs/fcm', 
            {
                method: 'POST',
                cache: "no-cache",
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify(data)
            })
            .then(function (response)
            {
                if (!response.ok)
                    console.log('[InstanceID Token] 서버전송 실패: ', response);
                else
                    console.log('[InstanceID Token] 서버전송 완료: ', response);
                return response.json();
            })
            .then(function (responseData)
            {
                if (!(responseData && responseData.Success))
                    console.log('[InstanceID Token] 서버전송 응답(실패): ', responseData);
                else {
                    localStorage.setItem("jbs_fcm", 1);
                    console.log('[InstanceID Token] 서버전송 응답(성공): ', responseData);
                }
            });
    }
</script>