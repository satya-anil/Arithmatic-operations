using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace MethodEx4
{
    class Program
    {
        static void Main(string[] args)
        {
            int[] arr = new int[5];
            int[] brr = new int[arr.Length];
            Console.WriteLine("Enter Any Values : ");
            for(int i=0; i<arr.Length; i++)
            {
                Console.Write("arr[" + i + " ] = ");
                arr[i] = Convert.ToInt32(Console.ReadLine());
            }
            brr = FindReverseArray(arr);
            Console.WriteLine("The Reverse of the array values are : ");
            printArrayValues(brr);
            Console.ReadKey();

        }
        public static  int[] FindReverseArray(int[] X)
        {
            int[] revX = new int[X.Length]; 
            for(int i=0; i<X.Length; i++)
            {
                revX[i] = X[(X.Length - 1) - i];

            }
            return revX;
           
        }
        public static void printArrayValues(int[] X)
        {
            for (int i = 0; i < X.Length; i++)
            {
                Console.Write(X[i] + " ");
            }
        }    
    }
}
