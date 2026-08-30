# 自动化测试架构

## 1. 目标

测试架构服务两个目标：

1. 让 Pure C# Game Core 的正式规则可以快速、确定性验证。
2. 让 AI / Codex 可以通过稳定命令完成从内容到 Godot 集成的 E2E 验证。

测试不能成为第二套游戏逻辑，也不能依赖人工点击 Godot 编辑器。

## 2. 测试金字塔

当前基线：

```text
L0 Build / Static / Content Validation
L1 Pure C# Core Unit Tests
L2 Scenario Runner
L3 Godot Headless Integration
L4 Visual / Input Smoke
```

不同层解决不同风险，不要求所有问题都通过 UI E2E 发现。

## 3. L0 Build / Static / Content Validation

L0 必须很快，适合作为最前置反馈。

至少包括：

- `dotnet build`。
- 编译 warning / analyzer 基线。
- Gameplay Content JSON parse。
- Definition ID 唯一性。
- 内容引用完整性。
- Event / Condition / Effect 参数校验。
- NPC Schedule / Location 引用校验。

目标：

> **静态能发现的问题不要拖到启动游戏以后。**

L0 应作为普通 PR / AI 修改的强阻塞门。

## 4. L1 Pure C# Core Unit Tests

L1 完全不启动 Godot。

测试对象包括：

- 修炼规则。
- World Time。
- NPC Schedule。
- Relationship。
- Event Condition / Effect。
- Inventory / Economy。
- Intel。
- Injury。
- Combat rule units。
- RNG scope / determinism。
- Save model / migration 的纯逻辑部分。

推荐普通 .NET test runner。

当前倾向使用：

```text
xUnit
```

具体测试库在真正创建工程时锁定，但不应因此引入 Godot 运行依赖。

## 5. L1 测试原则

测试通过正式公共 seam 验证规则，不测试 private helper。

优先：

```text
Arrange domain state
→ execute public domain operation
→ assert returned result / state / domain events
```

避免：

- 为测试把 private helper 改 public。
- 对内部每个方法做脆弱 mock。
- 复制一套计算逻辑到测试里验证另一套逻辑。

## 6. L2 Scenario Runner

L2 是本项目最重要的自动化 E2E 层。

它不启动正式画面，通过与 Godot 相同的 `GameSession` / Application Commands 驱动一段真实游戏流程。

架构：

```text
Scenario Definition / Driver
          ↓
      GameCommand
          ↓
      GameSession
          ↓
       Game Core
          ↑
  Same Content Registry
```

核心原则：

> **Scenario Runner 是没有画面的正式游戏客户端。**

## 7. V0.1 主场景

主 Scenario：

```text
blackwater-continuous-life
```

至少能够执行：

```text
创建沈川开局状态
→ 获取黑水机会
→ 与关键 NPC 接触
→ 获取 / 验证情报
→ 在修炼 / 调查 / 交易之间做选择
→ 购买有限准备资源
→ 推进时间
→ 进入黑水山
→ 进入正式 CombatState
→ 完成战斗 / 撤退 / 失败路径之一
→ 进入秘境事件
→ 返回青玄宗
→ 继续事件余波
→ 验证关系、伤势、资源、情报与世界状态
```

它不需要测试动画、像素位置和音效。

## 8. Scenario 不得绕过 Application

完整场景步骤禁止直接：

```text
GameState.Relationship += 5
GameState.Inventory.Add(...)
GameState.EventState = Completed
```

应该通过：

```text
Execute(InteractWithNpc(...))
Execute(Investigate(...))
Execute(Cultivate(...))
Execute(BuyItem(...))
Execute(Travel(...))
Execute(UseCombatAction(...))
```

必要的初始 Fixture 可以构造合法起点，但 Scenario 正式行为必须穿过 Application seam。

否则 L2 无法代表真实客户端行为。

## 9. Scenario Determinism

Scenario 必须可以固定：

- initial state / fixture version。
- content version。
- RNG seed。
- 必要的 RNG scope。

目标：

```text
Same Content
+ Same Initial State
+ Same Commands
+ Same Seed
→ Same Authoritative Result
```

不得因为：

- 运行机器更快。
- 帧率不同。
- 多执行一次无关查询。
- 打开菜单。

而改变重大结果。

## 10. Scenario Trace

每次 Scenario 运行应生成结构化 Trace。

至少记录：

```text
scenario_id
scenario_version
seed
content_version
command_index
command
command_result
important domain events
before_state_summary
After_state_summary
final_state_hash / deterministic summary
```

失败时应能定位到某个 Command，而不是只有“Expected true, Actual false”。

示意：

```text
scenario=blackwater-continuous-life
seed=4217
content=0.1.0

#31 Cultivate(days=2)
#32 BuyItem(item.pill.detox_low)
#33 Travel(location.blackwater.mountain)
#34 StartCombat(combat.blackwater.ambush)

FAIL
expected npc.qingxuan.zhao_changqing.state = injured
actual   npc.qingxuan.zhao_changqing.state = missing
```

## 11. State Snapshot 与诊断

Application / Core 应提供面向诊断的稳定 Snapshot，而不是要求测试直接遍历所有内部对象。

Snapshot 可包含：

- GameTime。
- Player realm / cultivation / injuries。
- Current location。
- Inventory summary。
- Important NPC states。
- Relationships。
- Event states。
- Intel states。
- Active combat summary。
- Important world flags。

Snapshot 是观测面，不是第二个可写 GameState。

## 12. Scenario 粒度

不要把所有规则都放进一个 60 分钟大 Scenario 才发现问题。

建议：

### Domain Scenarios

较短、聚焦：

