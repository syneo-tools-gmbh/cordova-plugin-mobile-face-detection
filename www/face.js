/*global cordova, module*/

module.exports = {
    // TODO
    face: function (sourceType, imageSource, successCallback, errorCallback) {
        cordova.exec(successCallback, errorCallback, "Face", [sourceType, imageSource]);
    }
};
