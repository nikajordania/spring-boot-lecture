# Java Lecture 13 Resources

This guide walks through the key Spring Boot concepts used in **this project** — from the entry point and layered architecture to JPA entities, repositories, REST controllers, and API documentation with Swagger/OpenAPI.

---

## 1) Project Entry Point — `@SpringBootApplication`

**Why it matters**
- Every Spring Boot application starts here. This single annotation does the heavy lifting of configuring the whole application.

**What `@SpringBootApplication` does**
- It is a shortcut for three annotations combined:
  - `@SpringBootConfiguration` — marks the class as a configuration source.
  - `@EnableAutoConfiguration` — tells Spring Boot to automatically configure beans based on the dependencies on the classpath.
  - `@ComponentScan` — scans the current package and all sub-packages for Spring-managed components (`@Controller`, `@Service`, `@Repository`, etc.).

**Code from this project**

```java
@SpringBootApplication
public class SpringBootLectureApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringBootLectureApplication.class, args);
    }
}
```

**Read more**
- [Spring Boot Docs: @SpringBootApplication](https://docs.spring.io/spring-boot/docs/current/reference/html/using.html#using.using-the-springbootapplication-annotation)

---

## 2) Project Structure — Layered Architecture

**Why it matters**
- Separating code into layers keeps each class focused on one responsibility and makes the project easier to read and maintain.

**Layers in this project**

```
src/main/java/org/example/springbootlecture/
├── SpringBootLectureApplication.java   ← entry point
├── config/
│   └── OpenAPIConfiguration.java       ← app-wide configuration beans
├── controller/
│   └── PersonController.java           ← handles HTTP requests/responses
├── entities/
│   └── Person.java                     ← maps to the database table
└── repository/
    └── PersonRepository.java           ← database access logic
```

| Layer | Annotation | Responsibility |
|---|---|---|
| Entity | `@Entity` | Represents a database table row |
| Repository | `@Repository` | Communicates with the database |
| Controller | `@RestController` | Handles HTTP requests, returns JSON |
| Configuration | `@Configuration` | Declares Spring beans and settings |

---

## 3) The Entity — `@Entity` and JPA Annotations

**Why it matters**
- A JPA entity is a plain Java class that Spring automatically maps to a database table. Each field becomes a column.

**Key annotations used in `Person.java`**

| Annotation | Meaning |
|---|---|
| `@Entity` | Marks the class as a JPA-managed table |
| `@Id` | Marks the primary key field |
| `@GeneratedValue(strategy = GenerationType.IDENTITY)` | Auto-increments the ID by the database |

**Code from this project**

```java
@Entity
public class Person {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String name;
    private String lastname;
    private String groupName;
    private int age;

    // constructors, getters, setters, toString...
}
```

- Spring Boot + Hibernate reads this class and creates (or updates) the `PERSON` table in the database automatically, controlled by `spring.jpa.hibernate.ddl-auto=update` in `application.properties`.

**Read more**
- [Baeldung: JPA Entity](https://www.baeldung.com/jpa-entities)

---

## 4) The Repository — Spring Data JPA

**Why it matters**
- Instead of writing SQL manually, Spring Data JPA generates database queries from method names. You define the interface; Spring provides the implementation at runtime.

**Code from this project**

```java
@Repository
public interface PersonRepository extends JpaRepository<Person, Integer> {
    List<Person> findByGroupName(String groupName);
    List<Person> findByAgeBetween(int minAge, int maxAge);
    List<Person> findByGroupNameAndAgeLessThan(String groupName, int age);
}
```

**What you get for free by extending `JpaRepository<Person, Integer>`**

| Method | What it does |
|---|---|
| `findAll()` | Returns all rows as a `List<Person>` |
| `findById(id)` | Returns an `Optional<Person>` |
| `save(person)` | Inserts or updates a row |
| `delete(person)` | Deletes a row |
| `count()` | Returns the total number of rows |

**Derived query method naming rules**

Spring reads the method name and builds the SQL for you:

```
findBy<Field>                       → WHERE field = ?
findBy<Field>Between                → WHERE field BETWEEN ? AND ?
findBy<Field>LessThan               → WHERE field < ?
findBy<Field1>And<Field2>LessThan   → WHERE field1 = ? AND field2 < ?
```

**Read more**
- [Spring Data JPA Docs: Query Methods](https://docs.spring.io/spring-data/jpa/docs/current/reference/html/#jpa.query-methods)
- [Baeldung: Spring Data JPA](https://www.baeldung.com/the-persistence-layer-with-spring-data-jpa)

---

## 5) The Controller — REST API with `@RestController`

**Why it matters**
- The controller is the HTTP layer. It maps URLs to Java methods and automatically serializes return values to JSON.

**Key annotations**

| Annotation | Meaning |
|---|---|
| `@RestController` | Combines `@Controller` + `@ResponseBody`; every method returns JSON |
| `@RequestMapping("/persons")` | Base URL prefix for all methods in this class |
| `@GetMapping` | Handles HTTP GET requests |
| `@PostMapping` | Handles HTTP POST requests |
| `@PutMapping("/{id}")` | Handles HTTP PUT requests; `{id}` is a path variable |
| `@DeleteMapping("/{id}")` | Handles HTTP DELETE requests |
| `@PathVariable` | Binds a URL segment `{id}` to a method parameter |
| `@RequestBody` | Deserializes the JSON request body into a Java object |
| `@RequestParam` | Reads a query parameter from the URL (`?minAge=18&maxAge=30`) |

**Code from this project — all endpoints**

```java
@RestController
@RequestMapping("/persons")
public class PersonController {

    private final PersonRepository personRepository;

    // Constructor injection (recommended over @Autowired on fields)
    public PersonController(PersonRepository personRepository) {
        this.personRepository = personRepository;
    }

    // GET /persons
    @GetMapping
    public List<Person> getAllPersons() {
        return personRepository.findAll();
    }

    // GET /persons/5
    @GetMapping("/{id}")
    public ResponseEntity<Person> getPersonById(@PathVariable int id) {
        return personRepository.findById(id)
                .map(person -> ResponseEntity.ok().body(person))
                .orElse(ResponseEntity.notFound().build());
    }

    // POST /persons
    @PostMapping
    public Person createPerson(@RequestBody Person person) {
        return personRepository.save(person);
    }

    // PUT /persons/5
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

    // DELETE /persons/5
    @DeleteMapping("/{id}")
    public ResponseEntity<Object> deletePerson(@PathVariable int id) {
        return personRepository.findById(id)
                .map(person -> {
                    personRepository.delete(person);
                    return ResponseEntity.ok().build();
                }).orElse(ResponseEntity.notFound().build());
    }

    // GET /persons/group/GroupA
    @GetMapping("/group/{groupName}")
    public List<Person> getAllPersonsByGroupName(@PathVariable String groupName) {
        return personRepository.findByGroupName(groupName);
    }

    // GET /persons/age?minAge=18&maxAge=30
    @GetMapping("/age")
    public List<Person> getAllPersonsByAge(@RequestParam int minAge, @RequestParam int maxAge) {
        return personRepository.findByAgeBetween(minAge, maxAge);
    }

    // GET /persons/group/GroupA/age?age=25
    @GetMapping("/group/{groupName}/age")
    public List<Person> getAllPersonsByGroupNameAndAgeLessThan(
            @PathVariable String groupName, @RequestParam int age) {
        return personRepository.findByGroupNameAndAgeLessThan(groupName, age);
    }
}
```

**`ResponseEntity` explained**
- `ResponseEntity` lets you control the full HTTP response: status code, headers, and body.
- `ResponseEntity.ok().body(person)` → HTTP 200 with JSON body.
- `ResponseEntity.notFound().build()` → HTTP 404 with no body.
- Using `.map(...).orElse(...)` on an `Optional` is a clean way to handle "found vs. not found" logic.

**Read more**
- [Baeldung: Spring @RestController](https://www.baeldung.com/spring-controller-vs-restcontroller)
- [Baeldung: ResponseEntity](https://www.baeldung.com/spring-response-entity)

---

## 6) Dependency Injection — Constructor Injection

**Why it matters**
- Spring manages objects (called *beans*) and injects them where needed. Constructor injection is the recommended approach because it makes dependencies explicit and easier to test.

**How it works in this project**

```java
// Spring sees @RestController and creates a PersonController bean.
// It also sees the constructor needs a PersonRepository bean,
// so it creates one and passes it in automatically.
public PersonController(PersonRepository personRepository) {
    this.personRepository = personRepository;
}
```

- No `new PersonRepository()` is ever written — Spring does it.
- The field is `final`, which prevents accidental reassignment.

**Read more**
- [Baeldung: Spring Dependency Injection](https://www.baeldung.com/inversion-control-and-dependency-injection-in-spring)

---

## 7) Configuration — `@Configuration` and `@Bean`

**Why it matters**
- Sometimes you need to create a bean that doesn't fit neatly into `@Controller`, `@Service`, or `@Repository`. Use `@Configuration` + `@Bean` to register it manually.

**Code from this project**

```java
@Configuration
public class OpenAPIConfiguration {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Persons API")
                        .version("1.0")
                        .description("Persons Registration service API Description"));
    }
}
```

- `@Configuration` tells Spring this class contains bean definitions.
- `@Bean` tells Spring to call this method once and register the returned object as a bean in the application context.

---

## 8) API Documentation with Swagger / OpenAPI (`springdoc-openapi`)

**Why it matters**
- Swagger UI gives every developer (and tester) a browser-based page where they can read and try out all API endpoints without writing a single line of client code.

**Maven dependency in this project**

```xml
<dependency>
    <groupId>org.springdoc</groupId>
    <artifactId>springdoc-openapi-starter-webmvc-ui</artifactId>
    <version>3.0.3</version>
</dependency>
```

**How to open Swagger UI**
- Start the application, then visit: [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)

**Swagger annotations used in this project**

| Annotation | Where | What it does |
|---|---|---|
| `@Operation(summary = "...")` | Method | Short description of the endpoint shown in Swagger |
| `@ApiResponse(responseCode, description)` | Method | Documents a possible HTTP response code |
| `@ApiResponses` | Method | Groups multiple `@ApiResponse` annotations |
| `@Content(mediaType, schema)` | Inside `@ApiResponse` | Describes the response body format |
| `@Schema(description, example)` | Entity field | Documents a JSON field in Swagger |

**Example from this project**

```java
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
public ResponseEntity<Person> getPersonById(@PathVariable int id) { ... }
```

> **Note:** Swagger annotations only affect the generated API documentation — they do NOT change how the endpoint actually works.

**Read more**
- [springdoc-openapi GitHub](https://github.com/springdoc/springdoc-openapi)
- [Baeldung: Swagger with Spring Boot](https://www.baeldung.com/spring-rest-openapi-documentation)

---

## 9) Database Configuration — H2 and `application.properties`

**Why it matters**
- `application.properties` is the central place to configure the application without touching Java code. H2 is an in-memory/file-based database that is perfect for development and learning.

**Properties used in this project**

```properties
spring.application.name=spring-boot-lecture

# H2 file-based database (data is saved between restarts)
spring.datasource.url=jdbc:h2:file:./testdb;DB_CLOSE_DELAY=-1;AUTO_SERVER=TRUE;
spring.datasource.driverClassName=org.h2.Driver
spring.jpa.database-platform=org.hibernate.dialect.H2Dialect

# Automatically create/alter tables based on @Entity classes
spring.jpa.hibernate.ddl-auto=update

# Print generated SQL in the console (useful for learning)
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true

# Enable the H2 web console
spring.h2.console.enabled=true
```

**H2 Console**
- Visit [http://localhost:8080/h2-console](http://localhost:8080/h2-console) while the app is running.
- Use the JDBC URL from `application.properties` (`jdbc:h2:file:./testdb`) to connect and browse the data.

**`ddl-auto` values explained**

| Value | Behaviour |
|---|---|
| `create` | Drops and recreates all tables on every startup |
| `create-drop` | Same as `create`, but also drops tables on shutdown |
| `update` | Alters existing tables to match the entity (does not delete data) |
| `validate` | Checks the schema matches entities but makes no changes |
| `none` | Does nothing — you manage the schema yourself |

---

## 10) Full Request / Response Flow

This is what happens when a client calls `GET /persons/5`:

```
HTTP GET /persons/5
        │
        ▼
PersonController.getPersonById(5)
        │  calls
        ▼
PersonRepository.findById(5)        ← Spring Data JPA generates SQL
        │  returns Optional<Person>
        ▼
Optional is present?
  YES → ResponseEntity.ok().body(person)   → HTTP 200 + JSON
  NO  → ResponseEntity.notFound().build()  → HTTP 404
        │
        ▼
Spring serializes Person to JSON (Jackson library, included automatically)
        │
        ▼
HTTP Response sent to client
```

---

## 11) Practical Exercises

1. **Add a new field** `email` to `Person`. Restart the app and confirm H2 adds the column automatically (`ddl-auto=update`).
2. **Add a new endpoint** `GET /persons/name/{name}` that returns all persons with a given first name. Add the matching derived query method to `PersonRepository`.
3. **Use Java Faker** (already in `pom.xml`) to write a small `DataSeeder` class (annotated with `@Component` and implementing `CommandLineRunner`) that inserts 10 fake `Person` records on startup.
4. **Explore Swagger UI** at `/swagger-ui.html`. Try calling every endpoint from the browser. Notice how `@Operation` and `@ApiResponse` annotations change the documentation.
5. **Inspect generated SQL** in the console. Add a `findByLastname` method to the repository and watch the SQL Spring generates.
6. **Convert `Person` to use Lombok** (see Lecture 12). Replace all getters, setters, constructors, and `toString` with `@Data`, `@NoArgsConstructor`, and `@AllArgsConstructor`.

