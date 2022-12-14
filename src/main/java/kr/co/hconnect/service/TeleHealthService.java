package kr.co.hconnect.service;

import egovframework.rte.fdl.cmmn.EgovAbstractServiceImpl;
import kr.co.hconnect.vo.*;
import kr.co.hconnect.repository.TeleHealthDao;
import kr.co.hconnect.repository.UserDao;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import com.opentok.*;
import com.opentok.exception.OpenTokException;

import kr.co.hconnect.common.HttpUtil;



/**
 * 화상상담 서비스
 */
@Service
@Transactional(rollbackFor = Exception.class, readOnly = true)
public class TeleHealthService extends EgovAbstractServiceImpl{

    private int apikey = 47595911;
    private String apiSecret = "2ddde1eb92a2528bd22be0c465174636daca363d";

    private final TeleHealthDao teleHealthDao;
    private final UserDao userDao;



    @Autowired
    public TeleHealthService(TeleHealthDao teleHealthDao, UserDao userDao) {
        this.teleHealthDao = teleHealthDao;
        this.userDao = userDao;
    }


    /**
     *화상상담
     * @param vo 측정항목VO
     */
    @Transactional(rollbackFor = Exception.class)
    public TeleHealthConnectVO selectConnection (TeleHealthConnectVO vo){

        TeleHealthConnectVO  teleVO = new TeleHealthConnectVO();
/*
        String format = "name=[%s]%s&clientType=web&serialNumber=%s&profileImgUrl=%s";
        String metaData = String.format(format
            , webUser.getManagementCtnm()
            , webUser.getManagementNm()
            , condition.getLoginUserSno()
            , FilePathConfig.getFilePathConfig(FilePathConfig.BASE_MOBILE_CONTENTS_URL) + webUser.ge
            tFileSaveNm());
*/
        String metaData = vo.getLoginId();


        TeleHealthConnectVO teleEntity = new TeleHealthConnectVO();
        teleEntity = teleHealthDao.selectTeleSession(vo);
        String sessionId = "";
        if (teleEntity != null){
            sessionId = teleEntity.getSessionId();
        }

        System.out.println("sessionId ===============================================");
        System.out.println("sessionId = ");
        System.out.println("===============================================");



        //세션 id 가 없는 경우
        if (StringUtils.isEmpty(sessionId)) {
            OpenTok openTok = null;
            try {
                vo.setApiKey(apikey);
                vo.setApiSecret(apiSecret);

                System.out.println("세션 생성");

                //# 세션 생성
                openTok = new OpenTok(vo.getApiKey(), vo.getApiSecret());
                SessionProperties sessionProperties = new SessionProperties.Builder()
                    .mediaMode(MediaMode.ROUTED)
                    .build();
                Session session = openTok.createSession(sessionProperties);
                String ssid  = session.getSessionId();

                //화상상담 개설자  토큰생성
                TokenOptions tokenOptions = new TokenOptions.Builder()
                    .role(Role.MODERATOR)
                    .data(metaData)
                    .build();

                String ofToken = openTok.generateToken(ssid, tokenOptions);


                System.out.println("화상상담 시작정보 저장");
                //# 화상상담 시작정보 저장
                teleVO.setLoginId(vo.getLoginId());
                teleVO.setSessionId(ssid);
                teleVO.setOfficerToken(ofToken);
                teleVO.setAdmissionId(vo.getAdmissionId());

                int rtn = teleHealthDao.insertSession(teleVO);


                System.out.println("참석자(대상자 & 보호자)에게 푸시내역 생성");
                //# 참석자(대상자 & 보호자)에게 푸시내역 생성
                createTelehealthStartPush(teleVO);


            } catch (OpenTokException e){
                System.out.println(e.getMessage());
            }
        }
        return teleVO;
    }

