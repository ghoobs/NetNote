# CSEP Template Project

This repository contains the template for the CSE project. Please extend this README.md with sufficient instructions that will illustrate for your TA and the course staff how they can run your project.

To run the template project from the command line, you either need to have [Maven](https://maven.apache.org/install.html) installed on your local system (`mvn`) or you need to use the Maven wrapper (`mvnw`). You can then execute

	mvn clean install

to package and install the artifacts for the three subprojects. Afterwards, you can run ...

	cd server
	mvn spring-boot:run

Then select the port that you want the server to be active on (e.g. 9090; default is 8080)

or ...

	cd client
	mvn javafx:run

to run the client. Please note that the server needs to be running, before you can start the client.

Once this is working, you can try importing the project into your favorite IDE.

### Useful Shortcuts:
- CTRL + S - to save a Note
- CTRL + N - to create a new Note (in the Notes Overview window) or a new Collection (in the Edit Collections window)
- CTRL + D - to delete a Note
- CTRL + R - to refresh the client view
- CTRL + M - to navigate to the Edit Collections window

### Keyboard Navigation:
- Pressing the escape key sets the input focus to the search bar.
- After you typed your keyword(s) in the search bar, pressing ENTER will set the input focus to the list of notes.
- You can then navigate through the list of notes using arrow keys (up and down)
- To select a Note from the list, press SHIFT

