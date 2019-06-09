/*global cordova, module*/

module.exports = {
    recFace: function (sourceType, imageSource, successCallback, errorCallback) {
        cordova.exec(successCallback, errorCallback, "Face", "recFace", [sourceType, imageSource]);
    }
};
