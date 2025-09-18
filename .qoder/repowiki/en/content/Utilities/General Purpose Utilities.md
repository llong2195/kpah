# General Purpose Utilities

<cite>
**Referenced Files in This Document**   
- [Util.java](file://src/main/java/utils/Util.java)
- [NumericStringComparator.java](file://src/main/java/utils/NumericStringComparator.java)
</cite>

## Table of Contents
1. [Introduction](#introduction)
2. [Core Utility Functions](#core-utility-functions)
3. [NumericStringComparator for Natural Sorting](#numericstringcomparator-for-natural-sorting)
4. [Usage in Key Game Systems](#usage-in-key-game-systems)
5. [Performance and Thread Safety](#performance-and-thread-safety)
6. [Recommendations and Best Practices](#recommendations-and-best-practices)

## Introduction
The `Util` and `NumericStringComparator` classes in the `utils` package provide essential general-purpose functions used throughout the game server codebase. These utilities support critical operations such as random number generation, string manipulation, time formatting, data conversion, and natural sorting of alphanumeric strings. This document details their implementation, usage patterns, performance characteristics, and best practices for integration within high-frequency game systems.

## Core Utility Functions

The `Util.java` class serves as a central repository for commonly used utility methods across the game server. Key functional categories include:

### Random Number Generation
The class provides thread-safe random number generation using a shared `Random` instance:
- `nextInt(int max)` and `nextInt(int from, int to)` for integer generation
- `nextDouble(double max)` and `nextDouble(double from, to)` for floating-point values
- `isTrue(double ratio, double typeRatio)` for probabilistic checks used in gameplay mechanics

These methods are optimized for frequent calls in game loops, such as determining skill success rates or loot drops.

### String Manipulation
String utilities include:
- `capitalizeFirstLetter(String)` for proper name formatting
- `removeSpecialCharacters(String)` for sanitizing player input by replacing spaces with underscores and removing non-alphanumeric characters
- `removeControlCharacters(String)` to eliminate control characters from strings
- Vietnamese character normalization using `FROM_CHARS` and `TO_CHARS` arrays to convert accented characters to their ASCII equivalents

### Time and Date Formatting
Time-related utilities support:
- `convertTimeToString(long)` for formatting timestamps in "dd/MM/yyyy HH:mm:ss" format
- `getCurrentDateTimeInVietnam()` for retrieving current time in Asia/Ho_Chi_Minh timezone
- Duration calculations via `getDayDifference`, `getMinutesDifference`, and `getSecondDifference`

### Data Conversion and Processing
Key data methods include:
- `readFile()` variants for loading binary data from files using NIO channels
- `readFileAndSplit()` for dividing large files into two byte arrays
- `concatenate(byte[], byte[])` for combining byte arrays with length prefixes
- `getDistance()` for calculating Euclidean distance between game entities
- `sqrt(int)` for integer square root calculation using Newton's method

### Numeric and Array Utilities
Additional utilities:
- `roundNumber(long)` for rounding numbers based on magnitude
- `binarySearch(short[], short)` for efficient searching in sorted arrays
- `findIndex(byte[], byte)` for linear search in byte arrays
- `formatNumber(long)` using US locale formatting
- `addItemToMap(ConcurrentHashMap<K, List<V>>, K, V)` for thread-safe list management in concurrent maps

**Section sources**
- [Util.java](file://src/main/java/utils/Util.java#L25-L190)

## NumericStringComparator for Natural Sorting

The `NumericStringComparator` class implements natural order sorting for alphanumeric strings, addressing the limitation of lexicographic sorting where "Item10" would appear before "Item2".

### Implementation Details
The comparator uses regular expressions to identify numeric substrings:
- `PATTERN = Pattern.compile("\\d+")` matches sequences of digits
- During comparison, it finds numeric segments in both strings and compares them as integers
- If numeric values differ, their integer difference determines sort order
- Non-numeric portions are effectively ignored in the current implementation

```mermaid
classDiagram
class NumericStringComparator {
-static final Pattern PATTERN
+int compare(String s1, String s2)
}
NumericStringComparator ..|> Comparator<String>
```

**Diagram sources**
- [NumericStringComparator.java](file://src/main/java/utils/NumericStringComparator.java#L8-L28)

### Limitations
The current implementation has notable limitations:
- Only compares the first numeric sequence found in each string
- Returns 0 if no differing numeric values are found, potentially leading to inconsistent ordering
- Does not fall back to lexicographic comparison of non-numeric portions
- May not handle complex cases like multiple numeric segments (e.g., "File2a10" vs "File2a2")

**Section sources**
- [NumericStringComparator.java](file://src/main/java/utils/NumericStringComparator.java#L8-L28)

## Usage in Key Game Systems

### Player Naming and Sanitization
The string utilities are critical for player name processing:
- `removeSpecialCharacters()` ensures player names conform to naming rules by removing invalid characters
- Name normalization supports consistent player identification and display
- Used during character creation and chat message processing to maintain data integrity

### Item Sorting and Display
The `NumericStringComparator` is intended for sorting inventory items and other game objects with numeric identifiers:
- Enables natural sorting of items like "Weapon1", "Weapon2", "Weapon10"
- Improves user experience by displaying items in logical numerical order
- Potentially used in inventory management, shop displays, and leaderboard systems

### Network Packet Processing
Data conversion utilities play a vital role in network communication:
- `concatenate()` combines multiple data segments into single byte arrays for transmission
- Byte array manipulation supports efficient packet construction
- File reading utilities load binary assets for network distribution
- Used in message serialization and asset delivery systems

### Game Logic and Mechanics
Random number generation underpins core gameplay:
- `isTrue()` determines success/failure of skills, crafting, and combat actions
- Random values influence monster behavior, drop rates, and event triggers
- Distance calculations support AI pathfinding and combat range checks
- Time utilities manage cooldowns, duration effects, and session tracking

**Section sources**
- [Util.java](file://src/main/java/utils/Util.java#L190-L350)
- [NumericStringComparator.java](file://src/main/java/utils/NumericStringComparator.java#L8-L28)

## Performance and Thread Safety

### Performance Characteristics
The utility methods are optimized for high-frequency calls typical in game servers:
- Random number generation uses a single shared `Random` instance to avoid object creation overhead
- String operations minimize memory allocation through StringBuilder usage
- File I/O leverages NIO memory mapping for efficient large file reading
- Mathematical operations use optimized algorithms (e.g., Newton's method for square root)

### Thread Safety
Thread safety considerations:
- `RANDOM` is not synchronized, making `nextInt()` and `nextDouble()` not thread-safe
- `addItemToMap()` is annotated with `@Synchronized` for thread-safe concurrent map operations
- Most methods are stateless and thread-safe by nature
- String manipulation methods create new instances, avoiding shared state issues

### High-Frequency Call Optimization
Methods designed for game loop usage:
- Simple arithmetic operations without object allocation
- Pre-compiled regular expressions for pattern matching
- Reusable formatters and constants
- Efficient algorithms for distance calculation and array searching

**Section sources**
- [Util.java](file://src/main/java/utils/Util.java#L25-L200)
- [NumericStringComparator.java](file://src/main/java/utils/NumericStringComparator.java#L8-L28)

## Recommendations and Best Practices

### When to Use Built-in Java Alternatives
Consider standard library alternatives for:
- **Random generation**: Use `ThreadLocalRandom` for thread-safe operations instead of `Util.nextInt()`
- **String manipulation**: Consider `java.text.Normalizer` for Unicode normalization instead of manual character replacement
- **Time formatting**: Use `DateTimeFormatter` constants when possible for better performance
- **Collections**: Use `Collections.sort()` with `Comparator.comparing()` for simple sorting needs

### Project-Specific Utility Advantages
The current utilities provide value when:
- Consistent behavior across the codebase is required
- Specialized functionality like Vietnamese character handling is needed
- Integration with existing game systems that expect specific return formats
- Performance characteristics have been validated in production

### Suggested Improvements
Enhancements to consider:
- Fix `NumericStringComparator` to properly handle multiple numeric segments and provide consistent ordering
- Add thread-safety to random number generation methods
- Implement caching for frequently used formatters and patterns
- Add input validation and null checks to prevent runtime exceptions
- Consider replacing `readFile()` methods with try-with-resources to ensure proper stream closure

### Usage Guidelines
Best practices for developers:
- Use `Util` methods consistently across the codebase to maintain uniform behavior
- For high-concurrency scenarios, wrap random number calls with synchronization or use `ThreadLocalRandom`
- Prefer `Util` string methods when handling player-generated content
- Use `NumericStringComparator` with awareness of its current limitations
- Consider performance implications when calling utilities in tight game loops

**Section sources**
- [Util.java](file://src/main/java/utils/Util.java#L25-L410)
- [NumericStringComparator.java](file://src/main/java/utils/NumericStringComparator.java#L8-L28)