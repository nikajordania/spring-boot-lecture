package org.example.springbootlecture;

import com.github.javafaker.Faker;
import org.example.springbootlecture.entities.Person;
import org.example.springbootlecture.repository.PersonRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;

@SpringBootTest
@AutoConfigureMockMvc
class SpringBootLectureApplicationTests {

	@Autowired
	private PersonRepository personRepository;

	@Test
	void generateFakeData() {
		personRepository.deleteAll();
		Faker faker = new Faker();
		List<Person> persons = new ArrayList<>();
		for (int i = 0; i < 100; i++) {
			persons.add(new Person(
					faker.name().firstName(),
					faker.name().lastName(),
					faker.options().option("Group1", "Group2", "Group3"),
					faker.number().numberBetween(1, 100)
			));
		}
		personRepository.saveAll(persons);
	}

}
