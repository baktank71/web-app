package kr.co.hconnect.repository;

import kr.co.hconnect.domain.Patient;
import kr.co.hconnect.domain.SearchLoginIdInfo;
import org.springframework.stereotype.Repository;

import egovframework.rte.psl.dataaccess.EgovAbstractMapper;
import kr.co.hconnect.vo.PatientVO;

import java.util.List;

/**
 * 환자정보 관리 Dao
 */
@Repository
public class PatientDao extends EgovAbstractMapper {

	/**
	 * 환자정보 조회-로그인ID 기준
	 * @param loginId 로그인ID
	 * @return Patient
	 */
	public Patient selectPatientByLoginId(String loginId) {
		return selectOne("kr.co.hconnect.sqlmapper.selectPatientByLoginId", loginId);
	}

	/**
	 * 환자정보 조회-주민번호(암호화) 기준
	 * @param ssn 주민번호(암호화)
	 * @return Patient
	 */
	public Patient selectPatientBySsn(String ssn) {
		return selectOne("kr.co.hconnect.sqlmapper.selectPatientBySsn", ssn);
	}

	/**
	 * 환자정보 조회-아이디 검색 조건 정보 기준
	 * @param searchLoginIdInfo 아이디 검색 조건 정보
	 * @return Patient
	 */
	public List<Patient> selectPatientListBySearchLoginIdInfo(SearchLoginIdInfo searchLoginIdInfo) {
		return selectList("kr.co.hconnect.sqlmapper.selectPatientBySearchLoginIdInfo", searchLoginIdInfo);
	}

	/**
	 * 환자 추가정보 업데이트 - 기존 생성된 환자정보 추가내역
	 * @param patient 환자정보
	 * @return affectedRow
	 */
	public int updatePatientInfo(Patient patient) {
		return update("kr.co.hconnect.sqlmapper.addPatientInfo", patient);
	}

	/**
	 * 환자 생성 
	 * @param vo PatientVO
	 * @return affectedRow
	 */
	public int insertPatient(PatientVO vo) {
		return insert("kr.co.hconnect.sqlmapper.insertPatient", vo);
	}
	
	/**
	 * 환자 수정 
	 * @param vo PatientVO
	 * @return affectedRow
	 */
	public int updatePatient(PatientVO vo) {
		return update("kr.co.hconnect.sqlmapper.updatePatient", vo);
	}
	
}
