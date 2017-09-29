$(function(){
	var LV = {
			// 初始化
			init: function(){
				LV.initFun();
			},
			initFun:function(){
				//去除字符串头部空格或指定字符
				String.prototype.trimStart = function(c){
				    if(c==null||c==""){
				        var str= this.replace('/^/s*/', '');
				        return str;
				    }else{
				        var rg=new RegExp("^"+c+"*");
				        var str= this.replace(rg, '');
				        return str;
				    }
				}

				//去除字符串尾部空格或指定字符
				String.prototype.trimEnd = function(c){
				    if(c==null||c==""){
				        var str= this;
				        var rg = /s/;
				        var i = str.length;
				        while (rg.test(str.charAt(--i)));
				        return str.slice(0, i + 1);
				    }else{
				        var str= this;
				        var rg = new RegExp(c);
				        var i = str.length;
				        while (rg.test(str.charAt(--i)));
				        return str.slice(0, i + 1);
				    }
				},
				/**
				 ** 加法函数，用来得到精确的加法结果
				 ** 说明：javascript的加法结果会有误差，在两个浮点数相加的时候会比较明显。这个函数返回较为精确的加法结果。
				 ** 返回值：
				 **/
				Number.prototype.add = function accAdd(arg) {
				    var r1, r2, m, c, arg1 = arg, arg2 = this;
				    try {
				        r1 = arg1.toString().split(".")[1].length;
				    } catch (e) {
				        r1 = 0;
				    }
				    try {
				        r2 = arg2.toString().split(".")[1].length;
				    } catch (e) {
				        r2 = 0;
				    }
				    c = Math.abs(r1 - r2);
				    m = Math.pow(10, Math.max(r1, r2));
				    if (c > 0) {
				        var cm = Math.pow(10, c);
				        if (r1 > r2) {
				            arg1 = Number(arg1.toString().replace(".", ""));
				            arg2 = Number(arg2.toString().replace(".", "")) * cm;
				        } else {
				            arg1 = Number(arg1.toString().replace(".", "")) * cm;
				            arg2 = Number(arg2.toString().replace(".", ""));
				        }
				    } else {
				        arg1 = Number(arg1.toString().replace(".", ""));
				        arg2 = Number(arg2.toString().replace(".", ""));
				    }
				    return (arg1 + arg2) / m;
				},
				/**
				 ** 减法函数，用来得到精确的减法结果
				 ** 说明：javascript的减法结果会有误差，在两个浮点数相减的时候会比较明显。这个函数返回较为精确的减法结果。
				 ** 返回值：精确结果
				 **/
				Number.prototype.sub = function (arg) {
				    var r1, r2, m, n, arg1 = arg, arg2 = this;
				    try {
				        r1 = arg1.toString().split(".")[1].length;
				    }catch (e) {
				        r1 = 0;
				    }
				    try {
				        r2 = arg2.toString().split(".")[1].length;
				    }catch (e) {
				        r2 = 0;
				    }
				    m = Math.pow(10, Math.max(r1, r2)); //last modify by deeka //动态控制精度长度
				    n = (r1 >= r2) ? r1 : r2;
				    return ((arg1 * m - arg2 * m) / m).toFixed(n);
				},
				/**
				 ** 乘法函数，用来得到精确的乘法结果
				 ** 说明：javascript的乘法结果会有误差，在两个浮点数相乘的时候会比较明显。这个函数返回较为精确的乘法结果。
				 ** 调用：accMul(arg1,arg2)
				 ** 返回值：arg1乘以 arg2的精确结果
				 **/
				Number.prototype.mul = function (arg) {
				    var m = 0, s1 = arg1.toString(), s2 = arg2.toString(), arg1 = arg , arg2 = this;
				    try {
				        m += s1.split(".")[1].length;
				    }catch (e) {
				    	
				    }
				    try {
				        m += s2.split(".")[1].length;
				    }catch (e) {
				    	
				    }
				    return Number(s1.replace(".", "")) * Number(s2.replace(".", "")) / Math.pow(10, m);
				},
				/** 
				 ** 除法函数，用来得到精确的除法结果
				 ** 说明：javascript的除法结果会有误差，在两个浮点数相除的时候会比较明显。这个函数返回较为精确的除法结果。
				 ** 调用：accDiv(arg1,arg2)
				 ** 返回值：arg1除以arg2的精确结果
				 **/
				Number.prototype.div = function (arg) {
				    var t1 = 0, t2 = 0, r1, r2, arg1 = arg , arg2 = this;
				    try {
				        t1 = arg1.toString().split(".")[1].length;
				    } catch (e) {
				    	
				    }
				    try {
				        t2 = arg2.toString().split(".")[1].length;
				    } catch (e) {
				    	
				    }
				    with (Math) {
				        r1 = Number(arg1.toString().replace(".", ""));
				        r2 = Number(arg2.toString().replace(".", ""));
				        return (r1 / r2) * pow(10, t2 - t1);
				    }
				},
				// 对Date的扩展，将 Date 转化为指定格式的String
				// 月(M)、日(d)、小时(h)、分(m)、秒(s)、季度(q) 可以用 1-2 个占位符， 
				// 年(y)可以用 1-4 个占位符，毫秒(S)只能用 1 个占位符(是 1-3 位的数字) 
				// 例子： 
				// (new Date()).Format("yyyy-MM-dd hh:mm:ss.S") ==> 2006-07-02 08:09:04.423 
				// (new Date()).Format("yyyy-M-d h:m:s.S")      ==> 2006-7-2 8:9:4.18 
				Date.prototype.Format = function (fmt) { //author: meizz 
				    var o = {
				        "M+": this.getMonth() + 1, //月份 
				        "d+": this.getDate(), //日 
				        "h+": this.getHours(), //小时 
				        "m+": this.getMinutes(), //分 
				        "s+": this.getSeconds(), //秒 
				        "q+": Math.floor((this.getMonth() + 3) / 3), //季度 
				        "S": this.getMilliseconds() //毫秒 
				    };
				    if (/(y+)/.test(fmt)) fmt = fmt.replace(RegExp.$1, (this.getFullYear() + "").substr(4 - RegExp.$1.length));
				    for (var k in o)
				    if (new RegExp("(" + k + ")").test(fmt)) fmt = fmt.replace(RegExp.$1, (RegExp.$1.length == 1) ? (o[k]) : (("00" + o[k]).substr(("" + o[k]).length)));
				    return fmt;
				}
			},

			/**
			 * 删除股东
			 */
			delPartner:function(relationId){
				if(relationId != undefined && relationId != ""){
					$.post("/weixin/mywf/business/roleRelationDel", "&relationId=" + relationId, function(data){});
				}else{
					
				}
			},
			/**
			 * 触发下一任务
			 * @param variableName
			 * @param value
			 */
			wfSignlReceiveTask:function(variableName, value){
				var param = "&procInstId=" + procInstId + "&taskId=" + taskId;
				if(variableName != undefined && variableName != "" && value != undefined && value != ""){
					param += "&variableName=" + variableName + "&value=" + value;
				}
				$.ajax({
					url:"/weixin/mywf/business/wfSignlReceiveTask",
					type:'post',    
				    data:param,
				    dataType:'json',
				    async:false,
					complete:function(data){
						
					}
				});
			},
			/**
			 * 业务信息提交
			 */
			enterpriseSubmit:function(businessKey){
				var param = "&taskId=" + taskId + "&procInstId=" + procInstId + "&openId=" + openId;
				if(businessKey == undefined || businessKey == ""){
					businessKey = nextBusinessKey;
				}
				param += "&nextBusinessKey=" + businessKey;
				$.ajax({
					url:"/weixin/mywf/business/enterpriseSubmit",
					type:'post',    
				    data:param,
				    dataType:'json',
				    async:false,
					complete:function(data){
						
					},
					success:function(data){
						
					}
				});
			},
			
			/**
			 * 业务信息保存
			 */
			enterpriseSave:function(){
				var mainData = {};
				// 核名业务
				var enterpriseData = {};
				$("[data-info='enterprise:main']").each(function(index, element){
					if($(element).parents("div:hidden").length <= 0){
						enterpriseData[$(element).attr("id")] =  $(element).val();
					}else{
//						enterpriseData[$(element).attr("id")] = "";
					}
				});
				mainData[businessKey] = enterpriseData;
				// 公司地址信息
				var addressData = {};
				$("[data-info='addressInfo:main']").each(function(index, element){
					
					addressData[$(element).attr("id")] =  $(element).val();
				});
				mainData["addressData"] = addressData;
				var param = "&enterpriseId=" + enterpriseId + "&data=" + JSON.stringify(mainData);
				$.ajax({
					url:"/weixin/mywf/business/enterpriseSave",
					type:'post',    
				    data:param,
				    dataType:'json',
				    async:false,
					complete:function(data){
					}
				});
			},
			
			enterpriseRoleSave:function(code){
				var mainData = {};
				// 核名业务
				var enterpriseData = {};
				$("[data-info='enterprise:main']").each(function(index, element){
					if($(element).parents("div:hidden").length <= 0){
						enterpriseData[$(element).attr("id")] =  $(element).val();
					}else{
//						enterpriseData[$(element).attr("id")] = "";
					}
				});
				mainData[businessKey] = enterpriseData;
				// 公司相关人员信息
				var partnerInfoArray = new Array();
				$("[data-info='partnerInfo']:visible").each(function(index, element){
					var partnerInfo = {};
					// 角色
					partnerInfo["dataRoleType"] = $(element).attr("data-roleType");
					// 主体信息
					var partnerInfoMain = {};
					$(element).find("input[data-info='partnerInfo:main']").each(function(idx, ele){
						partnerInfoMain[$(ele).attr("id")] =  $(ele).val();
					});
					partnerInfo["partnerInfoMain"] = partnerInfoMain;
					
					// 扩展信息
					var partnerInfoExt = {};
					$(element).find("input[data-info='partnerInfo:ext']").each(function(idx, ele){
						partnerInfoExt[$(ele).attr("id")] =  $(ele).val();
					});
					partnerInfo["partnerInfoExt"] = partnerInfoExt;
					
					partnerInfoArray.push(partnerInfo); 
				});
				mainData["partnerInfoArray"] = partnerInfoArray;
				var param = "&enterpriseId=" + enterpriseId + "&data=" + JSON.stringify(mainData)+"&code="+code+"&openId="+openId+"&taskId="+taskId+"&procInstId="+procInstId;
				$.ajax({
					url:"/weixin/mywf/business/enterpriseRoleSave",
					type:'post',    
				    data:param,
				    dataType:'json',
				    async:false,
					complete:function(data){
						
					},
					success:function(data){
						procInstId = data.procInstId;
						taskId = data.taskId;
						enterpriseId = data.enterpriseId;
					}
				});
			},

			/**
			 * 获取随字符串
			 * @returns {String}
			 */
			generateMixed:function(){
				var chars = ['0','1','2','3','4','5','6','7','8','9','A',
				             'B','C','D','E','F','G','H','I','J','K',
				             'L','M','N','O','P','Q','R','S','T','U','V','W','X','Y','Z'];
			     var res = "";
			     for(var i = 0; i < 10 ; i ++) {
			         var id = Math.ceil(Math.random()*35);
			         res += chars[id];
			     }
			     return res;
			}

	};
	window.location.LV =window.LV = LV;
});