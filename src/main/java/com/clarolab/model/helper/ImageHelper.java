/*
 * Copyright (c) 2019, Clarolab. All rights reserved.
 */

package com.clarolab.model.helper;

import com.clarolab.util.Constants;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;
import javax.validation.constraints.NotNull;
import javax.xml.bind.DatatypeConverter;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Iterator;
import java.util.Properties;

import static com.clarolab.util.Constants.DATA_IMAGE_TYPE;

public final class ImageHelper {


    private static char[] HEXCHARS = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};


    private ImageHelper() {
    }

    //data:image/jpeg;base64,/
    public static String compressBase64Image(@NotNull String source) {
        String[] strings = source.split(",");
        String ext = strings[0];

        if (isAnImage(ext)){
            byte[] data = DatatypeConverter.parseBase64Binary(strings[1]);
            return Constants.DATA_IMAGE_PREFIX + DatatypeConverter.printBase64Binary(compressImage(data));
        }
        return "";
    }

    private static boolean isAnImage(String ext) {
        return ext.contains("jpeg") || ext.contains("jpg") || ext.contains("png") || ext.contains("gif");
    }

    public static byte[] compressImage(byte[] source) {
        byte[] result = {};

        //  InputStream in = new ByteArrayInputStream(source);
        BufferedImage image = null;
        try {
            image = ImageHelper.readImage(source);

            OutputStream os = new ByteArrayOutputStream();

            Iterator<ImageWriter> writers = ImageIO.getImageWritersByFormatName(DATA_IMAGE_TYPE);
            ImageWriter writer = writers.next();

            ImageOutputStream ios = null;

            ios = ImageIO.createImageOutputStream(os);

            writer.setOutput(ios);

            ImageWriteParam param = writer.getDefaultWriteParam();

            param.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
            //param.setCompressionQuality(DATA_IMAGE_QUALITY);  // Change the quality value you prefer

            writer.write(null, new IIOImage(image, null, null), param);

            result = ((ByteArrayOutputStream) os).toByteArray();

            os.close();
            ios.close();
            writer.dispose();

        } catch (Exception e) {
            e.printStackTrace();
            return result;
        }

        return result;
    }


    private static byte[] readStream(InputStream in) throws IOException {
        byte[] buffer = new byte[1024];
        byte[] resource = new byte[0];
        int n;

        while ((n = in.read(buffer)) != -1) {
            byte[] b = new byte[resource.length + n];
            System.arraycopy(resource, 0, b, 0, resource.length);
            System.arraycopy(buffer, 0, b, resource.length, n);
            resource = b;
        }
        return resource;
    }


    public static byte[] getBytesFromResource(String file) {

        InputStream in = ImageHelper.class.getResourceAsStream(file);

        if (in == null) {
            return null;
        } else {
            try {
                return ImageHelper.readStream(in);
            } catch (IOException e) {
                return new byte[0];
            } finally {
                try {
                    in.close();
                } catch (IOException e) {
                }
            }
        }
    }

    public static BufferedImage readImageFromResource(String file) {
        return readImage(getBytesFromResource(file));
    }

    public static BufferedImage readImage(String url) {
        try {
            return readImage(new URL(url));
        } catch (MalformedURLException e) {
            return null;
        }
    }

    public static BufferedImage readImage(URL url) {

        InputStream in = null;

        try {
            URLConnection urlConnection = url.openConnection();
            in = urlConnection.getInputStream();
            return readImage(readStream(in));
        } catch (IOException e) {
            return null;
        } finally {
            try {
                if (in != null) {
                    in.close();
                }
            } catch (IOException e) {
            }
        }
    }

    public static BufferedImage readImage(byte[] b) {
        if (b == null) {
            return null;
        } else {
            try {
                return ImageIO.read(new ByteArrayInputStream(b));
            } catch (IOException e) {
                return null;
            }
        }
    }

    public static byte[] writeImage(BufferedImage img) {
        if (img == null) {
            return null;
        } else {
            try {
                ByteArrayOutputStream b = new ByteArrayOutputStream();
                ImageIO.write(img, "png", b);
                b.flush();
                b.close();
                return b.toByteArray();
            } catch (IOException e) {
                return null;
            }
        }
    }

    public static Object readSerializable(byte[] b) {
        if (b == null) {
            return null;
        } else {
            try {
                ObjectInputStream in = new ObjectInputStream(new ByteArrayInputStream(b));
                Object obj = in.readObject();
                in.close();
                return obj;
            } catch (ClassNotFoundException eCNF) {
                //logger.error("Cannot create lists object", eCNF);
                return null;
            } catch (IOException eIO) {
                //logger.error("Cannot load lists file", eIO);
                return null;
            }
        }
    }

    public static byte[] writeSerializable(Object o) {

        if (o == null) {
            return null;
        } else {
            try {
                ByteArrayOutputStream b = new ByteArrayOutputStream();
                ObjectOutputStream out = new ObjectOutputStream(b);
                out.writeObject(o);
                out.flush();
                out.close();
                return b.toByteArray();
            } catch (IOException eIO) {
                eIO.printStackTrace();
                return null;
            }
        }
    }

    public static Properties readProperties(byte b[]) {
        Properties prop = new Properties();
        try {
            if (b != null) {
                prop.loadFromXML(new ByteArrayInputStream(b));
            }
        } catch (IOException e) {
        }
        return prop;
    }

    public static String bytes2hex(byte[] binput) {

        StringBuilder s = new StringBuilder(binput.length * 2);
        for (int i = 0; i < binput.length; i++) {
            byte b = binput[i];
            s.append(HEXCHARS[(b & 0xF0) >> 4]);
            s.append(HEXCHARS[b & 0x0F]);
        }
        return s.toString();
    }
}
