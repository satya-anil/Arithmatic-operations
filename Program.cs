using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace GotoEx
{
    class Program
    {
        static void Main(string[] args)
        {
           
            int n;
            start:
            Console.Write("Enter Any value: ");
            n = Convert.ToInt32(Console.ReadLine());
            if(n % 5 != 0)
            {
                goto start;

            }
            Console.WriteLine("The Given Value is : " + n);
            Console.WriteLine("Program Completed");
            Console.ReadKey();
        }
    }
}
