# Getting Started

<cite>
**Referenced Files in This Document**  
- [README.md](file://README.md)
- [pom.xml](file://pom.xml)
- [build.bat](file://build.bat)
- [build.sh](file://build.sh)
- [start.bat](file://start.bat)
- [start.sh](file://start.sh)
- [Server.java](file://src/main/java/server/Server.java)
- [Settings.java](file://src/main/java/manager/Settings.java)
- [HikariCP.java](file://src/main/java/database/HikariCP.java)
</cite>

## Table of Contents
1. [Introduction](#introduction)
2. [Environment Setup](#environment-setup)
3. [Database Configuration](#database-configuration)
4. [Network Settings](#network-settings)
5. [Building the Project](#building-the-project)
6. [Running the Server](#running-the-server)
7. [Command-Line Arguments and Output](#command-line-arguments-and-output)
8. [Troubleshooting Common Issues](#troubleshooting-common-issues)
9. [Verification Procedure](#verification-procedure)
10. [Hello World Test Scenario](#hello-world-test-scenario)

## Introduction
This guide provides comprehensive instructions for setting up and running the KPAH-qoder server, a Java-based game server project. It covers all essential steps from environment preparation to server verification, including detailed guidance on Java installation, database setup, build processes, and troubleshooting. The documentation is designed to help developers and system administrators quickly deploy the server and validate its functionality.

## Environment Setup

### Java 21 Installation
The KPAH-qoder server requires **Java 21** with preview features enabled. Follow these steps to install and configure Java:

#### Windows
1. Download JDK 21 from [Oracle](https://www.oracle.com/java/technologies/downloads/#java21) or [OpenJDK](https://openjdk.org/projects/jdk/21/)
2. Install the JDK package
3. Set environment variables:
   ```cmd
   setx JAVA_HOME "C:\Program Files\Java\jdk-21"
   setx PATH "%PATH%;%JAVA_HOME%\bin"
   ```
4. Verify installation:
   ```bash
   java --version
   javac --version
   ```

#### Linux/macOS
```bash
# Ubuntu/Debian
sudo apt install openjdk-21-jdk

# macOS with Homebrew
brew install openjdk@21

# Verify installation
java --version
```

Ensure preview features are enabled by default in your runtime environment.

### Maven Installation
Maven is required for building the project. Install it using your preferred method:

#### Windows (Chocolatey)
```bash
choco install maven
```

#### Windows (Scoop)
```bash
scoop install maven
```

#### Linux/macOS
```bash
# Ubuntu/Debian
sudo apt install maven

# macOS
brew install maven
```

Verify Maven installation:
```bash
mvn -version
```

**Section sources**
- [README.md](file://README.md#L10-L45)

## Database Configuration

The server uses MySQL 8.0 with HikariCP connection pooling. Configure the database as follows:

### MySQL Setup
1. Install MySQL 8.0 from the official website
2. Start the MySQL service
3. Create the database and user:
   ```sql
   CREATE DATABASE kpah;
   CREATE USER 'root'@'localhost' IDENTIFIED BY 'password';
   GRANT ALL PRIVILEGES ON kpah.* TO 'root'@'localhost';
   FLUSH PRIVILEGES;
   ```

### Database Connection Settings
The connection parameters are defined in `Settings.java` and used by `HikariCP.java`:

| Parameter | Value | Description |
|---------|-------|-----------|
| `DATABASE` | `kpah` | Database name |
| `HOST` | `127.0.0.1:3306` | Host and port |
| `USER` | `root` | Database username |
| `PASS` | `password` | Database password |

These settings can be modified in `Settings.java` before compilation.

**Section sources**
- [Settings.java](file://src/main/java/manager/Settings.java#L10-L20)
- [HikariCP.java](file://src/main/java/database/HikariCP.java#L15-L25)

## Network Settings

The server listens on a specific port for incoming client connections. The network configuration is controlled through the `Settings` class.

### Port Configuration
- **Port Number**: `19129` (defined in `Settings.PORT_SERVER`)
- **Maximum Players**: `40,000` concurrent connections
- **Connection Timeout**: 30 seconds
- **Idle Timeout**: 60 seconds

To change the port, modify the `PORT_SERVER` constant in `Settings.java`:
```java
public static final int PORT_SERVER = 19129;
```

Ensure the port is not blocked by firewalls or used by other applications.

**Section sources**
- [Settings.java](file://src/main/java/manager/Settings.java#L15-L16)
- [Server.java](file://src/main/java/server/Server.java#L32-L35)

## Building the Project

You can build the project using either Maven commands or the provided build scripts.

### Using Maven
```bash
# Clean and compile
mvn clean compile

# Run tests
mvn test

# Package into JAR
mvn clean package
```

### Using Build Scripts
```bash
# Windows
build.bat

# Linux/macOS
./build.sh
```

The build process creates:
- `target/kpah-server.jar` - Main application JAR
- `target/kpah-server-jar-with-dependencies.jar` - Executable JAR with all dependencies
- `target/lib/` - Directory containing dependency JARs

The Maven build includes:
- Compilation with Java 21
- Dependency resolution via `pom.xml`
- Packaging with manifest configuration
- Copying dependencies to `lib/` directory

**Section sources**
- [pom.xml](file://pom.xml#L10-L215)
- [build.bat](file://build.bat#L1-L7)
- [build.sh](file://build.sh#L1-L5)

## Running the Server

### Using Start Scripts
```bash
# Windows
start.bat

# Linux/macOS
./start.sh
```

### Using Maven Directly
```bash
mvn clean compile exec:java -Dexec.mainClass="server.Server" -Dexec.args="--enable-preview"
```

### Manual Execution
After building, run the executable JAR:
```bash
java -jar target/kpah-server-jar-with-dependencies.jar
```

The server will start and begin listening for connections on the configured port.

**Section sources**
- [start.bat](file://start.bat#L1-L6)
- [start.sh](file://start.sh#L1-L3)
- [Server.java](file://src/main/java/server/Server.java#L25-L30)

## Command-Line Arguments and Output

### Server Startup Output
When the server starts successfully, you should see output similar to:
```
Building KPAH Server package...
[INFO] Scanning for projects...
[INFO] Building KPAH Server 1.0.0
...
Listen Port 19129
Accept IpAddress 127.0.0.1
```

### Command-Line Interface
Once running, the server accepts the following commands:

| Command | Effect |
|--------|--------|
| `baotri` | Puts server in maintenance mode and shuts down |
| `thread` | Displays current thread count |
| `player` | Shows number of players in game |
| `session` | Shows number of active sessions |

These commands are processed by the `activeCommandLine()` method in `Server.java`.

### Expected Startup Sequence
1. ANSI console initialization
2. ASCII art logo display
3. Manager initialization
4. Command line thread activation
5. Port binding and listening
6. Virtual thread submission for clan and top manager updates

**Section sources**
- [Server.java](file://src/main/java/server/Server.java#L60-L118)
- [Settings.java](file://src/main/java/manager/Settings.java#L40-L55)

## Troubleshooting Common Issues

### Port Conflicts
**Symptom**: "Port Is Already Open" error message  
**Solution**: 
- Check if another instance is running
- Use `netstat` to identify port usage:
  ```bash
  # Windows
  netstat -ano | findstr :19129
  
  # Linux/macOS
  lsof -i :19129
  ```
- Change the port in `Settings.java` if needed

### Database Connection Failures
**Symptom**: Server fails to start or throws SQL exceptions  
**Causes and Solutions**:
- MySQL service not running → Start MySQL service
- Incorrect credentials → Verify `USER` and `PASS` in `Settings.java`
- Database not created → Create `kpah` database
- Firewall blocking connection → Allow port 3306

### Missing Dependencies
**Symptom**: Compilation errors or `ClassNotFoundException`  
**Solution**:
- Run `mvn dependency:resolve` to download all dependencies
- Ensure internet connection during build
- Verify `pom.xml` contains correct dependency versions

### Build Failures
**Common Issues**:
- Maven not in PATH → Add Maven to system PATH
- Java version mismatch → Ensure Java 21 is used
- Encoding issues → Build scripts set `chcp 65001` for UTF-8

**Section sources**
- [Server.java](file://src/main/java/server/Server.java#L50-L55)
- [HikariCP.java](file://src/main/java/database/HikariCP.java#L20-L30)
- [pom.xml](file://pom.xml#L50-L100)

## Verification Procedure

To verify the server is running correctly:

1. **Check Build Success**: Ensure `build.bat` or `build.sh` completes without errors
2. **Monitor Startup**: Look for "Listen Port 19129" in the output
3. **Test Connection**: Use telnet to test port connectivity:
   ```bash
   telnet 127.0.0.1 19129
   ```
4. **Execute Commands**: Type `thread`, `player`, or `session` to verify command processing
5. **Check Logs**: Look for any error messages in the console output
6. **Verify Database**: Confirm the server can access the `kpah` database

A successful verification shows the server accepting connections and responding to commands.

**Section sources**
- [Server.java](file://src/main/java/server/Server.java#L60-L118)
- [start.bat](file://start.bat#L1-L6)

## Hello World Test Scenario

Perform a simple test to validate the installation:

1. **Start the Server**:
   ```bash
   start.bat  # Windows
   ./start.sh # Linux/macOS
   ```

2. **Wait for Startup**: Confirm you see:
   ```
   Listen Port 19129
   ```

3. **Test Commands**: Type the following commands:
   ```
   thread
   player
   session
   ```

4. **Expected Output**:
   ```
   Thread count: 10
   Player in game: 0
   Session connect: 0
   ```

5. **Graceful Shutdown**: Type `baotri` to shut down the server properly.

This test verifies:
- Java environment is correctly configured
- Maven build process works
- Server starts and binds to the port
- Command-line interface functions
- Shutdown procedure completes cleanly

If all steps succeed, your KPAH-qoder server installation is working correctly.

**Section sources**
- [Server.java](file://src/main/java/server/Server.java#L90-L118)
- [start.bat](file://start.bat#L1-L6)
- [start.sh](file://start.sh#L1-L3)