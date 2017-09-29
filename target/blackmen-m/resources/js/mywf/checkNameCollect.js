// 弹出层2 -- 股东信息
jQuery(document).ready(function($){
	//open popup
	clickAddOrEditFun();
	
	//close popup
	$('.cd-box2-partnerInfo').on('click', function(event){
		if( $(event.target).is('.cd-popup-close') || $(event.target).is('.qxbtn') || $(event.target).is('.cd-box2-partnerInfo') ) {
			event.preventDefault();
			$(this).removeClass('is-visible');
		}
	});
	//close popup when clicking the esc keyboard button
	$(document).keyup(function(event){
    	if(event.which=='27'){
    		$('.cd-box2-partnerInfo').removeClass('is-visible');
	    }
    });
});


//弹出层3 -- 股东类型
jQuery(document).ready(function($){
	//open popup
	$('.pop_box3_partnerType').on('click', function(event){
		event.preventDefault();
		var role = $(this).find('input[type="text"]').attr('data-role');
		$('#partnerType').find('li').addClass('b1').removeClass('checkon');
		$('#partnerType').find('li[data-role="'+role+'"]').removeClass('b1').addClass('checkon');
		
		$('.cd-box3-partnerType').addClass('is-visible');
	});
	//close popup
	$('.cd-box3-partnerType').on('click', function(event){
		if( $(event.target).is('.cd-popup-close') || $(event.target).is('.qxbtn') || $(event.target).is('.cd-box3-partnerType') ) {
			event.preventDefault();
			$(this).removeClass('is-visible');
		}
	});
	//close popup when clicking the esc keyboard button
	$(document).keyup(function(event){
    	if(event.which=='27'){
    		$('.cd-box3-partnerType').removeClass('is-visible');
	    }
    });
});


//弹出层4
jQuery(document).ready(function($){
	//open popup
	/*$('.pop_box4_sav').on('click', function(event){
		event.preventDefault();
		$('.cd-box4').addClass('is-visible');
	});*/
	
	//close popup
	$('.cd-box4').on('click', function(event){
		if( $(event.target).is('.cd-popup-close') || $(event.target).is('.qxbtn') || $(event.target).is('.cd-box4') ) {
			event.preventDefault();
			$(this).removeClass('is-visible');
		}
	});
	//close popup when clicking the esc keyboard button
	$(document).keyup(function(event){
    	if(event.which=='27'){
    		$('.cd-box4').removeClass('is-visible');
	    }
    });
});


