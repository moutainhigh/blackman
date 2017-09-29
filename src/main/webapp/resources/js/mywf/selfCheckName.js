// 弹出层-行业特点
jQuery(document).ready(function($){
	//open popup
	$('.pop_box1_Industry').on('click', function(event){
		event.preventDefault();
		$('.cd-box1-Industry').addClass('is-visible');
	});
	//close popup
	$('.cd-box1-Industry').on('click', function(event){
		if( $(event.target).is('.chek_bg li') || $(event.target).is('.qxbtn') || $(event.target).is('.cd-box1-Industry') ) {
			event.preventDefault();
			$(this).removeClass('is-visible');
		}
	});
	//close popup when clicking the esc keyboard button
	$(document).keyup(function(event){
    	if(event.which=='27'){
    		$('.cd-box1-Industry').removeClass('is-visible');
	    }
    });
});


//选择
$(function(){
    $(".chek_bg li").each(function(){
        $(this).click(function(){
            $(".chek_bg li").addClass("b1").removeClass("checkon");
            $(this).addClass("checkon").removeClass("b1");
        })
    });
    
    //点击查看
    /* $("#lateTaskLink").click(function(){
    	var procInstId = $(this).attr("data-ProcInstId");
    	var taskId = $(this).attr("data-TaskId");
    	
    	window.location.href = "/weixin/mywf/business/companyRegTask?openId="+openId;
    	return true;
    }); */
    
    $("#lateTaskDivLink").click(function(){
    	var procInstId = $(this).find('a#lateTaskLink').attr("data-ProcInstId");
    	var taskId = $(this).find('a#lateTaskLink').attr("data-TaskId");
    	
    	//window.location.href = "/weixin/mywf/business/companyRegTask?openId="+openId;
    	window.location.href = "/wx/myservice/free.html?openId=" + openId;
    	return true;
    });
    
    
    //清空数据
    var shopName = $("#shopName").val();
    var busCode = $("#busCodeHidden").val();
    var busHtml = $(".cd-box1-Industry").find('ul li[data-code='+busCode+']').html();
    var busVal = $(".cd-box1-Industry").find('ul li[data-code='+busCode+']').attr('data-value');
    $("#industry_txt").html(busHtml);
    $("#name_see").val(shopName);
    var industry_see = shopName + busVal;
    console.log(industry_see);
    if(industry_see == null || industry_see == '' || industry_see == 'undefined'){
    	industry_see = "";
    }
    $("#industry_see").html(industry_see);
    
});

$(function(){
	// click .chek_bg li 行业特点
	$('.cd-box1-Industry ul li').on('click',function(event){
		var li = $(this).html();
		//行业特点
		var industry = $(this).attr("data-value");
		//主营业务
		var busCode = $(this).attr('data-code');
		var busUniteCode = $(this).attr('data-uniteCode');
		
		$("#industryHidden").val(industry);
		$("#busCodeHidden").val(busCode);
		$("#busUniteCodeHidden").val(busUniteCode);
		
		
		$("#industry_see").html(industry);
		$("#industry_txt").val(li);
		$('.cd-box-Industry').removeClass('is-visible');
		
		var shopName = $("#shopName").val();
		lv_clickLog("moudle=selfCheckName_industry&data_openId="+openId+"&data_action=click&data_shopName="+shopName+"&data_mainBusCode="+busCode);		
		
	}); 

	//click .hm-box  input.text_in  字号
	$('.hm-box input.text_in').on('keyup',function(event){
		var inputVal = $(event.target).val();
		$("#name_see").html(inputVal);
	});
	$('.hm-box input.text_in').on('change',function(event){
		var inputVal = $(event.target).val();
		$("#name_see").html(inputVal);
	});
	
	//initial .check display is none
	$('.btn-box .check').css('display','none'); 
	
	
	//检测是否可用按钮
	$('#checkNameBtn2').unbind('click').bind('click',function(event){
		$('#checkNameBtn').css('display',''); //显示
		$("#checkNameBtn2").css('display','none');//隐藏
		
		//验证
		var validateArray = new Array();
		validateArray.push($("div.hm-box"));
		if(!JX.validateArray(validateArray)){//验证不通过
			$('#checkNameBtn').css('display','none'); //隐藏
			$("#checkNameBtn2").css('display','');//显示
			return;
		}
		
		//图形验证码弹出框
		$('.cd-box2CheckNameImgCode').addClass('is-visible');
		$('#checkNameImgCodeInput').val('');
		$('#checkNameImgCode').click();
		$('.okbtn_checkNameImgCode').unbind('click').click(function(){//每次都重新绑定事件，防止重复点击
			okbtnEvent();
		});
		
		
	});
	
	
	$('.qxbtn_checkNameImgCode').click(function(){
		$('#checkNameBtn').css('display','none'); //隐藏
		$("#checkNameBtn2").css('display','');//显示
		$('.cd-box2CheckNameImgCode').removeClass('is-visible');
	});
	
});

//验证输入的图形验证码是否正确
function validateCheckNameImgCode(tokenstr,validatecode){
	var flag = false;
	$.ajax({
		url:'/weixin/mywf/validateImgCode',
		type:'post',
		async:false,
		dataType:'json',
		data:{"validatecode":validatecode,"tokenstr":tokenstr},
		success:function(data){
			if(data.flag == '8'){//信息为空
				alert('请输入验证码！');
				//重新绑定事件
				$('.okbtn_checkNameImgCode').unbind('click').click(function(){//每次都重新绑定事件，防止重复点击
					okbtnEvent();
				});
				flag = false;
			}else if(data.flag == '9'){//输入的验证码不正确
				alert('输入的验证码不正确');
				//重新绑定事件
				$('.okbtn_checkNameImgCode').unbind('click').click(function(){//每次都重新绑定事件，防止重复点击
					okbtnEvent();
				});
				flag = false;
			}else if(data.flag == '1'){
				flag = true;
			}
		},
		error:function(){
			alert('校验图形验证码出错，请重新输入验证码..');
			flag = false;
		}
	});
	return flag;
}

//检测名称是否可用
function checkNameFun(){
	/*获取参数*/
	var hangye = $("#industryHidden").val();
	var localCity = $("#localCity").html();
	var zihao = $("#shopName").val();
	var hbdm = $("#busUniteCodeHidden").val();
	var hydm = $("#busCodeHidden").val();
	var organizationType = $("#organizationTypeHidden").val();//组织形式
	var param = "?industryCharacteristics="+hangye+"&localCity="+localCity+"&hbdm="+hbdm+"&hydm="+hydm+"&zihao="+zihao+"&organizationType="+organizationType+"&openId="+openId;
	var url = "/weixin/mywf/checkName" + param;
	window.location.href = encodeURI(url);
	
	lv_clickLog("moudle=selfCheckName_checkBtn&data_openId="+openId+"&data_action=click&data_shopName="+zihao+"&data_mainBusCode="+hydm);
	return true;
}

function okbtnEvent(){
	$('.okbtn_checkNameImgCode').unbind('click');
	var validatecode = $('#checkNameImgCodeInput').val();
	if(validatecode == ''){
		alert('请输入图形验证码!');
		return;
	}
	var tokenstr = $('#token').val();
	if(tokenstr == ''){
		$('.cd-box2CheckNameImgCode').removeClass('is-visible');
		alert('请刷新页面重试..');
		return;
	}
	//同步检测图形验证码是否正确
	var ckCodeFlag = validateCheckNameImgCode(tokenstr,validatecode);
	if(ckCodeFlag){
		//检测名称是否可用
		checkNameFun();
	}
}


