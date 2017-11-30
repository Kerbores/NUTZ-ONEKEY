/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package club.zhcs.thunder.ext.shiro.aop;

import org.apache.shiro.aop.AnnotationResolver;
import org.apache.shiro.authz.aop.PermissionAnnotationMethodInterceptor;

/**
 * 
 * @author kerbores
 *
 * @email kerbores@gmail.com
 *
 */
public class SINOPermissionAnnotationMethodInterceptor extends PermissionAnnotationMethodInterceptor {

	/*
	 * The character to look for that closes a permission definition.
	 */
	// private static final char ARRAY_CLOSE_CHAR = ']';

	/**
	 * Default no-argument constructor that ensures this interceptor looks for
	 * {@link org.apache.shiro.authz.annotation.RequiresPermissions
	 * RequiresPermissions} annotations in a method declaration.
	 */
	public SINOPermissionAnnotationMethodInterceptor() {
		setHandler(new SINOPermissionAnnotationHandler());
	}

	/**
	 * @param resolver
	 * @since 1.1
	 */
	public SINOPermissionAnnotationMethodInterceptor(AnnotationResolver resolver) {
		setHandler(new SINOPermissionAnnotationHandler());
		setResolver(resolver);
	}
}
