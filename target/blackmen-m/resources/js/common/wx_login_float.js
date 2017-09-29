//取消按钮
$("a.loginWinQxBtn").on('click', function() {
	$(".cd-box4loginWin").removeClass("is-visible");
})

function initloginWin(handleType) {
	// 新用户
	$(".cd-box4loginWin").addClass("is-visible");
	window.clearInterval(InterValObj);// 停止计时器  
	//把剩余秒数改成重新发送
	$("#se_btn").show();
	$("#in_btn").hide();
	//清空数据
	$("#J_mobile").val("");
	$("#valicodeinput").val("");
	$("#phoneNumvali").val("");
	$("#valicodeImg").click();//重新获取图形验证码
	$("#loginBtn").unbind("click").click(function() {
		// 登陆成功
		var isLogin = myLogin();

	});
}

//登录
function myLogin() {
	var reval = false;
	var phoneNum = $("#J_mobile").val();
	var code = $("#phoneNumvali").val();
	var token = $("#token").val();
	var valicod = $("#valicodeinput").val();

	if (phoneNum == "") {
		alert("手机号码为空！！");
		return false;
	}
	if (valicod == "") {
		alert("图形验证码为空！！");
		return false;
	}
	if (token == "") {
		alert("请刷新重试！！");
		return false;
	}
	if (code == "") {
		alert("输入的验证码为空！！");
		return false;
	}
	if (code.length != 6) {
		alert("输入的验证码有误！！");
		return false;
	}
	jQuery.ajax({
		url : '/wx/loginAction',
		type : "post",
		data : {
			"userphone" : phoneNum,
			"usercode" : code,
			"token" : token,
			"validatecode" : valicod
		},
		dataType : "json",
		async : false,
		success : function(data, textStatus) {
			if (data.ret == "success") {
				$(".cd-box4loginWin").removeClass("is-visible");
				reval = true;
				return true;

			} else if (data.ret == "404") {
				alert("404!");
			} else {
				alert(data.msg);
			}
		},
		error : function(XMLHttpRequest, textStatus, errorThrown) {
		}
	});
	return reval;
};

//是否是新用户
function isNewUser() {
	var flag = false;
	//通过openid检测是否是新用户
	$.ajax({
		url : "/weixin/mywf/isLogin",
		type : "post",
		dataType : "json",
		async : false,
		data : {
			"openId" : openId
		},
		success : function(data) {
			if (data.result == false) {
				//如果是新用户
				flag = true;
			}
		},
		error : function() {
			flag = false;
			alert("未知错误..");
		}
	});
	return flag;
}
