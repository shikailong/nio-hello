Date.prototype.Format = function(fmt)
{
    var o = {
        "M+" : this.getMonth()+1,                 //月份
        "d+" : this.getDate(),                    //日
        "h+" : this.getHours(),                   //小时
        "m+" : this.getMinutes(),                 //分
        "s+" : this.getSeconds(),                 //秒
        "q+" : Math.floor((this.getMonth()+3)/3), //季度
        "S"  : this.getMilliseconds()             //毫秒
    };
    if(/(y+)/.test(fmt))
        fmt=fmt.replace(RegExp.$1, (this.getFullYear()+"").substr(4 - RegExp.$1.length));
    for(var k in o)
        if(new RegExp("("+ k +")").test(fmt))
            fmt = fmt.replace(RegExp.$1, (RegExp.$1.length==1) ? (o[k]) : (("00"+ o[k]).substr((""+ o[k]).length)));
    return fmt;
}

$(document).ready(function () {
   // var host = location.href.replace(/http:\/\/(.*)\/chat.html/i, "");
   window.CHAT = {
       serverAddr : "ws://localhost:8080/im",
       socket : null,
       nickName : "匿名",
       init : function(nickName){

           var addSystemTip = function (c) {
             var html = "";
             html += '<div class="msg-system">';
             html += c;
             html += '</div>';
             var section = document.createElement('section');
             section.className = 'system J-mjrlinkWrap J-cutMsg';
             section.innerHTML = html;

             $("#onlinemsg").append(section);
           };
           
           var appendToPanel = function (message) {
               var regx = /^\[(.*)\](\s\-\s(.*))?/g;
               var group = '',
                   label = '',
                   content = '',
                   cmd = '',
                   time = 0,
                   name = '';
               while (group = regx.exec(message)){
                   label = group[1];
                   content = group[3];
               }

               var labelArr = label.split("][");
               cmd = labelArr[0];
               time = labelArr[1];
               name = labelArr[2];
               if(cmd == 'SYSTEM'){
                   var total = labelArr[2];
                   $("#onlinecount").html("" + total);
                   addSystemTip(content);
               }else if(cmd == 'CHAT'){
                   var date = new Date(parseInt(time));
                   addSystemTip('<span class="time-label">' + date.Format("hh:mm:ss") + '</span>');
                   var isme = (name == 'you') ? true : false;
                   var contentDiv = '<div>' + content + '</div>';
                   var userNameDiv = '<span>' + name + '</span>';

                   var section = document.createElement('section');
                   if(isme){
                       section.className = 'user';
                       section.innerHTML = contentDiv + userNameDiv;
                   }else{
                       section.className = 'service';
                       section.innerHTML = userNameDiv + contentDiv ;
                   }
                   $("#onlinemsg").append(section);
               }else if(cmd == 'FLOWER'){
                   addSystemTip(content);

                   // 鲜花特效
                   $(document).snowfall('clear');
                   $(document).snowfall({
                       image : '/images/face/50.gif',
                       flakeCount : 60,
                       minSize : 20,
                       maxSize : 40
                   });

                   window.flowerTime = window.setTimeout(function () {
                       $(document).snowfall('clear');
                       window.clearTimeout(flowerTime);
                   }, 5000);
               }
           }

           if(!window.WebSocket){
               window.WebSocket = window.MozWebSocket;
           }

           if(window.WebSocket){
               CHAT.socket = new WebSocket(CHAT.serverAddr);
               CHAT.socket.onmessage = function (e) {
                   // alert(e.data);
                   appendToPanel(e.data);
               };
               CHAT.socket.onopen = function (e) {
                   // alert("WebSocket开启");
                   CHAT.socket.send("[LOGIN][" + new Date().getTime() + "][" + nickName + "]");
               };
               CHAT.socket.close = function (e) {
                   // alert("WebSocket关闭");
                   appendToPanel("[SYSTEM][" + new Date() + "][0] - 服务器关闭，暂不能聊天！" )
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
       },
       clear : function(){
            CHAT.box.innerHTML = "";
       },
       sendText : function(){
           var message = $("#send-message");

           if(message.html().replace(/\s/ig, "") == ""){
               return false;
           }
           if(!window.WebSocket){
               return false;
           }
           if(CHAT.socket.readyState == WebSocket.OPEN){
               var msg = ("[CHAT][" + new Date().getTime() + "][" + CHAT.nickName + "]" + " - " + message.html());
               CHAT.socket.send(msg);
               message.empty();
               message.focus();
           } else{
               alert("与服务器连接失败！");
           }

       }
   }
});