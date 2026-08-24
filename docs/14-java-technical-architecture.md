# 诸世问道：Java 技术架构基线

> 状态：技术基线 V1.0  
> 目标：将现有游戏设计转换为可长期维护、可多端发布、适合单人 + AI 开发的工程架构。

---

# 1. 技术决策

后端正式采用 **Java**，不再考虑 NestJS / Go 作为主服务端实现。

当前基线：

- Java 25 LTS
- Spring Boot 4.1.x
- Spring MVC
- Virtual Threads
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
       │                 │                 │
       │                 ▼                 │
       │             Game Core             │
       │                 │                 │
       └─────────────────┼─────────────────┘
                         │
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
│   ├── game-core/               # 纯 Java 游戏规则
│   ├── game-application/        # 用例编排 / 事务边界
│   ├── game-domain-support/     # 公共领域类型
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

客户端与后端不同语言，但通过 OpenAPI 契约解耦。

---

# 4. Game Core 是最重要的模块

`server/game-core` 必须保持纯 Java。

不得直接依赖：

- Spring
- HTTP
- PostgreSQL
- jOOQ
- JWT
- 微信 SDK
- 短信 SDK
- AI Provider

Game Core 只处理：

```text
输入：当前游戏状态 + 玩家动作 + 内容定义 + 随机上下文

输出：
- 新游戏状态
- 状态变化列表
- 事件结果
- 日志事件
- 下一步可选行动
```

概念接口：

```java
public interface GameEngine {
    GameActionResult execute(GameState state, GameAction action, GameContext context);
}
```

游戏核心模块建议包含：

```text
game-core
├── time
├── cultivation
├── event
├── random
├── combat
├── risk
├── npc
├── relation
├── karma
├── inventory
├── equipment
├── technique
├── location
├── secretrealm
├── timeline
├── anchor
├── rewind
├── knowledge
├── insight
├── solidification
└── settlement
```

---

# 5. 状态模型

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

## 5.1 Anchor State

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

## 5.2 Run State

从 Anchor 复制得到，并在当前一世持续变化。

包含：

- runSeed
- actionSequence
- 当前角色状态
- 当前世界状态
- NPC 状态
- 事件状态
- 背包 / 装备
- 本世 Knowledge / Insight
- 伤势 / 中毒
- 世界线偏移

## 5.3 Meta State

默认持久化：

- Knowledge
- Insight
- 诸世录
- 天衍资源
- 成就 / 图鉴

另外允许通过“固道”消耗珍贵资源，从一世中选择一项实体成果固化，详见独立固道设计文档。

---

# 6. 服务端权威

客户端不得直接计算并保存真实结果。

例如：

```text
玩家点击：闭关 30 日
         ↓
POST /api/runs/{runId}/actions
         ↓
Application Service
         ↓
锁定 Run
         ↓
Game Core 执行
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

# 7. 并发与事务

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
执行 Game Core
↓
保存 RunState
↓
写 ActionLog / EventLog
↓
COMMIT
```

同一个 Run 不允许两个行动同时成功提交。

HTTP 请求可以使用 Java Virtual Threads，保持同步编程模型，不引入 Reactor。

---

# 8. PostgreSQL 数据策略

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

content_version
```

`game_run` 可以包含：

```text
id
user_id
character_id
anchor_id
run_number
run_seed
action_sequence
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

# 9. 数据访问

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

# 10. 确定性随机数

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

Game Core 中必须有统一 RandomSource 接口：

```java
public interface RandomSource {
    int nextInt(String namespace, String key, int bound);
    double nextDouble(String namespace, String key);
}
```

禁止各模块直接 `new Random()` 或使用不可追踪的随机源。

---

# 11. 内容系统

游戏内容存放于仓库 `content/`，不把所有事件手工录入数据库。

主要内容：

```text
events
npcs
items
techniques
locations
realms
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

内容必须版本化。

一个 Run 创建后绑定 `contentVersion`，避免版本升级导致进行中的一世突然改变规则。

---

# 12. Event Engine

事件引擎由两部分组成：

```text
Event Eligibility
事件是否可进入池

Event Scheduler
从当前可用事件中进行调度
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

不得让脚本文本直接修改数据库。

---

# 13. 账号与登录

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

# 14. API

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

# 15. AI

AI 不在 Game Core 中。

服务器提供：

```java
public interface NarrativeProvider {
    NarrativeResult generate(NarrativeRequest request);
}
```

可以实现：

```text
TemplateNarrativeProvider
OpenAiNarrativeProvider
OtherModelProvider
```

规则：

- AI 只能读取已结算状态。
- AI 不决定真实奖励。
- AI 不决定死亡。
- AI 不修改 RunState。
- AI 调用失败时必须可以退化成模板文本。

因此没有模型服务，游戏仍完整可玩。

---

# 16. 测试策略

## 16.1 Game Core

纯单元测试为主。

必须重点覆盖：

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

Game Core 测试不得启动 Spring Context。

## 16.2 Application / Persistence

使用 Testcontainers + PostgreSQL。

覆盖：

- Run FOR UPDATE
- 同 Run 并发动作
- 回滚
- JSONB 保存恢复
- Anchor / Run / Meta 一致性

## 16.3 HTTP

V0.1 使用真实 HTTP 集成测试覆盖关键链路。

---

# 17. 多端策略

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

# 18. V0.1 部署

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

等真实负载证明需要时再增加。

---

# 19. 当前版本基线

截至 2026-08：

- Java 25 为 LTS 基线。
- Spring Boot 4.1.x 为当前主线，项目固定使用 4.1 系列稳定补丁版本。
- jOOQ 使用 3.21 系列稳定版本。

依赖版本统一由 Maven parent 管理，不允许子模块各自漂移。

---

# 20. 技术不变量

后续开发必须保持：

1. Game Core 不依赖 Spring。
2. 服务端拥有权威状态。
3. 客户端不重复实现核心规则。
4. 所有随机结果可复现。
5. Run 行动必须事务化。
6. 内容必须版本化和校验。
7. AI 不得修改真实游戏状态。
8. 第一阶段保持模块化单体。
9. 不因为“未来可能需要”提前增加 Redis、MQ、微服务或 K8s。
10. API 通过 OpenAPI 对客户端暴露稳定契约。

---

# 21. 结论

最终技术主线：

```text
uni-app x + Vue 3 + TypeScript
              │
            REST
              │
Java 25 + Spring Boot 4.1
              │
       Pure Java Game Core
              │
PostgreSQL + jOOQ + Flyway
```

这一方案的核心优势不是单纯性能，而是：

> **复杂修仙规则可以长期清晰建模，同时保持多端客户端简单、服务端权威、内容可持续扩展。**
