package entity.metricbeat;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ContainerMetrics {
    @JsonProperty("@timestamp")
    private Timestamp timeStamp;

    @JsonProperty("@metadata")
    private Metadata metadata;

    @JsonProperty("kubernetes")
    private Kubernetes kubernetes;

    @JsonProperty("event")
    private Event event;

    public Timestamp getTimeStamp() {
        return timeStamp;
    }

    public Metadata getMetadata() {
        return metadata;
    }

    public Kubernetes getKubernetes() {
        return kubernetes;
    }

    public Event getEvent() {
        return event;
    }

    public void setEvent(Event event) {
        this.event = event;
    }

    @Override
    public String toString() {
        return timeStamp.toString() + " | " + metadata.toString()
      ;
    }
}
