var ioc = {
	log : {
		type : "org.nutz.aop.interceptor.LoggingMethodInterceptor"
	},
	$aop : {
		type : "org.nutz.ioc.aop.config.impl.ComboAopConfigration",
		fields : {
			aopConfigrations : [ {
				type : "org.nutz.ioc.aop.config.impl.JsonAopConfigration",
				fields : {
					itemList : [ [ "club\\.zhcs\\..+", ".+", "ioc:log" ], // 日志
					[ "club\\.zhcs\\.hammer\\.module\\..+", "^(?!.*(_)).*$", "ioc:apm" ],// apm
					[ "club\\.zhcs\\.hammer\\.module\\..+", "^(?!.*(_)).*$", "ioc:uaDebug" ],// ua
					[ "club\\.zhcs\\.hammer\\.module\\..+", "^(?!.*(_)).*$", "ioc:aopTimer" ],// 模块执行时间
					[ "club\\.zhcs\\.hammer\\.module\\..+", "^(?!.*(_)).*$", "ioc:txSERIALIZABLE" ] // 模块事务处理
					],
				}
			}, {
				type : "org.nutz.ioc.aop.config.impl.AnnotationAopConfigration"
			} ]
		}
	}
};