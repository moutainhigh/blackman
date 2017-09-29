/**
 * 自定义方法
 */
var GoodsContent = {
	/**
	 * 初始化iScroll控件
	 */
	iScrollLoad:function () {
		var pullUpEl = document.getElementById('pullUp');	
		var pullUpOffset = pullUpEl.offsetHeight;
		myScroll = new iScroll('wrapper', {
			useTransition: false, /* 此属性不知用意，本人从true改为false */
			topOffset: 0,
			disableTouch:true,
			onRefresh: function () {
				if (pullUpEl.className.match('loading')) {
					pullUpEl.className = '';
					pullUpEl.querySelector('.pullUpLabel').innerHTML = '上拉加载更多...';
				}
			},
			onBeforeScrollStart: null,
			onScrollMove: function () {
				if (this.y < (this.maxScrollY -73) && !pullUpEl.className.match('flip')) {
					pullUpEl.className = 'flip';
					pullUpEl.querySelector('.pullUpLabel').innerHTML = '松手开始更新...';
					this.maxScrollY = this.maxScrollY;
				} else if (this.y > (this.maxScrollY + 73) && pullUpEl.className.match('flip')) {
					pullUpEl.className = '';
					pullUpEl.querySelector('.pullUpLabel').innerHTML = '上拉加载更多...';
					this.maxScrollY = pullUpOffset;
				}
			},
			onScrollEnd: function () {
				if (pullUpEl.className.match('flip')) {
					pullUpEl.className = 'loading';
					pullUpEl.querySelector('.pullUpLabel').innerHTML = '已加载完毕...';				
					GoodsContent.pullUpAction();
				}
			}
		});
		
		setTimeout(function () { document.getElementById('wrapper').style.left = '0'; }, 800);
	},
	/**
	 * 下拉刷新 （自定义实现此方法）
	 * myScroll.refresh();
	 */
	
	/**
	 * 滚动翻页 （自定义实现此方法）
	 * myScroll.refresh();		// 数据加载完成后，调用界面更新方法
	 */
	pullUpAction:function() {
			
		//获取页面上已经加载的数据总条数
		var dataSize = $("#wrapper-ul").children('li').length;
      	var curScopeCount = pingfenScopeCount;//当前选择的评分范围总条数
      	if(dataSize >= curScopeCount){
      		return;
      	}else{//继续从后台加载数据
			var pageIndex = dataSize%10 == 0 ? (dataSize/10+1) : Math.ceil(dataSize/10);//向上取整
            loadPageCusEvaluation(pageIndex);
      	}
      	
      	
		setTimeout(function () {myScroll.refresh();}, 1000);
	}
}
/**
 * 初始化
 */
var t1=null;//全局
$(function(){
	
	//初始化绑定iScroll控件 
	// var myScroll;
	document.getElementById("wrapper-ul").addEventListener('DOMContentLoaded', GoodsContent.iScrollLoad(), false); 
	
});

