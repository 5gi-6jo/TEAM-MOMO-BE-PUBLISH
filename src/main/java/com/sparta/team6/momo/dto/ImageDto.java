package com.sparta.team6.momo.dto;

import com.sparta.team6.momo.model.Image;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ImageDto {

    private Long imageId;
    private String image;

    public ImageDto(Long imageId, String image) {
        this.imageId = imageId;
        this.image = image;
    }

    public ImageDto(Image image) {
        this.imageId = image.getId();
        this.image = image.getImage();
    }
}
