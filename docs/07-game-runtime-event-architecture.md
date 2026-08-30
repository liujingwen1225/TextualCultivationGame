# 诸世问道：游戏运行时与 Event Engine 架构

> 状态：Steam 像素 RPG 重设计当前有效基线
>
> 本文档与具体引擎解耦，Java + libGDX 和 Godot + C# 都必须遵守这些架构边界。

## 1. 总原则

单机游戏本身就是权威运行时，不再使用浏览器客户端 + REST 服务端的结构。

```text
Desktop Game Runtime
├─ Presentation / Scene
├─ Input
├─ Game Application
├─ Game Core
├─ Event Engine
├─ World / Map
├─ Combat
├─ Content
├─ Save System
└─ Platform Integration
```

核心目标：

> 渲染可以替换，规则不能被绑死在场景节点或 UI 控件中。

## 2. Game Core

Game Core 负责纯规则：

- World。
- Life。
- Anchor。
- Knowledge。
- Trait。
- Cultivation。
- Inventory。
- NPC / World State 的领域变化。
- Inheritance。
- Rewind。
- Deterministic Random。

Game Core 不依赖：

- 渲染 API。
- 场景对象。
- 键盘 / 鼠标。
- Steamworks。
- 文件系统具体实现。

核心规则必须能够直接单元测试。

## 3. Game Application

Game Application 接收高层玩家意图，例如：

```text
MoveTo
InteractWithNpc
Investigate
Cultivate
Travel
StartCombat
UseSkill
ChooseEventOption
CommitSuicide
SelectAnchor
Inherit
RealizeTrait
StartNextLife
```

它负责：

1. 校验当前游戏状态。
2. 调用 Game Core / Event Engine / Combat。
3. 应用状态变化。
4. 触发自动保存或 checkpoint。
5. 返回表现层需要的结果。

UI / Scene 不直接改权威状态。

## 4. World / Map

地图层负责：

- Tile / Scene 加载。
- 玩家位置。
- Zone。
- 碰撞。
- 关键交互点。
- NPC 场景位置。
- 地图切换。
- 相机。

地图层不负责：

- 判断某 Knowledge 是否有效。
- 自己授予物品。
- 直接推进事件链。
- 自己决定 NPC 核心命运。

例如玩家靠近灵酒并按交互键：

```text
Scene detects interaction
↓
Game Application receives Interact(objectId)
↓
Event Engine evaluates current context
↓
returns legal event / choices
↓
Presentation renders result
```

## 5. Event Engine

Event Engine 是项目最核心的内容运行系统之一。

负责：

- Event 定义。
- Trigger。
- Condition。
- Choice。
- Effect。
- Event Chain。
- Knowledge 解锁。
- Trait 解锁。
- NPC / World State 事件变化。
- Repeat / Cooldown。

Event Engine 不负责：

- 地图渲染。
- UI。
- 保存文件格式。
- Steam 功能。
- AI 文本自由决定权威结果。

## 6. Event Trigger

事件触发来源：

```text
ENTER_ZONE
INTERACT_NPC
INTERACT_OBJECT
TIME_ADVANCED
CULTIVATION_FINISHED
COMBAT_FINISHED
ITEM_ACQUIRED
NPC_STATE_CHANGED
WORLD_STATE_CHANGED
LIFE_STARTED
LIFE_DIED
```

Trigger 只是候选入口，最终是否发生仍由 Condition / Scheduler 判断。

## 7. Condition

Condition 必须类型化。

示例：

```text
RealmAtLeast
AtLocation
GameTimeBetween
NpcStateEquals
RelationshipAtLeast
HasKnowledge
KnowledgeApplicability
HasTraitEquipped
HasItem
EventCompleted
WorldFlag
```

组合：

```text
all
any
not
```

内容文件不得嵌入任意可执行脚本直接修改游戏状态。

## 8. Effect

Effect 同样类型化：

```text
AdvanceTime
ChangeCultivation
ApplyInjury
ChangeRelationship
MoveNpc
SetNpcState
AddItem
RemoveItem
GrantKnowledge
AdvanceEventChain
SetWorldFlag
StartCombat
KillPlayer
```

每个 Effect 应可测试、可记录、可回放问题。

## 9. Event Scheduler

