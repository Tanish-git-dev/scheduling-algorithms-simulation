import java.util.*;
public class RoundRobin {

    public static GanttResult schedule(List<Process> input, int quantum) {

        List<Process> procs = new ArrayList<>();
        for (Process p : input) procs.add(new Process(p));
        procs.sort(Comparator.comparingInt((Process p) -> p.arrival)
                             .thenComparingInt(p -> p.pid));

        Queue<Process> queue = new LinkedList<>();
        List<int[]> gantt    = new ArrayList<>();

        int time = 0;
        int idx  = 0;

        while (idx < procs.size() && procs.get(idx).arrival <= time) {
            queue.add(procs.get(idx++));
        }

        if (queue.isEmpty() && idx < procs.size()) {
            time = procs.get(idx).arrival;
            queue.add(procs.get(idx++));
        }

        while (!queue.isEmpty()) {

            Process p = queue.poll();
            int slice = Math.min(p.remaining, quantum);
            gantt.add(new int[]{p.pid, time, time + slice});    
            time        += slice;
            p.remaining -= slice;

            while (idx < procs.size() && procs.get(idx).arrival <= time) {
                queue.add(procs.get(idx++));
            }

            if (p.remaining == 0) {
                p.completion = time;
                p.turnaround = p.completion - p.arrival;
                p.waiting    = p.turnaround - p.burst;
            } else {
                queue.add(p);
            }
            if (queue.isEmpty() && idx < procs.size()) {
                time = procs.get(idx).arrival;
                queue.add(procs.get(idx++));
            }
        }

        return new GanttResult(gantt, procs);
    }
}
