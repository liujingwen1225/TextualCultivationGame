# 诸世问道文档索引

> 状态：当前有效基线
>
> 当前产品方向已从“移动端 / H5 文字修仙”调整为 **Steam / Windows 优先的东方修仙像素人生模拟 Roguelite RPG**。领域术语仍以根目录 `CONTEXT.md` 为准。

## 当前有效文档

1. `01-core-game-design.md`
   - 游戏定位
   - 核心循环
   - 长期目标
   - 一局与一世的关系
   - 核心规则仍有效，但表现层应按当前像素 RPG 方向理解

2. `02-reincarnation-anchor-inheritance.md`
   - 10 次基础回溯
   - 多 Anchor
   - 定世
   - 死亡结算
   - 承世 / 悟世
   - Knowledge / 词条 / 唯一物

3. `03-cultivation-events-pacing.md`
   - 修炼成长
   - 突破
   - 游戏内时间
   - Event Pressure
   - 随机与确定性
   - 战斗、伤势、死亡

4. `04-world-content-system.md`
   - 世界模板
   - 地图、宗门、NPC、秘境
   - 内容对象与事件结构
   - 新世界组合
   - AI 使用边界

5. `05-v0.1-playable-prototype.md`
   - V0.1 黑水三世纵切
   - 当前按 Steam 像素 RPG 表现重新定义
   - 小范围地图、NPC、事件、战斗和回溯闭环
   - 测试与完成标准

6. `06-desktop-game-technical-baseline.md`
   - Steam / Windows 首发目标
   - 单机本地权威运行时
   - 像素 RPG 技术边界
   - Java + libGDX 与 Godot + C# 两个候选
   - 本地 SaveGame
   - Scenario / Headless / Visual Smoke 自动测试
   - AI / Codex CLI 开发闭环

## 已废弃 / 待重做文档

以下文档不再是当前实现基线，只保留历史设计价值：

- `06-java-technical-architecture.md`
  - 旧 Spring Boot + PostgreSQL + MyBatis-Flex + uni-app x 技术方案。
  - 已由 `06-desktop-game-technical-baseline.md` 替代。

- `07-liteflow-event-engine-boundary.md`
  - LiteFlow 属于旧 Java 服务端方案。
  - Event Engine 的职责思想仍可参考，但 LiteFlow 实现边界不再有效。

- `08-v0.1-ui-prototype.md`
  - 旧移动端竖屏页面原型。
  - 当前需要重做为桌面像素 RPG 的 HUD、地图交互、事件、角色、死亡结算和轮回界面。

- `09-ui-visual-style-guide.md`
  - 旧移动端 UI 视觉规范。
  - 其中墨青、暗金、青玉、命书 / 道纹、轮回仪式感等视觉语言继续保留。
  - 移动端竖屏、底部导航、卡片式主界面等布局规则不再有效。

## 当前产品表现关系

```text
可探索像素地图
= 空间 / 沉浸 / NPC 与地点入口

Event Engine
= 条件事件 / 连锁事件 / Knowledge / 词条 / 世界状态

Anchor + Knowledge + 多世回溯
= 项目核心差异化
```

不做：

- 纯文字菜单游戏作为最终形态。
- 完整世界模拟。
- 复刻 Project Zomboid 的系统规模。
- 复刻八方旅人的 HD-2D 制作规模。

## 设计优先级

发生冲突时按以下顺序解释：

```text
用户最新已确认决策
  ↓
README 当前项目定位
  ↓
CONTEXT.md 领域定义
  ↓
本索引列出的当前有效文档
  ↓
旧 / 待重做文档
  ↓
代码实现
```

如果代码、Issue 或旧文档仍然出现 H5、uni-app x、Spring Boot 游戏服务端、MyBatis-Flex、LiteFlow 等旧实现方向，应先判断是否尚未迁移，不得据此恢复旧技术路线。

## 当前不可恢复的旧方向

- 单 Anchor 回溯。
- 独立 Insight 核心系统。
- 境界和修为跨世继承。
- 固定“首次筑基 15～30 小时”等现实时间承诺。
- 通过点击次数累计 Event Pressure。
- V0.1 同时完成多端发布、登录和支付基础设施。
- uni-app x / H5（Web）/ 微信小程序作为首发客户端路线。
- Spring Boot + REST 作为单机玩法权威运行时。
- PostgreSQL + MyBatis-Flex 作为首版本地存档。
- LiteFlow 作为首版游戏流程编排层。
- 账号级付费回溯额度作为 V0.1 必备商业化机制。
- 移动端竖屏 UI 作为正式界面基线。

## 当前下一项关键技术决策

```text
Java + libGDX
vs
Godot + C#
```

在最终引擎选型完成前，不进入正式桌面客户端大规模实现。