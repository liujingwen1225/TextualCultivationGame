# 游戏运行时架构

## 1. 总体目标

运行时架构服务当前“正统修仙人生模拟 RPG”，不再承担多世回溯、Anchor、承世、悟世或跨 World Meta。

核心目标：

- 规则可测试。
- 内容可数据驱动。
- Godot 表现层不拥有权威状态。
- 连续世界时间、NPC、事件和战斗可以共享同一 GameState。
- Save / Load 简单直接。
- Scenario Runner 与正式游戏通过同一 Application 入口驱动规则。

## 2. 分层与依赖方向

```text
┌───────────────────────────────────────┐
│           Godot Presentation          │
│ Scene / Node / UI / Input / Audio     │
└───────────────────┬───────────────────┘
                    │ Intent
                    ▼
┌───────────────────────────────────────┐
│              GameSession              │
│          Application Module           │
│                                       │
│ Execute(Command)                      │
│ Query(Snapshot)                       │
└───────────────────┬───────────────────┘
                    │
                    ▼
┌───────────────────────────────────────┐
│            Pure C# Game Core          │
│                                       │
│ Cultivation                           │
│ World / Time                          │
│ NPC / Relationship / Faction          │
│ Event                                 │
│ Combat                                │
│ Inventory / Economy                   │
│ Intel                                 │
└───────────────────────────────────────┘

       ▲                      ▲
       │                      │
┌──────┴───────┐      ┌──────┴─────────┐
│ Game.Content │      │ Game.Persistence│
│ JSON Loader  │      │ Save / Migration│
│ Validation   │      │ Atomic Write    │
└──────────────┘      └────────────────┘
```

依赖方向：

```text
Presentation → Application
Application  → Core
Content      → Core
Persistence  → Core

Core → 不依赖 Godot / 文件系统 / 平台 / JSON 格式
```

“Adapter”不是 Game Core 下方被 Core 调用的通用层，而是外部实现依赖内部稳定模型和入口。

## 3. Game Core

Game Core 拥有唯一权威规则状态。

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

战斗进行时允许存在独立 `CombatState`，但它仍属于纯 C# Core；战斗结束后把持续结果写回 `GameState`。

不要提前创建：

- MultiLifeState。
- AnchorSnapshotManager。
- RewindService。
- InheritanceResolver。
- TraitMetaState。
- WorldMetaProgression。
- 通用脚本 VM。
- 全世界逐帧模拟器。

这些都不属于当前需求。

## 4. Application Module 与 GameSession

Godot 不直接修改 GameState，而是提交玩家或宿主意图。

Application 对外优先形成少量稳定入口，而不是大量浅 Service。

建议核心概念：

```text
GameSession
├─ Execute(GameCommand)
├─ Query(GameQuery) / GetSnapshot()
└─ 生命周期协调
```

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
ChooseEventOption
SaveGame
LoadGame
```

Application 负责：

- 校验操作上下文。
- 调用一个或多个 Core 领域模块完成完整用例。
- 处理一次 Command 引起的领域事件与调度顺序。
- 返回表现层需要的结果 / Snapshot / Domain Events。
- 协调 Save / Load 等外部能力，但不把文件格式规则塞进 Core。

不要把每个名词自动变成 `XxxService` / `IXxxService`。

只有在存在真实替换实现或清晰边界价值时才建立额外 interface seam。

## 5. Godot 与 Scenario Runner 使用同一入口

正式游戏：

```text
Godot Input / Scene
→ GameCommand
→ GameSession
→ Core
```

自动场景：

```text
Scenario Runner
→ GameCommand
→ GameSession
→ Core
```

因此：

> **Scenario Runner 不是第二套游戏逻辑，而是一个没有正式画面的游戏客户端。**

Scenario Test 不通过直接改 GameState、调用私有帮助方法或绕开 Application 来“快速布置结果”。

必要测试夹具可以构造合法初始状态，但完整场景行为必须走公开 Application seam。

## 6. 世界时间（World Time）

所有重大世界时间推进都必须通过统一领域操作，例如：

```text
AdvanceWorldTime(duration, reason)
```

或者由具有明确 duration 的行为统一结算。

世界时间推进后调度：

- NPC schedule。
- Event trigger。
- 秘境 / 商店 / 资源状态。
- 伤势、毒素、长期 BUFF / DEBUFF。
- 宗门 / 地区事件。

禁止 Godot `_Process()` 或物理帧直接推进重大世界时间。

长时间行为如修炼、旅行、疗伤不是“瞬间修改日期”，而应让时间跨度内需要处理的关键世界节点得到确定性调度。

## 7. 战斗时间（Combat Clock）

RTwP 使用独立的战斗时间域。

Combat Clock 负责：

- 移动进度。
- 动作启动与完成时间。
- 施法。
- 打断。
- 冷却 / 恢复。
- 状态持续时间。

Godot 可以以固定逻辑步长或明确节奏请求推进 Combat Clock，但 Core 不能把渲染帧率、真实 wall-clock delta 当作权威战斗规则。

目标是保证：

```text
相同初始 CombatState
+ 相同 Command 序列
+ 相同 RNG seed
→ 相同战斗结果
```

战斗结束后，将实际战斗经过时间计入 World Time，并写回持续伤势、资源消耗、死亡、撤退等结果。

## 8. 探索空间与战斗空间

探索阶段不要求 Pure C# Core 重造 Godot Physics2D。

推荐边界：

```text
Godot 探索场景
├─ CharacterBody2D
├─ TileMap / Collision
├─ Camera
└─ Interaction Area

