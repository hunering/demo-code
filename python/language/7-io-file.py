# it is good practice to use the 'with' keyword when 
# dealing with file objects. The advantage is that 
# the file is properly closed after its suite finishes, 
# even if an exception is raised at some point. 
# Using with is also much shorter than writing equivalent try-finally blocks:

with open('text_file_read.txt') as f:
    read_data = f.read() # will read the entire file content as string
    print(read_data)
    print(f.read()=='') # will read empty string('') if we already read this file 
print("is file closed after with keyword? ", f.closed)

with open('text_file_read.txt') as f:
    for line in f:
      print(line, end='')


with open('text_file_write.txt', 'w') as f:
    f.write("This is for write\nThis is the second line\n")
    f.writelines("lines")
    
print()
f = open('text_file_read_write.b', 'rb+')
# Bytes literals are always prefixed with 'b' or 'B'; 
# they produce an instance of the bytes type instead of the str type. 
# They may only contain ASCII characters; bytes with a 
# numeric value of 128 or greater must be expressed with escapes.
f.write(b'0123456789abcdef')
f.seek(5)
f.read(1)
f.seek(-3, 2)  # Go to the 3rd byte before the end
f.read(1)
print(f.tell())
