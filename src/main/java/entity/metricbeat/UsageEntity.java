package entity.metricbeat;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)

public class UsageEntity {
    private NodeEntity node;
    private LimitEntity limit;
    private long bytes;

    public NodeEntity getNode() {
        return node;
    }

    public void setNode(NodeEntity node) {
        this.node = node;
    }

    public LimitEntity getLimit() {
        return limit;
    }

    public void setLimit(LimitEntity limit) {
        this.limit = limit;
    }

    public long getBytes() {
        return bytes;
    }

    public void setBytes(long bytes) {
        this.bytes = bytes;
    }
}
