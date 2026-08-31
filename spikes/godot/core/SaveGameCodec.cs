using System.Text.Json;

namespace Zhushi.EngineSpike.Core;

public sealed class SaveGameCodec
{
    private static readonly JsonSerializerOptions Options = new() { WriteIndented = true };

    public async Task SaveAsync(SpikeState state, string path)
    {
        await using FileStream stream = File.Create(path);
        await JsonSerializer.SerializeAsync(stream, state, Options);
    }

    public async Task<SpikeState> LoadAsync(string path)
    {
        await using FileStream stream = File.OpenRead(path);
        return await JsonSerializer.DeserializeAsync<SpikeState>(stream, Options)
            ?? throw new InvalidDataException("Save file contained no state");
    }
}
