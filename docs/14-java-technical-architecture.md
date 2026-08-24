# 诸世问道：Java 技术架构基线

> 状态：技术基线 V1.1  
> 目标：将现有游戏设计转换为可长期维护、可多端发布、适合单人 + AI 开发的工程架构。

---

# 1. 技术决策

后端正式采用 **Java**，不再考虑 NestJS / Go 作为主服务端实现。

当前基线：

- Java 25 LTS
- Spring Boot 4.1.x
- Spring MVC
- Virtual Threads
- LiteFlow 2.16.1
- PostgreSQL
- jOOQ 3.21.x
- Flyway
- Maven
- OpenAPI
- JUnit 5 + AssertJ + Testcontainers

客户端：

- uni-app x
- Vue 3
- TypeScript
- 微信小程序 / Android / iOS / HarmonyOS / Web
- Windows / macOS / Linux 使用 Tauri 2 封装 Web 构建

原则：

> 客户端只负责交互和表现，服务端拥有全部权威游戏状态与规则。

LiteFlow 的定位：

> **LiteFlow 负责游戏流程编排，自研 Game Core 负责真实领域规则。**

---

# 2. 总体架构

```text
微信小程序 / Android / iOS / HarmonyOS / Web / Desktop
                         │
                      REST API
                         │
                         ▼
                 Spring Boot Game Server
                         │
       ┌─────────────────┼─────────────────┐
       │                 │                 │
    Account          Application         Content
                         │
                         ▼
                   Game Engine
                         │
          ┌──────────────┼──────────────┐
          │              │              │
      LiteFlow        Game Core      Event Engine
      流程编排          领域规则        内容规则
          │              │              │
          └──────────────┼──────────────┘
                         │
               Deterministic Random
                         │
                         ▼
                    Game State
                         │
                         ▼
                    PostgreSQL
```

不采用：

- 微服务
- Redis（第一阶段）
- MQ
- Kubernetes
- WebFlux / Reactor
- 独立规则服务
- 独立事件服务
- 独立 NPC 服务

采用 **模块化单体**。

---

# 3. 仓库结构

推荐根目录：

```text
TextualCultivationGame/
├── apps/
│   ├── game-client/             # uni-app x + Vue 3 + TypeScript
│   └── desktop/                 # Tauri 2
│
├── server/
│   ├── pom.xml                  # Maven parent
│   ├── game-domain/             # 状态 / 值对象 / 领域模型
│   ├── game-core/               # 纯领域规则 / Resolver
│   ├── game-flow/               # LiteFlow 组件与 Chain
│   ├── game-application/        # 用例编排 / 事务边界
│   ├── game-infrastructure/     # PostgreSQL / jOOQ / 外部服务
│   └── game-server/             # Spring Boot / REST / Auth
│
├── content/
│   ├── events/
│   ├── npcs/
│   ├── items/
│   ├── techniques/
│   ├── locations/
│   ├── realms/
│   ├── balance/
│   └── schemas/
│
├── contracts/
│   └── openapi.yaml
│
├── tools/
│   └── content-validator/
│
└── docs/
```

客户端与后端不同语言，通过 OpenAPI 契约解耦。

---

# 4. Game Engine 分层

## 4.1 game-domain

负责：

- RunState
- AnchorState
- MetaState
- CharacterState
- WorldState
- NPC State
- Value Object
- Domain Event

不得依赖：

- Spring
- LiteFlow
- HTTP
- PostgreSQL
- jOOQ

## 4.2 game-core

`server/game-core` 保存真正的游戏规则。

负责：

```text
修炼
突破
战斗
风险
事件条件 / Effect
事件调度
确定性随机
NPC / 关系 / 因果
Knowledge / Insight
回溯
固道
结算
```

不得直接依赖：

- Spring
- HTTP
- PostgreSQL
- jOOQ
- JWT
- 微信 SDK
- 短信 SDK
- AI Provider

领域算法不得堆入 Controller、Application Service 或 LiteFlow Node。

## 4.3 game-flow

`server/game-flow` 使用 LiteFlow。

负责：

