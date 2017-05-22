
var transactionAlias = "mainTrans";

function createApplicationDomain() {
    createTransaction(transactionAlias).done(function () {
        //createDomain(transactionAlias, commitTransaction(transactionAlias).done(update));
        createDomain(transactionAlias, update);
    });
}

function update() {
    console.log("update");
    showMain();
    getInstanceByName(MAIN_CLASS_NAME, MAIN_CLASS_INSTANCE_NAME).done(function (data) {
        console.log("Found class", data);
        getInstanceRelations(MAIN_CLASS_INSTANCE_NAME, MAIN_PERSON_RELATION_ALIAS).done(function (data) {
            console.log("Found instance relations", data);
        })
    }).fail(function (err) {
        console.log("Failed to get class", err);
    });
    /*nameSearch(MAIN_CLASS_NAME, MAIN_CLASS_ALIAS).done(function (data) {
        console.log("Found class", data);
    }).fail(function (err) {
        console.log("Failed to get class", err);
    });*/
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
