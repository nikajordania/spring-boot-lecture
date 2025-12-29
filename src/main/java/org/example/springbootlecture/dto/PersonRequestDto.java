package org.example.springbootlecture.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Person request data transfer object (no id)")
public class PersonRequestDto {

    @Schema(description = "First name of the user", example = "John")
    private String name;

    @Schema(description = "Last name of the user", example = "Doe")
    private String lastname;

    @Schema(description = "Group name of the user", example = "GroupA")
    private String groupName;

    @Schema(description = "Age of the user", example = "30")
    private int age;

    public PersonRequestDto() {
    }

    public PersonRequestDto(String name, String lastname, String groupName, int age) {
        this.name = name;
        this.lastname = lastname;
        this.groupName = groupName;
        this.age = age;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }
}

