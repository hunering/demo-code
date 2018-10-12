
# Formatted String Literals
s = 'Hello, world.'
print("str(s): ", str(s))
print("repr(s):", repr(s))

s = 'Hello, world.\n'
print("str(s): ", str(s))
print("repr(s):", repr(s))

print(repr((s, ('spam', 'eggs'))))

import math
print(f'The value of pi is approximately {math.pi:.3f}.')

table = {'Sjoerd': 4127, 'Jack': 4098, 'Dcab': 7678}
for name, phone in table.items():
    print(f'{name:10} ==> {phone:10d}')


# Other modifiers can be used to convert the value before it is formatted. 
# '!a' applies ascii(), '!s' applies str(), and '!r' applies repr():
animals = 'eels'
print(f'My hovercraft is full of {animals}.')
print(f'My hovercraft is full of {animals!r}.')

# String format() 
print('We are the {} who say "{}!"'.format('knights', 'Ni'))
print('{0} and {1}'.format('spam', 'eggs'))
print('{1} and {0}'.format('spam', 'eggs'))
print('This {food} is {adjective}.'.format(food='spam', adjective='absolutely horrible'))
print('The story of {0}, {1}, and {other}.'.format('Bill', 'Manfred', other='Georg'))

table = {'Sjoerd': 4127, 'Jack': 4098, 'Dcab': 8637678}
print('Jack: {0[Jack]:d}; Sjoerd: {0[Sjoerd]:d}; Dcab: {0[Dcab]:d}'.format(table))
print('Jack: {Jack:d}; Sjoerd: {Sjoerd:d}; Dcab: {Dcab:d}'.format(**table))

# Manual String Formatting
for x in range(1, 11):
    print(repr(x).rjust(2), repr(x*x).rjust(3), end=' ')
    # Note use of 'end' on previous line
    print(repr(x*x*x).rjust(4))

print("abc".zfill(10))