package com.rabbiter.association.activity.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import java.util.List;

public class SubmitActivityCompletionRequest {
    @NotBlank(message = "活动总结不能为空")
    private String summary;

    @NotEmpty(message = "请至少上传一张活动结束照片")
    private List<String> images;

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public List<String> getImages() {
        return images;
    }

    public void setImages(List<String> images) {
        this.images = images;
    }
}
