import java.io.*;
import java.util.*;

class DegreeComparator implements Comparator<Course> {
    @Override
    public int compare(Course course1, Course course2) {
        return course2.get_conflict()-course1.get_conflict();
    }
}
class EnrollmentComparator implements Comparator<Course> {
    @Override
    public int compare(Course course1, Course course2) {
        return course2.get_total_enrollment()-course1.get_total_enrollment();
    }
}
class SlotComparator implements Comparator<Course> {
    @Override
    public int compare(Course course1, Course course2) {
        return course1.get_slot()-course2.get_slot();
    }
}

public class Scheduler {
    public static int LargestDegree(ArrayList<Course> courses, ArrayList<Student> students) {
        for (Student student : students) {
            ArrayList<Course> Courses_taken = student.get_Courses_taken();
            for (int j = 0; j < Courses_taken.size(); j++) {
                if (Courses_taken.size() == 1)
                    Courses_taken.get(j).set_conflict(Courses_taken.get(j).get_conflict());
                else Courses_taken.get(j).set_conflict(Courses_taken.get(j).get_conflict() + 1);
            }
        }
        Comparator comparator=new DegreeComparator();
        Collections.sort(courses,comparator);
        System.out.println("Used Heuristic  : Largest Degree");
        return do_Schedule(courses);
    }

    public static int SaturationDegree(ArrayList<Course> courses, ArrayList<Student> students) {
        for (Student student : students) {
            ArrayList<Course> Courses_taken = student.get_Courses_taken();
            for (int j = 0; j < Courses_taken.size(); j++) {
                Courses_taken.get(j).set_conflict(Courses_taken.get(j).get_conflict() + Courses_taken.size() - 1);
            }
        }
        Comparator comparator=new DegreeComparator();
        Collections.sort(courses, comparator);
        System.out.println("Used Heuristic  : Saturation Degree");
        return do_Schedule(courses);
    }

    public static int LargestEnrollment(ArrayList<Course> Courses) {
        Comparator comparator=new EnrollmentComparator();
        Collections.sort(Courses, comparator);
        System.out.println("Used Heuristic  : Largest Enrollment");
        return do_Schedule(Courses);
    }

    public static int RandomOrdering(ArrayList<Course> Courses) {
        Collections.shuffle(Courses);
        System.out.println("Used Heuristic  : Random Ordering");
        return do_Schedule(Courses);
    }

    private static void DFS(ArrayList<Course> Courses1, Course course, int t) {
        Courses1.add(course);
        course.set_isExplored(1);  //gray
        ArrayList<Course> conflicted_courses = course.get_Conflicted_Courses();

        for (Course c : conflicted_courses) {
            if (c.get_isExplored() == 0 && c.get_slot() == t) {
                DFS(Courses1, c, course.get_slot());
            }
        }
        course.set_isExplored(2); //white
    }

    private static ArrayList<Course> KempeChainInterchange(ArrayList<Course> courses, ArrayList<Student> students, Course current, int neighbor_slot) {
        ArrayList<Course> courses1=new ArrayList<>();

        DFS(courses1,current, neighbor_slot);

        // interchanging time slots among kempe-chain nodes
        double current_penalty = getAveragePenalty(students);
        int current_slot = current.get_slot();

        for (Course c : courses) {
            if (c.get_isExplored() == 2) {
                if (c.get_slot() == current_slot) {
                    c.set_slot(neighbor_slot);
                } else {
                    c.set_slot(current_slot);
                }
            }
        }

        //if new penalty is worse, then revert kci
        if(current_penalty <= getAveragePenalty(students)) {
            for (Course c : courses) {
                if (c.get_isExplored() == 2) {
                    if (c.get_slot() == current_slot) {
                        c.set_slot(neighbor_slot);
                    } else {
                        c.set_slot(current_slot);
                    }
                }
            }
        }

        courses.stream().filter(c -> c.get_isExplored() == 2).forEach(c -> c.set_isExplored(0));
        //courses that is explored, is made unexplored

        return courses;
    }

    private static int do_Schedule(ArrayList<Course> courses) {
        int total_slot = 0;

        for (Course course : courses) {
            ArrayList<Course> conflicted_courses = course.get_Conflicted_Courses();
            int[] already_booked_slot = new int[conflicted_courses.size()];
            for (int j = 0; j < already_booked_slot.length; j++) {
                already_booked_slot[j] = conflicted_courses.get(j).get_slot();
            }
            Arrays.sort(already_booked_slot);

            int suitable_time_slot = 0;
            for (int k : already_booked_slot) {
                if (k != -1) {
                    if (suitable_time_slot == k) {
                        suitable_time_slot++;
                    }
                    if (suitable_time_slot < k) {
                        course.set_slot(suitable_time_slot);
                    }
                }
            }
            if (course.get_slot() == -1) {
                if (suitable_time_slot == total_slot) {
                    course.set_slot(total_slot++);
                } else {
                    course.set_slot(suitable_time_slot);
                }
            }
        }
        return total_slot;
    }

