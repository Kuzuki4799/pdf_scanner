package com.shockwave.pdf_scaner.model;

import android.content.Context;
import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;
import android.view.View;

import com.shockwave.pdf_scaner.util.FileUtils;

import java.util.ArrayList;

public class PhotoDirectory {
    public long id = 0;
    public String bucketId = "";
    public Uri coverPath;
    public String name = "";
    public long dateAdded = 0;

    public PhotoDirectory() {
    }

    public ArrayList<Media> medias = new ArrayList<>();

    public PhotoDirectory(String name, ArrayList<Media> medias) {
        this.name = name;
        this.medias = medias;
    }

    public Uri getCoverPath() {
        if (medias.size() > 0) {
            return medias.get(0).path;
        } else return coverPath;

    }

    public String getPath(View view) {
        return FileUtils.getPath(view.getContext(), medias.get(0).path);
    }

    public void setCoverPath(Uri coverPath) {
        this.coverPath = coverPath;
    }

    public void addPhoto(long imageId, String fileName, Uri path) {
        medias.add(new Media(imageId, fileName, path));
    }

    @Override
    public boolean equals(Object o) {
        PhotoDirectory that = (PhotoDirectory) o;
        return bucketId.equals(that.bucketId);
    }

    public static class Media implements Parcelable {
        public long id;
        public String name;
        public Uri path;
        public int pos = -1;
        public boolean isSelected = false;

        protected Media(Parcel in) {
            id = in.readLong();
            name = in.readString();
            path = in.readParcelable(Uri.class.getClassLoader());
            pos = in.readInt();
            isSelected = in.readByte() != 0;
        }

        public static final Creator<Media> CREATOR = new Creator<Media>() {
            @Override
            public Media createFromParcel(Parcel in) {
                return new Media(in);
            }

            @Override
            public Media[] newArray(int size) {
                return new Media[size];
            }
        };

        public String getPath(Context view) {
            return FileUtils.getPath(view, path);
        }

        public Media(long id, String name, Uri path) {
            this.id = id;
            this.name = name;
            this.path = path;
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeLong(id);
            dest.writeString(name);
            dest.writeParcelable(path, flags);
            dest.writeInt(pos);
            dest.writeByte((byte) (isSelected ? 1 : 0));
        }
    }
}
