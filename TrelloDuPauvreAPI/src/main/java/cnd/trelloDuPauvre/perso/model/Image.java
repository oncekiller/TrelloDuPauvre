package cnd.trelloDuPauvre.perso.model;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;


import javax.imageio.ImageIO;
import javax.persistence.*;

import org.hibernate.annotations.Type;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "image")
public class Image{

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int imageId;

    @Column(name = "file_name")
    private String fileName;

    @Column(name = "file_type")
    private String fileType;

    @Column(name = "size")
    private long size;

    @Lob
    @Column(name = "data")
    @Type(type = "org.hibernate.type.BinaryType")
    private byte[] data;

    public Image(String fileName, String fileType, long size, byte[] data) {
        this.fileName = fileName;
        this.fileType = fileType;
        this.size = size;
        this.data = data;
    }

    /**
     * Create new Image class.
     *
     * @return new Image.
     */

    public void setFiles(MultipartFile file) {
        setFileType(file.getContentType());
        setSize(file.getSize());
    }

    /**
     * Scale image data with given width and height.
     *
     * @param width  scale width
     * @param height scale height
     * @return scaled image byte array and change to class data.
     */
    public byte[] scale(int width, int height) throws Exception {

        if (width == 0 || height == 0)
            return data;

        ByteArrayInputStream in = new ByteArrayInputStream(data);

        try {
            BufferedImage img = ImageIO.read(in);

            java.awt.Image scaledImage = img.getScaledInstance(width, height, java.awt.Image.SCALE_SMOOTH);
            BufferedImage imgBuff = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
            imgBuff.getGraphics().drawImage(scaledImage, 0, 0, new Color(0, 0, 0), null);
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();

            ImageIO.write(imgBuff, "jpg", buffer);
            setData(buffer.toByteArray());
            return buffer.toByteArray();

        } catch (Exception e) {
            throw new Exception("IOException in scale");
        }
    }

    /**
     * @param fileName - filename of the resources.
     *
     * @return inputstream for image
     * */
    private static InputStream getResourceFileAsInputStream(String fileName) {
        ClassLoader classLoader = Image.class.getClassLoader();
        return classLoader.getResourceAsStream(fileName);
    }



    public static Image buildImage(MultipartFile file) {
        String fileName = generateDisplayName(file.getOriginalFilename());

        Image image = new Image();
        image.setFileName(fileName);
        image.setFiles(file);

        try {
            image.setData(file.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return image;
    }

    public static String generateUniqueNumber() {
        int min = 10000;
        int max = 99999;
        int random_int = (int) (Math.random() * (max - min + 1) + min);
        return "" + random_int;
    }

    public static String generateFileName(String fileName) {

        // generate random alphabet
        String shortRandomAlphabet = generateUniqueNumber();

        // create date format as string
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("HHmmss_ddMMyyyy");
        String dateStrFormat = dtf.format(LocalDateTime.now());

        // find extension of file
        int indexOfExtension = fileName.indexOf(".");
        String extensionName = fileName.substring(indexOfExtension);

        // return new file name
        return dateStrFormat + "_" + shortRandomAlphabet + extensionName;

    }

    public static String generateDisplayName(String orgFileName) {
        String orgCleanPath = StringUtils.cleanPath(orgFileName);

        // generate new file name
        return generateFileName(orgCleanPath);
    }

}