// 选择
$(function(){
	$(".partnerTypeLi").click(function(){//股东类型
		var roleType = $(this).attr('data-role');
		loadPartnerUL(roleType); //通过股东类型加载PartnerUL标签
		$(".cd-box3-partnerType").removeClass("is-visible");
	});
	
    $(".chek_bg li").click(function(){
        $(".chek_bg li").addClass("b1").removeClass("checkon");
        $(this).addClass("checkon").removeClass("b1");
    });
    
    //登录box  点击--取消
    $("a.loginWinQxBtn").on('click',function(){
    	$(".cd-box4loginWin").removeClass("is-visible");
    })
    
    //修改股东信息box  点击--取消
    $("a.mbtn.cancel").on('click',function(){
    	$(".cd-box2-partnerInfo").removeClass('is-visible');
    });
    
    $('#savBtn').on('click', function(event){
    	event.preventDefault();
    	//验证
    	var validateArray = new Array();
    	validateArray.push($("#alertBox"));
    	if(!JX.validateArray(validateArray)){//验证不通过
    		return;
    	}
    	var handleType = $("#handleTypeHidden").val();//操作类型
    	if(handleType == "add"){
    		//隐藏--删除按钮
    		$("#delBtn").hide();
    		//判断添加的事自然人股东还是单位股东
    		var roleType = $("#partnerTypeHidden").val();
    		var addLiDom = "";
    		if(roleType == 'legalPartner'){//单位股东
    			var busLicenseNum = $("#busLicenseNumTxt").val();//营业执照
        		var companyName = $("#companyNameTxt").val();
    			//******检测要添加的股东营业执照号是否已经存在
    			var spanDoms = $(".add-hm ul li[data-isFocus=false]").find('span');
    			var idNumArr = []; // 定义一个空数组
    			for(var i=0;i<spanDoms.length;i++){
    				idNumArr.push(spanDoms.eq(i).text());
    			}
        		var sameFlag = isSameIdNum(busLicenseNum,idNumArr);
        		if(sameFlag){
        			alert("已有同样的营业执照号码，请检查后重新输入！");
        			return;
        		}
        		//******
        		addLiDom = '<li data-isFocus="false">'
					+'<label class="name">'+ companyName +'</label>'
					+'<span class="text">'+ busLicenseNum +'</span>'
					+'<a href="javascript:void(0);" class="edit pop_box2Partner">编辑</a>'
					+'<div data-info="partnerInfo" data-roleType="legalPartner" >'
						+'<input id="name" data-flag="companyNameHidden" type="hidden" value="' + companyName + '" data-info="partnerInfo:main"/>'
						+'<input id="businessLicenseNum" data-flag="busLicenseNumHidden" type="hidden" value="' + busLicenseNum + '" data-info="partnerInfo:main"/>'
					+'</div>'
				+'</li>'
				
				$(".add-hm ul").append(addLiDom);
    		}else if(roleType == 'naturalPartner'){//自然人股东
    			var idCard_add = $("#idCard").val();
        		var name_add = $("#nameTxt").val();
        		//******检测要添加的股东身份证号是否已经存在
        		var spanDoms = $(".add-hm ul li[data-isFocus=false]").find('span');
    			var idNumArr = []; // 定义一个空数组
    			for(var i=0;i<spanDoms.length;i++){
    				idNumArr.push(spanDoms.eq(i).text());
    			}
        		var sameFlag = isSameIdNum(idCard_add,idNumArr);
        		if(sameFlag){
        			alert("已有同样的身份证号码，请检查后重新输入！");
        			return;
        		}
        		//******
        		addLiDom = '<li data-isFocus="false">'
					+'<label class="name">'+ name_add +'</label>'
					+'<span class="text">'+ idCard_add +'</span>'
					+'<a href="javascript:void(0);" class="edit pop_box2Partner">编辑</a>'
					+'<div data-info="partnerInfo" data-roleType="naturalPartner" >'
						+'<input id="name" data-flag="nameHidden" type="hidden" value="' + name_add + '" data-info="partnerInfo:main"/>'
						+'<input id="idNum" data-flag="idCardHidden" type="hidden" value="' + idCard_add + '" data-info="partnerInfo:main"/>'
					+'</div>'
				+'</li>'
				
				$(".add-hm ul").append(addLiDom);
        		//如果添加的是第一个股东，则将股东信息赋值到 firstName和firstIDCard中
        		if($(".add-hm ul li").length == 1){
        			var regUserName = $("#registUserName").val();
        			var regUserCerNo = $("#registUserCerNo").val();
        			if(regUserName == "" && regUserCerNo ==""){//都为空才修改
    	    			$("#registUserName").val(name_add);
    	    			$("#registUserCerNo").val(idCard_add);
        			}
        		}
    		}else{
    			alert('获取股东类型失败，请重新填写..');
    			return;
    		}
        	//open popup
        	clickAddOrEditFun();
    	}else if(handleType == "edit"){
    		//选中的<li>
    		var checkedLi = $(".add-hm ul li[data-isFocus=true]");
    		var nameLabelDom = $(checkedLi).children('label');
    		var spanDom = $(checkedLi).children('span');
    		
    		//显示--删除按钮
    		$("#delBtn").show();
    		//判断添加的是自然人股东还是单位股东
    		var roleType = $("#partnerTypeHidden").val();
    		if(roleType == 'legalPartner'){//单位股东
    			var busLicenseNumTxt_edit = $("#busLicenseNumTxt").val();
        		var companyNameTxt_edit = $("#companyNameTxt").val();
        		var busLicenseNumHiddenDom = $(checkedLi).find('div input[data-flag=busLicenseNumHidden]');
        		var companyNameHiddenDom = $(checkedLi).find('div input[data-flag=companyNameHidden]');
    			//修改相关的值
        		$(nameLabelDom).text(companyNameTxt_edit);
        		$(spanDom).text(busLicenseNumTxt_edit);
    			$(busLicenseNumHiddenDom).val(busLicenseNumTxt_edit);
        		$(companyNameHiddenDom).val(companyNameTxt_edit);
    		}else{//自然人股东s
    			var idCard_edit = $("#idCard").val();
        		var name_edit = $("#nameTxt").val();
    			var nameHiddenDom = $(checkedLi).find('div input[data-flag=nameHidden]');
        		var idCardHiddenDom = $(checkedLi).find('div input[data-flag=idCardHidden]');
    			//修改相关的值
        		$(nameLabelDom).text(name_edit);
        		$(spanDom).text(idCard_edit);
    			$(nameHiddenDom).val(name_edit);
        		$(idCardHiddenDom).val(idCard_edit);
        		
       			var regUserName = $("#registUserName").val();
       			var regUserCerNo = $("#registUserCerNo").val();
       			if(regUserName == $(nameHiddenDom).val() || regUserCerNo == $(idCardHiddenDom).val()){//其中一个相同则说明是一个人
       				$("#registUserName").val(name_edit);
           			$("#registUserCerNo").val(idCard_edit);
       			}
    		}
    		//保存完后让<li>选中状态改为未选中
    		$(checkedLi).attr('data-isFocus','false');
    	}
    	$(".cd-box2-partnerInfo").removeClass('is-visible');
    });
    
    // 登陆取消
   /* $(".qxbtn").click(function(){
    	$(".cd-box4loginWin").removeClass("is-visible");
    });*/
    
    //点击--保存
    var checkSaveFlg = false;
    $("#saveBtn").click(function(){
    	if(!checkSaveFlg){
    		//第一次提交
    		checkSaveFlg = true;
    	}else{
    		return false;
    	}
    	
    	var registUserCerNo = $("#registUserCerNo").val();
    	var registUserName = $("#registUserName").val();
    	if($(".add-hm ul li").length == 0 && registUserCerNo == "" && registUserName == ""){//必须填写一个信息
    		alert("必须添加至少一个股东或者添加姓名及身份证");
    		checkSaveFlg = false;
    		return false;
    	}else if($(".add-hm ul li").length > 0 && registUserCerNo == "" && registUserName == ""){//有股东信息，但没有注册人姓名及注册人身份证信息
    		$("#registUserCerNo").attr('data-validate','none');
    		$("#registUserName").attr('data-validate','none');
    	}else if(registUserCerNo != "" || registUserName != ""){//有注册人姓名或者注册人身份证信息
    		$("#registUserCerNo").attr('data-validate','required:true;identity:true;');
    		$("#registUserName").attr('data-validate','required:true;');
    	}
    	
    	//验证
    	var validateArray = new Array();
    	validateArray.push($("#hm-box"));
    	if(!JX.validateArray(validateArray)){//验证不通过
    		checkSaveFlg = false;
    		return;
    	}
    	
    	var isNew = isNewUser();
    	if(isNew == true){
    		init_cd_box4loginWin('save');
    	}else{
    		saveInfo();
    	}
    	checkSaveFlg = false;
    });
    
    //点击--提交
    var checkSubmitFlg = false;
    $("#submitBtn").unbind('click').bind('click',function(){
    	if(!checkSaveFlg){
    		//第一次提交
    		checkSubmitFlg = true;
    	}else{
    		return false;
    	}
    	
    	if($(".add-hm ul li").length == 0){//必须添加股东信息
    		alert("必须添加至少一个股东");
    		checkSubmitFlg = false;
    		return false;
    	}
    	
    	//验证
    	var validateArray = new Array();
    	validateArray.push($("#hm-box"));
    	if(!(JX.validateArray(validateArray))){
    		//验证不通过
    		checkSubmitFlg = false;
    		return;
    	}
    	var isNew = isNewUser();
    	if(isNew == true){
    		init_cd_box4loginWin('submit');
    	}else{
    		submitInfo();
    	}
    	checkSubmitFlg = false;
    });
    
    /*点击--删除（删除股东）*/
   	$("#delBtn").click(function(){
   		delPartner();
   	});
    
    
});

