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

import io.servicecomb.edge.core.AbstractEdgeDispatcher;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.StaticHandler;

public class WebPageDispatcher extends AbstractEdgeDispatcher {
//	private Vertx vertx = VertxUtils.getOrCreateVertxByName("web-client", null);

	@Override
	public int getOrder() {
		return 9999;
	}

	@Override
	public void init(Router router) {
//		Router router2 = Router.router(vertx);
//		router.route("/").handler(routingContext -> {
//			   HttpServerResponse response = routingContext.response();
//			   response
//			       .putHeader("content-type", "text/html")
//			       .end("<h1>Hello from my first Vert.x 3 application</h1>");
//			 });

			 // Serve static resources from the /assets directory
			 // 将访问“/assets/*”的请求route到“assets”目录下的资源
			 
			 router.route("/static/*").handler(StaticHandler.create("static"));

//		router.routeWithRegex("/static/*").handler(StaticHandler.create("static")).failureHandler(this::onFailure);
	}

	/*protected void onRequest(RoutingContext context) {
		onRequest(context);
		context.next();
	}*/
}
