package kr.co.hconnect.service;

import com.opentok.Archive;
import egovframework.rte.fdl.cmmn.EgovAbstractServiceImpl;
import egovframework.rte.fdl.cmmn.exception.FdlException;
import kr.co.hconnect.vo.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import kr.co.hconnect.repository.*;
import org.springframework.util.ResourceUtils;


import java.io.*;
import java.net.URL;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import com.opentok.*;
import com.opentok.exception.OpenTokException;

@Service
public class BatchService extends EgovAbstractServiceImpl{

    private  final  AiInferenceDao aiInferenceDao;


    private int apikey = 47595911;
    private String apiSecret = "2ddde1eb92a2528bd22be0c465174636daca363d";


    private String orgPath ="E:\\projects\\snuh_smile\\web-app\\src\\main\\resources\\inference\\score\\";
    @Autowired
    public BatchService(AiInferenceDao dao) {
        this.aiInferenceDao = dao;
    }


    @Transactional(rollbackFor = Exception.class)
    public void score() throws IOException, InterruptedException {
        //create

        String rtnIns = scoreInsert();

        // AiInferenceVO entity = new AiInferenceVO();
        // entity.setInfDiv("10");
        //
        // String filePath = "./resources/inference/score/score.csv";
        // int rtnScore = csvCreate(filePath);
        // if (rtnScore != 0) {
        //     if ( pythonProcessbuilder() ){       //파일썬 실행
        //
        //         dao.insInf_log(entity);   //히스토리
        //
        //         dao.delInf(entity);       //scoreDelete
        //
        //         String rtnIns = scoreInsert();
        //
        //     }
        // }


    }



    @Transactional(rollbackFor = Exception.class)
    public void temperature() throws FdlException {
        //create
        String filePath = "./resources/inference/temperature/temp.csv";
        int rtnScore = csvCreate(filePath);
        if (rtnScore != 0) {

        }
        //excute
        //scorehist
        //scoreDelete
        //scoreinsert
    }
    @Transactional(rollbackFor = Exception.class)
    public void depress() throws FdlException {
        //create
        String filePath = "./resources/inference/depressed/depressed.csv";      //우울
        int rtnScore = csvCreate(filePath);
        if (rtnScore != 0) {

        }
        //excute
        //scorehist
        //scoreDelete
        //scoreinsert
    }

    public int csvCreate( String filePath) {
        int resultCount = 0;

        List list= null;
        //데이터를 받아오고 파일로 쓰기
        try {
            //csv 파일의 기존 값에 이어쓰려면 위처럼 tru를 지정하고 기존갑을 덮어 쓰려면 true를 삭제한다
            BufferedWriter fw = new BufferedWriter(new FileWriter(filePath));

            //쿼리 를 한다.
            //
            List<ScoreVO> dataList = aiInferenceDao.scoreList();

            if (dataList.size() > 0 ) {
                for (ScoreVO dt : dataList) {
                    String aData = "";
                    aData = dt.getAdmissionId();   //환자 id
                    aData += "," + dt.getAge();    // 나이
                    aData += "," + dt.getPr();     //심박수
                    aData += "," + dt.getSpo2();   // 산소포화도
                    aData += "," + dt.getSbp();    //수축기 혈압
                    aData += "," + dt.getDbp();    //이완기 혈압
                    aData += "," + dt.getBt();     //체온
                    aData += "," + dt.getAche();   //통증

                    fw.write(aData);
                    fw.newLine();
                }
            }
            fw.flush();
            //객체 닫기
            fw.close();

        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        } finally {
            return resultCount;
        }

    }

    /**
     * 파이썬 소스 파일 실행하기
     * @return
     */
    public Boolean pythonProcessbuilder() throws IOException, InterruptedException {
        System.out.println("pythonbuilder ");
        Boolean bool = true;
        String arg1;
        ProcessBuilder builder;
        BufferedReader br;

        arg1 = "./resources/inference/score/score.py";
        builder = new ProcessBuilder("python",arg1); //python3 error

        builder.redirectErrorStream(true);
        Process process = builder.start();

        // 자식 프로세스가 종료될 때까지 기다림
        int exitval = process.waitFor();

        //// 서브 프로세스가 출력하는 내용을 받기 위해
        br = new BufferedReader(new InputStreamReader(process.getInputStream(),"UTF-8"));

        String line;
        while ((line = br.readLine()) != null) {
            System.out.println(">>>  " + line); // 표준출력에 쓴다
        }

        if(exitval !=0){
            //비정상종료
            System.out.println("비정상종료");
            bool=false;
        }

        return bool;
    }


