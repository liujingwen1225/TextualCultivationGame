# 诸世问道文档索引

> 状态：当前有效基线
>
> 本目录已按最新设计重新建立。旧 16 份设计文档全部废弃，不再作为实现依据。领域术语以根目录 `CONTEXT.md` 为准。

## 文档结构

1. `01-core-game-design.md`
   - 游戏定位
   - 核心循环
   - 长期目标
   - 一局与一世的关系

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
   - V0.1 唯一验收目标
   - 黑水可玩场景
   - 三世验证路径
   - 测试与完成标准

6. `06-java-technical-architecture.md`
   - Java 技术基线
   - 模块化单体
   - 数据模型原则
   - 版本策略
   - V0.x 技术范围

7. `07-liteflow-event-engine-boundary.md`
   - LiteFlow 与 Event Engine 职责
   - 确定性随机
   - 事务与流程边界
   - 内容规则执行方式

8. `08-v0.1-ui-prototype.md`
   - V0.1 页面地图与信息架构
   - 推演主界面 / 事件页
   - 角色、词条与 Knowledge
   - 一世总结 / Anchor 选择
   - 承世 / 悟世 / 下一世词条配置
   - 回溯耗尽与主动坐化
   - 移动端原型验收标准

## 设计优先级

发生冲突时按以下顺序解释：

```text
用户最新已确认决策
  ↓
CONTEXT.md 领域定义
  ↓
01~08 当前设计文档
  ↓
代码实现
```

如果代码与当前设计冲突，应先判断代码是否尚未迁移，不应反过来用旧代码恢复已废弃的规则。

## 当前不可恢复的旧设计

以下旧方向已明确废弃：

- 单 Anchor 回溯。
- 独立 Insight 核心系统。
- 使用天衍本源从境界 / 修为 / 物品 / 功法 / 装备中任意固道一项的旧模型。
- 境界和修为跨世继承。
- 固定“首次筑基 15～30 小时”等现实时间承诺。
- 通过点击次数累计 Event Pressure。
- LiteFlow `EVENT_RESOLVE` 与 Event Engine 重复负责事件解析。
- V0.1 同时完成微信、手机号、正式多端发布、真实支付等完整产品基础设施。
- V0.x 永久并存多代 Java 规则运行时。
