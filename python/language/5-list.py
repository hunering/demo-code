# list operation
the_list = [1, 2, 3, 4]
# l[len(l):] = 5 #TypeError: can only assign an iterable
the_list[len(the_list):] = [5]
print(the_list)

the_list.append(6)
print(the_list)

the_list[len(the_list):] = range(7,9) # iterate the iterable, and add them to the list
print(the_list)

the_list.append(range(7,9)) # add the range object to the list
print(the_list)

# Extend the list by appending all the items from the iterable. 
# Equivalent to a[len(a):] = iterable.
the_list.extend(range(7, 9)) 
print(the_list)

the_list.insert(0, "insert at 0")
print(the_list)
the_list.insert(len(the_list), "insert at end")
print(the_list)

print("remove the element: 'insert at 0'")
the_list.remove("insert at 0")
print(the_list)

print("pop the first element")
first_item = the_list.pop(0)
print(first_item, " is removed")
print(the_list)

print("pop the last element")
last_item = the_list.pop()
print(last_item, " is removed")
print(the_list)

print("clear the list")
the_list.clear()
print(the_list)

the_list = list(range(0,5))
the_list[len(the_list):] = range(0, 5)
print("new list is", the_list)
print("index of 3 is ", the_list.index(3))
print("index of 3 is start from 4 is ", the_list.index(3, 4))
# throw '3 is not in list'
# print("index of 3 is start from 4 and end with 7 is ", the_list.index(3, 4, 7))

print("count of 3 is ", the_list.count(3))


copied_list = the_list.copy() # Equivalent to the_list[:]
copied_list[0] = 100
print("the original list: ", the_list)
print("the modified list: ", copied_list)

for i, v in enumerate(['tic', 'tac', 'toe']):
    print(i, v)