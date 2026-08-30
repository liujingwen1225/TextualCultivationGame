# Godot 工程架构

## 1. 目标

本文件定义 Godot 4.7.x .NET 工程与纯 C# 游戏代码之间的工程边界。

目标不是建立复杂框架，而是确保：

- Godot Scene / Node 专注表现、输入和引擎适配。
- Game Core 可以脱离 Godot 编译和测试。
- Scenario Runner 不依赖 Godot 编辑器或窗口。
- Godot 与 Scenario Runner 通过同一 `GameSession` 驱动正式规则。
- 工程结构对 AI / Codex 容易导航、构建和验证。

## 2. 建议仓库结构

```text
TextualCultivationGame/
│
├─ src/
│  ├─ Game.Core/
│  ├─ Game.Application/
│  ├─ Game.Content/
│  └─ Game.Persistence/
│
├─ game/
│  ├─ project.godot
│  ├─ TextualCultivationGame.csproj
│  ├─ scenes/
│  ├─ scripts/
│  ├─ ui/
│  ├─ assets/
│  └─ content/
│
├─ tests/
│  ├─ Game.Core.Tests/
│  ├─ Game.Content.Tests/
│  └─ Game.Scenarios/
│
└─ tools/
   ├─ ContentValidator/
   └─ ScenarioRunner/
```

具体 solution / project 文件名称可在进入实现前微调，但职责边界不变。

## 3. Game.Core

`Game.Core` 是最内层纯 C# 模块。

拥有：

- `GameState` 与领域状态。
- 修炼规则。
- World Time 规则。
- NPC / Relationship / Faction 规则。
- Event Condition / Effect 解释规则。
- Combat State 与战斗规则。
- Inventory / Economy 规则。
- Intel / Journal 规则。
- 确定性 RNG 规则。

禁止引用：

- Godot assembly。
- SceneTree / Node。
- 文件系统具体实现。
- JSON 文件路径。
- Steam API。
- OS wall-clock。

Core 中的位置、颜色、时间等如需要值对象，应使用纯 C# 类型，不直接复用 Godot 类型。

## 4. Game.Application

`Game.Application` 负责完整游戏用例，不拥有第二套领域规则。

核心入口优先保持窄：

```text
GameSession
├─ Execute(GameCommand)
├─ Query(GameQuery) / GetSnapshot()
└─ Session Lifecycle
```

它负责：

- 接收玩家 / Scenario / Host 意图。
- 校验当前上下文。
- 协调多个 Core 模块完成一次完整行为。
- 确定领域事件与时间调度顺序。
- 产生供表现层消费的 Snapshot / Result / Domain Events。
- 协调 Save / Load 外层能力。

不要把每个领域名词都拆成一个公开 Service。

## 5. Game.Content

`Game.Content` 负责把外部 Gameplay Content 转成 Core 可使用的不可变 Definition Registry。

职责：

```text
JSON
→ Parse
→ Validation
→ Immutable Definitions
→ Registry
```

它可以依赖 `Game.Core` 的 Definition 类型或稳定 contract，但 Core 不依赖 JSON / 文件路径实现。

详细规则见 `12-content-data-architecture.md`。

## 6. Game.Persistence

`Game.Persistence` 负责 SaveGame 外部持久化。

职责：

- Serialize / Deserialize。
- schema version。
- Migration。
- Validation。
- 自动存档 / 手动存档槽。
- 原子写入与损坏保护。

它依赖 Core / Application 的稳定 State 或 Save Model。

Core 不知道存档最终落成 JSON、二进制还是其他格式。

V0.1 优先可诊断、可人工检查，不为了文件体积提前优化。

## 7. Godot Game Host

`game/` 是唯一正式 Godot 项目。

Godot 负责：

- Scene / Node 生命周期。
- TileMapLayer / TileSet。
- CharacterBody2D / Area2D。
- Camera2D。
- UI / HUD。
- 动画。
- Audio。
- Particles / Shader / Lighting。
- Input Mapping。
- 把场景语义和玩家输入转换成 Application Command。
- 把 Snapshot / Domain Events 转换为视觉表现。

Godot 不负责：

- 直接修改 NPC Relationship。
- 直接结算 Event Effect。
- 自己计算正式战斗伤害。
- 直接推进重大 World Time。
- 直接修改 SaveGame 数据结构。

## 8. Godot C# 脚本边界

继承 Godot `Node`、`Control`、`Resource` 等引擎类型的脚本保留在 `game/` Godot 工程内。

纯 C# 规则代码放在 `src/` 的普通 .NET project 中，由 Godot project 通过 project reference 使用。

原则：

> **Godot-facing code 留在 Godot project；engine-agnostic code 留在普通 .NET project。**

不要为了“项目更整齐”把大量 Node-derived scripts 放进独立通用 Class Library，也不要让 `Game.Core` 因此引用 Godot SDK。

## 9. Scene 组织原则

Scene 是表现组合单元，不是领域状态容器。

建议语义层级：

```text
GameRoot
├─ WorldHost
│  ├─ CurrentMapScene
│  └─ ActorViews
├─ CombatPresentation
├─ UI
│  ├─ HUD
│  ├─ Panels
│  └─ EventOverlay
└─ ApplicationBridge
```

