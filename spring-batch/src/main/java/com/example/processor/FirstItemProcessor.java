package com.example.processor;

import com.example.model.StudentJdbc;
import com.example.model.StudentJson;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

@Component
public class FirstItemProcessor implements ItemProcessor<StudentJdbc, StudentJson> {
	@Override
	public StudentJson process(StudentJdbc item) throws Exception {
		System.out.println("Inside Item Processor");
		StudentJson studentJson = new StudentJson();
		studentJson.setId(item.getId());
		studentJson.setFirstName(item.getFirstName());
		studentJson.setLastName(item.getLastName());
		studentJson.setEmail(item.getEmail());

		return studentJson;
	}

}
