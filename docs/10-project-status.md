# 当前项目状态

> 分支：`redesign/orthodox-cultivation-rpg-docs`

## 1. 当前产品结论

产品方向已经从“东方修仙像素人生模拟 Roguelite RPG”切换为：

> **东方修仙人生模拟 RPG**

核心目标是把“修仙人生本身”做成立，而不是依赖死亡回溯 Meta 循环制造差异化。

## 2. 已锁定

### 2.1 产品

- 平台：Steam / Windows 优先。
- 核心目标：让玩家真正活完一个修仙人生。
- 战斗：当前地图 RTwP 战术战斗。
- 玩家完全控制主角，盟友采用简化战术。
- 修炼消耗游戏内时间。
- 世界事件和重要 NPC 不等待玩家。
- 境界必须有质变和社会意义。
- 修真社会包含宗门、家族、散修、坊市和高阶修士威慑。
- 情报 / 调查是当前人生中的正式玩法系统。
- 玩家选择会形成持续关系、资源、伤势、身份和世界后果。
- 死亡恢复为普通 SaveGame / AutoSave / Checkpoint。

### 2.2 V0.1

- 使用沈川 + 青玄宗 / 青石坊市 / 黑水山 / 黑水秘境连续人生纵切。
- 目标首次完整游玩约 45～75 分钟。
- 不扩大地图面积，以 4 类最小语义区域验证玩法。
- 3 个核心 NPC 为基线，最多 5 个有内容价值的 NPC。
- 至少存在一个真正有机会成本的修炼选择。
- 至少存在一条体现外门弟子身份的宗门制度约束。
- 黑水结束后必须有 5～10 分钟可玩的事件余波。
- V0.1 同时通过 Player Experience Gate 与 Technical Confidence Gate。

### 2.3 技术

- 引擎：Godot 4.7.x .NET。
- 首个工程 patch 建议：Godot 4.7.2 .NET，工程与 CI 使用明确 pin 版本。
- 语言：C#。
- Runtime：.NET 8。
- 表现：纯 2D 像素地图。
- 架构：本地权威运行时。
- Game Core：纯 C#，不依赖 Godot Scene / Node。
- Application：以 `GameSession` 作为窄入口，不建设大量浅 Service。
- Godot 与 Scenario Runner 通过同一个 Application seam 驱动正式规则。
- World Time 与 Combat Clock 分离。
- 探索移动可使用 Godot Physics2D；正式战斗规则进入纯 C# CombatState。

### 2.4 内容数据

- Gameplay Content 当前基线：JSON + `System.Text.Json`。
- Definition 与动态 State 分离。
- SaveGame 通过稳定 Definition ID 引用静态内容。
- Godot Scene / Resource 负责表现资产和 Content ID 绑定，不作为 Game Core 唯一规则源。
- Event Engine 只支持类型化 Condition / Effect，不建设任意脚本 DSL。
- Content Validation 必须在启动 / CI 中发现 ID、引用、Event Chain、Schedule 等错误。

### 2.5 自动化测试

测试架构已锁定为：

```text
L0 Build / Static / Content Validation
L1 Pure C# Core Unit Tests
L2 Scenario Runner
L3 Godot Headless Integration
L4 Visual / Input Smoke
```

- L2 是最重要的连续人生 E2E 规则测试层。
- V0.1 主 Scenario：`blackwater-continuous-life`。
- Scenario Runner 不复制游戏规则，不直接修改 GameState 绕过 Application。
- Scenario 必须支持固定 seed 与结构化 Trace。
- L4 不作为早期每个小 Core 修改的唯一阻塞门。

## 3. 已删除

以下系统不再属于当前产品：

- Anchor / 天衍锚点 / 定世。
- 10 次回溯和死亡后重开人生。
- 承世。
- 悟世。
- 跨世 Trait。
- 跨世 Knowledge。
- 强制黑水三世结构。
- World Fate / 结世 / 跨 World Meta 作为当前主循环。
- 唯一物跨时间线规则。

技术方向同步删除：

- libGDX 候选与 Engine Spike。
- H5 / uni-app x / 小程序首发。
- Spring Boot + REST 单机玩法运行时。
- PostgreSQL + MyBatis-Flex 首版本地存档。
- LiteFlow 核心流程编排。
- 为旧多世机制预留的抽象层。
- 自制通用 Event DSL。
- 为单一实现提前建设的大量 Adapter / Interface。

