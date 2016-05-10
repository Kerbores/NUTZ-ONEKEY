var ioc = {
	wxJsapiTicketStore : {
		type : "org.nutz.weixin.at.impl.MemoryJsapiTicketStore"
	},
	wxApi : {
		type : "org.nutz.weixin.impl.WxApi2Impl",
		fields : {
			token : {
				java : "$config.get('token')"
			},
			appid : {
				java : "$config.get('appid')"
			},
			appsecret : {
				java : "$config.get('appsecret')"
			},
			encodingAesKey : {
				java : "$config.get('encodingAesKey')"
			},
			jsapiTicketStore : {
				refer : 'wxJsapiTicketStore'
			}
		}
	}
}