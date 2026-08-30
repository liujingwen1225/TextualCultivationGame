# 诸世问道：Java 技术架构

> 状态：当前有效技术基线
>
> 原则：先支撑可玩核心，不为尚未验证的商业规模提前建设分布式系统。

## 1. 技术基线

服务端：

- Java 25 LTS。
- Spring Boot 4.1.x。
- Spring MVC。
- PostgreSQL。
- MyBatis-Flex 1.11.8。
- Flyway。
- LiteFlow 2.16.x。
- JUnit 5。
- AssertJ。
- Testcontainers。
- OpenAPI。
- Maven。

客户端：

- **uni-app x + Vue 3 + TypeScript** 作为正式客户端技术基线。
- V0.1 首个实际运行和 E2E 验收目标为 Web。
- 微信小程序作为首个跨端发布 / 回归目标。
- Android / iOS / HarmonyOS 在核心玩法稳定后逐端验收。
- 桌面端首阶段直接使用 Web；独立桌面客户端如有真实需求再评估 Tauri。

客户端开发方式：

- 日常代码编辑由 VS Code / Cursor / Codex 等通用开发环境完成，不把 HBuilderX 作为主 IDE。
- 项目从 V0.1 起直接使用 uni-app x 工程，不先维护一套独立 Web 前端再迁移。
- 编译、运行、日志读取和跨端自动化优先通过 **HBuilderX CLI / `@dcloudio/hbuilderx-cli`** 调用。
- HBuilderX 作为 uni-app x 必要工具链存在，用于编译器、平台运行环境、插件与发行能力，不作为人工开发流程的中心。
- AI 开发必须能够通过 CLI 启动 Web、读取运行日志并执行自动化测试，形成“修改 → 运行 → E2E → 修复 → 回归”闭环。

原则：

> 客户端只负责交互和表现，服务端拥有全部权威游戏状态与规则。

## 2. 架构形态

采用 **模块化单体**，单一 Java 应用部署。

V0.1 不引入：

- 微服务。
- Redis。
- MQ。
- Kubernetes。
- WebFlux。
- 分布式事务。
- 服务注册发现。

除非后续有真实瓶颈，否则不为了“以后可能需要”提前加入。

## 3. 逻辑包边界

V0.1 推荐先使用一个 Maven 应用，通过 package 保持边界：

```text
com.zhushi
├─ domain
│  ├─ world
│  ├─ life
│  ├─ anchor
│  ├─ cultivation
│  ├─ knowledge
│  ├─ trait
│  ├─ item
│  ├─ npc
│  └─ event
├─ engine
│  ├─ flow
│  ├─ event
│  ├─ random
│  ├─ scheduler
│  └─ evaluation
├─ application
│  ├─ command
│  ├─ query
│  └─ service
├─ infrastructure
│  ├─ persistence
│  ├─ content
│  └─ external
└─ server
   ├─ api
   ├─ auth
   └─ config
```

当包边界稳定、代码量确实增长后，再按这些边界拆 Maven module；不需要在 V0.1 先建立大量空模块。

## 4. 领域权威

客户端不能直接修改任何权威状态。

所有这些状态由服务端决定：

- 世界时间。
- 修为和境界。
- 死亡。
- 回溯次数。
- Anchor。
- Knowledge。
- 词条资格和升级。
- 承世候选。
- 唯一物。
- NPC 状态。
- 事件结果。
- 随机结果。

客户端只发送意图：

```text
修炼
选择事件选项
主动坐化
选择 Anchor
选择承世成果
选择悟世词条
装配词条
```

## 5. 核心聚合建议

### World

代表一个真正的新世界实例。

至少包含：

- worldId。
- worldSeed。
- templateComposition。
- baseRewindRemaining。
- currentLifeId。
- worldStatus。

### Life / Run

代表一世人生。

- lifeId。
- worldId。
- sourceAnchorId。
- runNumber。
- currentGameTime。
- playerState。
- currentEventState。
- runVariationSeed。
- status。

### Anchor

不可变快照。

