# Tuples are immutable, and usually contain a heterogeneous 
# sequence of elements that are accessed via unpacking 
# (see later in this section) or indexing (or even by 
# attribute in the case of namedtuples). Lists are mutable, 
# and their elements are usually homogeneous and are accessed 
# by iterating over the list.

empty = ()
print(empty)

single_item = 'single',
print(single_item)

single_item = ('single',)
print(single_item)

# packing as tuple
t = 12345, 54321, 'hello!'
print(t)

t = (12345, 54321, 'hello!')
print(t)

# unpacking the tuple
x, y, z = t
print(x, y, z)

print(*t)