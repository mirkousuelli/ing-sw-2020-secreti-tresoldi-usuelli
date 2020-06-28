# Final project in Software Engineering 2019/2020
Santorini, GC15 - Computer Science Engineering BSc, Politecnico di Milano.

![Santorini board game](https://www.playbazar.it/11316-large/santorini.jpg)

Java implementation of the board game called Santorini edited by Cranio Creations.

## What we have done
We implemented all the requirements listed for the maximum assessment of 30L.
1) :books: Complete rules
2) :video_game: CLI
3) :palm_tree: GUI
4) :satellite: Socket
5) :trident: Advanced gods
6) :floppy_disk: Persistence

## Coverage
- Controller = 100%
- Model = 93%

## Usage
Generate jar files (client and server) through maven by executing the following commands:
```shell
mvn clean
mvn package
```
- **Server**<br />
Default port:
```shell
java -jar target/GC15-server-jar-with-dependencies.jar
```
Chosen port:
```shell
java -jar target/GC15-server-jar-with-dependencies.jar -p <port_num>
```
- **Client CLI**
```shell
java -jar target/GC15-client-jar-with-dependencies.jar
```
- **Client GUI**<br />
double click on **target/GC15-client-jar-with-dependencies.jar** icon.

## Students
- Riccardo Secreti (matr. 889417)   :   @RiccardoSecreti
- Fabio Tresoldi (matr. 886622)     :   @Fabio-Tresoldi
- Mirko Usuelli (matr. 888170)      :   @mirkousuelli

## Professors/Tutors
- Prof. Giampaolo Cugola
- Emanuele Del Sozzo        :     @emanueledelsozzo
- Alberto Parravicini       :     @AlbertoParravicini
