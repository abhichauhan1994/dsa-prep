# DSA Playground

A Spring Boot scratch pad for practising DSA patterns in Java.

## Run

```bash
mvn spring-boot:run
```

Output appears in the console. Edit `Runner.java` to try different inputs or algorithms.

## Test

```bash
mvn test
```

Tests live in `src/test/java/com/dsa/playground/SolutionTest.java`.

## Workflow

1. **Write your solution** in `Runner.java` (or a new class in the same package).
2. **Call it** from `Runner#run()` to see live output.
3. **Add a `@Test`** in `SolutionTest.java` to lock in correctness.
4. Run `mvn test` — green = done.

## Requirements

- Java 17+
- Maven 3.6+
