#include <stdio.h>
#include <inttypes.h>

#define P4D_SHIFT	39
#define __AC(X,Y)	(X##Y)
#define _AC(X,Y)	__AC(X,Y)

int main(void) {
	unsigned long va = _AC(-4, UL);
	
	printf("val = 0x%" PRIx64 "\n", va);
	printf("val = 0x%016llx\n", _AC(-4, UL));
	printf("val = 0x%016llx\n", (_AC(-4, UL)<<P4D_SHIFT));
	return 0;
}

