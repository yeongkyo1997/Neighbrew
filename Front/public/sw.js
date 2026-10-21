self.addEventListener("install", e => {
  // console.log("[Service Worker] installed");
  self.skipWaiting();
});

// activate event
self.addEventListener("activate", e => {
  // console.log("[Service Worker] actived", e);
  e.waitUntil(self.clients.claim());
});

// fetch event
self.addEventListener("fetch", e => {
  // console.log("[Service Worker] fetched resource " + e.request.url);
});

self.addEventListener("notificationclick", function (event) {
  //알림 팝업 클릭(본문 또는 "goTab" 버튼) 시 해당 페이지로 이동
  event.notification.close(); // Notification을 닫습니다.
  var url = event.notification.data || "/";

  event.waitUntil(
    clients.matchAll({ type: "window" }).then(function (clientList) {
      var client = clientList[0];
      if (client && "navigate" in client) {
        return client.navigate(url).then(function (c) {
          return (c || client).focus();
        });
      }
      //열린 창이 없는 경우 새창으로
      if (clients.openWindow) {
        return clients.openWindow(url);
      }
    })
  );
});
