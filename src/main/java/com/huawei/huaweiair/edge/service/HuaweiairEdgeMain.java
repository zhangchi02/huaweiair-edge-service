package com.huawei.huaweiair.edge.service;

import io.servicecomb.foundation.common.utils.BeanUtils;
import io.servicecomb.foundation.common.utils.Log4jUtils;

public class HuaweiairEdgeMain {

	public static void main(String[] args) throws Exception {
		System.setProperty("vertx.disableFileCPResolving", "false");
	    Log4jUtils.init();
	    BeanUtils.init();
	}

}