## 4. 已完成的设计收口

本轮已完成：

- 新产品方向文档一致性清理。
- V0.1 从“缩小版完整游戏”进一步收紧为有内容预算的正式纵切。
- 修炼选择、宗门制度约束、事件余波进入 V0.1 验收。
- 运行时依赖方向校正。
- `GameSession` Application seam 明确。
- World Time / Combat Clock 分离。
- 探索空间 / 战斗空间权威边界明确。
- Event Engine 防止万能脚本化。
- Godot 工程架构形成独立设计文档。
- Gameplay Content / Definition / State / Stable ID 架构形成独立设计文档。
- L0～L4 测试架构与 Scenario Trace 形成独立设计文档。

## 5. 仍需继续设计

进入 Spec 前仍需收口的主要问题已经从“架构方向”缩小为具体玩法与参数问题：

### V0.1 内容

- 黑水事件具体人物与隐藏真相。
- 3 个核心 NPC 的目标、立场、关系和时间安排。
- 黑水事件 2～3 种主要结果路径。
- 事件余波具体呈现。
- V0.1 宗门制度约束最终选择。

### 修炼 / Build

- V0.1 的主修功法。
- 4～6 个正式战斗能力。
- 1～2 件有主动意义的法宝 / 装备。
- 修炼选择的时间与收益节奏。
- 炼气阶段最小成长反馈。

### RTwP

- 移动与暂停交互。
- Combat Clock 的逻辑步长 / 行动推进方式。
- 动作启动、完成、打断与冷却的具体规则。
- Position / Distance / Range / Tagged Zone 最小表达。
- 逃跑规则。
- 自动暂停触发规则。

### Save / Load

- 手动槽数量与交互。
- AutoSave 触发点。
- 战斗中是否允许手动保存。
- 死亡后的默认读取策略。
- V0.1 可调试存档磁盘格式。

### UI / Content authoring

- V0.1 关键 HUD / Panel 的最小信息层级。
- JSON 内容文件的具体字段结构仍应等上述玩法规则收口后再定，不提前写 Spec schema。

这些仍然是设计问题，不意味着需要现在生成 Spec / Tickets。

## 6. 当前开发禁令

在设计与架构最终审查完成前：

- 不生成正式 V0.1 Spec。
- 不生成实施 Tickets。
- 不开始正式 V0.1 功能开发。
- 不复用旧多世回溯 Issues 作为当前任务。
- 不为了未来可能需求创建 Anchor / Meta / World Generation 等抽象层。
- 不扩大 V0.1 地图和内容规模。
- 不因为内容数据架构已确定就提前定义全部正式版 JSON schema。
- 不因为测试架构已确定就开始搭建完整 CI / 测试基础设施。

允许：

- 文档一致性清理。
- 产品 / 玩法设计收口。
- RTwP 规则设计。
- V0.1 黑水内容设计。
- Save / AutoSave 设计。
- 必要的极小技术验证，但不能伪装成正式开发。

## 7. V0.1 当前定位

V0.1 是一个连续的炼气修士人生片段：

```text
宗门生活
→ 修炼 / 时间取舍
→ 发现黑水机会
→ 调查
→ 宗门身份 / 资源约束
→ 坊市准备
→ 黑水探索
→ RTwP 战斗
→ 秘境
→ 发现隐藏真相
→ 返回宗门
→ 可玩事件余波
→ 后果持续存在并产生新机会 / 新限制
```

V0.1 不实现筑基完整层，也不实现世界级终局。

## 8. 下一阶段

接下来不需要再做一次大规模产品重构。

按以下顺序继续：

```text
V0.1 黑水人物 / 隐藏真相 / 结果路径收口
↓
V0.1 功法 / 术法 / 法宝最小内容收口
↓
RTwP 具体规则收口
↓
Save / AutoSave 策略收口
↓
UI / 信息呈现与上述规则最终对齐
↓
全部当前文档最终一致性审查
↓
V0.1 范围最终审查
↓
再进入 Spec
```

## 9. 当前文档真相源

只使用：

- `README.md`
- `CONTEXT.md`
- `docs/00-docs-index.md` 中列出的当前文档

其中新增：

- `docs/11-godot-project-architecture.md`
- `docs/12-content-data-architecture.md`
- `docs/13-test-architecture.md`

旧分支、旧 Issues、旧 Grill 决策稿和 Git 历史仅作参考，不得覆盖当前设计。
