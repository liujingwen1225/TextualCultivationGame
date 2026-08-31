namespace Zhushi.EngineSpike.Core;

public enum LifeStatus
{
    Alive,
    Dead
}

public sealed record EventChoice(string Id, string Label);

public sealed class SpikeState
{
    public const string PoisonKnowledge = "K_BLACKWATER_POISON";

    public LifeStatus LifeStatus { get; set; } = LifeStatus.Alive;
    public HashSet<string> Knowledge { get; set; } = [];
    public bool EventOpen { get; set; }

    public bool HasKnowledge(string id) => Knowledge.Contains(id);
}

public sealed class BlackwaterRules
{
    public const string Drink = "DRINK";
    public const string Leave = "LEAVE";
    public const string RememberRefuse = "REMEMBER_REFUSE";

    public IReadOnlyList<EventChoice> OpenWineEvent(SpikeState state)
    {
        if (state.LifeStatus == LifeStatus.Dead)
            throw new InvalidOperationException("Dead life cannot open a new event");

        state.EventOpen = true;
        List<EventChoice> choices = [new(Drink, "Drink the offered spirit wine")];
        choices.Add(state.HasKnowledge(SpikeState.PoisonKnowledge)
            ? new EventChoice(RememberRefuse, "Prior life: the wine is poisoned — refuse")
            : new EventChoice(Leave, "Leave without drinking"));
        return choices;
    }

    public void Choose(SpikeState state, string choiceId)
    {
        if (!state.EventOpen)
            throw new InvalidOperationException("No event is open");

        switch (choiceId)
        {
            case Drink:
                state.LifeStatus = LifeStatus.Dead;
                state.Knowledge.Add(SpikeState.PoisonKnowledge);
                break;
            case RememberRefuse:
                if (!state.HasKnowledge(SpikeState.PoisonKnowledge))
                    throw new InvalidOperationException("Prior-life choice requires Knowledge");
                break;
            case Leave:
                break;
            default:
                throw new ArgumentOutOfRangeException(nameof(choiceId), choiceId, "Unknown choice");
        }

        state.EventOpen = false;
    }
}
