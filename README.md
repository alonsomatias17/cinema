<h1 align="center">
  <a href="https://github.com/alonsomatias17">
  </a>
  <br>
  <br>
  Cinema Service
  <br>
</h1>
<h4 align="center">Cinema app build with Kotlin, Ktor and Koin.</h4>

#### Package Architecture

Based on hexagonal architecture

![pkgsarchitecture](static/architecture.png)

As it can be seen, the <strong>Application</strong> module has visibility over Adapters and Domain.
<strong>Adapters</strong> module has visibility over Domain.
And the last one <strong>Domain</strong> can't "see" any other module but itself. Thus, Domain module can only interact with Adapters module over ports(interfaces).

### Dependencies


### Running the project

Before running the project please ensure that all the dependencies are installed in your system. Then follow the next:

1. Run the project

    ```
    docker-compose up app
    ```

2. Alternative you can run the app locally

    ```
    ./gradlew run
    ```

### Running quality checks

This project includes a static code analysis tool for the Kotlin programming language:

```
./gradlew detekt
```

### Running the tests

In order to run the project tests you need to execute the following command:

```
./gradlew test
```
