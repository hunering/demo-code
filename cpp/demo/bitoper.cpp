#include "stdafx.h"

#define BITS_PER_LONG	64
#define BITMAP_FIRST_WORD_MASK(start) (~0ULL << ((start) & (BITS_PER_LONG - 1)))
#define BITMAP_LAST_WORD_MASK(nbits) (~0ULL >> (-(nbits) & (BITS_PER_LONG - 1)))

void bitoper() {

	printf("BITMAP_FIRST_WORD_MASK(64):%llX\n", BITMAP_FIRST_WORD_MASK(64));
	printf("BITMAP_FIRST_WORD_MASK(65):%llX\n", BITMAP_FIRST_WORD_MASK(65));

	printf("BITMAP_LAST_WORD_MASK(64):%llX\n", BITMAP_LAST_WORD_MASK(64));
	printf("BITMAP_LAST_WORD_MASK(65):%llX\n", BITMAP_LAST_WORD_MASK(65));

	int i = (-(65) & (BITS_PER_LONG - 1));

}