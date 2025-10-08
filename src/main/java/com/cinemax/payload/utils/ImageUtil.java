package com.cinemax.payload.utils;

import com.cinemax.exception.ImageException;

import java.io.ByteArrayOutputStream;
import java.io.IOError;
import java.io.IOException;
import java.util.zip.Deflater;
import java.util.zip.Inflater;

public class ImageUtil {


    public static byte[] compressImage(byte[] data) {
        // Byte dizisini sıkıştırmak için ByteArrayOutputStream oluşturuyoruz.
        // try-with-resources kullanıldığı için otomatik kapanacak (close).
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream(data.length)) {

            // Deflater nesnesi, Java'da veriyi sıkıştırmak için kullanılır.
            Deflater deflater = new Deflater();

            // Maksimum sıkıştırma seviyesini ayarlıyoruz.
            // BEST_COMPRESSION → en küçük boyut, fakat işlem daha yavaştır.
            deflater.setLevel(Deflater.BEST_COMPRESSION);

            // Sıkıştırılacak ham veriyi Deflater'a veriyoruz.
            deflater.setInput(data);

            // Sıkıştırmanın tamamlandığını bildiriyoruz.
            deflater.finish();

            // Sıkıştırılmış veriyi geçici olarak alacağımız buffer (4 KB)
            byte[] tmp = new byte[4 * 1024];

            // Deflater bitene kadar döngü ile veriyi buffer üzerinden oku ve outputStream'e yaz
            while (!deflater.finished()) {
                // Sıkıştırılmış veriyi tmp buffer'a yaz ve gerçek boyutu size ile al
                int size = deflater.deflate(tmp);

                // Buffer'daki veriyi outputStream'e ekle
                outputStream.write(tmp, 0, size);
            }

            // Deflater kaynaklarını serbest bırak
            deflater.end();

            // Sıkıştırılmış veriyi byte dizisi olarak döndür
            return outputStream.toByteArray();

        } catch (IOException e) {
            // Eğer I/O hatası oluşursa özel bir ImageException fırlat
            throw new ImageException("Cannot compress image");
        }
    }

    public static byte[] decompressImage(byte[] data) {
        // Byte dizisini açmak (decompress) için ByteArrayOutputStream oluşturuyoruz.
        // try-with-resources kullanıldığı için otomatik olarak kapanacak (close).
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream(data.length)) {

            // Inflater nesnesi, Java'da sıkıştırılmış veriyi açmak için kullanılır.
            Inflater inflater = new Inflater();

            // Açılacak (decompressed) veriyi inflater'a veriyoruz.
            inflater.setInput(data);

            // Açılmış veriyi geçici olarak alacağımız buffer (4 KB)
            byte[] tmp = new byte[4 * 1024];

            // Inflater bitene kadar döngü ile veriyi buffer üzerinden oku ve outputStream'e yaz
            while (!inflater.finished()) {
                // Buffer'a açılmış veriyi yaz ve geçerli byte sayısını count ile al
                int count = inflater.inflate(tmp);

                // Buffer'daki veriyi outputStream'e ekle
                outputStream.write(tmp, 0, count);
            }

            // Inflater kaynaklarını serbest bırak
            inflater.end();

            // Açılmış (decompressed) veriyi byte dizisi olarak döndür
            return outputStream.toByteArray();

        } catch (IOException | java.util.zip.DataFormatException e) {
            // Eğer I/O hatası veya veri format hatası oluşursa özel ImageException fırlat
            throw new ImageException("Cannot decompress image");
        }
    }








}
