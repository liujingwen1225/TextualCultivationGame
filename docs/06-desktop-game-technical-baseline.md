# 诸世问道：Steam 桌面游戏技术基线

> 状态：当前有效技术方向，游戏引擎最终选型待确认
>
> 原则：先做一款能够在 Steam 上成立的单机像素修仙 RPG，不再以 Web / 小程序 / App 多端统一作为技术约束。

## 1. 平台基线

当前产品目标：

- **P0：Windows 桌面版 / Steam**。
- 键鼠为首要输入方式，手柄支持在核心玩法稳定后补齐。
- Steam Deck / Linux、macOS 是否支持由首版完成度和真实需求决定。
- H5（Web）、微信小程序、Android、iOS、HarmonyOS 不再属于 V0.1 / V1 首发目标。

桌面端不是 H5 外壳，而是真正的本地游戏运行时。

## 2. 游戏形态

当前正式方向：

> **东方修仙像素人生模拟 Roguelite RPG。**

玩家直接控制角色在像素世界中移动、进入地点、接触 NPC、探索、修炼和战斗；系统深度仍主要由事件、Knowledge、NPC / 世界状态、功法、因果、随机、Anchor 与多世回溯提供。

地图负责：

- 空间感。
- 沉浸感。
- 探索路径。
- NPC / 地点交互入口。

Event Engine 负责：

- 条件事件。
- 事件链。
- Knowledge 选项。
- 词条选项。
- NPC / 世界状态变化。
- 多世差异。

不做“万物皆可交互”的全世界模拟，不以复刻《Project Zomboid》的系统规模为目标。

## 3. V0.1 视觉目标

视觉目标是：

> **高质量东方像素 RPG + 少量现代光影与粒子表现。**

首版不以复刻 HD-2D 精品 JRPG 制作规格为目标。

建议范围：

- 俯视 / 斜俯视像素地图。
- 2D 像素角色。
- Tile 地图。
- 基础昼夜 / 雾效 / 粒子 / 动态光源按需要加入。
- 关键 NPC、死亡、轮回、事件可以使用立绘 / 插画加强表现。
- Anchor、Knowledge、承世 / 悟世继续保留墨青、暗金、青玉、命书 / 道纹等既有视觉语言。

## 4. 当前引擎候选

引擎最终选型尚未锁死，只保留两个候选：

### A. Java + libGDX

适合方向：

- 2D 像素为主。
- 系统和代码驱动。
- 希望保留 Java 技术优势。
- 希望 Gradle / CLI / Codex 自动开发链尽量直接。
- 希望核心规则与运行时都保持普通 Java 工程体验。

### B. Godot + C#

适合方向：

- 更依赖场景编辑器。
- 更强调地图、动画、粒子、灯光与可视化制作效率。
- 未来可能提高 2.5D / 3D 表现比例。

### 最终选型原则

不是比较“哪个引擎更强”，而是比较哪个更适合本项目：

1. 像素地图与角色开发效率。
2. 一个人 + AI 的长期维护成本。
3. Codex 是否能通过 CLI 独立完成构建、运行、测试和修复。
4. Headless / Scenario Test 能力。
5. UI、动画、地图、粒子等美术生产工作量。
6. Steam Windows 打包与发布链路。
7. 后续 Steam Deck / Linux 的可行性。

在该选型完成前，不进入正式游戏客户端实现。

## 5. 已废弃的旧技术方向

以下内容不再是当前实现基线：

- uni-app x。
- H5（Web）作为主客户端。
- 微信小程序作为首发目标。
- Spring Boot 作为单机游戏权威运行时。
- REST 作为本地玩法调用边界。
- PostgreSQL 作为首版本地游戏存档。
- MyBatis-Flex 作为首版游戏持久化层。
- LiteFlow 作为首版游戏流程编排层。
- 为移动端 / 小程序设计的账号登录和多端基础设施。

这些方案保留在 Git 历史中，不再作为实现依据。

## 6. 新运行时架构原则

单机首版采用本地权威游戏运行时：

```text
Desktop Game
├─ Presentation / Scene
├─ Input
├─ Game Core
├─ Event Engine
├─ World / Map
├─ Combat
├─ Content
├─ Save System
└─ Platform Integration
```

