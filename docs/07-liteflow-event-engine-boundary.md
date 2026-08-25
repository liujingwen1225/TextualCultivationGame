# 诸世问道：LiteFlow、Event Engine 与规则边界

> 状态：当前有效技术设计

## 1. 核心结论

LiteFlow 只负责 **高层业务流程编排**。

Event Engine 负责 **事件内部规则执行**。

二者不能同时拥有事件解析权，否则会形成两套规则系统。

## 2. LiteFlow 负责什么

建议保留这些高层 Chain：

```text
CULTIVATE
BREAKTHROUGH
COMBAT
EXPLORE
SECRET_REALM_EXPLORE
DEATH_SETTLEMENT
REWIND
ANCHOR_CREATE
```

它们负责描述：

```text
先做什么
→ 再做什么
→ 哪一步失败后走哪里
```

例如修炼：

```text
校验角色状态
→ 计算游戏内时间
→ 计算修为变化
→ 推进世界时间
→ 调用 Event Scheduler
→ 应用结果
→ 保存
```

LiteFlow 不应该直接保存某个具体“赵长青下毒事件”的剧情规则。

## 3. Event Engine 负责什么

Event Engine 完整拥有：

- 事件定义加载。
- Condition 判断。
- 事件候选过滤。
- Choice 可见性。
- Choice 合法性。
- Effect 执行。
- Knowledge 奖励。
- NPC / 世界状态 Effect。
- 事件链推进。
- 后续事件触发。
- 事件重复策略。

典型调用：

```text
LiteFlow Node
  ↓
EventEngine.findCandidates(context)
  ↓
Scheduler.choose(...)
  ↓
返回待处理 Event
```

玩家选择后：

```text
Application Service
  ↓
EventEngine.resolve(eventId, choiceId, context)
  ↓
得到 Effects
  ↓
统一应用到聚合状态
```

## 4. 删除 EVENT_RESOLVE Chain

不再保留独立 LiteFlow：

```text
EVENT_RESOLVE
```

原因：

如果事件内部也交给 LiteFlow，就会出现：

```text
Event Engine 判断条件
LiteFlow 再判断条件
Event Engine 执行 Effect
LiteFlow 再编排 Effect
```

后续内容编辑将难以确定规则到底属于哪里。

因此正式边界：

> 流程进 Event Engine，事件内部不再回 LiteFlow 编排。

## 5. Condition

Condition 采用类型化规则，不在内容文件里嵌任意脚本。

示例：

```yaml
conditions:
  all:
    - type: REALM_AT_LEAST
      value: QI_REFINING_3
    - type: LOCATION_IS
      value: BLACKWATER
    - type: KNOWLEDGE_NOT_HAS
      value: K_BLACKWATER_POISON
```

推荐 Condition 类型：

- 境界。
- 修为。
- 时间。
- 地点。
- 身体状态。
- NPC 状态。
- 关系值。
- Knowledge。
- 词条。
- 物品。
- 事件历史。
- 世界模板标签。

Condition 实现属于 Java Game Core / Event Engine，不属于 LiteFlow Node。

## 6. Effect

Effect 同样类型化：

```yaml
effects:
  - type: ADVANCE_TIME
    days: 3
  - type: ADD_KNOWLEDGE
    knowledgeId: K_BLACKWATER_POISON
  - type: APPLY_STATUS
    status: POISONED
  - type: CHANGE_RELATION
    npcId: NPC_ZHAO
    dimension: TRUST
    delta: -20
```

常见 Effect：

- 时间推进。
- 修为变化。
- 资源变化。
- 物品变化。
- NPC 状态。
- 关系变化。
- Knowledge。
- 事件链状态。
- 伤势 / 中毒。
- 死亡。

Effect 先生成结构化结果，再由 Application 层统一提交状态。

## 7. Event Scheduler

Scheduler 不负责事件具体剧情，只负责从合法候选里确定“现在发生什么”。

输入：

- 游戏时间。
- Event Pressure。
- 地点。
- 当前事件链。
- Run Theme。
- NPC 相关性。
- 冷却。
- 重复次数。
- 世界状态。

输出：

- 强制事件；或
- 一个候选事件；或
- 无事件。

不要让 Event Pressure 由点击数简单累加。

## 8. 两层随机系统

### Anchor Fate Random

服务关键命运：

- 核心真相。
- 关键人物身份。
- 重大事件固定变量。
- 重要传承。

同一 Anchor 下应稳定。

### Run Variation Random

服务一世变化：

- 普通随机事件。
- 市场商品。
- 一般奇遇。
- 非关键遭遇。

每一世可以变化。

## 9. Keyed Random

关键规则不能直接调用有状态全局 RNG。

推荐接口概念：

```java
interface RandomSource {
    int range(RandomKey key, int min, int max);
    boolean chance(RandomKey key, int basisPoints);
}
```

