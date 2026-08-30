namespace Zhushi.EngineSpike.Core;

public static class ScenarioRunner
{
    public static async Task RunAsync()
    {
        BlackwaterRules rules = new();
        SpikeState firstLife = new();

        rules.OpenWineEvent(firstLife);
        rules.Choose(firstLife, BlackwaterRules.Drink);
        Require(firstLife.LifeStatus == LifeStatus.Dead, "first life must die after drinking");
        Require(firstLife.HasKnowledge(SpikeState.PoisonKnowledge), "death must grant poison Knowledge");

        string savePath = Path.Combine(Path.GetTempPath(), $"zhushi-godot-spike-{Guid.NewGuid():N}.json");
        try
        {
            SaveGameCodec codec = new();
            await codec.SaveAsync(firstLife, savePath);
            SpikeState loaded = await codec.LoadAsync(savePath);
            Require(loaded.LifeStatus == LifeStatus.Dead, "save/load must preserve death state");
            Require(loaded.HasKnowledge(SpikeState.PoisonKnowledge), "save/load must preserve Knowledge");

            SpikeState secondLife = new();
            foreach (string knowledge in loaded.Knowledge)
                secondLife.Knowledge.Add(knowledge);

            IReadOnlyList<EventChoice> choices = rules.OpenWineEvent(secondLife);
            Require(choices.Any(c => c.Id == BlackwaterRules.RememberRefuse),
                "second life must expose prior-life choice");
            rules.Choose(secondLife, BlackwaterRules.RememberRefuse);
            Require(secondLife.LifeStatus == LifeStatus.Alive, "prior-life refusal must survive");
        }
        finally
        {
            if (File.Exists(savePath)) File.Delete(savePath);
        }
    }

    private static void Require(bool condition, string message)
    {
        if (!condition) throw new InvalidOperationException(message);
    }
}
