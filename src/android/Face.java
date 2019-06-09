package com.synetools.faceplugin;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.PluginResult;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.Manifest;
import android.content.Context;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.util.SparseArray;

import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.face.Face;
import com.google.android.gms.vision.face.FaceDetector;

import java.io.FileNotFoundException;
import java.util.Objects;


public class Face extends CordovaPlugin {

  //private static final int REQUEST_CODE = 99;

  private static final int NORMFILEURI = 0; // Make bitmap without compression using uri from picture library (NORMFILEURI & NORMNATIVEURI have same functionality in android)
  private static final int NORMNATIVEURI = 1; // Make compressed bitmap using uri from picture library for faster detection but might reduce accuracy (NORMFILEURI & NORMNATIVEURI have same functionality in android)
  private static final int FASTFILEURI = 2; // Make uncompressed bitmap using uri from picture library (FASTFILEURI & FASTFILEURI have same functionality in android)
  private static final int FASTNATIVEURI = 3; // Make compressed bitmap using uri from picture library for faster detection but might reduce accuracy (FASTFILEURI & FASTFILEURI have same functionality in android)
  private static final int BASE64 = 4;  // send base64 image instead of uri

  private static final int TARGETWIDTH = 1024;
  private static final int TARGETHEIGHT = 1024;
  private FaceDetector detector;

