struct duart {
  int i;
};

struct duart a __attribute__((section("DUART_A" "..page_aligned"))) = {0};

struct duart b __attribute__((section("DUART_B"))) = {0};

char stack[10000] __attribute__((section("STACK"))) = {0};

int init_data __attribute__((section("INITDATA"))) = 0;

main() {

  /* Initialize stack pointer */

  //init_sp(stack + sizeof(stack));

  /* Initialize initialized data */

  //memcpy(&init_data, &data, &edata - &data);

  /* Turn on the serial ports */

  //init_duart(&a);

  //init_duart(&b);
}
