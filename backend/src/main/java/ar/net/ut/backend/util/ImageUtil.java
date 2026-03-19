package ar.net.ut.backend.util;

import lombok.experimental.UtilityClass;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

@UtilityClass
public final class ImageUtil {

    public BufferedImage resize(BufferedImage image, int newWidth, int newHeight, int imageType) {
        BufferedImage resizedImage = new BufferedImage(newWidth, newHeight, imageType);
        Graphics2D graphics = resizedImage.createGraphics();

        graphics.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
        graphics.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        graphics.drawImage(image, 0, 0, newWidth, newHeight, null);
        graphics.dispose();

        return resizedImage;
    }

    public MultipartFile resize(MultipartFile file, int newWidth, int newHeight) throws IOException {
        String extension = StringUtils.getFilenameExtension(file.getOriginalFilename());
        if (!extension.equalsIgnoreCase("png") && !extension.equalsIgnoreCase("jpg")) {
            throw new IllegalArgumentException("Profile picture must be PNG or JPG");
        }

        BufferedImage originalImage;
        try (InputStream inputStream = file.getInputStream()) {
            originalImage = ImageIO.read(inputStream);
            if (originalImage == null) {
                throw new IllegalArgumentException("Invalid or corrupted image");
            }
        }

        BufferedImage resizedImage = resize(
                originalImage,
                newWidth,
                newHeight,
                extension.equalsIgnoreCase("png") ? BufferedImage.TYPE_INT_ARGB : BufferedImage.TYPE_INT_RGB
        );

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ImageIO.write(resizedImage, extension, outputStream);

        return new MockMultipartFile(
                file.getName(),
                file.getOriginalFilename(),
                file.getContentType(),
                outputStream.toByteArray()
        );
    }
}

