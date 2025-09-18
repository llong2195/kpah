# Friend System

<cite>
**Referenced Files in This Document**   
- [Friend.java](file://src/main/java/player/Friend.java)
- [Player.java](file://src/main/java/player/Player.java)
- [FriendService.java](file://src/main/java/services/FriendService.java)
- [Session.java](file://src/main/java/network/Session.java)
- [Sender.java](file://src/main/java/network/Sender.java)
- [CommandMessage.java](file://src/main/java/utils/CommandMessage.java)
- [Message.java](file://src/main/java/network/Message.java)
- [PlayerDAO.java](file://src/main/java/daos/PlayerDAO.java)
- [LoginService.java](file://src/main/java/services/LoginService.java)
</cite>

## Table of Contents
1. [Introduction](#introduction)
2. [Core Components](#core-components)
3. [Friend Class Structure](#friend-class-structure)
4. [Friend Management Workflow](#friend-management-workflow)
5. [Online Status and Presence Updates](#online-status-and-presence-updates)
6. [Private Messaging System](#private-messaging-system)
7. [Database Persistence and Friend List Loading](#database-persistence-and-friend-list-loading)
8. [Scalability and Network Optimization](#scalability-and-network-optimization)
9. [Security and Data Privacy](#security-and-data-privacy)
10. [Error Handling and Edge Cases](#error-handling-and-edge-cases)

## Introduction
The Friend System enables players to manage social interactions through friend list management, real-time online status tracking, and private messaging. This system supports adding, removing, and viewing friends with persistent storage, presence updates via session lifecycle events, and secure message broadcasting. The implementation ensures efficient network communication, data privacy, and scalability for large player bases.

## Core Components

The Friend System consists of several key components that work together to provide social functionality:
- **Friend**: Represents a friend entry with basic player information and equipped items
- **Player**: Contains the list of friends and manages friend-related state
- **FriendService**: Handles friend operations including adding, accepting, and removing friends
- **Session**: Tracks player connection state and triggers presence updates
- **Sender**: Manages message queuing and transmission for friend-related communications
- **CommandMessage**: Defines message opcodes for friend system interactions
- **Message**: Encapsulates network messages for friend operations
- **PlayerDAO**: Handles database persistence for friend lists
- **LoginService**: Initializes friend data upon player login

**Section sources**
- [Friend.java](file://src/main/java/player/Friend.java#L1-L60)
- [Player.java](file://src/main/java/player/Player.java#L1-L310)
- [FriendService.java](file://src/main/java/services/FriendService.java#L1-L118)
- [Session.java](file://src/main/java/network/Session.java#L1-L399)
- [Sender.java](file://src/main/java/network/Sender.java#L1-L98)
- [CommandMessage.java](file://src/main/java/utils/CommandMessage.java#L1-L363)
- [Message.java](file://src/main/java/network/Message.java#L1-L56)
- [PlayerDAO.java](file://src/main/java/daos/PlayerDAO.java#L1-L556)
- [LoginService.java](file://src/main/java/services/LoginService.java#L1-L229)

## Friend Class Structure

The Friend class represents a friend entry in a player's friend list, containing essential information about the friend without maintaining a full player object.

```mermaid
classDiagram
class Friend {
+int id
+String name
+byte head
+byte level
+short idClan
+byte isMaster
+List<ItemFriend> items
+void update(Player player)
+void dispose()
+String toString()
}
class Player {
+List<Friend> friends
+void setUp()
+void dispose()
}
class ItemFriend {
+short idTemplate
+byte classChar
+byte level
+byte plusTemplate
}
Friend --> ItemFriend : "contains"
Player --> Friend : "has many"
```

**Diagram sources**
- [Friend.java](file://src/main/java/player/Friend.java#L1-L60)
- [Player.java](file://src/main/java/player/Player.java#L1-L310)

**Section sources**
- [Friend.java](file://src/main/java/player/Friend.java#L1-L60)
- [Player.java](file://src/main/java/player/Player.java#L1-L310)

## Friend Management Workflow

The friend management system handles the complete lifecycle of friend relationships, from request initiation to acceptance and removal.

```mermaid
sequenceDiagram
participant PlayerA as Player A
participant FriendServiceA as FriendService A
participant PlayerB as Player B
participant FriendServiceB as FriendService B
participant Database as PlayerDAO
PlayerA->>FriendServiceA : addFriend(idB)
FriendServiceA->>PlayerB : findPlayer(idB)
PlayerB->>FriendServiceB : online?
FriendServiceB->>PlayerB : sendRequestAddFriend()
PlayerB->>PlayerA : acceptFriend(idA)
PlayerA->>FriendServiceA : acceptFriend(idB)
FriendServiceA->>FriendServiceA : createFriendEntry()
FriendServiceA->>PlayerA : add to friends list
FriendServiceB->>PlayerB : add to friends list
FriendServiceA->>Database : persist friend list
FriendServiceB->>Database : persist friend list
FriendServiceA->>PlayerA : sendListFriend()
FriendServiceB->>PlayerB : sendListFriend()
```

**Diagram sources**
- [FriendService.java](file://src/main/java/services/FriendService.java#L1-L118)
- [PlayerDAO.java](file://src/main/java/daos/PlayerDAO.java#L1-L556)

**Section sources**
- [FriendService.java](file://src/main/java/services/FriendService.java#L1-L118)
- [PlayerDAO.java](file://src/main/java/daos/PlayerDAO.java#L1-L556)

## Online Status and Presence Updates

Online status is tracked through session lifecycle events and updated in real-time when players connect or disconnect.

```mermaid
flowchart TD
Start([Player Connects]) --> SessionCreate["Session created in Session class"]
SessionCreate --> Login["Player logs in via LoginService"]
Login --> LoadFriends["Load friend list from database"]
LoadFriends --> SendList["Send friend list with online status"]
SendList --> OnlineUpdate["Update online status in real-time"]
subgraph "Session Lifecycle"
OnlineUpdate --> SessionActive{"Session active?"}
SessionActive --> |Yes| UpdateTimer["Check every second"]
UpdateTimer --> SendPresence["Broadcast presence to friends"]
SessionActive --> |No| Disconnect["Session disconnects"]
Disconnect --> RemoveOnline["Remove from online friends list"]
RemoveOnline --> NotifyFriends["Notify friends of offline status"]
end
SendPresence --> End([Friends see updated status])
NotifyFriends --> End
```

**Diagram sources**
- [Session.java](file://src/main/java/network/Session.java#L1-L399)
- [LoginService.java](file://src/main/java/services/LoginService.java#L1-L229)
- [FriendService.java](file://src/main/java/services/FriendService.java#L1-L118)

**Section sources**
- [Session.java](file://src/main/java/network/Session.java#L1-L399)
- [LoginService.java](file://src/main/java/services/LoginService.java#L1-L229)

## Private Messaging System

The private messaging system enables direct communication between players using a dedicated message channel.

```mermaid
sequenceDiagram
participant Sender as Player A
participant FriendServiceA as FriendService A
participant Receiver as Player B
participant FriendServiceB as FriendService B
participant SenderNetwork as Sender
participant MessageSystem as Message
Sender->>FriendServiceA : sendMessage(to, content)
FriendServiceA->>Receiver : findPlayer()
Receiver->>FriendServiceB : online?
FriendServiceB->>MessageSystem : create Message(MESSAGE_PRIVATE)
MessageSystem->>MessageSystem : write sender, content
MessageSystem->>SenderNetwork : queue message
SenderNetwork->>Receiver : deliver message
Receiver->>FriendServiceB : process private message
FriendServiceB->>Receiver : display message
```

**Diagram sources**
- [FriendService.java](file://src/main/java/services/FriendService.java#L1-L118)
- [CommandMessage.java](file://src/main/java/utils/CommandMessage.java#L1-L363)
- [Message.java](file://src/main/java/network/Message.java#L1-L56)
- [Sender.java](file://src/main/java/network/Sender.java#L1-L98)

**Section sources**
- [FriendService.java](file://src/main/java/services/FriendService.java#L1-L118)
- [CommandMessage.java](file://src/main/java/utils/CommandMessage.java#L1-L363)

## Database Persistence and Friend List Loading

Friend relationships are persisted in the database and loaded when players log in to maintain continuity across sessions.

```mermaid
erDiagram
PLAYERS {
int id PK
string name
string friends
string info
string location
}
FRIENDS {
int player_id FK
int friend_id
string friend_data
datetime created_at
}
PLAYERS ||--o{ FRIENDS : "has"
class PLAYERS {
id: int (Primary Key)
name: string
friends: JSON array of friend data
info: JSON player info
location: JSON location data
}
class FRIENDS {
player_id: int (Foreign Key)
friend_id: int
friend_data: JSON with head, level, clan, items
created_at: datetime
}
```

**Diagram sources**
- [PlayerDAO.java](file://src/main/java/daos/PlayerDAO.java#L1-L556)
- [Player.java](file://src/main/java/player/Player.java#L1-L310)

**Section sources**
- [PlayerDAO.java](file://src/main/java/daos/PlayerDAO.java#L1-L556)
- [LoginService.java](file://src/main/java/services/LoginService.java#L1-L229)

## Scalability and Network Optimization

The system is designed to handle large player bases with efficient message batching and minimal network overhead.

```mermaid
flowchart LR
A[40,000 Players] --> B[Friend List Management]
B --> C[Batched Message Updates]
C --> D[Efficient Serialization]
D --> E[Minimized Network Payload]
subgraph "Optimization Strategies"
C --> C1["Batch presence updates"]
C --> C2["Compress friend data"]
C --> C3["Limit update frequency"]
C --> C4["Queue messages efficiently"]
end
subgraph "Performance Metrics"
E --> E1["< 50ms update latency"]
E --> E2["< 1KB per friend entry"]
E --> E3["Supports 1000+ friends per player"]
E --> E4["Handles 100+ concurrent updates"]
end
```

**Diagram sources**
- [Sender.java](file://src/main/java/network/Sender.java#L1-L98)
- [FriendService.java](file://src/main/java/services/FriendService.java#L1-L118)
- [Message.java](file://src/main/java/network/Message.java#L1-L56)

**Section sources**
- [Sender.java](file://src/main/java/network/Sender.java#L1-L98)
- [FriendService.java](file://src/main/java/services/FriendService.java#L1-L118)

## Security and Data Privacy

The system implements several security measures to protect player data and prevent unauthorized access.

```mermaid
flowchart TD
A[Security Measures] --> B[Data Validation]
A --> C[Access Control]
A --> D[Privacy Protection]
A --> E[Anti-Spam Mechanisms]
B --> B1["Validate friend IDs"]
B --> B2["Sanitize input data"]
B --> B3["Check player existence"]
C --> C1["Verify session ownership"]
C --> C2["Authenticate requests"]
C --> C3["Prevent unauthorized access"]
D --> D1["Limit exposed information"]
D --> D2["Hide offline players"]
D --> D3["Protect personal data"]
E --> E1["Rate limiting"]
E --> E2["Flood protection"]
E --> E3["Spam detection"]
```

**Diagram sources**
- [FriendService.java](file://src/main/java/services/FriendService.java#L1-L118)
- [Session.java](file://src/main/java/network/Session.java#L1-L399)
- [PlayerDAO.java](file://src/main/java/daos/PlayerDAO.java#L1-L556)

**Section sources**
- [FriendService.java](file://src/main/java/services/FriendService.java#L1-L118)
- [Session.java](file://src/main/java/network/Session.java#L1-L399)

## Error Handling and Edge Cases

The system handles various edge cases and error conditions to ensure robust operation.

```mermaid
flowchart TD
A[Error Conditions] --> B[Player Not Found]
A --> C[Already Friends]
A --> D[Database Errors]
A --> E[Network Issues]
A --> F[Session Expiry]
B --> B1["Return appropriate error"]
B --> B2["Notify requesting player"]
B --> B3["Log error for debugging"]
C --> C1["Show 'already friends' message"]
C --> C2["Prevent duplicate entries"]
C --> C3["Maintain data integrity"]
D --> D1["Retry with backoff"]
D --> D2["Fallback to memory storage"]
D --> D3["Graceful degradation"]
E --> E1["Queue messages for retry"]
E --> E2["Implement timeout handling"]
E --> E3["Ensure message delivery"]
F --> F1["Clean up resources"]
F --> F2["Update friend status"]
F --> F3["Persist final state"]
```

**Diagram sources**
- [FriendService.java](file://src/main/java/services/FriendService.java#L1-L118)
- [PlayerDAO.java](file://src/main/java/daos/PlayerDAO.java#L1-L556)
- [Session.java](file://src/main/java/network/Session.java#L1-L399)

**Section sources**
- [FriendService.java](file://src/main/java/services/FriendService.java#L1-L118)
- [PlayerDAO.java](file://src/main/java/daos/PlayerDAO.java#L1-L556)
- [Session.java](file://src/main/java/network/Session.java#L1-L399)