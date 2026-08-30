# 内容数据架构

## 1. 目标

本文件定义正式 Gameplay Content 与运行时 State 的边界。

目标：

- 内容可数据驱动。
- Game Core 不依赖 Godot Resource。
- Scenario Runner 与正式游戏加载同一份 Gameplay Content。
- SaveGame 不复制整套静态内容。
- AI 可以稳定生成、校验和修改内容。
- 内容系统保持简单，不演变为自制脚本语言。

## 2. 基本原则

> **静态 Definition 与动态 State 分离。**

例如：

```text
NpcDefinition
!=
NpcState
```

`Definition` 描述“这个东西是什么”。

`State` 描述“当前这个游戏实例里它变成了什么状态”。

## 3. 当前文件格式基线

Gameplay Content 当前基线：

```text
JSON
+
System.Text.Json
```

原因：

- .NET 8 原生支持。
- 无额外 Runtime。
- 结构明确。
- 易于 CI 校验。
- AI 生成和修改稳定。
- diff 可读。
- 不要求启动 Godot。

当前阶段不把 YAML、Godot Resource、自制 DSL 或嵌入 C# 脚本作为权威 Gameplay Content 格式。

## 4. 内容与表现资产分离

### JSON Gameplay Content

负责：

- NPC Definition。
- Event / Event Chain。
- Condition / Effect 参数。
- Dialogue structure / text references。
- Intel。
- Item。
- Technique / Ability。
- Cultivation Method。
- Shop / resource pool。
- Schedule。
- Location semantic definition。
- Faction / Identity definitions。

### Godot Scene / Resource

负责：

- TileMap / TileSet。
- Sprite。
- Animation。
- Audio。
- Shader。
- Particle。
- UI Theme。
- Scene composition。
- 视觉 / 音频表现配置。

Godot Scene 可以通过 Content ID 绑定 Gameplay Content，但不能成为 Core 唯一规则来源。

## 5. 稳定 ID

所有需要跨内容文件、SaveGame、Scenario、Scene 引用的对象使用稳定 ID。

推荐形式：

```text
npc.qingxuan.zhao_changqing
item.pill.detox_low
event.blackwater.invitation
intel.blackwater.wine_origin
location.blackwater.secret_realm
ability.shenchuan.fire_talisman
shop.qingshi.alchemist
```

原则：

- ID 是机器身份，不是显示名称。
- 改中文名称不应导致存档失效。
- ID 一旦进入 SaveGame 或正式内容，应避免随意修改。
- 如必须改 ID，应有显式 migration / alias 策略。

## 6. Definition Registry

内容加载流程：

```text
JSON Content Files
→ Parse
→ Schema / Structural Validation
→ Cross-reference Validation
→ Immutable Definitions
→ Definition Registry
```

运行时 Core 通过稳定 ID 查询 Definition。

Registry 对外只暴露需要的查询，不让任意代码修改已加载 Definition。

## 7. Definition 类型

V0.1 只设计实际需要的 Definition 类型。

可能包括：

```text
NpcDefinition
ItemDefinition
AbilityDefinition
CultivationMethodDefinition
EventDefinition
IntelDefinition
LocationDefinition
ShopDefinition
IdentityDefinition
FactionDefinition
```

不要为了正式版全部设想提前定义几十种空类型。

## 8. State 类型

动态 State 进入 GameState / SaveGame。

例如：

```text
NpcState
├─ definition_id
├─ current_location_id
├─ current_status
├─ injury_state
├─ goal_state
└─ runtime flags

InventoryEntry
├─ item_definition_id
└─ quantity / instance state

EventState
├─ event_id
├─ current_phase
└─ runtime outcome flags
```

SaveGame 不需要复制：

- NPC 显示名称。
- Item 描述。
- Ability 全部固定参数。
- Event 全部 Conditions / Effects。

这些来自当前 Content Registry。

## 9. 内容目录组织

V0.1 推荐按“基础规则内容 + 纵切内容”组织。

示例：

```text
game/content/
│
├─ core/
│  ├─ cultivation/
│  ├─ abilities/
│  ├─ items/
│  ├─ injuries/
│  └─ identities/
│
└─ blackwater/
   ├─ npcs/
   ├─ events/
   ├─ dialogues/
   ├─ intel/
   ├─ shops/
   ├─ locations/
   └─ schedules/
```

正式版以后可以自然扩展为 Region / Sect / Story Pack，但 V0.1 不设计 Mod Package 系统。

## 10. NPC Definition 与 State

示意：

```text
NpcDefinition
├─ id
├─ display_name
├─ identity
├─ realm
├─ base_capabilities
├─ default_schedule
└─ presentation_ref
```

动态：

```text
NpcState
├─ current_location
├─ current_condition
├─ relationship state
├─ current_goal
├─ event state
└─ persistent injuries
```

Schedule 是 Definition 与 State / Event / World Time 共同解释的规则，不要求把 NPC 每分钟路径写入内容。

## 11. Event 内容

事件保持：

```text
EventDefinition
├─ id
├─ trigger
├─ conditions[]
├─ choices[]
└─ effects[]
```

只允许白名单类型化 Condition / Effect。

示例：

```text
Condition:
HasIntel
HasItem
AtLocation
GameTimeBetween
RelationshipAtLeast
NpcStateEquals
HasIdentity

Effect:
GrantIntel
AddItem
RemoveItem
ChangeRelationship
SetNpcState
ApplyInjury
SetWorldFlag
StartCombat
```

