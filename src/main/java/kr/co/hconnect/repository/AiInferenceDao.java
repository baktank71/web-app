package kr.co.hconnect.repository;

import egovframework.rte.psl.dataaccess.EgovAbstractMapper;
import org.springframework.stereotype.Repository;
import egovframework.rte.psl.dataaccess.EgovAbstractMapper;
import kr.co.hconnect.vo.*;

@Repository
public class AiInferenceDao extends EgovAbstractMapper {

    public void insInf (AiInferenceVO vo) {
        insert("kr.co.hconnect.sqlmapper.insInf", vo);
    }

    public void insInf_log (AiInferenceVO vo) {
        insert("kr.co.hconnect.sqlmapper.insInf", vo);
    }

    public void delInf (AiInferenceVO vo) {
        delete("kr.co.hconnect.sqlmapper.udpScore", vo);
    }

}
