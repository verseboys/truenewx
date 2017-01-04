(function($) {

	var UnstructuredUpload = function(element, options) {
		this.init(element, options);
	};

	var rpc = $.tnx.rpc.imports("unstructuredAuthorizeController");

	var authorizeTypes = ["DOCTOR_HEAD_IMAGE", "DOCTOR_WORK_CARD"];

	var defaultOptions = {
		authorizeType: null,// 授权类型
		maxCapacity: null,// 最大容量,以byte为单位
		progress: function(p) {}//上传进度回调
	};

	UnstructuredUpload.prototype = {
		init: function(element, options) {
			this.element = element;
			this.setOptions(options);
		},
		setOptions: function(options) {
			defaultOptions = $.extend(defaultOptions, options);
		}
	};

	var validationAuthorizeType = function(type) {
		if (type == "") {
			throw "UnstructuredAuthorizeType is null";
		}
		if (authorizeTypes.indexOf(type) < 0) {
			throw "UnstructuredAuthorizeType does not exist";
		}
	};

	var getSuffix = function(fileName) {
		var result = /\.[^\.]+/.exec(fileName);
		return result;
	}

	var bytesToSize = function(bytes) {
		if (bytes === 0) return '0 B';
		var k = 1024,
		sizes = ['B', 'KB', 'MB', 'GB', 'TB', 'PB', 'EB', 'ZB', 'YB'],
		i = Math.floor(Math.log(bytes) / Math.log(k));
		return (bytes / Math.pow(k, i)).toPrecision(3) + ' ' + sizes[i];
	}

	var methods = {
		init: function(option) {
			var args = arguments,
			result = null;
			$(this).each(function(index, item) {
				var data = $.data(document, $(item).attr("id")),
				options = (typeof option !== 'object') ? null: option;
				if (!data) {
					data = new UnstructuredUpload(item, options);
					$.data(document, $(item).attr("id"), data);
					result = $.extend({
						"element": data.element
					},
					methods);
					return false;
				}
				if (typeof option === 'string') {
					if (data[option]) {
						result = data[option].apply(data, Array.prototype.slice.call(args, 1));
					} else {
						throw "Method " + option + " does not exist";
					}
				} else {
					result = data.setOptions(option);
				}
			});
			return result;
		},
		upload: function(filename, callback) {
			// 判断浏览器是否支持文件 api
			if (! (window.File || window.FileReader || window.FileList || window.Blob)) {
				throw "Browser nonsupport file api";
			}
			var type = defaultOptions.authorizeType,
			maxCapacity = defaultOptions.maxCapacity;
			if (!type || type == "" || type == null) {
				throw "Please set the authorization type";
			}

			var $el = !this.element ? $(this) : $(this.element);
			var files = $el.prop("files"); // 得到文件
			if (files.length == 0) {
				throw "Please select the uploaded file";
			}
			var file = files[0];
			if (maxCapacity != null && maxCapacity < file.size) {
				var size = bytesToSize(maxCapacity);
				$.tnx.alert("文件最大不能超过" + size, "上传提示");
				return;
			}

			filename = !filename || filename == "" || filename == null ? file.name: filename
			var suffix = getSuffix(filename) if (suffix == "" || suffix == null) {
				suffix = getSuffix(file.name);
				filename = filename + suffix;
			}

			validationAuthorizeType(type); // 校验授权类型是否正确
			var token = rpc.authorizePrivateWrite(type); // 请求授权
			if (!token) {
				throw "Request authorization failed";
			}
			var client = new OSS.Wrapper({
				region: token.region,
				accessKeyId: token.accessId,
				accessKeySecret: token.accessSecret,
				bucket: token.bucket
			});
			var storeAs = token.path + filename;
			client.multipartUpload(storeAs, file, defaultOptions.progress).then(function(res) {
				if (token.publicReadable) {
					client.putACL(storeAs, 'public-read').then(function(result) {
						if (callback && typeof callback == "function") {
							var innerUrl = token.innerUrl + filename
							var protocol = window.location.protocol.replace(":");
							var outerUrl = rpc.getOuterUrl(type, innerUrl, protocol);
							var result = {
								"innerUrl": innerUrl,
								"outerUrl": outerUrl
							};
							callback(result);
						}
					});
				}
			}); // 上传文件
		}
	};

	$.fn.unstructuredUpload = function(method) {
		if (methods[method]) {
			return methods[method].apply(this, Array.prototype.slice.call(arguments, 1));
		} else if (typeof method === 'object' || !method) {
			return methods.init.apply(this, arguments);
		} else {
			return $.error("Method " + method + " does not exist on plug-in: unstructured-upload");
		}
	};
})(jQuery);