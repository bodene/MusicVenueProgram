Live Music Venue Matchmaker (LMVM)
The Live Music Venue Matchmaker (LMVM) is a desktop-based application designed to manage live music venues, events, bookings, and user accounts. The application is built using JavaFX, JDBC, and SQLite, and it follows the Model-View-Controller (MVC) design pattern as well as SOLID principles for maintainability and scalability.
________________________________________
Libraries and Dependencies
The application requires the following libraries and dependencies. Click the links below to download or view more details:
•	Java SE Development Kit 21
•	JavaFX 21
•	SceneBuilder (Any version compatible with JavaFX 21)
•	SQLite Studio (SQLite is included with SQLite Studio)
•	JDBC Driver for SQLite (org.xerial:sqlite-jdbc:3.48.0.0)
•	SLF4J API (slf4j-api-1.7.36.jar) (Required for more recent versions of sqlite-jdbc)
________________________________________
Installation and Setup
1.	Install Java SE Development Kit 21
Download and install the JDK 21 from the Oracle JDK 21 Downloads page.
2.	Install JavaFX 21
Download the JavaFX 21 SDK from OpenJFX.
Ensure you configure your IDE and Maven to include JavaFX on the module-path.
3.	Install SceneBuilder
Download and install SceneBuilder from Gluon SceneBuilder.
This tool will help you design the FXML-based user interfaces.
4.	Install SQLite Studio and SQLite
Download SQLite Studio from SQLite Studio.
SQLite is bundled with SQLite Studio, so no separate installation is necessary.
5.	Set Up JDBC and SLF4J
The Maven project includes the SQLite JDBC driver (version 3.48.0.0).
If you use a more recent version of sqlite-jdbc, ensure that slf4j-api-1.7.36.jar is added to your classpath.
________________________________________
Compiling and Running the Program
1.	Clone the Repository
Clone the LMVM repository to your local machine.
2.	Navigate to the Project Directory
Open a terminal or command prompt and change to the project’s root directory (where pom.xml is located).
3.	Compile the Project
Use Maven to clean and compile the project:
  		#mvn clean compile
4.	Run the Application
Run the application using the JavaFX Maven plugin:
      #mvn javafx:run
The pom.xml is configured with the main class (e.g., app.Main), so Maven will launch the application accordingly.
________________________________________
Additional Notes
•	IDE Setup: Ensure that your IDE is configured with the correct JDK and JavaFX libraries.
•	Database Initialisation: The SQLite database (e.g., db/music_venue.db) will be created/initialised using the provided schema if not already present.
•	Logging and Debugging: For enhanced error tracking, consider integrating a logging framework (e.g., SLF4J with Logback) instead of using printStackTrace().
________________________________________
This README should help you install the required dependencies and run your LMVM application successfully. If you have any questions or run into issues, please consult the documentation provided with each dependency or reach out for support.

