import json
print(json.dumps([1, 'simple', 'list']))

with open('text_file_json.txt', 'r+') as f:
    x = [1, 'simple', 'list']
    json.dump(x, f)
    
    f.seek(0)
    x = json.load(f)