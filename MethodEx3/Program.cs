using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace MethodEx3
{
    class Program
    {
        static void Main(string[] args)
        {
            int n;
            string Res = "";
            Console.Write("Enter Any Value : ");
            n = Convert.ToInt32(Console.ReadLine());
            Res=FindEvenorOdd(n);
            Console.WriteLine("The Given Number is : " + Res);
            Res = FindPrime(n);
            Console.WriteLine("The Given Number is : " + Res);
            Console.ReadKey();
        }
        public static string FindEvenorOdd(int n)
        {
            if(n % 2 == 0)
            {
                return "Even";

            }
            else
            {
                return "Odd";
            }
        }
        public static string FindPrime(int n)
        {
            int i=1 , count =0;
            while(i<=n)
            {
                if(n % i ==0)
                {
                    count++;
                }
                i++;
            }
            if(count==2)
            {
                return "prime";
            }
            else
            {
                return "Not prime";
            }
        }
    }
}
