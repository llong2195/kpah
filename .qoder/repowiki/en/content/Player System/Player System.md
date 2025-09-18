# Player System

<cite>
**Referenced Files in This Document**   
- [Player.java](file://src/main/java/player/Player.java)
- [Info.java](file://src/main/java/player/Info.java)
- [Point.java](file://src/main/java/player/Point.java)
- [Inventory.java](file://src/main/java/player/Inventory.java)
- [Sundry.java](file://src/main/java/player/Sundry.java)
- [Skill.java](file://src/main/java/player/Skill.java)
- [PlayerDAO.java](file://src/main/java/daos/PlayerDAO.java)
- [AttributeConst.java](file://src/main/java/consts/AttributeConst.java)
- [Const.java](file://src/main/java/consts/Const.java)
- [Settings.java](file://src/main/java/manager/Settings.java)
</cite>

## Table of Contents
1. [Introduction](#introduction)
2. [Player Entity Structure](#player-entity-structure)
3. [Data Persistence Mechanism](#data-persistence-mechanism)
4. [Attribute System and Stat Calculation](#attribute-system-and-stat-calculation)
5. [Leveling and Experience Progression](#leveling-and-experience-progression)
6. [Player Initialization and State Management](#player-initialization-and-state-management)
7. [Client Synchronization](#client-synchronization)
8. [Common Issues and Performance Optimization](#common-issues-and-performance-optimization)
9. [Conclusion](#conclusion)

## Introduction
The Player System forms the core entity representation in the game, serving as the central data structure for character state management. This document provides a comprehensive analysis of the player entity implementation, focusing on its structural composition, data persistence, attribute calculation, leveling mechanics, and synchronization patterns. The system is designed to aggregate various components such as inventory, skills, and attributes while maintaining efficient state management and data integrity.

## Player Entity Structure

The Player class serves as the central representation of a character, aggregating multiple components that define a player's state and capabilities. The entity is composed of several key components that work together to maintain a complete player state.

```mermaid
classDiagram
class Player {
+ISession session
+int idDatabase
+short idPlayer
+boolean isNpc
+String name
+Info info
+Location location
+Point point
+Inventory inventory
+Skill skill
+Sundry sundry
+SkillBuff skillBuff
+BuffInfluencePlayer buffInfluence
+Horse horse
+Manufacture manufacture
+List<Friend> friends
+Party party
+List<Short> otherMobInside
+List<Short> otherItemMapInside
+List<Short> otherPlayerInside
+setUp() void
+injured(int, boolean, byte, boolean) int
+isPlayer() boolean
+isDie() boolean
+getHead() short
+getBody() short
+getLeg() short
+getHat() short
+getWeaponStyle() short
+getCoat() short
+updatePlayer() Runnable
+update() void
+dispose() void
}
class Info {
+byte classPlayer
+byte head
+byte gender
+byte idNation
+byte he
+byte level
+Clan clan
+short killer
+plusLevel(byte) void
+plusKiller(byte) void
+minusKiller(byte) void
}
class Point {
+Player player
+int hpMax
+int hp
+int mpMax
+int mp
+int attack
+int defend
+int defendMagic
+short percentPlusHp
+short percentPlusMp
+short hutHp
+double percentDropXu
+double percentDropEquip
+short accurate
+short dodge
+short critical
+short x2
+short hapThu
+short baoKich
+short xuyenGiap
+short expDonate
+short docTinh
+short giamStVat
+short giamStMa
+short strength
+short agility
+short spirit
+short health
+short luck
+short strengthAdd
+short agilityAdd
+short spiritAdd
+short healthAdd
+long exp
+short basePoint
+short skillPoint
+int dedicationPoint
+byte speed
+long xuRevive
+increaseSkillPoint(byte) void
+increaseBasePoint(byte, short) void
+getDameAttack(boolean, boolean, boolean, boolean) int
+initPoint() void
+resetPoint() void
+plusHp(int) void
+plusMp(int) void
+minusHp(int) void
+minusMp(int) void
+plusExp(int) void
}
class Inventory {
+int luong
+int luongKhoa
+long xu
+byte limItemBag
+short maxIdItem
+List<ItemEquip> itemBody
+List<ItemEquip> itemBag
+List<ItemEquip> itemBox
+List<ItemEquip> itemSold
+List<ItemGem> itemGem
+List<ItemGem> itemGemLock
+List<ItemPotion> itemPotion
+List<ItemQuest> itemQuest
+List<ItemAnimal> itemAnimal
+List<ItemAnimal> itemAnimalExpiry
+long[] lastTimeUsePotion
+initIdItem() void
+getPriceRepair(int) int
+plusLuong(int) boolean
+plusLuongKhoa(int) boolean
+plusXu(long) boolean
+minusLuong(int) boolean
+minusLuongKhoa(int) boolean
+minusXu(long) boolean
+isFullInventory() boolean
+fullInventory() int
+dispose() void
}
class Sundry {
+boolean isInGame
+boolean isNewlyRevived
+long lastTimeRevived
+long lastTimeDie
+byte dayCanRestore
+long lastTimeEndDelete
+long lastTimeLogout
+byte idNpcOpen
+byte selected
+byte idOpenMenu
+byte selectedOption
+boolean comeHome
+long lastTimeComeHome
+Friend clanMember
+int xuQuyenGop
+long lastTimeUpdateDatabase
+long lastTimeChat
+byte pk
+long lastTimeChangePk
+boolean isKiller
+boolean isOnBoard
+long lastTimeOnBoard
+byte indexHoaTieu
+byte idHoaTieu
+byte countDie
+int miliSecondRevive
+Player trader
+List<ItemPotion> itemPotionTrade
+boolean isConfirmTrade
+boolean isFinishTrade
+List<ItemBuyNpc> itemNpcShop
+List<DepositeItemEquip> depositeItemEquips
+List<DepositeItemGem> depositeItemGems
+short idItemNhan
+byte npcTypeDeposite
+byte indexShopDeposite
+int priceDeposite
+short itemIdDeposite
+byte categoryDeposite
+List<Short> idDoiHe
+indexOfGem(DepositeItemGem) int
+countItemGemDeposite(short) int
+containsDepositeItemEquip(ItemEquip) boolean
+containsDepositeItemGem(DepositeItemGem) boolean
+containsDepositeItemGem(int) boolean
+findItemGemDeposite(short) DepositeItemGem
+findItemEquipDeposite(short) DepositeItemEquip
+addDepositeItemGem(DepositeItemGem) void
+addDepositeItemEquip(DepositeItemEquip) void
+removeDepositeItemGem(DepositeItemGem) void
+removeDepositeItemGem(ItemGem) void
+removeDepositeItemEquip(ItemEquip) void
+plusCountDie() void
+hasItemPotionTrade(short) boolean
+findItemPotion(short) ItemPotion
+dispose() void
}
class Skill {
+byte[] levelSkill
+long[] timeLastUseSkills
+byte typeSkill
+byte typeBuffSkill
}
Player --> Info : "has"
Player --> Location : "has"
Player --> Point : "has"
Player --> Inventory : "has"
Player --> Skill : "has"
Player --> Sundry : "has"
Player --> SkillBuff : "has"
Player --> BuffInfluencePlayer : "has"
Player --> Horse : "has"
Player --> Manufacture : "has"
Player --> Friend : "has"
Player --> Party : "has"
Point --> Player : "references"
```

**Diagram sources**
- [Player.java](file://src/main/java/player/Player.java#L1-L310)
- [Info.java](file://src/main/java/player/Info.java#L1-L64)
- [Point.java](file://src/main/java/player/Point.java#L1-L593)
- [Inventory.java](file://src/main/java/player/Inventory.java#L1-L246)
- [Sundry.java](file://src/main/java/player/Sundry.java#L1-L169)
- [Skill.java](file://src/main/java/player/Skill.java#L1-L31)

**Section sources**
- [Player.java](file://src/main/java/player/Player.java#L1-L310)
- [Info.java](file://src/main/java/player/Info.java#L1-L64)
- [Point.java](file://src/main/java/player/Point.java#L1-L593)
- [Inventory.java](file://src/main/java/player/Inventory.java#L1-L246)
- [Sundry.java](file://src/main/java/player/Sundry.java#L1-L169)
- [Skill.java](file://src/main/java/player/Skill.java#L1-L31)

## Data Persistence Mechanism

The player state persistence system is implemented through a DAO (Data Access Object) pattern that handles both loading and saving player data to a relational database. The PlayerDAO class manages the serialization and deserialization of player entities, converting complex object structures into JSON strings for database storage.

```mermaid
sequenceDiagram
participant Client as "Client"
participant Player as "Player"
participant PlayerDAO as "PlayerDAO"
participant Database as "Database (HikariCP)"
Client->>Player : createPlayer()
Player->>PlayerDAO : createPlayer(ISession, name, clazz, head, gender, idNation)
PlayerDAO->>Database : INSERT INTO players
Database-->>PlayerDAO : success
PlayerDAO->>PlayerDAO : setupPlayer(id)
PlayerDAO->>Database : SELECT * FROM players WHERE id = ?
Database-->>PlayerDAO : ResultSet
PlayerDAO->>PlayerDAO : buildInfo(), buildLocation(), buildPoint(), etc.
PlayerDAO-->>Player : Player object
Player-->>Client : Player created
Client->>Player : update state
Player->>Player : modify attributes
Player->>PlayerDAO : updatePlayer(Player)
PlayerDAO->>PlayerDAO : convert to JSON strings
PlayerDAO->>Database : UPDATE players SET ... WHERE id = ?
Database-->>PlayerDAO : success
PlayerDAO-->>Player : update complete
```

The persistence mechanism uses JSON serialization to store complex nested objects as text fields in the database. Each component of the player entity (info, location, point, inventory, etc.) is converted to a JSON string representation and stored in separate columns. This approach allows for flexible schema design while maintaining relational database benefits.

**Diagram sources**
- [PlayerDAO.java](file://src/main/java/daos/PlayerDAO.java#L1-L556)
- [Player.java](file://src/main/java/player/Player.java#L1-L310)

**Section sources**
- [PlayerDAO.java](file://src/main/java/daos/PlayerDAO.java#L1-L556)
- [Player.java](file://src/main/java/player/Player.java#L1-L310)

## Attribute System and Stat Calculation

The attribute system implements a comprehensive stat calculation framework that combines base attributes, equipment bonuses, and skill effects. The Point class serves as the central calculation engine, aggregating various modifiers to determine final character statistics.

```mermaid
flowchart TD
Start([Stat Calculation]) --> BaseAttributes["Base Attributes (strength, agility, spirit, health, luck)"]
BaseAttributes --> ClassModifiers["Class-Based Modifiers"]
ClassModifiers --> EquipmentAttributes["Equipment Attribute Bonuses"]
EquipmentAttributes --> SkillModifiers["Skill Level Modifiers"]
SkillModifiers --> HorseModifiers["Horse and Mount Bonuses"]
HorseModifiers --> FinalStats["Final Stat Calculation"]
subgraph "Stat Components"
BaseAttributes
EquipmentAttributes
SkillModifiers
HorseModifiers
end
FinalStats --> HPMax["HP Max = (health + healthAdd) * ClassMultiplier"]
FinalStats --> MPMax["MP Max = (spirit + spiritAdd) * ClassMultiplier"]
FinalStats --> Attack["Attack = Base + Equipment + Skill + Horse"]
FinalStats --> Defend["Defend = Base + Equipment + Skill + Horse"]
FinalStats --> DefendMagic["Defend Magic = Base + Equipment + Skill + Horse"]
FinalStats --> Accurate["Accurate = Base + Equipment + Horse"]
FinalStats --> Dodge["Dodge = Base + Equipment + Horse"]
FinalStats --> Critical["Critical = luck/20 + Equipment + Horse"]
HPMax --> Output
MPMax --> Output
Attack --> Output
Defend --> Output
DefendMagic --> Output
Accurate --> Output
Dodge --> Output
Critical --> Output
Output([Final Statistics])
```

The system uses a layered approach to stat calculation, where base attributes are modified by multiple factors:
- **Base Attributes**: Strength, agility, spirit, health, and luck form the foundation of character stats
- **Equipment Bonuses**: Items in the inventory contribute additional attribute points through ItemEquip objects
- **Skill Effects**: Character class-specific skills provide percentage-based stat modifications
- **Horse/Mount Bonuses**: Mounted characters receive additional stat bonuses based on their mount type

The calculation process is triggered by the `initPoint()` method, which resets all calculated values and recomputes them based on current equipment, skills, and status effects.

**Diagram sources**
- [Point.java](file://src/main/java/player/Point.java#L1-L593)
- [AttributeConst.java](file://src/main/java/consts/AttributeConst.java#L1-L133)
- [Const.java](file://src/main/java/consts/Const.java#L1-L115)

**Section sources**
- [Point.java](file://src/main/java/player/Point.java#L1-L593)
- [AttributeConst.java](file://src/main/java/consts/AttributeConst.java#L1-L133)
- [Const.java](file://src/main/java/consts/Const.java#L1-L115)

## Leveling and Experience Progression

The leveling system implements a progressive experience-based advancement mechanism that allows players to improve their character capabilities through gameplay. The experience progression is managed through the Point class, which tracks accumulated experience points and handles level advancement.

```mermaid
stateDiagram-v2
[*] --> LevelingProcess
LevelingProcess --> ExperienceGain : "Player performs actions"
ExperienceGain --> CheckLevelUp : "plusExp(int exp)"
CheckLevelUp --> IsLevelUp? : "Determine if level threshold reached"
IsLevelUp? --> No : "Continue current level"
IsLevelUp? --> Yes : "Level up sequence"
Yes --> AwardPoints : "Grant basePoint and skillPoint"
AwardPoints --> UpdateStats : "Recalculate all character stats"
UpdateStats --> NotifyClient : "Send updated character info to client"
NotifyClient --> LevelingProcess : "Ready for next progression"
No --> LevelingProcess
```

The leveling mechanics are implemented through several key methods in the Point class:
- `plusExp(int exp)`: Adds experience points to the player's total
- `increaseBasePoint(byte type, short numIncrease)`: Allocates base points to primary attributes
- `increaseSkillPoint(byte type)`: Advances skill levels based on available skill points

When a player gains sufficient experience to level up, they are awarded both base points (for attribute improvement) and skill points (for ability enhancement). The system enforces level requirements for skill advancement through the `Manager.getLevelAddSkill(type, levelSkill)` method, ensuring balanced progression.

**Diagram sources**
- [Point.java](file://src/main/java/player/Point.java#L1-L593)
- [Info.java](file://src/main/java/player/Info.java#L1-L64)

**Section sources**
- [Point.java](file://src/main/java/player/Point.java#L1-L593)
- [Info.java](file://src/main/java/player/Info.java#L1-L64)

## Player Initialization and State Management

Player initialization and state management are handled through a comprehensive lifecycle system that ensures proper setup and cleanup of player entities. The system implements both creation and disposal patterns to maintain data integrity and resource efficiency.

```mermaid
sequenceDiagram
participant Client as "Client"
participant Player as "Player"
participant PlayerDAO as "PlayerDAO"
participant Services as "Various Services"
Client->>PlayerDAO : createPlayer()
PlayerDAO->>Database : Insert player record
Database-->>PlayerDAO : Success
PlayerDAO->>PlayerDAO : setupPlayer()
PlayerDAO->>PlayerDAO : buildInfo(), buildLocation(), etc.
PlayerDAO-->>Player : Player object
Player->>Player : setUp()
Player->>Services : Register with clan, party, map services
Player->>Player : Initialize timers and update loops
Player-->>Client : Player ready
loop Update Cycle
Player->>Player : update()
Player->>BuffInfluencePlayer : Update status effects
Player->>SkillBuff : Update active buffs
Player->>MapService : Handle auto-revive if dead
Player->>PlayerDAO : Check if time to update database
Player->>ChangeMapService : Handle board transitions
end
Client->>Player : disconnect
Player->>Player : dispose()
Player->>PartyService : Leave party if member
Player->>ClanService : Update clan membership
Player->>MapService : Exit current map
Player->>PlayerDAO : Final database update
Player->>Inventory : Clear and dispose items
Player->>Sundry : Dispose temporary data
Player-->>Client : Cleanup complete
```

The initialization process begins with player creation through the `createPlayer` method in PlayerDAO, which inserts a new record into the database with default values for all character components. Upon successful creation, the `setupPlayer` method loads the player data and constructs the complete player object with all nested components.

The `setUp()` method in the Player class performs post-creation initialization, including:
- Setting the last database update timestamp
- Initializing tracking lists for nearby entities
- Creating the manufacture system instance
- Setting up the party system
- Linking the player reference to dependent components
- Initializing the point system
- Registering with clan services if applicable

State management is handled through the `update()` method, which runs on a regular interval (approximately once per second) to:
- Update active buffs and status effects
- Handle auto-revive mechanics for low-level players
- Manage database synchronization timing
- Process map transitions

Resource cleanup is managed through the `dispose()` method, which systematically releases all references and clears data structures to prevent memory leaks.

**Diagram sources**
- [Player.java](file://src/main/java/player/Player.java#L1-L310)
- [PlayerDAO.java](file://src/main/java/daos/PlayerDAO.java#L1-L556)

**Section sources**
- [Player.java](file://src/main/java/player/Player.java#L1-L310)
- [PlayerDAO.java](file://src/main/java/daos/PlayerDAO.java#L1-L556)

## Client Synchronization

Client synchronization is implemented through a combination of periodic updates and event-driven messaging to maintain consistency between server state and client presentation. The system ensures that player state changes are properly communicated to the connected client.

The synchronization mechanism operates on multiple levels:
- **Initial Synchronization**: When a player logs in, the complete player state is sent to the client
- **Periodic Updates**: Player state is periodically saved to the database at intervals defined by `Settings.MILISECOND_UPDATE_DATABASE`
- **Event-Driven Updates**: Significant state changes trigger immediate synchronization messages
- **Final Synchronization**: Player state is saved when disconnecting

Key synchronization points include:
- Character information updates after stat or skill changes
- Inventory changes when items are added or removed
- Position updates when moving between map locations
- Status effect changes when buffs are applied or expire
- Combat state updates during battles

The system uses service classes like Service.instance to send targeted messages to clients, ensuring that only relevant information is transmitted. This approach minimizes network overhead while maintaining state consistency.

**Section sources**
- [Player.java](file://src/main/java/player/Player.java#L1-L310)
- [PlayerDAO.java](file://src/main/java/daos/PlayerDAO.java#L1-L556)
- [Settings.java](file://src/main/java/manager/Settings.java#L1-L59)

## Common Issues and Performance Optimization

The player system addresses several common issues related to data integrity, concurrency, and performance through careful design and implementation choices.

### Data Corruption Prevention
The system implements multiple safeguards against data corruption:
- **JSON Validation**: All serialized data is validated before processing
- **Null Checks**: Comprehensive null checks prevent NPEs during deserialization
- **Bounds Checking**: All numeric operations include bounds checking to prevent overflow
- **Transaction Safety**: Database operations are designed to be idempotent when possible

### Concurrency Management
Concurrency issues during stat updates are mitigated through:
- **Synchronized Methods**: Critical sections like `injured()` are marked with @Synchronized
- **Atomic Operations**: Database updates use atomic SQL operations
- **Thread-Safe Collections**: Concurrent collections are used where appropriate
- **Update Locking**: The update loop prevents concurrent modifications during processing

### Performance Optimization
Several performance optimizations are implemented for frequently accessed player data:
- **Caching**: Frequently accessed computed values are cached and only recalculated when necessary
- **Batch Updates**: Database updates are batched to reduce I/O operations
- **Lazy Loading**: Related entities are loaded only when needed
- **Object Pooling**: Temporary objects are reused where possible
- **Efficient Serialization**: JSON serialization is optimized for speed and size

The system also implements several configuration-driven optimizations:
- Database update frequency is configurable via `MILISECOND_UPDATE_DATABASE`
- Auto-revive behavior is limited to players below `LEVEL_CAN_AUTO_REVIVE`
- Session cleanup is enforced after `MILISECOND_WAIT_KICK_SESSION`
- Player cleanup occurs after `MILISECOND_WAIT_KICK_PLAYER`

These optimizations ensure that the player system can handle the maximum configured player count (`MAX_PLAYER`) while maintaining responsive performance and data integrity.

**Section sources**
- [Player.java](file://src/main/java/player/Player.java#L1-L310)
- [PlayerDAO.java](file://src/main/java/daos/PlayerDAO.java#L1-L556)
- [Settings.java](file://src/main/java/manager/Settings.java#L1-L59)

## Conclusion
The Player System represents a comprehensive implementation of character state management in a multiplayer game environment. By aggregating components like inventory, skills, and attributes into a cohesive entity, the system provides a robust foundation for player interaction and progression. The data persistence mechanism ensures reliable state storage, while the attribute calculation system enables complex stat interactions. The leveling and experience progression mechanics support long-term player engagement, and the synchronization patterns maintain consistency between server and client states. Through careful attention to data integrity, concurrency, and performance optimization, the system is designed to handle high player loads while providing a smooth gaming experience.