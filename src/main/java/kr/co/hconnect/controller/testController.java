package kr.co.hconnect.controller;
import com.opentok.exception.OpenTokException;
import egovframework.rte.fdl.cmmn.exception.FdlException;
import kr.co.hconnect.common.ApiResponseCode;
import kr.co.hconnect.common.VoValidationGroups;
import kr.co.hconnect.exception.InvalidRequestArgumentException;
import kr.co.hconnect.exception.NotFoundUserInfoException;
import kr.co.hconnect.jwt.TokenDetailInfo;
import kr.co.hconnect.repository.TeleHealthDao;
import kr.co.hconnect.service.UserService;
import kr.co.hconnect.vo.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

import kr.co.hconnect.service.TestService;


/**
 * 사용자 관리 Controller
 */
@RestController
@RequestMapping("/api/test")
public class testController {


    private final TestService testService;

    @Autowired
    public testController(TestService testService) {
        this.testService = testService;
    }




    @RequestMapping(value = "/fileDown", method = RequestMethod.POST)
    public ResponseBaseVO<testVO> insertDrugDose(@Validated(VoValidationGroups.add.class) @RequestBody testVO vo
        , BindingResult bindingResult, @RequestAttribute TokenDetailInfo tokenDetailInfo) {

        ResponseBaseVO<testVO> responseVO = new ResponseBaseVO<>();

        try {
            int rtn = testService.fileDownload();
            responseVO.setCode(ApiResponseCode.SUCCESS.getCode());
            responseVO.setMessage("fileDown ok");
        } catch (NotFoundUserInfoException e) {
            responseVO.setCode(ApiResponseCode.CODE_INVALID_REQUEST_PARAMETER.getCode());
            responseVO.setMessage(e.getMessage());
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (OpenTokException e) {
            throw new RuntimeException(e);
        }

        return responseVO;
    }
}
