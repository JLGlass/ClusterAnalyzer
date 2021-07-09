import re

def removeNewLine(lst_file):
    for i in lst_file:
        lst_fileClean.append(i.strip())
    return lst_fileClean

def findHeader(lst_fileClean):
    for i in range(len(lst_fileClean)):
        if lst_fileClean[i][0] == ">":
            if lst_fileClean[i] == ">":
                dct_headers[i] = f"default{i}"
            else:
                dct_headers[i] = lst_fileClean[i][1:len(lst_fileClean[i])]
    return dct_headers, lst_fileClean

def formatFile(dct_headers, lst_fileClean):
    for key in dct_headers.keys():
            lst_fileClean.pop(key)
    str_file = "".join(lst_fileClean)
    return str_file

def findEx(str_pattern, str_file):
    regEx = re.compile(str_pattern)
    for match in regEx.finditer(str_file):
        lst_regExPos.append(match.start())
    return lst_regExPos

#Open file
filePath = "E:\Internship_MSK\chr21.fa"
file = open(filePath, "r")
lst_file = file.readlines()

#Define variables
dct_headers = {}
lst_fileClean = []
lst_regExPos = []
str_pattern = input(str("Regular expression to search for:"))

#Run code
removeNewLine(lst_file)
findHeader(lst_fileClean)
findEx(str_pattern, formatFile(dct_headers, lst_fileClean))

print(f"The headers are located at lines: {dct_headers}")
print(f"There are {len(lst_regExPos)} {str_pattern} positions.")
print(f"The first 5 are {lst_regExPos[0:4]} and the last 5 are {lst_regExPos[len(lst_regExPos)-5:len(lst_regExPos)]}")