package org.example.springbootlecture.mapper;

import org.example.springbootlecture.dto.PersonDto;
import org.example.springbootlecture.dto.PersonRequestDto;
import org.example.springbootlecture.entities.Person;

public class PersonMapper {

    public static PersonDto toDto(Person person) {
        if (person == null) return null;
        return new PersonDto(person.getId(), person.getName(), person.getLastname(), person.getGroupName(), person.getAge());
    }

    public static Person toEntity(PersonRequestDto requestDto) {
        if (requestDto == null) return null;
        return new Person(requestDto.getName(), requestDto.getLastname(), requestDto.getGroupName(), requestDto.getAge());
    }

    public static void updateEntityFromDto(Person person, PersonRequestDto requestDto) {
        if (person == null || requestDto == null) return;
        person.setName(requestDto.getName());
        person.setLastname(requestDto.getLastname());
        person.setGroupName(requestDto.getGroupName());
        person.setAge(requestDto.getAge());
    }
}