- anchorId。
- worldId。
- realmTier。
- gameTime。
- snapshot。
- anchorFateSeed。
- createdLifeId。

同一世界同一大境界最多一个 Anchor，通过数据库唯一约束保证。

### WorldMeta

跨 Anchor 保留，但不跨新世界：

- Knowledge。
- 词条库。
- 唯一物全局状态。
- 历世统计。

### AccountMeta

跨新世界保留：

- 账号付费回溯额度。
- 诸世总录。
- 成就 / 图鉴。
- 后续商业权益。

## 6. Anchor 快照原则

Anchor 是不可变历史状态。

不要把它实现成一个不断 UPDATE 的“当前存档”。

建议保存：

- 角色状态。
- 当前境界和修为。
- 背包。
- 功法。
- 装备。
- NPC / 世界必要状态。
- 事件链状态。
- 游戏时间。

不保存为 Anchor 内容：

- Knowledge。
- 词条库。
- 付费回溯额度。
- 账号诸世总录。

这些属于更高层 Meta。

## 7. 数据库存储与数据访问

PostgreSQL 采用关系字段 + JSONB 混合。

适合关系化：

- world。
- life_run。
- anchor。
- knowledge。
- trait。
- trait_loadout。
- unique_entity_state。
- inventory / item_instance。
- account_rewind_credit。

适合 JSONB：

- Anchor snapshot 中变化较快的复杂状态。
- 事件上下文。
- 一世评价事实快照。
- 内容版本绑定信息。

关键业务约束仍应有明确数据库约束，不要把全部状态塞进一个 JSON。

### MyBatis-Flex 使用原则

持久层正式采用 **MyBatis-Flex**，不再使用 jOOQ 作为项目基线。

使用优先级：

```text
BaseMapper
  ↓
QueryWrapper / UpdateChain 等 MyBatis-Flex API
  ↓
注解 SQL
  ↓
XML / 原生复杂 SQL（仅必要场景）
```

目标不是完全禁止 SQL，而是让大多数 CRUD、普通条件查询和更新不需要手写 Mapper SQL。

以下场景允许直接使用明确 SQL：

- `SELECT ... FOR UPDATE` 等关键游戏事务锁。
- PostgreSQL JSONB 的特殊操作。
- 批量日志 / 统计查询。
- 用 QueryWrapper 表达会明显降低可读性的复杂查询。

禁止为了“全部使用 QueryWrapper”把简单 SQL 变成难以维护的 DSL。

`domain` / `engine` 不得依赖 MyBatis-Flex 的 Entity、Mapper、QueryWrapper 等类型；持久层对象与游戏领域对象之间通过 repository / mapper adapter 转换。

数据库迁移继续使用 Flyway。

## 8. 内容存储

游戏事件、词条模板、功法、物品、世界模板优先放在仓库中的 YAML / JSON 内容文件。

启动或构建阶段进行内容编译 / 校验：

- ID 唯一。
- 引用存在。
- Condition 类型合法。
- Effect 类型合法。
- 事件链无明显断链。
- 词条升级链无循环。
- 唯一物 ID 唯一。

V0.1 不需要内容后台管理系统。

## 9. 并发与事务

单个世界同一时刻只允许一个权威状态变更事务。

对 action / event choice / death settlement 等命令：

1. 锁定当前 world / life。
2. 校验客户端提交对应当前版本和状态。
3. 执行规则。
4. 持久化所有状态变化。
5. 提交事务。

可以使用 PostgreSQL 行锁，例如 `SELECT ... FOR UPDATE`。

关键事务通过 Spring `@Transactional` 统一管理；不建立 MyBatis-Flex 自有事务和 Spring 事务并存的两套习惯。

避免让 LiteFlow Node 自己打开多个不一致事务，也禁止 LiteFlow Node 直接散落调用 Mapper 修改游戏状态。

## 10. 幂等与客户端重试

移动端和 H5 都可能重复请求。

关键命令需要：

- commandId / requestId。
- 服务端幂等记录或状态版本校验。
- 当前事件 choice 只能成功提交一次。
- 死亡结算每个阶段只能确认一次。

