package kr.co.hconnect.controller;

import kr.co.hconnect.common.ApiResponseCode;
import kr.co.hconnect.common.VoValidationGroups;
import kr.co.hconnect.exception.InvalidRequestArgumentException;
import kr.co.hconnect.jwt.TokenDetailInfo;
import kr.co.hconnect.vo.*;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import kr.co.hconnect.service.TeleHealthService;
import kr.co.hconnect.service.AdmissionService;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/teleHealth")
public class TeleHealthController {

    private static final Logger LOGGER = LoggerFactory.getLogger(TeleHealthController.class);

    private final MessageSource messageSource;

    private final TeleHealthService teleHealthService;


    private final AdmissionService admissionService;



    @Autowired
    public TeleHealthController(MessageSource messageSource, TeleHealthService teleHealthService, AdmissionService admissionService) {
        this.messageSource = messageSource;
        this.teleHealthService = teleHealthService;
        this.admissionService = admissionService;
    }

    public String getAdmissionId(String loginId) {
        return admissionService.selectAdmissionListByLoginId(loginId).getAdmissionId();
    }

    @RequestMapping(value = "/getTeleHealth", method = RequestMethod.POST)
    public ResponseBaseVO<TeleHealthConnectVO> selectConnection(@Validated(VoValidationGroups.add.class) @RequestBody TeleHealthConnectVO vo
        , BindingResult bindingResult, @RequestAttribute TokenDetailInfo tokenDetailInfo) {

        if (bindingResult.hasErrors()) {
            throw new InvalidRequestArgumentException(bindingResult);
        }
        ResponseBaseVO<TeleHealthConnectVO> responseVO = new ResponseBaseVO<>();
        try{
            vo.setLoginId(tokenDetailInfo.getId());

            String adid = vo.getAdmissionId();
            System.out.println("adid =======================================");
            System.out.println(adid);
            System.out.println("adid =======================================");
            if (StringUtils.isEmpty(adid)){
                vo.setAdmissionId("0000000076");  //????????? ?????????
            }
            TeleHealthConnectVO dt = teleHealthService.selectConnection(vo);
            responseVO.setCode(ApiResponseCode.SUCCESS.getCode());
            responseVO.setMessage("?????? ??????");
            responseVO.setResult(dt);

        }catch (Exception  e){
            responseVO.setCode(ApiResponseCode.CODE_INVALID_REQUEST_PARAMETER.getCode());
            responseVO.setMessage(e.getMessage());
        }
        return responseVO;
    }
    /**
     *
     */
    @RequestMapping(value = "/getSubScriberToken", method = RequestMethod.POST)
    public ResponseBaseVO<TeleHealthConnectVO> getsubToken(@Validated(VoValidationGroups.add.class) @RequestBody TeleHealthConnectVO vo
        , BindingResult bindingResult, @RequestAttribute TokenDetailInfo tokenDetailInfo) {

        if (bindingResult.hasErrors()) {
            throw new InvalidRequestArgumentException(bindingResult);
        }
        ResponseBaseVO<TeleHealthConnectVO> responseVO = new ResponseBaseVO<>();


        try{

            String loginid = vo.getLoginId();
            String admid = getAdmissionId(loginid);
            vo.setAdmissionId(admid);

            TeleHealthConnectVO dt = teleHealthService.getSubscriberToken(vo);
            responseVO.setCode(ApiResponseCode.SUCCESS.getCode());
            responseVO.setMessage("????????????????????????");
            responseVO.setResult(dt);

        }catch (Exception  e){
            responseVO.setCode(ApiResponseCode.CODE_INVALID_REQUEST_PARAMETER.getCode());
            responseVO.setMessage(e.getMessage());
        }
        return responseVO;
    }

    /**
     * ?????? ??????
     * @param vo
     * @param bindingResult
     * @param tokenDetailInfo
     * @return
     */
    @RequestMapping(value = "/setEndSession", method = RequestMethod.POST)
    public ResponseBaseVO<TeleHealthConnectVO> endSession(@Validated(VoValidationGroups.add.class) @RequestBody TeleHealthSearchVO vo
        , BindingResult bindingResult, @RequestAttribute TokenDetailInfo tokenDetailInfo) {

        if (bindingResult.hasErrors()) {
            throw new InvalidRequestArgumentException(bindingResult);
        }
        ResponseBaseVO<TeleHealthConnectVO> responseVO = new ResponseBaseVO<>();
        try{

            int rtn = teleHealthService.endSession(vo);
            responseVO.setCode(ApiResponseCode.SUCCESS.getCode());
            responseVO.setMessage("????????????");

        }catch (Exception  e){
            responseVO.setCode(ApiResponseCode.CODE_INVALID_REQUEST_PARAMETER.getCode());
            responseVO.setMessage(e.getMessage());
        }
        return responseVO;
    }

    @RequestMapping(value = "/setPush", method = RequestMethod.POST)
    public ResponseBaseVO<TeleHealthConnectVO> sendPush(@Validated(VoValidationGroups.add.class) @RequestBody TeleHealthSearchVO vo
        , BindingResult bindingResult, @RequestAttribute TokenDetailInfo tokenDetailInfo) {

        if (bindingResult.hasErrors()) {
            throw new InvalidRequestArgumentException(bindingResult);
        }
        ResponseBaseVO<TeleHealthConnectVO> responseVO = new ResponseBaseVO<>();
        try{
            //int rtn = teleHealthService.sendPush(vo);
            responseVO.setCode(ApiResponseCode.SUCCESS.getCode());
            responseVO.setMessage("??????????????? ?????? ??????");

        }catch (Exception  e){
            responseVO.setCode(ApiResponseCode.CODE_INVALID_REQUEST_PARAMETER.getCode());
            responseVO.setMessage(e.getMessage());
        }
        return responseVO;
    }

}
