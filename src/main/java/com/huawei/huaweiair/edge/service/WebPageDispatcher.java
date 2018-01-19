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
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.StaticHandler;

public class WebPageDispatcher extends AbstractEdgeDispatcher {

	@Override
	public int getOrder() {
		return 2;
	}

	@Override
	public void init(Router router) {
		router.route("/*").handler(StaticHandler.create("static").setDefaultContentEncoding("UTF-8"));
//		router.route("/*").failureHandler(this::onFailure);
	}

}
