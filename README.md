# Final project in Software Engineering 2019/2020
### Santorini, GC15 - Computer Science Engineering BSc, Politecnico di Milano.

![Santorini board game](https://www.playbazar.it/11316-large/santorini.jpg)

Java implementation of the board game called Santorini, edited by Cranio Creations.

## What we have done
We implemented all the requirements listed for a maximum assessment of 30L, as indicated in the requirements:
1) :books: Complete rules
2) :video_game: CLI
3) :palm_tree: GUI
4) :satellite: Socket
5) :trident: Advanced Gods
6) :floppy_disk: Persistence

## Line Coverage
- Controller = 100%
- Model = 95% <br >
More details listed in jacoco (deliveries/jacoco)

### Testing
We made 183 tests for both model and controller, the latter has been tested also through stubs; in order to run them successfully:
```shell
mvn test
```

## Usage
Generate jar files (client and server) through maven by executing the following commands:
```shell
mvn clean
mvn package
```


**NB.** The following commands to run jar files only work if they are generated through maven.
If the user wants to use the ones in 'deliveries' folder the path has to change accordingly.

- **Server**
  - **Default port** (1337):
  ```shell
  java -jar target/GC15-server-jar-with-dependencies.jar
  ```
  - **Chosen port**:
  ```shell
  java -jar target/GC15-server-jar-with-dependencies.jar -p <port_num>
  ```
  where <port_num> stands for the socket port which both server and clients will use for the connection.

- **Client CLI**
  ```shell
  java -jar target/GC15-client-jar-with-dependencies.jar
  ```
  - ***Commands***
    - Move: **move (x,y)**
    - Build: **build (x,y)**
    - Active Power: **usePower (x,y)**
  
- **Client GUI**<br />
Double click on **target/GC15-client-jar-with-dependencies.jar** icon. <br >


## Students
- Riccardo Secreti (matr. 889417)   :   @RiccardoSecreti
- Fabio Tresoldi (matr. 886622)     :   @Fabio-Tresoldi
- Mirko Usuelli (matr. 888170)      :   @mirkousuelli

## Professors/Tutors
- Prof. Gianpaolo Cugola
- Emanuele Del Sozzo        :     @emanueledelsozzo
- Alberto Parravicini       :     @AlbertoParravicini
