$(document).ready(function(){
	var JX = JX;
	JX = {
			// 校验，多个
			validateArray: function(validateArray){
				var validateFlag = true;
				if(validateArray != undefined){
					for(var i=0;i<validateArray.length;i++){
						if(JX.validate(validateArray[i])){
							continue;
						}else{
							validateFlag = false;
							break;
						}
					}
				}
				return validateFlag;
			},
			// 校验，单个
			validate: function(validateObj){
				if(validateObj == undefined){
					return true;
				}
				var validateFlag = true;
				$(validateObj).find("[data-validate]" ).each(function(index,element){
					var dataValidate = $(element).attr("data-validate");
					if(dataValidate == "none"){
						// 忽略，不校验
						return validateFlag;
					}
					var key = $(element).attr("placeholder");
					var val = $(element).val()
					if(val != undefined ){
						val = val.replace(/\s/g,"");
						$(element).val(val);
					}
					if(dataValidate != undefined && dataValidate != ""){
						var dataValidateArray = dataValidate.split(";");
						for(var n=0;n<dataValidateArray.length;n++){
							var dataValidate = dataValidateArray[n];
							if(dataValidate == undefined || dataValidate == ""){
								continue;
							}
							var dataValidateMap = dataValidate.split(":");
							if(dataValidateMap == undefined || dataValidateMap.length != 2){
								continue;
							}
							var dataValidateKey = dataValidateMap[0];
							var dataValidateValue = dataValidateMap[1];
							
							// 非空
							if(dataValidateKey == "required"){
								if(dataValidateValue == "true" || dataValidateValue == true){
									if(val == ""){
										alert(key + "不能为空。");
										validateFlag = false;
										return validateFlag;
									}
								}
							}
							
							// 数字
							if(dataValidateKey == "number" && val != ""){
								if(dataValidateValue == "true" || dataValidateValue == true){
									// 数字校验
									var reg = /^\d+(\.\d+)?$/;
									if(!reg.test(val)){
										alert(key + " 请输入数字");
										validateFlag = false;
										return validateFlag;
									}
								}
							}
							// 整数
							if(dataValidateKey == "int" && val != ""){
								if(dataValidateValue == "true" || dataValidateValue == true){
									// 数字校验
									var reg = /^[1-9]*[1-9][0-9]*$/;
									if(!reg.test(val)){
										alert(key + " 请输入整数");
										validateFlag = false;
										return validateFlag;
									}
								}
							}
							// 最少
							if(dataValidateKey == "min" && val != ""){
								if(val == undefined || val.length < dataValidateValue ){
									alert(key + " 至少" + dataValidateValue + "个字" );
									validateFlag = false;
									return validateFlag;
								}
							}
							// 手机格式
							if(dataValidateKey == "mobilePhone" && val != ""){
								if(dataValidateValue == "true" || dataValidateValue == true){
									// 手机号码校验
									var myreg = /^((1[0-9]{2})+\d{8})$/; 
									if(!myreg.test(val)){
										alert(key + " 格式错误");
										validateFlag = false;
										return validateFlag;
									}
								}
							}
							
							// 邮政编码
							if(dataValidateKey == "postCode" && val != ""){
								if(dataValidateValue == "true" || dataValidateValue == true){
									// 手机号码校验
									var myreg = /^[1-9][0-9]{5}$/; 
									if(!myreg.test(val)){
										alert(key + " 格式错误");
										validateFlag = false;
										return validateFlag;
									}
								}
							}
							
							// 座机电话
							if(dataValidateKey == "landlinePhone" && val != ""){
								if(dataValidateValue == "true" || dataValidateValue == true){
									// 固定电话
									var myreg = /^0\d{2,3}-?\d{7,8}$/;
									if(!myreg.test(val)){
										alert(key + " 格式错误");
										validateFlag = false;
										return validateFlag;
									}
								}
							}
							
							// 电话
							if(dataValidateKey == "telPhone" && val != ""){
								if(dataValidateValue == "true" || dataValidateValue == true){
									// 固定电话 或者 手机号码
									var landreg = /^0\d{2,3}-?\d{7,8}$/; 
									// 手机号码校验
									var mobilereg = /^((1[0-9]{2})+\d{8})$/; 
									if(!landreg.test(val) && !mobilereg.test(val)){
										alert(key + " 格式错误");
										validateFlag = false;
										return validateFlag;
									}
								}
							}
							
							// 身份证格式
							if(dataValidateKey == "identity" && val != ""){
								if(dataValidateValue == "true" || dataValidateValue == true){
									// 身份证校验
									if(!JX.identityCodeValid(val)){
										validateFlag = false;
										return validateFlag;
									}
								}
							}
							// 汉字
							if(dataValidateKey == "chinese" && val != ""){
								if(dataValidateValue == "true" || dataValidateValue == true){
									// 汉字校验
									var reg = /^[\u4e00-\u9fa5]+$/;
									if(!reg.test(val)){
										alert(val + "，必须全部为汉字");
										validateFlag = false;
										return validateFlag;
									}
								}
							}
							// 中文字符
							if(dataValidateKey == "chineseCode" && val != ""){
								if(dataValidateValue == "true" || dataValidateValue == true){
									// 汉字校验
									var reg = /[^\u00-\uFF]/;
									if(!reg.test(val)){
										alert(val + "，必须全部为中文字符");
										validateFlag = false;
										return validateFlag;
									}
								}
							}
							// 电子邮箱
							if(dataValidateKey == "email" && val != ""){
								if(dataValidateValue == "true" || dataValidateValue == true){
									// 汉字校验
									var reg = /^(\w-*\.*)+@(\w-?)+(\.\w{2,})+$/;
									if(!reg.test(val)){
										alert(val + "，邮箱格式错误");
										validateFlag = false;
										return validateFlag;
									}
								}
							}
							
						}
					}
				});
				return validateFlag;
			},
			identityCodeValid:function(code){
				var city={11:"北京",12:"天津",13:"河北",14:"山西",
						15:"内蒙古",21:"辽宁",22:"吉林",23:"黑龙江 ",
						31:"上海",32:"江苏",33:"浙江",34:"安徽",
						35:"福建",36:"江西",37:"山东",41:"河南",
						42:"湖北 ",43:"湖南",44:"广东",45:"广西",
						46:"海南",50:"重庆",51:"四川",52:"贵州",
						53:"云南",54:"西藏 ",61:"陕西",62:"甘肃",
						63:"青海",64:"宁夏",65:"新疆",71:"台湾",
						81:"香港",82:"澳门",91:"国外 "};
				var tip = "";
				var pass= true;
				if(!code || !/^\d{6}(14|15|16|17|18|19|20)?\d{2}(0[0-9]|1[0-9]|2[12])(0[0-9]|[12]\d|3[01])\d{3}(\d|X)$/i.test(code)){
	                tip = "身份证号码：" + code + "，格式错误";
	                pass = false;
	            } else if(!city[code.substr(0,2)]){
	                tip = "身份证号码：" + code + "，地址编码错误";
	                pass = false;
	            }else{
	                //18位身份证需要验证最后一位校验位
	                if(code.length == 18){
	                    var codeArray = code.split('');
	                    //∑(ai×Wi)(mod 11)
	                    //加权因子
	                    var factor = [ 7, 9, 10, 5, 8, 4, 2, 1, 6, 3, 7, 9, 10, 5, 8, 4, 2 ];
	                    //校验位
	                    var parity = [ 1, 0, 'X', 9, 8, 7, 6, 5, 4, 3, 2 ];
	                    var sum = 0;
	                    var ai = 0;
	                    var wi = 0;
	                    for (var i = 0; i < 17; i++){
	                        ai = codeArray[i];
	                        wi = factor[i];
	                        sum += ai * wi;
	                    }
	                    var last = parity[sum % 11];
	                    if(parity[sum % 11] != codeArray[17]){
	                        tip = "身份证号码：" + code + "，校验位错误";
	                        pass =false;
	                    }
	                }
	            }
	            if(!pass) alert(tip);
	            return pass;
	        }

	};
	window.location.JX =window.JX = JX;
});