- 游戏行动流程编排
- LiteFlow NodeComponent
- Chain
- GameFlowContext
- 流程分支
- 调用 game-core Resolver

允许依赖 LiteFlow，但不得直接访问数据库。

---

# 5. LiteFlow 流程编排

LiteFlow 正式作为 Game Engine 的流程编排层。

适合编排：

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

例如“闭关 30 日”：

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

但以下内容不交给 LiteFlow：

- 几百 / 几千个具体随机事件
- Event Condition / Effect 数据
- 确定性随机算法
- 修炼 / 战斗具体公式
- Game State 真相
- 数据持久化

具体随机事件继续由数据驱动 Event Engine 管理。

详细规则见：

> `docs/16-liteflow-game-flow-orchestration.md`

---

# 6. 状态模型

必须维持四层概念：

```text
Content State
开发者定义的世界规则

Anchor State
锚点建立时冻结的现实状态

Run State
当前推演中的完整状态

Meta State
跨世状态
```

## 6.1 Anchor State

至少包含：

- 角色状态
- 境界 / 修为
- 当前时间
- 当前地点
- 物品 / 装备
- 功法掌握
- NPC 状态
- 关系 / 因果
- 世界事件状态

## 6.2 Run State

从 Anchor 复制得到，并在当前一世持续变化。

包含：

- runSeed
- actionSequence
- ruleVersion
- contentVersion
- balanceVersion
- 当前角色状态
- 当前世界状态
- NPC 状态
- 事件状态
- 背包 / 装备
- 本世 Knowledge / Insight
- 伤势 / 中毒
- 世界线偏移

## 6.3 Meta State

默认持久化：

- Knowledge
- Insight
- 诸世录
- 天衍资源
- 成就 / 图鉴

另外允许通过“固道”消耗珍贵资源，从一世中选择一项实体成果固化，详见 `15-cross-life-solidification-system.md`。

---

# 7. 服务端权威

客户端不得直接计算并保存真实结果。

例如：

```text
玩家点击：闭关 30 日
         ↓
POST /api/game/runs/{runId}/actions
         ↓
Application Service
         ↓
锁定 Run
         ↓
根据 ruleVersion 选择对应 Flow
         ↓
LiteFlow 执行 Chain
         ↓
Game Core Resolver 计算真实结果
         ↓
保存新 RunState
         ↓
写 ActionLog / EventLog
         ↓
Commit
         ↓
返回结果
```

客户端只展示：

- 时间变化
- 修为变化
- 状态变化
- 触发事件
- 可选项

这样小程序或 App 客户端即使被修改，也不能直接篡改修为或物品。

---

# 8. 并发与事务

本游戏不是实时 MMO，不需要复杂 Actor Cluster。

对单个 Run 的一次动作采用数据库事务串行化即可。

推荐：

```sql
SELECT ...
FROM game_run
WHERE id = ?
FOR UPDATE;
```

事务流程：

```text
BEGIN
↓
锁定 game_run
↓
加载 RunState
↓
执行 LiteFlow / Game Core
↓
得到新 RunState + Mutations + Domain Events
↓
持久化
↓
写 ActionLog / EventLog
↓
COMMIT
```

LiteFlow Node **不得自行开启事务或直接写数据库**。

同一个 Run 不允许两个行动同时成功提交。

HTTP 请求使用 Java Virtual Threads，保持同步编程模型，不引入 Reactor。

---

# 9. PostgreSQL 数据策略

采用：

> 核心索引字段关系化 + 复杂运行状态 JSONB。

避免两个极端：

1. 把所有状态拆成上百张表。
2. 整个游戏只存一个巨大 save.json。

第一阶段核心表建议：

```text
user
user_identity
character

game_anchor
game_run
game_meta

run_action_log
run_event_log

rule_version
content_version
balance_version
```

`game_run` 建议包含：

```text
id
user_id
character_id
anchor_id
run_number
run_seed
action_sequence
rule_version
content_version
balance_version
game_time
status
character_state JSONB
world_state JSONB
npc_state JSONB
event_state JSONB
inventory_state JSONB
created_at
updated_at
version
```

