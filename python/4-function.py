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

# keywork argument
def parrot(voltage, state='a stiff', action='voom', type='Norwegian Blue'):
    print("-- This parrot wouldn't", action, end=' ')
    print("if you put", voltage, "volts through it.")
    print("-- Lovely plumage, the", type)
    print("-- It's", state, "!")

parrot(1000)                                          # 1 positional argument
parrot(voltage=1000)                                  # 1 keyword argument
parrot(voltage=1000000, action='VOOOOOM')             # 2 keyword arguments
parrot(action='VOOOOOM', voltage=1000000)             # 2 keyword arguments
parrot('a million', 'bereft of life', 'jump')         # 3 positional arguments
parrot('a thousand', state='pushing up the daisies')  # 1 positional, 1 keyword

# the following are invalide
# parrot()                     # required argument missing
# parrot(voltage=5.0, 'dead')  # non-keyword argument after a keyword argument
# parrot(110, voltage=220)     # duplicate value for the same argument
# parrot(actor='John Cleese')  # unknown keyword argument



# Arbitrary Argument Lists, using packing
def concat(prefix, *args, sep="/"):
    return prefix + sep.join(args)

print(concat("Prifix:", "earth", "mars", "venus"))
print(concat("Prifix:", "earth", "mars", "venus", sep="-"))

# Unpacking Argument Lists
args = [3, 6]
print(list(range(*args)))

# dictionary using ** to unpacking
d = {"voltage": "four million", "state": "bleedin' demised", "action": "VOOM"}
parrot(**d)


# Lambda
def make_incrementor(n):
    return lambda x: x + n

f = make_incrementor(42)
print(f(0)) #42
print(f(1)) #43

pairs = [(1, 'one'), (2, 'two'), (3, 'three'), (4, 'four')]
pairs.sort(key=lambda pair: pair[1])
print(pairs) #[(4, 'four'), (1, 'one'), (3, 'three'), (2, 'two')]

# Document String
def my_function():
    """
    Do nothing, but document it.

    No, really, it doesn't do anything.
    """
    pass
print(my_function.__doc__)


# annotation
def foobar(a: int, b: "it's b", c: str = 5) -> tuple:
    return a, b, c
print(foobar(1, "b", 6))
print(foobar(1, "b"))
print(foobar.__annotations__)