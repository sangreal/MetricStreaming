package entity;

import entity.metricbeat.ContainerMetrics;

public class SimpleMetricEntity {
    private double cpu_pct;
    private double mem_pct;
    private String container_name;
    private long timestamp;

    public double getCpu_pct() {
        return cpu_pct;
    }

    public void setCpu_pct(double cpu_pct) {
        this.cpu_pct = cpu_pct;
    }

    public double getMem_pct() {
        return mem_pct;
    }

    public void setMem_pct(double mem_pct) {
        this.mem_pct = mem_pct;
    }

    public String getContainer_name() {
        return container_name;
    }

    public void setContainer_name(String container_name) {
        this.container_name = container_name;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public static SimpleMetricEntity from(ContainerMetrics containerMetrics) {
        SimpleMetricEntity simpleMetricEntity = new SimpleMetricEntity();
        simpleMetricEntity.setContainer_name(containerMetrics.getKubernetes().getPod().getName());
        simpleMetricEntity.setTimestamp(containerMetrics.getTimeStamp().getTime());

        simpleMetricEntity.setCpu_pct(
                containerMetrics
                .getKubernetes()
                .getContainer()
                .getCpu()
                .getUsage()
                .getLimit()
                .getPct());
        simpleMetricEntity.setMem_pct(
                containerMetrics
                .getKubernetes()
                .getContainer()
                .getMemory()
                .getUsage()
                .getLimit()
                .getPct());
        return simpleMetricEntity;
    }
}
