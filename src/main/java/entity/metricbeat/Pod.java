package entity.metricbeat;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)

public class Pod {
    private String name;
    private String uid;
    private CPUEntity cpu;
    private MemoryEntity memory;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public CPUEntity getCpu() {
        return cpu;
    }

    public void setCpu(CPUEntity cpu) {
        this.cpu = cpu;
    }

    public MemoryEntity getMemory() {
        return memory;
    }

    public void setMemory(MemoryEntity memory) {
        this.memory = memory;
    }
}
