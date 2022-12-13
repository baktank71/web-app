package kr.co.hconnect.service;
import com.opentok.Archive;
import com.opentok.OpenTok;
import com.opentok.exception.OpenTokException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import org.apache.commons.io.FileUtils;


import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

/**
 * 복약 서비스
 */
@Service
@Transactional(rollbackFor = Exception.class, readOnly = true)
public class TestService {



    public TestService() {
    }

    public void fileDownload_Url() throws IOException, OpenTokException {

        String OUTPUT_FILE_PATH = "E:\\score";
        String FILE_URL = "E:\\upload\\gizmo.mp4";
        String archiveId = "아카이브 파일 경로";

        Archive archive = null;
        try {
            System.out.println("파일 다운로드 시작");
            InputStream in = new URL("file:///" +FILE_URL).openStream();
            Path imagePath = Paths.get(OUTPUT_FILE_PATH);
            Files.copy(in, imagePath);
            System.out.println("파일 다운로드 종료");

        } catch(IOException e){
            System.out.println(e.getMessage());
        }

    }

    public void fileDownload2(String url, String fileName) throws IOException, OpenTokException , MalformedURLException {

        File f= new File((fileName));
        FileUtils.copyURLToFile(new URL(url), f);

    }

    public void fileDownload3(String url, String fileName) throws IOException, OpenTokException , MalformedURLException {

        File f= new File((fileName));
        FileUtils.copyURLToFile(new URL(url), f);


    }
    public int fileDownload() throws IOException, OpenTokException {

        int rtn = 0;

        String OUTPUT_FILE_PATH = "E:\\score";
        String FILE_URL = "E:\\upload\\gizmo.mp4";
        String archiveId = "아카이브 파일 경로";

        String filePath = "E:\\upload\\gizmo.mp4"; // 대상 파일
        FileInputStream inputStream = null; // 파일 스트림
        FileOutputStream outputStream = null;
        BufferedInputStream bufferedInputStream = null; // 버퍼 스트림
        BufferedOutputStream bufferedOutputStream = null;

        String TargetfilePath = "E:\\score\\gizmo.mp4"; // 대상 파일

        try {
            inputStream = new FileInputStream(filePath);// 파일 입력 스트림 생성
            bufferedInputStream = new BufferedInputStream(inputStream);// 파일 출력 스트림 생성

            outputStream = new FileOutputStream(TargetfilePath);
            bufferedOutputStream = new BufferedOutputStream(outputStream);

            // 파일 내용을 담을 버퍼(?) 선언
            byte[] readBuffer = new byte[1024];
            while (bufferedInputStream.read(readBuffer, 0, readBuffer.length) != -1)
            {
                //버퍼 크기만큼 읽을 때마다 출력 스트림에 써준다.
                bufferedOutputStream.write(readBuffer);
            }

        }
        catch (Exception e) {
            rtn = 1;
            System.out.println(e.getMessage());
        }
        return rtn;
    }

}
