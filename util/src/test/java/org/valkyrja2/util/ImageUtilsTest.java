/*
 * PROJECT valkyrja2
 * util/ImageUtilsTest.java
 * Copyright (c) 2022 Tequila.Yang
 */

package org.valkyrja2.util;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

class ImageUtilsTest {

    private static final Logger log = LoggerFactory.getLogger(ImageUtilsTest.class);

    private String imgBase64_Origin = "iVBORw0KGgoAAAANSUhEUgAAAZAAAAGQCAIAAAAP3aGbAAAABGdBTUEAALGPC/xhBQAAACBjSFJNAAB6JgAAgIQAAPoAAACA6AAAdTAAAOpgAAA6mAAAF3CculE8AAAABmJLR0QA/wD/AP+gvaeTAAAACXBIWXMAABcRAAAXEQHKJvM/AAAosElEQVR42u3deXAc150f8N97PTdmcBMkQQIgCYoUSfEAaZm6L8r2StrI9h6ON9kkFW/Vbin2VlK1MSuu/cP6I38ozh+pSqKNU7XZWid2NrIcH+vYa1uy7sMmJRLUwZsEAR64z8Hc3e/lj57p6RkMBphBT6Mf9f1UWQYH0z1veqa/eP3616+ZlJIAAFTA17sBAACrhcACAGUgsABAGQgsAFAGAgsAlIHAAgBlILAAQBkILABQBgILAJSBwAIAZSCwAEAZCCwAUAYCCwCUgcACAGX4ano2Y2y9G1y/KhPpVHlf9S3lZjPq4/237OYK63utKhx/X46/lkfU+pbRwwIAZSCwAEAZCCwAUAYCCwCUgcACAGUgsABAGQgsAFAGAgsAlMFqKtxys7KxDgMDA4ODg8620CMFlvW1sL7XUrogtgo3Cyw/gZt3Oc7ulehhAYAyEFgAoAwEFgAoA4EFAMpAYAGAMhBYAKAMBBYAKAOBBQDKqG3G0fp4ZBJIj7yW4xWAStevulmX67j6NpRH3peieyV6WACgDAQWA";
    private String imgBase64 = "data:image/png;base64," + imgBase64_Origin;

    @Test
    void testExtractImageType() {
        String ext = ImageUtils.extractImageType(imgBase64, ImageUtils.IMAGE_TYPE_PNG);
        log.info("testExtractImageType, {}", ext);
        assertEquals("png", ext);
    }

    @Test
    void testExtractImage() {
        String img = ImageUtils.extractImage(imgBase64);
        log.info("testExtractImage, {}", img);
        assertEquals(imgBase64_Origin, img);

        img = ImageUtils.extractImage(imgBase64_Origin);
        log.info("testExtractImage, {}", img);
        assertEquals(imgBase64_Origin, img);
    }

    @Test
    void testExtractImageBytes() {
        byte[] img = ImageUtils.extractImageBytes(imgBase64);
        log.info("testExtractImage, {}", img);
        assertArrayEquals(Base64.decodeBase64(imgBase64_Origin), img);
    }

    @Test
    void testDrawVerifyCodeImage() throws IOException {
        byte[] img = ImageUtils.drawVerifyCodeImage("A123456", 200, 50);
        FileUtils.writeByteArrayToFile(new File("/Users/Tequila/Downloads/imgvc.png"), img);
        ThreadUtils.sleep(500);
    }
}