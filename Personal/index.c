#include <stdio.h>
#include <stdlib.h>

int main ()
{   
    int ordenado;
    int vector[]={5,4,3,2,1};
     
 do
   {
     ordenado = 1 ;
     for (int i = 0 ; i < 4 ; i ++)
        {
           if (vector[i]>vector[i+1]) 
            {
             int aux = vector [i];
             vector[i] = vector[i+1];
             vector [i+1]= aux;
             ordenado = 0;
            }
        }    
    }  while (ordenado == 0 );

 for(int i = 0 ; i < 5 ; i++)
    {
        printf("\t%d",vector[i]);
    }
   return 0;
}
