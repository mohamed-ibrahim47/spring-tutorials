package com.spring.crudmvc.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.spring.crudmvc.entity.Customer;
import com.spring.crudmvc.service.CustomerService;

@Controller
@RequestMapping("/customer")
public class CustomerController {
	
	
	//@Autowired
	//private CustomerDao customerDao;
	
	@Autowired
	private CustomerService customerService;
	
	//@RequestMapping("/list")
	@GetMapping("/list")
	public String listCostomers(Model model) {
		List<Customer> customers = customerService.getCustomers();
		
		model.addAttribute("customers",customers);
		
		return "list-customers";
	}
	
	@GetMapping("/showFormForAdd") 
	public String showFormForAdd(Model model) {
		Customer customer = new Customer();
		
		model.addAttribute("customer",customer);
		
		return "customer-form";
	}
	
	@PostMapping("/saveCustomer")
	public String saveCustomer(@ModelAttribute("customer") Customer customer) {
		
		customerService.saveCustomer(customer);
		return "redirect:/customer/list";
	}
	
	@GetMapping("/showFormForUpdate") 
	public String showFormForUpdate(@RequestParam(name="customerId") int customerId,Model model) {
		Customer customer =   customerService.getCustomer(customerId);
		model.addAttribute("customer",customer);
		
		return "customer-form";
	}
	
	@GetMapping("/delete")
	public String deleteCustomer(@RequestParam(name="customerId") int customerId) {
		
		customerService.deleteCustomer(customerId);
		return "redirect:/customer/list";
	}
}
