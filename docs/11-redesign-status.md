# Steam 像素 RPG 重设计状态

> 分支：`redesign/steam-pixel-rpg`
>
> 用途：记录本分支在合并 main 前的重设计状态，避免旧移动端 / Web 技术基线与新方向混用。

## 已锁定

- 产品平台：Steam / Windows 优先。
- 产品形态：东方修仙像素人生模拟 Roguelite RPG。
- 玩家在真实像素地图中移动、探索、接触 NPC、修炼和战斗。
- 地图负责沉浸与空间选择，Event Engine 负责内容深度。
- Anchor / Knowledge / 承世 / 悟世 / 多世回溯继续作为核心系统。
- V0.1 继续使用黑水三世垂直切片。
- 10 次回溯是世界内有限资源，不再绑定账号付费续命。
- 单机本地权威运行时。
- AI / Codex 自动开发与测试是硬约束。
- Engine Spike 使用稳定版 libGDX 1.14.2 与 Godot 4.7.2 .NET。
- Engine Spike 的统一范围、评分和淘汰条件以 `12-engine-spike-spec.md` 为准。

## 已废弃

- 纯文字 / H5 页面式产品形态。
- uni-app x 作为当前客户端基线。
- 微信小程序作为首发目标。
- Spring Boot + REST 作为游戏运行时。
- PostgreSQL + MyBatis-Flex 作为首版游戏存档。
- LiteFlow 作为首版游戏流程编排。
- 账号级付费回溯额度。
- 移动端竖屏 UI 作为设计基准。
- 《八方旅人》规模的 HD-2D 制作目标。
- 《Project Zomboid》规模的全沙盒世界模拟。

## 待锁定

- Java + libGDX 或 Godot + C#。
- 战斗制式：回合 / 时间轴 / 极简即时。
- 角色与 Tileset 基础像素规格。
- V0.1 最终输入方案与手柄范围。
- Steamworks 接入阶段。

## 当前阶段

设计一致性清理已经完成到可以进入 Engine Spike：

- 旧 Java Web / LiteFlow 当前技术文档已退出本分支。
- `docs/agents/spec-workflow.md` 已切换为 Game Core / Scenario / SaveGame / Runtime 测试缝。
- Spike 规格已经固定，不再继续扩张产品范围。

下一阶段在独立子分支执行：

```text
spike/engine-selection
```

该子分支上的代码属于一次性技术验证，不视为正式 V0.1 实现。

## 合并 main 前必须完成

1. 完成 Engine Spike 决策。
2. 将最终引擎写入技术基线。
3. 对 `07-game-runtime-event-architecture.md` 做引擎落地校准。
4. 重新生成 V0.1 Spec / Issues / Tickets。
5. 审查现有 Issues，关闭或改写移动端 / Web 相关票。
6. 对全部 docs 做一次一致性审查。
7. 只保留胜出引擎需要的正式实现方向。

## 当前开发禁令

在最终引擎选型完成前：

- 不开发正式 V0.1 客户端。
- 不继续实现旧 Spring Boot 服务端。
- 不按旧 H5 Issues 开工。
- 不扩充黑水之外的大量内容。
- 不把 Spike 代码未经评审直接升级为正式架构。

允许的下一步只有：

> **执行 Engine Spike → 形成有证据的引擎结论 → 更新技术基线 → 进入 to-spec。**
