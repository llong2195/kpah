# Attributes and Statistics

<cite>
**Referenced Files in This Document**   
- [Point.java](file://src/main/java/player/Point.java)
- [Attribute.java](file://src/main/java/item/Attribute.java)
- [AttributeConst.java](file://src/main/java/consts/AttributeConst.java)
- [InventoryService.java](file://src/main/java/services/InventoryService.java)
- [ItemEquip.java](file://src/main/java/item/ItemEquip.java)
- [Manager.java](file://src/main/java/manager/Manager.java)
- [SkillBuff.java](file://src/main/java/skill/SkillBuff.java)
</cite>

## Table of Contents
1. [Introduction](#introduction)
2. [Core Statistics Management](#core-statistics-management)
3. [Attribute Calculation and Progression Mechanics](#attribute-calculation-and-progression-mechanics)
4. [Stat Allocation System](#stat-allocation-system)
5. [Dynamic Stat Recalculation](#dynamic-stat-recalculation)
6. [Combat Effectiveness Impact](#combat-effectiveness-impact)
7. [Common Issues and Optimization Strategies](#common-issues-and-optimization-strategies)
8. [Conclusion](#conclusion)

## Introduction
This document provides a comprehensive analysis of the player attribute and stat system in the game. It explains how core statistics such as HP, MP, strength, intelligence, and agility are managed through the Point.java class, including their calculation and progression mechanics. The documentation covers how attributes are influenced by equipment, buffs, and leveling using data from Attribute.java and constants defined in AttributeConst.java. It also describes the stat allocation system upon level-up and its impact on combat effectiveness. Code examples demonstrate dynamic stat recalculation when equipping items or applying buffs. The document addresses common issues like floating-point precision errors in stat computation, overflow risks, and performance bottlenecks during frequent stat updates, providing optimization strategies such as caching derived values and minimizing redundant recalculations.

**Section sources**
- [Point.java](file://src/main/java/player/Point.java#L1-L593)

## Core Statistics Management

The Point.java class serves as the central component for managing player statistics in the game. It encapsulates core attributes such as HP, MP, attack, defense, and various secondary stats that influence gameplay mechanics. The class maintains both base values and derived values that are calculated based on player progression, equipment, and active buffs.

The primary statistics managed by the Point class include:
- **HP (Health Points)**: Represents the player's health, with hpMax storing the maximum value and hp tracking current health
- **MP (Mana Points)**: Represents the player's magical energy, with mpMax storing the maximum value and mp tracking current mana
- **Attack**: Determines the player's offensive capability in combat
- **Defend and DefendMagic**: Represent physical and magical defense values respectively
- **Primary Attributes**: Strength, agility, spirit, health, and luck that form the foundation for stat progression

These statistics are initialized and maintained through a comprehensive system that accounts for player class, level progression, equipment bonuses, and temporary effects. The class provides methods for safely modifying these values, such as plusHp(), plusMp(), minusHp(), and minusMp(), which include boundary checks to prevent invalid states.

The Point class also tracks progression-related metrics including experience points (exp), base points available for allocation (basePoint), and skill points (skillPoint) that players can distribute to enhance their capabilities. This system enables a flexible character development framework where players can customize their characters according to their preferred playstyle.

**Section sources**
- [Point.java](file://src/main/java/player/Point.java#L15-L593)

## Attribute Calculation and Progression Mechanics

The attribute calculation system in the game follows a sophisticated progression model that combines base attribute values with modifiers from equipment, buffs, and player class-specific bonuses. The Point class implements this through a series of private setter methods that compute derived statistics based on the player's current state.

Primary attributes (strength, agility, spirit, health, and luck) serve as the foundation for calculating secondary statistics. These base attributes are enhanced by equipment through the sumAttributeValueForId() method in InventoryService, which aggregates attribute bonuses from all equipped items. For example, the setStrength() method adds equipment bonuses to the base strength value:

```java
private void setStrength() {
    strengthAdd += (short) InventoryService.instance.sumAttributeValueForId(player, AttributeConst.TANG_SUC_MANH);
    // Additional bonuses from mounts and other sources
}
```

Secondary statistics are calculated based on player class and primary attributes:
- **Attack**: Calculated differently for each class - melee classes use strength, magic classes use spirit, and ranged classes use agility
- **HP Max**: Determined by health attribute with class-specific multipliers (80x for swordsmen, 70x for warriors/tank classes, 60x for mages/archers)
- **MP Max**: Based on spirit attribute with different multipliers for magic users (52x) versus other classes (20x)

The system also incorporates percentage-based bonuses through fields like percentPlusHp and percentPlusMp, which are applied multiplicatively to the base maximum values. These percentage bonuses can come from equipment attributes, active buffs, or class-specific abilities.

Progression mechanics are tied to the player's level and class, with different classes receiving different stat scaling. The initPoint() method orchestrates the complete recalculation of all statistics, ensuring that changes to any contributing factor (equipment, level, buffs) are properly reflected in the final values.

**Section sources**
- [Point.java](file://src/main/java/player/Point.java#L150-L550)
- [AttributeConst.java](file://src/main/java/consts/AttributeConst.java#L0-L133)
- [InventoryService.java](file://src/main/java/services/InventoryService.java#L500-L550)

## Stat Allocation System

The stat allocation system allows players to distribute points when leveling up, providing customization options that influence their character's development. This system is implemented through the increaseBasePoint() method in the Point class, which enables players to allocate base points to primary attributes including strength, agility, spirit, health, and luck.

When a player gains a level, they receive basePoint increments that can be allocated to enhance specific attributes. The allocation process follows these rules:
- Players can distribute points among five primary attributes
- Each allocation consumes one base point
- The system validates that sufficient points are available before applying changes
- After allocation, all dependent statistics are recalculated

The allocation process triggers a complete stat recalculation through the initPoint() method, ensuring that changes propagate to all derived statistics. This recalculation affects combat effectiveness by modifying attack power, defense values, maximum HP/MP, and other secondary attributes that depend on the primary attributes.

Different player classes benefit differently from attribute allocation:
- **Melee classes** (swordsmen, warriors) gain more attack power from strength
- **Magic classes** (mages) benefit more from spirit, which enhances both attack and MP
- **Ranged classes** (archers) see greater benefits from agility, which contributes to both attack and evasion
- **Tank classes** (fighters) benefit from health, which increases HP and defense

The system also includes validation to prevent invalid allocations, such as attempting to allocate more points than available or distributing negative values. After successful allocation, the client interface is updated through Service.instance.sendMainCharInfo() to reflect the changes, and map services are notified of HP/MP changes through MapService.instance.onNewHpMp().

**Section sources**
- [Point.java](file://src/main/java/player/Point.java#L130-L149)
- [Player.java](file://src/main/java/player/Player.java#L200-L250)

## Dynamic Stat Recalculation

The dynamic stat recalculation system ensures that player statistics are accurately updated when equipment is changed, buffs are applied, or other stat-affecting events occur. This system is centered around the initPoint() method in the Point class, which serves as the primary entry point for comprehensive stat recalculation.

When equipment is equipped or removed, the system triggers a complete recalculation of all dependent statistics. The process follows this sequence:
1. Reset all calculated values through resetPoint()
2. Calculate percentage-based bonuses (percentPlusHp, percentPlusMp)
3. Compute attribute additions from equipment and mounts
4. Calculate primary statistics (HP, MP, attack, defense)
5. Update secondary attributes and combat modifiers
6. Ensure current HP/MP values are within valid ranges

The recalculation process is designed to handle various sources of stat modifications:
- **Equipment**: Attributes from worn items are summed using InventoryService.sumAttributeValueForId()
- **Mounts**: Special mounts like horses provide additional stat bonuses
- **Buffs**: Active skill buffs can modify stats temporarily
- **Class-specific abilities**: Certain classes receive stat bonuses from their skills

For example, when a player equips an item that provides additional strength, the following sequence occurs:
1. The item is added to the player's equipment inventory
2. InventoryService notifies the player's Point component of the change
3. The player calls initPoint() to recalculate all statistics
4. setStrength() sums equipment bonuses using sumAttributeValueForId()
5. setAttack() recalculates attack power based on updated strength
6. setHpMax() updates maximum HP based on any health bonuses
7. Client interfaces are updated to reflect the changes

The system also handles edge cases such as preventing negative values and ensuring minimum thresholds are met. For instance, damage values are clamped to a minimum of 1 to prevent zero-damage attacks, and HP/MP values are validated to ensure they remain within their maximum limits.

**Section sources**
- [Point.java](file://src/main/java/player/Point.java#L500-L593)
- [InventoryService.java](file://src/main/java/services/InventoryService.java#L500-L600)
- [ItemEquip.java](file://src/main/java/item/ItemEquip.java#L100-L150)

## Combat Effectiveness Impact

The attribute system directly influences combat effectiveness through multiple interconnected mechanics that determine a player's performance in battles. The Point class implements these mechanics by calculating various combat statistics that affect damage output, survivability, and tactical advantages.

Attack effectiveness is determined by several factors:
- **Base attack power**: Calculated from primary attributes based on player class
- **Critical hits**: The critical field determines the chance of landing critical strikes, which double damage output
- **Accuracy and evasion**: The accurate and dodge fields influence hit/miss rates in combat
- **Damage multipliers**: Fields like x2 and baoKich provide additional damage multipliers

Defensive capabilities are equally comprehensive:
- **Physical defense**: The defend field reduces incoming physical damage
- **Magical defense**: The defendMagic field mitigates magical damage
- **Damage reduction**: Fields like giamStVat and giamStMa provide percentage-based damage reduction
- **Damage absorption**: The hapThu field allows absorption of a percentage of incoming damage

Special combat mechanics enhance tactical depth:
- **Penetration**: The xuyenGiap field increases damage by ignoring a portion of enemy defense
- **Damage over time**: The docTinh field contributes to poison damage effects
- **Resource management**: HP and MP values determine sustainability in prolonged battles
- **Revival costs**: The xuRevive field calculates the cost to revive after death

The getDameAttack() method implements the complete damage calculation formula, incorporating multiple factors:
- Base attack power scaled by class-specific multipliers
- Skill level bonuses that enhance damage output
- Critical hit and "bao kich" (critical strike) multipliers
- Random variation in damage (70-90% of calculated damage)
- Minimum damage threshold to prevent zero-damage attacks

These mechanics create a balanced combat system where different stat allocations lead to distinct playstyles. For example, a strength-focused build excels in raw damage output but may sacrifice survivability, while a balanced build with investment in health and defense can sustain longer in combat at the expense of damage output.

**Section sources**
- [Point.java](file://src/main/java/player/Point.java#L100-L150)
- [Player.java](file://src/main/java/player/Player.java#L50-L100)

## Common Issues and Optimization Strategies

The attribute and stat system faces several technical challenges that require careful consideration to maintain game stability and performance. This section addresses common issues such as floating-point precision errors, overflow risks, and performance bottlenecks, along with corresponding optimization strategies.

### Floating-Point Precision and Overflow Risks
The system handles floating-point precision through careful type selection and rounding. Percentage-based calculations use short integers with implied decimal places (e.g., 150 representing 15.0%) to avoid floating-point inaccuracies. When conversion to floating-point is necessary, the system uses explicit casting and rounding:

```java
dameAttack *= 2; // Integer multiplication avoids floating-point accumulation
dameAttack = Util.nextInt((int) ((float) dameAttack * 0.7), (int) ((float) dameAttack * 0.9)); // Explicit casting
```

Overflow risks are mitigated through boundary checks in all modification methods. For example, the plusHp() and plusMp() methods ensure values do not exceed their maximums, while minus methods prevent negative values. The system also validates input parameters to reject negative point allocations.

### Performance Bottlenecks
The primary performance bottleneck occurs during stat recalculation, particularly when initPoint() is called frequently. This method recalculates all statistics, which can be expensive when triggered by rapid equipment changes or frequent buff applications.

### Optimization Strategies
Several optimization strategies address these performance concerns:

**Caching Derived Values**
The system could implement caching for frequently accessed but infrequently changed values. For example, attack power could be cached and only recalculated when relevant attributes change:

```java
private int cachedAttack = -1;
private long lastAttackCalculation = 0;

public int getAttack() {
    if (cachedAttack == -1 || lastAttackCalculation < getLastStatChangeTime()) {
        recalculateAttack();
    }
    return cachedAttack;
}
```

**Minimizing Redundant Recalculations**
Instead of calling initPoint() for every minor change, the system could implement targeted recalculation methods that only update affected statistics. For example, equipping a strength-boosting item would only require recalculation of attack and related stats, not the entire stat sheet.

**Batch Processing**
Multiple stat changes could be batched and processed together to reduce the number of full recalculation cycles. A change queue could accumulate modifications and process them in a single initPoint() call.

**Event-Driven Updates**
Implementing an event system where only interested components are notified of stat changes would reduce unnecessary processing. For example, the UI would only update when visible stats change, rather than after every stat recalculation.

**Lazy Evaluation**
Some statistics could be calculated on-demand rather than being stored, reducing memory usage and update overhead for rarely accessed values.

These optimization strategies would significantly improve performance during high-frequency stat updates while maintaining the integrity of the game mechanics.

**Section sources**
- [Point.java](file://src/main/java/player/Point.java#L100-L593)
- [Util.java](file://src/main/java/utils/Util.java#L100-L150)

## Conclusion
The player attribute and stat system represents a sophisticated framework that balances character progression, combat mechanics, and customization options. Through the Point class and its integration with equipment, buffs, and leveling systems, the game provides a rich character development experience that rewards strategic stat allocation and equipment choices.

The system's strength lies in its comprehensive approach to stat calculation, considering multiple factors including base attributes, equipment bonuses, class-specific modifiers, and temporary effects. This creates a dynamic gameplay experience where players can experiment with different builds and strategies.

While the current implementation effectively handles the core requirements, opportunities exist for optimization, particularly in reducing the performance impact of frequent stat recalculations. Implementing caching, targeted recalculation, and event-driven updates would enhance performance without compromising gameplay integrity.

The attribute system successfully creates meaningful choices for players, where each stat allocation decision has tangible effects on combat effectiveness and playstyle. This depth of customization contributes significantly to the game's replay value and strategic depth.

**Section sources**
- [Point.java](file://src/main/java/player/Point.java#L1-L593)
- [Attribute.java](file://src/main/java/item/Attribute.java#L1-L56)
- [AttributeConst.java](file://src/main/java/consts/AttributeConst.java#L1-L133)