//登录
function myLogin(){
	var reval = false;
	var phoneNum = $("#J_mobile").val();
	var code = $("#phoneNumvali").val();
	var token = $("#token").val();
	 var valicod = $("#valicodeinput").val();
	 
	if(phoneNum == ""){
		alert("手机号码为空！！");
		return false;
	}
	if(valicod == ""){
		alert("图形验证码为空！！");
		return false;
	}
	if(token == ""){
		alert("请刷新重试！！");
		return false;
	}
	if(code == ""){
		alert("输入的验证码为空！！");
		return false;
	}
	if(code.length != 6){
		alert("输入的验证码有误！！");
		return false;
	}
	jQuery.ajax({
		url:'/wx/loginAction',
		type:"post",
		data : {"userphone":phoneNum,"usercode":code,"token":token,"validatecode":valicod,"openId":openId},
		dataType:"json",
		async : false,
		success:function(data,textStatus){
			if(data.ret == "success"){
	    		$.ajax({
	    			async : false,
	                dataType: "json", // 数据格式:JSON  
	    			url : '/weixin/addLoginUser',
	    			data : {'phoneNum':phoneNum,'authenflag':'true', 'openid': openId},
	    			type : 'POST',
	    			complete : function(){
	    	    		$(".cd-box4loginWin").removeClass("is-visible");
	    	    		reval = true;
	    	    		return true;
	    			}
	    		});
			}else if(data.ret == "404"){
				alert("404!");
			}else{
				alert(data.msg);
			}
		},
		error:function(XMLHttpRequest,textStatus,errorThrown){}
	});
	return reval;
};

