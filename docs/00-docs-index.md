# 诸世问道文档索引

> 状态：`redesign/steam-pixel-rpg` 当前有效基线
>
> 本分支正在把项目从移动端文字/H5 方案重设计为 **Steam / Windows 东方修仙像素人生模拟 Roguelite RPG**。领域术语以根目录 `CONTEXT.md` 为准。

## 当前产品方向

```text
Steam / Windows
+
可探索像素世界
+
事件驱动内容
+
Anchor / Knowledge / 多世回溯
+
单机本地权威运行时
```

## 当前文档结构

1. `01-core-game-design.md`
   - Steam 像素 RPG 产品定位
   - 四个设计支柱
   - 核心循环
   - 世界探索原则
   - 战斗与 AI 边界

2. `02-reincarnation-anchor-inheritance.md`
   - 10 次基础回溯
   - 多 Anchor / 定世
   - 死亡结算
   - 承世 / 悟世
   - Knowledge / 词条 / 唯一物
   - 世界终局总结

3. `03-cultivation-events-pacing.md`
   - 修炼成长
   - 游戏内时间
   - Event Pressure
   - 事件调度
   - 锚点命运 / 一世变数
   - 战斗、伤势、死亡

4. `04-world-content-system.md`
   - 可探索像素地图边界
   - 地点 / Zone / 交互点
   - NPC 状态与简单日程
   - 宗门 / 秘境
   - Event Trigger / Condition / Effect
   - 多世内容生产规则

5. `05-v0.1-playable-prototype.md`
   - Steam Windows 可玩纵切
   - 青玄宗 / 坊市 / 黑水山脉 / 黑水秘境
   - 黑水三世路线
   - 最小战斗
   - Save / Load
   - Scenario / Headless 测试

6. `06-desktop-game-technical-baseline.md`
   - Steam / Windows 平台基线
   - 单机本地权威运行时
   - 本地 SaveGame
   - AI 自动开发与测试要求
   - Java + libGDX / Godot + C# 两候选
   - 旧 Web 服务端技术方向废弃清单

7. `07-game-runtime-event-architecture.md`
   - Game Core / Application / Scene 边界
   - World / Map
   - Event Engine
   - Combat
   - RandomSource
   - Save System
   - Scenario / Visual Smoke Test

8. `08-v0.1-ui-prototype.md`
   - 桌面 HUD
   - 键鼠交互
   - 地图交互
   - 事件 / 对话
   - 角色 / Knowledge / 战斗 UI
   - 死亡 / Anchor / 承世 / 悟世

9. `09-ui-visual-style-guide.md`
   - 东方像素世界
   - Tile / Sprite / 光影原则
   - 青玄宗 / 坊市 / 黑水区域视觉
   - Knowledge / Anchor / 轮回视觉
   - 美术资产上限

10. `10-engine-selection.md`
    - Java + libGDX 与 Godot + C# 比较
    - 当前倾向
    - Engine Spike 范围
    - 最终锁定规则

11. `11-redesign-status.md`
    - 本重设计分支已锁定 / 已废弃 / 待锁定内容
    - 合并 main 前检查清单

12. `12-engine-spike-spec.md`
    - libGDX 1.14.2 / Godot 4.7.2 固定版本
    - 两候选完全相同的最小玩法输入
    - Game Core / Scene 解耦约束
    - CLI / Scenario / Headless 验收
    - 100 分加权评分与硬淘汰条件

## 文档关系

```text
01 产品核心
 ↓
02 多世核心规则
 ↓
03 成长 / 时间 / 风险
 ↓
04 世界与内容
 ↓
05 V0.1 纵切
 ↓
06 技术基线
 ↓
07 运行时架构
 ↓
08 交互
 ↓
09 视觉
 ↓
10 引擎决策
 ↓
12 Engine Spike 规格
```

`11-redesign-status.md` 记录整个重设计分支的阶段状态，不属于设计依赖链。

## 设计优先级

发生冲突时：

```text
用户最新已确认决策
  ↓
CONTEXT.md
  ↓
01~12 当前有效文档
  ↓
代码实现
```

旧代码不能反向恢复已经废弃的设计。

## 当前不可恢复的旧方向

### 产品 / 平台

- 纯文字菜单作为正式产品形态。
- H5（Web）作为主客户端。
- 微信小程序作为首发目标。
- Android / iOS / HarmonyOS 多端统一作为 V0.1 约束。
- 移动端竖屏作为主 UI 基准。

### 技术

- uni-app x 作为当前客户端基线。
- Spring Boot + REST 作为单机玩法运行时。
- PostgreSQL + MyBatis-Flex 作为首版本地存档。
- LiteFlow 作为首版游戏流程编排。
- jOOQ 作为正式数据访问基线。
- V0.x 多代 Java 规则运行时。

### 商业化

- 账号级付费回溯额度。
- “基础次数耗尽后充值继续当前世界”的移动端商业模型。

### 玩法 / 制作规模

- 单 Anchor 回溯。
- 独立 Insight 核心系统。
- 境界 / 修为承世。
- 固定现实小时升级指标。
- 点击次数累计 Event Pressure。
- 《八方旅人》规格 HD-2D 制作目标。
- 《Project Zomboid》规格万物模拟沙盒。

## 当前尚未锁定

- Java + libGDX 或 Godot + C#。
- 战斗制式。
- 基础像素规格。
- 手柄首版范围。

这些内容必须先通过 Engine Spike 或专项设计确认，不能由实现代码自行决定。

## 当前开发顺序

```text
完成本分支设计一致性清理
↓
按 12-engine-spike-spec 执行 Engine Spike
↓
锁定唯一引擎
↓
更新 06 技术基线
↓
to-spec
↓
to-tickets
↓
开始正式开发
```

Engine Spike 属于设计验证原型，不使用旧 GitHub V0.1 Issues 作为实现任务。在引擎锁定前，不按旧 H5 / Spring Boot Issues 继续开发。
