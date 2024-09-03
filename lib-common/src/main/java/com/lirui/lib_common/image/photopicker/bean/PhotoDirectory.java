package com.lirui.lib_common.image.photopicker.bean;

import android.text.TextUtils;

import com.lirui.lib_common.util.FileUtils;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * 图片文件夹
 */
public class PhotoDirectory {

    private String id;
    private String coverPath;
    private String name;
    private String imagePath;
    private long dateAdded;
    private List<Photo> photos = new ArrayList<>();
    private boolean isSelected;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCoverPath() {
        return coverPath;
    }

    public void setCoverPath(String coverPath) {
        this.coverPath = coverPath;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getDateAdded() {
        return dateAdded;
    }

    public void setDateAdded(long dateAdded) {
        this.dateAdded = dateAdded;
    }

    public List<Photo> getPhotos() {
        return photos;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public void setPhotos(List<Photo> photos) {
        if (photos == null) return;
        Iterator<Photo> iterator = photos.iterator();
        while (iterator.hasNext()) {
            Photo photo = iterator.next();
            if (photo == null || !FileUtils.isFileExists(photo.getPath())) {
                iterator.remove();
            }
        }
        this.photos = photos;
    }

    public List<String> getPhotoPaths() {
        List<String> paths = new ArrayList<>(photos.size());
        for (Photo photo : photos) {
            paths.add(photo.getPath());
        }
        return paths;
    }

    public void addPhoto(int id, String path) {
        if (FileUtils.isFileExists(path)) {
            photos.add(new Photo(id, path));
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PhotoDirectory)) return false;

        PhotoDirectory directory = (PhotoDirectory) o;

        boolean hasId = !TextUtils.isEmpty(id);
        boolean otherHasId = !TextUtils.isEmpty(directory.id);

        if (hasId && otherHasId) {
            if (!TextUtils.equals(id, directory.id)) {
                return false;
            }

            return TextUtils.equals(name, directory.name);
        }

        return false;
    }

    @Override
    public int hashCode() {
        if (TextUtils.isEmpty(id)) {
            if (TextUtils.isEmpty(name)) {
                return 0;
            }

            return name.hashCode();
        }

        int result = id.hashCode();

        if (TextUtils.isEmpty(name)) {
            return result;
        }

        result = 31 * result + name.hashCode();
        return result;
    }

    /**
     * 获取选中的Photo
     */
    public ArrayList<Photo> getSelectedPhotos() {
        ArrayList<Photo> selectedPhotos = new ArrayList<>();
        for (Photo photo : photos) {
            if (photo.isSelected()) {
                selectedPhotos.add(photo);
            }
        }
        return selectedPhotos;
    }

    /**
     * 获取选中的Photo size
     */
    public int getSelectedPhotosSize() {
        ArrayList<Photo> selectedPhotos = new ArrayList<>();
        for (Photo photo : photos) {
            if (photo.isSelected()) {
                selectedPhotos.add(photo);
            }
        }
        return selectedPhotos.size();
    }
}
