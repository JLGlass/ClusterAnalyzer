function graphTypeChange() {
    if (document.getElementById("graphTypeSel").value == 1) {
        document.getElementById("graphTitle").innerHTML = "Local Minima";
        document.getElementById("graph").title = "Minima";
    }
    else if (document.getElementById("graphTypeSel").value == 2) {
        document.getElementById("graphTitle").innerHTML = "Average Cluser Size vs. Maximum Fragment Length";
        document.getElementById("graph").title = "Optimization";
    }
    else {
        document.getElementById("graphTitle").innerHTML = "Fragment Length";
        document.getElementById("graph").title = "Fragment Length";
    }
}

function resetBtn() {
    document.getElementById("caseSensitiveChkbx").checked = false
    document.getElementById("searchPatternTxt").value = "CG"
    document.getElementById("minimumTxt").value = "15"
    document.getElementById("maximumTxt").value = "60"
    document.getElementById("stepSizeTxt").value = "5"
    document.getElementById("smoothingFactorChkbx").checked = true
}

function clearBtn() {
    document.getElementById("lowerBoundTxt").value = ""
    document.getElementById("upperBoundTxt").value = ""
}

function resetDomainBtn() {
    document.getElementById("domainMinimumTxt").value = "1.7976931348623157E308"
    document.getElementById("domainMaximumTxt").value = "0.0"
}