# DSA Java Playground

Plain Java + JUnit 5 project for practicing DSA solutions. No frameworks.

## Quick Start

```bash
cd java-playground

# Compile and run Main.java
mvn compile exec:java

# Run tests
mvn test
```

## How to Use

1. **Solve a problem**: Add a method to an existing class in `src/main/java/com/dsa/problems/`, or create a new class
2. **Run it**: Call your method from `Main.java`, then `mvn compile exec:java`
3. **Test it**: Add test cases in `src/test/java/com/dsa/problems/`, then `mvn test`

## Project Structure

```
src/main/java/com/dsa/
├── Main.java              <- Entry point. Call your solutions here.
└── problems/
    ├── SlidingWindow.java <- Topic 1 solutions
    ├── TwoPointers.java   <- Topic 2 (create as you progress)
    ├── BinarySearch.java  <- Topic 4
    └── ...                <- One class per topic
```

No Spring Boot, no web server, no database. Just Java and a terminal.
