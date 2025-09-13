# KPAH Server

A Java game server project migrated from NetBeans to IntelliJ IDEA with Maven.

## Prerequisites

### Required Software
- **Java 21** (with preview features enabled) ✅ Already installed
- **Maven 3.8+** (needs to be installed)
- **IntelliJ IDEA** (recommended IDE)

### Installing Maven

#### Windows
1. Download Maven from: https://maven.apache.org/download.cgi
2. Extract to a folder like `C:\Program Files\Apache\maven`
3. Add `C:\Program Files\Apache\maven\bin` to your PATH environment variable
4. Restart your terminal/command prompt
5. Verify installation: `mvn -version`

#### Using Chocolatey (Windows)
```bash
choco install maven
```

#### Using Scoop (Windows)
```bash
scoop install maven
```

#### Linux/macOS
```bash
# Ubuntu/Debian
sudo apt install maven

# macOS with Homebrew
brew install maven
```

## Project Structure

```
KPAH/
├── src/
│   ├── main/
│   │   ├── java/          # Source code
│   │   └── resources/     # Resources
│   │       └── data/      # Game assets (images, maps, etc.)
│   └── test/
│       ├── java/          # Test code
│       └── resources/     # Test resources
├── target/                # Maven build output
├── .idea/                 # IntelliJ IDEA configuration
├── pom.xml               # Maven configuration
├── start.bat             # Windows run script
├── start.sh              # Unix/Linux run script
├── build.bat             # Windows build script
├── build.sh              # Unix/Linux build script
└── README.md             # This file
```

## Building the Project

### Using Maven Commands
```bash
# Clean and compile
mvn clean compile

# Run tests
mvn test

# Package into JAR
mvn clean package

# Run the application
mvn exec:java -Dexec.mainClass="server.Server" -Dexec.args="--enable-preview"
```

### Using Build Scripts
```bash
# Windows
build.bat

# Linux/macOS
./build.sh
```

## Running the Project

### Using Maven
```bash
mvn clean compile exec:java -Dexec.mainClass="server.Server" -Dexec.args="--enable-preview"
```

### Using Run Scripts
```bash
# Windows
start.bat

# Linux/macOS
./start.sh
```

### Using IntelliJ IDEA
1. Open the project in IntelliJ IDEA
2. Import Maven project (should happen automatically)
3. Use the "KPAH Server" run configuration
4. Or manually run the `server.Server` main class

## Dependencies

The project uses the following dependencies (managed by Maven):

- **HikariCP 5.1.0** - Database connection pooling
- **MySQL Connector 8.0.33** - MySQL database driver
- **Lombok 1.18.30** - Code generation (provided scope)
- **SLF4J 2.0.9** - Logging API
- **Log4j 2.21.1** - Logging implementation
- **Jansi 2.4.1** - Console colors
- **JSON 20231013** - JSON processing
- **JUnit 5.10.1** - Testing framework (test scope)

## Migration Notes

This project has been migrated from NetBeans to IntelliJ IDEA with Maven:

### Changes Made:
1. ✅ Created standard Maven directory structure (`src/main/java`, `src/test/java`)
2. ✅ Converted NetBeans dependencies to Maven dependencies in `pom.xml`
3. ✅ Moved source files to Maven structure
4. ✅ Moved `data/` folder to `src/main/resources/data/` for proper resource management
5. ✅ Created IntelliJ IDEA configuration files
6. ✅ Removed NetBeans-specific files (`nbproject/`, `build.xml`, etc.)
7. ✅ Updated build scripts for Maven workflow
8. ✅ Configured Java 21 with preview features enabled

### Old vs New:
- **Old**: NetBeans project with manual JAR dependencies
- **New**: Maven project with automatic dependency management
- **Main Class**: `server.Server` (unchanged)
- **Java Version**: Java 21 with preview features (unchanged)

## Important Notes

### Game Data Assets
The `data/` folder contains essential game assets (images, maps, effects, etc.) that are required for the game to run properly. The code references:

- **Image Assets**: `data/image/` (weapons, animals, potions, skills, etc.)
- **Map Data**: `data/map/` (mob images, tile data)
- **Effect Data**: Various effect files for game animations

**Action Required**: Ensure the `data/` folder is properly placed in `src/main/resources/data/` for Maven resource management, or update the code paths accordingly.

### Development Workflow

1. **Import into IntelliJ IDEA**: Open the project folder
2. **Maven Import**: IntelliJ should automatically detect and import the Maven project
3. **Lombok Plugin**: Install Lombok plugin in IntelliJ for annotation processing
4. **Run Configuration**: Use the provided "KPAH Server" run configuration
5. **Build**: Use Maven goals or provided build scripts

## Troubleshooting

### Maven not found
- Ensure Maven is installed and in your PATH
- Try using IntelliJ's embedded Maven

### Compilation errors
- Ensure Java 21 is properly configured
- Verify preview features are enabled
- Check that all dependencies are downloaded

### Lombok issues
- Install Lombok plugin in IntelliJ IDEA
- Enable annotation processing in IntelliJ settings

## Commands Reference

```bash
# Build commands
mvn clean                 # Clean build artifacts
mvn compile              # Compile source code
mvn test                 # Run tests
mvn package              # Create JAR file
mvn clean package        # Clean and package

# Run commands
mvn exec:java            # Run main class
mvn exec:java -Dexec.mainClass="server.Server"

# Development
mvn clean compile        # Clean compile for development
mvn dependency:tree      # Show dependency tree
mvn dependency:resolve   # Download all dependencies
```