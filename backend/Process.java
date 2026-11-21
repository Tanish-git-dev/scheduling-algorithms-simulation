public class Process {

    int pid;       
    int arrival;    
    int burst;     
    int priority;
    int remaining;
    int completion; 
    int turnaround; 
    int waiting;

    public Process(int pid, int arrival, int burst, int priority) {
        this.pid       = pid;
        this.arrival   = arrival;
        this.burst     = burst;
        this.priority  = priority;
        this.remaining = burst;
    }

    public Process(Process other) {
        this.pid        = other.pid;
        this.arrival    = other.arrival;
        this.burst      = other.burst;
        this.priority   = other.priority;
        this.remaining  = other.burst;
        this.completion = 0;
        this.turnaround = 0;
        this.waiting    = 0;
    }

    public String toResultJson() {
        return String.format(
            "{\"pid\":%d,\"arrival\":%d,\"burst\":%d,\"priority\":%d," +
            "\"completion\":%d,\"turnaround\":%d,\"waiting\":%d}",
            pid, arrival, burst, priority, completion, turnaround, waiting
        );
    }
}