- 修炼推进触发 NPC 行动。
- 错过商人时间窗口。
- 情报解锁购买 / 对话方案。
- 战斗后重伤影响后续修炼。

### Vertical Slice Scenario

完整：

- `blackwater-continuous-life`。

L1 验规则，短 Scenario 验系统协作，完整 Scenario 验连续人生。

## 13. L3 Godot Headless Integration

L3 启动真实 Godot project，但不依赖人工窗口操作。

目标验证：

- Godot project 可以启动。
- C# assembly / project references 正确加载。
- Content Registry 可以从真实游戏路径加载。
- 关键 Scene 可以实例化。
- ApplicationBridge 可以连接 `GameSession`。
- Scene Content ID binding 合法。
- 基础 Save / Load host integration 可工作。
- Headless 模式下关键集成不报错。

L3 不重复大量 L1 / L2 规则断言。

## 14. L3 测试宿主

当前阶段优先使用项目自己的极小 Godot Headless Test Host。

原则：

```text
Godot --headless
→ 启动专用测试入口 / test scene
→ 执行有限集成检查
→ 输出机器可读结果
→ 明确 exit code
```

当前不把任何第三方 Godot 测试插件写成不可替换的技术硬基线。

未来如果 GdUnit4Net 等工具在当前 Godot patch 上验证稳定，可以作为增强工具引入，但不能破坏 L1 / L2 的纯 .NET 独立性。

## 15. L4 Visual / Input Smoke

L4 验证真正窗口、输入映射和关键表现链。

V0.1 只需要少量高价值 Smoke：

```text
启动游戏
→ 进入存档 / 新游戏
→ WASD 移动
→ 与 NPC / Interaction Point 交互
→ 打开见闻 / 背包
→ 进入战斗
→ Pause / Resume
→ 使用一个术法 / 消耗品
→ 打开 Save
→ Load 并恢复
```

L4 不承担完整玩法规则验证。

## 16. L4 执行频率

建议：

```text
L0  每次变更 / PR 必过
L1  每次变更 / PR 必过
L2  每次相关变更 / PR 必过，主场景进入稳定后默认必过
L3  每次影响 Godot / Content binding / Host 的变更必过
L4  milestone / nightly / release / 关键 UI 变更
```

不要求每个小 Core 修改都必须占用图形环境跑完整 Visual Smoke。

## 17. Screenshot / Golden Test 原则

V0.1 不建立大规模像素级截图 Golden Test。

原因：

- 动画 / 粒子 /字体差异容易产生脆弱结果。
- 视觉频繁调整阶段维护成本高。
- 无法替代人工判断“是否好看 / 是否好玩”。

只在确有稳定视觉价值的关键 UI 状态考虑少量截图检查。

## 18. Save / Load 测试

至少覆盖：

### L1

- Serialize / Deserialize logical round trip。
- schema validation。
- migration logic。

### L2

```text
进行游戏行为
→ Save
→ 创建新的 Session
→ Load
→ 继续 Scenario
→ 最终权威状态一致
```

### L3

验证真实 Godot host 的文件路径 / Adapter 集成。

测试不能因为 Save / Load 存在而引入任何 Anchor / Meta 语义。

## 19. Combat 测试

Combat 最低覆盖：

### L1

- action legality。
- range。
- resource cost。
- action time。
- interruption。
- damage / defense。
- injury。
- retreat。
- deterministic tick / clock。

### L2

真实黑水战斗路径，验证调查和准备如何改变可执行方案。

### L3 / L4

验证 Godot 表现与输入桥接，不重新计算战斗结果。

## 20. Content 测试

ContentValidator 属于 L0。

Game.Content.Tests 可额外验证：

- JSON → Definition round trip / parse。
- typed discriminator。
- stable ID lookup。
- cross-reference validation。
- content version compatibility。

完整 Scenario 必须加载真实 V0.1 content，不使用一套与正式内容完全无关的测试剧情替代黑水纵切。

## 21. AI 友好输出

所有自动化命令应：

- 有稳定 exit code。
- 输出失败测试名称。
- 输出关键文件 / Content ID。
- 避免只写 Godot Editor UI 日志。
- 可以从终端运行。
- CI 与本地使用相同入口。

失败诊断优先给出结构化信息，而不是要求 AI 解析大量无关引擎日志。

## 22. Player Experience Gate 不自动化替代

自动测试验证“规则是否按设计运行”，不能证明“游戏是否好玩”。

V0.1 必须单独保留 Player Experience Gate：

- 修炼选择是否真的让人犹豫。
- 时间压力是否合理。
- 情报是否真正改变判断。
- RTwP 是否有战术感。
- 宗门身份是否有制度感。
- 黑水余波是否让人感到世界继续存在。

CI 全绿不等于 V0.1 产品验收通过。

## 23. V0.1 不做

测试基础设施不需要：

- 自建分布式测试平台。
- 浏览器 E2E 框架。
- MMORPG 压测。
- 大规模 Screenshot farm。
- 复杂 Test DSL。
- 为测试复制 Game Core。
- 每个内部类的 mock-heavy 测试。

保持最小、可运行、可诊断。

## 24. 进入 Spec 前检查

测试架构应能明确回答：

1. 修改一个 Event JSON 后最快在哪一层发现引用错误？
2. 修改战斗公式后是否需要启动 Godot 才能验证？
3. 黑水完整人生能否无画面跑通？
4. Scenario 是否走与正式客户端相同的 `GameSession`？
5. 随机失败是否可以通过 seed 复现？
6. Godot Scene binding 错误由哪一层发现？
7. Save / Load 是否有跨 Session Scenario？
8. UI Smoke 是否只承担它真正擅长的输入 / 表现风险？

如果这些问题没有明确层级归属，则测试架构尚未收口。
