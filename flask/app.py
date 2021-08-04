import os
import time
import re
import json
import subprocess
import plotly.graph_objects as go
from flask import Flask, render_template, request, abort
from werkzeug.utils import secure_filename

#-----Python-----#
def algorithm(fileName, searchOptions):
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
    
    def findExCaseSensitive(str_pattern, str_file):
        lst_regExPos = []
        regEx = re.compile(str_pattern)
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

    filePath = folderPath + "/" + fileName
    print(filePath)
    file = open(filePath, "r")
    lst_file = file.readlines()

    str_pattern = searchOptions["searchPattern"]
    int_fragmentRange = searchOptions["fragmentRange"]
    lst_fileClean = removeNewLine(lst_file)
    dct_headers = findHeader(lst_fileClean)
    str_file = formatFile(dct_headers, lst_fileClean)

    if searchOptions["caseSensitive"] == True:
        lst_regExPos = findExCaseSensitive(str_pattern, str_file)
    else:
        lst_regExPos = findEx(str_pattern, str_file)
    
    lst_fragmentSize = findFragmentSize(int_fragmentRange, lst_regExPos)
    
    print("DEBUG")
    print(f"The headers are located at lines: {dct_headers}")
    print(f"There are {len(lst_regExPos)} {str_pattern} positions.")
    print(f"The first 5 are {lst_regExPos[0:4]} and the last 5 are {lst_regExPos[len(lst_regExPos)-5:len(lst_regExPos)]}")
    print(f"The first 5 fragment sizes are: {lst_fragmentSize[0:5]}")
    print(f"There are {len(lst_fragmentSize)} fragments.")

    #-----Plotly-----#
    def plotFragmentSize():
        fig = go.Figure()
        fig.add_trace(go.Histogram(x = lst_fragmentSize, xbins=dict(size= 1), autobinx = False, marker_color = "black"))
        fig.update_layout(title = "Fragment Length Histogram")
        JSONFragmentRange = fig.to_json(validate = True, pretty = True)
        return JSONFragmentRange
    return plotFragmentSize()

#-----Define Variables-----#
folderPath = "ClusterAnalyzer/flask/uploads"
dct_defaultSearchOptions = {"caseSensitive": False,
                            "searchPattern": "CG",
                            "minimum": 15,
                            "maximum": 60,
                            "stepSize": 5,
                            "smoothingFactorStatus": True,
                            "smoothingFactor": 0,
                            "fragmentRange": 30}

def parseSearchOptions(searchOptions):
    searchOptions["minimum"] = int(searchOptions["minimum"])
    searchOptions["maximum"] = int(searchOptions["maximum"])
    searchOptions["stepSize"] = int(searchOptions["stepSize"])
    searchOptions["smoothingFactor"] = int(searchOptions["smoothingFactor"])
    searchOptions["fragmentRange"] = int(searchOptions["fragmentRange"])
    return searchOptions

#-----Flask-----#
app = Flask(__name__)
app.config["UPLOAD_EXTENSIONS"] = [".fasta", ".fna", ".ffn", ".faa", ".frn", ".fa"]
app.config["UPLOAD_PATH"] = folderPath

@app.route("/")
def index():
   return render_template("index.html", chartEmpty = "The graph for the uploaded Fasta file with be displayed here:")

@app.route("/upload_file", methods = ["GET", "POST"])
def upload_files():
    uploaded_file = request.files["file"]
    global filename
    filename = secure_filename(uploaded_file.filename)
    if filename != "":
        file_ext = os.path.splitext(filename)[1]
        if file_ext not in app.config["UPLOAD_EXTENSIONS"]:
            abort(400)
        uploaded_file.save(os.path.join(app.config["UPLOAD_PATH"], filename))
    print(filename)
    return render_template("index.html", file_status = "File uploaded", chartEmpty = "", JSONFragmentSize = algorithm(filename, dct_defaultSearchOptions))

@app.route("/search_options/<string:searchOptions>", methods = ["POST"])
def processSearchOptions(searchOptions):
    searchOptions = parseSearchOptions(json.loads(searchOptions))
    return render_template("index.html", file_status = "File uploaded", chartEmpty = "", JSONFragmentSize = algorithm(filename, searchOptions))

if __name__ == "__main__":
   app.run()

if __name__ == "__main__":
    app.run(debug=True)

#-----File Security-----#
loop = True

while loop == True:
    now = time.time()
    files = [os.path.join(folderPath, filename) for filename in os.listdir(folderPath)]
    for filename in files:
        if (now - os.stat(filename).st_mtime) > 1800:
            command = "rm {0}".format(filename)
            subprocess.call(command, shell=True)