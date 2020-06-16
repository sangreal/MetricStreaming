package entity.metricbeat;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)

public class MemoryEntity {
    private AvailableEntity available;
    private UsageEntity usage;

    public AvailableEntity getAvailable() {
        return available;
    }

    public void setAvailable(AvailableEntity available) {
        this.available = available;
    }

    public UsageEntity getUsage() {
        return usage;
    }

    public void setUsage(UsageEntity usage) {
        this.usage = usage;
    }
}