`RandomKey` 包含有意义上下文：

```text
seed namespace
anchorId / fateSeed
lifeId / variationSeed（按需要）
eventId
choiceId
contextKey
```

关键结果使用 Anchor Fate 范围；普通枝节使用 Run Variation 范围。

## 10. 防止烧随机

不得把这些字段作为重大结果的唯一随机上下文：

- actionSequence。
- API 请求次数。
- 点击次数。
- 无关事件发生次数。

可以使用 actionSequence 做审计编号，但不能让玩家通过无关操作改变关键随机。

必须存在测试：

```text
相同 Anchor
相同关键状态
相同选择
不同无关操作序列
→ 重大结果相同
```

## 11. 死亡结算流程

死亡结算是 LiteFlow 高层流程，但候选计算分别委托领域服务。

```text
DEATH_SETTLEMENT
├─ FreezeLifeNode
├─ BuildLifeFactsNode
├─ BuildEvaluationNode
├─ UpdateKnowledgeAssessmentNode
├─ BuildInheritanceCandidatesNode
├─ BuildTraitCandidatesNode
└─ WaitForPlayerSettlementDecisionNode
```

玩家提交后由 Application 层完成：

```text
选择 Anchor
+ 承世 / 悟世
+ 回溯额度校验
→ REWIND Chain
```

不要在 LiteFlow Node 内直接弹 UI 或等待网络线程。

## 12. Anchor Create 流程

```text
ANCHOR_CREATE
├─ ValidateRealmAnchorSlot
├─ ValidateStableState
├─ BuildImmutableSnapshot
├─ CreateAnchorFateSeed
└─ PersistAnchor
```

必须由数据库唯一约束再次保证：

```text
(world_id, major_realm) UNIQUE
```

初始 Anchor 在 New World 流程中自动创建，不调用玩家手动定世入口。

## 13. 承世规则服务

承世候选应由独立领域服务计算：

```text
InheritancePolicy
```

负责：

- 只允许死亡时实际持有 / 掌握。
- 排除境界和修为。
- 堆叠上限。
- 容器展开限制。
- 唯一物状态迁移。
- 目标 Anchor 的兼容展示。

不要把这些判断散落在 Controller 或 LiteFlow XML 中。

## 14. 词条规则服务

建议：

```text
LifeFactCollector
TraitEligibilityEngine
TraitRankingService
```

流程：

```text
一世事实
↓
匹配所有词条形成条件
↓
计算代表性评分
↓
处理已有词条升级候选
↓
取最多 3 个
```

AI 不参与 Eligibility 或 Ranking。

## 15. Knowledge 规则服务

Knowledge 分为两件事：

```text
Knowledge Record
= 玩家记得的事实

Applicability Assessment
= 该事实对当前人生是否仍适用
```

因此世界线变化时：

- 不 DELETE Knowledge。
- 重新评估 applicability。

Event Choice 可以根据两者分别判断：

```text
HAS_KNOWLEDGE
KNOWLEDGE_APPLICABLE
```

避免把“曾经知道”与“现在仍然成立”混成一个布尔值。

## 16. 事务边界

Application Service 管事务。

推荐：

```text
@Transactional
handleCommand(...)
  ↓
lock world/life
  ↓
run domain / LiteFlow / Event Engine
  ↓
apply changes
  ↓
persist
```

LiteFlow Node：

- 不自行开启独立业务事务。
- 不直接绕过 Repository 边界乱写数据库。
- 尽量纯计算或调用领域服务。

## 17. V0.1 实现最小集

只需要实现：

### LiteFlow

- CULTIVATE。
- EXPLORE。
- DEATH_SETTLEMENT。
- REWIND。
- 一个简化 BREAKTHROUGH。

### Event Engine

- Condition。
- Choice。
- Effect。
- Knowledge。
- Event Chain。
- Scheduler。

### Random

- Anchor Fate Seed。
- Run Variation Seed。
- Keyed Random。

多 Anchor `ANCHOR_CREATE` 可以先有领域模型和测试，正式 UI/完整流程放 V0.2。

## 18. 最终边界图

```text
Controller
   ↓
Application Service  ← 事务 / 幂等 / 权限
   ↓
LiteFlow             ← 高层业务步骤
   ↓
Domain Services
   ├─ Event Engine   ← 事件内部规则
   ├─ Scheduler      ← 事件调度
   ├─ RandomSource   ← 确定性随机
   ├─ Trait Engine   ← 悟世
   ├─ Inheritance    ← 承世
   └─ Knowledge      ← 记忆与适用性
   ↓
Repository
   ↓
PostgreSQL
```

当未来出现复杂功能时，优先新增明确领域服务，而不是继续把规则塞进 LiteFlow Node。