#include <iostream>
#include <vector>
#include<bits/stdc++.h>

using namespace std;

class State{
    int k;
    int g_of_n;
    int h_of_n;
    State* parent;

    vector<vector<int>> Board;
    bool isVisited;

public:
    State(int num){
        k=num;
        parent=NULL;
        isVisited= false;
        Board.resize(k);
    }

    void Set_k(int num){
        k=num;
    }
    int Get_k(){
        return k;
    }

    void Set_h_of_n(int num){
        h_of_n=num;
    }
    int Get_h_of_n(){
        return h_of_n;
    }

    void Set_g_of_n(int num){
        g_of_n=num;
    }
    int Get_g_of_n(){
        return g_of_n;
    }

    void SetParent(State* ptr){
        parent=ptr;
    }
    State* GetParent(){
        return parent;
    }

    int CountInversion(){
        vector<int> linear_vector;
        for(int i=0;i<Board.size();i++){
            for(int j=0;j<Board.size();j++){
                linear_vector.push_back(Board[i][j]);
            }
        }

        int inv=0;
        for(int i=0;i<linear_vector.size()-1;i++)
            for(int j=i+1;j<linear_vector.size();j++)
                if(linear_vector[i]>linear_vector[j])inv++;
        return inv;
    }

    void PopulateBoard(int row, int value){
        Board[row].push_back(value);
    }

    vector<vector<int>> GetBoard(){
        return Board;
    }

    bool isSolvable(){
        int inv=CountInversion();

        if (k%2==1) {
            if (inv % 2 == 0)return true;
            else return false;
        }
        else{
            int pos;                 //position of * from bottom
            for (int i = k - 1; i >= 0; i--)
                for (int j = k - 1; j >= 0; j--)
                    if (Board[i][j] == 0)
                        pos=k-i;
            if(pos%2==0 && inv%2==1) return true;
            else if(pos%2==1 && inv%2==0) return true;
            else return false;
        }
    }

    int HammingDistance(){
        vector<int> linear_vector;
        for(int i=0;i<Board.size();i++){
            for(int j=0;j<Board.size();j++){
                linear_vector.push_back(Board[i][j]);
            }
        }
        int ham=0;
        for(int i=0;i<linear_vector.size();i++){
            if(linear_vector[i]!=0 && linear_vector[i]!=i+1)ham++;
        }
        return ham;
    }

    int ManhattanDistance(){
        int man=0;
        for(int i=0;i<k;i++){
            for(int j=0;j<k;j++){
                if(Board[i][j]!=0){
                    int row=(Board[i][j]-1)/k;
                    int col=(Board[i][j]-1)%k;
                    man+= abs(row-i)+abs(col-j);
                }
            }
        }
        return man;
    }

    void Swap(int rs, int rd, int cs, int cd){
        swap(Board[rs][cs], Board[rd][cd]);
    }

    void PrintBoard(){
        cout<<"......"<<endl;
        for(int i=0; i<Board.size(); i++){
            for(int j=0; j<Board[i].size(); j++){
                if(Board[i][j]!=0){
                    cout<<Board[i][j]<<" ";
                }
                else{
                    cout<<"*"<<" ";
                }
            }
            cout<<endl;
        }
        cout<<"......"<<endl<<endl;
    }

    bool Visited(){
        return isVisited;
    }

    void MakeVisited(){
        isVisited = true;
    }
};

struct Compare {
    bool operator()(State *s1, State *s2) {
        int c1 = s1->Get_g_of_n() + s1->Get_h_of_n();
        int c2 = s2->Get_g_of_n() + s2->Get_h_of_n();
        return c1 > c2;
    }
};

class Graph {
    State* state;
    int k;
public:

    int dx[4] = {-1, 1, 0, 0};    //to calculate new move
    int dy[4] = {0, 0, -1, 1};

    Graph(int num){
        k = num;
        state = new State(k);
    }

