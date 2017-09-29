function autoSendByUser(user){
	if(!!user){
		$.ajax({
			url:"/wx/sys/send",
			data:{"user":user},
			type:"post",
			dataType:"json",
			complete:function(data){
				if(data.readyState == 4 && data.status == 200){
					var responeDataJSON = $.parseJSON(data.responseText);
					if(responeDataJSON.success){
						
					}else{
						//重新发起
					}
				}
			}
		});
	}
}