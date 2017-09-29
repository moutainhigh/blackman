// 弹出层1
jQuery(document).ready(function($){
	
	//close popup
	$('.cd-box1').on('click', function(event){
		if( $(event.target).is('.cd-popup-close') || $(event.target).is('.pic-hide') || $(event.target).is('.cd-box1') ) {
			event.preventDefault();
			$(this).removeClass('is-visible');
		}
	});
	//close popup when clicking the esc keyboard button
	$(document).keyup(function(event){
    	if(event.which=='27'){
    		$('.cd-box1').removeClass('is-visible');
	    }
    });
});


// 弹出层2
jQuery(document).ready(function($){
	//open popup
	$('.pop_box2').on('click', function(event){
		event.preventDefault();
		$('.cd-box2').addClass('is-visible');
	});
	//close popup
	$('.cd-box2').on('click', function(event){
		if( $(event.target).is('.cd-popup-close') || $(event.target).is('.pic-hide') || $(event.target).is('.cd-box2') ) {
			event.preventDefault();
			$(this).removeClass('is-visible');
		}
	});
	//close popup when clicking the esc keyboard button
	$(document).keyup(function(event){
    	if(event.which=='27'){
    		$('.cd-box2').removeClass('is-visible');
	    }
    });
});


// 弹出层3
jQuery(document).ready(function($){
	//open popup
	$('.pop_box3').on('click', function(event){
		event.preventDefault();
		$('.cd-box3').addClass('is-visible');
	});
	//close popup
	$('.cd-box3').on('click', function(event){
		if( $(event.target).is('.cd-popup-close') || $(event.target).is('.cd-box3') ) {
			event.preventDefault();
			$(this).removeClass('is-visible');
		}
	});
	//close popup when clicking the esc keyboard button
	$(document).keyup(function(event){
    	if(event.which=='27'){
    		$('.cd-box3').removeClass('is-visible');
	    }
    });
});

// 弹出层4
jQuery(document).ready(function($){
	//open popup
	$('.pop_box4').on('click', function(event){
		event.preventDefault();
		$('.cd-box4').addClass('is-visible');
	});
	//close popup
	$('.cd-box4').on('click', function(event){
		if( $(event.target).is('.cd-popup-close') || $(event.target).is('.cd-box4') ) {
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


// 弹出层5
jQuery(document).ready(function($){
	//open popup
	$('.pop_box5').on('click', function(event){
		event.preventDefault();
		$('.cd-box5').addClass('is-visible');
	});
	//close popup
	$('.cd-box5').on('click', function(event){
		if( $(event.target).is('.cd-popup-close') || $(event.target).is('.cd-box5') ) {
			event.preventDefault();
			$(this).removeClass('is-visible');
		}
	});
	//close popup when clicking the esc keyboard button
	$(document).keyup(function(event){
    	if(event.which=='27'){
    		$('.cd-box5').removeClass('is-visible');
	    }
    });
});

// 弹出层6
jQuery(document).ready(function($){
	//open popup
	$('.pop_box6').on('click', function(event){
		event.preventDefault();
		$('.cd-box6').addClass('is-visible');
	});
	//close popup
	$('.cd-box6').on('click', function(event){
		if( $(event.target).is('.cd-popup-close') || $(event.target).is('.cd-box6') ) {
			event.preventDefault();
			$(this).removeClass('is-visible');
		}
	});
	//close popup when clicking the esc keyboard button
	$(document).keyup(function(event){
    	if(event.which=='27'){
    		$('.cd-box6').removeClass('is-visible');
	    }
    });
});

    // 其他人员担任 显示与隐藏
$(document).ready(function(){   
    $("#block_on").click(function(){
        $(".add_box").show();
    });
    $("#block_off").click(function(){
        $(".add_box").hide();
    });
});