//是否是新用户
function isNewUser(){
	var flag = false;
	//通过openid检测是否是新用户
	$.ajax({
		url:"/weixin/mywf/isLogin",
		type:"post",
		dataType:"json",
		async:false,
		data:{
			"openId":openId
		},
		success:function(data){
			if(data.result == false){
				//如果是新用户
				flag = true;
			}
		},
		error:function(){
			flag = false;
			alert("未知错误..");
		}
	});
	return flag;
}


/*保存*/
function saveInfo(){
	var mainBusinessCode = $("#mainBusinessCode").val();
	//发送保存信息的请求 
	LV.enterpriseRoleSave(mainBusinessCode);
	LV.enterpriseSubmit('checkNameCollect');//将流程改为下一个节点
	//**重新加载页面
	//window.location.href = "/weixin/mywf/company/detail/" + procInstId + "/" + taskId;
	//跳转到列表页
	//window.location.href = "/weixin/mywf/business/companyRegTask?openId=" + openId;
	window.location.href = "/wx/myservice/free.html?openId=" + openId;
	var shopName = $("#shopName").val();
	lv_clickLog("moudle=checkName_save&data_openId="+openId+"&data_action=click&data_shopName="+shopName+"&data_mainBusCode="+mainBusinessCode);
	return true;
}

/*提交*/
function submitInfo(){
	//保存企业和人员数据
	var mainBusinessCode = $("#mainBusinessCode").val();
	LV.enterpriseRoleSave(mainBusinessCode);
	// 注册人员
	var registUser = GOV.postRegistUser($("#registUserCerNo").val(), $("#registUserName").val());
	if(registUser == false){
		return;
	}
	$("#submitDataDiv").show();
	LV.enterpriseRoleSave(mainBusinessCode);
	LV.enterpriseSubmit('checkNameReason');//将流程改为下一个节点
	//**重新加载页面
	window.location.href = "/weixin/mywf/company/detail/" + procInstId + "/" + taskId + "?openId="+openId;
	var shopName= $("#shopName").val();
	lv_clickLog("moudle=checkName_reg&data_openId="+openId+"&data_action=click&data_shopName="+shopName+"&data_mainBusCode="+mainBusinessCode+"&data_procInstId="+procInstId+"&data_taskId="+taskId);
	return true;
}

/*删除股东*/
function delPartner(){
	if(window.confirm('你确定要删除吗？')){
		/*
		* 获取roleRelationId,如果为空则说明是新增的股东，直接移除li；
		* 否则说明是原本已有的，要删除的股东，则调用删除方法并刷新页面
		*/
		
		//选中的<li>
		var checkedLi = $(".add-hm ul li[data-isFocus=true]");
		var nameLabelDom = $(checkedLi).children('label');
		var idCardSpanDom = $(checkedLi).children('span');
		var nameHiddenDom = $(checkedLi).children('div').children('input[data-flag=nameHidden]');
		var idCardHiddenDom = $(checkedLi).children('div').children('input[data-flag=idCardHidden]');
		
		var roleRelationId = $("#partnerId").val();
		if(roleRelationId){//存在
			LV.delPartner(roleRelationId);
			document.location.reload();
		}else{
			$(checkedLi).remove();
		}
		
		$(".cd-box2-partnerInfo").removeClass('is-visible');
	}
}

