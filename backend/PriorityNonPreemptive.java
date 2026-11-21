import java.util.*;

public class PriorityNonPreemptive {

    public static GanttResult schedule(List<Process> input) {

        List<Process> procs = new ArrayList<>();
        for (Process p : input) procs.add(new Process(p));

        int n          = procs.size();
        boolean[] done = new boolean[n];
        int finished   = 0;
        int time       = 0;
        List<int[]> gantt = new ArrayList<>();

        while (finished < n) {

            List<Process> ready = new ArrayList<>();
            for (int i = 0; i < n; i++) {
                if (!done[i] && procs.get(i).arrival <= time) {
                    ready.add(procs.get(i));
                }
            }

            if (ready.isEmpty()) {
                int nextArrival = Integer.MAX_VALUE;
                for (int i = 0; i < n; i++) {
                    if (!done[i]) nextArrival = Math.min(nextArrival, procs.get(i).arrival);
                }
                time = nextArrival;
                continue;
            }

            ready.sort(Comparator.comparingInt((Process p) -> p.priority)
                                 .thenComparingInt(p -> p.arrival)
                                 .thenComparingInt(p -> p.pid));

            Process chosen = ready.get(0);

            int start = time;
            int end   = time + chosen.burst;
            gantt.add(new int[]{chosen.pid, start, end});

            chosen.completion = end;
            chosen.turnaround = chosen.completion - chosen.arrival;
            chosen.waiting    = chosen.turnaround - chosen.burst;
            time = end;

            for (int i = 0; i < n; i++) {
                if (procs.get(i).pid == chosen.pid) { done[i] = true; break; }
            }
            finished++;
        }

        return new GanttResult(gantt, procs);
    }
}
