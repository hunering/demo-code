# dictionaries are indexed by keys, 
# which can be any immutable type; 
# strings and numbers can always be keys. 
# Tuples can be used as keys if they contain only strings, numbers, or tuples;

empty = {}
print(empty)
tel = {'jack': 4098, 'sape': 4139}
print(tel)
tel['guido'] = 4127
print(tel)

print(tel['jack'])

del tel['sape']
tel['irv'] = 4127
print(tel)


print(list(tel))

print(sorted(tel))

print('guido' in tel)

print('jack' not in tel)

print(dict([('sape', 4139), ('guido', 4127), ('jack', 4098)]))
print(dict([['sape', 4139], ['guido', 4127], ['jack', 4098]]))
dict(sape=4139, guido=4127, jack=4098)

print({x: x**2 for x in (2, 4, 6)})

# Loop
knights = {'gallahad': 'the pure', 'robin': 'the brave'}

for item in knights: # go through the keys
    print(item)

for k, v in knights.items(): # go through both the key and value
    print(k, v)



