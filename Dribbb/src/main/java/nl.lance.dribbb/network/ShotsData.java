package nl.lance.dribbb.network;

import android.app.Activity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import nl.lance.dribbb.adapter.CommentsAdapter;
import nl.lance.dribbb.adapter.ContentShotsAdapter;
import nl.lance.dribbb.views.FooterState;

public class ShotsData {

  private RequestQueue mRequestQueue;
  List<Map<String, Object>> list;
  List<Map<String, Object>> commentsList;
  private static int size;
  private static int commentsSize;
  private static int commentsPages;

  public ShotsData(Activity a) {

    mRequestQueue = Volley.newRequestQueue(a);
    list = new ArrayList<Map<String, Object>>();
    commentsList = new ArrayList<Map<String, Object>>();
  }

  public List<Map<String, Object>> getList() {


    return list;
  }

  public static int getSize() {
    return size;
  }

  public List<Map<String, Object>> getCommentsList() {
    return commentsList;
  }

  public static int getCommentsPages() {
    return commentsPages;
  }

  public void getShotsRefresh(final String url, final ContentShotsAdapter adapter, final FooterState f) {
    JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
            new Response.Listener<JSONObject>() {

              @Override
              public void onResponse(JSONObject arg0) {
                try {
                  initShotsList(arg0);
                  f.setState(FooterState.State.Idle);
                  adapter.notifyDataSetChanged();
                } catch (JSONException e) {
                  e.printStackTrace();
                }
              }
            }, new Response.ErrorListener() {

      @Override
      public void onErrorResponse(VolleyError arg0) {

      }
    }
    );
    mRequestQueue.add(jsonObjectRequest);
  }

  public void getCommentsRefresh(final String url, final CommentsAdapter adapter) {
    JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
            new Response.Listener<JSONObject>() {
              @Override
              public void onResponse(JSONObject jsonObject) {
                try {
                  commentsPages = jsonObject.getInt("pages");
                  initCommentsList(jsonObject);
                  adapter.notifyDataSetChanged();
                } catch (JSONException e) {
                  e.printStackTrace();
                }
              }
            }, new Response.ErrorListener() {

      @Override
      public void onErrorResponse(VolleyError volleyError) {

      }
    }
    );
    mRequestQueue.add(jsonObjectRequest);
  }

  private void initShotsList(JSONObject jsonObject) throws JSONException {
    int respond_count = jsonObject.getInt("per_page");
    int totalPages = jsonObject.getInt("page");
    JSONArray array = jsonObject.getJSONArray("shots");

    String tagsShots[] = DribbbleAPI.tagsShots;
    String tagsPlayer[] = DribbbleAPI.tagPlayer;

    for (int i = 0; i < respond_count; i++) {
      Map<String, Object> map = new HashMap<String, Object>();

      JSONObject shotsObject = array.getJSONObject(i);
      for(int j = 0; j < tagsShots.length; j++) {
        map.put(tagsShots[j], shotsObject.getString(tagsShots[j]));
      }

      JSONObject playerObject = array.getJSONObject(i).getJSONObject("player");
      for(int j = 0; j < tagsPlayer.length; j++) {
        map.put(tagsPlayer[j],playerObject.getString(tagsPlayer[j]));
      }
      getList().add(map);

      if (respond_count * (totalPages - 1) + i + 1 == size) {
        break;
      }
    }
    size = jsonObject.getInt("total");
  }

  private void initCommentsList(JSONObject jsonObject) throws JSONException {
    int respond_count = jsonObject.getInt("per_page");
    int curPage = jsonObject.getInt("page");
    JSONArray array = jsonObject.getJSONArray("comments");
    commentsSize = jsonObject.getInt("total");
    commentsPages = jsonObject.getInt("pages");
    for (int i = 0; i < respond_count; i++) {
      Map<String, Object> map = new HashMap<String, Object>();

      //comments
      map.put("body", array.getJSONObject(i).getString("body"));
      map.put("likes_count", array.getJSONObject(i).getString("likes_count"));
      map.put("created_at", array.getJSONObject(i).getString("created_at"));

      //player
      map.put("name", array.getJSONObject(i).getJSONObject("player").getString("name"));
      map.put("username",array.getJSONObject(i).getJSONObject("player").getString("username") );
      map.put("avatar_url", array.getJSONObject(i).getJSONObject("player").getString("avatar_url"));
      getCommentsList().add(map);

      if (respond_count * (curPage - 1) + i + 1 == commentsSize) {
        break;
      }
    }
  }
}
