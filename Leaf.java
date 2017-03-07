
public class Leaf {
int gen;
int count;
Leaf[] branches=new Leaf[26];
public Leaf(int i){
	gen=i;
	count=1;
}
public Leaf(int i,int j){
	gen=i;
	count=j;
}
}
