$(function() {
	console.log("common.js is runnning...");
	// 收回展开效果
	$('.dropdown-toggle').on('click', function() {
		if ($(this).find('.fa-angle-down').length) {
			$(this).find('.fa-angle-down').removeClass('fa-angle-down').addClass('fa-angle-up');
		} else if ($(this).find('.fa-angle-up').length) {
			$(this).find('.fa-angle-up').removeClass('fa-angle-up').addClass('fa-angle-down');
		}
	})

	// 检索按钮的实现
	$('.search-btn').on('click', function() {
		if ($('.search-form').find('input').validation(function(status, dom, errorMsg, defaultValue) {// 此处覆盖一下默认的回调,让tips框框到后面的搜索按钮上面去
			status ? $.noop() : function() {
				validationFail(errorMsg, $(dom).next()[0]);
			}.call();
		})) {
			$('.search-form').submit();
		}
	});
	// 添加弹窗按钮的功能通用实现
	$('.btn-pop').on('click', function() {
		layer.open({
			type : 2,
			title : $(this).data('title'),
			shadeClose : false,
			closeBtn : false,
			shade : 0.8,
			area : [ $(this).data('width') + 'px', $(this).data('height') + 'px' ],
			content : getRootPath() + $(this).data('url')
		});
	});
	// 返回按钮
	$('.btn-back').on('click', function() {
		history.go(-1);
	});
	// 删除按钮功能
	$('.btn-delete').on('click', function() {
		var url = $(this).data('url');
		var id = $(this).data('id');
		layer.confirm('确认删除这条数据 ?', {
			icon : 3,
			title : '删除提示'
		}, function(index) {
			$.post(getRootPath() + url, {
				id : id
			}, function(result) {
				layer.close(index);// 关闭弹窗
				if (result.operationState == 'SUCCESS') {
					refresh();// 刷新页面
				} else {
					showMessage(result.data.reason);
				}
			}, 'json');

		});
	});
	// 弹窗返回按钮
	$('.btn-dialog-undo').on('click', function() {
		closePopWindow();
	});
	// 禁用分页条的disabled节点
	$('.pagination .disabled a').on('click', function() {
		return false;
	});
	// 如果存在chosen效果做适配
	if ($(".chosen-select").length) {
		$(".chosen-select").chosen();
		$('.chosen-container').attr('style', 'width:355px').each(function() {
			$(this).find('a:first-child').css('width', '350px');
			$(this).find('.chosen-drop').css('width', '350px');
			$(this).find('.chosen-search input').css('width', '350px').css('height', '32px');
		});
		$('.chosen-single ').attr('style', 'height:32px; border-radius:0').find('span').css('line-height', '32px');
		$('.active-result').css('line-height', '32px');
	}
	// pop-5
	if ($('.pop-5').length) {
		$('.pop-5')
				.popover(
						{
							trigger : 'hover',
							template : '<div class="popover" role="tooltip"><div class="arrow"></div><h3 class="popover-title" style="font-weight:600;font-size:14px"></h3><div class="popover-content" style="max-width:200px;word-wrap: break-word;"></div></div>'
						});
		$('.pop-5').on('click', function() {
			if (window.clipboardData)
				window.clipboardData.setData("Text", $(this).text());
		})
	}
})

/**
 * 通用JavaScript方法
 */

/**
 * 闭包处理页面加载效果
 */
var url = location.href;

function getRootPath() {
	return contextPath;
}
/**
 * 关闭弹窗
 */
function closePopWindow() {
	var api = frameElement.api;
	api.close();
}
/**
 * 刷新窗口
 */
function refreshWindow() {
	var api = frameElement.api, W = api.opener;
	W.location.reload();
}
/**
 * 刷新地址栏
 */
function refresh() {
	location.reload();
}
/**
 * 确认删除
 * 
 * @param msg
 *            确认消息
 */
function confirmDelete(msg, callback) {
	layer.confirm(msg, {
		icon : 3,
		title : '操作确认'
	}, function(index) {
		callback.call();
		layer.close(index);
	});
}

/**
 * 显示提示信息
 * 
 * @param info
 *            提示信息
 */
function showMessage(info) {
	layer.msg(info);
}

function validationFail(msg, dom) {
	layer.tips(msg, dom, {
		tips : 1
	});
}

/**
 * 日期计算
 * 
 * @param days
 *            减去的天数
 * @returns {Date} 目标日期
 */
function cutDay(days) {
	var date = new Date();
	return new Date(date.getTime() - days * 1000 * 24 * 60 * 60);
}

/**
 * 日期格式
 * 
 * @param date
 *            日期
 * @param env
 *            格式串
 * @returns {String}
 */
