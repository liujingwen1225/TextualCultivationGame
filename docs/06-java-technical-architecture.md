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
- jOOQ 3.21.x。
- Flyway。
- LiteFlow 2.16.x。
- JUnit 5。
- AssertJ。
- Testcontainers。
- OpenAPI。
- Maven。

客户端目标：

- V0.1：Web / H5。
- 后续：uni-app x + Vue 3 + TypeScript，逐步覆盖微信小程序、移动端和 Web。
- 桌面端如有需求再评估 Tauri。

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

## 7. 数据库存储

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

避免让 LiteFlow Node 自己打开多个不一致事务。

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

## 15. 测试基线

核心规则必须可无 UI 测试。

重点测试：

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