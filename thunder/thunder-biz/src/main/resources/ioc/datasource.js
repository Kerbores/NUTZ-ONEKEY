var ioc = {
	db : {
		type : "org.nutz.ioc.impl.PropertiesProxy",
		fields : {
			ignoreResourceNotFound : true,
			paths : [ 'datasource', '/var/datasource', 'C:/datasource' ],
			utf8 : false
		}
	},
	dataSource : {
		type : "com.alibaba.druid.pool.DruidDataSource",
		events : {
			depose : "close",
			init : "init"
		},
		fields : {
			url : {
				java : "$db.get('db-url')"
			},
			username : {
				java : "$db.get('db-user')"
			},
			password : {
				java : "$db.get('db-pwd')"
			},
			maxActive : {
				java : "$db.get('db-pool-max')"
			},
			initialSize : {
				java : "$db.get('db-pool-init')"
			},
			maxWait : {
				java : "$db.get('db-pool-wait')"
			},
			minIdle : {
				java : "$db.get('db-pool-min')"
			},
			timeBetweenEvictionRunsMillis : {
				java : "$db.get('db-time-betw')"
			},
			minEvictableIdleTimeMillis : {
				java : "$db.get('db-time-met')"
			},
			validationQuery : {
				java : "$db.get('db-query-val')"
			},
			testWhileIdle : {
				java : "$db.get('db-test-idle')"
			},
			testOnBorrow : {
				java : "$db.get('db-test-borr')"
			},
			testOnReturn : {
				java : "$db.get('db-test-return')"
			},
			filters : {
				java : "$db.get('db-filters')"
			},
			connectionProperties : {
				java : "$db.get('connectionProperties')"
			}
		}
	},
	sqlManeger : {
		type : "org.nutz.dao.impl.FileSqlManager",
		args : [ "sqls" ]
	},
	dao : {
		type : "org.nutz.dao.impl.NutDao",
		args : [ {
			refer : "dataSource"
		}, {
			refer : "sqlManeger"
		} ],
		fields : {
			interceptors : [ {
				refer : "cacheExecutor"
			}, "log" ]
		}
	},
	cacheExecutor : {
		type : "org.nutz.plugins.cache.dao.CachedNutDaoExecutor",
		fields : {
			cacheProvider : {
				refer : "cacheProvider"
			},
			cachedTableNames : [ "t_user", "t_role_permissione" ], // 需要缓存的表
			enableWhenTrans : true, // 事务作用域内是否启用,默认false
			cache4Null : true
		}
	},
	cacheProvider : {
		type : "org.nutz.plugins.cache.dao.impl.provider.EhcacheDaoCacheProvider",
		fields : {
			cacheManager : {
				refer : "cacheManager"
			}
		},
		events : {
			create : "init"
		}
	}
}