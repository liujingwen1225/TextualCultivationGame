# 事件与内容系统规范

> 目标：让《诸世问道》的主要玩法内容可以通过结构化配置持续扩展，而不是把剧情写死在页面或业务代码中。

---

# 1. 内容系统目标

对独立开发者而言，长期维护效率比一次性代码速度更重要。

因此内容层必须满足：

1. 新增普通事件尽量不改引擎代码。
2. 新增事件链只需配置节点、条件、结果和文本。
3. 前世知识可以直接控制新选项是否出现。
4. 所有影响存档的效果都必须是结构化 Effect。
5. 文本可以由 AI 辅助生成，但规则和结果必须可校验。
6. 所有内容必须可测试。

---

# 2. 核心内容对象

```text
Content
├─ Location
├─ NPC
├─ Technique
├─ Item
├─ Enemy
├─ Knowledge
├─ Event
└─ EventChain
```

其中 Event 是最核心对象。

---

# 3. Event 基础模型

示例：

```yaml
id: event_blackwater_cave_poison
name: 古修洞府·暗手
category: story
location: blackwater_cave
priority: 100
once_per_run: true

conditions:
  all:
    - player.realm >= QI_6
    - flag.blackwater_entered == true
    - npc.zhao_changqing.alive == true

text:
  title: 洞府深处
  body: 赵长青从储物袋中取出一只玉瓶，笑着递给你。

choices:
  - id: accept_pill
    text: 接过丹药服下
    effects:
      - type: add_status
        target: player
        status: poisoned_blackwater
      - type: trigger_event
        event: event_blackwater_poison_death

  - id: refuse_pill
    text: 婉拒他的好意
    conditions:
      all:
        - player.attribute.perception >= 60
    effects:
      - type: relation_change
        npc: zhao_changqing
        trust: -10

  - id: memory_prepare_antidote
    text: "★【前世记忆】提前服下解毒丹"
    conditions:
      all:
        - meta.knowledge contains KNOWLEDGE_ZHAO_POISON
        - inventory contains antidote_pill
    effects:
      - type: consume_item
        item: antidote_pill
        count: 1
      - type: set_flag
        key: blackwater_poison_prepared
        value: true
      - type: trigger_event
        event: event_blackwater_zhao_reveal
```

以上只是内容格式示意，技术实现时可使用 TypeScript/JSON/数据库，但语义保持一致。

---

# 4. Event 必备字段

建议至少包含：

| 字段 | 说明 |
|---|---|
| id | 全局唯一标识 |
| name | 开发/后台可读名称 |
| category | 事件类型 |
| location | 可选地点限制 |
| priority | 多事件同时满足时的优先级 |
| conditions | 触发条件 |
| text | 展示内容 |
| choices | 玩家选项 |
| once_per_run | 当前一世是否只能触发一次 |
| cooldown | 可选冷却 |
| tags | 检索/测试/分类 |

不允许以正文文本本身承载业务规则。

---

# 5. Choice 模型

每个选项建议包含：

```text
Choice
├─ id
├─ text
├─ conditions
├─ cost
├─ checks
├─ effects
└─ next
```

## 5.1 普通选项

任何满足条件的玩家可见。

## 5.2 条件选项

例如：

```text
境界 ≥ 炼气七层
神识 ≥ 50
拥有破阵符
与陆清瑶信任 ≥ 40
```

## 5.3 前世记忆选项

必须依赖 Knowledge：

```text
meta.knowledge contains KNOWLEDGE_XXX
```

展示时统一以：

```text
★【前世记忆】...
```

标识。

## 5.4 不可见与不可用

建议区分：

- `hidden`：条件不满足时完全不显示。
- `disabled`：显示但不可选，并告诉玩家缺少条件。

前世知识一般采用 hidden，避免提前剧透。

---

# 6. Condition 条件系统

条件必须组合化，不为每个事件写独立 if/else。

基础逻辑：

```text
all
any
not
```

V0.1 需要支持的条件：

## 玩家

- 境界
- 修为
- 年龄
- 属性
- 状态
- 当前地点

## 资源

- 拥有物品
- 灵石数量
- 功法是否掌握

## NPC

