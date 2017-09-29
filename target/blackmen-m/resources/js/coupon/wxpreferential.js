
$('.show_coupon').on('click', function(event){
		event.preventDefault();
		
		var productCode = $("#J_pcode").val();
		var productId = $("#J_pid").val();
		var cityId = $("#J_cityid").val();
		var areaId = $("#J_areaid").val();
		var openId = $("#J_openid").val();
		
		$.ajax({
			url:"/wx/preferential/getRecommend",
			data:{"productId":productId,"productCode":productCode,"cityId":cityId,"areaId":areaId,"openId":openId},
			type:"post",
			dataType:"json",
			complete:function(data){
				if (data == null)
					return;
				var result = $.parseJSON(data.responseText);
				//这里是渲染可领优惠券
				var avaList = eval(result.avaList);
				$("#available_coupons").empty();
				if (avaList != null && avaList.length != 0) {
					var html = "";
					for (var i=0;i<avaList.length ;i++) {
						html+= getCouponHtml(avaList[i],0);
					}
					$("#available_coupons").append(html);
					$(".available_title").show();
				} else {
					$(".available_title").hide();
				}
				
				//这里是渲染已领的优惠券
				var holdList = eval(result.holdList);
				$("#hold_coupons").empty();
				if (holdList != null && holdList.length != 0) {
					var html = "";
					for (var i=0;i<holdList.length ;i++) {
						html+= getCouponHtml(holdList[i],1);
					}
					$("#hold_coupons").append(html);
					
					$(".hold_tital").show();
				} else {
					$(".hold_tital").hide();
				}
				
				
				$('.cd-box3').addClass('is-visible');
				$('.body').addClass('body-no');
				
			}
		});
});


function getCouponHtml(data,typ){
	var html="";
	var liClass = typ == 0 ? "on" : "off";
	var date = "";
	if (typ == 0 && data.invalidType >= 1) {
		date = data.invalidName;
	} else {
		date = data.effectName+"~"+data.invalidName
	}
	var nameStr = data.name == "" || data.name == "''" ? "通用优惠券" : data.name;
	html+="<li class=\""+liClass+"\">";
	html+="<p class=\"name\">"+nameStr+"</p>";
	html+="<p class=\"text\">"+data.unitName+"使用</p>";
	html+="<p class=\"text\">有效期："+date+"</p>";
	if (typ == 0) {
		html+="<a data-pid =\""+data.id+"\" href=\"javascript:void(0);\" class=\"get gofetch\">领取</a>";
	}
	html+="</li>";
	return html;
}


function showTigs(content){
	$(".get-ts").html(content);
	$(".get-ts").addClass("ts-show")
    setTimeout(function(){
        $(".get-ts").removeClass("ts-show");
    },2000);
}
function checkCoupons(){
	var ava_length = $("#available_coupons").find("li").length;
	if (ava_length <= 0) {
		$(".available_title").hide();
	} else {
		$(".available_title").show();
	}
	var hold_length =  $("#hold_coupons").find("li").length;
	if (hold_length <= 0) {
		$(".hold_tital").hide();
	} else {
		$(".hold_tital").show();
	}
}

function moveCoupon(thisE){
	var parentE = thisE.parent();
	thisE.remove();
	parentE.remove();
	//换样式
	$("#hold_coupons").append(parentE);
	checkCoupons();
}

$(".gofetch").live("click",function(){
	var packetId = $(this).attr("data-pid");
	var openId = $("#J_openid").val();
	var thisE = $(this);
	$.ajax({
		url:"/wx/preferential/fetch",
		data:{"packetId":packetId,"openId":openId},
		type:"post",
		dataType:"json",
		complete:function(data){
			if(data.readyState == 4 && data.status == 200){
				var responeDataJSON = $.parseJSON(data.responseText);
				
				if (responeDataJSON.code == 1) {
					moveCoupon(thisE);
					showTigs("领取成功");
				} else if (responeDataJSON.code == 2) {
					moveCoupon(thisE);
					showTigs("领取成功");
				} else if (responeDataJSON.code == -101) {
					showTigs("该红包不存在！");
				} else if (responeDataJSON.code == -102) {
					showTigs("该红包状态错误！");
				} else if (responeDataJSON.code == -103) {
					showTigs("该红包已过期！");
				} else if (responeDataJSON.code == -104) {
					showTigs("该红包无库存！")
				} else if (responeDataJSON.code == -105) {
					showTigs("未到可领取时间!");
				} else if (responeDataJSON.code == -106) {
					showTigs("该红包未配置优惠单元！");
				} else if (responeDataJSON.code == -999) {
					initloginWin();
				} else {
					showTigs("系统异常!");
				}
				
				
			}
		}
	})
});