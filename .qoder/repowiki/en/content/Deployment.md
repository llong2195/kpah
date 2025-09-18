# Deployment

<cite>
**Referenced Files in This Document**   
- [README.md](file://README.md)
- [pom.xml](file://pom.xml)
- [build.sh](file://build.sh)
- [build.bat](file://build.bat)
- [start.sh](file://start.sh)
- [start.bat](file://start.bat)
- [Server.java](file://src/main/java/server/Server.java)
- [Settings.java](file://src/main/java/manager/Settings.java)
- [Manager.java](file://src/main/java/manager/Manager.java)
</cite>

## Table of Contents
1. [Introduction](#introduction)
2. [Building the Server Binary](#building-the-server-binary)
3. [Startup Procedures](#startup-procedures)
4. [System Service Configuration](#system-service-configuration)
5. [Process Monitoring and Log Rotation](#process-monitoring-and-log-rotation)
6. [Resource Requirements](#resource-requirements)
7. [Load Testing and Performance Monitoring](#load-testing-and-performance-monitoring)
8. [Scaling Strategies](#scaling-strategies)
9. [Rollback Procedures and Version Management](#rollback-procedures-and-version-management)

## Introduction
This document provides comprehensive deployment instructions for the KPAH game server in production environments. It covers the complete lifecycle from building the server binary to deployment, monitoring, scaling, and maintenance. The server is a Java-based application using Maven for build management, designed to handle high-concurrency scenarios typical of online multiplayer games.

The deployment process is designed to be consistent across Windows and Linux environments, with specific scripts provided for each platform. The server is configured to run with Java 21 and uses virtual threads for efficient handling of concurrent connections. This documentation provides detailed guidance for operations teams to deploy, monitor, and scale the server infrastructure effectively.

## Building the Server Binary

The KPAH server uses Maven as its build tool, with comprehensive build scripts provided for both Windows and Linux environments. The build process compiles the source code, packages dependencies, and creates executable JAR files ready for deployment.

### Maven Build Process
The project is configured with Maven profiles for different environments. The build process is defined in the `pom.xml` file, which specifies Java 21 as the target version and includes all necessary dependencies such as HikariCP for database connection pooling, MySQL Connector, Lombok, and logging frameworks.

To build the server using Maven commands directly:
```bash
# Clean and compile the project
mvn clean compile

# Package into JAR with dependencies
mvn clean package
```

The Maven configuration includes the maven-assembly-plugin to create a JAR file with all dependencies bundled, making deployment simpler by eliminating the need to manage external library files.

### Platform-Specific Build Scripts
The project provides platform-specific build scripts that encapsulate the Maven build process:

**Linux/macOS (build.sh):**
```bash
#!/bin/bash
echo "Building KPAH Server package..."
mvn clean package
echo
echo "Build completed! Check target directory for JAR files."
```

**Windows (build.bat):**
```batch
@echo off
chcp 65001
echo Building KPAH Server package...
mvn clean package
echo.
echo Build completed! Check target directory for JAR files.
pause
```

These scripts execute the Maven clean and package goals, which compile the source code and package it into a JAR file in the `target` directory. The build output includes `kpah-server-jar-with-dependencies.jar`, which contains the application and all required libraries.

**Section sources**
- [pom.xml](file://pom.xml#L1-L216)
- [build.sh](file://build.sh#L1-L5)
- [build.bat](file://build.bat#L1-L7)

## Startup Procedures

The KPAH server can be started using either Maven commands or platform-specific startup scripts. The server is designed to run as a standalone Java application with specific JVM arguments to enable preview features required by the codebase.

### Direct Maven Execution
The server can be started directly using Maven with the exec plugin:

```bash
mvn clean compile exec:java -Dexec.mainClass="server.Server" -Dexec.args="--enable-preview"
```

This command compiles the code (if necessary) and executes the main class `server.Server` with the `--enable-preview` argument, which is required for Java 21 preview features used in the application.

### Platform-Specific Startup Scripts
The project includes optimized startup scripts for each operating system:

**Linux/macOS (start.sh):**
```bash
#!/bin/bash
echo "Starting KPAH Server with Maven..."
mvn clean compile exec:java -Dexec.mainClass="server.Server" -Dexec.args="--enable-preview"
```

**Windows (start.bat):**
```batch
@echo off
chcp 65001
echo Starting KPAH Server with Maven...
@REM mvn clean compile exec:java -Dexec.mainClass="server.Server" -Dexec.args="--enable-preview"
@REM pause
java -jar kpah-server-jar-with-dependencies.jar
```

Notably, the Windows startup script uses direct JAR execution rather than Maven, which is more efficient for production environments. This approach reduces startup overhead by bypassing the Maven lifecycle and directly executing the pre-built JAR file.

The server entry point is the `Server` class in the `server` package, which initializes the server socket, binds to the configured port, and starts accepting client connections. The server uses virtual threads for handling client sessions, allowing for efficient processing of many concurrent connections.

**Section sources**
- [start.sh](file://start.sh#L1-L3)
- [start.bat](file://start.bat#L1-L6)
- [Server.java](file://src/main/java/server/Server.java#L1-L119)

## System Service Configuration

For production deployment, the KPAH server should be configured as a system service to ensure automatic startup, proper process management, and integration with the operating system's service management framework.

### Linux Service Configuration
On Linux systems, the server should be configured as a systemd service. Create a service file at `/etc/systemd/system/kpah-server.service`:

```ini
[Unit]
Description=KPAH Game Server
After=network.target
Requires=network.target

[Service]
Type=simple
User=kpah
Group=kpah
WorkingDirectory=/opt/kpah-server
ExecStart=/usr/bin/java -jar /opt/kpah-server/target/kpah-server-jar-with-dependencies.jar
Restart=always
RestartSec=10
StandardOutput=journal
StandardError=journal
SyslogIdentifier=kpah-server

[Install]
WantedBy=multi-user.target
```

After creating the service file, enable and start the service:
```bash
sudo systemctl daemon-reload
sudo systemctl enable kpah-server
sudo systemctl start kpah-server
```

### Windows Service Configuration
On Windows systems, the server can be configured as a Windows service using NSSM (Non-Sucking Service Manager) or similar tools:

1. Download and install NSSM
2. Run `nssm install KPAHServer`
3. Set the application path to `java.exe`
4. Set the arguments to `-jar "C:\path\to\kpah-server\target\kpah-server-jar-with-dependencies.jar"`
5. Set the startup directory to the server installation directory
6. Configure recovery options to restart the service on failure

Alternatively, use PowerShell to create a service:
```powershell
New-Service -Name "KPAHServer" -BinaryPathName "C:\Program Files\Java\jdk-21\bin\java.exe -jar C:\path\to\kpah-server\target\kpah-server-jar-with-dependencies.jar" -DisplayName "KPAH Game Server" -StartupType Automatic
```

The service should run under a dedicated user account with appropriate permissions and resource limits.

**Section sources**
- [Server.java](file://src/main/java/server/Server.java#L1-L119)
- [Settings.java](file://src/main/java/manager/Settings.java#L1-L59)

## Process Monitoring and Log Rotation

Effective process monitoring and log management are critical for maintaining server stability and diagnosing issues in production environments.

### Process Monitoring
The KPAH server includes built-in command-line monitoring capabilities accessible through the server console. Administrators can enter commands to monitor server status:

- `thread` - Displays the current thread count
- `player` - Shows the number of players currently in the game
- `session` - Displays the number of active client sessions
- `baotri` - Initiates server shutdown for maintenance

For external monitoring, integrate with standard monitoring tools:

**Linux (using systemd):**
```bash
# Check service status
systemctl status kpah-server

# View recent logs
journalctl -u kpah-server -n 100

# Monitor resource usage
systemctl show kpah-server --property=CPUUsage,MemoryCurrent
```

**Windows:**
- Use Windows Event Viewer to monitor service events
- Use Performance Monitor to track CPU, memory, and network usage
- Configure Windows Service Recovery options for automatic restart on failure

### Log Rotation
The server uses Log4j 2 for logging, configured through the standard Log4j configuration mechanism. To implement log rotation, create a `log4j2.xml` configuration file in the classpath:

```xml
<Configuration status="WARN">
    <Appenders>
        <RollingFile name="RollingFile" fileName="logs/server.log"
                     filePattern="logs/server-%d{yyyy-MM-dd}-%i.log.gz">
            <PatternLayout>
                <pattern>%d %p %c{1.} [%t] %m%n</pattern>
            </PatternLayout>
            <Policies>
                <TimeBasedTriggeringPolicy interval="1" modulate="true"/>
                <SizeBasedTriggeringPolicy size="100MB"/>
            </Policies>
            <DefaultRolloverStrategy max="10"/>
        </RollingFile>
    </Appenders>
    <Loggers>
        <Root level="info">
            <AppenderRef ref="RollingFile"/>
        </Root>
    </Loggers>
</Configuration>
```

This configuration rotates logs daily or when they reach 100MB, compresses them with gzip, and keeps up to 10 archived logs. The logs directory should be monitored to ensure sufficient disk space is available.

**Section sources**
- [Server.java](file://src/main/java/server/Server.java#L70-L85)
- [pom.xml](file://pom.xml#L50-L62)

## Resource Requirements

The resource requirements for the KPAH server vary based on the expected player capacity. The server is designed to handle high concurrency using Java virtual threads, but adequate resources must be provisioned to maintain performance.

### Player Capacity and Resource Allocation
The server configuration in `Settings.java` specifies a maximum player capacity of 40,000 concurrent connections. Resource requirements scale with player count:

**Small Scale (1,000 players):**
- **CPU**: 4 cores
- **Memory**: 8GB RAM
- **Network**: 100Mbps bandwidth
- **Storage**: 50GB (including game assets and database)

**Medium Scale (10,000 players):**
- **CPU**: 8 cores
- **Memory**: 16GB RAM
- **Network**: 500Mbps bandwidth
- **Storage**: 100GB

**Large Scale (40,000 players):**
- **CPU**: 16+ cores
- **Memory**: 32GB+ RAM
- **Network**: 1Gbps+ bandwidth
- **Storage**: 200GB+

### Configuration Parameters
Key configuration parameters from `Settings.java` that affect resource usage:

- `MAX_PLAYER`: Maximum concurrent players (40,000)
- `PORT_SERVER`: Server port (19129)
- `MILISECOND_UPDATE_DATABASE`: Database update interval (300,000ms = 5 minutes)
- `MILISECOND_WAIT_KICK_SESSION`: Session timeout (60,000ms = 1 minute)
- `MILISECOND_WAIT_KICK_PLAYER`: Player timeout (600,000ms = 10 minutes)

The server uses connection pooling via HikariCP for database access, which should be configured based on the expected database load. The MySQL database should be hosted on a separate server or cluster for production deployments to ensure optimal performance.

**Section sources**
- [Settings.java](file://src/main/java/manager/Settings.java#L1-L59)
- [pom.xml](file://pom.xml#L15-L18)

## Load Testing and Performance Monitoring

Proper load testing and performance monitoring are essential for ensuring the server can handle production traffic and for identifying performance bottlenecks.

### Load Testing Strategy
Implement a comprehensive load testing plan using tools like JMeter, Gatling, or custom client simulators:

1. **Gradual Ramp-Up**: Start with 100 concurrent users and increase by 1,000 every 5 minutes until reaching maximum capacity
2. **Realistic Scenarios**: Simulate typical player behavior including login, movement, combat, and chat
3. **Stress Testing**: Exceed maximum capacity to identify breaking points
4. **Soak Testing**: Run at 80% capacity for 24+ hours to identify memory leaks

Key metrics to monitor during load testing:
- **Response Time**: Target < 100ms for most operations
- **Throughput**: Requests per second
- **Error Rate**: Should be < 0.1%
- **Resource Utilization**: CPU, memory, network, and database usage

### Performance Monitoring
Implement continuous performance monitoring in production:

**Application-Level Monitoring:**
- Use the built-in command interface to monitor player count, session count, and thread count
- Implement custom metrics for game-specific operations
- Monitor database query performance

**Infrastructure Monitoring:**
- CPU usage (target < 70% sustained)
- Memory usage (watch for gradual increases indicating memory leaks)
- Network I/O (monitor for saturation)
- Disk I/O (especially for database operations)

**JVM Monitoring:**
- Garbage collection frequency and duration
- Heap and non-heap memory usage
- Thread count and states
- Class loading and compilation

The server's use of virtual threads should provide excellent scalability, but monitoring is essential to ensure the thread pool is properly sized and that no blocking operations are causing thread starvation.

**Section sources**
- [Server.java](file://src/main/java/server/Server.java#L70-L85)
- [Manager.java](file://src/main/java/manager/Manager.java#L1-L1525)

## Scaling Strategies

To handle high-concurrency scenarios and ensure high availability, implement appropriate scaling strategies for the KPAH server.

### Horizontal Scaling
For large player bases, deploy multiple server instances behind a load balancer:

1. **Stateless Design**: Ensure the server can operate in a stateless manner or implement distributed session management
2. **Load Balancer**: Use a load balancer (HAProxy, NGINX, or cloud-based solutions) to distribute connections
3. **Session Affinity**: Configure session affinity (sticky sessions) if client state is maintained on the server
4. **Auto-Scaling**: Implement auto-scaling groups that add or remove instances based on load metrics

### Database Optimization
The database is likely to be the primary bottleneck at scale:

- **Connection Pooling**: Tune HikariCP settings for optimal performance
- **Indexing**: Ensure proper indexes on frequently queried database fields
- **Query Optimization**: Monitor and optimize slow queries
- **Read Replicas**: Implement read replicas for read-heavy operations
- **Caching**: Implement Redis or similar caching for frequently accessed data

### Network Optimization
For global deployments:
- Deploy server instances in multiple geographic regions
- Use a global load balancing solution
- Implement connection optimization for high-latency networks
- Consider using UDP for real-time game data and TCP for reliable operations

### Containerization and Orchestration
For advanced deployment scenarios, consider containerizing the server:

**Dockerfile:**
```dockerfile
FROM openjdk:21-jdk-slim
COPY target/kpah-server-jar-with-dependencies.jar /app/server.jar
EXPOSE 19129
CMD ["java", "-jar", "/app/server.jar"]
```

Deploy with Kubernetes for automated scaling, rolling updates, and self-healing capabilities.

**Section sources**
- [pom.xml](file://pom.xml#L15-L18)
- [Settings.java](file://src/main/java/manager/Settings.java#L1-L59)

## Rollback Procedures and Version Management

Robust version management and rollback procedures are essential for maintaining service availability during deployments.

### Version Management
Implement a structured versioning strategy:

1. **Semantic Versioning**: Use MAJOR.MINOR.PATCH versioning (e.g., 1.2.3)
2. **Version Tagging**: Tag releases in the source control system
3. **Build Artifacts**: Store build artifacts with version identifiers
4. **Configuration Management**: Keep configuration separate from code

The Maven `pom.xml` file already includes version management with the project version set to 1.0.0, which should be updated according to the versioning strategy.

### Deployment Strategy
Use a blue-green deployment or canary release strategy:

1. **Blue-Green Deployment**:
   - Deploy new version to inactive server group (green)
   - Test thoroughly
   - Switch traffic from old (blue) to new (green) group
   - Keep blue group available for rollback

2. **Canary Release**:
   - Deploy new version to a small subset of servers
   - Route a small percentage of traffic to the new version
   - Gradually increase traffic as stability is confirmed
   - Roll back if issues are detected

### Rollback Procedures
Prepare for rapid rollback in case of issues:

1. **Pre-Deployment Backup**:
   - Backup current server binaries
   - Backup database (if schema changes are involved)
   - Document current configuration

2. **Rollback Process**:
   - Stop the new version
   - Restore previous server binaries
   - Revert any database schema changes (if applicable)
   - Restart the server
   - Verify functionality

3. **Automated Rollback**:
   - Implement health checks that trigger automatic rollback if failure thresholds are exceeded
   - Monitor key metrics (error rate, response time, player count) for automatic rollback decisions

The server's design allows for relatively quick restarts, minimizing downtime during rollback operations. The `baotri` command can be used to gracefully shut down the server before rollback.

**Section sources**
- [pom.xml](file://pom.xml#L7-L10)
- [start.bat](file://start.bat#L1-L6)
- [build.sh](file://build.sh#L1-L5)