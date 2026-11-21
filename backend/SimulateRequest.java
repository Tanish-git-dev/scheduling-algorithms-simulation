import java.util.List;

/**
 * A data transfer object file that mirrors the shape of the
 * json from the frontend. Gson deserializes the raw
 * json string directly into this class
 */
public class SimulateRequest {

    public String algorithm = "FCFS";
    public int    quantum   = 2;
    public List<ProcessDTO> processes;

    public static class ProcessDTO {
        public int pid;       
        public int arrival;
        public int burst;
        public int priority;
    }
}
