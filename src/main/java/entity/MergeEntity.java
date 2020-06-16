package entity;

import java.io.Serializable;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Optional;

public class MergeEntity implements Serializable {
    private double cpu_pct;
    private double mem_pct;
    private String container_name;
    private String message;
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

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public static MergeEntity from(JoinWrapper joinWrapper) {
        MergeEntity mergeEntity = new MergeEntity();
        try {
            mergeEntity.setCpu_pct(joinWrapper
                    .getContainerMetrics()
                    .getKubernetes()
                    .getPod()
                    .getCpu()
                    .getUsage()
                    .getLimit()
                    .getPct());
            mergeEntity.setMem_pct(joinWrapper
                    .getContainerMetrics()
                    .getKubernetes()
                    .getPod()
                    .getMemory()
                    .getUsage()
                    .getLimit()
                    .getPct());
            mergeEntity.setContainer_name(joinWrapper.getContainerMetrics().getKubernetes().getPod().getName());
            mergeEntity.setTimestamp(joinWrapper.getContainerMetrics().getTimeStamp().getTime());
            mergeEntity.setMessage(joinWrapper.getContainerLogs().getMessage());
        } catch (NullPointerException e) {
            System.out.println(e.getMessage());
        }
        return mergeEntity;
    }
}