后续只有在真实查询 / 统计需求出现时，才把高价值 JSONB 字段拆表。

---

# 10. 数据访问

使用：

```text
PostgreSQL
+
jOOQ
+
Flyway
```

不使用 JPA / Hibernate 作为主要数据访问层。

原因：

- 游戏事务边界明确
- JSONB 较多
- 需要 FOR UPDATE
- 需要批量日志写入
- SQL 可控性重要
- 避免复杂 ORM 生命周期影响 Game State

jOOQ 负责类型安全 SQL。

Flyway 负责数据库迁移。

---

# 11. 确定性随机数

同一 Run 内禁止通过刷新页面改变随机结果。

所有随机结果必须来源于确定性随机上下文。

概念：

```text
runSeed
+
actionSequence
+
randomNamespace
+
contextKey
```

例如：

```text
EVENT_SELECT
COMBAT_HIT
ADVENTURE_ROLL
DROP_ROLL
NPC_ENCOUNTER
```

Game Core 中必须有统一 RandomSource：

```java
public interface RandomSource {
    int nextInt(String namespace, String key, int bound);
    double nextDouble(String namespace, String key);
}
```

禁止任何模块，包括 LiteFlow Node，直接使用：

```java
new Random()
Math.random()
ThreadLocalRandom.current()
```

---

# 12. 三版本锁定机制

每个 Run 创建时必须绑定：

```text
ruleVersion
contentVersion
balanceVersion
```

## ruleVersion

负责：

- LiteFlow Chain 版本
- Game Core 规则兼容版本

## contentVersion

负责：

- 事件
- NPC
- 功法
- 物品
- 地点
- 秘境

## balanceVersion

负责：

- 数值
- 权重
- 修炼速度
- 事件压力
- 战斗参数

进行中的 Run 必须继续使用创建时绑定的版本。

LiteFlow 即使支持热刷新，也不得静默改变已有 Run 的规则。

正确策略：

```text
Rule V3 已有 Run
      ↓
发布 Rule V4
      ↓
旧 Run 继续 V3
新 Run 使用 V4
```

严重 BUG 使用显式 Migration 处理。

---

# 13. 内容系统

游戏内容存放于仓库 `content/`，不把所有事件手工录入数据库。

主要内容：

```text
events
npcs
items
techniques
locations
realms
balance
```

使用 YAML / JSON。

发布前执行：

```text
Schema Validation
↓
Reference Validation
↓
Rule Validation
↓
Content Compile
↓
Content Version
↓
Server Load
```

一个 Run 创建后绑定 `contentVersion`。

---

# 14. Event Engine

事件引擎由两部分组成：

```text
Event Eligibility
事件是否可进入池

Event Scheduler
从当前可用事件中调度
```

条件必须结构化：

```text
RealmCondition
LocationCondition
ItemCondition
TechniqueCondition
KnowledgeCondition
NpcRelationCondition
KarmaCondition
WorldFlagCondition
TimeCondition
```

组合：

```text
AllOf
AnyOf
Not
```

事件结果采用 Effect：

```text
AddItem
RemoveItem
ChangeCultivation
AddKnowledge
AddInsight
ChangeRelation
AddKarma
SetWorldFlag
DamageCharacter
TriggerEvent
KillCharacter
```

具体事件不得批量转换为 LiteFlow Chain。

LiteFlow 的 `resolveEvent` Node 只负责调用 Event Engine。

不得让脚本文本直接修改数据库。

---

# 15. LiteFlow Chain 管理

V0.1 推荐 Chain 随代码仓库版本管理：

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

V0.1 暂不引入：

- Nacos
- Apollo
- ZooKeeper
- 独立规则配置中心

约束：

- Chain 尽量短。
- Node 使用稳定语义命名。
- 数值平衡不写死在 Chain。
- 不使用大量动态脚本承载核心游戏规则。
- 不允许运行时用户输入生成 EL。

---

# 16. 账号与登录

支持：

- 微信小程序登录
- 手机号验证码登录
- App 微信授权登录
- Web / PC 手机号验证码
- 后续 Web / PC 微信扫码

统一：

