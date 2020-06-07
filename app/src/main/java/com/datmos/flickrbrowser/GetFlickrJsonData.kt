package com.datmos.flickrbrowser

import android.os.AsyncTask
import android.util.Log
import org.json.JSONObject

private const val TAG = "GetFlickrJsonData"

class GetFlickrJsonData(private val listener: OnDataAvailable) :
    AsyncTask<String, Void, ArrayList<Photo>>() {

    interface OnDataAvailable {
        fun onDataAvailable(data: ArrayList<Photo>)
        fun onError(exception: Exception)
    }

    override fun doInBackground(vararg params: String?): ArrayList<Photo> {
        Log.d(TAG, "doInBackground starts")
        val photoList = ArrayList<Photo>()
        try {
            val jsonData = JSONObject(params[0])
            val itemsArray = jsonData.getJSONArray("items")
            for (i in 0 until itemsArray.length()) {
                val item = itemsArray.getJSONObject(i)

                val title = item.getString("title")
                val author = item.getString("author")
                val authorId = item.getString("author_id")
                val tags = item.getString("tags")

                val jsonMedia = item.getJSONObject("media")
                val photoUrl = jsonMedia.getString("m")
                val link = photoUrl.replaceFirst("_m.jpg", "_b.jpg")

                val photo = Photo(title, author, authorId, link, tags, photoUrl)
                photoList.add(photo)
                Log.d(TAG, ".doInBackground $photo")
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Log.e(TAG, ".doInBackground: Error processing JSON data")
            cancel(true)
            listener.onError(e)
        }

        Log.d(TAG, ".doInBackground ends")
        return photoList
    }

    override fun onPostExecute(result: ArrayList<Photo>) {
        Log.d(TAG, "onPostExecute starts")
        listener.onDataAvailable(result)
    }
}