客户端本身就是游戏运行时，不再通过 REST 调用一个常驻 Java 服务端决定每一步玩法。

无论最终使用 Java 还是 C#，必须保持：

- Game Core 与渲染 / 场景层解耦。
- Event Engine 与 UI 解耦。
- 确定性随机统一通过 RandomSource 一类抽象访问。
- Anchor / Life / Knowledge / Trait / Inheritance 等核心领域规则可脱离画面测试。
- 内容规则不能依赖任意运行时脚本直接修改存档。

## 7. 本地存档

V0.1 不需要服务端数据库。

建议：

- 静态内容：JSON / YAML / 引擎资源文件。
- 游戏存档：结构化本地 SaveGame。
- 设置：本地配置文件。
- Steam Cloud：进入 Steam 集成阶段后接入。

SaveGame 至少包含：

```text
World
Anchor[]
CurrentLife
Knowledge
Traits
Inventory
NPCState
WorldState
EventState
VersionInfo
```

仍需保存：

- ruleVersion。
- contentVersion。
- balanceVersion。

用于开发期回放、兼容和调试。

## 8. 多世与确定性随机

旧设计中的两层随机继续保留：

```text
AnchorFateSeed
→ 关键命运

RunVariationSeed
→ 一世变数
```

重大结果仍必须使用与上下文绑定的 keyed / stateless 随机，避免通过无关操作“烧随机”。

地图移动、动画帧数、渲染帧率、输入次数都不能成为改变重大命运的隐式随机源。

## 9. 战斗方向

V0.1 战斗不是项目核心竞争力。

首版原则：

- 小规模。
- 短时。
- 高风险。
- 规则清楚。
- 容易被 AI 自动测试。

暂不做：

- 动作 RPG 连招。
- 大规模弹幕。
- 高成本战斗演出。
- 数十种敌人与数百技能。

具体采用回合制、时间轴制还是极简即时制，在引擎选型后单独确认。

## 10. AI 开发与 E2E 基线

“AI 能完成开发验证闭环”继续作为硬约束，但 E2E 不再等同于 Playwright 浏览器测试。

测试分四层：

```text
L1  Core Unit Test
    核心规则纯代码测试

L2  Scenario Test
    无画面跑完整黑水三世

L3  Headless / Integration Test
    启动真实游戏运行时但不依赖人工操作

L4  Visual / Input Smoke
    启动窗口、模拟输入、截图 / 状态断言
```

AI 开发闭环目标：

```text
Codex 修改代码
→ CLI 构建
→ 运行规则 / Scenario Test
→ 启动 Headless / 游戏实例
→ 读取日志 / 状态 / 截图
→ 修复
→ 再次回归
```

最终引擎必须能支持这条链，而不能要求每次开发都依赖人工在编辑器里点按钮。

## 11. Steam 集成原则

V0.1 先完成可玩 Windows 桌面构建，不让 Steam SDK 阻塞核心玩法。

核心闭环稳定后再接入：

- Steamworks。
- Steam Achievements。
- Steam Cloud。
- Steam Deck / controller 适配。
- 商店构建与发布流程。

首版游戏规则不能依赖 Steam 在线状态才能运行。

## 12. 商业化假设调整

旧移动端方案中的“账号级付费回溯额度”不再作为当前 V0.1 基线。

Steam 单机版本默认按完整游戏体验设计：

- 10 次基础回溯仍可作为世界内规则保留。
- 回溯耗尽后的继续方式重新做游戏内设计。
- 是否采用买断、DLC 或其他商业模式在后续商业化文档确认。
- 不在核心玩法中预埋必须依赖账号充值才能继续的逻辑。

## 13. 当前结论

当前已经锁定：

```text
平台：Steam / Windows 优先
形态：东方修仙像素人生模拟 Roguelite RPG
表现：可探索像素世界 + 事件驱动叙事
核心：Anchor + Knowledge + 多世回溯
架构：单机本地权威运行时
AI：必须支持 CLI + 自动测试闭环
```

当前尚未锁定：

```text
Java + libGDX
vs
Godot + C#
```

引擎选型是下一项技术决策。