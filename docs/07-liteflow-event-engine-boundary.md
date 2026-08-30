# 诸世问道：旧 LiteFlow / Event Engine 边界说明

> 状态：历史方案，不再作为当前实现基线
>
> 当前产品已切换为 Steam / Windows 优先的单机像素 RPG。LiteFlow 属于旧 Spring Boot 服务端技术路线，因此本文件不再指导实现。

## 仍然保留的设计原则

虽然 LiteFlow 已退出当前技术方向，但以下边界思想继续有效：

- Event Engine 独占事件内部规则执行。
- Condition、Choice、Effect 是明确的领域概念。
- 事件规则不能散落在 UI、场景脚本和存档层中。
- 游戏流程与事件内部解析应保持职责分离。
- 确定性随机必须由统一 RandomSource 一类能力提供。
- UI / Scene 不拥有权威游戏规则。
- 核心规则必须可脱离渲染和输入自动测试。

## 已废弃内容

以下内容不再适用：

- LiteFlow Chain 作为 CULTIVATE / EXPLORE / DEATH_SETTLEMENT / REWIND 的正式编排机制。
- Spring Application Service + LiteFlow Node + PostgreSQL 事务边界。
- LiteFlow Node 与 Mapper / Repository 的旧实现约束。

新的流程组织方式将在最终确定 `Java + libGDX` 或 `Godot + C#` 后写入 `06-desktop-game-technical-baseline.md` 的后续版本。

Git 历史中仍可查看本文件原始完整设计。