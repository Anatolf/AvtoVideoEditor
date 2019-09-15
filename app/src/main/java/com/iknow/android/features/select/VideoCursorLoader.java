package com.iknow.android.features.select;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.MediaStore;

import androidx.fragment.app.FragmentActivity;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.CursorLoader;
import androidx.loader.content.Loader;

import iknow.android.utils.callback.SimpleCallback;

/**
 * author : J.Chou
 * e-mail : who_know_me@163.com
 * time   : 2018/10/04 1:51 PM
 * version: 1.0
 * description:
 */
public class VideoCursorLoader implements LoaderManager.LoaderCallbacks<Cursor>, ILoader {

  private Context mContext;
  private SimpleCallback mSimpleCallback;

  @Override public void load(final Context context, final SimpleCallback listener) {
    mContext = context;
    mSimpleCallback = listener;
    ((FragmentActivity)context).getSupportLoaderManager().initLoader(1, null, this);
  }

  @Override public Loader<Cursor> onCreateLoader(int id, Bundle args) {
    return new CursorLoader(
        mContext,
        MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
        PROJECTION,
        SELECTION,
        SELECTION_ARGS,
        ORDER_BY
    );
  }

  @Override public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
    if (mSimpleCallback != null && cursor != null) {
      mSimpleCallback.success(cursor);
    }
  }

  @Override public void onLoaderReset(Loader<Cursor> loader) {

  }
}
