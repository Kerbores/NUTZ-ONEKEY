var ioc = {
	config : {
		type : "org.nutz.ioc.impl.PropertiesProxy",
		fields : {
			ignoreResourceNotFound : true,
			utf8 : false,
			paths : [ 'conf', {
				sys : "hammer.config"
			}, {
				sys : "hammer.common.config"
			} ]
		}
	},
	conf : {
		type : "org.nutz.ioc.impl.PropertiesProxy",
		fields : {
			ignoreResourceNotFound : true,
			utf8 : false,
			paths : [ 'conf/redis.properties', {
				sys : "hammer.redis.config"
			} ]
		}
	}
}