- 是否认识
- 是否存活
- 关系值
- 当前状态

## 事件

- 事件是否完成
- 某选项是否选择过
- 事件链阶段

## 世界

- 当前日期范围
- 标记 Flag

## 跨世

- 是否拥有某 Knowledge
- 某 Knowledge 是否仍可信
- 已完成推演次数

---

# 7. Effect 效果系统

任何改变状态的行为统一走 Effect。

V0.1 至少需要：

## 时间

```text
advance_time
```

## 角色

```text
add_cultivation
change_health
add_status
remove_status
set_realm
kill_player
```

## 资源

```text
add_item
consume_item
add_currency
```

## NPC

```text
relation_change
npc_state_change
kill_npc
```

## 事件

```text
set_flag
complete_event
advance_chain
trigger_event
```

## 知识

```text
grant_knowledge
invalidate_knowledge
change_knowledge_confidence
```

## 功法

```text
grant_technique
add_technique_insight
```

原则：

> 事件文本永远不能直接修改存档，必须通过 Effect。

---

# 8. Check 判定系统

用于不是必然成功的选择。

示例：

```yaml
checks:
  - type: attribute
    attribute: divine_sense
    difficulty: 55
    on_success:
      - trigger_event: event_find_hidden_rune
    on_failure:
      - trigger_event: event_trigger_trap
```

V0.1 只做少量通用判定：

- 属性判定
- 境界对抗
- 战斗判定
- 概率判定

避免每个内容类型发展成独立迷你系统。

---

# 9. Knowledge 知识模型

示例：

```yaml
id: KNOWLEDGE_ZHAO_POISON
name: 赵长青的暗手
type: person_secret
subject: zhao_changqing
description: 赵长青会在黑水秘境古修洞府中下毒。
source_event: event_blackwater_poison_death
persistence: permanent
confidence: 100
```

建议字段：

- id
- name
- type
- subject
- description
- source
- persistence
- confidence
- status
- tags

## 9.1 类型

V0.1：

```text
person_secret
future_event
location_secret
solution
cultivation_insight
```

## 9.2 状态

```text
active
stale
invalid
```

## 9.3 可信度

第一版可先展示整数 0–100，但不要一开始做复杂动态公式。

关键事件可以直接：

```text
active → stale
active → invalid
```

---

# 10. EventChain 事件链

事件链用于维护较长的剧情状态。

例如：

```yaml
id: chain_blackwater_secret_cave
name: 黑水秘境·古修洞府
stages:
  - invitation
  - enter_cave
  - poison
  - killing_formation
  - inheritance
```

每条链保存当前 Run 中的：

- stage
- branch
- flags
- important choices

事件链状态属于 Run State，除非明确生成 Knowledge，否则回溯后不会直接继承。

---

# 11. 多世事件设计规则

本项目真正重要的不是普通事件数量，而是跨世事件的设计质量。

一条优秀的多世链建议具备：

## 第一层：未知导致失败

玩家第一次没有足够信息。

## 第二层：知识解锁反制

玩家知道危险，因此出现新决策。

## 第三层：解决旧问题后发现更深问题

避免第二世直接通关。

## 第四层：提前准备

某些知识应该影响事件发生前的行动，而不只是现场选项。

## 第五层：改变未来

玩家成功后，原事件链的一部分必须永久发生偏移。

推荐模板：

```text
第一次：不知道 A → 死亡 → 得知 A
第二次：解决 A → 遇到 B → 死亡/失败 → 得知 B
第三次：提前为 A+B 做准备 → 解锁真正奖励
```

---

# 12. “前世知识”不能做成攻略列表

不合格设计：

```text
你知道第三个选项是正确答案。
```

合格设计：

```text
你知道赵长青会下毒。
```

然后允许多种利用方式：

- 准备解毒丹
- 不与赵同行
- 通知宗门
- 利用假中毒反制

也就是说：

> Knowledge 应提供新信息，而不是直接标注唯一正确答案。

V0.1 内容量有限时可以只实现 1–2 种反制，但数据模型应允许后续扩展。

---

# 13. NPC 内容模型

示例：

