import java.util.*;

public class PriorityPreemptive {

    public static GanttResult schedule(List<Process> input) {

        List<Process> procs = new ArrayList<>();
        for (Process p : input) procs.add(new Process(p));

        int n        = procs.size();
        int finished = 0;
        int time     = 0;
        List<int[]> gantt = new ArrayList<>();

        int totalBurst = 0;
        for (Process p : procs) totalBurst += p.burst;

        while (finished < n && time <= totalBurst + 100) {

            Process chosen = null;
            for (Process p : procs) {
                if (p.arrival <= time && p.remaining > 0) {
                    if (chosen == null
                            || p.priority < chosen.priority
                            || (p.priority == chosen.priority && p.pid < chosen.pid)) {
                        chosen = p;
                    }
                }
            }

            if (chosen == null) {
                int nextArrival = Integer.MAX_VALUE;
                for (Process p : procs) {
                    if (p.remaining > 0) nextArrival = Math.min(nextArrival, p.arrival);
                }
                time = nextArrival;
                continue;
            }

            if (!gantt.isEmpty() && gantt.get(gantt.size() - 1)[0] == chosen.pid) {
                gantt.get(gantt.size() - 1)[2] = time + 1;
            } else {
                gantt.add(new int[]{chosen.pid, time, time + 1});
            }

            chosen.remaining--;
            time++;

            if (chosen.remaining == 0) {
                chosen.completion = time;
                chosen.turnaround = chosen.completion - chosen.arrival;
                chosen.waiting    = chosen.turnaround - chosen.burst;
                finished++;
            }
        }

        return new GanttResult(gantt, procs);
    }
}
