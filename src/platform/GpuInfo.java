package platform;

public class GpuInfo {
    private final String name;
    private final long vram;

    public GpuInfo(String name, long vram) {
        this.name = name;
        this.vram = vram;
    }

    public String getName() {
        return name;
    }

    public long getVram() {
        return vram;
    }

    public String getVramString() {
        return vram == 0L ? "Shared" : vram + " MB";
    }
}
