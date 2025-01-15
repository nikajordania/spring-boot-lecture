package org.example.springbootlecture.repository;

import org.example.springbootlecture.entities.Person;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PersonRepository extends JpaRepository<Person, Integer> {
    List<Person> findByGroupName(String groupName);

    List<Person> findByAgeBetween(int minAge, int maxAge);

    List<Person> findByGroupNameAndAgeLessThan(String groupName, int age);
}
