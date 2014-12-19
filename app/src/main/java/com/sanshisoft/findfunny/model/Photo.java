package com.sanshisoft.findfunny.model;

/**
 * Created by chenleicpp on 2014/9/18.
 */
public class Photo {
    private String id;
    private String abs;
    private String imageUrl;
    private String thumbnailUrl;
    private int thumbnailWidth;
    private int thumbnailHeight;
    private int thumbLargeWidth;
    private int thumbLargeHeight;

	public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAbs() {
        return abs;
    }

    public void setAbs(String abs) {
        this.abs = abs;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getThumbnailUrl() {
        return thumbnailUrl;
    }

    public void setThumbnailUrl(String thumbnailUrl) {
        this.thumbnailUrl = thumbnailUrl;
    }

	public int getThumbnailWidth() {
		return 200;
	}

	public void setThumbnailWidth(int thumbnailWidth) {
		this.thumbnailWidth = thumbnailWidth;
	}

	public int getThumbnailHeight() {
		return thumbnailHeight;
	}

	public void setThumbnailHeight(int thumbnailHeight) {
		this.thumbnailHeight = thumbnailHeight;
	}

	public int getThumbLargeWidth() {
		return thumbLargeWidth;
	}

	public void setThumbLargeWidth(int thumbLargeWidth) {
		this.thumbLargeWidth = thumbLargeWidth;
	}

	public int getThumbLargeHeight() {
		return thumbLargeHeight;
	}

	public void setThumbLargeHeight(int thumbLargeHeight) {
		this.thumbLargeHeight = thumbLargeHeight;
	}
    
}