  @Override
  public boolean execute(String action, final JSONArray args, final CallbackContext callbackContext) throws JSONException {
    if (action.equals("recFace")) {
      cordova.getThreadPool().execute(new Runnable() {
        public void run() {
          try {
            int argstype = NORMFILEURI;
            String argimagestr = "";
            try {
              argstype = args.getInt(0);
              argimagestr = args.getString(1);
            } catch (Exception e) {
              callbackContext.error("Argument error");
              PluginResult r = new PluginResult(PluginResult.Status.ERROR);
              callbackContext.sendPluginResult(r);
            }
            Bitmap bitmap = null;
            Uri uri = null;
            if (argstype == NORMFILEURI || argstype == NORMNATIVEURI || argstype == FASTFILEURI || argstype == FASTNATIVEURI) {
              try {
                if (!argimagestr.trim().equals("")) {
                  String imagestr = argimagestr;

                  // code block that allows this plugin to directly work with document scanner plugin and camera plugin
                  if (imagestr.substring(0, 6).equals("file://")) {
                    imagestr = argimagestr.replaceFirst("file://", "");
                  }

                  uri = Uri.parse(imagestr);

                  if ((argstype == NORMFILEURI || argstype == NORMNATIVEURI) && uri != null) // normal detection
                  {
                    bitmap = MediaStore.Images.Media.getBitmap(cordova.getActivity().getBaseContext().getContentResolver(), uri);
                  } else if ((argstype == FASTFILEURI || argstype == FASTNATIVEURI) && uri != null) //fast detection (might be less accurate)
                  {
                    bitmap = decodeBitmapUri(cordova.getActivity().getBaseContext(), uri);
                  }

                } else {
                  callbackContext.error("Image Uri or Base64 string is empty");
                  PluginResult r = new PluginResult(PluginResult.Status.ERROR);
                  callbackContext.sendPluginResult(r);
                }
              } catch (Exception e) {
                e.printStackTrace();
                callbackContext.error("Exception");
                PluginResult r = new PluginResult(PluginResult.Status.ERROR);
                callbackContext.sendPluginResult(r);
              }
            } else if (argstype == BASE64) {
              if (!argimagestr.trim().equals("")) {
                byte[] decodedString = Base64.decode(argimagestr, Base64.DEFAULT);
                bitmap = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
              } else {
                callbackContext.error("Image Uri or Base64 string is empty");
                PluginResult r = new PluginResult(PluginResult.Status.ERROR);
                callbackContext.sendPluginResult(r);
              }
            } else {
              callbackContext.error("Non existent argument. Use 0, 1, 2 , 3 or 4");
              PluginResult r = new PluginResult(PluginResult.Status.ERROR);
              callbackContext.sendPluginResult(r);
            }

            detector = new FaceDetector.Builder(this).setTrackingEnabled(false).build();

            if (detector.isOperational() && bitmap != null) {


              Frame frame = new Frame.Builder().setBitmap(bitmap).build();
              SparseArray<Face> faces = faceDetector.detect(frame);

              JSONObject resultobj = new JSONObject();
              JSONArray resultFaces = new JSONArray();

              for (int i = 0; i < faces.size(); i++) {
                Face thisFace = faces.valueAt(i);

                JSONObject faceObj = new JSONObject();

                // Face ID
                faceObj.put("id", thisFace.getId());

                // Face rotations
                faceObj.put("eulerY", thisFace.getEulerY());
                faceObj.put("eulerZ", thisFace.getEulerZ());
                faceObj.put("height", thisFace.getHeight());
                faceObj.put("width", thisFace.getWidth());

                // top left position of the face in image
                faceObj.put("x", thisFace.getPosition().x);
                faceObj.put("y", thisFace.getPosition().y);

                // eyes open/smile probabilities
                faceObj.put("leftEyeOpenProbability", thisFace.getIsLeftEyeOpenProbability());
                faceObj.put("rightEyeOpenProbability", thisFace.getIsRightEyeOpenProbability());
                faceObj.put("smilingProbability", thisFace.getIsSmilingProbability());

                // contours
                JSONArray contoursArr = new JSONArray();
                List<Contour> contours = thisFace.getContours();
                for (int j = 0; j < contours.size(); j++) {
                  Contour contour = contours.get(j);
                  JSONObject contourObj = new JSONObject();
                  JSONArray positions = new JSONArray();
                  for (int k = 0; k < contour.getPositions().length; k++) {
                    JSONObject posXY = new JSONObject();
                    posXY.put("x", contour.getPositions()[k].x);
                    posXY.put("y", contour.getPositions()[k].y);
                    positions.put(posXY);
                  }
                  contourObj.put("positions", positions);
                  contourObj.put("type", contour.getType());
                  contoursArr.put(contourObj);
                }
                faceObj.put("contours", contoursArr);

                // landmarks
                JSONArray landmarkArr = new JSONArray();
                List<Landmark> landmarks = thisFace.getLandmarks();
                for (int j = 0; j < landmarks.size(); j++) {
                  Contour landmark = landmarks.get(j);
                  JSONObject landmarkObj = new JSONObject();
                  JSONObject posXY = new JSONObject();
                  posXY.put("x", landmark.getPosition().x);
                  posXY.put("y", landmark.getPosition().y);
                  landmarkObj.put("position", posXY);
                  landmarkObj.put("type", landmark.getType());
                  landmarksArr.put(landmarkObj);
                }
                faceObj.put("landmarks", landmarkArr);

                resultFaces.put(faceObj);
              }

              resultobj.put("faces", resultFaces);

              callbackContext.success(resultobj);
              PluginResult r = new PluginResult(PluginResult.Status.OK);
              callbackContext.sendPluginResult(r);

            } else {
              if (bitmap == null) {
                callbackContext.error("Problem with uri or base64 data!");
                PluginResult r = new PluginResult(PluginResult.Status.ERROR);
                callbackContext.sendPluginResult(r);
              } else {
                callbackContext.error("Could not set up the detector! Try Again!");
                PluginResult r = new PluginResult(PluginResult.Status.ERROR);
                callbackContext.sendPluginResult(r);
              }

            }
          } catch (Exception e) {
            callbackContext.error("Main loop Exception");
            PluginResult r = new PluginResult(PluginResult.Status.ERROR);
            callbackContext.sendPluginResult(r);
          }
          //callbackContext.success(); // Thread-safe.
        }
      });

      return true;

    }
    return false;
  }


  private Bitmap decodeBitmapUri(Context ctx, Uri uri) throws FileNotFoundException {
    BitmapFactory.Options bmOptions = new BitmapFactory.Options();
    bmOptions.inJustDecodeBounds = true;
    BitmapFactory.decodeStream(ctx.getContentResolver().openInputStream(uri), null, bmOptions);
    int photoW = bmOptions.outWidth;
    int photoH = bmOptions.outHeight;

    int scaleFactor = Math.min(photoW / TARGETWIDTH, photoH / TARGETHEIGHT);
    bmOptions.inJustDecodeBounds = false;
    bmOptions.inSampleSize = scaleFactor;

    return BitmapFactory.decodeStream(ctx.getContentResolver()
        .openInputStream(uri), null, bmOptions);
  }
}
