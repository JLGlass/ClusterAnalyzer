function graphTypeChange() {
    if (document.getElementById("graphTypeSel").value == 1) {
        document.getElementById("graphTitle").innerHTML = "Local Minima"
        document.getElementById("graph").title = "Minima"
    }
    else if (document.getElementById("graphTypeSel").value == 2) {
        document.getElementById("graphTitle").innerHTML = "Average Cluser Size vs. Maximum Fragment Length"
        document.getElementById("graph").title = "Optimization"
    }
    else {
        document.getElementById("graphTitle").innerHTML = "Fragment Length"
        document.getElementById("graph").title = "Fragment Length"
    }
}

function resetBtn() {
    document.getElementById("caseSensitiveChkbx").checked = false
    document.getElementById("searchPatternTxt").value = "CG"
    document.getElementById("minimumTxt").value = 15
    document.getElementById("maximumTxt").value = 60
    document.getElementById("stepSizeTxt").value = 5
    document.getElementById("smoothingFactorChkbx").checked = true
    document.getElementById("smoothingFactorTxt").value = 0
}

function clearBtn() {
    document.getElementById("lowerBoundTxt").value = 0
    document.getElementById("upperBoundTxt").value = 0
}

function resetDomainBtn() {
    document.getElementById("domainMinimumTxt").value = 1.7976931348623157E308
    document.getElementById("domainMaximumTxt").value = 0.0
}

function sendSearchOptions() {
    let searchOptions = {
        "caseSensitive": document.getElementById("caseSensitiveChkbx").checked,
        "searchPattern": document.getElementById("searchPatternTxt").value,
        "minimum": document.getElementById("minimumTxt").value,
        "maximum": document.getElementById("maximumTxt").value,
        "stepSize": document.getElementById("stepSizeTxt").value,
        "smoothingFactorStatus": document.getElementById("smoothingFactorChkbx").checked,
        "smoothingFactor": document.getElementById("smoothingFactorTxt").value,
        "fragmentRange": document.getElementById("graphedTxt").value,
    }
    const request = new XMLHttpRequest()
    request.open("POST", `/search_options/${JSON.stringify(searchOptions)}`)
    request.onload = () => {
        const flaskMessage = request.responseText
        console.log(flaskMessage)
    }
    request.send()
}