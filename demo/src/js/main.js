
var transactionAlias = "mainTrans";

function createApplicationDomain() {
    createTransaction(transactionAlias).done(function () {
        createDomain(transactionAlias, commitTransaction(transactionAlias).done(update));
    });
}

function update() {
    console.log("update");
    showMain();
}

function showMain() {
    $("#create-domain-panel").hide();
    $("#main-panel").show();
    $("#connect-to-objectagon").hide();
}

function showCreateDomain() {
    $("#create-domain-panel").show();
    $("#connect-to-objectagon").hide();
}

function connectToObjectagon() {
    objectagon_url = document.getElementById("objectagon-url").value;
    nameSearch(MAIN_CLASS_NAME, MAIN_CLASS_ALIAS).done(function (data) {
        console.log("Found class", data);
        showMain();
    }).fail(function (err) {
        console.log("Failed to get class", err);
        if (err.statusText==="error") {
            alert("Unable to connect to objectagon server!");
        } else {
            showCreateDomain();
        }
    });
}
