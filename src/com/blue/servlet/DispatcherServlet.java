package com.blue.servlet;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


import com.blue.annotation.AutoWare;
import com.blue.annotation.Controller;
import com.blue.annotation.RequestMapping;
import com.blue.annotation.Service;


public class DispatcherServlet extends HttpServlet {
	private List<String> allScanClassList = new ArrayList<String>();

	Map<String, Object> instanceMap = new HashMap<String, Object>();

	Map<String, Object> handleMap = new HashMap<String, Object>();

	/****
	 * tomact 启动之前所要做的工作 url-controller-处理method
	 */
	@Override
	public void init() throws ServletException {
		System.out.println("tomact启动。。。。");
		try {
			// 扫描包得到所有的
			scanPackapgeToAllClassFile("com.blue");
			// 所有的注解实例
			loadAnnotationInstace();
			// 注入
			loadIocAllFiled();

			// handleMapping
			handleMapping();

		} catch (ClassNotFoundException | InstantiationException
				| IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private void handleMapping() {
		for (Map.Entry<String, Object> entry : instanceMap.entrySet()) {
			Class<?>  clz=entry.getValue().getClass();
			if (clz
					.isAnnotationPresent(Controller.class)) {
				Controller controller = (Controller)clz.getAnnotation(Controller.class);
				String ctvalue = controller.value();
				
				if(clz.isAnnotationPresent(RequestMapping.class)){
					ctvalue=ctvalue+clz.getAnnotation(RequestMapping.class).value();
				}
				
				Method[] methods = clz.getMethods();
				for (Method method : methods) {
					if (method.isAnnotationPresent(RequestMapping.class)) {
						RequestMapping rm = (RequestMapping) method
								.getAnnotation(RequestMapping.class);
						String rmvalue = rm.value();
						handleMap.put(ctvalue +rmvalue, method);
					} else {
						continue;
					}
				}
			} else {
				continue;
			}
		}
	}

	private void loadIocAllFiled() {
		for (Map.Entry<String, Object> entry : instanceMap.entrySet()) {
			// 拿到里面的所有属性
			Field fields[] = entry.getValue().getClass().getDeclaredFields();
			for (Field field : fields) {
				field.setAccessible(true);// 可访问私有属性
				if (field.isAnnotationPresent(AutoWare.class));
				AutoWare autoWare = field.getAnnotation(AutoWare.class);
				String value = autoWare.value();
				field.setAccessible(true);
				try {
					field.set(entry.getValue(), instanceMap.get(value));
				} catch (IllegalArgumentException e) {
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					e.printStackTrace();
				}
			}
		}
	}

	private void loadAnnotationInstace() throws ClassNotFoundException,
			InstantiationException, IllegalAccessException {
		// TODO Auto-generated method stub
		for (String className : allScanClassList) {
			Class<?> cName = Class.forName(className);
			// 判断是否使用Controller注解
			if (cName.isAnnotationPresent(Controller.class)) {
				Object instance = cName.newInstance();
				Controller controller = (Controller) cName
						.getAnnotation(Controller.class);
				String key = controller.value();
				if(key == null || key.length() <= 0){
					if(cName.isAnnotationPresent(RequestMapping.class)){
						key=key+cName.getAnnotation(RequestMapping.class).value();
					}else{
						System.out.println("没有指定");
					}
					instanceMap.put(key, instance);
				}else{
					instanceMap.put(key, instance);
				}
			} else if (cName.isAnnotationPresent(Service.class)) {
				Object instance = cName.newInstance();
				Service service = (Service) cName
						.getAnnotation(Service.class);
				String key = service.value();
				instanceMap.put(key, instance);
			} else {
				continue;
			}
		}
	}

	private void scanPackapgeToAllClassFile(String path) {
		System.out.println(Test.class.getResource("/"));
		URL url = this.getClass().getClassLoader()
				.getResource(path.replaceAll("\\.", "/"));

		File file = new File(url.getFile());
		File[] files = file.listFiles();

		for (File f : files) {
			if (f.isDirectory()) {
				scanPackapgeToAllClassFile(path + "." + f.getName());
			} else {
				// 把所有的扫码的类给保存起来
				allScanClassList.add(path + "."
						+ f.getName().replace(".class", ""));
			}
		}

	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		this.doPost(req, resp);
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		PrintWriter printWriter=resp.getWriter();
		String uri=req.getRequestURI();
		String path=req.getContextPath().replace("/", "");
		uri=uri.replace("/"+path, "");
		if(!handleMap.containsKey(uri)){
			printWriter.write("404 not found url mapping"+uri);
			return;
		}
		
		String instanceIndex=uri.split("\\/")[1];
		Method method=(Method) handleMap.get(uri);
		System.out.println("method"+method);
		Object obj=instanceMap.get("/"+instanceIndex);
		
		try {
			Object result=method.invoke(obj, req);
			System.out.println(result);
			printWriter.write((String)result);
		} catch (IllegalAccessException | IllegalArgumentException
				| InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	@Override
	public void destroy() {
		// TODO Auto-generated method stub
		super.destroy();
	}

}
