# 诸世问道：LiteFlow 游戏流程编排规范

> 状态：技术基线 V1.0  
> 依赖：`14-java-technical-architecture.md`  
> 目标：明确 LiteFlow 在《诸世问道》中的职责、边界、版本策略与开发规范，避免流程编排、事件内容和游戏规则相互污染。

---

# 1. 结论

项目正式引入 **LiteFlow**，作为 Java Game Engine 的**流程编排层**。

当前基线版本：

- LiteFlow 2.16.1
- Java 25 LTS
- Spring Boot 4.1.x

LiteFlow 的定位必须固定为：

> **负责“规则组件以什么顺序执行”，不负责“整个游戏世界如何运转”。**

LiteFlow 不替代：

- Event Engine
- Event Scheduler
- Deterministic Random
- Game State
- Cultivation / Combat 等领域算法
- Anchor / Run / Meta
- Knowledge / Insight
- Persistence

---

# 2. 为什么引入 LiteFlow

《诸世问道》的玩家行动通常不是一次简单 CRUD，而是一组有顺序、有条件、有分支的领域步骤。

例如“闭关 30 日”：

```text
校验是否可闭关
↓
锁定当前行动上下文
↓
消耗资源
↓
推进时间
↓
计算修为
↓
处理身体状态
↓
累计事件压力
↓
执行事件调度
↓
处理可能触发的事件
↓
检查死亡 / 中断
↓
生成行动结果
```

如果完全手写 Application Service，长期会产生大量：

- if / else
- switch
- 嵌套流程
- 重复事务编排
- 难以阅读的超长方法

LiteFlow 用组件 + DSL 表达这些流程，使流程结构本身可读、可测试、可替换。

---

# 3. 总体位置

```text
Spring Boot
    │
    ▼
Application Layer
    │
    ▼
Game Engine
    │
    ├── LiteFlow Orchestration
    │      ├── Cultivation Flow
    │      ├── Breakthrough Flow
    │      ├── Combat Flow
    │      ├── Exploration Flow
    │      ├── Settlement Flow
    │      ├── Solidification Flow
    │      └── Rewind Flow
    │
    ├── Domain Resolvers
    │      ├── CultivationResolver
    │      ├── CombatResolver
    │      ├── RiskResolver
    │      └── SettlementResolver
    │
    ├── Event Engine
    ├── Event Scheduler
    ├── Deterministic Random
    └── Game State
```

LiteFlow 编排组件，组件内部调用真正的领域规则。

---

# 4. 推荐模块结构

为了保持职责清晰，推荐将纯领域规则和 LiteFlow 编排拆分：

```text
server/
├── game-domain/              # 状态、值对象、领域模型
├── game-core/                # 纯领域规则与 Resolver
├── game-flow/                # LiteFlow 编排与组件
├── game-application/         # 用例 / 事务边界
├── game-infrastructure/      # jOOQ / PostgreSQL / 外部服务
└── game-server/              # Spring Boot / REST / Auth
```

其中：

## game-domain

不得依赖：

- Spring
- LiteFlow
- PostgreSQL
- HTTP

负责：

- RunState
- AnchorState
- MetaState
- CharacterState
- WorldState
- EventState
- Value Objects

## game-core

不得依赖 Spring / Persistence。

负责：

- 修炼算法
- 战斗算法
- 突破算法
- 风险结算
- Event Condition / Effect
- Event Scheduler
- Deterministic Random
- Knowledge / Insight
- 固道领域规则

## game-flow

允许依赖 LiteFlow，但不得直接访问数据库。

负责：

- LiteFlow NodeComponent
- Chain 定义
- Flow Context
- 领域 Resolver 调用
- 流程级分支与顺序

---

# 5. 哪些流程交给 LiteFlow

第一阶段正式使用 LiteFlow 编排：

```text
CULTIVATE
BREAKTHROUGH
COMBAT
EXPLORE
SECRET_REALM_EXPLORE
EVENT_RESOLVE
RUN_SETTLEMENT
SOLIDIFICATION
REWIND
```

未来可以增加：

```text
SECT_TASK
ALCHEMY
CRAFT
TRADE
DAO_COMPANION
MAJOR_REALM_BREAKTHROUGH
```

原则：

> 一个流程代表一个稳定的“游戏行为生命周期”，而不是一个具体剧情事件。

---

# 6. 哪些内容不能做成 LiteFlow Chain

禁止将每个随机事件写成独立 Chain：

```text
EVENT_000001
EVENT_000002
EVENT_000003
...
```

游戏最终可能拥有数百甚至数千个事件。

事件必须继续由数据驱动 Event Engine 管理：

```yaml
id: blackwater_zhao_poison
conditions:
  - type: REALM_MIN
    value: QI_5
  - type: NPC_ALIVE
    npc: ZHAO_CHANGQING

choices:
  - id: accept
    effects:
      - type: ADD_STATUS
        status: POISONED

  - id: memory_antidote
    requiresKnowledge:
      - ZHAO_POISON
    effects:
      - type: CONSUME_ITEM
        item: ANTIDOTE
```

