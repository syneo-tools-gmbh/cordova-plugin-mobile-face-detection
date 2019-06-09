# cordova-plugin-mobile-face-decetion
Cordova plugin for face-decetion using google mobile vision face API

> This plugin was made possible because of [google mobile vision face API](https://developers.google.com/vision/face-detection-concepts). This plugin is absolutely free and will work offline once install is complete. All required files required for Face detection are downloaded during install if necessary space is available.


This plugin defines a global `face` object, which provides an method that accepts image uri or base64 inputs. If some text was detected in the image, this text will be returned as a string. The imageuri or base64 can be send to the plugin using any another plugin like [cordova-plugin-camera](https://github.com/apache/cordova-plugin-camera) or [cordova-plugin-document-scanner](https://github.com/NeutrinosPlatform/cordova-plugin-document-scanner). Although the object is attached to the global scoped `window`, it is not available until after the `deviceready` event.

```
document.addEventListener("deviceready", onDeviceReady, false);
function onDeviceReady() {
console.log(face);
}
```

# Supported Platforms

- Android
- iOS (*TODO*)

# Installation Steps

*TODO* (Only manual installation as far)

# Plugin Usage

`face.recFace(sourceType,  uriOrBase, successCallback, errorCallback)

- **face.recFace**
The **`textocr.recText`** function accepts image data as uri or base64 and uses google mobile vision to recognize text and return the recognized text as string on its successcallback.

- **sourceType**
The **`sourceType`** parameter can take values 0,1,2,3 or 4 each of which are explained in detail in the table below. `sourceType` is an `Int` within the native code.

| sourceType        | uriOrBase     | Accuracy      | Recommendation  | Notes       |
| :-------------:   |:-------------:|:-------------:|:-------------:  |:-------------:  |
| 0                 | NORMFILEURI   | Very High     | Recommended     | On android this is same as NORMNATIVEURI |
| 1                 | NORMNATIVEURI | Very High     | Not Recommended (See note below)     | On android this is same as NORMFILEURI |
| 2                 | FASTFILEURI   | Very Low      | Not Recommended | On android this is same as FASTNATIVEURI. Compression allows for faster processing but sacrifices a lot of accuracy. Best used if ocr images will always be extremely large with large text in them. |
| 3                 | FASTNATIVEURI | Very Low      | Not Recommended | On android this is same as FASTFILEURI. Compression allows for faster processing but sacrifices a lot of accuracy. Best used if ocr images will always be extremely large with large text in them. |
| 4                 | BASE64        | Very High     | Not Recommended | Extremely memory intensive and thus not recommended

>*Note :- NORMNATIVEURI & FASTNATIVEURI for iOS uses deprecated methods to access images. This is to support the [camera](https://github.com/apache/cordova-plugin-camera) plugin which still uses the deprecated methods to return native image URI's using [ALAssetsLibrary](https://developer.apple.com/documentation/assetslibrary/alassetslibrary). This plugin uses non deprecated [PHAsset](https://developer.apple.com/documentation/photokit/phasset?language=objc) library whose deprecated method [fetchAssets(withALAssetURLs:options:)](https://developer.apple.com/documentation/photokit/phasset/1624782-fetchassets) is used to retrieve the image data.*
