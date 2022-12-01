package kr.co.hconnect.service;

import egovframework.rte.fdl.cmmn.EgovAbstractServiceImpl;
import egovframework.rte.fdl.cmmn.exception.FdlException;
import egovframework.rte.fdl.idgnr.EgovIdGnrService;
import kr.co.hconnect.vo.*;
import kr.co.hconnect.repository.TeleHealthDao;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import com.opentok.*;
import com.opentok.exception.OpenTokException;

/**
 * 화상상담 서비스
 */
@Service
@Transactional(rollbackFor = Exception.class, readOnly = true)
public class TeleHealthService extends EgovAbstractServiceImpl{

    private int apikey = 47595911;
    private String apiSecret = "2ddde1eb92a2528bd22be0c465174636daca363d";

    private TeleHealthDao teleHealthDao;


    @Autowired
    public TeleHealthService(TeleHealthDao teleHealthDao) {
        this.teleHealthDao = teleHealthDao;
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

        //세션 id 가 없는 경우
        if (StringUtils.isEmpty(sessionId)) {
            OpenTok openTok = null;
            try {
                vo.setApiKey(apikey);
                vo.setApiSecret(apiSecret);

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

                //# 화상상담 시작정보 저장
                teleVO.setLoginId(vo.getLoginId());
                teleVO.setSessionId(ssid);
                teleVO.setOfficerToken(ofToken);
                teleVO.setAdmissionId(vo.getAdmissionId());



                int rtn = teleHealthDao.insertSession(teleVO);

                //# 참석자(대상자 & 보호자)에게 푸시내역 생성
                //createTelehealthStartPush(teleEntity);


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
}
