var InterValObj; //timer变量，控制时间  
var count = 120; //间隔函数，1秒执行  
var curCount;//当前剩余秒数  
//var code = ""; //验证码
//var codeLength = 6;//验证码长度
jQuery.extend({
	sendMessage : function(){
		$(".codebtn").disabled = true; //将按钮置为不可点击 
	    var phoneNum =  $("#userphone").val(); 
	    var validatecode = $("#validatecode").val();
	    var tokenstr = $("#token").val();
		//校验手机号码
		if(!jQuery.checkPhoneFormat(phoneNum)){
			$(".codebtn").disabled = false; //将按钮置为可点击 
			return false;
		}
		//校验图形验证码
		if(isEmpty(validatecode)){
			$(".codebtn").disabled = false; //将按钮置为可点击 
			alert("图形验证码不能为空");
			return false;
		}

		curCount = count;
		// 设置button效果，开始计时  
		$(".codebtn").val(curCount + "s");
		InterValObj = window.setInterval("jQuery.SetRemainTime()", 1000); // 启动计时器，1秒执行一次  
		// 向后台发送处理数据  
		$.ajax({
            type: "POST", // 用POST方式传输  
            dataType: "json", // 数据格式:JSON  
            url: "/common/sendPhoneCode", // 目标地址  
            data: "phoneNum=" + phoneNum+"&tokenstr="+tokenstr+"&validatecode="+validatecode,
            success:function(data){
            	if(data.flag == "8"){
            		$("#validateimg").click();
            		$("#box_btn").removeAttr("disabled");
            		alert("图形验证码不能为空！");
            		curCount = 0;
            		return false;
            	}
            	if(data.flag == "9"){
            		$("#validateimg").click(); //刷新图形验证码
            		alert("输入图形验证码不正确!");
            		curCount = 0;
            		return false;
            	}
            	if(data.flag == "-1"){
            		$("#validateimg").click();
            		curCount = 0;
            		alert("验证码发送失败!请重新发送");
            		return false;
            	}
            	if(data.flag == "2"){
            		alert("已发送语言验证码,请注意查收!");
            	}
            }
		});
	},
	SetRemainTime : function(){
		if (curCount == 0) {
			window.clearInterval(InterValObj); // 停止计时器  
			$(".codebtn").val("重新发送");
			$(".codebtn").disabled = false; //将按钮置为可点击 
		} else {
			curCount--;
			$(".codebtn").val(curCount + "s");
		}
	},
	/***
	 * 校验手机发送的验证码
	 * @return true 输入验证码正确 false 输入验证码错误
	 */
	checkPhoneCode : function(){
	    var phoneNum =  $("#userphone").val(); 
	    var validatecode = $("#validatecode").val();
	    var tokenstr = $("#token").val();
	    var code = $("#usercode").val();
		
	    //校验手机号码
		if(!jQuery.checkPhoneFormat(phoneNum)){
			return false;
		}
		//校验图形验证码
		if(isEmpty(validatecode)){
			alert("图形验证码不能为空");
			return false;
		}
		if(code == "" && code.length != 6){
			alert("输入的验证码为空或格式不正确!");
			return false;
		}
		return true;
	},
	/**
	 * 校验手机格式
	 */
	checkPhoneFormat : function(phoneNum){
		if(phoneNum == "" || !(/^1[3|4|5|7|8][0-9]\d{8}$/.test(phoneNum))){
			alert("输入的手机为空或格式不正确!");
			return false;
		}
		return true;
	}
});