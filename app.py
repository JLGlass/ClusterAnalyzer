import re
import plotly.graph_objects as go
from collections import Counter

def removeNewLine(lst_file):
    lst_fileClean = []
    for i in lst_file:
        lst_fileClean.append(i.strip())
    return lst_fileClean

def findHeader(lst_fileClean):
    dct_headers = {}
    for i in range(len(lst_fileClean)):
        if lst_fileClean[i][0] == ">":
            if lst_fileClean[i] == ">":
                dct_headers[i] = f"default{i}"
            else:
                dct_headers[i] = lst_fileClean[i][1:len(lst_fileClean[i])]
    return dct_headers

def formatFile(dct_headers, lst_fileClean):
    for key in dct_headers.keys():
            lst_fileClean.pop(key)
    return "".join(lst_fileClean)

def findEx(str_pattern, str_file):
    lst_regExPos = []
    regEx = re.compile(str_pattern, re.IGNORECASE)
    for match in regEx.finditer(str_file):
        lst_regExPos.append(match.start())
    return lst_regExPos

def findFragmentSize(int_fragmentRange, lst_regExPos):
    lst_fragmentSize = []
    int_pos1 = 0
    int_pos2 = int_pos1 + int_fragmentRange
    int_posLast = len(lst_regExPos)
    while int_pos2 < int_posLast:
        lst_fragmentSize.append(lst_regExPos[int_pos2]-lst_regExPos[int_pos1])
        int_pos1 += 1
        int_pos2 += 1
    lst_fragmentSize = [num for num in lst_fragmentSize if num < 7000]
    return lst_fragmentSize

#Plotly
def plotFragments():
    fig = go.Figure()
    for i in range(1000):
        fig.add_trace(go.Box(x = (lst_regExPos[i],lst_regExPos[i+int_fragmentRange]), name = f"Fragment {i}", marker_color = "black"))
    fig.update_layout(title = f"Fragment Range: {int_fragmentRange}")
    fig.show()

def plotFragmentSize():
    fig = go.Figure()
    fig.add_trace(go.Histogram(x = lst_fragmentSize, xbins=dict(size= 1), autobinx = False, marker_color = "black"))
    fig.update_layout(title = "Fragment Length Histogram")
    fig.show()
    #fig.write_html(r"C:\Users\ethan\Documents\GitHub\ClusterAnalyzer\plot.html")

#Open file
filePath = "E:\Internship_MSK\chr21.fa"
file = open(filePath, "r")
lst_file = file.readlines()

#Define variables
str_pattern = str(input("Regular expression to search for: "))
int_fragmentRange = int(input("Range of fragments: "))

#Run code
lst_fileClean = removeNewLine(lst_file)
dct_headers = findHeader(lst_fileClean)
str_file = formatFile(dct_headers, lst_fileClean)
lst_regExPos = findEx(str_pattern, str_file)
lst_fragmentSize = findFragmentSize(int_fragmentRange, lst_regExPos)
dct_fragmentSizeFrequency = Counter(lst_fragmentSize)

print(f"The headers are located at lines: {dct_headers}")
print(f"There are {len(lst_regExPos)} {str_pattern} positions.")
print(f"The first 5 are {lst_regExPos[0:4]} and the last 5 are {lst_regExPos[len(lst_regExPos)-5:len(lst_regExPos)]}")
print(f"The first 5 fragment sizes are: {lst_fragmentSize[0:5]}")
print(f"There are {len(lst_fragmentSize)} fragments.")

#plotFragments()
plotFragmentSize()