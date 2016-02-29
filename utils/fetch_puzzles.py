import requests, re

result = []
f = file('easy_boards', 'w')

for i in range(100):
    r = requests.get('http://view.websudoku.com/?level=1')
    raw_puzzle = re.findall("\d{81}", r.text)

    id = re.findall("set_id=(\d*)\"", r.text)[0]
    print "id: " + id

    solved = raw_puzzle[0]
    cipher = raw_puzzle[2]

    unsolved = ''
    for i in range(len(solved)):
        if cipher[i] == '1':
            unsolved += '0'
        else:
            unsolved += solved[i]
    result.append(unsolved)

    print unsolved

    print>>f, unsolved

