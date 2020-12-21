package com.kyunggi.worker2;

<<<<<<< Updated upstream
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
=======
import android.graphics.*;
import android.util.*;
import com.google.zxing.*;
import com.google.zxing.client.j2se.*;
import com.google.zxing.common.*;
import com.google.zxing.qrcode.*;
import java.io.*;
import java.util.*;
>>>>>>> Stashed changes

import com.google.zxing.BarcodeFormat;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.ChecksumException;
import com.google.zxing.FormatException;
import com.google.zxing.LuminanceSource;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.NotFoundException;
import com.google.zxing.RGBLuminanceSource;
import com.google.zxing.Reader;
import com.google.zxing.Result;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.qrcode.QRCodeReader;


import java.io.File;
import java.io.IOException;
import java.util.Map;

public class QRCode
{

	private static String TAG="BTOPP QR";

    /**
     *
     * @param args
     * @throws WriterException
     * @throws IOException
     * @throws NotFoundException
     */
	/*public static void main(String[] args) throws WriterException, IOException,
	NotFoundException {
		String qrCodeData = "student3232_2015_12_15_10_29_46_123";
		String filePath = "F:\\Opulent_ProjectsDirectory_2015-2016\\.metadata\\.plugins\\org.eclipse.wst.server.core\\tmp0\\wtpwebapps\\AttendanceUsingQRCode\\QRCodes\\student3232_2015_12_15_10_29_46_123";
		String charset = "UTF-8"; // or "ISO-8859-1"
		Map<EncodeHintType, ErrorCorrectionLevel> hintMap = new HashMap<EncodeHintType, ErrorCorrectionLevel>();
		hintMap.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.L);

		createQRCode(qrCodeData, filePath, charset, hintMap, 200, 200);
		System.out.println("QR Code image created successfully!");

		System.out.println("Data read from QR Code: "
						   + readQRCode(filePath, charset, hintMap));

	}
*/

    /***
     *
     * @param qrCodeData
     * @param filePath
     * @param charset
     * @param hintMap
     * @param qrCodeheight
     * @param qrCodewidth
     * @throws WriterException
     * @throws IOException
     */
    public static void createQRCode(String qrCodeData, String filePath,
                                    String charset, Map hintMap, int qrCodeheight, int qrCodewidth)
            throws WriterException, IOException {
        BitMatrix matrix = new MultiFormatWriter().encode(
                new String(qrCodeData.getBytes(charset), charset),
                BarcodeFormat.QR_CODE, qrCodewidth, qrCodeheight);
        MatrixToImageWriter.writeToFile(matrix, filePath.substring(filePath
                .lastIndexOf('.') + 1), new File(filePath));
    }

    //	/**
//	 *
//	 * @param filePath
//	 * @param charset
//	 * @param hintMap
//	 *
//	 * @return Qr Code value
//	 *
//	 * @throws FileNotFoundException
//	 * @throws IOException
//	 * @throws NotFoundException
//	 */
	/*public static String readQRCode(String filePath, String charset, Map hintMap)
	throws FileNotFoundException, IOException, NotFoundException {
		BinaryBitmap binaryBitmap = new BinaryBitmap(new HybridBinarizer(
														 new BufferedImageLuminanceSource(
															 ImageIO.read(new FileInputStream(filePath)))));
		Result qrCodeResult = new MultiFormatReader().decode(binaryBitmap);
		return qrCodeResult.getText();
	}*/
<<<<<<< Updated upstream
    // Interesting method
    public static String decodeQRImage(String path) {
        Bitmap bMap = BitmapFactory.decodeFile(path);
        String decoded = null;
=======
	// Interesting method
	public static String decodeQRImage(String path) {
		path=path.trim();
		path=path.replaceAll("\r","");
		Log.v(TAG,path);
		File file=new File(path);
		if(!file.isFile())
		{
			throw new RuntimeException(path+"not file");
		}
		Bitmap bMap = BitmapFactory.decodeFile(path);
		String decoded = null;
>>>>>>> Stashed changes

        int[] intArray = new int[bMap.getWidth() * bMap.getHeight()];
        bMap.getPixels(intArray, 0, bMap.getWidth(), 0, 0, bMap.getWidth(),
                bMap.getHeight());
        LuminanceSource source = new RGBLuminanceSource(bMap.getWidth(),
                bMap.getHeight(), intArray);
        BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));

        Reader reader = new QRCodeReader();
        try {
            Result result = reader.decode(bitmap);
            decoded = result.getText();
        } catch (NotFoundException e) {
            e.printStackTrace();
        } catch (ChecksumException e) {
            e.printStackTrace();
        } catch (FormatException e) {
            e.printStackTrace();
        }
        return decoded;
    }
}
