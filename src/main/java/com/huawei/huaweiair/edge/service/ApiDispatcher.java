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
import java.util.Set;

import javax.servlet.http.HttpServletResponse;

import org.apache.servicecomb.edge.core.AbstractEdgeDispatcher;
import org.apache.servicecomb.edge.core.EdgeInvocation;
import org.apache.servicecomb.provider.springmvc.reference.RestTemplateBuilder;
import org.apache.servicecomb.swagger.invocation.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import com.acmeair.entities.TokenInfo;
import com.acmeair.web.dto.CustomerSession;
import com.acmeair.web.dto.CustomerSessionInfo;

import io.vertx.core.buffer.Buffer;
import io.vertx.ext.web.Cookie;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.CookieHandler;

public class ApiDispatcher extends AbstractEdgeDispatcher {

	private static final Logger LOGGER = LoggerFactory.getLogger(ApiDispatcher.class);
	private static HashMap<String, String> microserviceNameMap = new HashMap<>();
	private static final String LOGIN_USER = "acmeair.login_user";
	private static final String LOGIN_PATH = "/api/login";
	private static final String LOGOUT_PATH = "/api/login/logout";
	private static final String LOADDB_PATH = "/api/loaddb";
	private static final String CONFIG_PATH = "/info/config/";
	private static final String LOADER_PATH = "/info/loader/";
	private static final String SESSIONID_COOKIE_NAME = "sessionid";
	private RestTemplate restTemplate = RestTemplateBuilder.create();
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
		String path = "/" + pathParams.get("param1");
		if (path.endsWith(LOGIN_PATH)) {
			EdgeInvocation edgeInvocation = new EdgeInvocation() {
				protected void sendResponse(Response response) {
					if (response.isSuccessed() && (response.getResult() != null)) {
						TokenInfo tokenInfo = (TokenInfo) response.getResult();
						Cookie cookie = Cookie.cookie("sessionid", tokenInfo.getSessionid());
						cookie.setPath("/");
						context.addCookie(cookie);
						context.response().headers().add("Content-Type", "application/json");
						context.response().headers().add("Content-Length", "" + "logged in".length());
						context.response().write(Buffer.buffer("logged in"));
						context.response().end();
						LOGGER.info("user login seccuessfully");
					}
				}
			};
			edgeInvocation.init(microserviceName, context, path, httpServerFilters);
			edgeInvocation.edgeInvoke();
			return;

		}

		if (path.endsWith(LOGOUT_PATH) || path.endsWith(LOADDB_PATH) || path.contains(CONFIG_PATH)
				|| path.contains(LOADER_PATH)) {

		} else {

			Set<Cookie> cookies = context.cookies();
			CustomerSession customerSession = null;
			if (null != cookies && !cookies.isEmpty()) {
				for (Cookie cookie : cookies) {
					if (cookie.getName().equals(SESSIONID_COOKIE_NAME) && null != cookie.getValue()) {
						customerSession = validateCustomerSession(cookie.getValue().trim());
						break;
					}
				}
			}

			if (null == customerSession) {
				LOGGER.info("unauthenticated user.");
				context.response().setStatusCode(HttpServletResponse.SC_FORBIDDEN);
				context.response().end();
				return;
			} else {
				context.request().headers().add(LOGIN_USER, customerSession.getCustomerid());
				LOGGER.info("Customer {} validated with session id {}", customerSession.getCustomerid(),
						customerSession.getId());
			}
		}
		EdgeInvocation edgeInvocation = new EdgeInvocation();
		edgeInvocation.init(microserviceName, context, path, httpServerFilters);
		edgeInvocation.edgeInvoke();

	}

	public CustomerSession validateCustomerSession(String sessionId) {
		if (null == sessionId || sessionId.isEmpty()) {
			return null;
		}
		ResponseEntity<CustomerSessionInfo> responseEntity = restTemplate.postForEntity(
				"cse://customerServiceApp" + "/api/login/validate", validationRequest(sessionId),
				CustomerSessionInfo.class);
		if (!responseEntity.getStatusCode().is2xxSuccessful()) {
			LOGGER.warn("No such customer found with session id {}", sessionId);
			return null;
		}
		return responseEntity.getBody();
	}

	protected Object validationRequest(String sessionId) {
		Map<String, String> map = new HashMap<>();
		map.put("sessionId", sessionId);
		return map;
	}
}
