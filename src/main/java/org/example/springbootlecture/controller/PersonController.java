package org.example.springbootlecture.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.example.springbootlecture.entities.Person;
import org.example.springbootlecture.repository.PersonRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/persons")
public class PersonController {

    private final PersonRepository personRepository;

    public PersonController(PersonRepository personRepository) {
        this.personRepository = personRepository;
    }

    // Any code written inside @Operation, @ApiResponses, @ApiResponse, or @Parameter annotations is used ONLY for Swagger / OpenAPI documentation.
    // These annotations help generate readable API docs but are NOT required for API to work correctly.
    @Operation(summary = "Get all users")
    @GetMapping
    public List<Person> getAllPersons() {
        return personRepository.findAll();
    }

    @Operation(summary = "Get user by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "Found the user",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = Person.class))}),
            @ApiResponse(responseCode = "404",
                    description = "User not found",
                    content = @Content)
    })
    @GetMapping("/{id}")
    public ResponseEntity<Person> getPersonById(@PathVariable int id) {
        return personRepository.findById(id)
                .map(person -> ResponseEntity.ok().body(person))
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Create a new user")
    @PostMapping
    public Person createPerson(@RequestBody Person person) {
        return personRepository.save(person);
    }

    @Operation(summary = "Update an existing user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "User updated",
                    content = {
                            @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = Person.class)
                            )
                    }
            ),
            @ApiResponse(responseCode = "404", description = "User not found", content = @Content)
    })
    @PutMapping("/{id}")
    public ResponseEntity<Person> updatePerson(@PathVariable int id, @RequestBody Person personDetails) {
        return personRepository.findById(id)
                .map(person -> {
                    person.setName(personDetails.getName());
                    person.setLastname(personDetails.getLastname());
                    person.setGroupName(personDetails.getGroupName());
                    person.setAge(personDetails.getAge());
                    Person updatedPerson = personRepository.save(person);
                    return ResponseEntity.ok().body(updatedPerson);
                }).orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Delete a user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User deleted", content = @Content),
            @ApiResponse(responseCode = "404", description = "User not found", content = @Content)
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Object> deletePerson(@PathVariable int id) {
        return personRepository.findById(id)
                .map(person -> {
                    personRepository.delete(person);
                    return ResponseEntity.ok().build();
                }).orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Get all users by group name")
    @GetMapping("/group/{groupName}")
    public List<Person> getAllPersonsByGroupName(@PathVariable String groupName) {
        return personRepository.findByGroupName(groupName);
    }

    @Operation(summary = "Get all users by query param age between defined range")
    @GetMapping("/age")
    public List<Person> getAllPersonsByAge(@RequestParam int minAge, @RequestParam int maxAge) {
        return personRepository.findByAgeBetween(minAge, maxAge);
    }

    @Operation(summary = "Get all users by group name and age less than defined value")

    @GetMapping("/group/{groupName}/age")
    public List<Person> getAllPersonsByGroupNameAndAgeLessThan(@PathVariable String groupName, @RequestParam int age) {
        return personRepository.findByGroupNameAndAgeLessThan(groupName, age);
    }

}
