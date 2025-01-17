package cn.keking.service;

import net.sf.sevenzipjbinding.ExtractOperationResult;
import net.sf.sevenzipjbinding.IInArchive;
import net.sf.sevenzipjbinding.SevenZip;
import net.sf.sevenzipjbinding.SevenZipException;
import net.sf.sevenzipjbinding.impl.RandomAccessFileInStream;
import net.sf.sevenzipjbinding.simple.ISimpleInArchive;
import net.sf.sevenzipjbinding.simple.ISimpleInArchiveItem;
import org.apache.commons.io.IOUtils;
import org.springframework.stereotype.Component;

import java.io.*;
import java.nio.charset.Charset;
import java.util.Arrays;

/**
 * @author yudian-it create 2017/11/27
 */
@Component
public class CompressFileReader {

    public String getUtf8String(String str) {
        if (str != null && str.length() > 0) {
            String needEncodeCode = "ISO-8859-1";
            try {
                if (Charset.forName(needEncodeCode).newEncoder().canEncode(str)) {//这个方法是关键，可以判断乱码字符串是否为指定的编码
                    str = new String(str.getBytes(needEncodeCode), "UTF-8");
                }
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
        return str;
    }
    public String un7z(String file7zPath, final String outPutPath){
        RandomAccessFile randomAccessFile = null;
        IInArchive archive = null;
        String panduan = outPutPath;
        try {
            randomAccessFile = new RandomAccessFile(file7zPath, "r");
            archive = SevenZip.openInArchive(null, new RandomAccessFileInStream(randomAccessFile));
            ISimpleInArchive   simpleInArchive = archive.getSimpleInterface();
            for (final ISimpleInArchiveItem item : simpleInArchive.getArchiveItems()) {
                final int[] hash = new int[] { 0 };
                if (!item.isFolder()) {
                    ExtractOperationResult result;
                    final long[] sizeArray = new long[1];
                    result = item.extractSlow(data -> {
                        try {
                            String str = getUtf8String(item.getPath());
                            str = str.replace("\\", File.separator); //Linux 下路径错误
                            String  str1 = str.substring(0, str.lastIndexOf(File.separator)+ 1);
                            File file = new File(outPutPath + File.separator + str1);
                            if (!file.exists()) {
                                file.mkdirs();
                            }
                            OutputStream out = new FileOutputStream( outPutPath + File.separator + str, true);
                            IOUtils.write(data, out);
                            out.close();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        hash[0] ^= Arrays.hashCode(data); // Consume data
                        sizeArray[0] += data.length;
                        return data.length; // Return amount of consumed
                    });
                    if (result == ExtractOperationResult.OK) {
                        // System.out.println("解压成功...." + String.format("%9X | %10s | %s", hash[0], sizeArray[0], getUtf8String(item.getPath())));
                    } else {
                        System.out.println("解压失败：密码错误或者其他错误...." + result);
                        panduan ="null";
                    }
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (SevenZipException e) {
            e.printStackTrace();
        } finally {
            if (randomAccessFile != null) {
                try {
                    randomAccessFile.close();
                } catch (IOException e) {
                    System.err.println("Error closing file: " + e);
                }
            }
            if (archive != null) {
                try {
                    archive.close();
                } catch (SevenZipException e) {
                    System.err.println("Error closing archive: " + e);
                }
            }
        }
        return panduan;
    }
}