实际 Node 名称后续实现时可调整。

不要在每张 Scene 中复制一套 GameState。

## 10. 地图场景

每张地图 Scene 主要包含：

- TileMap / visual layers。
- Collision。
- Zone markers。
- Interaction Points。
- NPC presentation anchors。
- Combat semantic markers。
- Environment visuals。

地图中有规则意义的对象使用稳定 Content ID 与 Core / Content 对应。

示例：

```text
InteractionPoint
content_id = "event.blackwater.wine.inspect"
```

而不是在 Scene 脚本里硬编码：

```text
玩家减物品
NPC 好感 +5
设置黑水事件完成
```

## 11. NPC View

Godot 中的 NPC 实例只是当前可感知人物的 View / Scene Representation。

NPC 权威状态存在 Core。

流程示意：

```text
NpcState + NpcDefinition
→ Presentation Snapshot
→ Godot 创建 / 更新 NPC View
```

NPC 离开当前地图后可以没有 Godot Node，但仍然继续由世界时间和 Schedule 规则推进状态。

## 12. 探索移动边界

普通探索移动：

```text
Input
→ Godot CharacterBody2D
→ Physics / Collision
→ 当发生语义事件时提交 Command
```

例如：

- 进入 Zone。
- 靠近 Interaction Point。
- 确认交互。
- 开始跨区域 Travel。

Core 不需要接收玩家每一个渲染帧的位置变化。

## 13. 战斗表现桥接

进入正式 Combat State 时：

```text
当前地图语义
+ Combatants
+ 战术区域
→ StartCombat Command
→ Core CombatState
```

Godot 根据 Combat Snapshot 表现：

- 角色位置。
- 目标。
- Action progress。
- 范围。
- 危险区。
- 受击 / 打断。
- Pause 状态。

玩家输入转换为：

- Move Combat Command。
- Use Ability Command。
- Use Item Command。
- Retreat Command。
- Pause / Resume Host Control。

战斗胜负和伤势不由动画回调决定。

## 14. UI 架构

UI 读取稳定 Snapshot，不直接绑定 Core 可变对象。

推荐：

```text
GameSession
→ Presentation Snapshot
→ UI Presenter / Binding
→ Godot Control
```

这样 UI 刷新不会获得任意修改 GameState 的能力。

重大 UI 行为仍提交 Command，例如：

- 购买物品。
- 选择事件选项。
- 改变 Loadout。
- 开始修炼。

## 15. Signal 使用原则

Godot Signal 用于表现层内部协作，例如：

- 按钮点击。
- 动画结束通知表现层。
- View 创建 / 销毁。

不要把 Signal 当作领域事件总线。

正式领域事件由 Core / Application 产生，再映射到 Godot 表现。

避免：

```text
某 Node signal
→ 另一个 Node 随机修改世界状态
→ 第三个 Node 再改 NPC 状态
```

这会重新把权威规则散回 SceneTree。

## 16. Autoload 使用原则

Autoload 只用于真正具有 Godot Host 生命周期意义的少量对象。

可以考虑：

- Application / GameSession Host。
- Scene transition coordinator。
- Audio presentation manager。

不要把所有系统做成 Autoload singleton。

尤其禁止把 `PlayerState`、`NpcSystem`、`CombatSystem` 等权威领域对象变成 Godot 全局单例。

## 17. 工程版本基线

技术族保持：

```text
Godot 4.7.x .NET
C#
.NET 8
```

工程与 CI 必须 pin 到具体 patch 版本。

当前首选：

```text
Godot 4.7.2 .NET
```

未来升级 patch / minor 版本前至少验证：

- `dotnet build`。
- Godot project build。
- Headless integration。
- Scene 加载。
- C# script binding。
- Input smoke。

## 18. V0.1 工程范围

V0.1 只建立支撑正式纵切所需的工程能力。

需要：

- 一个可运行 Godot project。
- 纯 C# Core / Application / Content / Persistence modules。
- 四类最小地图表现。
- ApplicationBridge。
- Combat presentation bridge。
- Save / Load host integration。
- Headless integration entry。

不需要：

- Plugin framework。
- Mod SDK。
- ECS migration。
- 自制 DI container。
- 全局 Event Bus 框架。
- 通用 Gameplay Ability System 克隆。
- 通用 Quest framework。
- 多平台抽象大框架。
- 为未来多人游戏预留网络层。

## 19. 设计检查

进入 Spec 前，工程架构应能够明确回答：

1. 哪些代码可以完全脱离 Godot 运行？
2. Godot 如何提交玩家意图？
3. Godot 如何得到只读表现状态？
4. NPC 没有对应 Node 时谁维护其状态？
5. Combat 逻辑是否可以无画面运行？
6. Save / Load 是否不污染 Core？
7. Scenario Runner 是否走与 Godot 相同的 Application seam？
8. 内容引用是否不依赖 Scene 脚本硬编码规则？

如果以上问题需要“到时候再看 Scene 怎么写”才能回答，则架构尚未收口。
