using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace MethodEx2
{
    class Program
    {
        static void Main(string[] args)
        {
            int a, b, Res = 0;
            Console.Write("Enter a value : ");
            a = Convert.ToInt32(Console.ReadLine());
            Console.Write("Enter b value : ");
            b = Convert.ToInt32(Console.ReadLine());
            Res = Operations(a, b, "+");
            Console.WriteLine("The sum is : " + Res);
            Res = Operations(a, b, "-");
            Console.WriteLine("The Sub is : " + Res);
            Res = Operations(a, b, "*");
            Console.WriteLine("The Mul is : " + Res);
            Res = Operations(a, b, "/");
            Console.WriteLine("The Div is : " + Res);
            Res = Operations(a, b, "%");
            Console.WriteLine("The Rem is : " + Res);
            Console.ReadKey();
        }
        public static int Operations(int x,int y,string Opchar)
        {
            int Returnval = 0;
            switch(Opchar)
            {
                case "+": { Returnval = x + y; break; }
                case "-": { Returnval = x - y; break; }
                case "*": { Returnval = x * y; break; }
                case "/": { Returnval = x / y; break; }
                case "%": { Returnval = x % y; break; }
            }
            return Returnval;
        }
    }
}
