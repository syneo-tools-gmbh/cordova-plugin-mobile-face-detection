# cordova-plugin-mobile-face-decetion
Cordova plugin for face-decetion using google mobile vision face API

> This plugin was made possible because of [google mobile vision face API](https://developers.google.com/vision/face-detection-concepts). This plugin is absolutely free and will work offline once install is complete. All required files required for Face detection are downloaded during install if necessary space is available.


This plugin defines a global `face` object, which provides an method that accepts image uri or base64 inputs. If some faces was detected in the image, this faces will be returned as a JSON Object (see further documentation for the format). The imageuri or base64 can be send to the plugin using any another plugin like [cordova-plugin-camera](https://github.com/apache/cordova-plugin-camera) or [cordova-plugin-document-scanner](https://github.com/NeutrinosPlatform/cordova-plugin-document-scanner). Although the object is attached to the global scoped `window`, it is not available until after the `deviceready` event.

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

**TODO** (Only manual installation as far)

# Plugin Usage

`face.recFace(sourceType,  uriOrBase, successCallback, errorCallback)

- **face.recFace**
The **`face.recFace`** function accepts image data as uri or base64 and uses google mobile vision to recognize faces and return the recognized faces as JSON Object on its successcallback.

- **sourceType**
The **`sourceType`** parameter can take values 0,1,2,3 or 4 each of which are explained in detail in the table below. `sourceType` is an `Int` within the native code.

| sourceType        | uriOrBase     | Accuracy      | Recommendation  | Notes       |
| :-------------:   |:-------------:|:-------------:|:-------------:  |:-------------:  |
| 0                 | NORMFILEURI   | Very High     | Recommended     | On android this is same as NORMNATIVEURI |
| 1                 | NORMNATIVEURI | Very High     | Not Recommended (See note below)     | On android this is same as NORMFILEURI |
| 2                 | FASTFILEURI   | Very Low      | Not Recommended | On android this is same as FASTNATIVEURI. Compression allows for faster processing but sacrifices a lot of accuracy. 
| 3                 | FASTNATIVEURI | Very Low      | Not Recommended | On android this is same as FASTFILEURI. Compression allows for faster processing but sacrifices a lot of accuracy.
| 4                 | BASE64        | Very High     | Not Recommended | Extremely memory intensive and thus not recommended

>*Note :- NORMNATIVEURI & FASTNATIVEURI for iOS uses deprecated methods to access images. This is to support the [camera](https://github.com/apache/cordova-plugin-camera) plugin which still uses the deprecated methods to return native image URI's using [ALAssetsLibrary](https://developer.apple.com/documentation/assetslibrary/alassetslibrary). This plugin uses non deprecated [PHAsset](https://developer.apple.com/documentation/photokit/phasset?language=objc) library whose deprecated method [fetchAssets(withALAssetURLs:options:)](https://developer.apple.com/documentation/photokit/phasset/1624782-fetchassets) is used to retrieve the image data.*

- **uriOrBase**
The plugin accepts image uri or base64 data in **`uriOrBase`** which is obtained from another plugin like cordova-plugin-document-scanner or cordova-plugin-camera.  This `uriOrBase` is then used by the plugin and via google mobile vision, it detects the faces on the image. The data required for face detector is initially downloaded when the app is first installed. 

- **successCallback**
The return value is sent to the **`successCallback`** callback function, in string format if no errors occured. 

- **errorCallback**
The **`errorCallback`** function returns `Scan Failed: Found no faces to scan` if no faces was detected on the image. It also return other messages based on the error conditions.

# Working Examples

**TODO**

# Return Object

The success callback will return a JSON Object in the format

```javascript
{
  "faces": [...]
}
```

Where faces in an array of Face objects.

## Face Object

A Face object has the following format:

```javascript
{
  // face ID
  "id": INTEGER,

  // top position of the face within the image
  "x": FLOAT,

  // left position of the face within the image
  "y": FLOAT,
  
  // rotation of the face about the vertical axis of the image
  "eulerY": FLOAT,
  
  // rotation of the face about the axis pointing out of the image
  "eulerZ": FLOAT,
  
  // width of the face region in pixels
  "width": FLOAT,
  
  // height of the face region in pixels
  "height": FLOAT,
  
  // value between 0.0 and 1.0 giving a probability that the face's left eye is open
  "leftEyeOpenProbability": FLOAT,
  
  // value between 0.0 and 1.0 giving a probability that the face's right eye is open
  "rightEyeOpenProbability": FLOAT,
  
  // value between 0.0 and 1.0 giving a probability that the face is smiling
  "smilingProbability": FLOAT,
  
  // array of contours object (eyes, nose, etc.) found on the face
  "contours": ARRAY OF CONTOURS OBJECT
  
  // array of landmarks object (eyes, nose, etc.) found on the face
  "landmarks": ARRAY OF LANDMARKS OBJECT
}
```

## Contour Object

A set of points that outlines a facial landmark or region such as eye, face, or lips.

When 'left' and 'right' are used, they are relative to the subject. For example, the LEFT_EYE contour is the subject's left eye, not the eye that is on the left when viewing the image.

```javascript
{
  // type of contour
  "type": INTEGER (CONTOUR TYPE)
  
  // array of (x, y) positions of the contour where (0, 0) is the upper-left corner of the image.
  "positions": [
    {
      "x": FLOAT,
      "y": FLOAT
    },
    ...
  ]
}
```

### Contour Type

-	`1` means `FACE` The outline of the subject's face
-	`2` means `LEFT_EYEBROW_TOP` The top outline of the subject's left eyebrow
-	`3` means `LEFT_EYEBROW_BOTTOM` The bottom outline of the subject's left eyebrow
-	`4` means `RIGHT_EYEBROW_TOP` The top outline of the subject's right eyebrow
-	`5` means `RIGHT_EYEBROW_BOTTOM` The bottom outline of the subject's right eyebrow
-	`6` means `LEFT_EYE` The outline of the subject's left eye cavity
-	`7` means `RIGHT_EYE` The outline of the subject's right eye cavity
-	`8` means `UPPER_LIP_TOP` The top outline of the subject's upper lip
-	`9` means `UPPER_LIP_BOTTOM` The bottom outline of the subject's upper lip
-	`10` means `LOWER_LIP_TOP` The top outline of the subject's lower lip
-	`11` means `LOWER_LIP_BOTTOM` The bottom outline of the subject's lower lip
-	`12` means `NOSE_BRIDGE` The outline of the subject's nose bridge
-	`13` means `NOSE_BOTTOM` The outline of the subject's nose bridge

## Landmark Object

**TODO**
