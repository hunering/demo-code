words = ['cat', 'window', 'defenestrate']
for word in words:
    print(word, " length is ", len(word))

# the follwoing code cause infinite loop
# for word in words:
#   if len(word) > 6:
#     words.insert(0, "asfdsafd")

for word in words[:]:
    if len(word) > 6:
        words.insert(0, "asfdsafd")
