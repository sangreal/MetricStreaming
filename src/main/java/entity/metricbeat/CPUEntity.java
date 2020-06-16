package entity.metricbeat;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)

public class CPUEntity {
    private UsageEntity usage;

    public UsageEntity getUsage() {
        return usage;
    }

    public void setUsage(UsageEntity usage) {
        this.usage = usage;
    }
}
