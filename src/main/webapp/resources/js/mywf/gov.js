$(function(){
	var GOV = {
			
			/**
			 * 注册丰台用户
			 */
			postRegistAppointUser:function(){
				var param = "&enterpriseId=" + enterpriseId;
				var value = true;
				$.ajax({
					url:"/gov/business/appoint/registUser",
					type:'post',    
				    cache:false,
				    data: param,
				    dataType:'json',
				    async:false,
				    complete:function(data){
						// 丰台用户注册失败
				    	if(data != undefined && data.responseText == "error" ){
				    		value = false;
				    	}
				    }
				});
				return value;
			},
			
			postRegistUser:function(cerNo, userName){
				var reVal = false;
				var param = "";
				if(cerNo != undefined && cerNo != ""){
					param += "&cerNo=" + cerNo;
				}
				if(userName != undefined && userName != ""){
					param += "&userName=" + userName;
				}
				if(businessKey != undefined && businessKey != ""){
					param += "&businessKey=" + businessKey;
				}
				if(enterpriseId != undefined && enterpriseId != ""){
					param += "&enterpriseId=" + enterpriseId;
				}
				if(openId != undefined && openId != ""){
					param += "&openid=" + openId;
				}
				var value = "";
				$.ajax({
					url:"/gov/business/postRegistUser",
					type:'post',    
				    cache:false,
				    data: param,
				    dataType:'json',
				    async:false,
					complete:function(data){
						if(data != undefined && data != "" 
							&& data.responseText != undefined 
							&& data.responseText != "undefined"
							&& data.responseText != ""){
							var reason = $.parseJSON(data.responseText);
				        	if(reason != undefined && reason != "" && reason.result == "true"){
				        		reVal = true;
				        		return true;
				        	}else{
				        		alert(reason.msg);
				        	}
						}else{
							alert("系统错误，请刷新后重试。")
						}
					}
				});
				return reVal;
			},
			
			checkUserAndPwd:function(userName, passWord){
				var param = "";
				if(userName != undefined && userName != ""){
					param += "&userName=" + userName
				}
				if(passWord != undefined && passWord != ""){
					param += "&passWord=" + passWord
				}
				var value = false;
				$.ajax({
					url:"/gov/business/checkUserAndPwd",
					type:'post',    
				    cache:false,
				    data: param,
				    dataType:'json',
				    async:false,
					complete:function(data){
						if(data != undefined && data != "" 
							&& data.responseText != undefined 
							&& data.responseText != "undefined" ){
							if(data.responseText == "true"){
								value= true;
							}
						}
					}
				});
				return value;
			}
	};
	window.location.GOV =window.GOV = GOV;
});