public class Program1{
public static void main(String args[]) {
int arg0 = Integer.valueOf(args[0]);
int arg1 = Integer.valueOf(args[1]);
int arg2 = Integer.valueOf(args[2]);
int count = 0;int i = 1;while (i <= arg2) {
if (i % arg0 == 0 || i % arg1 == 0) {
count = count + 1;}
i = i + 1;}
System.out.println(count);

}
}