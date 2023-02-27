import java.util.ArrayList;

public class Student {
    ArrayList<Course> Courses_taken;

    public Student(){
        Courses_taken = new ArrayList<>();
    }

    public void add_Course_to_Student_Profile(Course course){
        Courses_taken.add(course);
    }
    public ArrayList get_Courses_taken(){
        return Courses_taken;
    }

    public double getPenalty(){
        double penalty=0;
        for (int i=0;i<Courses_taken.size()-1;i++){
            for (int j=i+1;j<Courses_taken.size();j++){
                int gap=Courses_taken.get(i).get_slot()-Courses_taken.get(j).get_slot();
                gap=Math.abs(gap);

                if(gap<=5)
                    penalty+=2*(5-gap);
                    //penalty+=Math.pow(2,5-gap);
            }
        }
        return penalty;
    }
}
