# 诸世问道（暂定名）

一款面向 **Steam / Windows** 的 **东方修仙人生模拟 RPG**。

> 在一个持续运转的修真社会里，从低阶修士开始修炼、探索、争夺机缘、建立关系、承担后果，并逐步形成属于自己的修仙人生。

## 当前产品方向

游戏不再采用 Roguelite 多世回溯结构。以下系统已经从当前产品设计中移除：

- Anchor / 天衍锚点。
- 死亡回溯与回溯次数。
- 承世。
- 悟世。
- 跨世 Trait / 词条库。
- 跨世 Knowledge。
- 依赖多世机制的世界终局与 Meta Progression。

死亡恢复为正统单机 RPG 规则：**死亡后读取普通 SaveGame / 自动存档 / 检查点**。保存与读取不属于世界观核心机制。

## 核心玩法支柱

```text
修炼成长
+
像素世界探索
+
RTwP 战术战斗
+
NPC / 人物关系
+
宗门、家族与修真社会
+
持续推进的游戏时间与世界事件
+
情报、调查与机缘
+
选择造成的长期世界变化
```

核心循环：

```text
探索世界
→ 发现人物 / 机缘 / 危险
→ 调查与准备
→ 修炼 / 交易 / 社交 / 配置 Build
→ 战斗 / 秘境 / 宗门事务 / 突破
→ 承担伤势、时间、资源、关系与世界状态后果
→ 继续成长并进入更高层修真社会
```

## 世界与修炼

- 世界事件不会为了玩家暂停；闭关、旅行、疗伤都会推进游戏时间。
- 境界是能力边界、寿元和社会层级的质变，不只是数值等级。
- 当前大境界骨架：凡人 → 胎息 → 炼气 → 筑基 → 紫府 → 金丹 → 元婴 → 后续。
- 功法决定长期修炼路线；术法决定力量使用方式；神通是高阶规则能力。
- 灵根、体质、根骨、悟性、神魂等共同塑造修炼路线，不采用传统力量/敏捷/智力六维模板。
- 气血、法力、神识是核心运行资源；寿元是长期战略资源。
- 修炼不是独立经验按钮，而会与调查、交易、社交和世界机会争夺时间。

## 战斗

战斗采用 **当前地图原地进入 Combat State 的可暂停实时（RTwP）战术战斗**：

- 位置、距离、遮挡、地形、阵法区域有意义。
- 玩家完全控制主角；盟友采用简化战术与有限指令。
- 战斗强调术法、法宝、消耗品、伤势、时间与准备，不做动作游戏式精准闪避/连招。
- 合理的境界压制和致命术法可以造成一击致死，但不使用无意义随机猝死。

## V0.1 连续人生纵切

V0.1 使用沈川作为固定炼气期角色，验证：

```text
青玄宗生活
→ 修炼 / 时间取舍
→ 发现黑水机会
→ 调查
→ 宗门身份 / 资源约束
→ 青石坊市准备
→ 黑水山探索
→ RTwP 战斗
→ 黑水秘境
→ 揭开一层隐藏真相
→ 返回宗门
→ 可玩的事件余波
→ 新机会 / 新限制继续出现
```

当前内容预算：

- 首次完整游玩约 45～75 分钟。
- 3 个核心 NPC 为基线，最多 5 个有内容价值的 NPC。
- 4 类最小语义区域，不扩大为大型地图。
- 1 条主要调查链。
- 1 个真正有代价的修炼选择。
- 1 条体现外门弟子身份的宗门制度约束。
- 黑水后保留 5～10 分钟可玩事件余波。

V0.1 同时需要通过 **Player Experience Gate** 与 **Technical Confidence Gate**；自动化测试全绿不能替代“是否好玩”的产品验收。

## 技术基线

```text
Engine: Godot 4.7.x .NET
Language: C#
Runtime: .NET 8
Rendering: Pure 2D
Platform: Steam / Windows first
Authority: Local single-player runtime
```

硬架构原则：

> **权威规则保持在纯 C# Game Core；Godot Scene / Node 负责表现、输入和适配，不拥有核心规则。**

关键架构结论：

- `GameSession` 是 Godot 与 Scenario Runner 共用的 Application seam。
- World Time 与 Combat Time 分离。
- 探索移动可使用 Godot Physics2D；正式战斗规则由纯 C# Combat State 权威处理。
- Gameplay Content 使用 JSON + `System.Text.Json`，静态 Definition 与动态 State 分离。
- SaveGame 通过稳定 Definition ID 引用静态内容。
- 测试架构采用 L0 Build/Content Validation → L1 Core → L2 Scenario → L3 Headless → L4 Visual/Input Smoke。

详细技术方向见：

- `docs/06-technical-baseline.md`
- `docs/07-runtime-architecture.md`
- `docs/11-godot-project-architecture.md`
- `docs/12-content-data-architecture.md`
- `docs/13-test-architecture.md`

## 当前阶段

当前阶段仍是 **产品与架构设计收口**，不进入正式 Spec / Tickets / V0.1 开发。

本轮运行时、Godot 工程、内容数据和测试架构已经完成设计校准。

当前工作优先级转为：

1. 收口黑水事件具体人物、隐藏真相和主要结果路径。
2. 收口 V0.1 功法 / 术法 / 法宝最小内容。
3. 收口 RTwP 具体动作时间、暂停、移动、攻击、打断、逃跑规则。
4. 收口 Save / AutoSave 策略。
5. 完成全部当前文档最终一致性与 V0.1 范围审查。
6. 通过后再进入 Spec。

## 文档

唯一当前文档入口是 [`docs/00-docs-index.md`](./docs/00-docs-index.md)。

领域术语以 [`CONTEXT.md`](./CONTEXT.md) 为准。旧的多世回溯设计仅存在于 Git 历史与旧分支，不再作为当前实现依据。
