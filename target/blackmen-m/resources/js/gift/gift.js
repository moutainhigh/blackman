//@author bruce
//@赠送的大礼包活动js all In here

var InterValObj; //timer变量，控制时间  
var count = 120; //间隔函数，1秒执行  
var curCount;//当前剩余秒数  


//获取验证码
function MysendCode(){
	
	
	var phoneNum =  $("#userphone").val(); 
    var tokenstr = $("#token").val();
  //校验手机号码
	if(!checkPhoneFormat(phoneNum)){
		$("#J_send_code").removeAttr("disabled");//将按钮置为可点击 
		return false;
	}
  
	$("#J_send_code").attr("disabled",true); //将按钮置为可点击 
	$.ajax({

        url:"/m/gift/phoneFlag",

        type:"post",
        
        data: {"phoneNum":phoneNum},
        
        success:function(data){
			if(data=='0'){
				curCount = count;
				// 设置button效果，开始计时  
				$("#J_send_code").html(curCount + "s");
				InterValObj = window.setInterval("SetRemainTime()", 1000); // 启动计时器，1秒执行一次  

				// 向后台发送处理数据  
				$.ajax({
			        type: "POST", // 用POST方式传输  
			        dataType: "json", // 数据格式:JSON  
			        url: "/m/gift/sendPhoneCode", // 目标地址  
			        data: "phoneNum=" + phoneNum+"&tokenstr="+tokenstr,
			        success:function(data){
			        	if(data.tokenerror=='1'){
			        		swal({
			        			  title: '请不要频繁请求验证码!',
			        			  text: '',
			        			  timer: 1000
			        			});
			        		return false;
			        	}
			        	if(data.flag == "-1"){
			        		curCount = 0;
			        		swal(
									  '验证码发送失败!请重新发送',
									  '',
									  'error'
							);
			        		$("#J_send_code").removeAttr("disabled");//将按钮置为可点击 
			        		return false;
			        	}
			        	if(data.flag == "2"){
			        		swal(
									  '已发送语言验证码,请注意查收',
									  '',
									  'success'
							);
			        	}
			        	
			        }
				});
			}else{
				swal(
						  '老用户不能参与此活动.',
						  '',
						  'warning'
				);
//				$("#J_old_pop").addClass('is-visible');
//				$("#J_old_text").html('亲，老用户是不能参加这次活动的哦~');
				return false;
			}
        }
	});
  	
	

}


//一键预约
$(function(){
	$("#J_get_btn").click(function(){
		var phoneNum =  $("#userphone").val(); 
		var usercode = $("#usercode").val();
		
		if(!usercode){
			swal(
					  '请输入验证码!',
					  '',
					  'warning'
			);
			return fasle;
		}
		if(checkPhoneFormat(phoneNum)){
			// 向后台发送处理数据  
			$.ajax({
		        type: "POST", // 用POST方式传输  
		        dataType: "json", // 数据格式:JSON  
		        url: "/m/gift/receive", // 目标地址  
		        data: "phoneNum=" + phoneNum+"&usercode="+usercode,
		        success:function(data){
		        	swal({
	        			  title: data.msg,
	        			  text: '',
	        			  timer: 1000
	        			});
//		        	 $("#J_old_pop").addClass('is-visible');
//					 $("#J_old_text").html(data.msg);
	                 if(data.ret=="ok"){


	                     window.location.href = "/m/gift/showgift?uid="+data.uid;

	                 }else{
	                	 return false;
	                 }
	                 
		        	
		        }
			});
		}
	})
})


function checkPhoneFormat(phoneNum){
	if(phoneNum == "" || !(/^1[3|4|5|7|8][0-9]\d{8}$/.test(phoneNum))){
		swal(
				  '输入的手机为空或格式不正确!',
				  '',
				  'error'
		)
		return false;
	}
	return true;
}

function SetRemainTime(){
	if (curCount == 0) {
		window.clearInterval(InterValObj); // 停止计时器  
		$("#J_send_code").html("重新发送");
		$("#J_send_code").removeAttr("disabled");//将按钮置为可点击 
	} else {
		curCount--;
		$("#J_send_code").html(curCount + "s");
		$("#J_send_code").attr("disabled",true); //将按钮置为可点击 
	}
}

$(function(){
    setTimeout(function () {
        $(".tshide").hide();
    }, 5000);

});
