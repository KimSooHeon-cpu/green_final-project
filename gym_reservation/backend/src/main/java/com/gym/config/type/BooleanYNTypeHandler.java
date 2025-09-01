package com.gym.config.type;                        // 📦 타입핸들러 패키지(전역 등록 대상: MyBatisConfig#setTypeHandlersPackage로 스캔됨)

import org.apache.ibatis.type.BaseTypeHandler;      // 🧩 MyBatis 타입핸들러 베이스 클래스(제네릭으로 다룰 Java 타입 지정)
import org.apache.ibatis.type.JdbcType;             // 🧩 JDBC 타입 상수(필요 시 사용, 현재 구현에서는 직접 사용하지 않음)
import java.sql.*;                                  // 🧩 JDBC API(PreparedStatement/ResultSet/CallableStatement 등)

 /**
  * Oracle CHAR(1) 'Y'/'N' ↔ Java boolean 전역 매핑 핸들러                        
  * 사용 위치: account_tbl.account_main, card_tbl.card_main 등 CHAR(1) 컬럼
  * 저장 시: true → 'Y', false → 'N'
  * 조회 시: 'Y' → true, (그 외: 'N'/NULL/기타) → false
  * 전역 적용: MyBatisConfig#setTypeHandlersPackage("com.gym.config.type") 
  */
public class BooleanYNTypeHandler extends BaseTypeHandler<Boolean> {  // BaseTypeHandler<Boolean> 상속: Boolean 타입 처리

    @Override	// 부모 메서드 재정의(자바 → DB 바인딩 시 호출)
    // ps(PreparedStatement): SQL 실행 시 DB에 보낼 변수 (1부터 시작)
    public void setNonNullParameter(PreparedStatement ps, int i,
                                    Boolean parameter, JdbcType jdbcType) throws SQLException {
        ps.setString(i, parameter != null && parameter ? "Y" : "N");  // true면 'Y', null/false면 'N'으로 저장
    }                                                                 

    @Override	// 부모 메서드 재정의(DB → 자바 변환: 컬럼명을 이용해 읽을 때)
    // rs(ResultSet): DB에서 셀렉트한 결과값의 변수
    // value(값) 
    public Boolean getNullableResult(ResultSet rs, String columnName) throws SQLException {
        String value = rs.getString(columnName); // 지정한 컬럼명으로 CHAR(1) 값을 읽어옴('Y' 또는 'N' 또는 NULL)
        return "Y".equalsIgnoreCase(value);		 // 대소문자 무시 비교: 'Y'면 true, 나머지(NULL 포함)면 false
    }

    @Override	// 부모 메서드 재정의(DB → 자바 변환: 컬럼값을 인덱스로 읽을 때)
    public Boolean getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        String value = rs.getString(columnIndex);	// 인덱스로 CHAR(1) 값 읽음 (1부터 시작함)
        return "Y".equalsIgnoreCase(value);			// 'Y' → true (그 외의 결과는 → false)
    }

    @Override	// 부모 메서드 재정의(DB 함수/프로시저 결과 처리 시 사용)
    // cs(CallableStatement): DB에 저장된 프로시저/함수를 호출하는 변수
    public Boolean getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        String value = cs.getString(columnIndex);	// 프로시저/함수 OUT 파라미터로부터 CHAR(1) 값 읽기
        return "Y".equalsIgnoreCase(value);			// 'Y' → true (그 외의 결과는 → false)
    }
}
