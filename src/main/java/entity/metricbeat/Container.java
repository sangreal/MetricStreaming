package entity.metricbeat;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.Date;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Container {
    String name;
    CPUEntity cpu;
    Date start_time;

    MemoryEntity memory;


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public CPUEntity getCpu() {
        return cpu;
    }

    public void setCpu(CPUEntity cpu) {
        this.cpu = cpu;
    }

    public Date getStart_time() {
        return start_time;
    }

    public void setStart_time(Date start_time) {
        this.start_time = start_time;
    }

    public MemoryEntity getMemory() {
        return memory;
    }

    public void setMemory(MemoryEntity memory) {
        this.memory = memory;
    }
}
