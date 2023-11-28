package com.spring.crudmvc.service;

import java.util.List;

import com.spring.crudmvc.entity.Customer;

public interface CustomerService {

	public List<Customer> getCustomers();

	public void saveCustomer(Customer customer);

	public Customer getCustomer(int customerId);

	public void deleteCustomer(int customerId);

	
}