function loadPartnerUL(roleType){
	//*****控制股东类型的显示
	$(".pop_box3_partnerType input.text_w").attr('type','hidden');
	var dom = ".see_" + roleType;
	$(dom).attr('type','text');
	$("#partnerTypeHidden").val(roleType);//设置股东类型隐藏域
	$("#partnerUL li:gt(0) input").attr('data-validate','none');//清空所有的验证规则
	$("#partnerUL li:gt(0)").hide();//让所有的li隐藏
	$("#partnerUL li:gt(0) input").val('');//清空值
	var dom2 = "." + roleType + "Input";
	$(dom2).show();
	if(roleType == "naturalPartner"){//自然人股东  验证规则
		$("#idCard").attr('data-validate','required:true;identity:true');
		$("#nameTxt").attr('data-validate','required:true;');
	}else if(roleType == "legalPartner"){//单位股东  验证规则
		$("#busLicenseNumTxt").attr('data-validate','required:true;');
		$("#companyNameTxt").attr('data-validate','required:true;');
	}
}

/*检测身份证是否重复*/
function isSameIdNum(idCard,data){
	for(var i=0;i<data.length;i++){
		if(idCard == data[i]){
			return true;
		}
	}
	return false;
}


function clickAddOrEditFun(){
	//open popup
	$('.add-hm ul li,.addbtn.pop_box2Partner').unbind('click').on('click', function(event){
		event.preventDefault();
		
		$(".add-hm ul li").attr('data-isFocus','false');//标记股东列表<li>全部置为未选中
		$('.cd-box2-partnerInfo').addClass('is-visible');
		
		if($(this).attr("data-isFocus") != undefined && $(this).attr("data-isFocus") != "" ){//股东信息--如果点击的是'编辑'
			//显示--删除按钮
    		$("#delBtn").show();
    		
			//改变按钮样式
    		changeStyleClass('mbtn1','mbtn');
			
    		//将股东id保存到#partnerId中
			var relationId = $(this).children('div').attr('data-relationId');
			$("#partnerId").val(relationId);
			
			$(this).attr('data-isFocus','true');//标记选择的是当前<li>
			$("#handleTypeHidden").val("edit");
			
			var spanTxt = $(this).find("span").text();
			var nameTxt = $(this).find("label").text();
			
			var roleType = $(this).children('div').attr('data-roleType');//股东类型
			loadPartnerUL(roleType);//通过股东类型加载PartnerUL标签
			
			if(roleType == "legalPartner"){//单位股东
				$("#busLicenseNumTxt").val(spanTxt);
				$("#companyNameTxt").val(nameTxt);
			}else{//自然人股东
				$("#idCard").val(spanTxt);
				$("#nameTxt").val(nameTxt);
			}
		}else{//股东信息--如果点击的是添加新股东
			//隐藏--删除按钮
    		$("#delBtn").hide();
    		
    		$("#handleTypeHidden").val("add");
    		
    		//改变按钮样式
    		changeStyleClass('mbtn','mbtn1');
    		
    		loadPartnerUL("naturalPartner");
		}
	});
}


function changeStyleClass(remClass,addClass){
	//改变按钮样式
	$("#cancelBtn").removeClass(remClass).addClass(addClass);
	$("#delBtn").removeClass(remClass).addClass(addClass);
	$("#savBtn").removeClass(remClass).addClass(addClass);
}


function init_cd_box4loginWin(handleType){
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
	$("#loginBtn").unbind("click").click(function(){
		// 登陆成功
		var isLogin = myLogin();
		if(isLogin == true){
			if(handleType == 'save'){
				saveInfo();
			}else if(handleType == 'submit'){
				submitInfo();
			}
			
		}else{
			//alert("登陆失败");
		}
	});
}