```text
User
└── UserIdentity
    ├── PHONE
    ├── WECHAT_MINI_PROGRAM
    └── WECHAT_OPEN_PLATFORM
```

所有游戏数据只绑定 `userId`。

不得直接以手机号、openid 作为游戏存档主键。

---

# 17. API

采用 REST。

V0.1 不使用 WebSocket。

核心接口概念：

```text
POST /api/auth/wechat/mini-program
POST /api/auth/phone/send-code
POST /api/auth/phone/login

GET  /api/game/profile
POST /api/game/characters

POST /api/game/anchors/{anchorId}/runs
GET  /api/game/runs/{runId}
POST /api/game/runs/{runId}/actions
POST /api/game/runs/{runId}/settle
POST /api/game/runs/{runId}/rewind

GET  /api/game/meta
GET  /api/game/knowledge
GET  /api/game/life-records
```

契约维护在 `contracts/openapi.yaml`。

客户端 TypeScript API SDK 从 OpenAPI 自动生成。

---

# 18. AI

AI 不参与权威 Game Core 结算。

服务器提供：

```java
public interface NarrativeProvider {
    NarrativeResult generate(NarrativeRequest request);
}
```

实现可包括：

```text
TemplateNarrativeProvider
OpenAiNarrativeProvider
OtherModelProvider
```

LiteFlow 2.16 系列虽然提供 AI Agent 编排能力，但 V0.1 不使用 Agent 作为权威规则节点。

AI 规则：

- 只能读取已结算状态。
- 不决定真实奖励。
- 不决定死亡。
- 不修改 RunState。
- 调用失败必须退化成模板文本。

未来如接入 LiteFlow Agent，只允许位于 Narrative / Content 辅助链路。

---

# 19. 测试策略

## 19.1 game-core

纯单元测试为主。

重点覆盖：

- 修炼
- 突破
- 随机事件调度
- 条件组合
- 战斗
- 伤势
- Knowledge / Insight
- 回溯
- 固道
- 世界线偏移
- 确定性 RNG

不得启动 Spring Context。

## 19.2 game-flow

固定：

```text
GameState
GameAction
runSeed
ruleVersion
contentVersion
balanceVersion
```

执行 Chain，验证：

- Node 顺序
- IF / SWITCH 分支
- Mutation
- Domain Event
- 最终状态

同样输入重复执行，结果必须一致。

## 19.3 Application / Persistence

使用 Testcontainers + PostgreSQL。

覆盖：

- Run FOR UPDATE
- 同 Run 并发动作
- 回滚
- JSONB 保存恢复
- Anchor / Run / Meta 一致性
- rule/content/balance version 锁定

## 19.4 HTTP

V0.1 使用真实 HTTP 集成测试覆盖关键链路。

---

# 20. 多端策略

第一阶段发布顺序：

```text
Web
+
微信小程序
↓
Android / iOS / HarmonyOS
↓
Tauri Desktop
```

不是开发多套游戏客户端，而是同一套 uni-app x 客户端逐端验收。

桌面端只是新的发行容器，不增加新的 Game Core。

---

# 21. V0.1 部署

初期只需要：

```text
1 × Spring Boot Game Server
1 × PostgreSQL
```

可使用 Docker Compose 本地部署。

生产初期也优先单实例服务 + 托管 PostgreSQL。

不提前引入：

- Redis Cluster
- MQ
- Kubernetes
- Service Mesh
- Rule Config Center

等真实负载或运营需求证明需要时再增加。

---

# 22. 当前技术基线总结

截至 2026-08：

```text
Client
uni-app x + Vue 3 + TypeScript

Desktop
Tauri 2

Server
Java 25 LTS + Spring Boot 4.1.x

Flow Orchestration
LiteFlow 2.16.1

Game Rules
自研 Java Game Core

Event
自研 Event Engine + Event Scheduler

Random
Deterministic Random

Persistence
PostgreSQL + jOOQ + Flyway

API
REST + OpenAPI
```

最终原则：

> **Spring Boot 负责服务外壳，LiteFlow 负责编排，自研 Game Core 负责规则，Event Engine 负责内容，PostgreSQL 负责状态。**