LiteFlow 只需要一个类似：

```text
resolveEvent
```

的组件，内部调用 Event Engine。

---

# 7. Condition / Effect 继续自研

事件条件必须使用自己的领域模型：

```java
public interface EventCondition {
    boolean matches(GameContext context);
}
```

典型实现：

```text
RealmCondition
LocationCondition
ItemCondition
TechniqueCondition
KnowledgeCondition
NpcRelationCondition
KarmaCondition
TimeCondition
WorldFlagCondition
```

组合：

```text
AllOf
AnyOf
Not
```

Effect：

```java
public interface EventEffect {
    void apply(GameMutationContext context);
}
```

典型实现：

```text
AddItemEffect
RemoveItemEffect
AddKnowledgeEffect
AddInsightEffect
ChangeRelationEffect
AddKarmaEffect
AddInjuryEffect
ChangeCultivationEffect
SetWorldFlagEffect
UnlockEventEffect
KillCharacterEffect
```

LiteFlow 不直接替代这些 Condition / Effect。

---

# 8. 确定性随机必须独立

LiteFlow 不负责游戏随机数。

所有游戏随机必须经过统一接口：

```java
public interface RandomSource {
    int nextInt(String namespace, String key, int bound);
    double nextDouble(String namespace, String key);
}
```

随机上下文由：

```text
runSeed
+
actionSequence
+
namespace
+
contextKey
```

共同决定。

禁止 LiteFlow 组件直接调用：

```java
new Random()
Math.random()
ThreadLocalRandom.current()
```

否则会破坏：

> 同一 Run + 同一行为得到可复现结果。

---

# 9. 示例：修炼流程

概念 Chain：

```text
THEN(
    validateCultivation,
    consumeCultivationResources,
    advanceTime,
    calculateCultivation,
    updateBodyState,
    increaseEventPressure,
    scheduleEvent,
    resolveTriggeredEvent,
    checkRunTermination,
    buildActionResult
)
```

组件必须小而稳定。

例如：

```java
@Component("calculateCultivation")
public class CalculateCultivationNode extends NodeComponent {
    @Override
    public void process() {
        GameFlowContext ctx = getContextBean(GameFlowContext.class);
        CultivationResult result = ctx.cultivationResolver()
            .resolve(ctx.state(), ctx.action(), ctx.randomSource());
        ctx.apply(result);
    }
}
```

Node 负责调用，真正公式在 `game-core`。

---

# 10. 示例：筑基流程

```text
THEN(
    validateBreakthrough,
    calculatePreparation,
    calculateBreakthroughRisk,
    executeBreakthrough,
    IF(
        breakthroughSucceeded,
        applyBreakthroughSuccess,
        applyBreakthroughFailure
    ),
    generateBreakthroughInsight,
    updateTimeline,
    buildActionResult
)
```

LiteFlow 负责表达流程结构。

成功率、根基、伤势、功法理解等具体算法仍由 `BreakthroughResolver` 决定。

---

# 11. 示例：死亡 / 诸世结算 / 固道

Run 结束后：

```text
THEN(
    freezeRun,
    analyzeRunEnd,
    extractKnowledge,
    calculateInsight,
    generateLifeRecord,
    calculateSolidificationOptions,
    buildSettlementResult
)
```

如果玩家选择固道：

```text
THEN(
    validateSolidification,
    consumeTianyuanOrigin,
    applySolidification,
    persistMetaMutation,
    prepareRewind
)
```

固道只能调用 `SolidificationResolver`，LiteFlow 组件不得自行修改境界 / 修为 / 物品。

---

# 12. Flow Context

LiteFlow 执行时统一使用游戏流程上下文：

```java
public final class GameFlowContext {
    private GameState state;
    private final GameAction action;
    private final GameContentSnapshot content;
    private final RandomSource randomSource;
    private final MutationCollector mutations;
    private final List<GameDomainEvent> domainEvents;
}
```

禁止把：

- HttpServletRequest
- jOOQ DSLContext
- Repository
- Spring Security Context

塞进 GameFlowContext。

Flow Context 必须保持游戏领域语义。

---

# 13. LiteFlow 与事务边界

数据库事务由 `game-application` 控制，而不是 LiteFlow Node 控制。

正确流程：

```text
BEGIN
↓
SELECT game_run FOR UPDATE
↓
恢复 RunState
↓
Application 创建 GameExecutionContext
↓
执行 LiteFlow Chain
↓
得到新 RunState + Mutations + Domain Events
↓
持久化
↓
写 ActionLog / EventLog
↓
COMMIT
```

禁止：

> 每个 Node 自己开事务或直接写数据库。

否则流程中途失败时很难恢复一致性。

---

# 14. 三版本锁定机制

这是项目硬性规则。

每一个 Run 创建时必须绑定：

```text
ruleVersion
contentVersion
balanceVersion
```

含义：

## ruleVersion

