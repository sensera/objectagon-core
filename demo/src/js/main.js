
var transactionAlias = "mainTrans";

function createApplicationDomain() {
    createTransaction(transactionAlias).done(function () {
        createDomain(transactionAlias, update);
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
    console.log("Create initial transaction");
    createTransaction(transactionAlias).done(function () {
        nameSearch(MAIN_CLASS_NAME, MAIN_CLASS_ALIAS).done(function (data) {
            //console.log("Found class");
            showMain();
        }).fail(function (err) {
            //console.log("Failed to get class", err);
            showCreateDomain()
        });
    }).fail(function (err) {
        alert("Unable to connect to objectagon server!");
    });
}
