import java.util.LinkedList;
import java.util.stream.IntStream;

public class Variable {
    public int N;
    public int value;
    public int x;
    public int y;

    public LinkedList<Boolean> domain;
    public int forwardDegree;

    //unassigned variables
    public Variable(int n, int r, int c, boolean[] row_domain, boolean[] column_domain, int fd) {
        x = r;
        y = c;
        N = n;
        forwardDegree = fd;

        domain = new LinkedList<>();
        value = -1;
        IntStream.range(0, N).forEach(i -> domain.add(i,row_domain[i] && column_domain[i]));
    }

    //pre-assigned variables
    public Variable(int n, int r, int c, int v) {
        N = n;
        x = r;
        y = c;
        value = v;
        forwardDegree = -1;
        domain = null;
    }

    public int getDomainCount() {
        int c = 0;
        int i=0;
        while (i<N) {
            if (domain.get(i)) c++;
            i++;
        }
        return c;
    }


    //"Least Constraining Value First" is being used for Value Order Heuristic
    // Value that shrinks others’ domain the least is taken first
     public int ValueOrderHeuristic(Variable[][] V) {
        if (domain == null) return -1;

        int[] count = new int[N];
         {
             int i=0;
             while (i<N) {
                 if (domain.get(i)) {
                     int j=0;
                     while (j<N) {
                         if (V[x][j].domain != null && V[x][j].domain.get(i)) count[i]++;
                         if (V[j][y].domain != null && V[j][y].domain.get(i)) count[i]++;
                         j++;
                     }
                 }
                 i++;
             }
         }
        int minimum = -1;
         int i=0;
         while (i<N) {
             if (domain.get(i) && (minimum == -1 || count[i] < count[minimum])) {
                 minimum = i;
             }
             i++;
         }
         if (minimum != -1) {
            domain.set(minimum, false);
        }
        value = minimum;
        return minimum;
     }
}