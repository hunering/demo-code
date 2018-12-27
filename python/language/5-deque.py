from collections import deque

# double-ended queue

queue = deque(["Eric", "John", "Michael"])
print(queue)
queue.append("Terry")           # Terry arrives
print(queue)
queue.append("Graham")          # Graham arrives
print(queue)
queue.popleft()                 # The first to arrive now leaves
print(queue)
queue.popleft()                 # The second to arrive now leaves
print(queue)
