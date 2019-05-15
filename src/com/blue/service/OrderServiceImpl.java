package com.blue.service;

import com.blue.annotation.Service;

@Service("orderService")
public class OrderServiceImpl implements OrderService{
	public String query(int id){
		return "queryID"+id;
	}
}
