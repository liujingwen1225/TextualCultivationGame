# 技术基线

## 1. 已锁定技术栈

```text
Engine: Godot 4.7.x .NET
Language: C#
Runtime: .NET 8
Rendering: Pure 2D
Platform: Steam / Windows first
Game Type: Local single-player desktop RPG
```

Godot 已经是最终引擎，不再保留 libGDX 候选，也不再执行 Engine Spike。

实际工程、CI 与开发环境必须 pin 到明确的 Godot 4.7.x patch 版本，不使用浮动安装。当前设计建议以 `4.7.2 .NET` 作为首个工程基线；后续升级必须显式验证构建、Headless、输入、场景和 C# 集成。

## 2. 硬架构原则

> **权威规则必须留在纯 C# Game Core；Godot Scene / Node 只负责表现、输入和适配。**

Game Core 不依赖：

- Godot Node / SceneTree。
- Godot 类型作为领域模型基础类型。
- 渲染帧。
- 输入设备。
- Steam API。
- 文件系统具体实现。
- 当前系统时间。

这样核心修炼、事件、战斗和世界规则可以被纯 C# 单元测试与 Scenario Runner 直接驱动。

## 3. 依赖方向

运行时结构不是“Core 调用 Adapter”，而是外层实现依赖内层规则。

```text
Godot Presentation
        ↓
Game Application
        ↓
Pure C# Game Core

Game.Content ───────→ Game Core
Game.Persistence ───→ Game Core
Platform Adapter ───→ Application / Core contracts
Diagnostics ────────→ Application / Core snapshots
```

硬约束：

```text
Game Core → nothing engine-specific
Application → Game Core
Presentation → Application
Content → Game Core
Persistence → Game Core
```

不得让 Game Core 反向引用 Godot、Save 文件实现、Steam SDK 或具体内容文件格式。

## 4. 工程模块原则

正式工程至少区分：

```text
Game.Core
Game.Application
Game.Content
Game.Persistence
Godot Game Host
Tests / Scenario Runner
```

不是每个模块都需要先创建接口层。

优先采用深模块与少量稳定入口，不建立大量仅透传的 `XxxService`、`IXxxService`、Repository 或 Adapter 抽象。

只有出现真实可替换实现、外部系统边界或明确测试价值时再抽取 seam。

详细工程结构见 `11-godot-project-architecture.md`。

## 5. 纯 2D 表现

使用 Godot 2D 能力：

- TileMapLayer / TileSet。
- CharacterBody2D / Area2D 等基础 2D 节点。
- Y-sort / CanvasItem Z layering。
- Light2D。
- Particles2D。
- 2D Shader。
- Camera2D。

不采用“3D 场景 + 2D 角色”的 HD-2D 路线作为当前基线。

## 6. 本地权威

首发不需要服务端权威状态。

```text
GameState
→ 本机运行
→ 本地 SaveGame
→ Steam 平台层后接
```

不使用 Spring Boot / REST 作为游戏玩法运行时。

## 7. SaveGame

使用版本化的本地存档模型。

存档至少包含：

- PlayerState。
- WorldTime。
- NPC / Relationship / Faction State。
- EventState。
- Inventory / Economy State。
- Map / Location State。
- Intel / Journal State。
- Combat 外持续伤势与状态。
- Rule / Content / Save schema version。

V0.1 允许采用易调试的序列化格式；具体磁盘格式在实现前单独锁定。

普通保存、自动保存与读取属于标准单机游戏能力，不实现 Anchor / 跨世状态层。

存档模型只保存动态权威状态和稳定 Definition ID，不复制全部静态内容定义。

## 8. 内容数据

内容与核心规则分离。

优先将以下内容数据化：

- NPC 定义。
- 地图语义配置。
- Event / Event Chain。
- Condition / Effect 参数。
- 功法、术法、法宝和物品定义。
- 商店 / 资源池。
- 对话与文本。

当前设计基线：

> **Gameplay Content 使用 JSON + `System.Text.Json` 读取为纯 C# 不可变 Definitions。**

Godot Resource / Scene 用于表现资产和编辑器绑定，但不能成为 Game Core 或 Scenario Runner 的唯一数据依赖。

规则代码负责解释数据，不允许内容配置任意执行 C#、反射调用领域对象或直接改写 SaveGame。

详细数据架构见 `12-content-data-architecture.md`。

## 9. 世界时间与战斗时间

必须区分两个时间域。

### World Time

服务：

- 修炼。
- 疗伤。
- 旅行。
- NPC Schedule。
- 秘境 / 商店 / 宗门事件。

重大世界时间不能由 Godot `_Process()`、物理帧或现实系统时间直接推进。

### Combat Clock

服务 RTwP：

- 移动。
- 施法 / 动作时间。
- 打断。
- 冷却。
- 战斗状态变化。

Combat Clock 由 Game Core 的确定性逻辑推进；Godot 可以驱动逻辑更新请求，但渲染帧 `delta` 不是权威规则来源。

战斗结束后，实际战斗经过时间再折算 / 累计进 World Time。

## 10. 空间与位置边界

探索移动和战斗规则不需要共享同一套完整物理模拟。

V0.1 基线：

```text
探索：
Godot CharacterBody2D / TileMap / Physics2D
→ 负责表现移动和普通地图碰撞

Game Core：
CurrentMap / CurrentZone / InteractionContext
→ 负责语义地点与合法交互

战斗开始：
Godot 当前场景语义
→ 转换为纯 C# Tactical Combat State
→ Core 负责 Position / Distance / Range / Tagged Zone / 简单阻挡等正式规则
```

Core 中不得直接使用 `Godot.Vector2` 作为权威领域类型。

V0.1 不重造完整 Physics2D、复杂连续碰撞、完整 LOS 或大型战术导航系统。

## 11. 时间与随机确定性

重大结果不能依赖：

- 帧率。
- 真实时间。
- 鼠标移动 / 输入次数。
- 玩家多走几步。
- 无关菜单操作。

随机必须经过 Game Core 统一随机入口，并能够在 Scenario Test 中固定 seed / scope 以复现结果。

重要随机应按领域上下文派生或分 scope，避免无关行为消耗全局随机序列后改变远期关键事件。

## 12. AI / 自动化硬约束

项目必须适合 Codex / AI 自动开发与验证：

- 纯 C# Core 可独立编译测试。
- 关键场景可以无渲染执行。
- GameState 可导出成结构化诊断信息。
- 关键行为有稳定 Application Command 边界。
- Godot 与 Scenario Runner 通过同一个 Application 入口驱动规则。
- CI 不依赖人工点击编辑器。

测试层：

```text
L0 Build / Static / Content Validation
L1 Core Unit Tests
L2 Scenario Tests
L3 Godot Headless Integration
L4 Visual / Input Smoke
```

详细测试架构见 `13-test-architecture.md`。

## 13. 禁止重新引入的技术方向

除非重新做产品决策，否则不要引入：

- Java + libGDX 双引擎并行。
- H5 / uni-app x / 微信小程序主客户端。
- Spring Boot / REST 游戏服务器。
- PostgreSQL / MyBatis-Flex 首版本地存档。
- LiteFlow 作为核心 Event Engine。
- 运行时 LLM Agent 作为权威 NPC / 世界规则。
- 为已删除的 Anchor / 回溯 / 承世 / 悟世预留复杂接口。
- 为 V0.1 建造自制脚本语言或通用规则 DSL。
- 为单一实现提前创建大量抽象接口和 Adapter 层。
