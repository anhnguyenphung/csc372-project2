public class Program2{
public static void main(String args[]) {
int arg0 = Integer.valueOf(args[0]);
int i = 2;boolean flag = true;while (i < arg0) {
if (arg0 % i == 0) {
flag = false;
i = arg0;}
i = i + 1;}
if (flag) {
System.out.println("prime");
} else {
System.out.println("not prime");
}

}
}