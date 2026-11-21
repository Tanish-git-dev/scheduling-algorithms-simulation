import java.util.*;

public class FCFS {

    public static GanttResult schedule(List<Process> input) {

        List<Process> procs = new ArrayList<>();
        for (Process p : input) procs.add(new Process(p));

        procs.sort(Comparator.comparingInt((Process p) -> p.arrival)
                             .thenComparingInt(p -> p.pid));

        int time = 0;
        List<int[]> gantt = new ArrayList<>();  

        for (Process p : procs) {
            if (time < p.arrival) {
                time = p.arrival;
            }
            int start = time;
            int end   = time + p.burst;
            gantt.add(new int[]{p.pid, start, end});

            p.completion = end;
            p.turnaround = p.completion - p.arrival;  
            p.waiting    = p.turnaround - p.burst;  
            
            time = end;
        }

        return new GanttResult(gantt, procs);
    }
}
