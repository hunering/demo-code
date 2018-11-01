squares = []
for x in range(10):
    squares.append(x**2)

squares = list(map(lambda x: x**2, range(10)))
print(squares)

print([x**2 for x in range(10)])

# tuple
print([(x,y) for x in [1,2,3] for y in [1,2,3] if x!=y])

freshfruit = ['  banana', '  loganberry ', 'passion fruit  ']
print([weapon.strip() for weapon in freshfruit])

vec = [[1,2,3], [4,5,6], [7,8,9]]
print([num for elem in vec for num in elem])

matrix = [
    [1, 2, 3, 4],
    [5, 6, 7, 8],
    [9, 10, 11, 12],
]
print(matrix)
# row => column. 
# nested comprehension, "for i in range(4)" is the outside one
# for each i, the inner "row[i] for row in matrix" go through each row
print([[row[i] for row in matrix] for i in range(4)])

