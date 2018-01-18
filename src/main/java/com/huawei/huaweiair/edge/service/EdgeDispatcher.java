/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.huawei.huaweiair.edge.service;

import java.util.HashMap;
import java.util.Map;

import io.servicecomb.edge.core.AbstractEdgeDispatcher;
import io.servicecomb.edge.core.EdgeInvocation;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.CookieHandler;

public class EdgeDispatcher extends AbstractEdgeDispatcher {
	private static HashMap<String, String> microserviceNameMap = new HashMap<>();
	static {
		microserviceNameMap.put("customers", "customerServiceApp");
		microserviceNameMap.put("bookings", "bookingServiceApp");
		// microserviceNameMap.put("", "");
	}

	@Override
	public int getOrder() {
		return 1;
	}

	@Override
	public void init(Router router) {
		String regex = "/([^\\\\/]+)/rest/(.*)";
		router.routeWithRegex(regex).handler(CookieHandler.create());
		router.routeWithRegex(regex).handler(createBodyHandler());
		router.routeWithRegex(regex).failureHandler(this::onFailure).handler(this::onRequest);
	}
	protected void onRequest(RoutingContext context) {
		Map<String, String> pathParams = context.pathParams();
		String microserviceName = microserviceNameMap.get(pathParams.get("param0"));
		String path = "/"+pathParams.get("param1");
		EdgeInvocation edgeInvocation = new EdgeInvocation();
		edgeInvocation.init(microserviceName, context, path, httpServerFilters);
		edgeInvocation.edgeInvoke();
	}
}