    void Insert(int row, int val){
        state->PopulateBoard(row, val);
    }

    bool Solvable(){
        return state->isSolvable();
    }

    void PrintBoard(){
        state->PrintBoard();
    }

    void printPath(State* state){
        if(state->GetParent()!=NULL){
            printPath(state->GetParent());
        }
        state->PrintBoard();
    }

    bool isExceed(int n, int m){
        if(n < 0 || n >= k || m<0 || m>=k ){
            return true;
        }
        return false;
    }

    void A_Star_Algo(int h){
        int expanded=1;

        if(h==1){
            cout<<"Running A* Search using hamming distance heuristic"<<endl;
        }
        else if(h==2){
            cout<<"Running A* Search using manhattan distance heuristic"<<endl;
        }

        state->Set_g_of_n(0); //g(n) of source=0
        priority_queue<State*, vector<State*>, Compare>pq;
        unordered_set<State*> Explored;
        pq.push(state);
        int move_count=0;

        int c=1;
        vector<vector<int>> goalState;
        goalState.resize(k);
        for(int i=0; i<k; i++){
            for(int j=0; j<k; j++){
                goalState[i].push_back(c);
                c++;
            }
        }
        goalState[k-1][k-1] = 0;


        while(!pq.empty()){
            State *recently_Dequeued = pq.top();
            pq.pop();
            Explored.insert(recently_Dequeued);

            if(recently_Dequeued->GetBoard() == goalState){
                cout<<"Done"<<endl;
                cout<<"Cost: "<<recently_Dequeued->Get_g_of_n()<<endl;
                cout<<"Explored: "<<Explored.size()<<endl;
                cout<<"Expanded: "<<expanded<<endl;

                cout<<"Printing Path:"<<endl;
                printPath(recently_Dequeued);
                break;
            }

            move_count++;

            //locating the blank
            int blank_row = -1;
            int blank_col = -1;
            for(int i=0; i<k; i++){
                for(int j=0; j<k; j++){
                    if(recently_Dequeued->GetBoard()[i][j]==0){
                        blank_row = i;
                        blank_col = j;
                        break;
                    }
                }
            }

            for(int i=0; i<4; i++){
                int new_x = blank_row+dx[i], new_y = blank_col+dy[i];
                if(isExceed(new_x, new_y)) continue;

                State *new_State = new State(k);
                for(int i=0; i<k; i++){
                    for(int j=0; j<k; j++){
                        new_State->PopulateBoard(i, recently_Dequeued->GetBoard()[i][j]);
                    }
                }
                new_State->Swap(blank_row, new_x, blank_col, new_y );

                if(recently_Dequeued->GetParent()!=NULL){
                    if(new_State->GetBoard()==recently_Dequeued->GetParent()->GetBoard()){
                        continue;
                    }
                }

                new_State->SetParent(recently_Dequeued);
                new_State->Set_g_of_n(new_State->GetParent()->Get_g_of_n()+1);   //g(n) increased by 1
                if(h==1){
                    new_State->Set_h_of_n(new_State->HammingDistance());
                }
                else if(h==2){
                    new_State->Set_h_of_n(new_State->ManhattanDistance());
                }

                pq.push(new_State);
                expanded++;
            }
        }
    }
};



int main() {
    int k;
    cin>>k;

    Graph *g = new Graph(k);
    for(int i=0; i<k; i++){
        for(int j=0; j<k; j++){
            string s;
            cin>>s;
            if(s == "*"){
                g->Insert(i, 0);
            }else{
                g->Insert(i, stoi(s));
            }
        }
    }
    g->PrintBoard();
    if(g->Solvable()){
        cout<<"Puzzle is Solvable"<<endl;

        g->A_Star_Algo(1);
        g->A_Star_Algo(2);
    }else{
        cout<<"Puzzle is NOT Solvable"<<endl;
    }

    return 0;
}