- LiteFlow Chain 版本
- 领域规则兼容版本

## contentVersion

- 事件
- NPC
- 功法
- 物品
- 地点
- 秘境

## balanceVersion

- 数值平衡
- 权重
- 修炼速度
- 事件压力
- 战斗参数

例如：

```text
Run #128
runSeed        = 78541932
ruleVersion    = 3
contentVersion = 2026.08.2
balanceVersion = 5
```

---

# 15. 热更新原则

LiteFlow 支持规则热更新，但游戏不能直接采用“全局即时覆盖”。

禁止出现：

```text
玩家正在 Run #128
↓
运营修改流程
↓
Run #128 中途突然变成新规则
```

正确策略：

```text
发布 Rule Version 4
↓
已有 Run 继续使用 Rule Version 3
↓
新 Run 使用 Rule Version 4
```

如果旧版本出现严重 BUG，可以提供明确的 Run Migration，而不是静默替换。

因此：

> LiteFlow 的热刷新能力是发布工具，不是打破 Run 确定性的理由。

---

# 16. Chain 存储与版本管理

V0.1 推荐规则跟代码仓库版本管理，不从远程配置中心动态拉取。

```text
server/game-flow/src/main/resources/flows/
├── v1/
│   ├── cultivation.xml
│   ├── breakthrough.xml
│   ├── combat.xml
│   ├── exploration.xml
│   ├── settlement.xml
│   └── rewind.xml
└── ...
```

是否使用 XML / JSON / YAML 可在工程初始化阶段统一选定。

建议原则：

- Chain 定义尽量短。
- Node 名称使用稳定英文语义。
- 不把数值平衡写死在 Chain。
- 不在 Chain 中写大量脚本。
- 不允许运行时用户输入生成 EL。

V0.1 暂不引入 Nacos / Apollo / ZooKeeper 等规则配置中心。

---

# 17. AI Agent 模块暂不使用

LiteFlow 2.16 系列提供 AI Agent 编排能力，但 V0.1 **不使用 LiteFlow Agent 作为游戏规则节点**。

原因：

- AI 不得决定权威游戏状态。
- AI 结果不可作为确定性规则来源。
- AI 调用失败不能阻塞核心玩法。

未来如果使用，只允许放在：

```text
已经完成规则结算
↓
Narrative Generation
↓
文本表现
```

例如：

- 战斗结果润色
- NPC 对话表达
- 诸世录总结

不得让 Agent 直接修改 RunState。

---

# 18. 测试规则

LiteFlow 流程测试分三层。

## 18.1 Resolver Test

不启动 LiteFlow。

单独测试：

- CultivationResolver
- BreakthroughResolver
- CombatResolver
- EventScheduler
- SettlementResolver

## 18.2 Flow Test

给定固定：

```text
GameState
GameAction
runSeed
ruleVersion
contentVersion
balanceVersion
```

执行完整 Chain。

断言：

- Node 执行顺序
- 分支
- 结果
- Mutation
- Deterministic RNG

## 18.3 Replay Test

同样输入执行多次：

```text
Result A == Result B
```

保证同一 Run 不因为执行次数不同而改变结果。

---

# 19. V0.1 首批组件

只实现原型真正需要的组件。

### 修炼

```text
validateCultivation
advanceTime
calculateCultivation
updateBodyState
increaseEventPressure
scheduleEvent
```

### 事件

```text
resolveEventChoice
applyEventEffects
checkEventChain
```

### 风险 / 死亡

```text
resolveRisk
applyInjury
checkDeath
```

### 结算

```text
analyzeRunEnd
extractKnowledge
calculateInsight
generateLifeRecord
```

### 回溯

```text
restoreAnchor
applyMetaState
startNextRun
```

固道可以先完成领域接口，是否进入 V0.1 UI 根据原型范围决定。

---

# 20. 规则不变量

开发必须遵守：

1. LiteFlow 只做流程编排，不拥有游戏状态真相。
2. Node 不直接读写数据库。
3. Node 不直接使用非确定性随机源。
4. Event Content 不批量转换为 LiteFlow Chain。
5. Condition / Effect 属于自研 Event Engine。
6. 领域算法必须位于 `game-core`，不能堆在 Node 中。
7. 事务边界位于 `game-application`。
8. 一个 Run 固定 `ruleVersion + contentVersion + balanceVersion`。
9. 热更新不得静默改变进行中的 Run。
10. AI Agent 不得参与权威规则结算。

---

# 21. 当前结论

《诸世问道》的 Java Game Engine 采用：

```text
LiteFlow
负责流程编排

+

自研 Game Core
负责修炼 / 战斗 / 回溯 / 固道等领域规则

+

Event Engine
负责数据驱动事件条件与效果

+

Event Scheduler
负责随机事件池调度

+

Deterministic Random
负责可复现随机

+

Run / Anchor / Meta
负责游戏状态模型
```

核心原则：

> **用 LiteFlow 消灭复杂流程硬编码，但不把游戏设计本身交给 LiteFlow。**
