package com.shoppingCartBackend.shoppingCartBackend.service.image;

import com.shoppingCartBackend.shoppingCartBackend.dto.ImageDto;
import com.shoppingCartBackend.shoppingCartBackend.model.Image;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface IImageService {
    Image getImageById(Long id);
    void deleteImageById(Long id);
    List<ImageDto> saveImages(List<MultipartFile> files, Long productId);
    void updateImage(MultipartFile file, Long productId);


}
