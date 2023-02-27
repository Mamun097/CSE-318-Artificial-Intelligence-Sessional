import java.io.*;
import java.util.Arrays;

public class Main {
    public static void main(String[] args){
        File _sample_input=new File("C:\\Users\\dell\\AI_Offline02\\src\\data\\d-10-08.txt");

        int N = 0;
        int[][] _input_matrix = new int[0][];
        boolean[][] _row_dom = new boolean[0][];
        boolean[][] _col_dom = new boolean[0][];
        int[] forwardDegree_row = new int[0];
        int[] forwardDegree_col = new int[0];
        try {
            BufferedReader br = new BufferedReader(new FileReader(_sample_input));
            String line;
            int lc = 0;

            if((line = br.readLine()) != null) {
                N = Integer.parseInt(line);
                _input_matrix = new int[N][N];
                _row_dom = new boolean[N][N];
                _col_dom = new boolean[N][N];
                forwardDegree_row = new int[N];
                forwardDegree_col = new int[N]; // initialized to 0 by default
                int i = 0;
                while (i < N) {
                    int j=0;
                    while (j< N) {
                        _row_dom[i][j]=Boolean.TRUE;
                        _col_dom[i][j]=Boolean.TRUE;
                        j++;
                    }
                    i++;
                }
            }
            lc++;

            while ((line = br.readLine()) != null) {
                String[] input_row = line.split(" ");
                for (int i=0; i<N; i++) {
                    // input in 1 to N, storing 0 to N-1
                    int val = Integer.parseInt(input_row[i]) - 1;
                    _input_matrix[lc-1][i] = val;
                    if (val >= 0) {
                        _row_dom[lc-1][val] = false;
                        _col_dom[i][val] = false;
                    } else {
                        forwardDegree_row[lc-1]++;
                        forwardDegree_col[i]++;
                    }
                }
                lc++;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        Variable[][] variables = new Variable[N][N];

        VAH vah = new VAH1();
//        VAH vah = new VAH2();
//        VAH vah = new VAH3();
//        VAH vah = new VAH4();
//        VAH vah = new VAH5();
        {
            int i = 0;
            while (i < N) {
                int j = 0;
                while (j < N) {
                    Variable v;
                    if (_input_matrix[i][j] == -1) {
                        v = new Variable(N, i, j, _row_dom[i], _col_dom[j],
                                forwardDegree_row[i] + forwardDegree_col[j] + 1);
                        vah.AddInUnassignedList(v);
                    } else {
                        v = new Variable(N, i, j, _input_matrix[i][j]);
                    }
                    variables[i][j] = v;
                    j++;
                }
                i++;
            }
        }

       // Solver solver = new ForwardChecking(N, vah, variables);
        Solver solver = new Backtracking(N, vah, variables);
        System.out.println("Solving " + _sample_input.getName()+"\n");

        long s = System.nanoTime();
        boolean isSolved = solver.SolverMethod();
        long e = System.nanoTime();

        Variable[][] matrix_solved = solver.result;

        if (isSolved) {
            int i=0;
            while (i<N) {
                int j=0;
                while (j<N) {
                    System.out.print(matrix_solved[i][j].value + 1 + "\t");
                    j++;
                }
                System.out.println();
                i++;
            }

            System.out.println("\n___________________________________");
            System.out.println("Input              : "+_sample_input.getName());
            System.out.println("Solved Using       : "+solver.getClass().getName());
            System.out.println("Variable Heuristic : "+vah.getClass().getName());
            System.out.println("Node Count         : "+solver._node_count);
            System.out.println("Backtrack Count    : "+solver._backtrack_count);
            System.out.println("Total time         : "+(e-s)/1000000+" ms");
            System.out.println("-----------------------------------");
        } else {
            System.out.println("Can't solve");
        }
    }
}