function formatDate(date, env) {
	var year = date.getFullYear();
	var month = date.getMonth() + 1;
	var day = date.getDate();
	switch (env) {
	case 1:
		return year + '-' + (month < 10 ? "0" + month : month) + '-' + (day < 10 ? "0" + day : day);
	case 2:
		return year + '-' + (month < 10 ? "0" + month : month);
	case 3:
		return year + '';
	}
}
/**
 * 弹窗指定URL
 * 
 * @param url
 *            待弹出页面
 * @param title
 *            页面标题
 * @param width
 *            快读
 * @param height
 *            高度
 * @returns 弹窗对象,可以继续调用dialog api
 */
function openUrl(url, title, width, height, closeBtn) {
	layer.open({
		type : 2,
		shadeClose : true,
		title : title,
		closeBtn : closeBtn,
		area : [ width + 'px', height + 'px' ], // 宽高
		content : contextPath + url
	});
}

function commonAjaxCallBasck(result) {
	if (result.operationState == "SUCCESS") {
		refreshWindow();
		closePopWindow();
	} else {
		showMessage(result.data.reason);
	}
}

Date.prototype.format = function(format) {
	var date = {
		"M+" : this.getMonth() + 1,
		"d+" : this.getDate(),
		"h+" : this.getHours(),
		"m+" : this.getMinutes(),
		"s+" : this.getSeconds(),
		"q+" : Math.floor((this.getMonth() + 3) / 3),
		"S+" : this.getMilliseconds()
	};
	if (/(y+)/i.test(format)) {
		format = format.replace(RegExp.$1, (this.getFullYear() + '').substr(4 - RegExp.$1.length));
	}
	for ( var k in date) {
		if (new RegExp("(" + k + ")").test(format)) {
			format = format.replace(RegExp.$1, RegExp.$1.length == 1 ? date[k] : ("00" + date[k]).substr(("" + date[k]).length));
		}
	}
	return format;
}

function logOut() {
	$.post(getRootPath() + '/system/logout', {}, function(result) {
		if (result.operationState == 'SUCCESS') {
			if (!getRootPath()) {
				location.href = "/";
			} else {
				location.href = getRootPath();
			}
		}
	}, 'json');
}
/**
 * 检查用户名
 * 
 * @param userName
 * @returns
 */
function checkUserName(userName) {
	if (userName == null || userName == "") {
		showMessage("请输入用户名");
		return false;
	}
	var r;
	$.ajax({
		url : getRootPath() + "/common/checkUserName",
		async : false,
		type : "POST",
		dataType : "json",
		data : {
			userName : userName
		},
		success : function(result) {
			if (result.operationState == "SUCCESS") {
				r = true;
			} else {
				showMessage(result.data.reason);
				r = false;
			}
		}
	});
	return r;
}

function baiduMap(callback) {
	layer.open({
		type : 2,
		title : '地点选择器',
		fix : false,
		closeBtn : false,
		move : false,
		area : [ '800px', '600px' ],
		content : getRootPath() + '/map/show',
		btn : [ '确认', '取消' ],
		yes : function(index, layero) {
			var longitude = layer.getChildFrame('#longitude', index).val();
			var latitude = layer.getChildFrame('#latitude', index).val();
			var address = layer.getChildFrame('#address', index).val();
			if (!longitude || !latitude || !address) {
				var iframeWin = window[layero.find('iframe')[0]['name']];
				iframeWin.showMsg();
			} else {
				callback(longitude, latitude, address);
				layer.close(index);
			}
		},
		cancel : function(index) {
			layer.close(index);
		}
	});
}

function merchantsPicker(callback) {
	layer.open({
		type : 2,
		title : '商家树形选择器',
		fix : false,
		closeBtn : false,
		move : false,
		area : [ '800px', '600px' ],
		content : getRootPath() + '/merchants/tree',
		btn : [ '确认', '取消' ],
		yes : function(index, layero) {
			var option = JSON.parse(layer.getChildFrame('#data', index).val());
			if (!!option) {
				callback(option);
				layer.close(index);
			} else {
				var iframeWin = window[layero.find('iframe')[0]['name']];
				iframeWin.showMsg();
			}
		},
		cancel : function(index) {
			layer.close(index);
		}
	});
}

$(window).ajaxError(function() {
	layer.msg('服务器端数据错误!');
})

// 打开授权窗口
function openGrant(id) {
	var dialog = openUrl("/common/grant/" + id, "授权", 800, 650);
}

// 打开设置角色窗口
function openSetRole(id) {
	var dialog = openUrl("/common/setRole/" + id, "设置角色", 800, 650);
}

function openClock() {
	openUrl('/clock.html', "canvas时钟", 525, 525);
}