```yaml
id: zhao_changqing
name: 赵长青
role: 青玄宗外门弟子
realm: QI_8
traits:
  - ambitious
  - cautious
public_profile: 外门中颇有名气的师兄。
secrets:
  - secret_blackwater_motive
relationships:
  default:
    closeness: 0
    trust: 0
    caution: 10
```

V0.1 不需要完整 Agent 字段。

事件可以更新 NPC 状态：

```text
normal
suspicious
hostile
escaped
injured
dead
```

---

# 14. Location 内容模型

示例：

```yaml
id: qingxuan_library
name: 藏经阁
parent: qingxuan_sect
travel_days: 0
requirements:
  sect_rank >= outer_disciple
actions:
  - browse_techniques
  - talk_to_keeper
event_tags:
  - sect
  - technique
```

地点主要承担：

- 行动集合
- 事件上下文
- NPC 可见性
- 环境修炼修正

不负责复杂地理模拟。

---

# 15. Item 内容模型

示例：

```yaml
id: antidote_pill
name: 解毒丹
type: consumable
stackable: true
value: 20
effects:
  - remove_status: common_poison
```

关键剧情物品可以被 Condition 检查。

禁止在事件中通过名字字符串判断物品，应始终使用稳定 ID。

---

# 16. Technique 内容模型

示例：

```yaml
id: qingmu_art
name: 青木诀
rank: yellow_high
element: wood
realm_min: QI_1
base_efficiency: 1.0
traits:
  - stable
  - recovery
```

功法理解属于玩家状态，不属于 Technique 模板本身。

---

# 17. 文本层与规则层分离

事件需要允许同一规则结果对应多种文本变体。

例如：

```text
Result: PLAYER_POISONED
```

基础文本可以由开发者写。

AI 后续可根据：

- 当前人物
- 地点
- 玩家状态
- 已发生事件

生成更自然的叙事变体。

但 AI 返回文本后不能改变原有 Result。

---

# 18. AI 内容生产工作流

建议开发期使用：

```text
人工定义事件目的
↓
人工定义条件/结果/知识
↓
AI 生成文案和备选选项草稿
↓
人工筛选
↓
规则验证
↓
自动测试
↓
进入内容库
```

不要反过来：

```text
AI 随机生成完整事件
↓
直接上线
```

因为很容易造成：

- 奖励失衡
- 世界观冲突
- 无法回溯
- 事件链断裂
- 条件永远不可达

---

# 19. 内容测试要求

每条核心事件至少测试：

1. 正确条件下可以触发。
2. 错误条件下不能触发。
3. 每个 Choice 的条件正确。
4. Effects 完成后状态符合预期。
5. 事件链可以到达下一个阶段。
6. Grant Knowledge 后下一世新选项可见。
7. 没有 Knowledge 时特殊选项不可见。
8. 相关未来失效时不应继续提供错误选项。

核心跨世事件链必须有端到端测试。

---

# 20. 内容编辑器后续方向

当手写配置开始影响效率后，再实现内部 Content Studio：

```text
事件基础信息
条件编辑器
正文编辑器
选项编辑器
Effect 编辑器
Knowledge 绑定
事件链可视化
测试运行
AI 润色
```

Content Studio 是独立开发长期效率工具，但不属于 V0.1 首要玩家功能。

---

# 21. 首批内容制作顺序

不要先大量生产随机事件。

建议：

1. 黑水秘境完整三世事件链。
2. 支撑该事件链所需 NPC、地点、物品、功法。
3. 青玄宗教学事件。
4. 坊市准备事件。
5. 少量日常事件填充节奏。
6. 第二条跨世人物事件链。
7. 再增加随机事件。

优先保证核心链深度，而不是追求事件数量。

---

# 22. 内容设计验收原则

一个事件值得保留，至少应满足其中一个：

- 形成有效决策。
- 消耗重要资源。
- 推进人物关系。
- 暴露世界信息。
- 提供未来知识。
- 改变事件链。
- 带来明确风险。
- 支撑角色成长。

如果一个事件只有“看一段文字 → 获得 10 灵石”，且没有任何上下文价值，应减少这类内容。

最终目标不是做一部长篇固定小说，而是建立一个能够被前世知识反复重写的 **事件网络**。
