# Steam 像素 RPG 重设计状态

> 分支：`redesign/steam-pixel-rpg`
>
> 用途：记录本分支在合并 main 前的重设计状态，避免旧移动端 / Web 技术基线与新方向混用。

## 已锁定

- 产品平台：Steam / Windows 优先。
- 产品形态：东方修仙像素人生模拟 Roguelite RPG。
- 玩家在真实像素地图中移动、探索、接触 NPC、修炼和战斗。
- 地图负责沉浸与空间选择，Event Engine 负责内容深度。
- Anchor / Knowledge / 承世 / 悟世 / 多世回溯继续作为核心系统。
- V0.1 继续使用黑水三世垂直切片。
- 10 次回溯是世界内有限资源，不再绑定账号付费续命。
- 单机本地权威运行时。
- AI / Codex 自动开发与测试是硬约束。

## 已废弃

- 纯文字 / H5 页面式产品形态。
- uni-app x 作为当前客户端基线。
- 微信小程序作为首发目标。
- Spring Boot + REST 作为游戏运行时。
- PostgreSQL + MyBatis-Flex 作为首版游戏存档。
- LiteFlow 作为首版游戏流程编排。
- 账号级付费回溯额度。
- 移动端竖屏 UI 作为设计基准。
- 《八方旅人》规模的 HD-2D 制作目标。
- 《Project Zomboid》规模的全沙盒世界模拟。

## 待锁定

- Java + libGDX 或 Godot + C#。
- 战斗制式：回合 / 时间轴 / 极简即时。
- 角色与 Tileset 基础像素规格。
- V0.1 最终输入方案与手柄范围。
- Steamworks 接入阶段。

## 合并 main 前必须完成

1. 清理旧技术文档冲突。
2. 完成 Engine Spike 决策。
3. 将最终引擎写入技术基线。
4. 重新生成 V0.1 Issues / Tickets。
5. 审查现有 Issues，关闭或改写移动端 / Web 相关票。
6. 对全部 docs 做一次一致性审查。

## 当前开发禁令

在最终引擎选型完成前：

- 不开发正式客户端。
- 不继续实现旧 Spring Boot 服务端。
- 不按旧 H5 Issues 开工。
- 不扩充黑水之外的大量内容。

允许的下一步只有：

> **完成设计一致性清理 → Engine Spike → 锁定引擎 → 重做 Tickets。**