禁止因为重复点击产生两次承世、两次词条或重复扣回溯。

## 11. 确定性随机

随机统一通过 `RandomSource` 抽象访问，业务代码禁止直接调用 `new Random()`、`Math.random()` 等。

两层随机：

```text
AnchorFateSeed
→ 关键命运

RunVariationSeed
→ 本世枝节变化
```

重大结果使用 key-based/stateless 方式，不能只依赖全局 actionSequence。

例如：

```text
random.keyed(
  anchorFateSeed,
  "event:blackwater-poison",
  decisionId,
  relevantStateHash
)
```

## 12. 一世评价

服务端先产生结构化事实：

```json
{
  "age": 31,
  "realm": "QI_REFINING_4",
  "majorActions": [],
  "achievements": [],
  "relationships": [],
  "deathCause": "BLACKWATER_ARRAY",
  "traitCandidates": []
}
```

V0.1 直接模板化生成评价文字。

未来可把结构化事实交给 AI 润色，但 AI 输出不能反写权威事实。

## 13. 身份与账号

V0.1：

- Guest / DEV identity 即可。

正式产品可以再支持：

- 微信登录。
- 手机号登录。
- 账号绑定 / 合并。

不要让登录系统阻塞核心玩法验证。

## 14. 版本策略

每个 Life / Run 仍记录：

```text
ruleVersion
contentVersion
balanceVersion
```

用途：

- 调试。
- 回放问题。
- 确认内容版本。
- 后续正式兼容能力。

但 **V0.x 不维护多代 Java 规则运行时**。

开发阶段重大规则变化时允许：

- 迁移开发存档；或
- 标记旧 Life 不兼容并结束 / 重建。

contentVersion 和 balanceVersion 可以更早支持并存；只有正式上线存在长期存档后，再设计严肃的 ruleVersion 兼容策略。

## 15. AI 开发与 E2E 基线

项目把“AI 能独立完成开发验证闭环”作为客户端技术选型约束，而不是后补能力。

### 主验证链路

```text
AI / Codex 修改代码
  ↓
CLI 启动 Java Server
  ↓
CLI 启动 uni-app x Web
  ↓
Playwright / uni-app 自动化执行 E2E
  ↓
读取浏览器、客户端和服务端日志
  ↓
修复
  ↓
再次回归
```

Web 是 V0.1 的主 E2E 平台，核心玩法链路必须能够无人工点击完成。

首批 E2E 至少覆盖：

```text
进入游戏
→ 开始一世
→ 修炼
→ 触发事件
→ 选择事件
→ 死亡 / 主动坐化
→ 一世评价
→ Anchor 选择
→ 承世 / 悟世
→ 开始下一世
```

### uni-app x CLI

项目通过 `@dcloudio/hbuilderx-cli` / HBuilderX CLI 接入 uni-app x 编译、运行、日志与自动化测试能力。

跨端测试分级：

```text
P0  Web
    每个开发 Batch / PR 必跑完整核心 E2E

P1  微信小程序
    核心流程稳定后跑跨端 smoke / 回归

P2  Android / iOS / HarmonyOS
    对应平台进入发布范围后再加入发版回归
```

不要求每次代码修改都同时运行所有平台 E2E。

## 16. 测试基线

核心规则必须可无 UI 测试。

后端重点测试：

- 回溯次数消耗。
- Anchor 每大境界唯一和不可变。
- 承世合法性。
- 境界 / 修为不可承世。
- 词条候选规则。
- 词条升级。
- Knowledge 跨 Anchor。
- 唯一物防复制。
- 确定性重大结果。
- 防烧随机。
- Event Pressure 不依赖操作拆分。
- 幂等死亡结算。

集成测试优先使用 Testcontainers PostgreSQL。

客户端测试分为：

- 页面 / 组件级必要测试。
- Web 核心 Playwright E2E。
- uni-app x 官方自动化用于微信、Android、iOS、HarmonyOS 的跨端回归。

最终验收以真实可运行结果为准，不以“代码已生成”作为完成标准。
