package com.blue.controller;

import javax.servlet.http.HttpServletRequest;

import com.blue.annotation.AutoWare;
import com.blue.annotation.Controller;
import com.blue.annotation.RequestMapping;
import com.blue.service.OrderService;

@Controller
@RequestMapping("/test")
public class TestController {
	@AutoWare("orderService")
	private OrderService orderSerivce;
	
	@RequestMapping("/query")
	public String test(HttpServletRequest request){
		int id=Integer.parseInt(request.getParameter("id"));
		return orderSerivce.query(id);
		
	}
}
