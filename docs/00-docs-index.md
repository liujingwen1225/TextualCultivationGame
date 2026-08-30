# 文档索引与优先级

> 分支：`redesign/orthodox-cultivation-rpg-docs`
>
> 本目录只保留当前“正统修仙人生模拟 RPG”方向的有效文档。旧多世回溯方案不再属于当前设计基线。

## 当前唯一有效文档

| 编号 | 文档 | 作用 |
|---|---|---|
| 01 | `01-product-vision.md` | 产品定位、核心体验、玩法支柱、明确不做什么 |
| 02 | `02-cultivation-character.md` | 境界、修炼、属性、功法、术法、神通、伤势与寿元 |
| 03 | `03-combat-build.md` | RTwP 战斗、位置、Build、资源、伤势与战斗边界 |
| 04 | `04-world-npc-events.md` | 世界时间、地图、NPC、关系、势力、经济、事件与情报 |
| 05 | `05-v0.1-vertical-slice.md` | 第一阶段连续人生纵切范围与验收目标 |
| 06 | `06-technical-baseline.md` | Godot + C# 技术基线与硬约束 |
| 07 | `07-runtime-architecture.md` | Game Core / Application / Godot / Content / Save 分层 |
| 08 | `08-ui-ux-direction.md` | 桌面交互、HUD、面板和信息呈现原则 |
| 09 | `09-visual-style.md` | 纯 2D 东方修仙像素视觉基线 |
| 10 | `10-project-status.md` | 当前阶段、已锁定、待设计、开发禁令和下一步 |

根目录：

- `README.md`：项目入口和最短版本方向。
- `CONTEXT.md`：当前领域词汇唯一真相源。

Agent 约束：

- `docs/agents/domain.md`
- `docs/agents/spec-workflow.md`
- `docs/agents/issue-tracker.md`
- `docs/agents/triage-labels.md`

## 冲突优先级

当文档、Issue、代码或旧分支发生冲突时，按以下顺序处理：

1. 用户最新明确确认的决策。
2. `CONTEXT.md` 当前领域定义。
3. 本索引列出的当前有效文档。
4. 不与新产品决策冲突的 ADR。
5. 现有实现。
6. Git 历史、旧分支、旧 Issues 和旧设计稿。

## 已废弃方向

以下方向已经正式退出当前产品设计：

- Roguelite 多世回溯 RPG。
- Anchor / 天衍锚点。
- 10 次回溯。
- 承世 / 悟世。
- 跨世 Trait。
- 跨世 Knowledge。
- 黑水三世作为强制三次死亡结构。
- World Fate / 结世 / 跨 World Meta 作为当前核心结构。
- Java + libGDX 作为当前引擎候选。
- Engine Spike 作为当前阶段任务。
- H5 / uni-app x / 小程序首发。
- Spring Boot / REST 作为游戏玩法运行时。
- PostgreSQL / MyBatis-Flex 作为首版本地存档。
- LiteFlow 作为首版事件/流程核心。

历史内容仍保存在 Git 历史以及 `redesign/steam-pixel-rpg` 等旧分支中，不复制到当前有效 `docs/`，避免 AI/开发人员误读。

## 当前产品一句话

> **一款 Steam / Windows 优先的东方修仙人生模拟 RPG：玩家在持续运转的修真社会中修炼、探索、战斗、调查、建立关系、争夺机缘并承担长期后果。**

## 当前技术一句话

> **Godot 4.7.x .NET + C# + .NET 8 + 纯 2D；权威规则在纯 C# Game Core，Godot 负责表现、输入与适配。**

## 当前阶段

当前仍处于设计与架构收口期。

禁止直接从旧 Spec / Issues 开始正式开发。必须先完成：

```text
文档一致性清理
→ V0.1 连续人生纵切收口
→ 运行时 / Godot / 内容数据 / 测试架构校准
→ 设计审查
→ 再进入 Spec
```
