#include <stdio.h>
#define BITS_PER_LONG 64
#define BITMAP_LAST_WORD_MASK(nbits) (~0UL >> (-(nbits) & (64 - 1)))

#define BITMAP_LAST_WORD_MASK_TOOLS(nbits)					\
(									\
	((nbits) % BITS_PER_LONG) ?					\
		(1UL<<((nbits) % BITS_PER_LONG))-1 : ~0UL		\
)

int main(void) {
	printf("BITMAP_LAST_WORD_MASK(32):%d\n", BITMAP_LAST_WORD_MASK(32));
	printf("BITMAP_LAST_WORD_MASK(65):%d\n", BITMAP_LAST_WORD_MASK(65));
	printf("BITMAP_LAST_WORD_MASK_TOOLS(32):%d\n", BITMAP_LAST_WORD_MASK_TOOLS(32));
	printf("BITMAP_LAST_WORD_MASK_TOOLS(65):%d\n", BITMAP_LAST_WORD_MASK_TOOLS(65));
	
	return 0;
}
