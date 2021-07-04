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