Core
├─ CurrentMapId
├─ CurrentZoneId
├─ CurrentInteractionContext
└─ 当前可执行领域行为
```

普通视觉移动、墙体碰撞和寻路表现可以由 Godot 负责。

进入战斗时，把当前场景中对规则有意义的空间语义转换成纯 C# `CombatState`：

```text
Combatant Position
Distance / Range
Tagged Zone
阵法 / 危险区域
简单阻挡
目标关系
```

Core 不使用 `Godot.Vector2` 等 Godot 类型作为领域基础类型。

V0.1 不实现复杂连续物理、完整 LOS、任意多边形战术碰撞或大型导航模拟。

## 9. Event Engine

事件保持结构化与类型化：

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

### 9.1 不做通用 DSL

Event Engine 不允许：

- 任意 C# 表达式执行。
- 任意反射调用。
- 直接访问 SaveGame 字段。
- 内容脚本调用内部私有对象。
- Effect 任意递归调用 Effect。
- 为“以后也许需要”建设自制脚本语言。

只实现正式内容真正使用到的 typed Condition / Effect。

### 9.2 时间成本优先属于行为

`AdvanceTime` 不应默认成为可以任意嵌套的普通 Effect。

优先表达为：

```text
Action / Choice
├─ duration
└─ effects
```

由 Application / Core 完成一次行为后统一推进 World Time。

这可以降低 `TIME_ADVANCED → Event → AdvanceTime → TIME_ADVANCED` 的递归调度风险。

特殊事件如果确实需要额外消耗时间，再通过受控领域操作表达。

## 10. Combat

Combat 使用同一张地图表现，但逻辑由纯 C# 战斗状态拥有。

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

V0.1 只实现支撑正式玩法所需的最小空间规则，不为未来复杂战斗提前建设大型物理框架。

## 11. NPC 与 Schedule

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

V0.1 只要求关键 NPC 使用该模型，不要求所有路人都拥有完整计划系统。

## 12. 情报系统

`IntelState` 保存角色当前掌握的信息。

信息可关联：

- NPC。
- 地点。
- 事件。
- 势力。
- 功法 / 资源。

可以有来源、可信状态和关联线索，但 V0.1 不建立复杂自动知识图谱引擎。

情报通过普通 GameState 保存，不跨死亡形成 Meta 状态。

## 13. SaveGame

SaveGame 是 `GameState` 的版本化持久化表示。

标准流程：

```text
GameState
→ Save Serializer
→ Atomic Local Save File

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

动态 State 通过稳定 Definition ID 引用静态内容，不把整套 NPC / Item / Event 定义复制进每个存档。

## 14. 随机

Game Core 提供统一 RNG 入口。

重要随机按领域上下文派生或分 scope，避免：

```text
移动一步
→ 消耗全局随机数
→ 导致几天后的重要事件结果变化
```

测试必须可以固定 seed 并复现。

Scenario Trace 中应记录本次场景使用的 seed / scope 诊断信息。

## 15. 内容加载

内容加载器把外部定义转化成 Game Core 可理解的不可变 Definitions。

```text
JSON Content Files
→ Parse
→ Validation
→ Immutable Definition Registry
→ Game Core
```

启动 / CI 时应能执行内容校验：

- ID 是否唯一。
- 引用 ID 是否存在。
- Condition / Effect 参数是否完整。
- Event Chain 是否存在断链。
- 物品 / 技能 / NPC 引用是否正确。
- NPC Schedule Location 是否存在。
- Dialogue / Intel / Shop 引用是否合法。

详细设计见 `12-content-data-architecture.md`。

## 16. 测试入口

### L0 Build / Content Validation

快速验证编译、静态检查和全部内容引用完整性。

### L1 Core Unit

测试修炼、时间、事件、战斗、NPC、经济等纯规则。

### L2 Scenario Runner

不渲染场景，通过 `GameSession` 的公开 Application Commands 执行完整连续人生场景。

V0.1 主场景：

```text
blackwater-continuous-life
```

### L3 Godot Headless

启动真实 Godot 项目，验证 Scene / Binding / Content / Application / Core 集成。

### L4 Visual / Input Smoke

在支持图形环境时启动窗口、模拟输入、检查关键流程、日志和状态。

L4 初期不要求成为每个小 PR 的唯一强阻塞门。

详细设计见 `13-test-architecture.md`。

## 17. 架构原则

优先保持简单：

> **现在只有真正需要的系统，未来需求出现后再扩展。不要因为旧设计曾经存在，就为它保留抽象层。**

同时坚持：

> **正式游戏、自动 Scenario 和未来工具应共享同一个权威规则入口，而不是复制规则。**