Scheduler 决定：

> 当前应该发生哪个合法事件。

输入：

- 游戏内时间。
- 地点 / Zone。
- 当前剧情压力。
- NPC 相关度。
- 距离上次重大事件时间。
- 一世主题。
- Event cooldown / repeat decay。

Scheduler 不持有事件内部剧情逻辑。

## 10. RandomSource

所有规则随机统一通过 RandomSource。

禁止规则层直接调用：

```text
Math.random
new Random
frame time
system current time
input count
```

两层随机：

```text
AnchorFateSeed
→ 关键命运

RunVariationSeed
→ 一世枝节
```

重大结果使用 keyed / stateless context：

```text
anchorSeed
+ eventId
+ decisionId
+ relevantStateHash
```

玩家多走几步、提高帧率、打开背包都不能改变重大命运。

## 11. Combat

Combat 与 Event Engine 分离。

Event 可以：

```text
StartCombat(encounterId)
```

Combat 结束后返回结构化结果：

```text
Victory
Escape
Defeat
Injuries
ConsumedItems
Rewards
Death
```

然后再由 Game Application / Event Engine 继续处理后续因果。

这样未来即使战斗制式改变，也不需要重写事件内容系统。

## 12. Save System

Save System 只负责持久化当前权威状态。

至少包含：

```text
WorldState
Anchor[]
CurrentLife
Knowledge
Traits
Inventory
NpcState
EventState
RandomState / Seeds
VersionInfo
```

建议自动保存节点：

- 地图切换。
- 重大事件结算后。
- 战斗结束后。
- 死亡结算阶段变化。
- 开启下一世后。

避免允许玩家通过手动复制 / 读档绕过核心回溯机制；正式反作弊策略后续单独设计，但架构上不要把“传统无限读档”当作默认玩法。

## 13. Content

静态内容包括：

- Events。
- NPC definitions。
- Techniques。
- Items。
- Traits。
- Encounters。
- Maps metadata。
- World templates。

内容在开发 / 构建阶段进行校验：

- ID 唯一。
- 引用存在。
- Condition / Effect 类型合法。
- Event Chain 无明显断链。
- Trait 升级无循环。
- Unique Entity ID 唯一。

## 14. AI 自动测试接口

运行时必须提供开发模式入口，使 AI 不依赖人工点击编辑器。

目标能力：

```text
--headless
--dev
--scenario blackwater-three-lives
--seed <value>
--save <path>
--dump-state <path>
```

具体参数由最终引擎决定，但能力必须存在。

测试层：

```text
L1 Core Unit Test
L2 Scenario Test
L3 Runtime / Headless Integration
L4 Visual / Input Smoke
```

## 15. Scenario Test

Scenario Test 是核心 E2E 测试缝。

黑水测试应能够：

```text
CreateWorld
→ StartLife1
→ Cultivate / Travel / Trigger Blackwater
→ Choose Poison Route
→ Assert Death + K_BLACKWATER_POISON
→ Rewind
→ StartLife2
→ Assert Knowledge Choice Exists
→ Avoid Poison
→ Enter Killing Array
→ Assert Death + new Knowledge
→ Rewind / Realize Trait
→ StartLife3
→ Prepare
→ Enter New Route
→ Assert Obtain Inheritable Result
```

不需要渲染一帧画面即可执行。

## 16. Visual / Input Smoke

真正的窗口级测试只覆盖关键流程：

- 启动游戏。
- 读取存档。
- 移动角色。
- 与 NPC / 对象交互。
- 打开事件选择。
- 进入战斗。
- 打开系统菜单。
- 死亡结算。

不要把所有规则测试都压到 UI 自动化层。

## 17. 平台集成

Steamworks 属于最外层 Adapter。

核心规则不能直接依赖：

- Steam 登录。
- Steam Cloud。
- Achievement API。
- Steam Overlay。

V0.1 首先保证纯 Windows 本地构建可以完整游玩。

## 18. 当前结论

无论最终使用 Java + libGDX 还是 Godot + C#，都必须保持：

```text
Scene / UI
   ↓
Game Application
   ↓
Game Core + Event Engine + Combat
   ↓
Save / Content / Platform Adapters
```

这是后续代码审查和 AI 开发必须遵守的核心架构边界。