import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;
import java.util.stream.IntStream;

public abstract class Solver {
    public Variable[][] variables;
    public VAH vah;
    public int N;

    public long _node_count;
    public long _backtrack_count;
    public Variable[][] result;

    public Solver(int num, VAH h, Variable[][] v) {
        variables = v;
        vah = h;
        N = num;

        _node_count = 0;
        _backtrack_count = 0;
        result = null;
    }

    public abstract Set<Variable> UpdateVariables(Variable[][] variables, Variable variable);
    public abstract boolean SolverMethod();
}


class Backtracking extends Solver {

    public Backtracking(int N, VAH vah, Variable[][] v) {
        super(N, vah, v);
    }

    @Override
    public boolean SolverMethod() {
        Variable var = vah.ReturnVar();
        if (var == null) {
            // all variables are assigned
            this.result = variables;
            return true;
        }

        LinkedList<Boolean> arr = new LinkedList<>();
        for(int i=0;i<N;i++)
            arr.add(i,var.domain.get(i));
        int vh = var.ValueOrderHeuristic(variables);
        _node_count++;

        while (vh >= 0) {
            Set<Variable> updatedList = UpdateVariables(variables, var);

            if (!SolverMethod()) {
                for (Variable v : updatedList) {
                    v.domain.set(var.value, true);
                }
                for (int i = 0; i < var.N; i++) {
                    variables[var.x][i].forwardDegree++;
                    variables[i][var.y].forwardDegree++;
                }
            } else {
                return true;
            }

            vh = var.ValueOrderHeuristic(variables);
            _node_count++;
        }
        _backtrack_count++;

//      No value found for var
        var.domain = arr;
        vah.AddInUnassignedList(var);

        return false;
    }

    @Override
    public Set<Variable> UpdateVariables(Variable[][] V, Variable v) {
        Set<Variable> updatedList = new HashSet<>();
        for (int i = 0; i < v.N; i++) {
            V[v.x][i].forwardDegree--;
            if (V[v.x][i].domain == null || !V[v.x][i].domain.get(v.value) || V[v.x][i].value != -1) {
            } else {
                V[v.x][i].domain.set(v.value, false);
                updatedList.add(V[v.x][i]);
            }
            V[i][v.y].forwardDegree--;
            if (V[i][v.y].domain != null && V[i][v.y].domain.get(v.value) && V[i][v.y].value == -1) {
                V[i][v.y].domain.set(v.value, false);
                updatedList.add(V[i][v.y]);
            }
        }
        return updatedList;
    }
}

class ForwardChecking extends Solver {
    public boolean consistency = true;

    public ForwardChecking(int num, VAH h, Variable[][] v) {
        super(num, h, v);
    }

    @Override
    public boolean SolverMethod() {
        Variable var = vah.ReturnVar();
        if (var == null) {
            result = variables;
            return true;
        }

        LinkedList<Boolean> initialDomain = new LinkedList<>();
        IntStream.range(0, N).forEach(i -> initialDomain.set(i, var.domain.get(i)));
        int value = var.ValueOrderHeuristic(variables);
        _node_count++;

        while (value >= 0) {
            consistency = true;

            Set<Variable> updatedList = UpdateVariables(variables, var);

            // Forward Checking
            if (consistency && SolverMethod()) {
                return true;
            } else {
                // withdraw current update
                updatedList.forEach(v -> v.domain.set(var.value, true));

                // update forward degree count
                for (int i = 0; i < var.N; i++) {
                    variables[var.x][i].forwardDegree++;
                    variables[i][var.y].forwardDegree++;
                }
            }

            value = var.ValueOrderHeuristic(variables);
            _node_count++;
        }
        _backtrack_count++;

        var.domain = initialDomain;
        vah.AddInUnassignedList(var);

        return false;
    }

    @Override
    public Set<Variable> UpdateVariables(Variable[][] var, Variable v) {
        Set<Variable> _changed_var = new HashSet<>();
        for (int i = 0; i < v.N; i++) {
            var[v.x][i].forwardDegree--;
            if (var[v.x][i].domain != null && var[v.x][i].domain.get(v.value) && var[v.x][i].value == -1) {
                var[v.x][i].domain.set(v.value, false);
                _changed_var.add(var[v.x][i]);
                if (var[v.x][i].getDomainCount() == 0) {
                    consistency = false;
                    return _changed_var;
                }
            }
            var[i][v.y].forwardDegree--;
            if (var[i][v.y].domain != null && var[i][v.y].domain.get(v.value) && var[i][v.y].value == -1) {
                var[i][v.y].domain.set(v.value,false);
                _changed_var.add(var[i][v.y]);
                if (var[i][v.y].getDomainCount() == 0) {
                    consistency = false;
                    return _changed_var;
                }
            }
        }
        return _changed_var;
    }
}
