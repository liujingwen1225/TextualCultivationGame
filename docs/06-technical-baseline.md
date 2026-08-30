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

## 2. 硬架构原则

> **权威规则必须留在纯 C# Game Core；Godot Scene / Node 只负责表现、输入和适配。**

Game Core 不依赖：

- Godot Node / SceneTree。
- 渲染帧。
- 输入设备。
- Steam API。
- 文件系统具体实现。
- 当前系统时间。

这样核心修炼、事件、战斗和世界规则可以被纯 C# 单元测试与 Scenario Runner 直接驱动。

## 3. 运行时分层

```text
Godot Scene / UI / Input
        ↓
Application Layer
        ↓
Pure C# Game Core
        ├─ Player / Cultivation
        ├─ World / Time
        ├─ NPC / Relationship
        ├─ Event Engine
        ├─ Combat
        ├─ Inventory / Economy
        └─ Intel / Journal
        ↓
Adapters
        ├─ SaveGame
        ├─ Content Loader
        ├─ Audio / Visual
        └─ Platform / Steam
```

## 4. 纯 2D 表现

使用 Godot 2D 能力：

- TileMapLayer / TileSet。
- CharacterBody2D / Area2D 等基础 2D 节点。
- Y-sort / CanvasItem Z layering。
- Light2D。
- Particles2D。
- 2D Shader。
- Camera2D。

不采用“3D 场景 + 2D 角色”的 HD-2D 路线作为当前基线。

## 5. 本地权威

首发不需要服务端权威状态。

```text
GameState
→ 本机运行
→ 本地 SaveGame
→ Steam 平台层后接
```

不使用 Spring Boot / REST 作为游戏玩法运行时。

## 6. SaveGame

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

## 7. 内容数据

内容与核心规则分离。

优先将以下内容数据化：

- NPC 定义。
- 地图语义配置。
- Event / Event Chain。
- Condition / Effect 参数。
- 功法、术法、法宝和物品定义。
- 商店 / 资源池。
- 对话与文本。

规则代码负责解释数据，不允许内容配置任意执行 C# 或直接改写 SaveGame。

Godot Resource 可用于表现资源与编辑器友好资产，但不能成为 Game Core 唯一数据依赖。

## 8. 时间与确定性

重大结果不能依赖：

- 帧率。
- 真实时间。
- 鼠标移动 / 输入次数。
- 玩家多走几步。
- 无关菜单操作。

随机必须经过 Game Core 统一随机接口，并能够在 Scenario Test 中固定 seed / scope 以复现结果。

## 9. AI / 自动化硬约束

项目必须适合 Codex / AI 自动开发与验证：

- 纯 C# Core 可独立编译测试。
- 关键场景可以无渲染执行。
- GameState 可导出成结构化诊断信息。
- 关键行为有稳定 Application Command 边界。
- CI 不依赖人工点击编辑器。

建议测试层：

```text
L1 Core Unit Tests
L2 Scenario Tests
L3 Godot Headless Integration
L4 Visual / Input Smoke
```

## 10. 禁止重新引入的技术方向

除非重新做产品决策，否则不要引入：

- Java + libGDX 双引擎并行。
- H5 / uni-app x / 微信小程序主客户端。
- Spring Boot / REST 游戏服务器。
- PostgreSQL / MyBatis-Flex 首版本地存档。
- LiteFlow 作为核心 Event Engine。
- 运行时 LLM Agent 作为权威 NPC / 世界规则。
- 为已删除的 Anchor / 回溯 / 承世 / 悟世预留复杂接口。
