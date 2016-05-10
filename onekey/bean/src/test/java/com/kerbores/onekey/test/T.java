package com.kerbores.onekey.test;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

/**
 * @author Kerbores(kerbores@gmail.com)
 *
 * @project bean
 *
 * @file T.java
 *
 * @description // TODO write some description of this class
 *
 * @time 2016年3月30日 下午8:05:22
 *
 */
public class T {
	public static void main(String[] args) throws Exception {

		ScriptEngineManager manager = new ScriptEngineManager();
		ScriptEngine engine = manager.getEngineByName("JavaScript");

		if (engine == null) {
			throw new RuntimeException("not found JavaScript engine!");
		}

		engine.eval("println('hello,java7!')");

	}
}
