
for i, v in enumerate(['tic', 'tac', 'toe']):
    print(i, v)

knights = {'gallahad': 'the pure', 'robin': 'the brave'}
for item in knights: # go through the keys
    print(item)

for k, v in knights.items(): # go through both the key and value
    print(k, v)

questions = ['name', 'quest', 'favorite color']
answers = ['lancelot', 'the holy grail', 'blue']
for q, a in zip(questions, answers):
    print('What is your {0}?  It is {1}.'.format(q, a))

# reverse
for i in reversed(range(1, 10, 2)):
    print(i)

# sort
basket = ['apple', 'orange', 'apple', 'pear', 'orange', 'banana']
for f in sorted(set(basket)):
    print(f)

for f in sorted(basket):
    print(f)


string1, string2, string3 = '', 'Trondheim', 'Hammer Dance'
non_null = string1 or string2 or string3
if string1 or string2 or string3:
  print('')
print(non_null)