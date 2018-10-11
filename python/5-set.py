# A set is an unordered collection with no duplicate elements.
# Basic uses include membership testing and eliminating
# duplicate entries. Set objects also support mathematical
# operations like union, intersection, difference, and symmetric difference.

empty = set()  # NOT {}, {} will create an empty dicrionary

basket = {'apple', 'orange', 'apple', 'pear', 'orange', 'banana'}
print(basket)

print("apple in set? ", "apple" in basket)
print("peach in set? ", "peach" in basket)

a = set('abracadabra')
b = set('alacazam')

print(a-b)  # letters in a but not in b
print(a | b)  # letters in a or b or both
print(a & b)  # letters in both a and b
print(a ^ b)  # letters in a or b but not both

a.remove('a')
print(a)
# set comprehensions
print({x for x in 'abracadabra' if x not in 'abc'})
