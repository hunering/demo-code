#include <stdio.h>

int main(void) {
	printf("asm inside\n");
	int a=10, b;
	asm ( "movl %1, %%eax;"
	      "movl %%eax, %0;"
	      "std;"
	      "cld;"
	      :"=r"(b)  /* output */   
	      :"r"(a)       /* input */
	      :"%eax"); /* clobbered register */
	
	return 0;
}
