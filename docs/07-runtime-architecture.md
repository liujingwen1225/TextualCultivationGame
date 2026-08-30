# 游戏运行时架构

## 1. 总体目标

运行时架构服务当前“正统修仙人生模拟 RPG”，不再承担多世回溯、Anchor、承世、悟世或跨 World Meta。

核心目标：

- 规则可测试。
- 内容可数据驱动。
- Godot 表现层不拥有权威状态。
- 连续世界时间、NPC、事件和战斗可以共享同一 GameState。
- Save / Load 简单直接。

## 2. 分层

```text
Presentation / Godot Scene
        ↓
Input Mapping
        ↓
Game Application
        ↓
Pure C# Game Core
        ├─ Player
        ├─ Cultivation
        ├─ World Time
        ├─ NPC / Relationship / Faction
        ├─ Event Engine
        ├─ Combat
        ├─ Inventory / Economy
        └─ Intel / Journal
        ↓
Adapters
        ├─ Content
        ├─ SaveGame
        ├─ Platform
        └─ Diagnostics
```

依赖方向只能向下。

## 3. Game Core

Game Core 拥有唯一权威状态。

建议核心状态：

```text
GameState
├─ PlayerState
├─ WorldState
│  ├─ GameTime
│  ├─ NpcStates
│  ├─ RelationshipStates
│  ├─ FactionStates
│  ├─ LocationStates
│  └─ EconomyStates
├─ EventState
├─ InventoryState
├─ IntelState
└─ PersistentInjuryState
```

不要提前创建：

- MultiLifeState。
- AnchorSnapshotManager。
- RewindService。
- InheritanceResolver。
- TraitMetaState。
- WorldMetaProgression。

这些都属于已删除方向。

## 4. Application Layer

Godot 不直接修改 GameState，而是提交意图。

典型 Command / Intent：

```text
MoveTo
InteractWithNpc
InteractWithObject
Investigate
Travel
Cultivate
Rest
Heal
BuyItem
SellItem
ChangeLoadout
StartCombat
UseCombatAction
IssueAllyOrder
ChooseEventOption
SaveGame
LoadGame
```

Application Layer 负责：

- 校验操作上下文。
- 调用领域系统。
- 协调多个系统的一次完整用例。
- 返回表现层需要的结果 / 事件。

## 5. 时间系统

所有重大时间推进都通过统一接口，例如：

```text
AdvanceGameTime(duration, reason)
```

时间推进后统一调度：

- NPC schedule。
- Event trigger。
- 秘境 / 商店 / 资源状态。
- 伤势、毒素、BUFF/DEBUFF 持续效果。
- 宗门 / 地区事件。

禁止 Godot `_Process()` 或物理帧直接推进重大世界时间。

## 6. Event Engine

事件定义：

```text
EventDefinition
├─ Trigger
├─ Conditions[]
├─ Choices[]
└─ Effects[]
```

常用 Trigger：

- ENTER_ZONE
- INTERACT_NPC
- INTERACT_OBJECT
- TIME_ADVANCED
- CULTIVATION_FINISHED
- COMBAT_FINISHED
- ITEM_ACQUIRED
- NPC_STATE_CHANGED
- RELATIONSHIP_CHANGED
- WORLD_STATE_CHANGED

Condition 和 Effect 必须类型化。

示例 Condition：

- RealmAtLeast。
- AtLocation。
- GameTimeBetween。
- NpcStateEquals。
- RelationshipAtLeast。
- HasIdentity。
- HasIntel。
- HasItem。
- EventCompleted。
- WorldFlag。

示例 Effect：

- AdvanceTime。
- ChangeCultivation。
- ApplyInjury。
- ChangeRelationship。
- MoveNpc。
- SetNpcState。
- AddItem / RemoveItem。
- GrantIntel。
- AdvanceEventChain。
- SetWorldFlag。
- StartCombat。

内容脚本不能直接取得 SaveGame 引用后任意修改字段。

## 7. Combat

Combat 使用同一张地图，但逻辑由纯 C# 战斗状态拥有。

Godot 负责：

- 角色表现与动画。
- 战斗范围 / 目标提示。
- 输入和暂停 UI。
- 时间轴显示。
- 特效和音效。

Game Core 负责：

- 行动是否合法。
- 目标与距离规则。
- 动作启动 / 完成时间。
- 伤害、护体、状态和伤势。
- 资源消耗。
- 打断。
- 死亡 / 撤退 / 胜负结果。

战斗结束后将结果写回连续 World / Player State。

## 8. NPC 与 Schedule

关键 NPC 不逐帧模拟。

推荐：

```text
TimeBlock
+ NpcState
+ EventState
+ GoalState
→ Location / Availability / PlannedAction
```

只有玩家当前需要感知的 NPC 才需要对应 Godot 场景实例；场景实例不是 NPC 权威状态本身。

## 9. 情报系统

`IntelState` 保存角色当前掌握的信息。

信息可关联：

- NPC。
- 地点。
- 事件。
- 势力。
- 功法 / 资源。

可以有来源、可信状态和关联线索，但 V0.1 不建立复杂自动知识图谱引擎。

情报通过普通 GameState 保存，不跨死亡形成 Meta 状态。

## 10. SaveGame

SaveGame 是 `GameState` 的版本化持久化表示。

标准流程：

```text
GameState
→ Save Serializer
→ Local Save File

Local Save File
→ Migration / Validation
→ GameState
→ Resume
```

读取存档后不额外叠加任何跨世状态。

需要：

- schema version。
- content version。
- rule version（如有必要）。
- 原子写入 / 防损坏策略。
- 自动存档槽与手动存档槽。

## 11. 随机

Game Core 提供统一 RNG 抽象。

重要随机按领域上下文派生或分 scope，避免：

```text
移动一步
→ 消耗全局随机数
→ 导致几天后的重要事件结果变化
```

测试必须可以固定 seed 并复现。

## 12. 内容加载

内容加载器把外部定义转化成 Game Core 可理解的不可变 Definitions。

```text
Content Files
→ Validation
→ Definition Registry
→ Game Core
```

启动 / CI 时应能执行内容校验：

- 引用 ID 是否存在。
- Condition / Effect 参数是否完整。
- Event Chain 是否存在断链。
- 物品 / 技能 / NPC 引用是否正确。

## 13. 测试入口

### L1 Core Unit

测试修炼、时间、事件、战斗、NPC、经济等纯规则。

### L2 Scenario Runner

不渲染场景，通过 Application Commands 执行完整连续人生场景。

建议 V0.1：

```text
blackwater-continuous-life
```

### L3 Godot Headless

启动真实 Godot 项目，验证 Scene / Adapter / Content / Core 集成。

### L4 Visual / Input Smoke

在支持图形环境时启动窗口、模拟输入、检查关键截图 / 日志 / 状态。

## 14. 架构原则

优先保持简单：

> **现在只有真正需要的系统，未来需求出现后再扩展。不要因为旧设计曾经存在，就为它保留抽象层。**