    private static void PairSwapOperator(ArrayList<Student> students, Course course1, Course course2) {
        // if slot of any adjacent course of course2 is equal slot of course1
        //and vice-versa, then Pair Swap is not possible

        if (course1.get_slot() != course2.get_slot()) {
            ArrayList<Course> conflicted_courses = course1.get_Conflicted_Courses();
            for (Course c : conflicted_courses) {
                if (c.get_slot() != course2.get_slot()) {
                    continue;
                }
                return;
            }

            conflicted_courses = course2.get_Conflicted_Courses();
            for (Course course : conflicted_courses) {
                if (course.get_slot() != course1.get_slot()) {
                    continue;
                }
                return;
            }

            // pair swap
            double previous_penalty = getAveragePenalty(students);
            int temp=course1.get_slot();
            course1.set_slot(course2.get_slot());
            course2.set_slot(temp);

            if (previous_penalty <= getAveragePenalty(students)) {
                int temp1=course1.get_slot();
                course1.set_slot(course2.get_slot());
                course2.set_slot(temp1);
            }
        }
    }

    public static double getAveragePenalty(ArrayList<Student> Students){
        double penalty=0;
        for (Student student : Students) {
            penalty += student.getPenalty();
        }
        return penalty/Students.size();
    }

    public static void main(String[] args) throws IOException {
        String course_file="C:\\Users\\dell\\AI_Offline03\\Inputs\\car-f-92.crs";
        String student_file="C:\\Users\\dell\\AI_Offline03\\Inputs\\car-f-92.stu";

        File file1 = new File(course_file);
        BufferedReader br1 = new BufferedReader(new FileReader(file1));

        ArrayList<Course> Courses=new ArrayList<>();
        ArrayList<Student> Students=new ArrayList<>();

        String line;
        //reading crs file
        while ((line = br1.readLine()) != null){
            String[] course_info=line.split(" ");
            int course_id=Integer.parseInt(course_info[0]);
            int enrolled=Integer.parseInt(course_info[1]);
            Course course=new Course(course_id, enrolled);
            Courses.add(course);
        }

        boolean [][] Conflict_Matrix=new boolean[Courses.size()][Courses.size()];
        for (boolean[] conflict_matrix : Conflict_Matrix)
            Arrays.fill(conflict_matrix, false);


        File file2 = new File(student_file);
        BufferedReader br2 = new BufferedReader(new FileReader(file2));
        while ((line = br2.readLine()) != null){
            String[] taken_courses=line.split(" ");
            Student student=new Student();
            for (String course : taken_courses) {
                Course temp = Courses.get(Integer.parseInt(course) - 1);
                student.add_Course_to_Student_Profile(temp);
            }
            Students.add(student);

            int[] course_id=new int[taken_courses.length];
            for(int i=0;i<taken_courses.length;i++)
                course_id[i]=Integer.parseInt(taken_courses[i]);

            for(int i=0;i<course_id.length;i++){
                for(int j=i+1;j<course_id.length;j++){
                    if(!Conflict_Matrix[course_id[i]-1][course_id[j]-1]){
                        Conflict_Matrix[course_id[i]-1][course_id[j]-1]=true;
                        Conflict_Matrix[course_id[j]-1][course_id[i]-1]=true;
                    }
                }
            }
        }

        for(int i=0;i<Conflict_Matrix.length;i++) {
            for (int j = 0; j < Conflict_Matrix.length; j++) {
                if (Conflict_Matrix[i][j])
                    Courses.get(i).add_Conflicted_Course(Courses.get(j));
            }
        }

        String[] input=course_file.split("\\\\");
        System.out.println("Sample Input    : "+input[input.length-1].split("\\.")[0]);

        int total_slot=LargestDegree(Courses,Students);
     //   int total_slot=SaturationDegree(Courses,Students);
      //  int total_slot=LargestEnrollment(Courses);
      //  int total_slot=RandomOrdering(Courses);

        System.out.println("Total slot      : "+total_slot);
        System.out.println("Average Penalty : "+ String.format("%.3f", getAveragePenalty(Students)));

        Random random=new Random();
        for(int i=0; i<1000; i++) {
            int random_course = random.nextInt(Courses.size());
            ArrayList<Course> conflicted_courses = Courses.get(random_course).get_Conflicted_Courses();
            if(conflicted_courses.size() != 0) {
                KempeChainInterchange(Courses, Students, Courses.get(random_course), conflicted_courses.get(random.nextInt(conflicted_courses.size())).get_slot());
            }
        }
        double penalty=getAveragePenalty(Students);
        System.out.println("\nPenalty after Kempe Chain Interchange : "+ String.format("%.3f", penalty));

        for(int i=0;i<1000;i++) {
            PairSwapOperator(Students, Courses.get(random.nextInt(Courses.size())), Courses.get(random.nextInt(Courses.size())));
        }
        System.out.println("Penalty after Pair Swap Ordering      : "+String.format("%.3f", getAveragePenalty(Students)));

        String text = "";
        Courses.sort(new SlotComparator());
        for (Course course : Courses) {
            text += course.get_slot() + "\t" + course._course_id + "\n";
        }
        try {
            BufferedWriter f_writer = new BufferedWriter(new FileWriter(
                    "C:\\Users\\dell\\AI_Offline03\\output.txt"));
            f_writer.write(text);
            f_writer.close();
        }
        catch (IOException e) {
            System.out.print(e.getMessage());
        }
    }
}
