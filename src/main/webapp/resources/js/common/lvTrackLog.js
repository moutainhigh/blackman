var localLogName = document.getElementById('lvzhengLog').getAttribute('data');
var lv_referrer = encodeURIComponent(document.referrer);
function lv_ajaxsend(tracksrc) {
    (new Image).src = encodeURI(tracksrc);
}

function lv_clickLog(clickfrom) {
	lv_ajaxsend("/lv/empty.png?" + clickfrom + "&pageUrl" + lv_referrer + "&logName="+localLogName+"&rand=" + Math.random());
}
$(document).ready(function(){
	
	$("[trace]").on("click",function(){
		var str = "";
		var module = $(this).attr("t-module");
		var value = $(this).attr("t-value");
		str += "moudle="+module;
		str += "&data_action=click";
		str += "&data_value="+value;
//		alert(localLogName+"-->"+str);
		lv_clickLog(str);
	});
	
	$("[traceb]").on("blur",function(){
		var str = "";
		var module = $(this).attr("t-module");
		var value = $(this).val();
		if (value == null || value == "") 
			return;
		str += "moudle="+module;
		str += "&data_action=click";
		str += "&data_value="+value;
//		alert(localLogName+"-->"+str);
		lv_clickLog(str);
	});
	
});

