using Godot;
using Zhushi.EngineSpike.Core;

namespace Zhushi.EngineSpike.Godot;

public partial class Main : Node2D
{
    private const float WorldWidth = 640f;
    private const float WorldHeight = 384f;
    private static readonly Rect2 Water = new(new Vector2(320, 128), new Vector2(96, 96));
    private static readonly Vector2 Altar = new(520, 190);
    private static readonly Vector2 Npc = new(220, 210);

    private readonly BlackwaterRules _rules = new();
    private SpikeState _state = new();
    private Vector2 _player = new(96, 96);
    private IReadOnlyList<EventChoice> _choices = Array.Empty<EventChoice>();
    private Label _hud = null!;
    private Label _message = null!;
    private Label _eventText = null!;
    private Camera2D _camera = null!;
    private string _statusMessage = "Walk to the wine altar and press E";
    private string SavePath => ProjectSettings.GlobalizePath("user://engine-spike-save.json");

    public override async void _Ready()
    {
        if (OS.GetCmdlineUserArgs().Contains("--headless-smoke"))
        {
            try
            {
                await ScenarioRunner.RunAsync();
                GD.Print("GODOT_HEADLESS_OK");
                GetTree().Quit(0);
            }
            catch (Exception ex)
            {
                GD.PrintErr(ex);
                GetTree().Quit(1);
            }
            return;
        }

        _camera = new Camera2D { Position = _player, Enabled = true };
        AddChild(_camera);

        CanvasLayer canvas = new();
        AddChild(canvas);
        _hud = new Label { Position = new Vector2(10, 8) };
        _message = new Label { Position = new Vector2(10, 252) };
        _eventText = new Label { Position = new Vector2(80, 130), Visible = false };
        canvas.AddChild(_hud);
        canvas.AddChild(_message);
        canvas.AddChild(_eventText);
        UpdateUi();
        QueueRedraw();
    }

    public override void _Process(double delta)
    {
        if (_state.LifeStatus == LifeStatus.Alive && !_state.EventOpen)
        {
            Vector2 direction = Vector2.Zero;
            if (Input.IsKeyPressed(Key.A) || Input.IsKeyPressed(Key.Left)) direction.X -= 1;
            if (Input.IsKeyPressed(Key.D) || Input.IsKeyPressed(Key.Right)) direction.X += 1;
            if (Input.IsKeyPressed(Key.W) || Input.IsKeyPressed(Key.Up)) direction.Y -= 1;
            if (Input.IsKeyPressed(Key.S) || Input.IsKeyPressed(Key.Down)) direction.Y += 1;
            if (direction != Vector2.Zero)
            {
                direction = direction.Normalized() * 120f * (float)delta;
                Vector2 candidate = _player + direction;
                candidate.X = Mathf.Clamp(candidate.X, 12, WorldWidth - 12);
                candidate.Y = Mathf.Clamp(candidate.Y, 12, WorldHeight - 12);
                Rect2 playerRect = new(candidate - new Vector2(8, 8), new Vector2(16, 16));
                if (!playerRect.Intersects(Water)) _player = candidate;
            }
        }

        _camera.Position = _player;
        UpdateUi();
        QueueRedraw();
    }

    public override async void _UnhandledKeyInput(InputEvent @event)
    {
        if (@event is not InputEventKey key || !key.Pressed || key.Echo) return;

        if (key.Keycode == Key.E && _state.LifeStatus == LifeStatus.Alive && !_state.EventOpen
            && _player.DistanceTo(Altar) < 48f)
        {
            _choices = _rules.OpenWineEvent(_state);
            _statusMessage = "Choose 1 or 2";
        }
        else if (_state.EventOpen && key.Keycode == Key.Key1)
        {
            ApplyChoice(0);
        }
        else if (_state.EventOpen && key.Keycode == Key.Key2)
        {
            ApplyChoice(1);
        }
        else if (key.Keycode == Key.F5)
        {
            await new SaveGameCodec().SaveAsync(_state, SavePath);
            _statusMessage = "Saved to user://engine-spike-save.json";
        }
        else if (key.Keycode == Key.F9 && File.Exists(SavePath))
        {
            _state = await new SaveGameCodec().LoadAsync(SavePath);
            _state.EventOpen = false;
            _choices = Array.Empty<EventChoice>();
            _statusMessage = "Loaded authoritative state";
        }

        UpdateUi();
        QueueRedraw();
    }

    private void ApplyChoice(int index)
    {
        if (index < 0 || index >= _choices.Count) return;
        _rules.Choose(_state, _choices[index].Id);
        _choices = Array.Empty<EventChoice>();
        _statusMessage = _state.LifeStatus == LifeStatus.Dead
            ? "You died and learned the wine is poisoned. Restart the spike to simulate next life."
            : "You refused the wine and survived.";
    }

    private void UpdateUi()
    {
        if (_hud is null) return;
        _hud.Text = $"Godot spike | WASD move | E interact | 1/2 choose | F5 save | F9 load\nLife: {_state.LifeStatus} | Knowledge: {string.Join(',', _state.Knowledge)}";
        _message.Text = _statusMessage;
        _eventText.Visible = _state.EventOpen;
        if (_state.EventOpen)
        {
            _eventText.Text = "Blackwater spirit wine\n" + string.Join("\n", _choices.Select((c, i) => $"{i + 1}. {c.Label}"));
        }
    }

    public override void _Draw()
    {
        DrawRect(new Rect2(0, 0, WorldWidth, WorldHeight), new Color("1f3329"));
        DrawRect(new Rect2(32, 48, 576, 48), new Color("35523d"));
        DrawRect(new Rect2(176, 48, 48, 280), new Color("35523d"));
        DrawRect(Water, new Color("12324a"));
        DrawCircle(Altar, 12, new Color("b89a40"));
        DrawCircle(Npc, 10, new Color("43b89a"));
        DrawRect(new Rect2(_player - new Vector2(8, 8), new Vector2(16, 16)),
            _state.LifeStatus == LifeStatus.Alive ? Colors.White : Colors.DimGray);
    }
}
