# Data Management

<cite>
**Referenced Files in This Document**   
- [HikariCP.java](file://src/main/java/database/HikariCP.java)
- [PlayerDAO.java](file://src/main/java/daos/PlayerDAO.java)
- [ReadData.java](file://src/main/java/server/ReadData.java)
- [Manager.java](file://src/main/java/manager/Manager.java)
- [ItemEquipTemplate.java](file://src/main/java/template/ItemEquipTemplate.java)
- [MonsterTemplate.java](file://src/main/java/template/MonsterTemplate.java)
- [SkillNewTemplate.java](file://src/main/java/template/SkillNewTemplate.java)
- [HoaTieuTemplate.java](file://src/main/java/template/HoaTieuTemplate.java)
</cite>

## Table of Contents
1. [Introduction](#introduction)
2. [Project Structure](#project-structure)
3. [Core Components](#core-components)
4. [Architecture Overview](#architecture-overview)
5. [Detailed Component Analysis](#detailed-component-analysis)
6. [Dependency Analysis](#dependency-analysis)
7. [Performance Considerations](#performance-considerations)
8. [Troubleshooting Guide](#troubleshooting-guide)
9. [Conclusion](#conclusion)

## Introduction
This document provides comprehensive documentation for the data management system of the game server, focusing on the persistence layer using HikariCP connection pooling and PlayerDAO data access patterns. It details the template system architecture where game entities such as items, monsters, and skills are defined in configuration files and loaded into memory at startup. The document explains the relationship between templates and runtime instances, the data lifecycle from database storage to in-memory caching, and periodic persistence mechanisms. Performance considerations for template loading, query optimization, and connection pool tuning are also addressed.

## Project Structure
The project follows a modular structure with clear separation of concerns. The core components are organized into packages based on their functionality, including database access, data access objects, game templates, player management, and server initialization.

```mermaid
graph TD
subgraph "Core Packages"
database["database\n(HikariCP, ResultSetImpl)"]
daos["daos\n(PlayerDAO)"]
template["template\n(ItemEquipTemplate, MonsterTemplate, etc.)"]
manager["manager\n(Manager, Settings)"]
server["server\n(ReadData, Server)"]
end
subgraph "Entity Packages"
player["player\n(Player, Inventory, Skill)"]
item["item\n(ItemEquip, ItemPotion, etc.)"]
map["map\n(Map, Zone, Monster)"]
skill["skill\n(SkillBuff, BuffInfluencePlayer)"]
end
subgraph "Utility Packages"
consts["consts\n(Constant definitions)"]
utils["utils\n(Logger, Util, Printer)"]
effects["effects\n(Animation, ImageInfo)"]
end
database --> daos
daos --> player
template --> daos
template --> manager
server --> manager
server --> template
manager --> database
manager --> template
manager --> player
```

**Diagram sources**
- [HikariCP.java](file://src/main/java/database/HikariCP.java)
- [PlayerDAO.java](file://src/main/java/daos/PlayerDAO.java)
- [Manager.java](file://src/main/java/manager/Manager.java)

**Section sources**
- [HikariCP.java](file://src/main/java/database/HikariCP.java)
- [PlayerDAO.java](file://src/main/java/daos/PlayerDAO.java)
- [Manager.java](file://src/main/java/manager/Manager.java)

## Core Components
The data management system consists of three main components: the database connection pool using HikariCP, the data access layer implemented in PlayerDAO, and the template system managed by the Manager class. These components work together to provide efficient data persistence and retrieval for player data and game configuration.

**Section sources**
- [HikariCP.java](file://src/main/java/database/HikariCP.java)
- [PlayerDAO.java](file://src/main/java/daos/PlayerDAO.java)
- [Manager.java](file://src/main/java/manager/Manager.java)

## Architecture Overview
The data management architecture follows a layered approach with clear separation between data access, business logic, and configuration management. The system uses HikariCP for efficient database connection pooling, PlayerDAO for player data operations, and a comprehensive template system for game entity definitions.

```mermaid
graph TD
subgraph "Client Layer"
Session["Session\n(Network handling)"]
end
subgraph "Business Logic Layer"
Player["Player\n(Runtime instance)"]
Services["Services\n(Business logic)"]
end
subgraph "Data Access Layer"
PlayerDAO["PlayerDAO\n(Data access operations)"]
HikariCP["HikariCP\n(Connection pool)"]
end
subgraph "Data Storage Layer"
MySQL["MySQL Database\n(Persistent storage)"]
end
subgraph "Template System"
ReadData["ReadData\n(Template loading)"]
Manager["Manager\n(Template storage)"]
Templates["Template Classes\n(ItemEquipTemplate, etc.)"]
end
Session --> Player
Player --> Services
Services --> PlayerDAO
PlayerDAO --> HikariCP
HikariCP --> MySQL
ReadData --> Manager
Manager --> Templates
Templates --> PlayerDAO
Templates --> Services
style HikariCP fill:#f9f,stroke:#333
style PlayerDAO fill:#f9f,stroke:#333
style Manager fill:#bbf,stroke:#333
style ReadData fill:#bbf,stroke:#333
```

**Diagram sources**
- [HikariCP.java](file://src/main/java/database/HikariCP.java)
- [PlayerDAO.java](file://src/main/java/daos/PlayerDAO.java)
- [Manager.java](file://src/main/java/manager/Manager.java)
- [ReadData.java](file://src/main/java/server/ReadData.java)

## Detailed Component Analysis

### Database Connection Pooling with HikariCP
The HikariCP class provides a robust database connection pooling implementation that manages MySQL connections efficiently. It is configured with optimal settings for the game server's requirements, including connection timeout, idle timeout, and statement caching.

```mermaid
classDiagram
class HikariCP {
-static final String DB_URL
-static final HikariConfig config
-static final HikariDataSource dataSource
+static Connection getConnection()
+static boolean execute(String sql)
+static ResultSetImpl executeQuery(String sql)
+static ResultSetImpl executeQuery(String sql, Object... objs)
+static int executeUpdate(String sql)
+static int execute(String sql, int id, Object... objs)
+static int execute(String sql, int id)
+static int executeExist(String sql)
}
class HikariConfig {
+setDriverClassName(String)
+setJdbcUrl(String)
+setUsername(String)
+setPassword(String)
+setMinimumIdle(int)
+setMaximumPoolSize(int)
+setConnectionTimeout(long)
+setIdleTimeout(long)
+setMaxLifetime(long)
+addDataSourceProperty(String, String)
}
class HikariDataSource {
+getConnection()
}
HikariCP --> HikariConfig : "config"
HikariCP --> HikariDataSource : "dataSource"
```

**Diagram sources**
- [HikariCP.java](file://src/main/java/database/HikariCP.java#L1-L124)

**Section sources**
- [HikariCP.java](file://src/main/java/database/HikariCP.java#L1-L124)

### Player Data Access with PlayerDAO
The PlayerDAO class implements data access patterns for player data, providing methods to create, read, update, and delete player information. It handles the serialization and deserialization of complex player objects to and from database records.

```mermaid
classDiagram
class PlayerDAO {
+static Player setupPlayer(int idSelect)
+static void updatePlayer(Player player)
+static void createPlayer(ISession session, String name, byte clazz, byte head, byte gender, byte idNation)
+static void deletePlayer(ISession session, int playerId)
+static void restorePlayer(ISession session, int playerId)
+static Player buildNpcActor(NpcTemplate npcTemplate, short x, short y)
+static List<Friend> buildListFriend(String friendString)
}
PlayerDAO --> HikariCP : "Uses"
PlayerDAO --> Player : "Creates"
PlayerDAO --> ItemEquip : "Uses"
PlayerDAO --> ItemPotion : "Uses"
PlayerDAO --> Skill : "Uses"
PlayerDAO --> Inventory : "Uses"
PlayerDAO --> Friend : "Uses"
```

**Diagram sources**
- [PlayerDAO.java](file://src/main/java/daos/PlayerDAO.java#L1-L555)

**Section sources**
- [PlayerDAO.java](file://src/main/java/daos/PlayerDAO.java#L1-L555)

### Template System Architecture
The template system loads game entity definitions from the database at startup and stores them in memory for efficient access during gameplay. This includes templates for items, monsters, skills, and other game entities that define their properties and behavior.

```mermaid
classDiagram
class ItemEquipTemplate {
-short id
-String name
-byte type
-byte style
-byte he
-byte gender
-byte level
-short durable
-short[] attribute
-int price
-byte classChar
-byte colorItem
-short idIcon
-short ndayLoan
-byte dxWear
-byte dyWear
}
class MonsterTemplate {
-short id
-String name
-byte type
-int maxHp
-short level
-byte palate
-byte spalate
-byte moveType
-byte speed
-byte height
-byte w
-byte h
-byte xCenter
-byte yCenter
-boolean isNewMonster
}
class SkillNewTemplate {
-short id
-String name
-String decript
-byte idSkill
-byte classChar
-int price
}
class HoaTieuTemplate {
-byte id
-short[][] mapId
-String[] nameMap
-String[][] nameMapChild
-short[] x
-short[] y
}
class Manager {
-ConcurrentHashMap<Short, ItemEquipTemplate> ITEM_EQUIPMENTS
-ConcurrentHashMap<Short, MonsterTemplate> MOB_TEMPLATES
-ConcurrentHashMap<Short, SkillNewTemplate> SKILL_NEW_TEMPLATES
-ConcurrentHashMap<Byte, HoaTieuTemplate> HOA_TIEU_TEMPLATES
+static void init()
+static ItemEquipTemplate getItemEquipment(short itemId)
+static MonsterTemplate getMobTemplate(short mobId)
+static SkillNewTemplate getSkillNewTemplate(short id)
+static HoaTieuTemplate getHoaTieuTemplate(byte id)
}
Manager --> ItemEquipTemplate
Manager --> MonsterTemplate
Manager --> SkillNewTemplate
Manager --> HoaTieuTemplate
```

**Diagram sources**
- [ItemEquipTemplate.java](file://src/main/java/template/ItemEquipTemplate.java#L1-L30)
- [MonsterTemplate.java](file://src/main/java/template/MonsterTemplate.java#L1-L31)
- [SkillNewTemplate.java](file://src/main/java/template/SkillNewTemplate.java#L1-L20)
- [HoaTieuTemplate.java](file://src/main/java/template/HoaTieuTemplate.java#L1-L19)
- [Manager.java](file://src/main/java/manager/Manager.java#L1-L1525)

**Section sources**
- [ItemEquipTemplate.java](file://src/main/java/template/ItemEquipTemplate.java#L1-L30)
- [MonsterTemplate.java](file://src/main/java/template/MonsterTemplate.java#L1-L31)
- [SkillNewTemplate.java](file://src/main/java/template/SkillNewTemplate.java#L1-L20)
- [HoaTieuTemplate.java](file://src/main/java/template/HoaTieuTemplate.java#L1-L19)
- [Manager.java](file://src/main/java/manager/Manager.java#L1-L1525)

### Data Lifecycle and Caching
The data lifecycle begins with template data stored in the database, which is loaded into memory at server startup by the Manager class. Player data follows a different pattern, with persistent storage in the database and in-memory representation during gameplay.

```mermaid
sequenceDiagram
participant Server as "Server Startup"
participant Manager as "Manager"
participant ReadData as "ReadData"
participant HikariCP as "HikariCP"
participant MySQL as "MySQL Database"
Server->>Manager : init()
Manager->>HikariCP : executeQuery("SELECT * FROM monsters")
HikariCP->>MySQL : SQL Query
MySQL-->>HikariCP : ResultSet
HikariCP-->>Manager : ResultSetImpl
Manager->>Manager : Create MonsterTemplate objects
Manager->>Manager : Store in MOB_TEMPLATES map
Manager->>HikariCP : executeQuery("SELECT * FROM item_equipment")
HikariCP->>MySQL : SQL Query
MySQL-->>HikariCP : ResultSet
HikariCP-->>Manager : ResultSetImpl
Manager->>Manager : Create ItemEquipTemplate objects
Manager->>Manager : Store in ITEM_EQUIPMENTS map
loop For each template type
Manager->>HikariCP : executeQuery(template-specific SQL)
HikariCP->>MySQL : SQL Query
MySQL-->>HikariCP : ResultSet
HikariCP-->>Manager : ResultSetImpl
Manager->>Manager : Create template objects
Manager->>Manager : Store in appropriate map
end
Manager-->>Server : Initialization complete
```

**Diagram sources**
- [Manager.java](file://src/main/java/manager/Manager.java#L1-L1525)
- [ReadData.java](file://src/main/java/server/ReadData.java#L1-L792)
- [HikariCP.java](file://src/main/java/database/HikariCP.java#L1-L124)

**Section sources**
- [Manager.java](file://src/main/java/manager/Manager.java#L1-L1525)

## Dependency Analysis
The data management components have well-defined dependencies that follow a clear hierarchy. The HikariCP connection pool is a foundational dependency used by both PlayerDAO and Manager. PlayerDAO depends on template classes to properly instantiate player items and equipment. The Manager class serves as a central registry for all template types and is depended upon by various service classes.

```mermaid
graph TD
HikariCP["HikariCP\n(Connection Pool)"]
PlayerDAO["PlayerDAO\n(Data Access)"]
Manager["Manager\n(Template Registry)"]
ReadData["ReadData\n(Template Loader)"]
ItemEquipTemplate["ItemEquipTemplate"]
MonsterTemplate["MonsterTemplate"]
SkillNewTemplate["SkillNewTemplate"]
HoaTieuTemplate["HoaTieuTemplate"]
HikariCP --> PlayerDAO
HikariCP --> Manager
HikariCP --> ReadData
ReadData --> Manager
Manager --> ItemEquipTemplate
Manager --> MonsterTemplate
Manager --> SkillNewTemplate
Manager --> HoaTieuTemplate
PlayerDAO --> ItemEquipTemplate
PlayerDAO --> MonsterTemplate
PlayerDAO --> SkillNewTemplate
PlayerDAO --> HoaTieuTemplate
style HikariCP fill:#f9f,stroke:#333
style PlayerDAO fill:#f9f,stroke:#333
style Manager fill:#bbf,stroke:#333
style ReadData fill:#bbf,stroke:#333
```

**Diagram sources**
- [HikariCP.java](file://src/main/java/database/HikariCP.java)
- [PlayerDAO.java](file://src/main/java/daos/PlayerDAO.java)
- [Manager.java](file://src/main/java/manager/Manager.java)
- [ReadData.java](file://src/main/java/server/ReadData.java)

**Section sources**
- [HikariCP.java](file://src/main/java/database/HikariCP.java)
- [PlayerDAO.java](file://src/main/java/daos/PlayerDAO.java)
- [Manager.java](file://src/main/java/manager/Manager.java)

## Performance Considerations
The data management system includes several performance optimizations to ensure efficient operation under load. These include connection pooling configuration, prepared statement caching, and in-memory storage of frequently accessed template data.

### Connection Pool Tuning
The HikariCP connection pool is configured with parameters that balance resource usage and performance:

```mermaid
flowchart TD
Start["Connection Pool Configuration"] --> MinIdle["Minimum Idle: 5"]
Start --> MaxPool["Maximum Pool Size: 10"]
Start --> ConnTimeout["Connection Timeout: 30,000ms"]
Start --> IdleTimeout["Idle Timeout: 60,000ms"]
Start --> MaxLifetime["Max Lifetime: 1,800,000ms"]
Start --> StmtCache["Statement Caching: Enabled"]
Start --> PrepStmtCacheSize["Prepared Statement Cache Size: 250"]
Start --> PrepStmtCacheSqlLimit["SQL Limit: 2,048"]
MinIdle --> Benefits["Benefits"]
MaxPool --> Benefits
ConnTimeout --> Benefits
IdleTimeout --> Benefits
MaxLifetime --> Benefits
StmtCache --> Benefits
PrepStmtCacheSize --> Benefits
PrepStmtCacheSqlLimit --> Benefits
Benefits --> ReducedLatency["Reduced connection latency"]
Benefits --> ResourceEfficiency["Efficient resource usage"]
Benefits --> QueryPerformance["Improved query performance"]
Benefits --> ConnectionStability["Stable connection handling"]
```

**Diagram sources**
- [HikariCP.java](file://src/main/java/database/HikariCP.java#L1-L124)

**Section sources**
- [HikariCP.java](file://src/main/java/database/HikariCP.java#L1-L124)

### Template Loading Optimization
The template loading process is optimized to minimize database queries and maximize in-memory access speed:

```mermaid
flowchart TD
Start["Template Loading Process"] --> SingleQuery["Single query per template type"]
SingleQuery --> BulkLoad["Bulk load all records"]
BulkLoad --> InMemory["Store in ConcurrentHashMap"]
InMemory --> IndexedAccess["Indexed access by ID"]
IndexedAccess --> NoDbLookup["No database lookup during gameplay"]
SingleQuery --> Benefit1["Reduced round trips"]
BulkLoad --> Benefit2["Efficient memory usage"]
InMemory --> Benefit3["Thread-safe access"]
IndexedAccess --> Benefit4["O(1) lookup time"]
NoDbLookup --> Benefit5["Reduced database load"]
```

**Diagram sources**
- [Manager.java](file://src/main/java/manager/Manager.java#L1-L1525)

**Section sources**
- [Manager.java](file://src/main/java/manager/Manager.java#L1-L1525)

## Troubleshooting Guide
This section provides guidance for common issues related to data management and their solutions.

### Database Connection Issues
When experiencing database connection problems, check the following:

```mermaid
flowchart TD
Problem["Connection Issues"] --> CheckConfig["Verify database configuration in Settings"]
CheckConfig --> CheckHost["Check HOST, DATABASE, USER, PASS values"]
CheckHost --> CheckNetwork["Verify network connectivity to database server"]
CheckNetwork --> CheckPool["Check HikariCP pool settings"]
CheckPool --> CheckMinIdle["Verify minimumIdle setting"]
CheckPool --> CheckMaxSize["Verify maximumPoolSize setting"]
CheckPool --> CheckTimeouts["Check connectionTimeout and idleTimeout"]
CheckTimeouts --> Logs["Examine error logs for specific messages"]
Logs --> Solution["Apply appropriate solution based on error type"]
```

**Section sources**
- [HikariCP.java](file://src/main/java/database/HikariCP.java#L1-L124)
- [Settings.java](file://src/main/java/manager/Settings.java)

### Template Loading Failures
If templates fail to load properly, investigate these areas:

```mermaid
flowchart TD
Problem["Template Loading Failure"] --> CheckQuery["Verify SQL query syntax"]
CheckQuery --> CheckTable["Confirm table exists in database"]
CheckTable --> CheckColumns["Validate column names and types"]
CheckColumns --> CheckData["Examine data for JSON formatting issues"]
CheckData --> CheckMemory["Verify sufficient memory for template storage"]
CheckMemory --> CheckConcurrency["Ensure thread-safe access to template maps"]
CheckConcurrency --> Logs["Review initialization logs for errors"]
Logs --> Solution["Implement fix based on root cause"]
```

**Section sources**
- [Manager.java](file://src/main/java/manager/Manager.java#L1-L1525)
- [ReadData.java](file://src/main/java/server/ReadData.java#L1-L792)

## Conclusion
The data management system provides a robust foundation for the game server's persistence and configuration needs. The HikariCP connection pool ensures efficient database access with optimized settings for connection reuse and statement caching. The PlayerDAO class implements reliable data access patterns for player information, handling the complex serialization of player state to and from the database.

The template system architecture effectively separates game configuration from runtime instances, loading all template data into memory at startup for fast access during gameplay. This approach eliminates the need for database queries during normal operation, significantly improving performance. The Manager class serves as a central registry for all template types, providing indexed access to game entity definitions.

Performance considerations have been addressed through careful configuration of the connection pool, optimization of database queries, and efficient in-memory storage of template data. The system is designed to handle the demands of a multiplayer game environment with multiple concurrent players accessing and modifying data.

Overall, the data management implementation demonstrates a well-architected approach to handling both persistent player data and static game configuration, providing a solid foundation for the game server's operation.