$(document).ready(function () {
   // var host = location.href.replace(/http:\/\/(.*)\/chat.html/i, "");
   window.CHAT = {
       serverAddr : "ws://localhost:8080/im",
       socket : null,
       nickName : "匿名",
       init : function(nickName){
           if(!window.WebSocket){
               window.WebSocket = window.MozWebSocket;
           }

           if(window.WebSocket){
               CHAT.socket = new WebSocket(CHAT.serverAddr);
               CHAT.socket.onmessage = function (e) {
                   alert(e.data);
               };
               CHAT.socket.onopen = function (e) {
                   // alert("WebSocket开启");
                   CHAT.socket.send("[LOGIN][" + new Date().getTime() + "][" + nickName + "]");
               };
               CHAT.socket.close = function (e) {
                   alert("WebSocket关闭");
               };
           }else{
               alert("你的浏览器不支持websocket协议，赶紧几把换一个！");
           }

       },
       login : function () {
           $("#error-msg").empty();
           var nickName = $("#nickName").val();

           if(nickName != ''){
               if(!/^\S{1,10}$/.test($.trim(nickName))){
                   $("#error-msg").html("昵称长度必须是10个字以内！");
                   return false;
               }
               $("#loginbox").hide();
               $("#chatbox").show();
               CHAT.nickName = nickName;
               $("#shownickname").html(nickName);
               CHAT.init(nickName);
           }else{
               $("#error-msg").html("请输入昵称才能进入聊天室");
               return false;
           }
           return false;
       },
       logout : function () {
           window.location.reload();
       }
   }
});