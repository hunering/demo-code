def fib(n):    # write Fibonacci series up to n
    """Print a Fibonacci series up to n."""
    a, b = 0, 1
    while a < n:
        print(a, end=' ')
        a, b = b, a+b
    print()

fib(2000)

def ask_ok(prompt, retries=4, reminder='Please try again!'):
    while True:
        ok = input(prompt)
        if ok in ('y', 'ye', 'yes'):
            return True
        if ok in ('n', 'no', 'nop', 'nope'):
            return False
        retries = retries - 1
        if retries < 0:
            raise ValueError('invalid user response')
        print(reminder)

# ask_ok("Do you really want to quit?")
# ask_ok("Do you really want to quit, will retry tiwce?", 2)
# ask_ok("Do you really want to quit?, with reminder-Come on", 2, "Come on")

#The default values are evaluated at the point of function definition in the defining scope
i = 5
def f(arg=i):
    print(arg)

i = 6
f() # will print 5

# Important warning: The default value is evaluated only once. 
# This makes a difference when the default is a mutable object 
# such as a list, dictionary, or instances of most classes. 
# For example, the following function accumulates the arguments 
# passed to it on subsequent calls:
def f2(a, L=[]):
    L.append(a)
    return L
# the following will print
# [1]
# [1, 2]
# [1, 2, 3]
print(f2(1))
print(f2(2))
print(f2(3))

# If you don’t want the default to be shared between subsequent calls, 
# you can write the function like this instead:
def f3(a, L=None):
    if L is None:
        L = []
    L.append(a)
    return L