    public String scoreInsert() throws IOException, InterruptedException {

        String rtn = "0";
        //csv file open

        String filePath = orgPath + "result_score.csv";
        File csv = new File(filePath);

        //File csv = ResourceUtils.getFile('classpath:inference/score/result_score.csv');
        //File csv = ResourceUtils.getFile(ResourceUtils.CLASSPATH_URL_PREFIX + "inference/score/result_score.csv");

        BufferedReader br = null;
        String line = "";

        try{
            br = new BufferedReader(new FileReader(csv));
            while ((line = br.readLine()) != null){
                String[] lineArr = line.split(",");

                // AiInferenceVO entityVO = new AiInferenceVO();
                // entityVO.setAdmissionId(lineArr[0]);
                // entityVO.setInfDiv("10");
                // entityVO.setInfValue(Float.parseFloat(lineArr[1].toString()));
                //
                // dao.insInf(entityVO);  // 인서트

                System.out.println(lineArr[0]);
                System.out.println(lineArr[1]);
            }
        }catch (FileNotFoundException e){
            System.out.println(e.getMessage());
        }catch ( IOException e){
            System.out.println(e.getMessage());
        }finally {
            try{
                if(br != null){
                    br.close();

                    //파일 이동

                    // csv.getPath()
                    // Path filePath = Paths.get(File.separatorChar + "inference/score", File.separatorChar + "result_score.csv");
                    //
                    // Path filePathToMove = Paths.get(ResourceUtils.CLASSPATH_URL_PREFIX + "inference/score/backup/result_score.csv");
                    //

                    SimpleDateFormat format = new SimpleDateFormat ( "yyyyMMddHHmm");

                    String formatdate = format.format (System.currentTimeMillis());

                    System.out.println(formatdate);

                    Path fileRenamePath = Paths.get(orgPath+ "result_score_"+ formatdate + ".csv");

                    System.out.println(fileRenamePath);


                    Path filePathToMove = Paths.get(orgPath + "backup\\result_score.csv");
                    System.out.println(filePath);
                    System.out.println(filePathToMove);

                    //Files.move(filePath, filePathToMove);


                }
            }catch (IOException e) {
                e.printStackTrace();
            }
        }
        return rtn;

    }


    /**
     * 아카리브 파일 다운로드
     * @return
     */

    public void fileDownload() throws IOException, OpenTokException {

        String OUTPUT_FILE_PATH = "출력 파일 경로";
        String FILE_URL = "리소스 경로";
        String archiveId = "아카이브 파일 경로";

        Archive archive = null;
        try {

            OpenTok openTok = new OpenTok(apikey, apiSecret);
            archive =openTok.getArchive("archiveId");

            FILE_URL = archive.getUrl();

            InputStream in = new URL(FILE_URL).openStream();
            Path imagePath = Paths.get(OUTPUT_FILE_PATH);
            Files.copy(in, imagePath);

            // BufferedInputStream in = new BufferedInputStream(new URL(FILE_URL).openStream());
            // FileOutputStream fileOutputStream = new FileOutputStream(OUTPUT_FILE_PATH){
            //     byte dataBuffer[] = new byte[1024];
            //     int bytesRead;
            //     while ((bytesRead = in.read(dataBuffer, 0, 1024)) != -1) {
            //         fileOutputStream.write(dataBuffer, 0, bytesRead);
            //     }
            // }
        } catch ( OpenTokException e){

        } catch(IOException e){}

        // try(InputStream in = new URL(FILE_URL).openStream()){
        //     Path imagePath = Paths.get(OUTPUT_FILE_PATH);
        //     Files.copy(in, imagePath);
        // }


    }

    /**
     * 파이썬 파일 실행
     */

    public void excuteFile() {

        List<String> cmd = new ArrayList<String>();
        cmd.add("/bin/bash");
        cmd.add("-c");
        cmd.add("/pythonCode/test.py");

        StringBuilder sb = new StringBuilder(1024);
        String s = null;
        ProcessBuilder prsbld = null;
        Process prs = null;

        try {
            prsbld = new ProcessBuilder(cmd);
            // prsbld.directory(new File("/pythonCode")); // 디렉토리 이동
            // System.out.println("command: " + prsbld.command()); 	// 커맨드 확인

            // 프로세스 수행시작
            prs = prsbld.start();

            BufferedReader stdError = new BufferedReader(new InputStreamReader(prs.getErrorStream()));
            while ((s = stdError.readLine()) != null)
            {
                sb.append(s);
            }
            prs.getErrorStream().close();
            prs.getInputStream().close();
            prs.getOutputStream().close();

            // 종료까지 대기
            prs.waitFor();

        }catch (Exception e1) {
        }
        finally
        {
            if(prs != null)
                try {
                    prs.destroy();
                } catch(Exception e2) {
                }

        }

    }

}


