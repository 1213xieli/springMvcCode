package com.blue.servlet;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class Test {
	private List<String> allScanClassList = new ArrayList<String>();
	private void scanPackapgeToAllClassFile(String path) {
		System.out.println(Test.class.getResource("/"));
		URL url = this.getClass().getClassLoader()
				.getResource(path.replaceAll("\\.", "/"));
		System.out.println(url);
		File file = new File(url.getFile());

		File[] files = file.listFiles();
		for (File f : files) {
			if (f.isDirectory()) {
				scanPackapgeToAllClassFile(path+"."+f.getName());
			} else {
				// 把所有的扫码的类给保存起来
				allScanClassList.add(path + "." + f.getName().replace(".class", ""));
				System.out.println(path+"."+f.getName());
			}
		}

	}
	public static void main(String[] args) {
		Test t=new Test();
		t.scanPackapgeToAllClassFile("com.blue");
	}
}
