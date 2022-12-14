package kr.co.hconnect.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * 문진 저장정보
 * 아이디
 * 문진종류
 * 측정일자
 * 문항 답변 목록
 * 문항 번호
 * 답변
 */
@Getter
@Setter
@NoArgsConstructor
@ToString
public class SaveInformaionInfo extends BaseResponse {

    @NotNull(message = "{validation.null.loginId}")
    private String loginId;

    @NotNull(message = "{validation.null.interviewType}")
    private String interviewType;

    @NotNull(message = "{validation.null.interviewDate}")
    private String interviewDate;

    private List<SaveInformationAnswerListInfo> answerList;


}
