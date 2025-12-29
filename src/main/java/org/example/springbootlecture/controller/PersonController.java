package org.example.springbootlecture.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.example.springbootlecture.dto.PersonDto;
import org.example.springbootlecture.mapper.PersonMapper;
import org.example.springbootlecture.dto.PersonRequestDto;
import org.example.springbootlecture.entities.Person;
import org.example.springbootlecture.repository.PersonRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/persons")
public class PersonController {

    private final PersonRepository personRepository;

    public PersonController(PersonRepository personRepository) {
        this.personRepository = personRepository;
    }

    @Operation(summary = "Get all users")
    @GetMapping
    public List<PersonDto> getAllPersons() {
//        List<Person> persons = personRepository.findAll();
//        List<PersonDto> result = new ArrayList<>();
//        for (Person p : persons) {
//            result.add(PersonMapper.toDto(p));
//        }
//        return result;

        return personRepository.findAll().stream()
                .map(PersonMapper::toDto)
                .collect(Collectors.toList());
    }

    @Operation(summary = "Get user by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "Found the user",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = PersonDto.class))}),
            @ApiResponse(responseCode = "404",
                    description = "User not found",
                    content = @Content)
    })
    @GetMapping("/{id}")
    public ResponseEntity<PersonDto> getPersonById(@Parameter(description = "ID of the user to be searched")
                                                   @PathVariable int id) {
        return personRepository.findById(id)
                .map(person -> ResponseEntity.ok().body(PersonMapper.toDto(person)))
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Create a new user")
    @PostMapping
    public PersonDto createPerson(@RequestBody PersonRequestDto personRequestDto) {
        Person person = PersonMapper.toEntity(personRequestDto);
        Person saved = personRepository.save(person);
        return PersonMapper.toDto(saved);
    }

    @Operation(summary = "Update an existing user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "User updated",
                    content = {
                            @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = PersonDto.class)
                            )
                    }
            ),
            @ApiResponse(responseCode = "404", description = "User not found", content = @Content)
    })
    @PutMapping("/{id}")
    public ResponseEntity<PersonDto> updatePerson(
            @Parameter(description = "ID of the user to be updated")
            @PathVariable int id,
            @RequestBody PersonRequestDto personDetails) {
        return personRepository.findById(id)
                .map(person -> {
                    PersonMapper.updateEntityFromDto(person, personDetails);
                    Person updatedPerson = personRepository.save(person);
                    return ResponseEntity.ok().body(PersonMapper.toDto(updatedPerson));
                }).orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Delete a user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User deleted", content = @Content),
            @ApiResponse(responseCode = "404", description = "User not found", content = @Content)
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Object> deletePerson(@Parameter(description = "ID of the user to be deleted")
                                               @PathVariable int id) {
        return personRepository.findById(id)
                .map(person -> {
                    personRepository.delete(person);
                    return ResponseEntity.ok().build();
                }).orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Get all users by group name")
    @GetMapping("/group/{groupName}")
    public List<PersonDto> getAllPersonsByGroupName(@Parameter(description = "Group name of the user to be searched")
                                                    @PathVariable String groupName) {
        return personRepository.findByGroupName(groupName).stream()
                .map(PersonMapper::toDto)
                .collect(Collectors.toList());
    }


    @Operation(summary = "Get all users by query param age between defined range")
    @GetMapping("/age")
    public List<PersonDto> getAllPersonsByAge(@Parameter(description = "Minimum age of the user to be searched")
                                              @RequestParam int minAge,
                                              @Parameter(description = "Maximum age of the user to be searched")
                                              @RequestParam int maxAge) {
        return personRepository.findByAgeBetween(minAge, maxAge).stream()
                .map(PersonMapper::toDto)
                .collect(Collectors.toList());
    }

    @Operation(summary = "Get all users by group name and age less than defined value")

    @GetMapping("/group/{groupName}/age")
    public List<PersonDto> getAllPersonsByGroupNameAndAgeLessThan(@Parameter(description = "Group name of the user to be searched")
                                                                  @PathVariable String groupName,
                                                                  @Parameter(description = "Maximum age of the user to be searched")
                                                                  @RequestParam int age) {
        return personRepository.findByGroupNameAndAgeLessThan(groupName, age).stream()
                .map(PersonMapper::toDto)
                .collect(Collectors.toList());
    }

}
