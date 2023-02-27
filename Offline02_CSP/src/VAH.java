import java.util.*;

public abstract class VAH {
    public ArrayList<Variable> Unassigned;

    public VAH() {
        Unassigned =new ArrayList<>();
    }

    public void AddInUnassignedList(Variable v) {
        Unassigned.add(v);
    }
    public abstract Variable ReturnVar();
}

class VAH1 extends VAH {
    public VAH1() {
        super();
    }
    @Override
    public Variable ReturnVar() {
        //find variable which has smallest domain
        Variable temp = Unassigned.stream().min(Comparator.comparingInt(Variable::getDomainCount)).orElse(null);
        Unassigned.remove(temp);    //Removing temp from unassigned list
        return temp;
    }
}

class VAH2 extends VAH {
    public VAH2() {
        super();
    }
    @Override
    public Variable ReturnVar() {
        // return the variable with the maximum degree to unassigned variables
        Variable temp = Unassigned.stream().max(Comparator.comparingInt(v -> v.forwardDegree)).orElse(null);
        Unassigned.remove(temp);    //Removing temp from unassigned list
        return temp;
    }
}

class VAH3 extends VAH{
    public VAH3() {
        super();
    }

    //Return variable chosen by VAH1, ties broken by vah2
    @Override
    public Variable ReturnVar() {
        Variable temp = null;
        for (Iterator<Variable> iterator = Unassigned.iterator(); iterator.hasNext(); ) {
            Variable v = iterator.next();
            if (temp == null || temp.getDomainCount() > v.getDomainCount()) {
                temp = v;       //VAH1
            } else if (v.getDomainCount() == temp.getDomainCount()) {
                if (temp.forwardDegree < v.forwardDegree) {
                    temp = v;       //ties broken by VAH2
                }
            }
        }
        Unassigned.remove(temp);
        return temp;
    }
}

class VAH4 extends VAH{
    public VAH4() {
        super();
    }

    // The variable chosen is the one that minimizes the VAH1 / VAH2
    @Override
    public Variable ReturnVar() {
        Variable temp = Unassigned.stream().min(Comparator.comparingInt(v -> v.getDomainCount() / v.forwardDegree)).orElse(null);
        Unassigned.remove(temp);
        return temp;
    }
}

class VAH5 extends VAH {
    public VAH5() {
        super();
    }

    // A random unassigned variable is chosen
    @Override
    public Variable ReturnVar() {
        if (Unassigned.isEmpty()) {
            return null;
        }
        else {
            Variable temp = null;
            Random random=new Random();
            int ind=random.nextInt(Unassigned.size());
            int it = 0;
            for (Variable v : Unassigned) {
                if (ind==it) {
                    temp = v;
                    break;
                }
                it++;
            }
            Unassigned.remove(temp);
            return temp;
        }
    }
}