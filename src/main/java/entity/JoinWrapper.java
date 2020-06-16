package entity;

import entity.logbeat.ContainerLogs;
import entity.metricbeat.ContainerMetrics;

public class JoinWrapper {
    private ContainerLogs containerLogs;
    private ContainerMetrics containerMetrics;

    public JoinWrapper(ContainerLogs log, ContainerMetrics metric) {
        containerLogs = log;
        containerMetrics = metric;
    }

    public ContainerLogs getContainerLogs() {
        return containerLogs;
    }

    public void setContainerLogs(ContainerLogs containerLogs) {
        this.containerLogs = containerLogs;
    }

    public ContainerMetrics getContainerMetrics() {
        return containerMetrics;
    }

    public void setContainerMetrics(ContainerMetrics containerMetrics) {
        this.containerMetrics = containerMetrics;
    }
}
