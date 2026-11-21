import java.util.*;

public class GanttResult {

    List<int[]> gantt;
    List<Process> procs;

    public GanttResult(List<int[]> gantt, List<Process> procs) {
        this.gantt = gantt;
        this.procs = procs;
    }

    public String toJson() {
        StringBuilder sb = new StringBuilder();
        sb.append("{");
        sb.append("\"gantt\":[");

        for (int i = 0; i < gantt.size(); i++) {
            int[] seg = gantt.get(i);
            sb.append("[").append(seg[0]).append(",")
                          .append(seg[1]).append(",")
                          .append(seg[2]).append("]");
            if (i < gantt.size() - 1) sb.append(",");
        }
        sb.append("],");

        sb.append("\"processes\":[");
        for (int i = 0; i < procs.size(); i++) {
            sb.append(procs.get(i).toResultJson());
            if (i < procs.size() - 1) sb.append(",");
        }
        sb.append("]");

        sb.append("}");
        return sb.toString();
    }
}