## 12. Event Engine 不是编程语言

内容文件禁止：

- 任意 C# expression。
- 任意 method 名反射调用。
- 任意对象路径写值。
- eval。
- 任意脚本文件作为必需规则。
- Condition / Effect 之间无限嵌套。

如果新内容需要新规则：

```text
先判断是否是真正可复用领域规则
→ 在 Core 增加 typed operation
→ Content 增加对应 typed definition
→ 增加 Validation / Tests
```

而不是扩大一个万能表达式系统。

## 13. Choice 与时间成本

有意义行为的时间成本优先显式表达。

例如：

```text
ChoiceDefinition
├─ id
├─ conditions[]
├─ duration
└─ effects[]
```

这样修炼、调查、疗伤、部分事件行为都能自然参与 World Time 调度。

时间推进本身由 Application / Core 完成，不让内容任意递归触发 `TIME_ADVANCED`。

## 14. Dialogue

V0.1 对话可以数据化，但不要建设完整视觉小说脚本 VM。

需要支持：

- 文本节点。
- Speaker。
- 条件分支。
- Choice。
- 进入 / 结束时受控 Effect。
- 根据 Intel / Relationship / Identity 切换内容。

普通文本表现不拥有权威 State 修改能力。

复杂领域结算仍走 typed Effect / Command。

## 15. Intel 内容

Intel Definition 可以包含：

- ID。
- 标题。
- 类别。
- 来源描述。
- 可信阶段。
- 关联 NPC / Location / Event。
- 玩家可见文本。

动态 IntelState 记录玩家当前是否：

```text
Unknown
Rumor
Inferred
Verified
```

不要求自动知识图谱推理。

## 16. Item / Ability

静态规则参数数据化，但复杂行为仍由 Core 的能力规则解释。

例如：

```text
AbilityDefinition
├─ id
├─ action_time
├─ range
├─ resource_cost
├─ tags
└─ typed effects
```

不要在 JSON 中写算法。

法宝 / 功法如果有独特规则，应优先由少量明确领域机制组合，而不是每个物品拥有任意脚本。

## 17. Location 与 Godot Scene 绑定

内容 Location Definition 描述语义身份：

```text
location.qingxuan.outer_court
location.qingshi.market
location.blackwater.mountain
location.blackwater.secret_realm
```

Godot Scene 负责视觉与碰撞。

绑定可以通过显式 metadata / exported ID / mapping 完成。

Scene 中的 Interaction Point 只声明语义 ID，例如：

```text
content_id = "event.blackwater.wine.inspect"
```

Scene 脚本不直接写 Event outcome。

## 18. Content Validation

L0 / CI 必须可以不启动正式游戏完成全部内容校验。

至少检查：

### 结构

- JSON 可解析。
- 必填字段存在。
- enum / typed discriminator 合法。
- 参数类型正确。

### ID

- 全局 / 类型范围内唯一。
- ID 格式合法。
- 不出现重复定义。

### 引用

- NPC 引用存在。
- Item / Ability 引用存在。
- Location 引用存在。
- Event 引用存在。
- Dialogue / Intel 引用存在。
- Shop inventory 引用存在。

### Event

- Event Chain 不断链。
- Choice target 合法。
- Condition / Effect 参数完整。
- 不存在明显不可达必需节点。

### NPC Schedule

- Location 存在。
- TimeBlock 合法。
- 必需关键 NPC 没有明显空档错误。

## 19. Validation 失败原则

开发 / CI 环境发现权威 Gameplay Content 错误时应快速失败，并提供明确路径：

```text
file
json path
content id
error code
human-readable message
```

示例：

```text
blackwater/events/invitation.json
$.choices[1].effects[0]
event.blackwater.invitation
UNKNOWN_INTEL_ID
intel.blackwater.wine_source does not exist
```

目标是让开发者和 AI 能直接定位，不依赖运行 20 分钟后才发现坏引用。

## 20. Content Version

SaveGame 记录 `content_version`。

V0.1 不需要复杂 live-service migration，但必须能区分：

- schema version。
- content version。
- save version。

如果旧 Save 指向已删除 Definition，应在 Load Validation 阶段明确失败或迁移，而不是静默生成空对象。

## 21. AI 内容工作流

内容架构应支持：

```text
AI 修改 JSON
→ ContentValidator
→ Scenario Tests
→ Godot Headless
→ 人工体验验证
```

AI 不应为了新增一个剧情分支去修改 Scene 脚本中的领域规则。

## 22. V0.1 最小内容范围

只实现黑水纵切实际需要的数据类型与校验。

不需要：

- Mod manifest。
- Steam Workshop。
- 热更新协议。
- 在线内容 CDN。
- 任意脚本扩展。
- 通用数据库。
- 内容编辑器平台。
- 复杂 localization pipeline。

如果 JSON 手工维护在 V0.1 已可接受，就先使用文本工具与 Validation；专用编辑器等真实痛点出现后再设计。

## 23. 设计检查

进入 Spec 前应能回答：

1. 哪些是 Definition，哪些是 State？
2. SaveGame 为什么不会复制全部静态内容？
3. Event 如何修改状态而不执行任意脚本？
4. Scene 如何通过稳定 ID 找到规则内容？
5. 一个内容 ID 删除 / 改名后怎样被发现？
6. CI 是否能在不启动 Godot 的情况下发现坏引用？
7. Scenario Runner 是否加载与正式游戏相同的 Content Registry？

以上若依赖“实现时先写死再说”，则内容架构尚未收口。
