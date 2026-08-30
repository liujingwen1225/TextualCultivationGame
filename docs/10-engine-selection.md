# 诸世问道：游戏引擎选型决策

> 状态：**已锁定**
>
> 最终选择：**Godot 4.7.2 .NET + C# + 纯 2D**。

## 1. 决策

《诸世问道》正式采用：

```text
Godot 4.7.2 .NET
+
C# / .NET 8
+
纯 2D
+
Steam / Windows 优先
```

不再继续维护 Java + libGDX、Unity / 团结引擎、Cocos Creator 的正式候选实现。

## 2. 决策前提

当前产品方向已经锁定：

```text
Steam / Windows 优先
东方修仙像素人生模拟 Roguelite RPG
真实 2D 像素地图探索
事件驱动内容
单机本地权威运行时
AI / Codex 自动开发与测试
```

首版不追求 HD-2D 或 3D 场景，也不以 Web / 小程序 / App 多端统一作为技术约束。

## 3. 为什么选择 Godot + C#

### 3.1 纯 2D 与当前产品形态一致

项目核心表现是：

- TileMapLayer 地图；
- Sprite2D / AnimatedSprite2D 角色与 NPC；
- 2D 碰撞与导航；
- 2D 灯光、雾、粒子和 Shader；
- Control UI；
- 俯视 / 斜俯视像素世界。

Godot 的原生 2D 工作流可以直接覆盖这些需求，不需要为了当前 V0.1 引入 3D 或 HD-2D 制作复杂度。

### 3.2 地图与场景制作效率高于纯代码框架

《诸世问道》的核心规则复杂，但产品已经明确要求玩家真实移动、进入地图、接触 NPC、调查、战斗和探索。

长期开发成本不仅来自规则代码，还来自：

- 地图编辑；
- 碰撞与交互点；
- NPC 摆放；
- UI；
- 动画；
- 粒子与光影；
- 场景切换；
- 视觉 Smoke 验证。

Godot 的 Scene / Node / TileMapLayer 工作流可以减少这些重复基础设施建设。

### 3.3 C# 适合承载复杂领域规则

核心系统继续使用强类型 C# 实现：

```text
World
Life
Anchor
Knowledge
Trait
Cultivation
Event Engine
Combat
Save
Deterministic Random
```

选择 Godot 不意味着把规则写进 Node。

正式架构要求：

```text
Godot Scene / UI
       ↓
Game Application
       ↓
纯 C# Game Core + Event Engine + Combat
       ↓
Save / Content / Platform Adapters
```

Game Core 不依赖 Godot 场景树、渲染 API、输入 API 或 Steamworks。

### 3.4 自动化能力满足项目要求

Godot 必须通过 CLI / headless 参与开发闭环：

```text
Codex 修改
→ dotnet build / test
→ Scenario Test
→ Godot --headless 运行时测试
→ 日志 / 状态 / 截图
→ 修复
→ 回归
```

黑水三世 Scenario Test 必须脱离渲染执行。

## 4. Spike 结论

引擎 Spike 已证明两个候选都可以实现纯规则 Scenario；libGDX 的 CLI / Headless 路径成立，Godot 的纯 C# Core、Scenario 与 Godot Host Build 也已经成立。

最终不再继续用加权评分决定产品方向，原因是产品需求在 Spike 后进一步明确为：

> **纯 2D、可探索地图、频繁场景编辑、视觉表现与系统规则并重。**

在这个前提下，Godot 的地图 / Scene / UI / 动画 / 粒子生产效率带来的长期收益高于继续维护纯代码渲染框架的收益。

`spike/engine-selection` 只保留为历史技术验证，不作为正式 V0.1 代码基线。

## 5. 正式技术边界

### Presentation / Scene

允许依赖 Godot：

- Node / Node2D；
- TileMapLayer；
- CharacterBody2D；
- Sprite2D / AnimatedSprite2D；
- Camera2D；
- Control；
- Light2D；
- GPUParticles2D；
- Audio；
- 输入与 Scene 生命周期。

### Game Core

禁止依赖 Godot：

- Anchor / Life / Knowledge / Trait；
- 修炼与时间；
- Event Condition / Effect；
- NPC / World 权威状态；
- 承世 / 悟世；
- 确定性随机；
- 核心战斗规则；
- SaveGame 领域模型。

### Application

负责把 Godot 输入和场景交互转换成高层命令，例如：

```text
Interact
Investigate
Cultivate
Travel
StartCombat
ChooseEventOption
CommitSuicide
SelectAnchor
Inherit
RealizeTrait
StartNextLife
```

UI / Scene 不能直接修改权威游戏状态。

## 6. V0.1 默认 Godot 能力

V0.1 优先使用：

```text
TileMapLayer
CharacterBody2D
Area2D
AnimatedSprite2D
Camera2D
Control
Light2D
GPUParticles2D
Resource / JSON 内容定义
```

不因为引擎能力丰富而扩大 V0.1：

- 不做 3D；
- 不做 HD-2D；
- 不做大型开放世界；
- 不做复杂动作战斗；
- 不做运行时 AI NPC Agent；
- 不做完整内容编辑器。

## 7. 测试基线

```text
L1 纯 C# Unit Test
L2 黑水三世 Scenario Test
L3 Godot Headless / Runtime Integration
L4 Scene / Input / Screenshot Smoke
```

任何正式实现票都不能把“需要人工打开编辑器点击确认”作为唯一验收方式。

## 8. 被淘汰方向

以下方向退出当前正式基线：

- Java + libGDX；
- Unity / 团结引擎；
- Cocos Creator；
- uni-app x / H5 / 小程序首发；
- Spring Boot + REST 游戏运行时；
- PostgreSQL 首版本地存档；
- LiteFlow 游戏流程编排；
- HD-2D / 3D 作为首版视觉目标。

## 9. 后续顺序

引擎决策完成后，项目进入：

```text
更新技术基线与运行时文档
↓
清理 Agent / Spec 工作流中的旧 Web / PostgreSQL 假设
↓
grill-with-docs 一致性检查
↓
to-spec
↓
to-tickets
↓
blockers-first 正式实现
```

从本决策开始，除非出现已验证的硬阻塞，不再重复进行引擎选型。