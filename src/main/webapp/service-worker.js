self.oninstall = (e) => {
    console.log('[ServiceWorker] installed');
    self.skipWaiting();
    /**
    이벤트내에서 처리해야할 로직이 있고 작업이 끝날때까지 기다려야 한다면 다음과 같이 작성
    
    //'Ripple'이라는 이름으로 캐시를 열고 캐시에 필요한 리소스들을 추가할때까지 기다리게 한다
    e.waitUntil(
        caches.open('Ripple')
            .then(cache => {
                return cache.addAll([
                    '/',
                    '/index.html',
                    '/styles.css',
                    '/script.js',
                    '/images/logo.png'
                    //계속해서 필요한 리소스를 캐싱
                ]);
            })
            .then(() => {
                console.log('[ServiceWorker] Resources cached');
            })
    );    
    */
}

self.onactivate = (e) => {
    console.log('[ServiceWorker] activated');
    e.waitUntil(self.clients.claim()); // 제어권획득
    
    /**
    새로운 버전의 서비스 워커가 활성화된 경우 발생하는 이벤트이다.
    새로운 서비스 워커가 실행되었기 때문에 다음과 같이 캐시를 검사하여 초기화를 할 수 있다.
    
    e.waitUntil(caches.keys()
        .then(eacheNames => {
            return Promise.all(cacheNames.map
                (cacheName => {
                    if (cacheName !== 'Ripple') {
                        //다른 버전의 캐시를 정리
                        return caches.delete(cacheName);
                    }
                }));
        }).then(() => {
            console.log('[ServiceWorker] delete cache~');
        }));
    */
}

let reqDataCount = 0
self.onfetch = (e) => {
    console.log('[ServiceWorker] fetch request url', e.request.url);
    e.respondWith(fetch(e.request)); // 기존요청 그대로 보내기

    /**
    필요에 따라 아래와 같이 서버의 리소스 요청을 가로채어 작업을 수행하고 
    캐시를 활용하여 리소스를 반환하거나 네트워크 요청을 수정할 수 있다.
    
    // data.json 에 대한 요청을 가로채기
    if (e.request.url.endsWith('/data.json')) {
        reqDataCount++;
        e.respondWith(new Response(JSON.stringify({
                    reqDataCount
                }), {
                headers: {
                    'Content-Type': 'application/json'
                }
            }))

        return;
    }
    // 다음의 코드를 넣으면 무조건 캐시에서 리소스를 반환하도록 강제
    e.respondWith(
        caches.match(e.request)
            .then((response) => {
                if(response) { //캐시를 찾으면 캐시를 반환하고 종료
                    return response;
                } else { //캐시가 없으면 기존 요청 그대로 반환
                    return fetch(e.request);
                }
            })
            .then(error => {
                console.error('[ServiceWorker] error!!', error);
            })
    );    
    **/
 
}

//Push Message 수신 이벤트
self.onpush = (e) => {
    console.log('[ServiceWorker] 푸시알림 수신: ', e);
    console.log('[ServiceWorker] 푸시알림 내용: ', e.data.json().notification);

    //Push 정보 조회
    const noti = e.data.json().notification;
    var title = noti.title || '알림';
    var body = noti.body || 'no message';
    var icon = noti.icon || '/Images/icon.png'; //512x512
    var options = {
        body: body,
        icon: icon
    };
    
    //Notification 출력
    e.waitUntil(self.registration.showNotification(title, options));
}

//사용자가 Notification을 클릭했을 때
self.onotificationclick = (e) => {
    console.log('[ServiceWorker] 푸시알림 클릭: ', e);
    e.notification.close();
    e.waitUntil(clients.matchAll({
            type: "window"
        }).then(function (clientList) {    
            //실행된 브라우저가 있으면 Focus
            for (var i = 0; i < clientList.length; i++) {
                var client = clientList[i];
                if (client.url == '/' && 'focus' in client)
                    return client.focus();
            }
            //실행된 브라우저가 없으면 Open
            if (clients.openWindow)
                return clients.openWindow('https://localhost:44337/');
        }));
};