    /**
     * 화상상담 구독자 토큰 생성
     * @param vo
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public TeleHealthConnectVO getSubscriberToken (TeleHealthConnectVO vo){

        TeleHealthConnectVO  teleVO = new TeleHealthConnectVO();

        TeleHealthConnectVO teleEntity = new TeleHealthConnectVO();
        teleEntity = teleHealthDao.selectTeleSession(vo);
        String sessionId = "";
        if (teleEntity != null){
            sessionId = teleEntity.getSessionId();
        }

        OpenTok openTok = null;
        try {
            vo.setApiKey(apikey);
            vo.setApiSecret(apiSecret);

            openTok = new OpenTok(vo.getApiKey(), vo.getApiSecret());
            String attendeeToken = openTok.generateToken(sessionId);

            vo.setAttendeeToken(attendeeToken);

            //생성된 구독자 토큰 저장

            //
            teleVO.setLoginId(vo.getLoginId());
            teleVO.setApiKey(apikey);
            teleVO.setSessionId(sessionId);
            teleVO.setAttendeeToken(attendeeToken);
            teleVO.setAdmissionId(vo.getAdmissionId());

            int rtn = teleHealthDao.udpSubscriberToken(teleVO);


            //# 참석자(대상자 & 보호자)에게 푸시내역 생성
            //createTelehealthStartPush(teleEntity);


        } catch (OpenTokException e){
            System.out.println(e.getMessage());
        }
        return teleVO;
    }

    /**
     * 세션종료
     * @param vo
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public int endSession (TeleHealthSearchVO vo){
        int rtn = 0;
        try {
            rtn = teleHealthDao.endSession(vo);
        } catch (Exception e){
            System.out.println(e.getMessage());
        }
        return rtn;
    }
    public void createTelehealthStartPush(TeleHealthConnectVO vo){
        //텔레헬스
        String admissionId = vo.getAdmissionId();

        String CUID = userDao.selectPationtLoginId(admissionId);
        System.out.println(" CUID =====================================");
        System.out.println(admissionId);
        System.out.println(CUID);
        System.out.println(" CUID =====================================");

        //CUID = "smile01";
        //admissionId = "";

        String message = "화상진료를 시작합니다. 참여 부탁드립니다!";
        String sessionId  = vo.getSessionId();

        TeleHealthConnectVO  teleSsubVO = new TeleHealthConnectVO();

        OpenTok openTok = null;
        try {
            vo.setApiKey(apikey);
            vo.setApiSecret(apiSecret);

            openTok = new OpenTok(vo.getApiKey(), vo.getApiSecret());
            String attendeeToken = openTok.generateToken(sessionId);

            System.out.println("sessionId====================================================================");
            System.out.println(sessionId);
            System.out.println("sessionId====================================================================");

            System.out.println("attendeeToken====================================================================");
            System.out.println(attendeeToken);
            System.out.println("attendeeToken====================================================================");

            //생성된 구독자 토큰 저장

            //
            teleSsubVO.setLoginId(vo.getLoginId());
            teleSsubVO.setApiKey(apikey);
            teleSsubVO.setSessionId(sessionId);
            teleSsubVO.setAttendeeToken(attendeeToken);
            teleSsubVO.setAdmissionId(vo.getAdmissionId());

            int rtn = teleHealthDao.udpSubscriberToken(teleSsubVO);

            HashMap<String, Object> mapValue = new HashMap<String, Object>();
            mapValue.put("CUID", CUID);
            mapValue.put("MESSAGE", message);
            mapValue.put("apikey", apikey);
            mapValue.put("sessionId", vo.getSessionId());
            mapValue.put("attendeeToken", attendeeToken);

            //#푸시내역 생성
            int ret = sendPush(mapValue);

        } catch (OpenTokException e){
            System.out.println(e.getMessage());
        }



    }


    //푸시발송 테스트
    public int sendPush (HashMap<String, Object> mapValue){
        int rtn = 0;

        try {

            String cuid = mapValue.get("CUID").toString();
            String msg =  mapValue.get("MESSAGE").toString();

            String apikey =  mapValue.get("apikey").toString();
            String sessionID =  mapValue.get("sessionId").toString();
            String token =  mapValue.get("attendeeToken").toString();

            String message = "{\n" +
                "    \"title\": \" 안녕하세요 \",\n" +
                "    \"body\": \"" + msg + "\"\n" +
                "}";

            String attendeeToken = "{\n" +
                "    \"action\": \"doctor\",\n" +
                "    \"infomation\": {\n" +
                "        \"apikey\": \"" + apikey + "\",\n" +
                "        \"sessionId\": \""+ sessionID + "\",\n" +
                "        \"token\": \""+ token +"\"\n" +
                "    }\n" +
                "}";


            HashMap<String, Object> params = new HashMap<String , Object>();
            params.put("CUID", cuid);
            params.put("MESSAGE", message);
            params.put("PRIORITY", "3");
            params.put("BADGENO", "0");
            params.put("RESERVEDATE", "");
            params.put("SERVICECODE", "ALL");
            params.put("SOUNDFILE", "alert.aif");
            params.put("EXT", attendeeToken);
            params.put("SENDERCODE", "smile");
            params.put("APP_ID", "iitp.infection.pm");
            params.put("TYPE", "E");
            params.put("DB_IN", "Y");

            HashMap<String, Object> result = new HttpUtil()
                .url("http://192.168.42.193:8380/upmc/rcv_register_message.ctl")
                .method("POST")
                .body(params)
                .build();

        } catch (Exception e){
            rtn=1;
            System.out.println(e.getMessage());

        }

        return rtn;
    }


}
