import java.util.ArrayList;

public class Course {
    protected int _course_id;
    protected int _slot;
    protected int _total_enrollment;
    protected int _isExplored;
    protected int _conflict;
    protected ArrayList<Course> Conflicted_Courses;

    public Course(int id, int enrollment){          //Course initializer
        _course_id=id;
        _total_enrollment=enrollment;
        Conflicted_Courses=new ArrayList<>();
        _slot=-1;
    }

    public void set_course_id(int id){
        _course_id=id;
    }
    public int get_course_id(){
        return _course_id;
    }

    public void set_slot(int slot){
        _slot=slot;
    }
    public int get_slot(){
        return _slot;
    }

    public void set_total_enrollment(int enrollment){
        _total_enrollment=enrollment;
    }
    public int get_total_enrollment(){
        return _total_enrollment;
    }

    public void set_isExplored(int isExplored){
        _isExplored=isExplored;
    }
    public int get_isExplored(){
        return _isExplored;
    }

    public void set_conflict(int conflict){
        _conflict=conflict;
    }
    public int get_conflict(){
        return _conflict;
    }

    public void add_Conflicted_Course(Course course){
        Conflicted_Courses.add(course);
    }
    public ArrayList get_Conflicted_Courses(){
        return Conflicted_Courses;
    }
}
