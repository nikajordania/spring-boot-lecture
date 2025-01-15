package org.example.springbootlecture.entities;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
@Schema(description = "User model")
public class Person {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "Unique identifier of the user", example = "1", accessMode = Schema.AccessMode.READ_ONLY)
    private int id;

    @Schema(description = "First name of the user", example = "John")
    private String name;

    @Schema(description = "Last name of the user", example = "Doe")
    private String lastname;

    @Schema(description = "Group name of the user", example = "GroupA")
    private String groupName;

    @Schema(description = "Age of the user", example = "30")
    private int age;

    public Person() {
    }

    public Person(String name, String lastname, String groupName, int age) {
        this.name = name;
        this.lastname = lastname;
        this.groupName = groupName;
        this.age = age;
    }

    // getters and setters

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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

    @Override
    public String toString() {
        return "Person{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", lastname='" + lastname + '\'' +
                ", groupName='" + groupName + '\'' +
                ", age=" + age +
                '}';
    }
}
