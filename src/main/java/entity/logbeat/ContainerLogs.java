package entity.logbeat;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import entity.metricbeat.Kubernetes;
import entity.metricbeat.Metadata;

import java.sql.Date;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ContainerLogs {

    @JsonProperty("@timestamp")
    private Date timeStamp;

    @JsonProperty("@metadata")
    private Metadata metadata;

    @JsonProperty("kubernetes")
    private Kubernetes kubernetes;

    private String message;

    public Date getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(Date timeStamp) {
        this.timeStamp = timeStamp;
    }

    public Metadata getMetadata() {
        return metadata;
    }

    public void setMetadata(Metadata metadata) {
        this.metadata = metadata;
    }

    public Kubernetes getKubernetes() {
        return kubernetes;
    }

    public void setKubernetes(Kubernetes kubernetes) {
        this.kubernetes = kubernetes;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }


    @Override
    public String toString() {
        return super.toString();
    }
}
