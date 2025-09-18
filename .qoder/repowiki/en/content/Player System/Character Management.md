# Character Management

<cite>
**Referenced Files in This Document**   
- [Player.java](file://src/main/java/player/Player.java)
- [Info.java](file://src/main/java/player/Info.java)
- [Location.java](file://src/main/java/player/Location.java)
- [PlayerDAO.java](file://src/main/java/daos/PlayerDAO.java)
- [LoginService.java](file://src/main/java/services/LoginService.java)
- [Session.java](file://src/main/java/network/Session.java)
- [Point.java](file://src/main/java/player/Point.java)
- [Inventory.java](file://src/main/java/player/Inventory.java)
- [Skill.java](file://src/main/java/player/Skill.java)
- [Sundry.java](file://src/main/java/player/Sundry.java)
</cite>

## Table of Contents
1. [Introduction](#introduction)
2. [Core Character Components](#core-character-components)
3. [Character Initialization and Login Flow](#character-initialization-and-login-flow)
4. [State Persistence and Data Synchronization](#state-persistence-and-data-synchronization)
5. [Disconnection and Cleanup Handling](#disconnection-and-cleanup-handling)
6. [Common Issues and Security Risks](#common-issues-and-security-risks)
7. [Best Practices for Character Management](#best-practices-for-character-management)
8. [Conclusion](#conclusion)

## Introduction
This document provides a comprehensive analysis of character management in the KPAH game server, focusing on the core entity Player.java and its associated components. The Player class serves as the central representation of a player's character, aggregating personal data, status, and session state throughout the game lifecycle. This documentation details the architecture, initialization process, state persistence mechanisms, and disconnection handling procedures that ensure robust character management. It also addresses common issues such as race conditions during login, session hijacking risks, and memory bloat from inactive player instances, providing best practices for extending character attributes and optimizing load times.

## Core Character Components

The character management system in KPAH is built around several core components that work together to represent and manage player characters. The Player.java class serves as the primary entity, aggregating various sub-components that handle specific aspects of character data and behavior.

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
+toString() String
}
class Location {
+short x
+short y
+byte mapVillage
+byte inCountry
+short lastX
+short lastY
+boolean stopCollectMessageMove
+Zone zone
+Zone lastZone
+dispose() void
+toString() String
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
+toString() String
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
+toString() String
}
class Skill {
+byte[] levelSkill
+long[] timeLastUseSkills
+byte typeSkill
+byte typeBuffSkill
+toString() String
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
```

**Diagram sources**
- [Player.java](file://src/main/java/player/Player.java#L1-L310)
- [Info.java](file://src/main/java/player/Info.java#L1-L64)
- [Location.java](file://src/main/java/player/Location.java#L1-L48)
- [Point.java](file://src/main/java/player/Point.java#L1-L593)
- [Inventory.java](file://src/main/java/player/Inventory.java#L1-L246)
- [Skill.java](file://src/main/java/player/Skill.java#L1-L31)
- [Sundry.java](file://src/main/java/player/Sundry.java#L1-L169)

**Section sources**
- [Player.java](file://src/main/java/player/Player.java#L1-L310)
- [Info.java](file://src/main/java/player/Info.java#L1-L64)
- [Location.java](file://src/main/java/player/Location.java#L1-L48)

## Character Initialization and Login Flow

The character initialization process in KPAH begins with the login sequence, where player data is loaded from the database and the Player object is constructed. The PlayerDAO.setupPlayer method is responsible for creating a Player instance by retrieving data from the database and populating the various components.

```mermaid
sequenceDiagram
participant Client as "Client"
participant Session as "Session"
participant LoginService as "LoginService"
participant PlayerDAO as "PlayerDAO"
participant Database as "Database"
Client->>Session : Send login credentials
Session->>LoginService : loginAccount(Message)
LoginService->>Database : Query user credentials
Database-->>LoginService : User data
LoginService->>PlayerDAO : setupPlayer(idSelect)
PlayerDAO->>Database : SELECT players data
Database-->>PlayerDAO : Player data
PlayerDAO->>PlayerDAO : buildInfo(infoString)
PlayerDAO->>PlayerDAO : buildLocation(locationString)
PlayerDAO->>PlayerDAO : buildPoint(pointString)
PlayerDAO->>PlayerDAO : buildInventory(inventoryString)
PlayerDAO->>PlayerDAO : buildSkill(skillString)
PlayerDAO->>PlayerDAO : buildHorse(horseString)
PlayerDAO->>PlayerDAO : buildListFriend(friendString)
PlayerDAO-->>LoginService : Player object
LoginService->>Session : setPlayer(Player)
Session->>Player : setUp()
Player->>Player : Initialize sundry, lists, manufacture, party
Player->>Player : Set point.player, skillBuff.player, buffInfluence.player
Player->>Player : Initialize point values
Player->>Clan : Add member to clan if exists
Player->>Sundry : Set killer status if killer > 0
LoginService->>Player : sendDataWhenLogin()
Player->>ExecutorVirtualThread : submitThreadPlayer(updatePlayer())
LoginService->>MapService : changeMap(Player, zone, x, y)
Session-->>Client : Login successful
```

**Diagram sources**
- [PlayerDAO.java](file://src/main/java/daos/PlayerDAO.java#L1-L556)
- [LoginService.java](file://src/main/java/services/LoginService.java#L1-L229)
- [Session.java](file://src/main/java/network/Session.java#L1-L399)
- [Player.java](file://src/main/java/player/Player.java#L1-L310)

**Section sources**
- [PlayerDAO.java](file://src/main/java/daos/PlayerDAO.java#L1-L556)
- [LoginService.java](file://src/main/java/services/LoginService.java#L1-L229)
- [Session.java](file://src/main/java/network/Session.java#L1-L399)

## State Persistence and Data Synchronization

The KPAH game server implements a robust state persistence mechanism to ensure that player data is consistently saved to the database. The PlayerDAO.updatePlayer method is responsible for persisting player state, which is called periodically during gameplay and at critical moments such as character logout or disconnection.

```mermaid
flowchart TD
A[Player Update Loop] --> B{Is player connected?}
B --> |Yes| C[Execute player update]
C --> D[Update buffInfluence]
D --> E[Update skillBuff]
E --> F{Is player dead?}
F --> |Yes| G{Can auto-revive?}
G --> |Yes| H[Revive player]
G --> |No| I[Send revive countdown]
F --> |No| J{Time to update database?}
J --> |Yes| K[Call PlayerDAO.updatePlayer]
K --> L[Convert components to JSON strings]
L --> M[Build SQL UPDATE statement]
M --> N[Execute database update]
N --> O[Update lastTimeUpdateDatabase]
J --> |No| P[Continue loop]
B --> |No| Q[End update loop]
style K fill:#f9f,stroke:#333,stroke-width:2px
style L fill:#f9f,stroke:#333,stroke-width:2px
style M fill:#f9f,stroke:#333,stroke-width:2px
style N fill:#f9f,stroke:#333,stroke-width:2px
```

The updatePlayer method in the Player class runs on a separate thread and is responsible for managing the player's state updates. It calls the update method periodically, which in turn checks various conditions and triggers appropriate actions. The update method handles buff and skill updates, auto-revive logic for dead players, and database persistence when the configured time interval has elapsed.

**Diagram sources**
- [Player.java](file://src/main/java/player/Player.java#L1-L310)
- [PlayerDAO.java](file://src/main/java/daos/PlayerDAO.java#L1-L556)

**Section sources**
- [Player.java](file://src/main/java/player/Player.java#L1-L310)
- [PlayerDAO.java](file://src/main/java/daos/PlayerDAO.java#L1-L556)

## Disconnection and Cleanup Handling

Proper disconnection and cleanup handling is critical for maintaining server stability and preventing resource leaks. The KPAH game server implements a comprehensive cleanup process that ensures all player-related resources are properly released when a player disconnects.

```mermaid
sequenceDiagram
participant Player as "Player"
participant Session as "Session"
participant ClientManager as "ClientManager"
participant MapService as "MapService"
participant PartyService as "PartyService"
participant TradeService as "TradeService"
participant VongQuay as "VongQuay"
Session->>Player : disconnect()
Player->>Player : dispose()
Player->>PartyService : leaverParty(Player)
alt Leader is leaving
PartyService->>PartyService : disbandParty(Player, true)
end
Player->>VongQuay : disposePlayer(Player)
Player->>TradeService : cancelTrade(Player)
Player->>Clan : removeMemberOnGame(Player)
Player->>MapService : exitMap(Player)
Player->>PlayerDAO : updatePlayer(Player)
Player->>Player : Clear and nullify collections
Player->>Player : Dispose of inventory, location, buffInfluence, horse, manufacture
Player->>Sundry : Dispose of sundry
Player->>ClientManager : kickClient(Session)
Session->>Session : Close socket and streams
Session->>Session : Dispose of sender and collector
Session->>Session : Nullify references
```

The dispose method in the Player class is responsible for cleaning up all player-related resources. It handles party management by removing the player from their party or disbanding the party if the player is the leader. It also cancels any ongoing trades, removes the player from their clan, exits the current map, and updates the player's data in the database. After these operations, it clears and nullifies all collections and disposes of sub-components to prevent memory leaks.

**Diagram sources**
- [Player.java](file://src/main/java/player/Player.java#L1-L310)
- [Session.java](file://src/main/java/network/Session.java#L1-L399)
- [PlayerDAO.java](file://src/main/java/daos/PlayerDAO.java#L1-L556)

**Section sources**
- [Player.java](file://src/main/java/player/Player.java#L1-L310)
- [Session.java](file://src/main/java/network/Session.java#L1-L399)

## Common Issues and Security Risks

The KPAH game server faces several common issues and security risks related to character management that must be addressed to ensure a stable and secure gaming environment.

### Race Conditions During Login
One potential race condition occurs during the login process when a player attempts to log in while already logged in from another session. The server handles this by checking if the player is already in the ClientManager's player list. If the player is found, the server disconnects both the current session and the existing session to prevent multiple instances of the same character.

```mermaid
flowchart TD
A[Player attempts login] --> B{Player already in ClientManager?}
B --> |Yes| C[Disconnect existing session]
C --> D[Disconnect current session]
C --> E[Send "account logged in elsewhere" message]
B --> |No| F[Proceed with login process]
F --> G[Add player to ClientManager]
G --> H[Complete login]
```

### Session Hijacking Risks
Session hijacking is mitigated through several mechanisms. The server verifies the user ID and username when processing character selection, ensuring that players can only access characters associated with their account. Additionally, the server checks for existing sessions with the same user ID and disconnects them if found, preventing concurrent logins.

### Memory Bloat from Inactive Player Instances
Memory bloat can occur if player instances are not properly cleaned up after disconnection. The server addresses this through the comprehensive dispose method in the Player class, which clears all collections, disposes of sub-components, and nullifies references. The ClientManager also removes disconnected players from its tracking lists to prevent memory leaks.

**Section sources**
- [LoginService.java](file://src/main/java/services/LoginService.java#L1-L229)
- [Session.java](file://src/main/java/network/Session.java#L1-L399)
- [Player.java](file://src/main/java/player/Player.java#L1-L310)

## Best Practices for Character Management

To ensure optimal performance and maintainability of the character management system in KPAH, several best practices should be followed when extending character attributes and optimizing load times.

### Extending Character Attributes
When adding new character attributes, consider the following guidelines:
- Add new fields to the appropriate component class (Info, Point, Inventory, etc.) based on the attribute type
- Ensure proper JSON serialization by updating the toString method in the component class
- If the attribute affects gameplay mechanics, update the relevant calculation methods in Point.java
- For persistent attributes, ensure they are included in the database schema and PlayerDAO methods

### Optimizing Load Times
To optimize character load times, consider the following strategies:
- Implement lazy loading for non-essential data that can be loaded after the initial character setup
- Optimize database queries by using indexed columns and minimizing the amount of data retrieved
- Consider caching frequently accessed data that doesn't change often
- Batch database operations when possible to reduce round-trip times

### Performance Monitoring
Implement monitoring to track character management performance:
- Log the time taken for player initialization and database operations
- Monitor memory usage to detect potential leaks
- Track the number of active player instances to identify scaling issues

### Error Handling
Robust error handling is essential for maintaining server stability:
- Implement comprehensive exception handling in database operations
- Validate data integrity when loading player data
- Provide meaningful error messages for debugging purposes
- Implement retry mechanisms for transient database errors

**Section sources**
- [Player.java](file://src/main/java/player/Player.java#L1-L310)
- [PlayerDAO.java](file://src/main/java/daos/PlayerDAO.java#L1-L556)
- [LoginService.java](file://src/main/java/services/LoginService.java#L1-L229)

## Conclusion
The character management system in the KPAH game server is a comprehensive and well-structured implementation that effectively handles player character representation, initialization, state persistence, and cleanup. The Player.java class serves as the central entity, aggregating various components that manage specific aspects of character data and behavior. The system demonstrates robust handling of the login process, state persistence, and disconnection cleanup, while addressing common issues such as race conditions and security risks. By following the best practices outlined in this document, developers can extend and optimize the character management system to meet evolving game requirements while maintaining performance and stability.